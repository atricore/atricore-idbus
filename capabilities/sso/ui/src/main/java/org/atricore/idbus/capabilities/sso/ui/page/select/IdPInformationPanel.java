package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class IdPInformationPanel extends Panel {

    private SelectIdPMediator mediator;

    public IdPInformationPanel(String componentId, IModel<IdPModel> model, SelectIdPMediator mediator) {
        super(componentId, model);

        this.mediator = mediator;

        add(new Label("idpId", model.getObject().getId()));
        add(new Label("idpType", new ResourceModel(model.getObject().getProviderType())));

    }
}

