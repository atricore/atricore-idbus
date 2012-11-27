package org.atricore.idbus.kernel.main.mediation.state;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.StatefulProvider;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProviderStateContext {

    private StatefulProvider provider;

    private ClassLoader cl;

    private static final Log logger = LogFactory.getLog(ProviderStateContext.class);

    public ProviderStateContext(StatefulProvider provider, ClassLoader cl) {
        this.provider = provider;
        this.cl = cl;
    }

    public ProviderStateManager getStateManager() {
        return provider.getStateManager();
    }

    public StatefulProvider  getProvider() {
        return provider;
    }

    public ClassLoader getClassLoader() {
        return cl;
    }

    public LocalState retrieve(String key) {
        return provider.getStateManager().retrieve(this, key);
    }

    public LocalState retrieve(String key, int retryCount, long retryDelay) {

        int retries = 0;

        LocalState s = provider.getStateManager().retrieve(this, key);
        while (s == null && retries < retryCount) {

            if (logger.isTraceEnabled())
                logger.trace("State not found for ["+key+"], retrying in "+retryDelay+" ms");

            synchronized (this) {
                try { Thread.sleep(retryDelay); } catch (InterruptedException e) { /**/ }
            }

            retries ++;
            s = provider.getStateManager().retrieve(this, key);
        }
        return s;
    }


    public LocalState retrieve(String keyName, String key) {
        return provider.getStateManager().retrieve(this, keyName, key);
    }

    public LocalState retrieve(String keyName, String key, int retryCount, long retryDelay) {

        int retries = 0;

        LocalState s = provider.getStateManager().retrieve(this, keyName, key);
        while (s == null && retries < retryCount) {

            if (logger.isTraceEnabled())
                logger.trace("State not found for ["+keyName+"/"+key+"], retrying in "+retryDelay+" ms");

            synchronized (this) {
                try { Thread.sleep(retryDelay); } catch (InterruptedException e) { /**/ }
            }

            retries ++;
            s = provider.getStateManager().retrieve(this, keyName, key);
        }
        return s;
    }


    public LocalState createState() {
        return provider.getStateManager().createState(this);
    }

    public void store(LocalState state) {
        provider.getStateManager().store(this, state);
    }

    public void remove(String key) {
        provider.getStateManager().remove(this, key);
    }

    public Collection<LocalState> retrievAll() {
        return provider.getStateManager().retrieveAll(this);
    }
}
