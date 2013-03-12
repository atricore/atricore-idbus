package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingRegistry;

import java.util.Map;

/**
 * Registers web brandings available as OSGi services
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiWebBrandingRegitry {

    private static final Log logger = LogFactory.getLog(OsgiWebBrandingRegitry.class);

    private WebBrandingRegistry registry;

    public OsgiWebBrandingRegitry(WebBrandingRegistry r) {
        this.registry = r;
    }

    public void register(final WebBranding branding, final Map<String, ?> properties) {
        logger.info("Web Branding registered : " + branding.getId());
        if (logger.isDebugEnabled()) {
            logger.debug("Web Branding registered " + branding);
        }
        registry.register(branding);
    }

    public void unregister(final WebBranding branding, final Map<String, ?> properties) {
        logger.info("Web Branding unregistered : " + branding.getId());
        if (logger.isDebugEnabled()) {
            logger.debug("Web Branding unregistered " + branding);
        }

        registry.unregister(branding);
    }
}
