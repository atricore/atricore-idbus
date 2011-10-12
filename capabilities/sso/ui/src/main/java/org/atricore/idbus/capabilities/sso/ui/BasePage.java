package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BasePage extends WebPage {

    @SuppressWarnings("serial")
    public BasePage() {
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "ie6.css"));
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "ie7.css"));
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "processing.css"));
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "reset.css"));
        add(CSSPackageResource.getHeaderContribution(BasePage.class, "screen.css"));

        add(new Image("jossoLogo", new ResourceReference(BasePage.class, "images/josso-logo.png")));
        add(new Image("atricoreLogo", new ResourceReference(BasePage.class, "images/atricore-logo.gif")));

        add(new Label("footer", "Atricore"));

    }
}
