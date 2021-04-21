package org.atricore.idbus.capabilities.sso.main.idp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.IdentityMediator;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.SSOSession;
import org.atricore.idbus.kernel.main.session.SSOSessionEventListener;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdPSessionEventListener implements SSOSessionEventListener, ApplicationContextAware {

    private static final Log logger =  LogFactory.getLog(IdPSessionEventListener.class);

    private ApplicationContext applicationContext;

    private IdentityProvider identityProvider;

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public void handleEvent(String type, SSOSession session, Object data) {

        if (type.equals(BaseSession.SESSION_DESTROYED_EVENT)) {
            if (logger.isDebugEnabled())
                logger.debug("Received SSO Session event 'DESTROYED' for session " + session.getId());

            invalidateSession(session.getId());
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    // TODO : Duplicated with MBean, se if we can send a direct message through camel instead of using soap.
    protected boolean invalidateSession(String sessionId) {

        try {

            if (logger.isTraceEnabled())
                logger.trace("Invalidating SSO Session from IDP Session Listener. Session ID:" + sessionId +
                        " [IDP:"+identityProvider.getName()+"]");

            ProviderStateContext ctx = new ProviderStateContext(identityProvider, applicationContext.getClassLoader());
            LocalState state = ctx.retrieve(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, sessionId);
            if (state == null) {
                if (logger.isDebugEnabled())
                    logger.debug("No security context found for SSO Session " + sessionId);
                return true;
            }

            IdPSecurityContext secCtx = (IdPSecurityContext) state.getValue(identityProvider.getName().toUpperCase() + "_SECURITY_CTX");

            if (secCtx == null) {
                if (logger.isDebugEnabled())
                    logger.debug("IdP Security Context not found for SSO Session ID: " + sessionId);
                return false;
            }

            // Trigger IdP Initiated SLO for given session:
            triggerIdPInitiatedSLO(secCtx);

            if (logger.isDebugEnabled())
                logger.debug("SSO Session invalidated from IDP Session Listener: " + sessionId);

            return true;

        } catch (Exception e) {
            logger.error("Cannot invalidate SSO Session from IDP Session Listener: " + e.getMessage(), e);
        }

        return false;

    }

    protected void triggerIdPInitiatedSLO(IdPSecurityContext secCtx) throws SSOException, IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Triggering IDP Initiated SLO from IDP Session Listener for Security Context " + secCtx);

        EndpointDescriptor ed = resolveIdpSloEndpoint(identityProvider);

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
            logger.trace("Received SLO Response " + sloResponse.getID() +
                    " from IDP " + identityProvider.getName() +
                    " using endpoint " + ed.getLocation());


    }

    protected EndpointDescriptor resolveIdpSloEndpoint(IdentityProvider idp) throws SSOException {
        // User default channel to signal SLO
        Channel defaultChannel = idp.getChannel();

        // Look for local SLO endpoint, it will also receive SLO IDP Initiated requests
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

            return new EndpointDescriptorImpl(e.getName(),
                    e.getType(),
                    e.getBinding(),
                    location,
                    null);
        }

        throw new SSOException("No IDP SLO endpoint using LOCAL binding found for channel " + defaultChannel.getName());
    }
}
