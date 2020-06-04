package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.atricore.idbus.capabilities.sso.main.AppResource;
import org.atricore.idbus.capabilities.sso.ui.BrandingResource;
import org.atricore.idbus.capabilities.sso.ui.BrandingResourceType;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class AppLogoPanel extends Panel {

    public AppLogoPanel(String id, IModel<PartnerAppModel> model) {
        super(id, model);

        String spLogoResource = null;
        String spCustomLogoResource = null;
        BaseWebApplication app = (BaseWebApplication) getApplication();
        if (app.getBranding() != null) {
            WebBranding b = app.getBranding();
            for (BrandingResource r  : b.getResources()) {

                // Check if we have a custom logo for the App
                if (r.getType().equals(BrandingResourceType.IMAGE)
                    && r.getId().equals(model.getObject().getName())) {
                    spCustomLogoResource = r.getPath();
                }

                if (r.getType().equals(BrandingResourceType.IMAGE) &&
                        r.getId().equals(model.getObject().getResourceType())) {
                    spLogoResource = r.getPath();
                }
            }
        }

        // If custom log is present, use it!
        if (spCustomLogoResource != null)
            spLogoResource = spCustomLogoResource;

        // If not, and standard logo is not present, use default.
        if (spLogoResource == null)
            spLogoResource = AppResource.getForResource(model.getObject().getResourceType()).getImage();

        // Build link with logo
        add(new ExternalLink("login", model.getObject().getSsoEndpoint()).add(
                new Image("appLogo", new PackageResourceReference(AppResourceLocator.class, spLogoResource))));
    }

}
