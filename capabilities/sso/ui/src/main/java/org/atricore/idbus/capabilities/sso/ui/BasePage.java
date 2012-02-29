/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;

/**
 * Convenience base page for concrete SSO pages requiring a common layout and theme.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class BasePage extends WebPage {

    private String variation;

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

    public void setVariation(String variation) {
        this.variation = variation;
    }

    @Override
    public String getVariation() {
        return variation;
    }


}
