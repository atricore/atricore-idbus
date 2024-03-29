package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class AppDetailsPanel extends Panel {

    public AppDetailsPanel(String id, IModel<PartnerAppModel> model) {
        super(id, model);

        //add(new Label("displayName", model.getObject().getDisplayName()));

        // App ID as i18n key, defaulted to description.
        String appName = getLocalizer().getString("app.description." + model.getObject().getName(),
                this,
                model.getObject().getDescription());

        add(new Label("description", appName));
        add(new ExternalLink("login", model.getObject().getSsoEndpoint()));

        add(new Label("spId", model.getObject().getId()).setVisible(false));
        add(new Label("spType", new ResourceModel(model.getObject().getResourceType())));
    }


}
