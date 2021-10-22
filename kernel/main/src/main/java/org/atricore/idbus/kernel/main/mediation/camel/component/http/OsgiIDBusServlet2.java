package org.atricore.idbus.kernel.main.mediation.camel.component.http;

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
 * The same servlet will both act as a client, when receiving a browser request and sending it to the proxy, and
 * as the proxy when receiving requests generated as client.
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

    private boolean processingUIenabled;

    private boolean secureCookies = false;

    private String localTargetBaseUrl;

    private ConfigurationContext kernelConfig;

    private InternalProcessingPolicy internalProcessingPolicy;

    private HttpClient httpClient;

    private int connectionTimeoutMillis = 5000; // Five seconds

    private int socketTimeoutMillis = 300000; // Five minutes

    private String xFrameOptoinsURLs;

    private XFrameOptions mode;

    private String nodeId;

    public OsgiIDBusServlet2() {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        if (kernelConfig == null) {

            // Lazy load kernel config

            kernelConfig = HttpUtils.lookupKernelConfig(servletConfig.getServletContext());

            if (kernelConfig == null) {
                logger.error("No Kernel Configuration Context found!");
                throw new ServletException("No Kernel Configuration Context found!");
            }

            secureCookies = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.secureCookies", "false"));
            followRedirects = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.followRedirects", "true"));
            processingUIenabled = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.processingUIenabled", "false"));

            socketTimeoutMillis = Integer.parseInt(kernelConfig.getProperty("binding.http.socketTimeoutMillis", "300000"));
            connectionTimeoutMillis = Integer.parseInt(kernelConfig.getProperty("binding.http.connectionTimeoutMillis", "5000"));


            localTargetBaseUrl = kernelConfig.getProperty("binding.http.localTargetBaseUrl");

            logger.info("Following Redirects internally : " + followRedirects);


        }

        if (kernelConfig == null) {
            return;
        }

        nodeId = kernelConfig.getProperty("idbus.node");

        String xFrameOptions = kernelConfig.getProperty("binding.http.xFrameOptionsMode");

        if (xFrameOptions != null) {

            mode = XFrameOptions.fromValue(xFrameOptions);

            String xFrameOptoinsURLs = "";
            if (kernelConfig.getProperty("binding.http.xFrameOptionsURLs") != null) {
                StringTokenizer st = new StringTokenizer(kernelConfig.getProperty("binding.http.xFrameOptionsURLs"), ",");
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    xFrameOptoinsURLs = xFrameOptoinsURLs + " '" + s + "'";
                }
            }

        } else {
            mode = XFrameOptions.DISABLED;
        }

    }

    /**
     * this servlet is "recursive".  This means that some responses that should be sent back to the browser, but are
     * actually HTTP redirects that this server will handle, will be processed locally.  An HTTP client will act on
     * browser's behalf.  The servlet will receive a request and process it using the IDBus.  If the response is a redirect
     * to another IDBus location/endpoints, an HTTP client will follow the redirect, until no more redirects are produced.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        long started = 0;

        MonitoringServer mServer = lookupMonitoring();

        try {

            started = System.currentTimeMillis();


            // Add node ID to response headers
            if(nodeId != null)
                res.addHeader("X-IdBus-Node", nodeId);

            // Add additional headers
            // TODO : Do not set the header if it is already there ?!
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

                // -------------------------------------
                // Process the request directly, without proxying it
                // -------------------------------------
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

                // -------------------------------------
                // Process the request with the client, as a proxy.
                // -------------------------------------
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

    /**
     * This method will process HTTP requests internally.  It will create an HTTP client
     * and issue the received HttpServletRequest acting on browser's behalf.
     *
     * If the response is an HTTP redirect (302) to a URL that can also be handled internally, it will process it.
     * This loop will end when a response cannot be handled internally, and a response to the browser will be created.
     *
     * The response will have all headers and cookies created during the internal looping process.
     */
    protected void doProxyInternally(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String remoteAddr = null;
        String remoteHost = null;

        // We need the remote address for auditing purposes.
        // If this is NOT a proxied request (no parent thread header), we get remote address from
        // standard headers.
        String parentThread = req.getHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST);
        if (parentThread == null) {
            remoteAddr = HttpUtils.getRemoteAddress(req);
            remoteHost = req.getRemoteHost();
        } else {
            // We are working with a proxied request, use our internal/custom headers to get address and host
            remoteAddr = req.getHeader(HTTP_HEADER_IDBUS_REMOTE_ADDRESS);
            remoteHost = req.getHeader(HTTP_HEADER_IDBUS_REMOTE_HOST);
        }

        // Now we need to build an HTTP request and issue it as a client (acting as the browser).
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

        // Publish cookie domain as a client header
        httpContext.setAttribute("org.atricorel.idbus.kernel.main.binding.http.CookieDomain", cookieDomain);

        if (logger.isTraceEnabled())
            logger.trace("Starting to follow redirects for " + req.getPathInfo());

        // Store received headers and send them back to the browser
        List<Header> storedHeaders = new ArrayList<Header>(40);
        boolean followTargetUrl = true;
        boolean useProcessingUi = processingUIenabled;
        byte[] buff = new byte[1024];

        // As long as we receive redirects that must be handled locally, we loop.
        while(followTargetUrl) {

            if (logger.isTraceEnabled())
                logger.trace("Sending internal request " + proxyReq);

            // ----------------------------------------------------
            // Execute the request internally: this will be processed by this servlet (works recursively)
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
                    // Ignored headers, we will use original values for these.
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

                    // The sender of the response has explicitly ask no to follow this redirect.
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

                        // Check if the target URL must be proxied or just serviced.
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
                // ---------------------------------------------------------
                // Clean the client connection - START
                // ---------------------------------------------------------

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
                            // This is a response that will go to the browser, not handled internally.  It may be a 302 or
                            // some other code ...

                            // If we're not following the target URL, send all to the browser

                            if (logger.isTraceEnabled())
                                logger.trace("Sending entity content " + entity.getContentType() + " to browser");

                            // Previously stored headers
                            prepareResponse(req, res, headers, storedHeaders);

                            if (useProcessingUi && proxyRes.getStatusLine().getStatusCode() == 302) {
                                // When working with processing UI page, do NOT return 302, instead 200 and a custom location header: X-IdBusLocation ...
                                res.setHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_LOCATION, targetUrl);
                                res.setStatus(200);
                            } else {
                                res.setStatus(proxyRes.getStatusLine().getStatusCode());
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
                            //------------------------------------------------------------------------------------------
                            // SEND LAST RESPONSE BACK TO THE BROWSER
                            //
                            // This will send the response back to the browser, when no content is received in the HTTP response.
                            //------------------------------------------------------------------------------------------

                            if (logger.isTraceEnabled())
                                logger.trace("Sending response to the browser, HTTP Status " + proxyRes.getStatusLine().getReasonPhrase());

                            if (processingUIenabled && proxyRes.getStatusLine().getStatusCode() == 302) {
                                res.setHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_LOCATION, targetUrl);
                                res.setStatus(200);
                            } else {
                                // Send received HTTP STATUS (200, 302, 401, 403, 500, etc)
                                res.setStatus(proxyRes.getStatusLine().getStatusCode());
                            }


                            prepareResponse(req, res, headers, storedHeaders);


                        }

                    } finally {

                        if (proxyReq != null) {

                            if (logger.isTraceEnabled())
                                logger.trace("Releasing HTTP Connection " + proxyReq.getURI());

                            proxyReq.releaseConnection();
                        }
                    }


                }
                // ---------------------------------------------------------
                // Clean the client connection - END
                // ---------------------------------------------------------


            }


            // If we received a redirect that must be processed locally, build a new request for it.
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
        proxyReq.addHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROCESS_UI, "TRUE");

        return proxyReq;
    }

    protected HttpClient getHttpClient() {
        logger.trace("Building HTTP client instance");
        return buildHttpClient(false);
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

    /**
     * This will create an HTTP request on behalf of the user's browser, to be handled locally.
     *
     * @param req Received HTTP request
     * @param remoteAddr user's remote address
     * @param remoteHost user's remote host
     *
     */
    protected HttpRequestBase buildProxyRequest(HttpServletRequest req, String remoteAddr, String remoteHost) throws ServletException {

        HttpRequestBase proxyReq = null;

        // First wi build a target URL, local to the IDBus.
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

        // Now we build an HTTP GET based on the new URL, to be used by the HTTP client.
        proxyReq = new HttpGet(targetUrl.toString());

        // Copy all original headers except cookies (they are handled separately)
        Enumeration<String> hNames = req.getHeaderNames();
        while (hNames.hasMoreElements()) {
            String hName = hNames.nextElement();
            String hValue = req.getHeader(hName);

            // Received cookies will be added to HTTP Client cookie store by our own cookie interceptor (IDBusRequestAddCookies)
            if (hName.equals("Cookie"))
                continue;

            proxyReq.addHeader(hName, hValue);
        }


        // Add remote address and host as headers, to keep track of original values
        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_ADDRESS, remoteAddr);
        proxyReq.addHeader(HTTP_HEADER_IDBUS_REMOTE_HOST, remoteHost);

        // Mark request as PROXIED, so that we don't get into an infinite loop in the servlet.
        proxyReq.addHeader(HTTP_HEADER_IDBUS_PROXIED_REQUEST, "TRUE");
        if (req.isSecure())
            proxyReq.addHeader(HTTP_HEADER_IDBUS_SECURE, "TRUE");

        return proxyReq;

    }

    /**
     * Actually process this request
     */
    protected void doService(HttpServletRequest req, HttpServletResponse r)
            throws ServletException, IOException {

        // FIX For a bug in CXF!
        WHttpServletResponse res = new WHttpServletResponse(r);

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
        /** Synchrony version : TODO: use this instead of the old one with continuations! */
        try {
            final HttpExchange exchange = new HttpExchange(endpoint, req, res);

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

    public class WHttpServletResponse extends HttpServletResponseWrapper {

        private int status;

        private String location;

        public WHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type"))
                super.addHeader("Content-Type", value);
            else
                super.addHeader(name, value);

            if (name.equalsIgnoreCase("location"))
                this.location = value;
        }

        @Override
        public void setHeader(String name, String value) {
            if (name.equalsIgnoreCase("content.type"))
                super.setHeader("Content-Type", value);
            else
                super.setHeader(name, value);

            if (name.equalsIgnoreCase("location"))
                this.location = value;

        }

        @Override
        public void setStatus(int sc) {
            this.status = status;
            super.setStatus(sc);
        }

        @Override
        public void setStatus(int sc, String sm) {
            this.status = status;
            super.setStatus(sc, sm);
        }

        public int getStatus() {
            return status;
        }

        public String getLocation() {
            return location;
        }
    }

    /**
     * Prepares the response to the browser
     * @param req original request
     * @param res original response
     *
     * @param headers headers received form last proxied request
     * @param storedHeaders all previously stored headers from proxied requests
     */
    protected void prepareResponse(HttpServletRequest req, HttpServletResponse res, Header[] headers, List<Header> storedHeaders) {

        // Send useful headers from last response.
        if (headers != null) {
            // Latest headers
            for (Header header : headers) {
                if (header.getName().equals("Content-Type"))
                    res.setHeader(header.getName(), header.getValue());
                if (header.getName().equals("Content-Length"))
                    res.setHeader(header.getName(), header.getValue());

            }
        }

        // Send new cookies, force them as secured if configured
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
}
