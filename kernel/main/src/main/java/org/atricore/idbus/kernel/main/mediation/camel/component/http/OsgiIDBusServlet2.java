package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.component.jetty.CamelContinuationServlet;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.Registry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.mail.Header;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiIDBusServlet2 extends CamelContinuationServlet {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet.class);

    private IdentityMediationUnitRegistry registry;

    public OsgiIDBusServlet2() {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }



    @Override
    protected void service(HttpServletRequest req, HttpServletResponse r)
            throws ServletException, IOException {

        HttpServletResponse res = new WHttpServletResponse(r);

        // Lazy resolve identity mediation unit registry, give time OSGi components to startup
        if (registry == null) {
            synchronized (this) {
                registry = lookupIdentityMediationUnitRegistry();
            }
        }

        if (registry == null) {
            logger.error("No identity mediation registry found ");
            throw new ServletException("No identity mediation registry found!");
        }

        // PIVOT POC
        try {

            // Create a new request object based on the original request
            InternalHttpServletRequest ireq = createInternalRequest(req);

            // Create a new response object based on the original response
            InternalHttpServletResponse ires = createInternalResponse(res);

            doDispatch(ireq, ires);
            storeResponseState(ires, res);

            while (canHandleInternally(ires)) {
                // Create a new request for a previous response (act as http client)
                ireq = createInternalRequestFromResponse(ireq, ires);

                // Create a new response object based on the previous response
                ires = createInternalResponse(ires);

                // Dispatch through camel
                doDispatch(ireq, ires);

                // Store received headers and other stuff
                storeResponseState(ires, res);
            }

            //flushResponseState();

        } finally {
            //Flush state to
        }

    }

    protected void doDispatch(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        // FIX For a bug in CXF

        //HttpServletResponse res = new WHttpServletResponse(r);


        HttpConsumer consumer = resolveConsumer(req);
        if (consumer == null) {
            log("No HTTP Consumer found for " + req.getRequestURL().toString() + " Sending 404 (Not Found) HTTP Status.");
            logger.warn("Make sure your appliance is STARTED");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            // TODO : Send 404 page
            return;
        }

        IDBusHttpEndpoint endpoint = (IDBusHttpEndpoint) consumer.getEndpoint();

        final HttpExchange exchange = new HttpExchange(endpoint, req, res);

        boolean sync = consumer.getAsyncProcessor().process(exchange, new AsyncCallback() {
            public void done(boolean sync) {
                if (sync) {
                    return;
                }
//                continuation.setObject(exchange);
//                continuation.resume();
            }
        });


        // Have camel process the HTTP exchange.

        if (!sync) {
            // Wait for the exchange to get processed.
            // This might block until it completes or it might return via an exception and
            // then this method is re-invoked once the the exchange has finished processing
//              continuation.suspend(0);
        }

        // HC: The getBinding() is interesting because it illustrates the
        // impedance miss-match between HTTP's stream oriented protocol, and
        // Camels more message oriented protocol exchanges.

        // now lets output to the response
        // TODO : consumer.getBinding().writeResponse(exchange, res);
        logger.debug("Processed request/response");
        return;

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

    // -------------------------------------------------------------------
    // HTTP Pivot Utilities
    // -------------------------------------------------------------------
    /**
     * Create HTTP Request
     */
    protected InternalHttpServletRequest createInternalRequest(HttpServletRequest orig) {
        Request jOrig = (Request) orig;

        HttpConnection cOrig = jOrig.getConnection();
        HttpFields httpFields = cOrig.getRequestFields();

        InternalHttpServletRequest req = new InternalHttpServletRequest ();

        req.setRequestFields(httpFields);

        // Copy most of the properties from orig to req:

        req.setAttributes(jOrig.getAttributes());
        req.setParameters(jOrig.getParameters());
        req.setCookies((jOrig.getCookies()));

        req.setPathInfo(jOrig.getPathInfo());
        req.setSession(jOrig.getSession());
        req.setProtocol(jOrig.getProtocol());
        req.setContext(jOrig.getContext());
        req.setContextPath(jOrig.getContextPath());

        req.setServerName(jOrig.getServerName());
        req.setServerPort(jOrig.getServerPort());
        req.setServletName(jOrig.getServletName());
        req.setServletPath(jOrig.getServletPath());

        req.setUserPrincipal(jOrig.getUserPrincipal());

        req.setQueryString(jOrig.getQueryString());
        req.setRequestURI(jOrig.getRequestURI());

        req.setSessionManager(jOrig.getSessionManager());
        req.setRequestedSessionIdFromCookie(jOrig.isRequestedSessionIdFromCookie());

        req.setContext(jOrig.getContext());

        req.setRootURL(jOrig.getRootURL());

        req.setUserRealm(jOrig.getUserRealm());

        req.setQueryEncoding(jOrig.getQueryEncoding());
        req.setRoleMap(jOrig.getRoleMap());



        return req;

    }

    protected InternalHttpServletResponse createInternalResponse(HttpServletResponse orig) {

        InternalHttpServletResponse res = new InternalHttpServletResponse(null);

        return res;
    }

    protected InternalHttpServletResponse doDispatch(InternalHttpServletRequest request) {

        // Create response

        // send through camel

        // return response
        return null;
    }

    protected void storeResponseState(InternalHttpServletResponse ires, HttpServletResponse orig) {

    }

    protected boolean canHandleInternally(HttpServletResponse res) {
        return false;
    }

    /**
     * Creates a new HTTP Request based on a received response
     * @param request The originally sent HTTP Request
     * @param response The received HTTP Response
     * @return the new Internal Request to be dispatched ?!
     */
    protected InternalHttpServletRequest createInternalRequestFromResponse(InternalHttpServletRequest request, InternalHttpServletResponse response) {
        return null;
    }

    protected void flushState() {

    }



    // -------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------

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