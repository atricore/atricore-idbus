package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.SpmlAjaxClient;
import com.atricore.idbus.console.services.spi.exceptions.SpmlAjaxClientException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2ServiceRegistry implements BundleContextAware {


    private static final Log logger = LogFactory.getLog(SpmlR2ServiceRegistry.class);

    private Map<ServiceReference, SpmlR2Client> services = new ConcurrentHashMap<ServiceReference, SpmlR2Client>();

    private BundleContext bundleContext;

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void register(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {

    }

    public void unregister(SpmlR2Client spml,  Map<String, ?> properties) throws Exception {
    }

    public SpmlR2Client lookUpClient(String pspTargetId) throws SpmlAjaxClientException {
        Collection<SpmlR2Client> svcs = lookupRegisteredServices();

        for (SpmlR2Client svc : svcs) {
            if (svc.hasTarget(pspTargetId))
                return svc;
        }

        return null;
    }

    public synchronized Collection<SpmlR2Client> lookupRegisteredServices() throws SpmlAjaxClientException {
        try {
            ServiceReference[] refs = bundleContext.getAllServiceReferences(SpmlR2Client.class.getName(), null);
            services.clear();

            for (int i = 0; i < refs.length; i++) {
                ServiceReference ref = refs[i];
                SpmlR2Client c = (SpmlR2Client) bundleContext.getService(ref);
                    if (logger.isDebugEnabled())
                        logger.debug("Discovered new SPML R2 Client service : " + c);

                services.put(ref, c);
            }

            return services.values();
        } catch (InvalidSyntaxException e) {
            logger.error(e.getMessage(), e);
            throw new SpmlAjaxClientException(e);
        }

    }


}
