package org.ops4j.pax.web.service.jetty.internal;

import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.ServerController;
import org.ops4j.pax.web.service.spi.ServerControllerFactory;
import org.ops4j.pax.web.service.spi.model.ServerModel;

import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ConfigurableServerControllerFactoryImpl implements ServerControllerFactory {

    private SessionHandlerBuilder sessionHandlerBuilder;

    private Properties kernelProps;

    public SessionHandlerBuilder getSessionHandlerBuilder() {
        return sessionHandlerBuilder;
    }

    public void setSessionHandlerBuilder(SessionHandlerBuilder sessionHandlerBuilder) {
        this.sessionHandlerBuilder = sessionHandlerBuilder;
    }

    public Properties getKernelProps() {
        return kernelProps;
    }

    public void setKernelProps(Properties kernelProps) {
        this.kernelProps = kernelProps;
    }

    public ServerController createServerController( ServerModel serverModel )
    {
        ConfigurableJettyFactoryImpl f = new ConfigurableJettyFactoryImpl( serverModel);
        f.setSessionHandlerBuilder(sessionHandlerBuilder);
        f.setKernelProps(kernelProps);
        return new ServerControllerImpl( f );
    }

}
