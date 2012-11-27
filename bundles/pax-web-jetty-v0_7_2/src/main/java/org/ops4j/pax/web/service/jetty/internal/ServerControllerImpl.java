package org.ops4j.pax.web.service.jetty.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.Servlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.osgi.service.http.HttpContext;
import org.ops4j.pax.web.service.spi.Configuration;
import org.ops4j.pax.web.service.spi.ServerController;
import org.ops4j.pax.web.service.spi.ServerEvent;
import org.ops4j.pax.web.service.spi.ServerListener;
import org.ops4j.pax.web.service.spi.model.ContextModel;
import org.ops4j.pax.web.service.spi.model.ErrorPageModel;
import org.ops4j.pax.web.service.spi.model.EventListenerModel;
import org.ops4j.pax.web.service.spi.model.FilterModel;
import org.ops4j.pax.web.service.spi.model.ServletModel;

class ServerControllerImpl
    implements ServerController
{

    private static final Log LOG = LogFactory.getLog( ServerControllerImpl.class );

    private Configuration m_configuration;
    private State m_state;
    private final JettyFactory m_jettyFactory;
    private JettyServer m_jettyServer;
    private final Set<ServerListener> m_listeners;
    private Connector m_httpConnector;
    private Connector m_httpSecureConnector;

    // JDK Defaults
    private String defaultTrustStore = System.getProperty("java.home") +
            System.getProperty("file.separator") + "lib" +
            System.getProperty("file.separator") + "security" +
            System.getProperty("file.separator") + "cacerts";

    private String defaultTrustPassword = "changeit";
    private String defaultTrustStoreType = "JKS";

    ServerControllerImpl( final JettyFactory jettyFactory )
    {
        m_jettyFactory = jettyFactory;
        m_configuration = null;
        m_state = new Unconfigured();
        m_listeners = new CopyOnWriteArraySet<ServerListener>();
    }

    public synchronized void start()
    {
        LOG.debug( String.format( "Starting server [%s]", this ) );
        m_state.start();
    }

    public synchronized void stop()
    {
        LOG.debug( String.format( "Stopping server [%s]", this ) );
        m_state.stop();
    }

    public synchronized void configure( final Configuration configuration )
    {
        LOG.debug( String.format( "Configuring server [%s] -> [%s] ", this, configuration ) );
        if( configuration == null )
        {
            throw new IllegalArgumentException( "configuration == null" );
        }
        m_configuration = configuration;
        m_state.configure();
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public void addListener( ServerListener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException( "listener == null" );
        }
        m_listeners.add( listener );
    }

    public void addServlet( final ServletModel model )
    {
        m_state.addServlet( model );
    }

    public void removeServlet( final ServletModel model )
    {
        m_state.removeServlet( model );
    }

    public boolean isStarted()
    {
        return m_state instanceof Started;
    }

    public boolean isConfigured()
    {
        return !( m_state instanceof Unconfigured );
    }

    public void addEventListener( final EventListenerModel eventListenerModel )
    {
        m_state.addEventListener( eventListenerModel );
    }

    public void removeEventListener( final EventListenerModel eventListenerModel )
    {
        m_state.removeEventListener( eventListenerModel );
    }

    public void removeContext( HttpContext httpContext )
    {
        m_state.removeContext( httpContext );
    }

    public void addFilter( final FilterModel filterModel )
    {
        m_state.addFilter( filterModel );
    }

    public void removeFilter( final FilterModel filterModel )
    {
        m_state.removeFilter( filterModel );
    }

    public void addErrorPage( final ErrorPageModel model )
    {
        m_state.addErrorPage( model );
    }

    public void removeErrorPage( final ErrorPageModel model )
    {
        m_state.removeErrorPage( model );
    }

    public Integer getHttpPort()
    {
        if( m_httpConnector != null && m_httpConnector.isStarted() )
        {
            return m_httpConnector.getLocalPort();
        }
        return m_configuration.getHttpPort();
    }

    public Integer getHttpSecurePort()
    {
        if( m_httpSecureConnector != null && m_httpSecureConnector.isStarted() )
        {
            return m_httpSecureConnector.getLocalPort();
        }
        return m_configuration.getHttpSecurePort();
    }

    public Servlet createResourceServlet( ContextModel contextModel, String alias, String name )
    {
        return new ResourceServlet(
            contextModel.getHttpContext(),
            contextModel.getContextName(),
            alias,
            name
        );
    }

    void notifyListeners( ServerEvent event )
    {
        for( ServerListener listener : m_listeners )
        {
            listener.stateChanged( event );
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append( ServerControllerImpl.class.getSimpleName() ).append( "{" ).append( "state=" )
            .append( m_state ).append( "}" ).toString();
    }

    private interface State
    {

        void start();

        void stop();

        void configure();

        void addServlet( ServletModel model );

        void removeServlet( ServletModel model );

        void addEventListener( EventListenerModel eventListenerModel );

        void removeEventListener( EventListenerModel eventListenerModel );

        void removeContext( HttpContext httpContext );

        void addFilter( FilterModel filterModel );

        void removeFilter( FilterModel filterModel );

        void addErrorPage( ErrorPageModel model );

        void removeErrorPage( ErrorPageModel model );

    }

    private class Started
        implements State
    {

        public void start()
        {
            throw new IllegalStateException( "server is already started. must be stopped first." );
        }

        public void stop()
        {
            m_jettyServer.stop();
            m_state = new Stopped();
            notifyListeners( ServerEvent.STOPPED );
        }

        public void configure()
        {
            ServerControllerImpl.this.stop();
            ServerControllerImpl.this.start();
        }

        public void addServlet( final ServletModel model )
        {
            m_jettyServer.addServlet( model );
        }

        public void removeServlet( final ServletModel model )
        {
            m_jettyServer.removeServlet( model );
        }

        public void addEventListener( EventListenerModel eventListenerModel )
        {
            m_jettyServer.addEventListener( eventListenerModel );
        }

        public void removeEventListener( EventListenerModel eventListenerModel )
        {
            m_jettyServer.removeEventListener( eventListenerModel );
        }

        public void removeContext( HttpContext httpContext )
        {
            m_jettyServer.removeContext( httpContext );
        }

        public void addFilter( FilterModel filterModel )
        {
            m_jettyServer.addFilter( filterModel );
        }

        public void removeFilter( FilterModel filterModel )
        {
            m_jettyServer.removeFilter( filterModel );
        }

        public void addErrorPage( ErrorPageModel model )
        {
            m_jettyServer.addErrorPage( model );
        }

        public void removeErrorPage( ErrorPageModel model )
        {
            m_jettyServer.removeErrorPage( model );
        }

        @Override
        public String toString()
        {
            return "STARTED";
        }
    }

    private class Stopped
        implements State
    {

        Stopped()
        {
            m_httpConnector = null;
            m_httpSecureConnector = null;
        }

        public void start()
        {
            m_jettyServer = m_jettyFactory.createServer();
            m_httpConnector = null;
            m_httpSecureConnector = null;
            String[] addresses = m_configuration.getListeningAddresses();
            if( addresses == null || addresses.length == 0 )
            {
                addresses = new String[]
                    {
                        null
                    };
            }
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put( "javax.servlet.context.tempdir", m_configuration.getTemporaryDirectory() );
            m_jettyServer.configureContext( attributes, m_configuration.getSessionTimeout(), m_configuration
                .getSessionCookie(), m_configuration.getSessionUrl(), m_configuration.getWorkerName()
            );
            m_jettyServer.start();
            for( String address : addresses )
            {

                java.lang.Integer maxHeaderBuffSize = null;
                try {
                    Method m = m_configuration.getClass().getMethod("getHeaderBufferSize");
                    maxHeaderBuffSize = (Integer) m.invoke(m_configuration);
                    if (maxHeaderBuffSize != null)
                        LOG.info("Setting HTTP MAX HEADER BUFFER SIZE to " + maxHeaderBuffSize + " bytes");
                    else
                        LOG.info("Using Jetty default HTTP MAX HEADER BUFFER SIZE");

                } catch (NoSuchMethodException e) {
                    LOG.warn("Configuration does not support max header buffer size configuraiton");
                } catch (Exception e) {
                    LOG.error("Configuration does not support max header buffer size configuraiton");
                }

                if( m_configuration.isHttpEnabled() )
                {
                    final Connector connector = m_jettyFactory.createConnector( m_configuration.getHttpPort(), address,
                                                                                m_configuration.useNIO()
                    );
                    if( m_httpConnector == null )
                    {
                        m_httpConnector = connector;
                    }

                    // Get some additional properties, but use reflection to avoid a direct dependency with our extension
                    if (maxHeaderBuffSize != null)
                        connector.setHeaderBufferSize(maxHeaderBuffSize);
                    m_jettyServer.addConnector( connector );
                    try
                    {
                        connector.start();
                    }
                    catch( Exception e )
                    {
                        LOG.warn( "Http connector will not be started", e );
                    }
                }
                if( m_configuration.isHttpSecureEnabled() )
                {
                    final String sslPassword = m_configuration.getSslPassword();
                    final String sslKeyPassword = m_configuration.getSslKeyPassword();
                    if( sslPassword != null && sslKeyPassword != null )
                    {
                        final Connector secureConnector = m_jettyFactory.createSecureConnector( m_configuration
                            .getHttpSecurePort(), m_configuration.getSslKeystore(), sslPassword, sslKeyPassword,
                                                                                                address,
                                                                                                m_configuration.getSslKeystoreType(),
                                                                                                m_configuration.isClientAuthNeeded(),
                                                                                                m_configuration.isClientAuthWanted()
                        );
                        if( m_httpSecureConnector == null )
                        {
                            m_httpSecureConnector = secureConnector;
                        }
                        if (maxHeaderBuffSize != null)
                            secureConnector.setHeaderBufferSize(maxHeaderBuffSize);

                        m_jettyServer.addConnector( secureConnector );
                        try
                        {
                            secureConnector.start();
                        }
                        catch( Exception e )
                        {
                            LOG.warn( "Http connector will not be started", e );
                        }
                    }
                    else
                    {
                        LOG.warn( "SSL password and SSL keystore password must be set in order to enable SSL." );
                        LOG.warn( "SSL connector will not be started" );
                    }
                }
            }
            m_state = new Started();
            notifyListeners( ServerEvent.STARTED );
        }

        public void stop()
        {
            // do nothing. already stopped
        }

        public void configure()
        {
            notifyListeners( ServerEvent.CONFIGURED );
        }

        public void addServlet( final ServletModel model )
        {
            // do nothing if server is not started
        }

        public void removeServlet( final ServletModel model )
        {
            // do nothing if server is not started
        }

        public void addEventListener( EventListenerModel eventListenerModel )
        {
            // do nothing if server is not started
        }

        public void removeEventListener( EventListenerModel eventListenerModel )
        {
            // do nothing if server is not started
        }

        public void removeContext( HttpContext httpContext )
        {
            // do nothing if server is not started
        }

        public void addFilter( FilterModel filterModel )
        {
            // do nothing if server is not started
        }

        public void removeFilter( FilterModel filterModel )
        {
            // do nothing if server is not started
        }

        public void addErrorPage( ErrorPageModel model )
        {
            // do nothing if server is not started
        }

        public void removeErrorPage( ErrorPageModel model )
        {
            // do nothing if server is not started
        }

        @Override
        public String toString()
        {
            return "STOPPED";
        }
    }

    private class Unconfigured extends Stopped
    {

        @Override
        public void start()
        {
            throw new IllegalStateException( "server is not yet configured." );
        }

        @Override
        public void configure()
        {
            m_state = new Stopped();
            notifyListeners( ServerEvent.CONFIGURED );
            ServerControllerImpl.this.start();
        }

        @Override
        public String toString()
        {
            return "UNCONFIGURED";
        }
    }

}
