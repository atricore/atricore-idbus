package org.atricore.idbus.capabilities.sso.main.idp.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.common.AbstractSSOMediator;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 */
public class IdPInitiatedSingleLogoutProducer extends SSOProducer {

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static Log logger = LogFactory.getLog(IdPInitiatedSingleLogoutProducer.class);

    public IdPInitiatedSingleLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        logger.debug("Processing IDP Initiated Single SingOn on HTTP Redirect");

        try {

            CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

            MediationState mediationState = in.getMessage().getState();
            String varName = getProvider().getName().toUpperCase() + "_SECURITY_CTX";

            IdPSecurityContext secCtx = (IdPSecurityContext) mediationState.getLocalVariable(varName);
            if (secCtx != null && secCtx.getSessionIndex() != null) {
                IdentityProvider idp = (IdentityProvider) ((SPChannel)channel).getProvider();
                triggerIdPInitiatedSLO(idp, secCtx);
            }

            // Send user to some URL ?!
            String destinationLocation = ((SSOIDPMediator) channel.getIdentityMediator()).getDashboardUrl();

            EndpointDescriptor destination =
                    new EndpointDescriptorImpl("EmbeddedSPAcs",
                            "SingleLogoutService",
                            SSOBinding.SSO_REDIRECT.getValue(),
                            destinationLocation, null);

            logger.debug("Sending IdP-init SLO Response to " + destination);

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    null, "IdPLogoutResponse", null, destination, in.getMessage().getState()));

            exchange.setOut(out);

        } catch (Exception e) {
            throw new IdentityMediationException(e.getMessage(), e);
        }
    }

    protected void triggerIdPInitiatedSLO(IdentityProvider identityProvider, IdPSecurityContext secCtx) throws SSOException, IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Triggering IDP Initiated SLO from IDP for Security Context " + secCtx);

        EndpointDescriptor ed = resolveIdpInitiatedSloEndpoint(identityProvider);

        if (logger.isDebugEnabled())
            logger.debug("Using IDP Initiated SLO endpoint " + ed);

        IDPInitiatedLogoutRequestType sloRequest = new IDPInitiatedLogoutRequestType();
        sloRequest.setID(uuidGenerator.generateId());
        sloRequest.setSsoSessionId(secCtx.getSessionIndex());

        if (logger.isTraceEnabled())
            logger.trace("Sending SLO Request " + sloRequest.getID() +
                    " to IDP " + identityProvider.getName() +
                    " using endpoint " + ed.getLocation());

        IdentityMediator mediator = identityProvider.getChannel().getIdentityMediator();

        // Response from SP
        SSOResponseType sloResponse =
                (SSOResponseType) mediator.sendMessage(sloRequest, ed, identityProvider.getChannel());

        if (logger.isTraceEnabled())
            logger.trace("Recevied SLO Response " + sloResponse.getID() +
                    " from IDP " + identityProvider.getName() +
                    " using endpoint " + ed.getLocation());


    }

    protected EndpointDescriptor resolveIdpInitiatedSloEndpoint(IdentityProvider idp) throws SSOException {
        // User default channel to signal SLO
        Channel defaultChannel = idp.getChannel();

        IdentityMediationEndpoint e = null;
        for (IdentityMediationEndpoint endpoint : defaultChannel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.SingleLogoutService.toString())) {

                if (endpoint.getBinding().equals(SSOBinding.SSO_LOCAL.getValue())) {
                    // We need to build an endpoint descriptor descriptor now ...

                    String location = endpoint.getLocation().startsWith("/") ?
                            defaultChannel.getLocation() + endpoint.getLocation() :
                            endpoint.getLocation();

                    return new EndpointDescriptorImpl(idp.getName() + "-sso-slo-local",
                            SSOService.SingleLogoutService.toString(),
                            SSOBinding.SSO_LOCAL.toString(),
                            location,
                            null);
                } else if (endpoint.getBinding().equals(SSOBinding.SSO_SOAP.getValue())) {
                    e = endpoint;
                }
            }
        }

        if (e != null) {
            String location = e.getLocation().startsWith("/") ?
                    defaultChannel.getLocation() + e.getLocation() :
                    e.getLocation();

            return new EndpointDescriptorImpl(idp.getName() + "-sso-slo-soap",
                    SSOService.SingleLogoutService.toString(),
                    e.getBinding(),
                    location,
                    null);
        }

        throw new SSOException("No IDP SLO endpoint using LOCAL/SOAP binding found!");
    }
}
