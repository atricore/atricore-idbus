package org.atricore.idbus.capabilities.sso.ui.spi;

import org.atricore.idbus.capabilities.sso.ui.WebBranding;

/**
 * Allows dynamic registration/unregistration of web brandings
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface WebBrandingRegistry {

    void register(WebBranding branding);

    void unregister(WebBranding branding);

}
