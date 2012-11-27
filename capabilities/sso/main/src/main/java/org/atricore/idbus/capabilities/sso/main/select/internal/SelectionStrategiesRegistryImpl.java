package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.SelectionStrategiesRegistry;
import org.atricore.idbus.capabilities.sso.main.select.spi.SelectionStrategy;

import java.util.*;

/**
 */
public class SelectionStrategiesRegistryImpl implements SelectionStrategiesRegistry {

    private List<SelectionStrategy> builtInStrategies = new ArrayList<SelectionStrategy>();

    private Map<String, SelectionStrategy> registry = new HashMap<String, SelectionStrategy>();

    private boolean init = false;

    public void init() throws SSOException {
        if (init) return;
        synchronized (this) {
            if (init) return;
            init = true;
            for (SelectionStrategy builtInStregegy : builtInStrategies) {
                this.registerStrategy(builtInStregegy);
            }
        }
    }

    public void registerStrategy(SelectionStrategy strategy) throws SSOException {
        registry.put(strategy.getName(), strategy);
    }

    public void unregisterStrategy(SelectionStrategy strategy) throws SSOException {
        registry.remove(strategy.getName());
    }

    public Collection<SelectionStrategy> listStrategies() throws SSOException {
        return registry.values();
    }

    public SelectionStrategy lookup(String strategyName) {
        return registry.get(strategyName);
    }

    public List<SelectionStrategy> getBuiltInStrategies() {
        return builtInStrategies;
    }

    public void setBuiltInStrategies(List<SelectionStrategy> builtInStrategies) {
        this.builtInStrategies = builtInStrategies;
    }
}
