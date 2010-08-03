package org.ops4j.pax.web.service.jetty.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.SessionHandler;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.model.Model;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class DefaultSessionHandlerBuilder implements SessionHandlerBuilder {

    private static final Log LOG = LogFactory.getLog( DefaultSessionHandlerBuilder.class );

    public DefaultSessionHandlerBuilder() {
        LOG.info("Using Default Session Handler builder ...");
    }

    public SessionHandler build(Server server, Model model) {

        if (LOG.isTraceEnabled())
            LOG.trace("Building Default SessionHandler ");
        
        // Default JETTY Session Handler
        return new SessionHandler();
    }
}
