package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.spi.*;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class EntitySelectorManagerImpl implements EntitySelectorManager {

    // Selectors by concern
    private SelectionStrategiesRegistry registry;

    public List<EntitySelector> resolveSelectors(EntitySelectionContext ctx, SelectorChannel channel, String strategyName) throws SSOException {
        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();
        SelectionStrategy strategy = registry.lookup(strategyName);
        if (strategy == null)
            throw new SSOException("Invalid selection strategy " + strategyName);

        return strategy.getSelectors();
    }

    public List<EndpointDescriptor> resolveUserClaimsEndpoints(EntitySelectionContext ctx, SelectorChannel channel, String strategyName) throws SSOException {

        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();
        SelectionStrategy strategy = registry.lookup(strategyName);
        if (strategy == null)
            throw new SSOException("Invalid selection strategy " + strategyName);

        for (int i = 0; i < strategy.getSelectors().size(); i++) {
            EntitySelector selector = strategy.getSelectors().get(i);

            Collection<EndpointDescriptor> se = selector.getUserClaimsEndpoints(ctx, channel);
            if (se != null) {
                endpoints.addAll(se);
            }
        }
        return endpoints;
    }

    public CircleOfTrustMemberDescriptor selectEntity(String strategyName, EntitySelector preferedSelector,  EntitySelectionContext ctx, SelectorChannel channel) throws SSOException {
        SelectionStrategy strategy = registry.lookup(strategyName);
        if (strategy == null)
            throw new SSOException("Invalid selection strategy " + strategyName);

        for (int i = 0; i < strategy.getSelectors().size(); i++) {
            EntitySelector selector = strategy.getSelectors().get(i);

            if (!selector.equals(preferedSelector))
                continue;

            CircleOfTrustMemberDescriptor m = selector.selectCotMember(ctx, channel);

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
