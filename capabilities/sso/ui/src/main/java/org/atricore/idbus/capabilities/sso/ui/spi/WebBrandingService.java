package org.atricore.idbus.capabilities.sso.ui.spi;

import org.atricore.idbus.capabilities.sso.ui.WebBranding;

import java.util.Collection;

/**
 * This service holds references to all installed brandings.  These are brandings that are used in runtime.
 *
 * Actual branding definitions management, should be perform by another application. (console probably)
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface WebBrandingService {

    WebBranding lookup(String id);

    void publish(String id, WebBranding branding) throws WebBrandingServiceException;

    void remove(String id) throws WebBrandingServiceException;

    Collection<WebBranding> list();

    void register(WebBrandingEventListener listener);

    void unregister(WebBrandingEventListener listener);

}
