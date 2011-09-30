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
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.util.StopWatch;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiIDBusServlet3 extends CamelContinuationServlet {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet.class);

    private IdentityMediationUnitRegistry registry;

    public OsgiIDBusServlet3() {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Do we actually service this request or we proxy it ?
        if (req.getHeader("IDBUS-PROXIED-REQUEST") != null && req.getHeader("IDBUS-PROXIED-REQUEST").equals("TRUE")) {
            doService(req, res);
        } else {
            doProxy(req, res);
        }


    }

    protected void doProxy(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {


        HttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpRequestBase proxyReq = buildProxyRequest(req);
        HttpResponse proxyRes = null;

        List<Header> storedHeaders = new ArrayList<Header>(40);

        boolean followTargetUrl = true;

        while(followTargetUrl) {

            proxyRes = httpclient.execute(proxyReq);
            String targetUrl = null;

            // store received  cookie headers!
            Header[] headers = proxyRes.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().equals("Server"))
                    continue;

                if (header.getName().equals("Transfer-Encoding"))
                    continue;

                if (header.getName().equals("Location"))
                    continue;

                if (header.getName().equals("Expires"))
                    continue;


                storedHeaders.add(header);

            }

            if (logger.isTraceEnabled())
                logger.trace("HTTP/STATUS:" + proxyRes.getStatusLine().getStatusCode() + "["+proxyReq+"]");

            switch (proxyRes.getStatusLine().getStatusCode()) {
                case 200:
                    // TODO : Support following POST binding !?
                    followTargetUrl = false;
                    break;
                case 404:
                    followTargetUrl = false;
                    break;
                case 500:
                    followTargetUrl = false;
                    break;
                case 302:
                    // See if we have to proxy the response
                    Header location = proxyRes.getFirstHeader("Location");
                    targetUrl = location.getValue();

                    // TODO : Take from configuration !!!!
                    if (!location.getValue().startsWith("http://192.168.1.55:8081/IDBUS")) {
                        Collections.addAll(storedHeaders, proxyRes.getHeaders("Location"));
                        followTargetUrl = false;
                    } else {

                        // Create a new get and follow it
                        if (logger.isTraceEnabled())
                            logger.trace("Folow 302 target URL: ["+location.getValue()+"]");

                        // Read all now ..
                        if (proxyRes.getEntity().getContentLength() > 0) {
                            IOUtils.copy(proxyRes.getEntity().getContent(), new ByteArrayOutputStream((int)proxyRes.getEntity().getContentLength()));
                        }


                    }

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
                        res.setStatus(proxyRes.getStatusLine().getStatusCode());
                        for (Header header : storedHeaders) {
                            res.setHeader(header.getName(), header.getValue());
                        }

                        IOUtils.copy(instream, res.getOutputStream());
                    } else {

                        // Cookies are automatically managed by the client :)
                        proxyReq = new HttpGet(targetUrl);

                        // just ignore it
                        instream.read();


                    }
                    // do something useful with the response
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
            }



        }


    }

    protected HttpRequestBase buildProxyRequest(HttpServletRequest req) {
        StringBuffer targetUrl = new StringBuffer("http://localhost:8081");

        HttpRequestBase proxyReq = null;

        targetUrl.append(req.getContextPath().equals("") ? "/" : req.getContextPath()).append(req.getPathInfo());

        if (req.getMethod().equalsIgnoreCase("GET")) {
            if (req.getQueryString() != null)
                targetUrl.append("?").append(req.getQueryString());

            proxyReq = new HttpGet(targetUrl.toString());

            String cookies = req.getHeader("Cookie");
            proxyReq.addHeader("Cookie", cookies);

        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            // TODO !!!!!
            // get body>!
        }

        proxyReq.addHeader("IDBUS-PROXIED-REQUEST", "TRUE");
        // Add incoming headers, like cookies!


        return proxyReq;

    }




    protected void doService(HttpServletRequest req, HttpServletResponse r)
            throws ServletException, IOException {


        // FIX For a bug in CXF!
        HttpServletResponse res = new WHttpServletResponse(r);

        // Lookup identity mediation registry
        if (registry == null)
            registry = lookupIdentityMediationUnitRegistry();

        if (registry == null) {
            logger.error("No identity mediation registry found ");
            throw new ServletException("No identity mediation registry found!");
        }






        HttpConsumer consumer = resolveConsumer(req);
        if (consumer == null) {
            log("No HTTP Consumer found for " + req.getRequestURL().toString() + " Sending 404 (Not Found) HTTP Status.");
            logger.warn("Make sure your appliance is STARTED");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            // TODO : Send 404 page
            return;
        }

        IDBusHttpEndpoint endpoint = (IDBusHttpEndpoint) consumer.getEndpoint();

        final Continuation continuation = ContinuationSupport.getContinuation(req, null);
        if (continuation.isNew()) {

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
            consumer.getBinding().writeResponse(exchange, res);
            return;
        }

        if (continuation.isResumed()) {
            HttpExchange exchange = (HttpExchange) continuation.getObject();
            // now lets output to the response
            consumer.getBinding().writeResponse(exchange, res);
            return;
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

            if (logger.isDebugEnabled())
                logger.debug("Scanning Identity Mediation Unit [" + identityMediationUnit.getName() + "] " +
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