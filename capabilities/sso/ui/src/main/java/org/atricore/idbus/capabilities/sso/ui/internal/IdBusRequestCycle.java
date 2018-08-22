package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.http.WebResponse;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.IDBusHttpConstants;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.XFrameOptions;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

/**
 * This is a work-around to a problem between IE 9 and Jetty 6.
 * Jetty is setting the wrong mime type when delivering CSS resources, therefore IE 9 ignores them for security reasons.
 *
 * We force the proper mime type
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdBusRequestCycle extends RequestCycle {

    private BaseWebApplication app;

    public IdBusRequestCycle(RequestCycleContext ctx, BaseWebApplication app) {

        super(ctx);

        this.app = app;
        
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

        WebResponse res = ((WebResponse) getResponse());
        res.setHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_FOLLOW_REDIRECT, "FALSE" );

        // Configured headers:
        ConfigurationContext kernelConfig = app.getKernelConfig();
        if (kernelConfig != null) {

            // Add node ID to response headers
            String nodeId = kernelConfig.getProperty("idbus.node");
            if(nodeId != null)
                res.addHeader("X-IdBus-Node", nodeId);

            // Add additional headers

            String xFrameOptions = kernelConfig.getProperty("binding.http.xFrameOptionsMode");
            if (xFrameOptions != null) {

                XFrameOptions mode = XFrameOptions.fromValue(xFrameOptions);

                String xFrameOptoinsURLs = "";
                if (kernelConfig.getProperty("binding.http.xFrameOptionsURLs") != null) {
                    StringTokenizer st = new StringTokenizer(kernelConfig.getProperty("binding.http.xFrameOptionsURLs"), ",");
                    while (st.hasMoreTokens()) {
                        String s = st.nextToken();
                        xFrameOptoinsURLs = xFrameOptoinsURLs + " '" + s + "'";
                    }
                }


                switch(mode) {
                    case DISABLED:
                        // Nothing to do
                        break;
                    case SAME_ORIGIN:
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue());
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors 'self'" + xFrameOptoinsURLs);
                        break;
                    case ALLOW_FROM:
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue() + xFrameOptoinsURLs);
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors" + xFrameOptoinsURLs);
                        break;
                    case DENY:
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue());
                        res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors 'none'");
                        break;
                    default:
                        //logger.error("Unknown X-Frame-Options mode " + mode.getValue());
                        break;

                }
            }
        }

    }
}
