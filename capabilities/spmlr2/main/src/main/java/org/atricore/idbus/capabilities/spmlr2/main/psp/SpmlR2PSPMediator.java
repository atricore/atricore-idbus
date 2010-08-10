package org.atricore.idbus.capabilities.spmlr2.main.psp;

import org.apache.camel.builder.RouteBuilder;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Exception;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.common.AbstractSpmlR2Mediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTargetManager;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2PSPMediator extends AbstractSpmlR2Mediator {

    private List<ProvisioningTargetManager> provisioningTargets;

    public List<ProvisioningTargetManager> getProvisioningTargets() {
        return provisioningTargets;
    }

    public void setProvisioningTargets(List<ProvisioningTargetManager> provisioningTargets) {
        this.provisioningTargets = provisioningTargets;
    }

    @Override
    protected RouteBuilder createPsPRoutes(final PsPChannel pspChannel) throws Exception {
        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = pspChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoits defined for idpChannel : " + pspChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SpmlR2Binding binding = SpmlR2Binding.asEnum(endpoint.getBinding());
                    // HTTP Bindings are handled with Camel
                    EndpointDescriptor ed = resolveEndpoint(pspChannel, endpoint);

                    switch (binding) {
                        case SPMLR2_SOAP:

                            // ----------------------------------------------------------
                            // SOAP Incomming messages:
                            // ==> idbus-http ==> cxf ==> idbus-bind ==> spmlr2-psp
                            // ----------------------------------------------------------

                            // FROM idbus-http TO cxf (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName() + "-cxf");

                            // FROM cxf TO idbus-bind (through direct component)
                            from("cxf:camel://direct:" + ed.getName() + "-cxf" +
                                    "?serviceClass=org.atricore.idbus.capabilities.spmlr2.main.binding.services.SamlR2ServiceImpl" +
                                    "&serviceName={urn:oasis:names:tc:SPML:2:0:wsdl}SPMLService" +
                                    "&portName={urn:oasis:names:tc:SPML:2:0:wsdl}soap" +
                                    "&dataFormat=POJO").
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());


                            // FROM spmlr-bind TO spmlr2-psp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + pspChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("spmlr2-psp:" + ed.getType() +
                                            "?channelRef=" + pspChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO spmlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-cxf-response");

                                // Receive HTTP requests and handle them as SOAP messages.
                                from("cxf:camel://direct:" + ed.getName() + "-cxf-response" +
                                        "?serviceClass=org.atricore.idbus.capabilities.spmlr2.main.binding.services.SpmlR2R2ServiceImpl" +
                                        "&serviceName={urn:oasis:names:tc:SPML:2:0:wsdl}SPMLService" +
                                        "&portName={urn:oasis:names:tc:SPML:2:0:wsdl}soap" +
                                        "&dataFormat=POJO").
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM SPMLR2 SamlR2Binding TO SPMLR2-PSP
                                from("idbus-bind:camel://" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + pspChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("spmlr2-psp:" + ed.getType() +
                                                "?channelRef=" + pspChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;

                        case SPMLR2_LOCAL:

                             from("direct:" + ed.getLocation()).
                                     to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                "?binding=" + ed.getBinding() +
                                "&channelRef=" + pspChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("spmlr2-psp:" + ed.getType() +
                                            "?channelRef=" + pspChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());


                            break;

                        default:
                            throw new SpmlR2Exception("Unsupported SpmlR2Binding " + binding.getValue());


                    }
                }
            }
        };
    }
}
