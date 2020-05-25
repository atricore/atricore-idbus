package org.atricore.idbus.kernel.main.mediation.camel.component.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet filter that renders a UI while the HTTP request is being processed.
 */
public class ProcessingUIServletFilter implements Filter {

    private static final Log logger = LogFactory.getLog(ProcessingUIServletFilter.class);

    public static final String DEFAULT_BRANDING = "josso25";

    private ConfigurationContext kernelConfig;
    private ServletContext servletContext;
    private InternalProcessingPolicy internalProcessingPolicy;
    private VelocityEngine velocityEngine;

    private boolean processingUIenabled;
    private boolean followRedirects;


    private Map<String, String> pageTemplates = new HashMap<String, String>();

    public ProcessingUIServletFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // Disable the filter if something goes wrong

        try {
            servletContext = filterConfig.getServletContext();
            // TODO : pre-cache already existing brands: prepareUiPageTemplates();

            if (kernelConfig == null) {
                // Lazy load kernel config
                kernelConfig = HttpUtils.lookupKernelConfig(servletContext);

                if (kernelConfig == null) {
                    logger.error("No Kernel Configuration Context found! Disabling filter.");
                    processingUIenabled = false;
                    //throw new ServletException("No Kernel Configuration Context found!");
                    return;
                }

                processingUIenabled = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.processingUIenabled", "false"));
                logger.info("Processing UI Filter initialized: processingUIenabled=" + processingUIenabled);
                followRedirects = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.followRedirects", "false"));
                logger.info("Processing UI Filter initialized: followRedirects=" + followRedirects);
            }

            velocityEngine = HttpUtils.getVelocityEngine();

        } catch (Exception e) {
            logger.error("Processing UI Filter disabled due to error: " + e.getMessage(), e);
            this.processingUIenabled = false;
        }
    }



    /**
     * This will render an HTTP page that will actually trigger the processing of the original request.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hReq = (HttpServletRequest) req;
        HttpServletResponse hRes = (HttpServletResponse) res;
        String requestUrl = hReq.getRequestURL().toString();
        logger.trace("Processing request: " + requestUrl);

        if (((HttpServletRequest) req).getRequestURI().startsWith("/IDBUS/processing-ui")) {
            chain.doFilter(req, res);
            return;
        }

        // If processing UI is disabled or follow redirects is disabled, continue.
        if (!processingUIenabled  || !followRedirects ) {
            chain.doFilter(req, res);
            return;
        }

        // Identify if the request requires a UI.
        if (internalProcessingPolicy == null) {
            org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext wac =
                    (org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext)
                            WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

            internalProcessingPolicy = (InternalProcessingPolicy) wac.getBean("internal-processing-policy");
        }

        if (!internalProcessingPolicy.match(hReq)) {
            chain.doFilter(req, res);
            return;
        }

        if (hReq.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROCESS_UI) != null) {
            // This request was issued by the IDBUS Processing UI page, let the servlet handle it
            logger.trace("Request has already being processed by the UI : " + requestUrl);

            chain.doFilter(req, res);
        } else {
            logger.trace("Request must be processed by the UI : " + requestUrl);
            String page = prepareUiPage(hReq);
            hRes.getWriter().print(page);
        }
    }

    private String prepareUiPage(HttpServletRequest request) throws ServletException {

        // This should be the servlet context and the first level in the path (appliance ID)
        String pathInfo = request.getPathInfo();
        WebBranding branding = HttpUtils.resolveWebBranding(servletContext, request);

        String pageContent = pageTemplates.get(branding.getWebBrandingId());
        if (pageContent == null)  {
            pageContent = prepareUiPageTemplate(branding);
            pageTemplates.put(branding.getWebBrandingId(), pageContent);
        }

        return pageContent;
    }

    protected String prepareUiPageTemplate(WebBranding branding) throws ServletException {
        try {

            String pageTemplate = "/WEB-INF/processing-ui/" + branding.getWebBrandingId() + "/page.html";

            if (logger.isDebugEnabled())
                logger.debug("Resolving template [" + pageTemplate + "]");

            InputStream pageIs = servletContext.getResourceAsStream(pageTemplate);
            if (pageIs == null)
                pageIs = servletContext.getResourceAsStream("/WEB-INF/processing-ui/" + DEFAULT_BRANDING + "/page.html");

            Reader in = new InputStreamReader(pageIs);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(os);

            VelocityContext veCtx = new VelocityContext();
            // TODO : Variables : veCtx.put("")

            if (velocityEngine.evaluate(veCtx, out, branding.getWebBrandingId(), in)) {
                out.flush();
                return new String(os.toByteArray());
            }

            logger.error("No page found for branding: " + branding.getWebBrandingId());

            return null;

        } catch (IOException e) {
            logger.error("Couldn't generate HTML page for Processing UI: " + e.getMessage(), e);
            throw new ServletException("Couldn't generate HTML page for Processing UI");
        }
    }

    @Override
    public void destroy() {
    }

}
