package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.component.jetty.CamelContinuationServlet;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.importer.ServiceProxyDestroyedException;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * This servlet can follow redirects internally. If a redirect  location targets the same IDBus server,
 * it process it internally, without sending it to the browser.
 *
 * An improved version of the servlet could actually act as a proxy/client for external redirects or even to process HTML
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiIDBusServlet2 extends CamelContinuationServlet {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet2.class);

    private IdentityMediationUnitRegistry registry;

    private boolean followRedirects;

    private String localTargetBaseUrl;

    private ConfigurationContext kernelConfig;

    private InternalProcessingPolicy internalProcessingPolicy;

    public OsgiIDBusServlet2() {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        long started = 0;
        String thread = Thread.currentThread().getName();
        String pathInfo = req.getPathInfo();

        try {

            if (logger.isTraceEnabled()) {
                String parentThread = req.getHeader("IDBUS-PROXIED-REQUEST");
                String proxied = parentThread != null ? "PROXIED" : "BROWSER";
                started = System.currentTimeMillis();
                logger.trace("IDBUS-PERF " + proxied + " ["+thread+"] "+ (parentThread != null ? "{"+parentThread+"}" : "")+ " " + pathInfo + " START");
            }

            if (kernelConfig == null) {

                // Lazy load kernel config

                kernelConfig = lookupKernelConfig();

                if (kernelConfig == null) {
                    logger.error("No Kernel Configuration Context found!");
                    throw new ServletException("No Kernel Configuration Context found!");
                }

                followRedirects = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.followRedirects"));
                localTargetBaseUrl = kernelConfig.getProperty("binding.http.localTargetBaseUrl");

                logger.info("Following Redirects internally : " + followRedirects);
            }

            if (internalProcessingPolicy == null) {
                org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                    (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                            WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

                internalProcessingPolicy = (InternalProcessingPolicy) wac.getBean("internal-processing-policy");
            }

            // Do we actually service this request or we proxy it ?
            if (!followRedirects || !internalProcessingPolicy.match(req)) {
                doService(req, res);
            } else {
                doProxyInternally(req, res);
            }
        } finally {
            if (logger.isTraceEnabled()) {

                String parentThread = req.getHeader("IDBUS-PROXIED-REQUEST");
                String proxied = parentThread != null ? "PROXIED" : "BROWSER";

                long ended = System.currentTimeMillis();
                logger.trace("IDBUS-PERF " + proxied + " ["+thread+"] "+ (parentThread != null ? "{"+parentThread+"}" : "")+ " " + pathInfo + " END: " + (ended - started) + " ms");
            }

        }


    }

    protected void doProxyInternally(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpRequestBase proxyReq = buildProxyRequest(req);
        URI reqUri = proxyReq.getURI();
        String cookieDomain = reqUri.getHost();

        // Create HTTP Client
        DefaultHttpClient httpClient = new DefaultHttpClient();

        // Create an HTTP Context to publish resources for our client components
        HttpContext httpContext = new BasicHttpContext();

        // Publish the original request, it will be removed from the context later, on the first internal redirect.
        httpContext.setAttribute("org.atricorel.idbus.kernel.main.binding.http.HttpServletRequest", req);

        // Tailor client, we need to send the cookies received from the browser on all requests.
        // Replace default request cookie handling interceptor
        int intIdx = 0;
        for (int i = 0 ; i < httpClient.getRequestInterceptorCount(); i++) {
            if (httpClient.getRequestInterceptor(i) instanceof RequestAddCookies) {
                intIdx = i;
                break;
            }
        }

        IDBusRequestAddCookies interceptor = new IDBusRequestAddCookies(cookieDomain);
        httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
        httpClient.addRequestInterceptor(interceptor, intIdx);

        // Configure client, disable following redirects, we want to be in control of redirecting
        httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);


        if (logger.isTraceEnabled())
            logger.trace("Staring to follow redirects for " + req.getPathInfo());

        HttpResponse proxyRes = null;

        // Store received headers and send them back to the browser
        List<Header> storedHeaders = new ArrayList<Header>(40);
        boolean followTargetUrl = true;
        byte[] buff = new byte[1024];

        while(followTargetUrl) {

            if (logger.isTraceEnabled())
                logger.trace("Sending internal request " + proxyReq);

            // Actually execute the request
            proxyRes = httpClient.execute(proxyReq, httpContext);
            String targetUrl = null;

            // Store some of the received HTTP headers, Set-Cookie are the most important ones
            Header[] headers = proxyRes.getAllHeaders();

            for (Header header : headers) {
                // Ignored headers
                if (header.getName().equals("Server")) continue;
                if (header.getName().equals("Transfer-Encoding")) continue;
                if (header.getName().equals("Location")) continue;
                if (header.getName().equals("Expires")) continue;
                if (header.getName().equals("Content-Length")) continue;
                if (header.getName().equals("Content-Type")) continue;

                // The sender of the response has explicitly ask no to follow this redirect.
                if (header.getName().equals("FollowRedirect")) {
                    // Set 'followTargetUrl' to false
                    followTargetUrl = false;
                    continue;
                }

                storedHeaders.add(header);

            }

            if (logger.isTraceEnabled())
                logger.trace("HTTP/STATUS:" + proxyRes.getStatusLine().getStatusCode() + "["+proxyReq+"]");

            // Based on the status, we may need to follow redirects
            // We could convert this into HTTP Client components (req/res interceptors, etc)
            switch (proxyRes.getStatusLine().getStatusCode()) {
                case 200:
                    followTargetUrl = false;
                    break;
                case 302:
                    // See if we have to proxy the response
                    Header location = proxyRes.getFirstHeader("Location");
                    targetUrl = location.getValue();

                    // Just in case the value was already set to false
                    if (!followTargetUrl || !internalProcessingPolicy.match(req, targetUrl)) {

                        // This is outside our scope, send the response to the browser ...
                        if (logger.isTraceEnabled())
                            logger.trace("Do not follow HTTP 302 to ["+location.getValue()+"]");

                        Collections.addAll(storedHeaders, proxyRes.getHeaders("Location"));
                        followTargetUrl = false;
                    } else {

                        if (logger.isTraceEnabled())
                            logger.trace("Do follow HTTP 302 to ["+location.getValue()+"]");

                        followTargetUrl = true;

                    }

                    break;
                case 401:
                    followTargetUrl = false;
                    break;
                case 404:
                    followTargetUrl = false;
                    break;
                case 500:
                    followTargetUrl = false;
                    break;
                default:
                    followTargetUrl = false;
                    break;
            }

            // Get hold of the response entity
            HttpEntity entity = proxyRes.getEntity();

            // If the response does not enclose an entity, there is no need
            // to bother about connection release

            if (entity != null) {
                InputStream instream = entity.getContent();
                try {

                    if (!followTargetUrl) {
                        // If we're not following the target URL, send all to the browser
                        // Last received headers
                        for (Header header : headers) {
                            if (header.getName().equals("Content-Type"))
                                res.setHeader(header.getName(), header.getValue());
                            if (header.getName().equals("Content-Length"))
                                res.setHeader(header.getName(), header.getValue());
                        }

                        res.setStatus(proxyRes.getStatusLine().getStatusCode());
                        for (Header header : storedHeaders) {
                            if (header.getName().startsWith("Set-Cookie"))
                                res.addHeader(header.getName(), header.getValue());
                            else
                                res.setHeader(header.getName(), header.getValue());
                        }

                        // Send content to browser
                        IOUtils.copy(instream, res.getOutputStream());
                        res.getOutputStream().flush();

                    } else {

                        // Just ignore the content ...
                        // should we do something with this ?!
                        int r = instream.read(buff);
                        int total = r;
                        while (r > 0) {
                            r = instream.read(buff);
                            total += r;
                        }

                        if (total > 0)
                            logger.warn("Ignoring response content size : " + total);


                    }

                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    proxyReq.abort();
                    throw ex;
                } finally {
                    // Closing the input stream will trigger connection release
                    try { instream.close(); } catch (Exception ignore) {}
                }

            } else {

                if (!followTargetUrl) {
                    // If we're not following the target URL, send all to the browser
                    res.setStatus(proxyRes.getStatusLine().getStatusCode());

                    for (Header header : headers) {
                        if (header.getName().equals("Content-Type"))
                            res.setHeader(header.getName(), header.getValue());
                        if (header.getName().equals("Content-Length"))
                            res.setHeader(header.getName(), header.getValue());

                    }

                    for (Header header : storedHeaders) {
                        if (header.getName().startsWith("Set-Cookie"))
                            res.addHeader(header.getName(), header.getValue());
                        else
                            res.setHeader(header.getName(), header.getValue());
                    }

                }

            }

            if (followTargetUrl) {
                proxyReq = buildProxyRequest(targetUrl);
                // Clear context, we many need a new instance
                httpContext = null;
            }

        }

        if (logger.isTraceEnabled())
            logger.trace("Ended following redirects for " + req.getPathInfo());

    }

    protected HttpRequestBase buildProxyRequest(String targetUrl) throws MalformedURLException {

        if (localTargetBaseUrl != null) {
            URL url = new URL(targetUrl);

            StringBuilder newUrl = new StringBuilder(localTargetBaseUrl);
            newUrl.append(url.getPath());

            if (url.getQuery() != null) {
                newUrl.append("?");
                newUrl.append(url.getQuery());
            }

            targetUrl = newUrl.toString();
        }

        // Cookies are automatically managed by the client :)
        // Mark request as PROXIED, so that we don't get into an infinite loop
        HttpRequestBase proxyReq = new HttpGet(targetUrl);
        proxyReq.addHeader("IDBUS-PROXIED-REQUEST", Thread.currentThread().getName());

        return proxyReq;
    }

    protected HttpRequestBase buildProxyRequest(HttpServletRequest req) throws ServletException {

        HttpRequestBase proxyReq = null;

        StringBuilder targetUrl = new StringBuilder();

        // Adapt location to local location
        if (localTargetBaseUrl != null) {
            targetUrl.append(localTargetBaseUrl);
            targetUrl.append(req.getContextPath().equals("") ? "/" : req.getContextPath());
            targetUrl.append(req.getPathInfo());
            if (req.getMethod().equalsIgnoreCase("GET")) {
                if (req.getQueryString() != null)
                    targetUrl.append("?").append(req.getQueryString());

            } else {
                throw new ServletException(req.getMethod() + " HTTP method cannot be proxied!");
            }

        } else {
            targetUrl.append(req.getRequestURL());
        }

        proxyReq = new HttpGet(targetUrl.toString());
        // Mark request as PROXIED, so that we don't get into an infinite loop
        proxyReq.addHeader("IDBUS-PROXIED-REQUEST", Thread.currentThread().getName());
        // Add incoming headers, like cookies!
        Enumeration<String> hNames = req.getHeaderNames();
        while (hNames.hasMoreElements()) {
            String hName = hNames.nextElement();
            proxyReq.addHeader(hName, req.getHeader(hName));

            // Received cookies will be added to HTTP Client cookie store by our own cookie interceptor
        }

        return proxyReq;

    }

    /**
     * Actually process this request
     */
    protected void doService(HttpServletRequest req, HttpServletResponse r)
            throws ServletException, IOException {

        // FIX For a bug in CXF!

        HttpServletResponse res = new WHttpServletResponse(r);

        // Lazy  identity mediation registry
        if (registry == null)
            registry = lookupIdentityMediationUnitRegistry();

        if (registry == null) {
            logger.error("No identity mediation registry found ");
            throw new ServletException("No identity mediation registry found!");
        }

        // Work around for 'destroyed' Spring OSGi service references :
        try {
            if (!registry.isValid())
                registry = lookupIdentityMediationUnitRegistry();
        } catch (ServiceProxyDestroyedException e) {
            registry = lookupIdentityMediationUnitRegistry();
        }


        HttpConsumer consumer = resolveConsumer(req);
        if (consumer == null) {
            log("No HTTP Consumer found for " + req.getRequestURL().toString() + " Sending 404 (Not Found) HTTP Status.");
            logger.warn("Make sure your appliance is STARTED");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            // TODO : Send 404 HTML content
            return;
        }

        if (logger.isTraceEnabled()) {
            String cookies = req.getHeader("Cookie");
            logger.trace("Received COOKIES ["+cookies+"]");
        }

        IDBusHttpEndpoint endpoint = (IDBusHttpEndpoint) consumer.getEndpoint();
        /** Synchrony version : TODO: use this instead of the old one with continuations! */
        try {
            final HttpExchange exchange = new HttpExchange(endpoint, req, res);
            consumer.getProcessor().process(exchange);
            consumer.getBinding().writeResponse(exchange, res);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        /* Asynchronous version with continuations (TODO : deprecated!!!!)
        boolean wroteResponse = false;

        final Continuation continuation = ContinuationSupport.getContinuation(req, null);
        if (continuation.isNew()) {

            if (logger.isTraceEnabled())
                logger.trace("Continuation is NEW");

            // Have camel process the HTTP exchange.
            final HttpExchange exchange = new HttpExchange(endpoint, req, res);

            boolean sync = consumer.getAsyncProcessor().process(exchange, new AsyncCallback() {
                public void done(boolean sync) {
                    if (sync) {
                        return;
                    }
                    continuation.setObject(exchange);
                    continuation.resume();
                }
            });

            if (!sync) {
                // Wait for the exchange to get processed.
                // This might block until it completes or it might return via an exception and
                // then this method is re-invoked once the the exchange has finished processing
                continuation.suspend(0);
            }

            // HC: The getBinding() is interesting because it illustrates the
            // impedance miss-match between HTTP's stream oriented protocol, and
            // Camels more message oriented protocol exchanges.

            // now lets output to the response
            wroteResponse  = true;
            consumer.getBinding().writeResponse(exchange, res);
            return;
        } else {
            if (logger.isTraceEnabled())
                logger.trace("Continuation is NOT NEW");

        }

        if (continuation.isResumed()) {
            HttpExchange exchange = (HttpExchange) continuation.getObject();
            // now lets output to the response
            wroteResponse = true;
            consumer.getBinding().writeResponse(exchange, res);

            if (logger.isTraceEnabled())
                logger.trace("Continuation is RESUMED");

        } else {
            if (logger.isTraceEnabled())
                logger.trace("Continuation is NOT RESUMED");

        }

        if (!wroteResponse) {

            logger.error("Not Writting response !!!...");
        }

        */

    }

    protected HttpConsumer resolveConsumer(HttpServletRequest req) {
        HttpConsumer targetConsumer = null;

        String consumerKey = "idbus:" + req.getContextPath() + req.getPathInfo();

        Collection<IdentityMediationUnit> identityMediationUnits = registry.getIdentityMediationUnits();

        if (logger.isDebugEnabled())
            logger.debug("Scanning in " + identityMediationUnits.size() + " Identity Mediation Units " +
                    "for IDBus Http Camel Consumer [" + consumerKey + "]");

        for (IdentityMediationUnit identityMediationUnit : identityMediationUnits) {

            if (logger.isTraceEnabled())
                logger.trace("Scanning Identity Mediation Unit [" + identityMediationUnit.getName() + "] " +
                        "for IDBus Http Camel Consumer [" + consumerKey + "]");

            IdentityMediationUnitContainer imUnitContainer = identityMediationUnit.getContainer();
            if (imUnitContainer == null) {
                logger.error("Identity Mediation Registry [" + registry + "] " +
                        "has no Identity Mediation Engine. Ignoring!");
                return null;
            }

            if (!(imUnitContainer instanceof CamelIdentityMediationUnitContainer)) {
                logger.error("Identity Mediation Registry [" + registry + "] " +
                        "references unsupported Identity Mediation Engine " +
                        "type [" + imUnitContainer.getClass().getName() + "]. Ignoring!");
                return null;

            }

            CamelContext cctx = ((CamelIdentityMediationUnitContainer) imUnitContainer).getContext();
            Registry reg = cctx.getRegistry();
            JndiRegistry jReg = (JndiRegistry) reg;

            Object consumer = jReg.lookup(consumerKey);
            if (consumer != null && consumer instanceof HttpConsumer) {
                targetConsumer = (HttpConsumer) jReg.lookup(consumerKey);
                if (targetConsumer != null) {
                    if (logger.isTraceEnabled())
                        logger.trace("HTTP Consumer for consumer key [" + consumerKey + "] found");
                    break;
                }
            }

        }

        if (targetConsumer == null) {
            if (logger.isDebugEnabled())
                logger.debug("No HTTP Consumer bound to JNDI Name " + consumerKey);
        }

        return targetConsumer;
    }

    protected ConfigurationContext lookupKernelConfig() throws ServletException {

        org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                        WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        if (wac == null) {
            logger.error("Spring application context not found in servlet context");
            throw new ServletException("Spring application context not found in servlet context");
        }

        BundleContext bc = wac.getBundleContext();

        for (Bundle b : bc.getBundles()) {
            if (b.getRegisteredServices() != null) {

                if (logger.isTraceEnabled())
                    logger.trace("(" + b.getBundleId() + ") " + b.getSymbolicName() + " serviceReferences:" + b.getRegisteredServices().length);

                for (ServiceReference r : b.getRegisteredServices()) {

                    String props = "";
                    for (String key : r.getPropertyKeys()) {
                        props += "\n\t\t" + key + "=" + r.getProperty(key);

                        if (r.getProperty(key) instanceof String[]) {
                            String[] v = (String[]) r.getProperty(key);
                            props += "[";
                            String prefix = "";
                            for (String aV : v) {
                                props += prefix + aV;
                                prefix = ",";
                            }
                            props += "]";
                        }
                    }

                    if (logger.isTraceEnabled())
                        logger.trace("ServiceReference:<<" + r + ">> [" + r.getProperty("service.id") + "]" + props);
                }

            } else {
                if (logger.isTraceEnabled())
                    logger.trace("(" + b.getBundleId() + ") " + b.getSymbolicName() + "services:<null>");
            }
        }


        Map<String, ConfigurationContext> kernelCfgsMap = wac.getBeansOfType(ConfigurationContext.class);
        if (kernelCfgsMap == null) {
            logger.warn("No kernel configuration context configured");
            return null;
        }

        if (kernelCfgsMap.size() > 1) {
            logger.warn("More than one kernel configuration context configured");
            return null;
        }

        ConfigurationContext kCfg = kernelCfgsMap.values().iterator().next();
        if (logger.isDebugEnabled())
            logger.debug("Found kernel configuration context " + kCfg);
        return kCfg;

    }

    protected IdentityMediationUnitRegistry lookupIdentityMediationUnitRegistry() throws ServletException {

        org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                        WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        if (wac == null) {
            logger.error("Spring application context not found in servlet context");
            throw new ServletException("Spring application context not found in servlet context");
        }

        /* TODO : Used only for debugging
        BundleContext bc = wac.getBundleContext();

        for (Bundle b : bc.getBundles()) {
            if (b.getRegisteredServices() != null) {

                if (logger.isTraceEnabled())
                    logger.trace("(" + b.getBundleId() + ") " + b.getSymbolicName() + " serviceReferences:" + b.getRegisteredServices().length);

                for (ServiceReference r : b.getRegisteredServices()) {

                    String props = "";
                    for (String key : r.getPropertyKeys()) {
                        props += "\n\t\t" + key + "=" + r.getProperty(key);

                        if (r.getProperty(key) instanceof String[]) {
                            String[] v = (String[]) r.getProperty(key);
                            props += "[";
                            String prefix = "";
                            for (String aV : v) {
                                props += prefix + aV;
                                prefix = ",";
                            }
                            props += "]";
                        }
                    }

                    if (logger.isTraceEnabled())
                        logger.trace("ServiceReference:<<" + r + ">> [" + r.getProperty("service.id") + "]" + props);
                }

            } else {
                if (logger.isTraceEnabled())
                    logger.trace("(" + b.getBundleId() + ") " + b.getSymbolicName() + "services:<null>");
            }
        }
        */

        Map<String, IdentityMediationUnitRegistry> imuRegistryMap = wac.getBeansOfType(IdentityMediationUnitRegistry.class);
        if (imuRegistryMap == null) {
            logger.warn("No identity mediation unit registry configured");
            return null;
        }

        if (imuRegistryMap.size() > 1) {
            logger.warn("More than one identity mediation unit registry configured");
            return null;
        }

        IdentityMediationUnitRegistry r = imuRegistryMap.values().iterator().next();
        if (logger.isDebugEnabled())
            logger.debug("Found Identity Mediation Unit Registry " + r);
        return r;

    }

    protected class WHttpServletResponse extends HttpServletResponseWrapper {

        public WHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type"))
                super.addHeader("Content-Type", value);

            super.addHeader(name, value);
        }

        @Override
        public void setHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type")) {
                super.setHeader("Content-Type", value);
            }
            super.setHeader(name, value);
        }
    }
}