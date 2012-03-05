package org.atricore.idbus.capabilities.sso.ui.internal;

import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingRegistryImpl implements WebBrandingRegistry {

    private WebBrandingService service;

    public WebBrandingService getService() {
        return service;
    }

    public void setService(WebBrandingService service) {
        this.service = service;
    }

    public void register(WebBranding branding) {
        service.publish(branding.getId(), branding);
    }

    public void unregister(WebBranding branding) {
        service.remove(branding.getId());
    }
}
