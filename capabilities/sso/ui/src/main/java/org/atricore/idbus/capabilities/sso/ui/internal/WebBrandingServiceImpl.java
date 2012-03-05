package org.atricore.idbus.capabilities.sso.ui.internal;

import org.atricore.idbus.capabilities.sso.ui.BrandingResource;
import org.atricore.idbus.capabilities.sso.ui.BrandingResourceType;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingServiceImpl implements WebBrandingService {

    private Map<String, WebBranding> brandings = new ConcurrentHashMap<String, WebBranding>();

    public WebBrandingServiceImpl() {

    }

    public void init() {

        // TODO : Make this configurable ...

        // JOSSO 2 Default branding
        WebBranding branding = new WebBranding();
        branding.setId("josso2-default-branding");
        branding.setDescription("JOSSO 2.x Default");
        branding.setSkin("josso2");

        // CSS
        branding.getResources().add(new BrandingResource("ie6", "ie6.css", "", BrandingResourceType.CSS));
        branding.getResources().add(new BrandingResource("ie7", "ie7.css", "",  BrandingResourceType.CSS));
        branding.getResources().add(new BrandingResource("processing", "processing.css", "",  BrandingResourceType.CSS));
        branding.getResources().add(new BrandingResource("reset", "reset.css", "",  BrandingResourceType.CSS));
        branding.getResources().add(new BrandingResource("screen", "screen.css", "",  BrandingResourceType.CSS));

        // Images
        branding.getResources().add(new BrandingResource("jossoLogo", "images/josso-logo.png", "",  BrandingResourceType.CSS));
        branding.getResources().add(new BrandingResource("atricoreLogo", "images/atricore-logo.png", "",  BrandingResourceType.CSS));

        // Labels
        branding.getResources().add(new BrandingResource("footer", "", "Atricore, Inc.", BrandingResourceType.LABEL));

        // Publish default branding
        publish(branding.getId(), branding);

    }

    public WebBranding lookup(String id) {
        return brandings.get(id);
    }

    public void publish(String id, WebBranding branding) {
        brandings.put(id, branding);
    }

    public void remove(String id) {
        brandings.remove(id);
    }

    public Collection<WebBranding> list() {
        return brandings.values();
    }
}
