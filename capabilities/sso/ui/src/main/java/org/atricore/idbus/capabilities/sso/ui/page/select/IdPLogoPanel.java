package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.AppResource;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class IdPLogoPanel extends Panel {

    public IdPLogoPanel(String id, IModel<IdPModel> model, final SelectIdPMediator mediator) {
        super(id, model);

        Link idpLogo = new Link<IdPModel>("ssoLink", model) {
            @Override
            public void onClick() {
                // Send response back!
                IdPModel idp = getModel().getObject();
                idp.getName();
                mediator.onSelectIdp(idp.getName());
            }
        };

        idpLogo.add(
            new Image("idpLogo", new PackageResourceReference(AppResourceLocator.class,
                    AppResource.getForResource(model.getObject().getProviderType()).getImage()))
        );

        add(idpLogo);
    }

}
