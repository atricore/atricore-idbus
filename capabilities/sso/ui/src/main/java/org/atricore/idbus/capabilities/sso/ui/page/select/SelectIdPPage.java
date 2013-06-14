package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/13/13
 */
public class SelectIdPPage extends BasePage {

    private static Log logger = LogFactory.getLog(SelectIdPPage.class);

    public SelectIdPPage() throws Exception {
        this(null);
    }

    public SelectIdPPage(PageParameters parameters) throws Exception {
        super(parameters);
    }



}
