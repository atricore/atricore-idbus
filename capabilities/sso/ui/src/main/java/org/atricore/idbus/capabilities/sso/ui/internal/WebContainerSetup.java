package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.web.service.WebContainer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebContainerSetup {

    private static Log logger = LogFactory.getLog(WebContainerSetup.class);

    private WebContainer webContainer;

    public WebContainer getWebContainer() {
        return webContainer;
    }

    public void setWebContainer(WebContainer webContainer) {
        this.webContainer = webContainer;
    }

    public void init() {
        /*
        CssResourceFilter cssf = new CssResourceFilter();
        SVGResourceFilter svgf = new SVGResourceFilter();

        logger.debug("Registering filter for CSS resource handling ");

        webContainer.registerFilter(cssf, new String[] { "/*" }, null, null, null);
        webContainer.registerFilter(svgf, new String[] { "*.svg" }, null, null, null);
         */
    }
}
