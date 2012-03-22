package org.atricore.idbus.capabilities.sso.ui.internal;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CssResourceFilter implements Filter {
    
    
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            System.out.println("FILTERING CSS REQUEST !!!!! " + req.getLocalName());
            //res.setContentType("text/css");
        } finally {
            chain.doFilter(req, res);
        }
    }

    public void destroy() {

    }
}
