package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

/**
 * This is a work-around to a problem between IE 9 and Jetty 6.
 * Jetty is setting the wrong mime type when delivering CSS resources, therefore IE 9 ignores them for security reasons.
 *
 * We force the proper mime type
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CssWebRequestCycle extends WebRequestCycle {

    public CssWebRequestCycle(WebApplication application,
            WebRequest request,
            Response response) {

        super(application, request, response);
        
        String path = request.getPath();
        if (path == null) return;

        int mid = path.lastIndexOf('.');
        if (mid < 0) return;
        
//        System.out.println("Processing ["+uri+"]");
        String type = path.substring(mid + 1, path.length());
        if (type.equalsIgnoreCase("css")) {
//            System.out.println("Setting context type for "  + request.getPath());
            response.setContentType("text/css");
        }

    }
    
    

}
