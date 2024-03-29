package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import static org.atricore.idbus.kernel.main.mediation.camel.component.http.IDBusHttpConstants.IDBUS_CONTEXT;

/**
 * This policy will match
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DefaultInternalProcessingPolicy implements InternalProcessingPolicy, InitializingBean, MediationLocationsRegistry {

    private static final Log logger = LogFactory.getLog(DefaultInternalProcessingPolicy .class);

    private ConfigurationContext kernelConfig;

    private Set<String> excludedUrls = new HashSet<String>();

    private Set<String> includedUrls = new HashSet<String>();

    private Map<String, Set<String>> aliases = new HashMap<String, Set<String>>();

    public void afterPropertiesSet() throws Exception {

        String aliasesStr = kernelConfig.getProperty("binding.http.followRedirects.aliases");
        if (aliasesStr != null) {
            String[] aliasesMappings = aliasesStr.split(",");
            for (String aliasMapping : aliasesMappings) {
                String[] nameValue = aliasMapping.split("=");
                String host = nameValue[0];
                String alias = nameValue[1];

                if (logger.isDebugEnabled())
                    logger.debug("Binding host [" + host + "] with alias [" + alias + "]");

                Set<String> aliasesSet = aliases.get(host);
                if (aliasesSet == null) {
                    aliasesSet = new HashSet<String>();
                    aliases.put(host, aliasesSet);
                }
                aliasesSet.add(alias);
            }
        }

        String excludedUrlsCsv = kernelConfig.getProperty("binding.http.followRedirects.excludeUrls");
        if (excludedUrlsCsv != null) {
            StringTokenizer st = new StringTokenizer(excludedUrlsCsv, ",");
            while (st.hasMoreTokens()) {
                String url = st.nextToken().trim();
                logger.info("Not following redirects for ["+url+"]");
                excludedUrls.add(url);
            }
        }

        String includedUrlsCsv = kernelConfig.getProperty("binding.http.followRedirects.includeUrls");
        if (includedUrlsCsv != null) {
            StringTokenizer st = new StringTokenizer(includedUrlsCsv, ",");
            while (st.hasMoreTokens()) {
                String url = st.nextToken().trim();
                logger.info("Following redirects for ["+url+"]");
                includedUrls.add(url);
            }
        }

    }

    @Override
    public void register(String location) {
        // Location should be a URL, we assume IDBUS is used
        String url = stripAfterIDBUS(location);
        includedUrls.add(url);
    }

    @Override
    public void unregister(String location) {
        // Location should be a URL, we assume IDBUS is used
        String url = stripAfterIDBUS(location);
        includedUrls.remove(url);
    }

    public String stripAfterIDBUS(String input) {
        // Find the index of the first occurrence of "/IDBUS/" in the input string
        int index = input.indexOf(IDBUS_CONTEXT);

        // If "/IDBUS/" was not found in the input string, return the input string as is
        if (index == -1) {
            return input;
        }

        // Return a new string with everything after the first occurrence of "/IDBUS/" removed, including "/IDBUS/"
        return input.substring(0, index + IDBUS_CONTEXT.length());
    }


    /**
     * This method matches those requests that were processed internally and produced an HTTP redirect (302)
     * We need to compare if the target location received is actually a URL at the IDBUS servler.
     *
     * If not, include/exclude configurations are used, same as:
     *
     * #match(HttpServletRequest req) witout a redirectUrl.
     *
     */
    public boolean match(HttpServletRequest originalReq, String redirectUrl) {

        if (logger.isTraceEnabled())
            logger.trace("Matching: [" + redirectUrl + "], original: [" + originalReq.getRequestURL() + "]");

        // Force includes/excludes
        try {
            URL redir = new URL(redirectUrl);
            String originalServer = originalReq.getServerName();
            String redirServer = redir.getHost();

            if (!originalServer.equals(redirServer)) {

                Set<String> aliasesSet = aliases.get(redirServer);
                if (aliasesSet == null || !aliasesSet.contains(originalServer)) {

                    if (logger.isTraceEnabled())
                        logger.trace("Not Following, Matching URL to [" + redirectUrl + "] " + originalServer + "!=" + redirServer);
                    return false;

                }
            }


        } catch (MalformedURLException e) {
            if (logger.isTraceEnabled())
                logger.trace("Invalid target URL ["+redirectUrl+"] " + e.getMessage(), e);
        }

        if (logger.isTraceEnabled())
            logger.trace("Matching URL [" + redirectUrl+ "]");

        for (String excludedUrl : excludedUrls) {
            if (redirectUrl.length() >= excludedUrl.length()) {
                String prefix = redirectUrl.substring(0, excludedUrl.length());
                if (prefix.equals(excludedUrl)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Not Following, Matching URL to ["+excludedUrl+"]");

                    return false;
                }
            }
        }

        for (String includedUrl : includedUrls) {
            if (redirectUrl.length() >= includedUrl.length()) {
                String prefix = redirectUrl.substring(0, includedUrl.length());
                if (prefix.equals(includedUrl)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Following, Matching URL to [" + includedUrl + "]");
                    return true;
                }
            }
        }

        // See if we're redirected to the same host we started processing
        StringBuffer originalUrl = originalReq.getRequestURL();
        String ctxPath = originalReq.getContextPath();
        if (ctxPath.equals("")) {
            // Root context needs special treatment.
            throw new RuntimeException("Cannot work with mediation on root context !");
        }

        // This should give us the position of the "/" after the context
        int ctxEnd = originalUrl.indexOf(ctxPath) + ctxPath.length() + 1;

        // The URL we're going to is shorter that the original + the context path
        if (ctxEnd > redirectUrl.length()) {
            return false;
        }

        String originalBase = originalUrl.substring(0, ctxEnd);
        String redirectBase = redirectUrl.substring(0, ctxEnd);

        return originalBase.equals(redirectBase);


    }

    /**
     * This method matches those requests that MUST be processed internally
     *
     * 1. If the request hast the #IDBusHttpConstants.HTTP_HEADER_IDBUS_PROXIED_REQUEST header set, it will NOT be matched
     * to be processed internally. (It is already being processed internaly, this avoids infinite loops)
     *
     * 2. If the request is NOT an HTTP GET, it will NOT be matched.
     *
     * 3. If the request is for a SOAP endpoint, it will NOT be matched (no need to improve redirects here).
     *
     * 4. If the Location value (URL) matches those configured as included, it WILL be matched.
     *
     * 5. If the Location value (URL) matches those configured as excluded it will NOT be matched (unless 4., since it takes precedence)
     */
    public boolean match(HttpServletRequest req) {

        if (logger.isTraceEnabled())
            logger.trace("Matching: [" + req.getRequestURL() + "]");

        // Already internal, ignore it.
        if (req.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROXIED_REQUEST) != null)
            return false;

        // Do not proxy POST methods
        if (!req.getMethod().equals("GET"))
            return false;

        // Do not proxy SOAP requests
        if (req.getPathInfo().endsWith("/SOAP"))
            return false;

        // Do not proxy METADATA requests
        if (req.getPathInfo().endsWith("/MD"))
            return false;

        // Do not proxy METADATA requests
        if (req.getPathInfo().endsWith(".well-known/openid-configuration"))
            return false;

        // Do not proxy RESTFUL requests
        if (req.getPathInfo().endsWith("/REST"))
            return false;

        if (req.getPathInfo().endsWith("/JWKS"))
            return false;

        if (req.getPathInfo().endsWith("/SPNEGO/NEGOTIATE"))
            return false;

        // Exclude HttpError servlet (must match web.xml)
        if (req.getServletPath().equals("/ERR"))
            return false;

        StringBuffer reqUrl = req.getRequestURL();
        String requestUrl = reqUrl.toString();

        for (String excludedUrl : excludedUrls) {
            if (requestUrl.length() >= excludedUrl.length()) {
                String prefix = requestUrl.substring(0, excludedUrl.length());
                if (prefix.equals(excludedUrl)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Not Following, Matching URL to ["+excludedUrl+"]");

                    return false;
                }
            }
        }

        // Force includes/excludes
        for (String includedUrl : includedUrls) {
            if (requestUrl.length() >= includedUrl.length()) {
                String prefix = requestUrl.substring(0, includedUrl.length());
                if (prefix.equals(includedUrl)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Following, Matching URL to [" + includedUrl + "]");
                    return true;
                }
            }
        }

        return true;

    }


    public Set<String> getExcludedUrls() {
        return excludedUrls;
    }

    public void setExcludedUrls(Set<String> excludedUrls) {
        this.excludedUrls = excludedUrls;
    }

    public Set<String> getIncludedUrls() {
        return includedUrls;
    }

    public void setIncludedUrls(Set<String> includedUrls) {
        this.includedUrls = includedUrls;
    }

    public ConfigurationContext getKernelConfig() {
        return kernelConfig;
    }

    public void setKernelConfig(ConfigurationContext kernelConfig) {
        this.kernelConfig = kernelConfig;
    }


}
