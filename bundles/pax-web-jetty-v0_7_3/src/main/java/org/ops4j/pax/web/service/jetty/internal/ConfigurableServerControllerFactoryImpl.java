package org.ops4j.pax.web.service.jetty.internal;

import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.ServerController;
import org.ops4j.pax.web.service.spi.ServerControllerFactory;
import org.ops4j.pax.web.service.spi.model.ServerModel;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ConfigurableServerControllerFactoryImpl implements ServerControllerFactory {

    private SessionHandlerBuilder sessionHandlerBuilder;

    public SessionHandlerBuilder getSessionHandlerBuilder() {
        return sessionHandlerBuilder;
    }

    public void setSessionHandlerBuilder(SessionHandlerBuilder sessionHandlerBuilder) {
        this.sessionHandlerBuilder = sessionHandlerBuilder;
    }

    public ServerController createServerController( ServerModel serverModel )
    {
        ConfigurableJettyFactoryImpl f = new ConfigurableJettyFactoryImpl( serverModel);
        f.setSessionHandlerBuilder(sessionHandlerBuilder);
        return new ServerControllerImpl( f );
    }

}
