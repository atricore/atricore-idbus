package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingServiceException;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingRegistryImpl implements WebBrandingRegistry {
    
    private static final Log logger = LogFactory.getLog(WebBrandingRegistryImpl.class);

    private WebBrandingService service;

    public WebBrandingService getService() {
        return service;
    }

    public void setService(WebBrandingService service) {
        this.service = service;
    }

    public void register(WebBranding branding) {
        try {
            service.publish(branding.getId(), branding);
        } catch (WebBrandingServiceException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void unregister(WebBranding branding) {
        try {
            service.remove(branding.getId());
        } catch (WebBrandingServiceException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
