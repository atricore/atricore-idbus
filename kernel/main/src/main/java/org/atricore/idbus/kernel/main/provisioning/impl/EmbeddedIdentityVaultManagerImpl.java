package org.atricore.idbus.kernel.main.provisioning.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityVaultManager;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityConnector;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmbeddedIdentityVaultManagerImpl implements IdentityVaultManager, BundleContextAware {

    private static final Log logger = LogFactory.getLog(EmbeddedIdentityVaultManagerImpl.class);

    private BundleContext bundleContext;

    private Map<ServiceReference, IdentityConnector> connectors = new ConcurrentHashMap<ServiceReference, IdentityConnector>();

    public void init() {

    }

    public void shutdown() {
        for (ServiceReference ref : connectors.keySet()) {
            try {
                bundleContext.ungetService(ref);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Collection<IdentityConnector> getSharedConnectors() throws ProvisioningException {
        List<IdentityConnector> sharedConnectors = new ArrayList<IdentityConnector>();
        for (IdentityConnector c : connectors.values()) {
            if (c.isShared())
                sharedConnectors.add(c);
        }
        return sharedConnectors;
    }

    @Override
    public synchronized Collection<IdentityConnector> getRegisteredConnectors() throws ProvisioningException {
        try {
            ServiceReference[] refs = bundleContext.getAllServiceReferences(IdentityConnector.class.getName(), null);
            connectors.clear();
            if (refs != null) {

                for (int i = 0; i < refs.length; i++) {
                    ServiceReference ref = refs[i];
                    IdentityConnector c = (IdentityConnector) bundleContext.getService(ref);
                    if (logger.isDebugEnabled())
                        logger.debug("Discovered new Identity Connector : " + c);
                    connectors.put(ref, c);
                }
            }
            return connectors.values();
        } catch (InvalidSyntaxException e) {
            throw new ProvisioningException(e);
        }

    }

    @Override
    public IdentityConnector lookupByName(String name) throws ProvisioningException {
        Iterator<IdentityConnector> it = getRegisteredConnectors().iterator();
        while (it.hasNext()) {
            IdentityConnector connector = it.next();
            if (connector.getName().equals(name))
                return connector;
        }
        return null;
    }
}
