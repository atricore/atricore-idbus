package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class IdPDetailsPanel extends Panel {

    private final SelectIdPMediator mediator;

    private final IModel<IdPModel> model;

    public IdPDetailsPanel(String id, final IModel<IdPModel> model, final SelectIdPMediator mediator) {
        super(id, model);
        this.mediator = mediator;
        this.model = model;
    }

    @Override
    protected void onInitialize() {

        super.onInitialize();

        // IDP Description
        add(new Label("idpDescription", model.getObject().getDescription()));

        // IDP Initiated SSO link w/Button
        add(new Link<IdPModel>("ssoLinkBtn", model) {
            @Override
            public void onClick() {
                // Send response back!
                IdPModel idp = getModel().getObject();
                idp.getName();
                mediator.onSelectIdp(idp.getName(), true);
            }
        });

        add(new Label("idpId", model.getObject().getId()));

        add(new Label("idpType", new ResourceModel(model.getObject().getProviderType())));
    }


}
