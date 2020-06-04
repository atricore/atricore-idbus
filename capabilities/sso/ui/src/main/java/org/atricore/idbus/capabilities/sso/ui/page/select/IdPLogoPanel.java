package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.atricore.idbus.capabilities.sso.ui.BrandingResource;
import org.atricore.idbus.capabilities.sso.ui.BrandingResourceType;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;
import org.atricore.idbus.capabilities.sso.main.AppResource;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class IdPLogoPanel extends Panel {

    private final IModel<IdPModel> model ;

    private final SelectIdPMediator mediator;

    public IdPLogoPanel(String id, IModel<IdPModel> model, final SelectIdPMediator mediator) {
        super(id, model);
        this.model = model;
        this.mediator = mediator;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        String idpLogoResource = null;

        BaseWebApplication app = (BaseWebApplication) getApplication();
        if (app.getBranding() != null) {
            WebBranding b = app.getBranding();
            for (BrandingResource r  : b.getResources()) {
                if (r.getType().equals(BrandingResourceType.IMAGE) && r.getId().equals(model.getObject().getProviderType())) {
                    idpLogoResource = r.getPath();
                }
            }
        }

        if (idpLogoResource == null)
            idpLogoResource = AppResource.getForResource(model.getObject().getProviderType()).getImage();

        Link idpLogo = new Link<IdPModel>("ssoLink", model) {
            @Override
            public void onClick() {
                // Send response back!
                IdPModel idp = getModel().getObject();
                idp.getName();
                mediator.onSelectIdp(idp.getName(), true);
            }
        };

        idpLogo.add(
            new Image("idpLogo",
                    new PackageResourceReference(AppResourceLocator.class, idpLogoResource )));

        add(idpLogo);
    }

}
