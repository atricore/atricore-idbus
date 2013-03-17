package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * This is a work-around to a problem between IE 9 and Jetty 6.
 * Jetty is setting the wrong mime type when delivering CSS resources, therefore IE 9 ignores them for security reasons.
 *
 * We force the proper mime type
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdBusRequestCycle extends RequestCycle {

    public IdBusRequestCycle(RequestCycleContext ctx) {

        super(ctx);
        
        String url = ctx.getRequest().getUrl().toString();
        if (url == null) return;

        int mid = url.lastIndexOf('.');
        if (mid < 0) return;
        
        String type = url.substring(mid + 1, url.length());
        if (type.equalsIgnoreCase("css")) {
            ((HttpServletResponse)ctx.getResponse().getContainerResponse()).setContentType("text/css");
        }

    }

    @Override
    protected void onEndRequest() {
        super.onEndRequest();
        ((WebResponse) getResponse()).setHeader("X-FollowRedirect", "FALSE" );
    }
}
