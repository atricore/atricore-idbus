package org.atricore.idbus.capabilities.openid.main.claims;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

import java.util.Collection;

/**
 * Created by sgonzalez on 2/24/14.
 */
public class OpenIDClaimsMediator extends AbstractSSOMediator {

    private static final Log logger = LogFactory.getLog(OpenIDClaimsMediator.class);


    private ProvisioningTarget provisioningTarget;

    private String openIDUILocation;

    public String getOpenIDUILocation() {
        return openIDUILocation;
    }

    public void setOpenIDUILocation(String openIDUILocation) {
        this.openIDUILocation = openIDUILocation;
    }

    public ProvisioningTarget getProvisioningTarget() {
        return provisioningTarget;
    }

    public void setProvisioningTarget(ProvisioningTarget provisioningTarget) {
        this.provisioningTarget = provisioningTarget;
    }

    @Override
    protected RouteBuilder createClaimRoutes(final ClaimChannel claimChannel) throws Exception {
        // Create routes based on endpoints!

        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                // --------------------------------------------------
                // Process configured endpoints for this channel
                // --------------------------------------------------
                Collection<IdentityMediationEndpoint> endpoints = claimChannel.getEndpoints();

                if (endpoints == null)
                    throw new IdentityMediationException("No endpoints defined for claims channel : " + claimChannel.getName());

                for (IdentityMediationEndpoint endpoint : endpoints) {

                    SSOBinding binding = SSOBinding.asEnum(endpoint.getBinding());
                    EndpointDescriptor ed = resolveEndpoint(claimChannel, endpoint);

                    switch (binding) {
                        case SSO_ARTIFACT:
                        case SSO_POST:
                        case SSO_REDIRECT:

                            // HTTP Bindings are handled with Camel

                            // FROM idbus-http TO samlr2-binding (through direct component)
                            from("idbus-http:" + ed.getLocation()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("direct:" + ed.getName());

                            // FROM samlr-bind TO openid-idp
                            from("idbus-bind:camel://direct:" + ed.getName() +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openid-claim:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {

                                // FROM idbus-http TO samlr2-binding (through direct component)
                                from("idbus-http:" + ed.getResponseLocation()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("direct:" + ed.getName() + "-response");

                                // FROM samlr-bind TO openid-idp
                                from("idbus-bind:camel://direct:" + ed.getName() + "-response" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openid-claim:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");

                            }


                            break;
                        case SSO_LOCAL:

                            from("direct:" + ed.getLocation()).
                                    to("direct:" + ed.getName() + "-local");

                            from("idbus-bind:camel://direct:" + ed.getName() + "-local" +
                                    "?binding=" + ed.getBinding() +
                                    "&channelRef=" + claimChannel.getName()).
                                    process(new LoggerProcessor(getLogger())).
                                    to("openid-claim:" + ed.getType() +
                                            "?channelRef=" + claimChannel.getName() +
                                            "&endpointRef=" + endpoint.getName());

                            if (ed.getResponseLocation() != null) {
                                from("direct:" + ed.getLocation()).
                                        to("direct:" + ed.getName() + "-local-resp");

                                from("idbus-bind:camel://direct:" + ed.getName() + "-local-resp" +
                                        "?binding=" + ed.getBinding() +
                                        "&channelRef=" + claimChannel.getName()).
                                        process(new LoggerProcessor(getLogger())).
                                        to("openid-claim:" + ed.getType() +
                                                "?channelRef=" + claimChannel.getName() +
                                                "&endpointRef=" + endpoint.getName() +
                                                "&response=true");
                            }
                            break;

                        default:
                            throw new SSOException("Unsupported SSOBinding " + binding.getValue());
                    }


                }

            }
        };

    }
}
