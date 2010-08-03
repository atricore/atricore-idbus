package org.atricore.idbus.capabilities.samlr2.management.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.samlr2.main.idp.IdentityProviderConstants;
import org.atricore.idbus.capabilities.samlr2.management.codec.JmxSSOSession;
import org.atricore.idbus.capabilities.samlr2.support.SSOMessagingConstants;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.LocalProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;
import org.atricore.idbus.kernel.main.session.SSOSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;

import javax.management.openmbean.TabularData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityProviderMBeanImpl extends AbstractProviderMBean
        implements SSOMessagingConstants {

    private static final Log logger = LogFactory.getLog(ServiceProviderMBeanImpl.class);

    private IdentityProviderImpl identityProvider;

    public IdentityProviderImpl getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProviderImpl identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Override
    protected LocalProvider getProvider() {
        return identityProvider;
    }

    public boolean invalidateSession(String sessionId) {

        try {

            if (logger.isTraceEnabled())
                logger.trace("Invalidating SSO Session from MBean. Session ID:" + sessionId);

            ProviderStateContext ctx = new ProviderStateContext(identityProvider, applicationContext.getClassLoader());
            LocalState state = ctx.retrieve(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, sessionId);
            IdPSecurityContext secCtx = (IdPSecurityContext) state.getValue(identityProvider.getName().toUpperCase() + "_SECURITY_CTX");

            if (secCtx == null) {
                if (logger.isDebugEnabled())
                    logger.debug("IdP Security Context not found for SSO Session ID: " + sessionId);
                return false;
            }

            // Trigger IdP Initiated SLO for given session:
            triggerIdPInitiatedSLO(secCtx);

            if (logger.isDebugEnabled())
                logger.debug("SSO Session invalidated from MBean: " + sessionId);

            return true;

        } catch (Exception e) {
            logger.error("Cannot invalidate SSO Session from MBean: " + e.getMessage(), e);
        }

        return false;

    }

    public boolean invalidateUserSessions(String username) {
        boolean invalidated = true;
        try {

            if (logger.isTraceEnabled())
                logger.trace("Invalidating SSO Sessions from MBean for user:" + username);

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getUserSessions(username);
            for (SSOSession session : sessions) {

                try {

                    String sessionId = session.getId();

                    if (logger.isTraceEnabled())
                        logger.trace("Invalidating SSO Session from MBean. Session ID:" + sessionId);

                    ProviderStateContext ctx = new ProviderStateContext(identityProvider, applicationContext.getClassLoader());
                    LocalState state = ctx.retrieve(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, sessionId);
                    IdPSecurityContext secCtx = (IdPSecurityContext) state.getValue(identityProvider.getName().toUpperCase() + "_SECURITY_CTX");

                    if (secCtx == null) {
                        if (logger.isDebugEnabled())
                            logger.debug("IdP Security Context not found for SSO Session ID: " + sessionId);
                        continue;
                    }

                    // Trigger IdP Initiated SLO for given session:
                    triggerIdPInitiatedSLO(secCtx);

                    if (logger.isDebugEnabled())
                        logger.debug("SSO Session invalidated from MBean: " + sessionId);

                } catch (Exception e) {
                    logger.error("Cannot invalidate SSO Session from MBean: " + e.getMessage(), e);
                    invalidated = false;
                }

            }


        } catch (Exception e) {
            logger.error("Cannot invalidate SSO Sessions from MBean for username: " + username + ".  " + e.getMessage(), e);
            invalidated = false;
        }
        return invalidated;
    }



    public boolean invalidateAllSessions() {

        boolean invalidated = true;
        try {

            if (logger.isTraceEnabled())
                logger.trace("Invalidating all SSO Sessions from MBean");

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getSessions();
            for (SSOSession session : sessions) {

                try {

                    String sessionId = session.getId();

                    if (logger.isTraceEnabled())
                        logger.trace("Invalidating SSO Session from MBean. Session ID:" + sessionId);

                    ProviderStateContext ctx = new ProviderStateContext(identityProvider, applicationContext.getClassLoader());
                    LocalState state = ctx.retrieve(IdentityProviderConstants.SEC_CTX_SSOSESSION_KEY, sessionId);
                    IdPSecurityContext secCtx = (IdPSecurityContext) state.getValue(identityProvider.getName().toUpperCase() + "_SECURITY_CTX");

                    if (secCtx == null) {
                        if (logger.isDebugEnabled())
                            logger.debug("IdP Security Context not found for SSO Session ID: " + sessionId);
                        continue;
                    }

                    // Trigger IdP Initiated SLO for given session:
                    triggerIdPInitiatedSLO(secCtx);

                    if (logger.isDebugEnabled())
                        logger.debug("SSO Session invalidated from MBean: " + sessionId);

                } catch (Exception e) {
                    logger.error("Cannot invalidate session: " + e.getMessage(), e);
                    invalidated = false;
                }

            }


        } catch (Exception e) {
            logger.error("Cannot invalidate all SSO Sessions: " + e.getMessage(), e);
            invalidated = true;
        }
        return invalidated;
    }

    public TabularData listSessionsAsTable() {

        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing all SSO Sessions from MBean");

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getSessions();
            List<JmxSSOSession> jmxSessions = new ArrayList<JmxSSOSession>(sessions.size());
            for (SSOSession session : sessions) {
                jmxSessions.add(new JmxSSOSession(session));
            }
            TabularData table = JmxSSOSession.tableFrom(jmxSessions);
            return table;
        } catch (Exception e) {
            logger.error("Cannot find sessions: " + e.getMessage(), e);
        }

        return null;
    }

    public TabularData listUserSessionsAsTable(String username) {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing SSO Sessions from MBean. User:" + username);

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getUserSessions(username);
            List<JmxSSOSession> jmxSessions = new ArrayList<JmxSSOSession>(sessions.size());
            for (SSOSession session : sessions) {
                jmxSessions.add(new JmxSSOSession(session));
            }
            TabularData table = JmxSSOSession.tableFrom(jmxSessions);
            return table;
        } catch (Exception e) {
            logger.error("Cannot find sessions: " + e.getMessage(), e);
        }

        return null;
    }

    public SSOSession[] listSessions() {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing all SSO Sessions from MBean");

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getSessions();
            return sessions.toArray(new SSOSession[sessions.size()]);
        } catch (Exception e) {
            logger.error("Cannot find sessions: " + e.getMessage(), e);
        }

        return null;
    }

    public SSOSession[] listUserSessions(String username) {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing SSO Sessions from MBean. User:" + username);

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getUserSessions(username);
            return sessions.toArray(new SSOSession[sessions.size()]);
        } catch (Exception e) {
            logger.error("Cannot find sessions: " + e.getMessage(), e);
        }

        return null;
    }

    public long getMaxInactiveInterval() {
        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing all SSO Sessions from MBean");

            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getMaxInactiveInterval();
        } catch (Exception e) {
            logger.error("Cannot find SSO Sessions max inactive interval");
            return -1;
        }
    }

    protected void triggerIdPInitiatedSLO(IdPSecurityContext secCtx) throws SamlR2Exception, IdentityMediationException {

        if (logger.isTraceEnabled())
            logger.trace("Triggering IDP Initiated SLO from MBean for Security Context " + secCtx);

        EndpointDescriptor ed = resolveIdpInitiatedSloEndpoint(identityProvider);

        if (logger.isDebugEnabled())
            logger.debug("Using IDP Initiated SLO endpoint " + ed);

        // ---------------------------------------------------------
        // Send message through mediator
        // ---------------------------------------------------------

        IDPInitiatedLogoutRequestType sloRequest = new IDPInitiatedLogoutRequestType();
        sloRequest.setID(uuidGenerator.generateId());
        sloRequest.setSsoSessionId(secCtx.getSessionIndex());

        if (logger.isTraceEnabled())
            logger.trace("Sending SLO Request " + sloRequest.getID() +
                    " to IDP " + identityProvider.getName() +
                    " using endpoint " + ed.getLocation());

        // Response from SP
        SSOResponseType sloResponse =
                (SSOResponseType) identityProvider.getChannel().getIdentityMediator().sendMessage(sloRequest, ed, identityProvider.getChannel());

        if (logger.isTraceEnabled())
            logger.trace("Recevied SLO Response " + sloResponse.getID() +
                    " from IDP " + identityProvider.getName() +
                    " using endpoint " + ed.getLocation());


    }

    protected EndpointDescriptor resolveIdpInitiatedSloEndpoint(IdentityProvider idp) throws SamlR2Exception {
        // User default channel to signal SLO
        Channel defaultChannel = idp.getChannel();

        IdentityMediationEndpoint soapEndpoint = null;
        for (IdentityMediationEndpoint endpoint : defaultChannel.getEndpoints()) {

            if (endpoint.getType().equals(SamlR2Service.IDPInitiatedSingleLogoutService.toString())) {
                // We need to build an endpoint descriptor descriptor now ...

                if (endpoint.getBinding().equals(SamlR2Binding.SSO_SOAP.getValue())) {
                    soapEndpoint = endpoint;
                } else if (endpoint.getBinding().equals(SamlR2Binding.SSO_LOCAL.getValue())) {

                    String location = endpoint.getLocation().startsWith("/") ?
                            defaultChannel.getLocation() + endpoint.getLocation() :
                            endpoint.getLocation();

                    return new EndpointDescriptorImpl(endpoint.getName(),
                            SamlR2Service.IDPInitiatedSingleLogoutService.toString(),
                            SamlR2Binding.SSO_LOCAL.toString(),
                            location,
                            null);

                }

            }
        }

        if (soapEndpoint != null) {

            String location = soapEndpoint.getLocation().startsWith("/") ?
                    defaultChannel.getLocation() + soapEndpoint.getLocation() :
                    soapEndpoint.getLocation();

            return new EndpointDescriptorImpl(soapEndpoint.getName(),
                    SamlR2Service.IDPInitiatedSingleLogoutService.toString(),
                    SamlR2Binding.SSO_SOAP.toString(),
                    location,
                    null);

        }

        throw new SamlR2Exception("No IDP Initiated SLO endpoint using SOAP binding found!");
    }
}

