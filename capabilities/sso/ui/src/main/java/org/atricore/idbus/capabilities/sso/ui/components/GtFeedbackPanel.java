package org.atricore.idbus.capabilities.sso.ui.components;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/7/13
 */
public class GtFeedbackPanel extends FeedbackPanel {

    public GtFeedbackPanel(String id) {
        super(id);
    }

    public GtFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        return super.getCSSClass(message);
    }
}
