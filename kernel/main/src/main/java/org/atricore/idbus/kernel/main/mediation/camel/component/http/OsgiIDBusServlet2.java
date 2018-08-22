package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.jetty.CamelContinuationServlet;
import org.apache.camel.http.common.HttpConsumer;
import org.apache.camel.impl.DefaultExchange;
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
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.importer.ServiceProxyDestroyedException;
import org.springframework.util.StopWatch;
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
public class OsgiIDBusServlet2 extends CamelContinuationServlet implements IDBusHttpConstants {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet2.class);

    private static final String ATRICORE_WEB_PROCESSING_TIME_MS_METRIC_NAME = "AtricoreWebProcessingTimeMs";

    private static final String ATRICORE_WEB_BROWSER_PROCESSING_TIME_MS_METRIC_NAME = "AtricoreWebBrowserProcessingTimeMs";

    private IdentityMediationUnitRegistry registry;

    private boolean followRedirects;

    private boolean secureCookies = false;

    private boolean reuseHttpClient = false;

    private String localTargetBaseUrl;

    private ConfigurationContext kernelConfig;

    private InternalProcessingPolicy internalProcessingPolicy;

    private HttpClient httpClient;

    private int connectionTimeoutMillis = 5000; // Five seconds
    private int socketTimeoutMillis = 300000; // Five minutes

    public OsgiIDBusServlet2() {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        if (kernelConfig == null) {

            // Lazy load kernel config

            kernelConfig = lookupKernelConfig();

            if (kernelConfig == null) {
                logger.error("No Kernel Configuration Context found!");
                throw new ServletException("No Kernel Configuration Context found!");
            }

            secureCookies = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.secureCookies", "false"));
            followRedirects = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.followRedirects", "true"));
            reuseHttpClient = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.reuseHttpClient", "false"));

            socketTimeoutMillis = Integer.parseInt(kernelConfig.getProperty("binding.http.socketTimeoutMillis", "300000"));
            connectionTimeoutMillis = Integer.parseInt(kernelConfig.getProperty("binding.http.connectionTimeoutMillis", "5000"));

            localTargetBaseUrl = kernelConfig.getProperty("binding.http.localTargetBaseUrl");

            logger.info("Following Redirects internally : " + followRedirects);

            if (reuseHttpClient)
                logger.warn("Reuse HTTP client option is ON (EXPERIMENTAL !!!!)");

        }
    }





    protected void serviceTODO(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        long started = 0;

        MonitoringServer mServer = lookupMonitoring();

        try {

            started = System.currentTimeMillis();

            String nodeId = null;
            if (kernelConfig != null) {

                // Add node ID to response headers
                nodeId = kernelConfig.getProperty("idbus.node");
                if(nodeId != null)
                    res.addHeader("X-IdBus-Node", nodeId);

                // Add additional headers

                String xFrameOptions = kernelConfig.getProperty("binding.http.xFrameOptionsMode");
                if (xFrameOptions != null) {

                    XFrameOptions mode = XFrameOptions.fromValue(xFrameOptions);

                    String xFrameOptoinsURLs = "";
                    if (kernelConfig.getProperty("binding.http.xFrameOptionsURLs") != null) {
                        StringTokenizer st = new StringTokenizer(kernelConfig.getProperty("binding.http.xFrameOptionsURLs"), ",");
                        while (st.hasMoreTokens()) {
                            String s = st.nextToken();
                            xFrameOptoinsURLs = xFrameOptoinsURLs + " '" + s + "'";
                        }
                    }


                    switch(mode) {
                        case DISABLED:
                            // Nothing to do
                            break;
                        case SAME_ORIGIN:
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue());
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors 'self'" + xFrameOptoinsURLs);
                            break;
                        case ALLOW_FROM:
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue() + xFrameOptoinsURLs);
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors" + xFrameOptoinsURLs);
                            break;
                        case DENY:
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, mode.getValue());
                            res.addHeader(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors 'none'");
                            break;
                        default:
                            logger.error("Unknown X-Frame-Options mode " + mode.getValue());
                           break;

                    }
                }

            }

            if (internalProcessingPolicy == null) {
                org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                        (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                                WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

                internalProcessingPolicy = (InternalProcessingPolicy) wac.getBean("internal-processing-policy");
            }

            // Do we actually service this request or we proxy it ?
            if (!followRedirects || !internalProcessingPolicy.match(req)) {

                // Non proxied requests, that required secured cookies
                if (req.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROXIED_REQUEST) == null && (secureCookies || req.isSecure())) {
                    if (logger.isTraceEnabled())
                        logger.trace("Requesting secure cookies for non-proxied request");
                    req.setAttribute("org.atricore.idbus.http.SecureCookies", "TRUE");
                }

                doService(req, res);
            } else {
                StopWatch sw = new StopWatch("http-request-processing-time-ms");
                sw.start();
                // Just signal if the result of proxied requests must be secured
                if (secureCookies || req.isSecure()) {

                    if (logger.isTraceEnabled())
                        logger.trace("Requesting secure cookies for proxied requests");

                    req.setAttribute("org.atricore.idbus.http.SecureCookies", "TRUE");
                }

                doProxyInternally(req, res);
                sw.stop();
                mServer.recordResponseTimeMetric(ATRICORE_WEB_PROCESSING_TIME_MS_METRIC_NAME, sw.getTotalTimeMillis());
            }

        } finally {
            long ended = System.currentTimeMillis();
            String parentThread = req.getHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST);
            if (parentThread == null) {
                mServer.recordResponseTimeMetric(ATRICORE_WEB_BROWSER_PROCESSING_TIME_MS_METRIC_NAME, ended - started);
            }

        }


    }

    protected void doProxyInternally(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String remoteAddr = null;
        String remoteHost = null;
        String parentThread = req.getHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST);
        if (parentThread == null) {

            if (req.getHeader("X-Forwarded-For") != null) {
                // This means that we're probably behind a proxy

                /**
                The general format of the field is:
                X-Forwarded-For: client, proxy1, proxy2
                where the value is a comma+space separated list of IP addresses, the left-most being the original client, and each successive proxy
                 */
                String addresses = req.getHeader("X-Forwarded-For");
                StringTokenizer st = new StringTokenizer(addresses, ",", false);
                remoteAddr = st.nextToken();
                if (remoteAddr != null)
                    remoteAddr = remoteAddr.trim();
            }

            // Take default request remote address
            if (remoteAddr  == null) {
                remoteAddr = req.getRemoteAddr();
            }
            remoteHost = req.getRemoteHost();
        } else {
            remoteAddr = req.getHeader(HTTP_HEADER_IDBUS_REMOTE_ADDRESS);
            remoteHost = req.getHeader(HTTP_HEADER_IDBUS_REMOTE_HOST);
        }

        HttpRequestBase proxyReq = buildProxyRequest(req, remoteAddr, remoteHost);
        URI reqUri = proxyReq.getURI();
        String cookieDomain = reqUri.getHost();

        // Create HTTP Client
        HttpClient httpClient = getHttpClient();
        HttpProtocolParams.setUserAgent(httpClient.getParams(), req.getHeader("User-Agent"));

        // Create an HTTP Context to publish resources for our client components
        HttpContext httpContext = new BasicHttpContext();

        // Publish the original request, it will be removed from the context later, on the first internal redirect.
        httpContext.setAttribute("org.atricorel.idbus.kernel.main.binding.http.HttpServletRequest", req);
        httpContext.setAttribute("org.atricorel.idbus.kernel.main.binding.http.CookieDomain", cookieDomain);

        if (logger.isTraceEnabled())
            logger.trace("Staring to follow redirects for " + req.getPathInfo());

        // Store received headers and send them back to the browser
        List<Header> storedHeaders = new ArrayList<Header>(40);
        boolean followTargetUrl = true;
        byte[] buff = new byte[1024];

        while(followTargetUrl) {

            if (logger.isTraceEnabled())
                logger.trace("Sending internal request " + proxyReq);

            // ----------------------------------------------------
            // Execute the request internally:
            // ----------------------------------------------------
            HttpResponse proxyRes = httpClient.execute(proxyReq, httpContext);

            if (logger.isTraceEnabled())
                logger.trace("Sending internal request " + proxyReq + " DONE!");

            String targetUrl = null;
            Header[] headers = null;

            try {

                // ----------------------------------------------------
                // Store some of the received HTTP headers, skip others
                // Set-Cookie headers are the most important
                // ----------------------------------------------------
                headers = proxyRes.getAllHeaders();

                for (Header header : headers) {
                    // Ignored headers
                    if (header.getName().equals("Server")) continue;
                    if (header.getName().equals("Transfer-Encoding")) continue;
                    if (header.getName().equals("Location")) continue;
                    if (header.getName().equals("Expires")) continue;
                    if (header.getName().equals("Content-Length")) continue;
                    if (header.getName().equals("Content-Type")) continue;

                    // The sender of the response has explicitly ask no to follow this redirect.
                    if (header.getName().equals(HTTP_HEADER_FOLLOW_REDIRECT)) {
                        // Set 'followTargetUrl' to false
                        followTargetUrl = false;
                        continue;
                    }

                    if (header.getName().equals(HTTP_HEADER_IDBUS_FOLLOW_REDIRECT)) {
                        // Set 'followTargetUrl' to false
                        followTargetUrl = false;
                        continue;
                    }

                    storedHeaders.add(header);

                }

                if (logger.isTraceEnabled())
                    logger.trace("HTTP/STATUS:" + proxyRes.getStatusLine().getStatusCode() + "["+proxyReq+"]");

                // ----------------------------------------------------
                // Based on the status, we may need to follow redirects
                // ----------------------------------------------------
                switch (proxyRes.getStatusLine().getStatusCode()) {
                    case 302:
                        // This is a redirect, but is it to an IDBUS local URL ?
                        // See if we have to proxy the response
                        Header location = proxyRes.getFirstHeader("Location");
                        targetUrl = location.getValue();

                        // Check if the target URL is an IDBUS endpoint
                        if (!followTargetUrl || !internalProcessingPolicy.match(req, targetUrl)) {

                            // This is outside our scope, send the response to the browser ...
                            if (logger.isTraceEnabled())
                                logger.trace("Do not follow HTTP 302 to ["+location.getValue()+"]");

                            Collections.addAll(storedHeaders, proxyRes.getHeaders("Location"));
                            followTargetUrl = false;
                        } else {

                            // The redirect can be handled by our HTTP client, no need to send it to the browser
                            if (logger.isTraceEnabled())
                                logger.trace("Do follow HTTP 302 to ["+location.getValue()+"]");

                            followTargetUrl = true;

                        }

                        break;
                    default:
                        // All non 302 codes are sent to the browser
                        if (logger.isTraceEnabled())
                            logger.trace("Do not follow HTTP " + proxyRes.getStatusLine().getStatusCode());

                        followTargetUrl = false;
                        break;
                }

            } finally {

                // Clean the client connection

                // Get hold of the response entity
                HttpEntity entity = proxyRes.getEntity();

                // If the response does not enclose an entity, there is no need
                // to bother about connection release

                if (entity != null) {

                    if (logger.isTraceEnabled())
                        logger.trace("Reading HTTP entity content " + entity.getContentType());

                    // Release the connection, read all available content.
                    InputStream instream = null;
                    try {

                        instream = entity.getContent();

                        if (!followTargetUrl) {
                            // If we're not following the target URL, send all to the browser

                            if (logger.isTraceEnabled())
                                logger.trace("Sending entity content " + entity.getContentType() + " to browser");

                            // Last received headers
                            if (headers != null) {
                                for (Header header : headers) {
                                    if (header.getName().equals("Content-Type"))
                                        res.setHeader(header.getName(), header.getValue());
                                    if (header.getName().equals("Content-Length"))
                                        res.setHeader(header.getName(), header.getValue());
                                }
                            }

                            // Previously stored headers
                            res.setStatus(proxyRes.getStatusLine().getStatusCode());

                            boolean secureRequestCookies  = req.getAttribute( "org.atricore.idbus.http.SecureCookies") != null;

                            for (Header header : storedHeaders) {
                                if (header.getName().startsWith("Set-Cookie")) {
                                    String hValue = header.getValue() + (secureRequestCookies  ? ";Secure" : "");
                                    if (logger.isTraceEnabled())
                                        logger.trace("Adding 'Set-Cookie' header : " + header.getValue());
                                    res.addHeader(header.getName(), hValue);
                                } else
                                    res.setHeader(header.getName(), header.getValue());
                            }

                            // Send content to browser
                            IOUtils.copy(instream, res.getOutputStream());
                            res.getOutputStream().flush();

                        } else {

                            if (logger.isTraceEnabled())
                                logger.trace("Ignoring entity content " + entity.getContentType());

                            // Just ignore the content ...
                            // should we do something with this ?!
                            int r = instream.read(buff);
                            int total = r;
                            while (r > 0) {
                                r = instream.read(buff);
                                total += r;
                            }

                            if (total > 0)
                                logger.warn("Ignoring entity content size : " + total);

                        }

                    } catch (IOException ex) {
                        // In case of an IOException the connection will be released
                        // back to the connection manager automatically
                        throw ex;
                    } catch (RuntimeException ex) {
                        // In case of an unexpected exception you may want to abort
                        // the HTTP request in order to shut down the underlying
                        // connection immediately.
                        logger.warn("Aborting HTTP Connection " + proxyReq.getURI());
                        proxyReq.abort();
                        throw ex;
                    } finally {

                        try {

                            // Closing the input stream will trigger connection release
                            if (instream != null)
                                instream.close();

                            if (logger.isTraceEnabled())
                                logger.trace("Releasing HTTP Connection " + proxyReq.getURI());
                            proxyReq.releaseConnection();
                        } catch (Exception ignore) {
                            // Ignore this
                        }
                    }

                } else {

                    try {
                        if (!followTargetUrl) {

                            if (logger.isTraceEnabled())
                                logger.trace("Sending response to the browser, HTTP Status " + proxyRes.getStatusLine().getReasonPhrase());

                            // If we're not following the target URL, send all to the browser
                            res.setStatus(proxyRes.getStatusLine().getStatusCode());

                            if (headers != null) {
                                // Latest headers
                                for (Header header : headers) {
                                    if (header.getName().equals("Content-Type"))
                                        res.setHeader(header.getName(), header.getValue());
                                    if (header.getName().equals("Content-Length"))
                                        res.setHeader(header.getName(), header.getValue());

                                }
                            }

                            boolean secureRequestCookies  = req.getAttribute( "org.atricore.idbus.http.SecureCookies") != null;

                            for (Header header : storedHeaders) {
                                if (header.getName().startsWith("Set-Cookie")) {
                                    String hValue = header.getValue() + (secureRequestCookies ? ";Secure" : "");
                                    if (logger.isTraceEnabled())
                                        logger.trace("Adding 'Set-Cookie' header : " + header.getValue());
                                    res.addHeader(header.getName(), hValue);

                                } else {
                                    res.setHeader(header.getName(), header.getValue());
                                }
                            }

                        }

                    } finally {

                        if (proxyReq != null) {

                            if (logger.isTraceEnabled())
                                logger.trace("Releasing HTTP Connection " + proxyReq.getURI());

                            proxyReq.releaseConnection();
                        }
                    }


                }

            }

            if (followTargetUrl) {

                if (logger.isTraceEnabled())
                    logger.trace("Building new proxy HTTP Request for " + targetUrl);

                proxyReq = buildProxyRequest(req, targetUrl, remoteAddr, remoteHost);
                // Clear context, we many need a new instance

                httpContext = null;
            }

        }

        if (logger.isTraceEnabled())
            logger.trace("Ended following redirects for " + req.getPathInfo());

    }

    protected HttpRequestBase buildProxyRequest(HttpServletRequest req, String targetUrl, String remoteAddr, String remoteHost) throws MalformedURLException {

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
        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_ADDRESS, remoteAddr);
        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_HOST, remoteHost);
        proxyReq.addHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST, "TRUE");
        if (req.isSecure())
            proxyReq.addHeader(HTTP_HEADER_IDBUS_SECURE, "TRUE");

        return proxyReq;
    }

    protected HttpClient getHttpClient() {

        if (!reuseHttpClient) {
            logger.trace("Building HTTP client instance");
            return buildHttpClient(false);
        }

        if (logger.isTraceEnabled())
            logger.trace("Reusing HTTP client instance (experimental)");

        if (httpClient == null)
            httpClient = buildHttpClient(true);

        return httpClient;

    }

    protected HttpClient buildHttpClient(boolean multiThreaded) {

        if (logger.isDebugEnabled())
            logger.debug("Building HttpClient instance: [multithreaded:" + multiThreaded + "]");

        DefaultHttpClient newHttpClient = multiThreaded ? new DefaultHttpClient(new ThreadSafeClientConnManager()) : new DefaultHttpClient();

        // Tailor client, we need to send the cookies received from the browser on all requests.
        // Replace default request cookie handling interceptor
        int intIdx = 0;
        for (int i = 0 ; i < newHttpClient.getRequestInterceptorCount(); i++) {
            if (newHttpClient.getRequestInterceptor(i) instanceof RequestAddCookies) {
                intIdx = i;
                break;
            }
        }

        IDBusRequestAddCookies interceptor = new IDBusRequestAddCookies();
        newHttpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
        newHttpClient.addRequestInterceptor(interceptor, intIdx);

        // Configure client, disable following redirects, we want to be in control of redirecting
        newHttpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        newHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        HttpConnectionParams.setConnectionTimeout(newHttpClient.getParams(), connectionTimeoutMillis);
        HttpConnectionParams.setSoTimeout(newHttpClient.getParams(), socketTimeoutMillis);


        httpClient = newHttpClient;

        return httpClient;
    }

    protected HttpRequestBase buildProxyRequest(HttpServletRequest req, String remoteAddr, String remoteHost) throws ServletException {

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

        Enumeration<String> hNames = req.getHeaderNames();
        while (hNames.hasMoreElements()) {
            String hName = hNames.nextElement();
            String hValue = req.getHeader(hName);

            // Received cookies will be added to HTTP Client cookie store by our own cookie interceptor (IDBusRequestAddCookies)
            if (hName.equals("Cookie"))
                continue;

            proxyReq.addHeader(hName, hValue);
        }

        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_ADDRESS, remoteAddr);
        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_HOST, remoteHost);
        proxyReq.addHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST, "TRUE");
        if (req.isSecure())
            proxyReq.addHeader(HTTP_HEADER_IDBUS_SECURE, "TRUE");

        return proxyReq;

    }

    /**
     * Actually process this request
        */
    @Override
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
            logger.warn("Make sure your appliance is STARTED [" + req.getRequestURL().toString() + "]");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            // TODO : Send 404 HTML content
            return;
        }

        if (logger.isTraceEnabled()) {
            String cookies = req.getHeader("Cookie");
            logger.trace("Received COOKIES ["+cookies+"]");
        }

        IDBusHttpEndpoint endpoint = (IDBusHttpEndpoint) consumer.getEndpoint();
        /// Synchrony version : TODO: use this instead of the old one with continuations!
        try {
            final Exchange exchange = null; // TODO UPD_15 new DefaultExchange(endpoint, req, res);

            if (logger.isTraceEnabled())
                logger.trace("Triggering camel processors for consumer " + consumer.getPath());

            consumer.getProcessor().process(exchange);

            if (logger.isTraceEnabled())
                logger.trace("Writing exchange to binding " + exchange.getExchangeId());

            consumer.getBinding().writeResponse(exchange, res);

            if (logger.isTraceEnabled())
                logger.trace("Processed 'doService' ");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }



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

    protected MonitoringServer lookupMonitoring() throws ServletException {

        org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                        WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        if (wac == null) {
            logger.error("Spring application context not found in servlet context");
            throw new ServletException("Spring application context not found in servlet context");
        }

        return (MonitoringServer)wac.getBean("monitoring");
    }

    protected class WHttpServletResponse extends HttpServletResponseWrapper {

        public WHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type"))
                super.addHeader("Content-Type", value);
            else
                super.addHeader(name, value);
        }

        @Override
        public void setHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type"))
                super.setHeader("Content-Type", value);
            else
                super.setHeader(name, value);
        }
    }
}