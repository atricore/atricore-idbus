package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class SpDetailsPanel extends Panel {

    public SpDetailsPanel(String componentId, IModel<PartnerAppModel> model) {
        super(componentId, model);
        add(new Label("spId", model.getObject().getId()));
        add(new Label("spType", new ResourceModel(model.getObject().getResourceType())));

    }
}
