package org.atricore.idbus.capabilities.sso.ui.selfsvcs.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/27/13
 */
public class ProfilePanel extends Panel {

    private static final Log logger = LogFactory.getLog(ProfilePanel.class);

    public ProfilePanel(String id, IModel<?> model) {
        super(id, model);
    }

    public ProfilePanel(String id) {
        super(id);
    }
}
