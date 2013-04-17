package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/9/13
 */
public class RegistrationEMailTemplate extends WebPage {

    /**
     * Constructor.
     *
     * @param parameters
     *            the current page parameters
     */
    public RegistrationEMailTemplate(final PageParameters parameters) {
        super(parameters);

        String registrationConfirmUrl = parameters.get("confirmUrl").toString();
        String newPassword = parameters.get("tmpPassword").toString();

        add(new Label("password", newPassword));

        add(new ExternalLink("registrationLink", registrationConfirmUrl));

    }
}

