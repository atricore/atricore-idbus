package org.ops4j.pax.web.service.jetty.internal;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.*;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.servlet.AbstractSessionIdManager;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.servlet.SessionHandler;
import org.ops4j.pax.web.service.jetty.ehcache.EHCacheSessionManager;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.ops4j.pax.swissbox.core.BundleUtils;
import org.ops4j.pax.web.service.WebContainerConstants;
import org.ops4j.pax.web.service.spi.model.Model;
import org.ops4j.pax.web.service.spi.model.ServerModel;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JettyServerWrapper extends Server {

    private static final Log LOG = LogFactory.getLog( JettyServerWrapper.class );


    private final ServerModel m_serverModel;
    private final Map<HttpContext, Context> m_contexts;

    private SessionHandlerBuilder m_sessionHandlerBuilder;

    private Properties m_kernelProps;

    private Map<String, Object> m_contextAttributes;
    private Integer m_sessionTimeout;
    private String m_sessionCookie;
    private String m_sessionUrl;
    private String m_sessionWorkerName;

    JettyServerWrapper( ServerModel serverModel, SessionHandlerBuilder sessionHandlerBuilder, Properties kernelProps )
    {
        m_serverModel = serverModel;
        m_contexts = new IdentityHashMap<HttpContext, Context>();
        m_sessionHandlerBuilder = sessionHandlerBuilder;
        m_kernelProps = kernelProps;
        this.setSendServerVersion(false);
    }

    public void setSessionHandlerBuilder(SessionHandlerBuilder b) {
        this.m_sessionHandlerBuilder = b;
    }

    public SessionHandlerBuilder getSessionHandlerBuilder() {
        return m_sessionHandlerBuilder;
    }

    @Override
    public void addConnector(Connector connector) {
        // Let's make some configuration before adding this connector


        super.addConnector(connector);
    }

    @Override
    public void addHandler( final Handler handler )
    {
        if( getHandler() == null )
        {
            setHandler( new JettyServerHandlerCollection( m_serverModel ) );
        }
        ( (HandlerCollection) getHandler() ).addHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    public void configureContext( final Map<String, Object> attributes,
                                  final Integer sessionTimeout,
                                  final String sessionCookie,
                                  final String sessionUrl,
                                  final String sessionWorkerName )
    {
        m_contextAttributes = attributes;
        m_sessionTimeout = sessionTimeout;
        m_sessionCookie = sessionCookie;
        m_sessionUrl = sessionUrl;
        m_sessionWorkerName = sessionWorkerName;
    }

    Context getContext( final HttpContext httpContext )
    {
        return m_contexts.get( httpContext );
    }

    Context getOrCreateContext( final Model model )
    {
        Context context = m_contexts.get( model.getContextModel().getHttpContext() );
        if( context == null )
        {
            context = addContext( model );
            m_contexts.put( model.getContextModel().getHttpContext(), context );
        }
        return context;
    }

    void removeContext( final HttpContext httpContext )
    {
        removeHandler( getContext( httpContext ) );
        m_contexts.remove( httpContext );
    }

    private Context addContext( final Model model )
    {

        // Create session handler for context
        SessionHandler sessionHandler = createSessionHandler(model);

        Context context = new ConfigurableHttpServiceContext( this, sessionHandler, model.getContextModel().getContextParams(),
                                                  getContextAttributes(
                                                      BundleUtils.getBundleContext( model.getContextModel().getBundle()
                                                      )
                                                  ),
                model.getContextModel().getContextName(),
                model.getContextModel().getHttpContext(),
                model.getContextModel().getAccessControllerContext(),
                m_kernelProps
        );
        context.setClassLoader( model.getContextModel().getClassLoader() );
        Integer sessionTimeout = model.getContextModel().getSessionTimeout();
        if( sessionTimeout == null )
        {
            sessionTimeout = m_sessionTimeout;
        }
        String sessionCookie = model.getContextModel().getSessionCookie();
        if( sessionCookie == null )
        {
            sessionCookie = m_sessionCookie;
        }
        String sessionUrl = model.getContextModel().getSessionUrl();
        if( sessionUrl == null )
        {
            sessionUrl = m_sessionUrl;
        }
        String workerName = model.getContextModel().getSessionWorkerName();
        if( workerName == null )
        {
            workerName = m_sessionWorkerName;
        }
        configureSessionManager( context, sessionTimeout, sessionCookie, sessionUrl, workerName );

        LOG.debug( "Added servlet context: " + context );

        if( isStarted() )
        {
            try
            {
                LOG.debug( "(Re)starting servlet contexts..." );
                // start the server handler if not already started
                Handler serverHandler = getHandler();
                if( !serverHandler.isStarted() && !serverHandler.isStarting() )
                {
                    serverHandler.start();
                }
                // if the server handler is a handler collection, seems like jetty will not automatically
                // start inner handlers. So, force the start of the created context
                if( !context.isStarted() && !context.isStarting() )
                {
                    context.start();
                }
            }
            catch( Exception ignore )
            {
                LOG.error( "Could not start the servlet context for http context ["
                           + model.getContextModel().getHttpContext() + "]", ignore
                );
            }
        }
        return context;
    }

    /**
     * Returns a list of servlet context attributes out of configured properties and attribues containing the bundle
     * context associated with the bundle that created the model (web element).
     *
     * @param bundleContext bundle context to be set as attribute
     *
     * @return context attributes map
     */
    private Map<String, Object> getContextAttributes( final BundleContext bundleContext )
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        if( m_contextAttributes != null )
        {
            attributes.putAll( m_contextAttributes );
        }
        attributes.put( WebContainerConstants.BUNDLE_CONTEXT_ATTRIBUTE, bundleContext );
        attributes.put( "org.springframework.osgi.web.org.osgi.framework.BundleContext", bundleContext );
        return attributes;
    }

    /**
     * Configures the session time out by extracting the session handlers->sessionManager for the context.
     *
     * @param context    the context for which the session timeout should be configured
     * @param minutes    timeout in minutes
     * @param cookie     Session cookie name. Defaults to JSESSIONID.
     * @param url        session URL parameter name. Defaults to jsessionid. If set to null or  "none" no URL
     *                   rewriting will be done.
     * @param workerName name appended to session id, used to assist session affinity in a load balancer
     */
    private void configureSessionManager( final Context context,
                                          final Integer minutes,
                                          final String cookie,
                                          final String url,
                                          final String workerName )
    {
        LOG.debug( "configureSessionManager for context [" + context + "] using - timeout:" + minutes
                   + ", cookie:" + cookie + ", url:" + url + ", workerName:" + workerName
        );
        final SessionHandler sessionHandler = context.getSessionHandler();
        if( sessionHandler != null )
        {
            final SessionManager sessionManager = sessionHandler.getSessionManager();
            if( sessionManager != null )
            {
                if( minutes != null )
                {
                    sessionManager.setMaxInactiveInterval( minutes * 60 );
                    LOG.debug( "Session timeout set to " + minutes + " minutes for context [" + context + "]" );
                }
                if( cookie != null )
                {
                    sessionManager.setSessionCookie( cookie );
                    LOG.debug( "Session cookie set to " + cookie + " for context [" + context + "]" );
                }
                if( url != null )
                {
                    sessionManager.setSessionURL( url );
                    LOG.debug( "Session URL set to " + url + " for context [" + context + "]" );
                }

                if( workerName != null )
                {
                    SessionIdManager sessionIdManager = sessionManager.getIdManager();
                    if( sessionIdManager == null )
                    {
                        sessionIdManager = new HashSessionIdManager();
                        sessionManager.setIdManager( sessionIdManager );
                        LOG.debug( "Hash Session ID Manager created for contxt [" + context + "]" );
                    }
                    if( sessionIdManager instanceof HashSessionIdManager )
                    {
                        HashSessionIdManager s = (HashSessionIdManager) sessionIdManager;
                        s.setWorkerName( workerName );
                        LOG.debug( "Worker name set to " + workerName + " for context [" + context + "]" );
                    }

                    if (sessionIdManager instanceof AbstractSessionIdManager) {
                        ((AbstractSessionIdManager)sessionIdManager).setWorkerName(workerName);
                        LOG.debug( "Worker name set to " + workerName + " for context [" + context + "]" );
                    }

                }

                if (sessionManager instanceof EHCacheSessionManager) {
                    // TODO : Make this configurable !
                    long saveInterval = 100;
                    ((EHCacheSessionManager)sessionManager).setSaveInterval(saveInterval);
                    LOG.debug( "Session Save Interval set to " + saveInterval + " for context [" + context + "]" );
                }

            }
        }
    }

    protected SessionHandler createSessionHandler(Model model)  {

        if (this.m_sessionHandlerBuilder == null ) {

            BundleContext ctx = BundleUtils.getBundleContext( model.getContextModel().getBundle());
            ServiceReference ref = ctx.getServiceReference(SessionHandlerBuilder.class.getName());
            if (ref == null) {
                LOG.error("Session Handler Builder Service is unavailable. (no service reference)");
                return null;
            }
            try {
                SessionHandlerBuilder svc = (SessionHandlerBuilder) ctx.getService(ref);
                if (svc == null) {
                    LOG.error("Session Handler Builder Service service is unavailable. (no service)");
                    return null;
                }

                if (LOG.isTraceEnabled())
                    LOG.trace("Using Session Handler Builder " + svc);

                return svc.build(this, model);

            } finally {
                ctx.ungetService(ref);
            }
        } else {
            if (LOG.isTraceEnabled())
                LOG.trace("Using Session Handler Builder " + m_sessionHandlerBuilder);

            return m_sessionHandlerBuilder.build(this, model);

        }
    }


}
