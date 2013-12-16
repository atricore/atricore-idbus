package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityVaultManager {

    Collection<IdentityConnector> getSharedConnectors() throws ProvisioningException;

    Collection<IdentityConnector> getRegisteredConnectors() throws ProvisioningException;

    IdentityConnector lookupByName(String name) throws ProvisioningException;

}
