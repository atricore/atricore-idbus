package org.atricore.idbus.kernel.main.mediation.state;

import org.atricore.idbus.kernel.main.mediation.provider.LocalProvider;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ProviderStateContext {

    private LocalProvider provider;

    private ClassLoader cl;

    public ProviderStateContext(LocalProvider provider, ClassLoader cl) {
        this.provider = provider;
        this.cl = cl;
    }

    public ProviderStateManager getStateManager() {
        return provider.getStateManager();
    }

    public LocalProvider getProvider() {
        return provider;
    }

    public ClassLoader getClassLoader() {
        return cl;
    }

    public LocalState retrieve(String key) {
        return provider.getStateManager().retrieve(this, key);
    }

    public LocalState retrieve(String keyName, String key) {
        return provider.getStateManager().retrieve(this, keyName, key);
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
