package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.ui.WebBranding;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpUtils {

    private static final Log logger = LogFactory.getLog(HttpUtils.class);

    public static ConfigurationContext lookupKernelConfig(ServletContext servletContext) throws ServletException {

        org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        if (wac == null) {
            logger.error("Spring application context not found in servlet context");
            throw new ServletException("Spring application context not found in servlet context");
        }

        BundleContext bc = wac.getBundleContext();
/*
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


    public static WebBranding resolveWebBranding(ServletContext servletContext, HttpServletRequest req) {

        OsgiBundleXmlWebApplicationContext wac =
                (OsgiBundleXmlWebApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // TODO : Resolve the branding based on the current request and connect it with console branding
        return new WebBranding("JOSSO 2.5", "en_US", "josso25", null) {

        };

    }

    public static VelocityEngine getVelocityEngine() throws Exception {

        VelocityEngine velocityEngine = new VelocityEngine();

        // Setup classpath resource loader  (Actually not used!)
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "classpath");

        velocityEngine.addProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        velocityEngine.setProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".cache", "false");

        velocityEngine.setProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".modificationCheckInterval",
                "2");

        velocityEngine.init();

        return velocityEngine;
    }

    public static String getRemoteAddress(HttpServletRequest req) {
        String remoteAddr = null;
        if (req.getHeader("X-Forwarded-For") != null) {
            // This means that we're behind an external proxy

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
        return remoteAddr;

    }

}
