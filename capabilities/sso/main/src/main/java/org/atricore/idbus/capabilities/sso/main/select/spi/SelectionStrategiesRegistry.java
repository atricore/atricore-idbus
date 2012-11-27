package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.SSOException;

import java.util.Collection;

/**
 *
 */
public interface SelectionStrategiesRegistry {

    void registerStrategy(SelectionStrategy strategy) throws SSOException;

    void unregisterStrategy(SelectionStrategy strategy) throws SSOException;

    Collection<SelectionStrategy> listStrategies() throws SSOException;

    SelectionStrategy lookup(String strategyName);
}
