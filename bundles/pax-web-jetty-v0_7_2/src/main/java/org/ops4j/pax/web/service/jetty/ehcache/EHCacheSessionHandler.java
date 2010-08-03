package org.ops4j.pax.web.service.jetty.ehcache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.servlet.SessionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheSessionHandler extends SessionHandler {

    private static final Log logger = LogFactory.getLog(EHCacheSessionHandler.class);

    public EHCacheSessionHandler() {
        super();
    }

    public EHCacheSessionHandler(SessionManager manager) {
        super(manager);
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        if (logger.isTraceEnabled())
            logger.trace("Handling session for request/response " + request + "/" + response);

        super.handle(target, request, response, dispatch);
    }
}
