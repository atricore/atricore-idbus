package org.ops4j.pax.web.service.jetty.internal;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.model.ServerModel;

import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ConfigurableJettyFactoryImpl implements JettyFactory {

    /**
     * Associated server model.
     */
    private final ServerModel m_serverModel;

    private SessionHandlerBuilder m_sessionHandlerBuilder;

    private Properties m_kernelProps;

    /**
     * Constrcutor.
     *
     * @param serverModel asscociated server model
     */
    ConfigurableJettyFactoryImpl( final ServerModel serverModel )
    {
        NullArgumentException.validateNotNull( serverModel, "Service model" );
        m_serverModel = serverModel;
    }

    public SessionHandlerBuilder getSessionHandlerBuilder() {
        return m_sessionHandlerBuilder;
    }

    public void setSessionHandlerBuilder(SessionHandlerBuilder m_sessionHandlerBuilder) {
        this.m_sessionHandlerBuilder = m_sessionHandlerBuilder;
    }

    public Properties getKernelProps() {
        return m_kernelProps;
    }

    public void setKernelProps(Properties m_kernelProps) {
        this.m_kernelProps = m_kernelProps;
    }

    /**
     * {@inheritDoc}
     */
    public JettyServer createServer()
    {
        JettyServerImpl s = new JettyServerImpl( m_serverModel, m_sessionHandlerBuilder, m_kernelProps );
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( final int port,
                                      final String host,
                                      final boolean useNIO )
    {
        if( useNIO )
        {
            final SelectChannelConnector nioConnector = new NIOSocketConnectorWrapper();
            nioConnector.setHost( host );
            nioConnector.setPort( port );
            nioConnector.setUseDirectBuffers( true );
            return nioConnector;
        }
        else
        {
            final Connector connector = new SocketConnectorWrapper();
            connector.setPort( port );
            connector.setHost( host );
            return connector;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Connector createSecureConnector( final int port,
                                            final String sslKeystore,
                                            final String sslPassword,
                                            final String sslKeyPassword,
                                            final String host,
                                            final String sslKeystoreType,
                                            final boolean isClientAuthNeeded,
                                            final boolean isClientAuthWanted )
    {
        final SslSocketConnector connector = new SslSocketConnector();
        connector.setPort( port );
        connector.setKeystore( sslKeystore );
        connector.setPassword( sslPassword );
        connector.setKeyPassword( sslKeyPassword );
        connector.setHost( host );

        connector.setNeedClientAuth( isClientAuthNeeded );
        connector.setWantClientAuth( isClientAuthWanted );

        if( sslKeystoreType != null )
        {
            connector.setKeystoreType( sslKeystoreType );
        }
        return connector;
    }

}
