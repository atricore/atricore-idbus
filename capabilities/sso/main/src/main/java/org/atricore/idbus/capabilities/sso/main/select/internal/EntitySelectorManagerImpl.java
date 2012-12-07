package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class EntitySelectorManagerImpl implements EntitySelectorManager {

    // Selectors by concern
    private SelectionStrategiesRegistry registry;

    public List<String> resolveAttributeEndpoints(String strategyName) throws SSOException {

        List<String> endpoints = new ArrayList<String>();
        SelectionStrategy strategy = registry.lookup(strategyName);
        if (strategy == null)
            throw new SSOException("Invalid selection strategy " + strategyName);

        for (int i = 0; i < strategy.getSelectors().size(); i++) {
            EntitySelector selector = strategy.getSelectors().get(i);

            if (selector.getSelectorAttributesEndpoint() != null) {
                endpoints.add(selector.getSelectorAttributesEndpoint());
            }
        }
        return endpoints;
    }

    public CircleOfTrustMemberDescriptor selectEntity(String strategyName, EntitySelectionContext ctx) throws SSOException {
        SelectionStrategy strategy = registry.lookup(strategyName);
        if (strategy == null)
            throw new SSOException("Invalid selection strategy " + strategyName);

        for (int i = 0; i < strategy.getSelectors().size(); i++) {
            EntitySelector selector = strategy.getSelectors().get(i);

            if (!selector.canHandle(ctx))
                continue;

            CircleOfTrustMemberDescriptor m = selector.selectCotMember(ctx);

            if (m != null)
                return m;
        }

        return null;
    }

    public SelectionStrategiesRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(SelectionStrategiesRegistry registry) {
        this.registry = registry;
    }
}
