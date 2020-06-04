package org.atricore.idbus.kernel.main.mediation.camel.component.http.ui;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.HttpUtils;
import org.mortbay.jetty.Request;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Serlvet to handle HTTP error status, like 404 or 500. It will render a page based on the status value.
 */
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
                if (status < 100)
                    status = 500;
                pageName = status +  ".html";
            } catch (NumberFormatException e) {
                logger.trace("Illegal status " + statusStr + ", forcing 404");
            } catch (NullPointerException e) {
                logger.trace("Illegal status NULL, forcing 404");
            }

            String internalError = null;
            // Lookup for branding
            WebBranding branding = HttpUtils.resolveWebBranding(servletContext, req);
            String templateLocation = "/WEB-INF/err/" + branding.getWebBrandingId() + "/" + pageName;
            Reader in = resolveTemplate(templateLocation);
            if (in == null) {
                logger.warn("Unsupported HTTP Status code: " + statusStr);
                in = resolveTemplate("/WEB-INF/err/" + branding.getWebBrandingId() + "/" + "404.html");
                internalError = "Illegal status : " + statusStr;
            }

            // We need to get the Jetty request to get parameters (bug?)
            Request r = (Request) req.getAttribute("org.ops4j.pax.web.service.internal.jettyRequest");

            // Location
            String location = r.getParameter("location");
            String shortLocation = "";
            if (location != null) {
                location = new String(Base64.decodeBase64(location.getBytes())).
                        replace("<", "&lt;").
                        replace(">", "&gt;");
                int  args = location.indexOf("?");
                shortLocation = args > 0 ? location.substring(0, args) : location;
            } else {
                location = "";
            }

            // Error
            String error = r.getParameter("error");
            if (error != null) {
                error = new String(Base64.decodeBase64(error.getBytes())).
                        replace("<", "&lt;").
                        replace(">", "&gt;");
            } else if (internalError != null) {
                error = internalError;
            } else {
                error = "";
            }

            // Velocity
            VelocityContext veCtx = new VelocityContext();

            veCtx.put("location", URLEncoder.encode(shortLocation, "UTF-8"));
            veCtx.put("error", error);

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
            if (pageIs == null)
                return null;

            content = IOUtils.toString(pageIs, "UTF-8");
            templates.put(templateLocation, content);
        }

        return new StringReader(content);


    }
}
