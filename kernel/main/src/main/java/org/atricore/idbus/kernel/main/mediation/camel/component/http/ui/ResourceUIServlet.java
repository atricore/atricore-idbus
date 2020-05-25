package org.atricore.idbus.kernel.main.mediation.camel.component.http.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.OsgiIDBusServlet2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet used to resolve resources needed by UI pages.
 */
public class ResourceUIServlet extends HttpServlet {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet2.class);

    protected ServletContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.context = config.getServletContext();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final int bufferSize = 65535;

        String uri = req.getPathInfo();
        String resourcePath  = "/WEB-INF/processing-ui" + uri;

        InputStream resourceStream = null;
        OutputStream out = resp.getOutputStream();
        try {
            resourceStream = context.getResourceAsStream(resourcePath);

            if (resourceStream == null) {
                resp.setStatus(404);
                return;
            }

            // TODO : Other types ?
            if (uri.endsWith(".svg"))
                resp.setContentType("image/svg+xml");

            int bytesRead;
            byte[] buffer = new byte[bufferSize];
            while( (bytesRead = resourceStream.read(buffer, 0, bufferSize)) > 0 ) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } finally   {
            if( resourceStream != null )
                resourceStream.close();
            out.close();
        }


    }


}
