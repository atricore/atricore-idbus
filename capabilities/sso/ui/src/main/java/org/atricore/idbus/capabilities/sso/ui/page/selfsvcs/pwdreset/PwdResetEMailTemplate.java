package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/11/13
 */
public class PwdResetEMailTemplate extends WebPage {

    /**
     * Constructor.
     *
     * @param parameters
     *            the current page parameters
     */
    public PwdResetEMailTemplate(final PageParameters parameters) {
        super(parameters);

        String transactionId = parameters.get("transactionId").toString();

        // TODO : Validate parameters ?!
        add(new Label("name", parameters.get("name").toString("Unknown")));

        add(new Label("username", parameters.get("username").toString("Unknown")));


        // TODO : Improve this, it must be a better way to create an external link to the PwdRegister.class page !
        String path = RequestCycle.get().getRequest().getFilterPath();

        String pagePath = urlFor(((BaseWebApplication)getApplication()).resolvePage("SS/PWDRESET"), new PageParameters().add("transactionId", transactionId)).toString();
        pagePath = pagePath.substring(1);

        path = path + "/SS" + pagePath;

        Url url  = RequestCycle.get().getRequest().getClientUrl();

        String link = url.getProtocol() + "://" +
                url.getHost() + (url.getPort() != 443 && url.getPort() != 80 ? ":" + url.getPort() + "" : "") +
                path;

        add(new ExternalLink("pwdResetLink", link));

    }
}

