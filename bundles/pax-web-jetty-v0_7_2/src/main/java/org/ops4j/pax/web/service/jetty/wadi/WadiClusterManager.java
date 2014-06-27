package org.ops4j.pax.web.service.jetty.wadi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.jetty.servlet.wadi.WadiCluster;
import org.ops4j.pax.swissbox.core.BundleUtils;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.model.Model;
import org.osgi.framework.BundleContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class WadiClusterManager implements SessionHandlerBuilder {

    private static final Log LOG = LogFactory.getLog(WadiClusterManager.class);

    private WadiCluster m_wadiCluster;

    private int nbReplica;
    private int numPartitions;
    private int sweepInterval;
    private boolean enableReplication;
    private boolean deltaReplication;
    private boolean secureCookies;

    public WadiClusterManager() {
        // Some default values
        nbReplica = 2;
        numPartitions = 24;
        sweepInterval = 360;
        enableReplication = true;
        deltaReplication = false;

        LOG.info("Using WADI Session Handler builder ...");

    }

    public void start() throws Exception {

        if (m_wadiCluster == null) {
            throw new IllegalStateException("WADI Cluster instance not configured!");
        }

        m_wadiCluster.start();

        if (LOG.isDebugEnabled())
            LOG.debug("Started WADI CLuster: " + m_wadiCluster);
    }

    public void stop() throws Exception {
        if (m_wadiCluster  != null)
            m_wadiCluster.stop();

        m_wadiCluster = null;
    }

    public SessionHandler build(Server server, Model model) {

        if (m_wadiCluster == null)
            throw new IllegalStateException("WADI Cluster not configured");

        ClassLoader orig = Thread.currentThread().getContextClassLoader();

        try {

            if (LOG.isTraceEnabled())
                LOG.trace("Building WADI SessionHandler ");


            BundleContext ctx = BundleUtils.getBundleContext(model.getContextModel().getBundle());

            // TODO : Tune this CL
            ClassLoader wadiClassLoader = new OsgiBundlespaceClassLoader(ctx,
                    org.codehaus.wadi.core.manager.Manager.class.getClassLoader(), ctx.getBundles());
            Thread.currentThread().setContextClassLoader(wadiClassLoader);

            SessionManager sm = new OsgiWadiSessionManager(wadiClassLoader,
                    m_wadiCluster, nbReplica, numPartitions, sweepInterval, enableReplication, deltaReplication, secureCookies);

            SessionHandler sh = new OsgiWadiSessionHandler(sm);

            if (LOG.isDebugEnabled())
                LOG.debug("Created WADI SessionHandler : " + sh);

            return sh;
        } catch (Exception e) {
            LOG.error("HTTP Session support disabled!. Cannot create Session Manager: " + e.getMessage(), e);
            return null;

        } finally {
            // Restore original classloader, no matter what.
            Thread.currentThread().setContextClassLoader(orig);
        }
    }


    public int getNbReplica() {
        return nbReplica;
    }

    public void setNbReplica(int nbReplica) {
        this.nbReplica = nbReplica;
    }

    public int getNumPartitions() {
        return numPartitions;
    }

    public void setNumPartitions(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public int getSweepInterval() {
        return sweepInterval;
    }

    public void setSweepInterval(int sweepInterval) {
        this.sweepInterval = sweepInterval;
    }

    public boolean isEnableReplication() {
        return enableReplication;
    }

    public void setEnableReplication(boolean enableReplication) {
        this.enableReplication = enableReplication;
    }

    public boolean isDeltaReplication() {
        return deltaReplication;
    }

    public void setDeltaReplication(boolean deltaReplication) {
        this.deltaReplication = deltaReplication;
    }

    public WadiCluster getWadiCluster() {
        return m_wadiCluster;
    }

    public void setWadiCluster(WadiCluster m_wadiCluster) {
        this.m_wadiCluster = m_wadiCluster;
    }

    public boolean isSecureCookies() {
        return secureCookies;
    }

    public void setSecureCookies(boolean secureCookies) {
        this.secureCookies = secureCookies;
    }
}
