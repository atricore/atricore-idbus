package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class AppLogoPanel extends Panel {

    public AppLogoPanel(String id, IModel<PartnerAppModel> model) {
        super(id, model);
        add(new ExternalLink("login", model.getObject().getSsoEndpoint()).add(
                new Image("appLogo", new PackageResourceReference(AppResourceLocator.class,
                        AppResource.getForResource(model.getObject().getResourceType()).getImage()))));
    }

}
