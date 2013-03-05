package org.atricore.idbus.capabilities.sso.ui.spi;

import org.apache.wicket.markup.head.HeaderItem;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IPageHeaderContributor {

    void render(HeaderItem response, BasePage page);
}
