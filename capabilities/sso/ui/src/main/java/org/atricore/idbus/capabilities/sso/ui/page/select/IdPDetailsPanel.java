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

    public IdPDetailsPanel(String id, final IModel<IdPModel> model, final SelectIdPMediator mediator) {
        super(id, model);

        this.mediator = mediator;

        //add(new Label("displayName", model.getObject().getDisplayName()));
        add(new Label("description", model.getObject().getDescription()));
        add(new CheckBox("rememberSelection") {

            @Override
            protected void onSelectionChanged(Boolean newSelection) {
                super.onSelectionChanged(newSelection);
            }
        });
        add(new Link<IdPModel>("ssoLink", model) {

            @Override
            public void onClick() {
                // Send response back!
                IdPModel idp = getModel().getObject();
                idp.getName();
                mediator.onSelectIdp(idp.getName());
            }
        }

        );
    }


}
