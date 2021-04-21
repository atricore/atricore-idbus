package org.atricore.idbus.capabilities.sso.management.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.management.ServiceProviderMBean;
import org.atricore.idbus.capabilities.sso.management.codec.JmxSSOSession;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
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
public class ServiceProviderMBeanImpl extends AbstractProviderMBean implements ServiceProviderMBean {

    private static final Log logger = LogFactory.getLog(ServiceProviderMBeanImpl.class);

    private ServiceProviderImpl serviceProvider;

    public ServiceProviderImpl getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProviderImpl serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected FederatedLocalProvider getProvider() {
        return serviceProvider;
    }

    public TabularData listSessionsAsTable() {

        try {
            if (logger.isTraceEnabled())
                logger.trace("Listing all SSO Sessions from MBean");

            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
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

            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
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

            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
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

            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            Collection<SSOSession> sessions = mgr.getUserSessions(username);
            return sessions.toArray(new SSOSession[sessions.size()]);
        } catch (Exception e) {
            logger.error("Cannot find sessions: " + e.getMessage(), e);
        }

        return null;
    }

    public long getSessionsCount() {
        try {
            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            //return mgr.getStatsCurrentSessions();
            return mgr.getSessionCount();
        } catch (Exception e) {
            logger.error("Cannot find SSO Sessions count");
            return -1;
        }

    }

    public long getTotalCreatedSessions() {
        try {
            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsCreatedSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO created sessions count");
            return -1;
        }

    }

    public long getTotalDestroyedSessions() {
        try {
            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsDestroyedSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO destroyed count");
            return -1;
        }

    }

    public long getMaxSessionsCount() {
        try {
            IdPChannel channel = (IdPChannel) serviceProvider.getChannel();
            SSOSessionManager mgr = channel.getSessionManager();

            return mgr.getStatsMaxSessions();
        } catch (Exception e) {
            logger.error("Cannot find SSO max sessions count");
            return -1;
        }

    }
}
