package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.PartnerAppModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class AppDetailsPanel extends Panel {

    public AppDetailsPanel(String id, IModel<PartnerAppModel> model) {
        super(id, model);

        //add(new Label("displayName", model.getObject().getDisplayName()));
        add(new Label("description", model.getObject().getDescription()));
        add(new ExternalLink("login", model.getObject().getSsoEndpoint()));
    }


}
