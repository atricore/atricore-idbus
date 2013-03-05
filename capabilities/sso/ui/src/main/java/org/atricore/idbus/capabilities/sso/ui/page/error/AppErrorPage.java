package org.atricore.idbus.capabilities.sso.ui.page.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.IdBusErrorPage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/4/13
 */
public class AppErrorPage extends BasePage {

    private static final Log logger = LogFactory.getLog(IdBusErrorPage.class);

    public AppErrorPage() throws Exception {
        this(null);
    }

    public AppErrorPage(PageParameters parameters) throws Exception {
        // TODO : ?!
    }

}
