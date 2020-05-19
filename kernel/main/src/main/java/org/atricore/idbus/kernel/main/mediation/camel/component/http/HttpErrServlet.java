package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Request;
import org.mortbay.util.MultiMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class HttpErrServlet  extends HttpServlet {

    private static final Log logger = LogFactory.getLog(HttpErrServlet.class);

    protected ServletContext servletContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servletContext = config.getServletContext();
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
        String page = "404.html";

        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            String statusStr = pathInfo.substring(1);

            try {
                int status = Integer.parseInt(statusStr);
                page = status +  ".html";
            } catch (NumberFormatException e) {
                logger.trace ("Illegal status " + statusStr + ", forcing 404");
            }
        }

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

        String html = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/err/" + page));
        html = String.format(html, shortLocation, location); // TODO :Improve templating.
        resp.getWriter().print(html);
    }
}
