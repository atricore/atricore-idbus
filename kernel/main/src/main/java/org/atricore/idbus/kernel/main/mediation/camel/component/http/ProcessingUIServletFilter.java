package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet filter that renders a UI while the HTTP request is being processed.
 */
public class ProcessingUIServletFilter implements Filter {

    private static final Log logger = LogFactory.getLog(ProcessingUIServletFilter.class);

    private ConfigurationContext kernelConfig;
    private ServletContext servletContext;
    private InternalProcessingPolicy internalProcessingPolicy;

    private boolean processingUIenabled;
    private boolean followRedirects;

    private String pageTemplate;

    public ProcessingUIServletFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // Disable the filter if something goes wrong

        try {
            servletContext = filterConfig.getServletContext();
            prepareUiPageTemplate();

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
        } catch (Exception e) {
            logger.error("Processing UI Filter disabled due to error: " + e.getMessage(), e);
            this.processingUIenabled = false;
        }
    }

    private void prepareUiPageTemplate() throws ServletException {
        try {
            String html = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/processing-ui/josso-25/page.html"));
            String jquery = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/processing-ui/jquery.js"));

            pageTemplate = String.format(html, jquery);
        } catch (IOException e) {
            logger.error("Cannot load resource : " + e.getMessage(), e);
            throw new ServletException("Couldn't generate HTML page for Processing UI");
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

    private String prepareUiPage(HttpServletRequest request) {
        return pageTemplate.replace("#METHOD#", request.getMethod());
    }

    @Override
    public void destroy() {
    }

}
