package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import javax.servlet.http.HttpServletRequest;

public class ProcessingUIPolicy implements InternalProcessingPolicy {

    @Override
    public boolean match(HttpServletRequest originalRequest, String redirectUrl) {
        return match(originalRequest);
    }

    @Override
    public boolean match(HttpServletRequest req) {

        // Do not use UI for IDP initited requests in REST  mode
        if (req.getPathInfo().endsWith("/SAML2/SSO/IDP_INITIATE") &&
                req.getParameter("atricore_response_format") != null &&
                req.getParameter("atricore_response_format").equals("REST"))
            return false;

        return true;
    }
}
