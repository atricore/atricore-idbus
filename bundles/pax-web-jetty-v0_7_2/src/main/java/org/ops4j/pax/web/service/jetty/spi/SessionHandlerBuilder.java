package org.ops4j.pax.web.service.jetty.spi;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.SessionHandler;
import org.ops4j.pax.web.service.spi.model.Model;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SessionHandlerBuilder {

    SessionHandler build(Server server, Model model);

    void setSecureCookies(boolean secureCookies);

    boolean isSecureCookies();

}
