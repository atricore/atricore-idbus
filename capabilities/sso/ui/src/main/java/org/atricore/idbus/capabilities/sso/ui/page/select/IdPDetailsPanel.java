package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
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

    // IDP Initiated SSO link
        add(new Link<IdPModel>("ssoLink", model) {
            @Override
            public void onClick() {
                // Send response back!
                IdPModel idp = getModel().getObject();
                idp.getName();
                mediator.onSelectIdp(idp.getName(), true);
            }
        }.add(new Label("description", model.getObject().getDescription())));
    }


}
