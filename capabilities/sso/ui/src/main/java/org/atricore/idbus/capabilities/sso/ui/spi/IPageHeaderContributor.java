package org.atricore.idbus.capabilities.sso.ui.spi;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.atricore.idbus.capabilities.sso.ui.BasePage;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IPageHeaderContributor {

    void renderHead(IHeaderResponse response, BasePage page);
}
