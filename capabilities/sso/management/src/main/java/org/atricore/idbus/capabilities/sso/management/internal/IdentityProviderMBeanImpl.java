package org.atricore.idbus.capabilities.sso.management.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.idp.IdPSecurityContext;
import org.atricore.idbus.capabilities.sso.main.idp.IdentityProviderConstants;
import org.atricore.idbus.capabilities.sso.management.IdentityProviderMBean;
import org.atricore.idbus.capabilities.sso.management.codec.JmxSSOSession;
import org.atricore.idbus.capabilities.sso.support.SSOMessagingConstants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
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
        implements IdentityProviderMBean, SSOMessagingConstants {

    private static final Log logger = LogFactory.getLog(ServiceProviderMBeanImpl.class);

    private IdentityProviderImpl identityProvider;

    public IdentityProviderImpl getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProviderImpl identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Override
    protected FederatedLocalProvider getProvider() {
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

    public long getSessionsCount() {
        try {
            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsCurrentSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO Sessions count");
            return -1;
        }

    }

    public long getTotalCreatedSessions() {
        try {
            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsCreatedSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO created sessions count");
            return -1;
        }

    }

    public long getTotalDestroyedSessions() {
        try {
            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsDestroyedSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO destroyed count");
            return -1;
        }

    }

    public long getMaxSessionsCount() {
        try {
            SPChannel channel = (SPChannel) identityProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsMaxSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO max sessions count");
            return -1;
        }

    }


    protected void triggerIdPInitiatedSLO(IdPSecurityContext secCtx) throws SSOException, IdentityMediationException {

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

