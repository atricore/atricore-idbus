package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mortbay.jetty.Request;
import org.mortbay.util.MultiMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpErrServlet  extends HttpServlet {

    private static final Log logger = LogFactory.getLog(HttpErrServlet.class);

    protected ServletContext servletContext;

    protected VelocityEngine velocityEngine;

    private Map<String, String> templates = new HashMap<String, String>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        try {
            servletContext = config.getServletContext();
            velocityEngine = HttpUtils.getVelocityEngine();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Path info MUST be "/{http-status-code}
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // default page
        String pathInfo = req.getPathInfo();
        String statusStr = pathInfo.substring(1);

        String pageName = "404.html"; // Default
        int status = 404; // Default

        if (pathInfo != null) {
            try {
                status = Integer.parseInt(statusStr);
                pageName = status +  ".html";
            } catch (NumberFormatException e) {
                logger.trace("Illegal status " + statusStr + ", forcing 404");
            } catch (NullPointerException e) {
                logger.trace("Illegal status NULL, forcing 404");
            }

            // Lookup for branding
            WebBranding branding = HttpUtils.resolveWebBranding(servletContext, req);
            String templateLocation = "/WEB-INF/err/" + branding.getWebBrandingId() + "/" + pageName;

            // We need to get the Jetty request to get parameters (bug?)
            Request r = (Request) req.getAttribute("org.ops4j.pax.web.service.internal.jettyRequest");
            String location = r.getParameter("location");
            String shortLocation = "";
            if (location != null) {
                location = new String(Base64.decodeBase64(location.getBytes()));
                int  args = location.indexOf("?");
                shortLocation = args > 0 ? location.substring(0, args) : location;
            } else {
                location = "";
            }

            // Velocity
            VelocityContext veCtx = new VelocityContext();
            veCtx.put("location", shortLocation != null ? shortLocation : location);
            Reader in = resolveTemplate(templateLocation);

            // Write to response
            resp.setStatus(status);
            velocityEngine.evaluate(veCtx, resp.getWriter(), status + ".html", in);
        }

    }

    protected Reader resolveTemplate(String templateLocation) throws IOException {

        String content = templates.get(templateLocation);
        if (content == null) {
            if (logger.isDebugEnabled())
                logger.debug("Resolving pate template ["+templateLocation+"]");
            InputStream pageIs = servletContext.getResourceAsStream(templateLocation);
            content = IOUtils.toString(pageIs, "UTF-8");
            templates.put(templateLocation, content);
        }

        return new StringReader(content);


    }
}
