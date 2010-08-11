package org.atricore.idbus.capabilities.spmlr2.main.psp.producers;

import oasis.names.tc.spml._2._0.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.common.producers.SpmlR2Producer;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

import java.util.List;

/**
 * // TODO : Split this in several producers ...
 *
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class PSPProducer extends SpmlR2Producer {

    private static final Log logger = LogFactory.getLog(PSPProducer.class);

    public PSPProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        Object content = in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.debug("Processing SPML " + content.getClass().getSimpleName() + " request");

        if (content instanceof ListTargetsRequestType) {
            doProcessListTargetsReques(exchange, (ListTargetsRequestType) content);
        } else {

            // TODO : Send status=failure error= in response ! (use super producer or binding to build error

            // TODO : See SPMPL Section 3.1.2.2 Error (normative)

            throw new IdentityMediationFault("status='failure'",
                    null,
                    "error='unsupportedOperation'",
                    content.getClass().getName(),
                    null);
        }
    }

    protected void doProcessListTargetsReques(CamelMediationExchange exchange, ListTargetsRequestType request) {
        // TODO : Use planning to convert SPML Request into kernel request

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) channel.getIdentityMediator();
        List<ProvisioningTarget> targets = mediator.getProvisioningTargets();

        ListTargetsResponseType spmlResponse = new ListTargetsResponseType();


        for (ProvisioningTarget target : targets) {
            TargetType spmlTarget = new TargetType();

            // TODO : Check spec
            spmlTarget.setProfile("dsml");
            spmlTarget.setTargetID(target.getIdentityPartition().getName());

            CapabilitiesListType capabilitiesList = new CapabilitiesListType();

            CapabilityType spmlSearchCap = new CapabilityType();
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:search");
            spmlSearchCap.setNamespaceURI("urn:oasis:names:tc:SPML:2:0:update");

            spmlResponse.getTarget().add(spmlTarget);
        }


        // Send response back.
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(), endpoint.getBinding(), null, null);


        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        out.setMessage(new MediationMessageImpl(idGen.generateId(),
                spmlResponse,
                spmlResponse.getClass().getSimpleName(),
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);
    }
}
