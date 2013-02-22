package org.atricore.idbus.capabilities.sso.ui.selfsvcs.register;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class BaseRegisterPanel extends Panel {

    public BaseRegisterPanel(String id) {
        super(id);
    }

    public BaseRegisterPanel(String id, IModel<?> model) {
        super(id, model);
    }
}
