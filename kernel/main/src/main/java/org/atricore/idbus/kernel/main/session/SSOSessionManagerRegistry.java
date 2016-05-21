package org.atricore.idbus.kernel.main.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SSOSessionManagerRegistry {

    private Map<String, SSOSessionManagerFactory> factories = new HashMap<String, SSOSessionManagerFactory>();

    public void register(SSOSessionManagerFactory factory) {
        factories.put(factory.getName(), factory);
    }

    public void unregister(SSOSessionManagerFactory factory) {
        factories.remove(factory.getName());
    }

    public Collection<SSOSessionManagerFactory> listFactories() {
        return factories.values();
    }
}
