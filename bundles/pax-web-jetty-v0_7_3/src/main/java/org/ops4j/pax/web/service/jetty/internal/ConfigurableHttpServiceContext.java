package org.ops4j.pax.web.service.jetty.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ErrorPageErrorHandler;
import org.mortbay.jetty.servlet.SessionHandler;
import org.ops4j.pax.swissbox.core.ContextClassLoaderUtils;
import org.ops4j.pax.web.service.WebContainerContext;
import org.osgi.service.http.HttpContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ConfigurableHttpServiceContext extends Context
{

    private static final Log LOG = LogFactory.getLog( ConfigurableHttpServiceContext.class );

    /**
     * Context attributes.
     */
    private final Map<String, Object> m_attributes;
    private final HttpContext m_httpContext;
    /**
     * Access controller context of the bundle that registred the http context.
     */
    private final AccessControlContext m_accessControllerContext;

    ConfigurableHttpServiceContext( final Server server,
                        final SessionHandler sessionHandler,
                        final Map<String, String> initParams,
                        final Map<String, Object> attributes,
                        final String contextName,
                        final HttpContext httpContext,
                        final AccessControlContext accessControllerContext )
    {
        super( server, "/" + contextName, sessionHandler, null, null, null);

        setInitParams( initParams );
        m_attributes = attributes;
        m_httpContext = httpContext;
        m_accessControllerContext = accessControllerContext;

        _scontext = new SContext();
        setServletHandler( new HttpServiceServletHandler( httpContext ) );
        setErrorHandler( new ErrorPageErrorHandler() );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        super.doStart();
        if( m_attributes != null )
        {
            for( Map.Entry<String, ?> attribute : m_attributes.entrySet() )
            {
                _scontext.setAttribute( attribute.getKey(), attribute.getValue() );
            }
        }
        LOG.debug( "Started servlet context for http context [" + m_httpContext + "]" );
    }

    @Override
    protected void doStop()
        throws Exception
    {
        super.doStop();
        LOG.debug( "Stopped servlet context for http context [" + m_httpContext + "]" );
    }

    @Override
    public void handle( String target, HttpServletRequest request, HttpServletResponse response, int dispatch )
        throws IOException, ServletException
    {
        LOG.debug( "Handling request for [" + target + "] using http context [" + m_httpContext + "]" );
        super.handle( target, request, response, dispatch );
    }

    @Override
    public void setEventListeners( final EventListener[] eventListeners )
    {
        if( _sessionHandler != null )
        {
            _sessionHandler.clearEventListeners();
        }

        super.setEventListeners( eventListeners );
        if( _sessionHandler != null )
        {
            for( int i = 0; eventListeners != null && i < eventListeners.length; i++ )
            {
                EventListener listener = eventListeners[ i ];

                if( ( listener instanceof HttpSessionActivationListener)
                    || ( listener instanceof HttpSessionAttributeListener)
                    || ( listener instanceof HttpSessionBindingListener)
                    || ( listener instanceof HttpSessionListener ) )
                {
                    _sessionHandler.addEventListener( listener );
                }

            }
        }
    }

    /**
     * If the listener is a servlet conetx listener and the context is already started, notify the servlet context
     * listener about the fact that context is started. This has to be done separately as the listener could be added
     * after the context is already started, case when servlet context listeners are not notified anymore.
     *
     * @param listener to be notified.
     */
    @Override
    public void addEventListener( final EventListener listener )
    {
        super.addEventListener( listener );
        if( isStarted() && listener instanceof ServletContextListener)
        {
            try
            {
                ContextClassLoaderUtils.doWithClassLoader(
                    getClassLoader(),
                    new Callable<Void>()
                    {

                        public Void call()
                        {
                            ( (ServletContextListener) listener ).contextInitialized(
                                new ServletContextEvent( _scontext )
                            );
                            return null;
                        }

                    }
                );
            }
            catch( Exception e )
            {
                if( e instanceof RuntimeException )
                {
                    throw (RuntimeException) e;
                }
                LOG.error( "Ignored exception during listener registration", e );
            }
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "httpContext=" ).append( m_httpContext )
            .append( "}" )
            .toString();
    }

    @SuppressWarnings( { "deprecation" } )
    public class SContext extends Context.SContext
    {

        @Override
        public String getRealPath( final String path )
        {
            if( LOG.isDebugEnabled() )
            {
                LOG.debug( "getting real path: [" + path + "]" );
            }

            URL resource = getResource( path );
            if( resource != null )
            {
                String protocol = resource.getProtocol();
                if( protocol.equals( "file" ) )
                {
                    String fileName = resource.getFile();
                    if( fileName != null )
                    {
                        File file = new File( fileName );
                        if( file.exists() )
                        {
                            String realPath = file.getAbsolutePath();
                            if( LOG.isDebugEnabled() )
                            {
                                LOG.debug( "found real path: [" + realPath + "]" );
                            }
                            return realPath;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public URL getResource( final String path )
        {
            if( LOG.isDebugEnabled() )
            {
                LOG.debug( "getting resource: [" + path + "]" );
            }
            URL resource = null;
            try
            {
                resource = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<URL>()
                    {
                        public URL run()
                            throws Exception
                        {
                            return m_httpContext.getResource( path );
                        }
                    },
                    m_accessControllerContext
                );
                if( LOG.isDebugEnabled() )
                {
                    LOG.debug( "found resource: " + resource );
                }
            }
            catch( PrivilegedActionException e )
            {
                LOG.warn( "Unauthorized access: " + e.getMessage() );
            }
            return resource;
        }

        @Override
        public InputStream getResourceAsStream( final String path )
        {
            final URL url = getResource( path );
            if( url != null )
            {
                try
                {
                    return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<InputStream>()
                        {
                            public InputStream run()
                                throws Exception
                            {
                                try
                                {
                                    return url.openStream();
                                }
                                catch( IOException e )
                                {
                                    LOG.warn( "URL canot be accessed: " + e.getMessage() );
                                }
                                return null;
                            }

                        },
                        m_accessControllerContext
                    );
                }
                catch( PrivilegedActionException e )
                {
                    LOG.warn( "Unauthorized access: " + e.getMessage() );
                }

            }
            return null;
        }

        /**
         * Delegate to http context in case that the http context is an {@link org.ops4j.pax.web.service.WebContainerContext}.
         * {@inheritDoc}
         */
        @Override
        public Set getResourcePaths( final String path )
        {
            if( m_httpContext instanceof WebContainerContext)
            {
                if( LOG.isDebugEnabled() )
                {
                    LOG.debug( "getting resource paths for : [" + path + "]" );
                }
                try
                {
                    final Set<String> paths = AccessController.doPrivileged(
                        new PrivilegedExceptionAction<Set<String>>()
                        {
                            public Set<String> run()
                                throws Exception
                            {
                                return ( (WebContainerContext) m_httpContext ).getResourcePaths( path );
                            }
                        },
                        m_accessControllerContext
                    );
                    if( paths == null )
                    {
                        return null;
                    }
                    // Servlet specs mandates that the paths must start with an slash "/"
                    final Set<String> slashedPaths = new HashSet<String>();
                    for( String foundPath : paths )
                    {
                        if( foundPath != null )
                        {
                            if( foundPath.trim().startsWith( "/" ) )
                            {
                                slashedPaths.add( foundPath.trim() );
                            }
                            else
                            {
                                slashedPaths.add( "/" + foundPath.trim() );
                            }
                        }
                    }
                    if( LOG.isDebugEnabled() )
                    {
                        LOG.debug( "found resource paths: " + paths );
                    }
                    return slashedPaths;
                }
                catch( PrivilegedActionException e )
                {
                    LOG.warn( "Unauthorized access: " + e.getMessage() );
                    return null;
                }
            }
            else
            {
                return super.getResourcePaths( path );
            }
        }

        @Override
        public String getMimeType( final String name )
        {
            if( LOG.isDebugEnabled() )
            {
                LOG.debug( "getting mime type for: [" + name + "]" );
            }
            return m_httpContext.getMimeType( name );
        }

    }
}
