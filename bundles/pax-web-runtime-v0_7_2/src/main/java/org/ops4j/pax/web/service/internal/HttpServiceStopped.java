/* Copyright 2007 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.service.internal;

import java.util.Dictionary;
import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.ops4j.pax.web.service.SharedWebContainerContext;
import org.ops4j.pax.web.service.WebContainer;

class HttpServiceStopped
    implements StoppableHttpService
{

    private static final Log LOG = LogFactory.getLog( HttpServiceStopped.class );

    HttpServiceStopped()
    {
        LOG.debug( "Changing HttpService state to " + this );
    }

    public void registerServlet( final String alias,
                                 final Servlet servlet,
                                 final Dictionary initParams,
                                 final HttpContext httpContext )
        throws ServletException, NamespaceException
    {
        LOG.warn( "Http service has already been stopped" );
    }

    public void registerResources( final String alias,
                                   final String name,
                                   final HttpContext httpContext )
        throws NamespaceException
    {
        LOG.warn( "Http service has already been stopped" );
    }

    public void unregister( final String alias )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    public HttpContext createDefaultHttpContext()
    {
        LOG.warn( "Http service has already been stopped" );
        return null;
    }

    public void stop()
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * Does nothing.
     *
     * @see WebContainer#registerServlet(Servlet, String[], Dictionary, HttpContext)
     */
    public void registerServlet( final Servlet servlet,
                                 final String[] urlPatterns,
                                 final Dictionary initParams,
                                 final HttpContext httpContext )
        throws ServletException
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * Does nothing.
     *
     * @see WebContainer#registerServlet(javax.servlet.Servlet, String, String[],java.util.Dictionary,org.osgi.service.http.HttpContext)
     */
    public void registerServlet( final Servlet servlet,
                                 final String servletName,
                                 final String[] urlPatterns,
                                 final Dictionary initParams,
                                 final HttpContext httpContext )
        throws ServletException
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * Does nothing.
     *
     * @see WebContainer#unregisterServlet(Servlet)
     */
    public void unregisterServlet( final Servlet servlet )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * Does nothing.
     *
     * @see WebContainer#registerEventListener(java.util.EventListener, HttpContext)
     */
    public void registerEventListener( final EventListener listener,
                                       final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * Does nothing.
     *
     * @see WebContainer#unregisterEventListener(java.util.EventListener)
     */
    public void unregisterEventListener( final EventListener listener )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#registerFilter(Filter, String[], String[], Dictionary, HttpContext)
     */
    public void registerFilter( final Filter filter,
                                final String[] urlPatterns,
                                final String[] servletNames,
                                final Dictionary initParams,
                                final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#unregisterFilter(Filter)
     */
    public void unregisterFilter( final Filter filter )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#setContextParam(Dictionary, HttpContext)
     */
    public void setContextParam( final Dictionary params,
                                 final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * {@inheritDoc}
     */
    public void setSessionTimeout( final Integer minutes,
                                   final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#registerJsps(String[], HttpContext)
     */
    public void registerJsps( final String[] urlPatterns,
                              final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#unregisterJsps(HttpContext)
     */
    public void unregisterJsps( final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#registerErrorPage(String, String, HttpContext)
     */
    public void registerErrorPage( final String error,
                                   final String location,
                                   final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#unregisterErrorPage(String, HttpContext)
     */
    public void unregisterErrorPage( final String error,
                                     final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#registerWelcomeFiles(String[], boolean, HttpContext)
     */
    public void registerWelcomeFiles( final String[] welcomeFiles,
                                      final boolean rediect,
                                      final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

    /**
     * @see WebContainer#unregisterWelcomeFiles(HttpContext)
     */
    public void unregisterWelcomeFiles( final HttpContext httpContext )
    {
        LOG.warn( "Http service has already been stopped" );
    }

	public SharedWebContainerContext getDefaultSharedHttpContext() {
		LOG.warn( "Http service has already been stopped" );
		return null;
	}
    
    

}