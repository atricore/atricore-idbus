package org.atricore.idbus.capabilities.openid.ui;

import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BasePage extends WebPage {


    // list of supported Locales - should be replaced by resolver/detector or something similar
    private IModel<List<Locale>> supportedLocales = new ListModel<Locale>(Arrays.asList(Locale.FRENCH, Locale.ENGLISH));

    @SuppressWarnings("serial")
    public BasePage() {
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "style.css"));
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "grid.css"));

        add(new Label("footer", "OpenID Support"));

    }
}
