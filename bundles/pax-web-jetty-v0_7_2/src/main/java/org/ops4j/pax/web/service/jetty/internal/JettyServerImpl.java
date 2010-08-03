package org.ops4j.pax.web.service.jetty.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.*;
import org.mortbay.util.LazyList;
import org.mortbay.xml.XmlConfiguration;
import org.ops4j.pax.swissbox.core.ContextClassLoaderUtils;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.model.*;
import org.osgi.service.http.HttpContext;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JettyServerImpl implements JettyServer {

private static final Log LOG = LogFactory.getLog( JettyServerImpl.class );

    private final JettyServerWrapper m_server;

    JettyServerImpl( final ServerModel serverModel, final SessionHandlerBuilder sessionHandlerBuilder)
    {
        m_server = new JettyServerWrapper( serverModel, sessionHandlerBuilder );
    }

    public void start()
    {
        LOG.debug( "Starting " + this );
        try
        {
            URL resource = getClass().getResource( "/jetty.xml" );
            if( resource != null )
            {
                LOG.debug( "Configure using resource " + resource );
                XmlConfiguration configuration = new XmlConfiguration( resource );
                configuration.configure( m_server );
            }
            m_server.start();
        }
        catch( Exception e )
        {
            LOG.error( e );
        }
    }

    public void stop()
    {
        LOG.debug( "Stopping " + this );
        try
        {
            m_server.stop();
        }
        catch( Exception e )
        {
            LOG.error( e );
        }
    }

    /**
     * @see JettyServer#addConnector(org.mortbay.jetty.Connector)
     */
    public void addConnector( final Connector connector )
    {
        LOG.info(
            String.format(
                "Pax Web available at [%s]:[%s]",
                connector.getHost() == null ? "0.0.0.0" : connector.getHost(),
                connector.getPort()
            )
        );
        m_server.addConnector( connector );
    }

    /**
     * {@inheritDoc}
     */
    public void configureContext( final Map<String, Object> attributes,
                                  final Integer sessionTimeout,
                                  final String sessionCookie,
                                  final String sessionUrl,
                                  final String workerName )
    {
        m_server.configureContext( attributes, sessionTimeout, sessionCookie, sessionUrl, workerName );
    }

    public void addServlet( final ServletModel model )
    {
        LOG.debug( "Adding servlet [" + model + "]" );
        final ServletMapping mapping = new ServletMapping();
        mapping.setServletName( model.getName() );
        mapping.setPathSpecs( model.getUrlPatterns() );
        final Context context = m_server.getOrCreateContext( model );
        final ServletHandler servletHandler = context.getServletHandler();
        if( servletHandler == null )
        {
            throw new IllegalStateException( "Internal error: Cannot find the servlet holder" );
        }
        final ServletHolder holder = new ServletHolder( model.getServlet() );
        holder.setName( model.getName() );
        if( model.getInitParams() != null )
        {
            holder.setInitParameters( model.getInitParams() );
        }
        // Jetty does not set the context class loader on adding the filters so we do that instead
        try
        {
            ContextClassLoaderUtils.doWithClassLoader( context.getClassLoader(), new Callable<Void>()
            {

                public Void call()
                {
                    servletHandler.addServlet( holder );
                    servletHandler.addServletMapping( mapping );
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
            LOG.error( "Ignored exception during servlet registration", e );
        }
    }

    public void removeServlet( final ServletModel model )
    {
        LOG.debug( "Removing servlet [" + model + "]" );
        // jetty does not provide a method fro removing a servlet so we have to do it by our own
        // the facts bellow are found by analyzing ServletHolder implementation
        boolean removed = false;
        final Context context = m_server.getContext( model.getContextModel().getHttpContext() );
        final ServletHandler servletHandler = context.getServletHandler();
        final ServletHolder[] holders = servletHandler.getServlets();
        if( holders != null )
        {
            final ServletHolder holder = servletHandler.getServlet( model.getName() );
            if( holder != null )
            {
                servletHandler.setServlets( (ServletHolder[]) LazyList.removeFromArray( holders, holder ) );
                // we have to find the servlet mapping by hand :( as there is no method provided by jetty
                // and the remove is done based on equals, that is not implemented by servletmapping
                // so it is == based.
                ServletMapping[] mappings = servletHandler.getServletMappings();
                if( mappings != null )
                {
                    ServletMapping mapping = null;
                    for( ServletMapping item : mappings )
                    {
                        if( holder.getName().equals( item.getServletName() ) )
                        {
                            mapping = item;
                            break;
                        }
                    }
                    if( mapping != null )
                    {
                        servletHandler.setServletMappings( (ServletMapping[]) LazyList.removeFromArray( mappings,
                                                                                                        mapping
                        )
                        );
                        removed = true;
                    }
                }
                // if servlet is still started stop the servlet holder (=servlet.destroy()) as Jetty will not do that
                if( holder.isStarted() )
                {
                    try
                    {
                        ContextClassLoaderUtils.doWithClassLoader( context.getClassLoader(), new Callable<Void>()
                        {

                            public Void call()
                                throws Exception
                            {
                                holder.stop();
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
                        LOG.warn( "Exception during unregistering of servlet [" + model + "]" );
                    }
                }
            }
        }
        if( !removed )
        {
            throw new IllegalStateException( model + " was not found" );
        }
    }

    public void addEventListener( final EventListenerModel model )
    {
        m_server.getOrCreateContext( model ).addEventListener( model.getEventListener() );
    }

    public void removeEventListener( final EventListenerModel model )
    {
        final Context context = m_server.getContext( model.getContextModel().getHttpContext() );
        final List<EventListener> listeners =
            new ArrayList<EventListener>( Arrays.asList( context.getEventListeners() ) );
        listeners.remove( model.getEventListener() );
        context.setEventListeners( listeners.toArray( new EventListener[listeners.size()] ) );
    }

    public void removeContext( final HttpContext httpContext )
    {
        m_server.removeContext( httpContext );
    }

    public void addFilter( final FilterModel model )
    {
        LOG.debug( "Adding filter model [" + model + "]" );
        final FilterMapping mapping = new FilterMapping();
        mapping.setFilterName( model.getName() );
        if( model.getUrlPatterns() != null && model.getUrlPatterns().length > 0 )
        {
            mapping.setPathSpecs( model.getUrlPatterns() );
        }
        if( model.getServletNames() != null && model.getServletNames().length > 0 )
        {
            mapping.setServletNames( model.getServletNames() );
        }
        // set-up dispatcher
        int dispatcher = Handler.DEFAULT;
        for( String d : model.getDispatcher() )
        {
            dispatcher |= Dispatcher.type( d );
        }
        mapping.setDispatches( dispatcher );

        final Context context = m_server.getOrCreateContext( model );
        final ServletHandler servletHandler = context.getServletHandler();
        if( servletHandler == null )
        {
            throw new IllegalStateException( "Internal error: Cannot find the servlet holder" );
        }
        final FilterHolder holder = new FilterHolder( model.getFilter() );
        holder.setName( model.getName() );
        if( model.getInitParams() != null )
        {
            holder.setInitParameters( model.getInitParams() );
        }

        // Jetty does not set the context class loader on adding the filters so we do that instead
        try
        {
            ContextClassLoaderUtils.doWithClassLoader( context.getClassLoader(), new Callable<Void>()
            {

                public Void call()
                {
                    servletHandler.addFilter( holder, mapping );
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
            LOG.error( "Ignored exception during filter registration", e );
        }
    }

    public void removeFilter( FilterModel model )
    {
        LOG.debug( "Removing filter model [" + model + "]" );
        final Context context = m_server.getContext( model.getContextModel().getHttpContext() );
        final ServletHandler servletHandler = context.getServletHandler();
        // first remove filter mappings for the removed filter
        final FilterMapping[] filterMappings = servletHandler.getFilterMappings();
        FilterMapping[] newFilterMappings = null;
        for( FilterMapping filterMapping : filterMappings )
        {
            if( filterMapping.getFilterName().equals( model.getName() ) )
            {
                if( newFilterMappings == null )
                {
                    newFilterMappings = filterMappings;
                }
                newFilterMappings = (FilterMapping[]) LazyList.removeFromArray( newFilterMappings, filterMapping );
            }
        }
        servletHandler.setFilterMappings( newFilterMappings );
        // then remove the filter
        final FilterHolder filterHolder = servletHandler.getFilter( model.getName() );
        final FilterHolder[] filterHolders = servletHandler.getFilters();
        final FilterHolder[] newFilterHolders =
            (FilterHolder[]) LazyList.removeFromArray( filterHolders, filterHolder );
        servletHandler.setFilters( newFilterHolders );
        // if filter is still started stop the filter (=filter.destroy()) as Jetty will not do that
        if( filterHolder.isStarted() )
        {
            try
            {
                ContextClassLoaderUtils.doWithClassLoader( context.getClassLoader(), new Callable<Void>()
                {

                    public Void call()
                        throws Exception
                    {
                        filterHolder.stop();
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
                LOG.warn( "Exception during unregistering of filter [" + filterHolder.getFilter() + "]" );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public void addErrorPage( final ErrorPageModel model )
    {
        final Context context = m_server.getOrCreateContext( model );
        final ErrorPageErrorHandler errorPageHandler = (ErrorPageErrorHandler) context.getErrorHandler();
        if( errorPageHandler == null )
        {
            throw new IllegalStateException( "Internal error: Cannot find the error handler. Please report." );
        }
        Map<String, String> errorPages = errorPageHandler.getErrorPages();
        if( errorPages == null )
        {
            errorPages = new HashMap<String, String>();
        }
        errorPages.put( model.getError(), model.getLocation() );
        errorPageHandler.setErrorPages( errorPages );
    }

    @SuppressWarnings( "unchecked" )
    public void removeErrorPage( final ErrorPageModel model )
    {
        final Context context = m_server.getOrCreateContext( model );
        final ErrorPageErrorHandler errorPageHandler = (ErrorPageErrorHandler) context.getErrorHandler();
        if( errorPageHandler == null )
        {
            throw new IllegalStateException( "Internal error: Cannot find the error handler. Please report." );
        }
        final Map<String, String> errorPages = errorPageHandler.getErrorPages();
        if( errorPages != null )
        {
            errorPages.remove( model.getError() );
            if( errorPages.size() == 0 )
            {
                errorPageHandler.setErrorPages( null );
            }
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append( JettyServerImpl.class.getSimpleName() ).append( "{" ).append( "}" )
            .toString();
    }

}
