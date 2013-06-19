package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.sidebar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.util.List;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/11/13
 */
public class SideBarPanel extends Panel {

    protected User user ;

    protected List<PartnerAppModel> apps;
    public SideBarPanel(String id, User u, List<PartnerAppModel> partnerApps) {
        super(id);

        this.user = u;
        this.apps = partnerApps;


    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Populate user and apps information !
        String fullName = user.getFirstName() + " " + user.getSurename();

        add(new Label("fullName", fullName));
        add(new Label("email", user.getEmail()));

        // Build apps table
        ListView<PartnerAppModel> appsList = new ListView<PartnerAppModel>("apps", apps) {

            @Override
            public void populateItem(final ListItem<PartnerAppModel> listItem) {
                final PartnerAppModel app = listItem.getModelObject();

                String appName = app.getDescription() != null ? app.getDescription() : app.getName();

                listItem.add(new ExternalLink("ssoLink", app.getSsoEndpoint()).add(new Label("name", new Model<String>(appName))));

                //listItem.add(new MultiLineLabel("text", comment.getText()));
            }
        };

        add(appsList);

    }

    public List<PartnerAppModel> getApps() {
        return apps;
    }

    public void setApps(List<PartnerAppModel> apps) {
        this.apps = apps;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

