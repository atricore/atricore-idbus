package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

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

    private boolean processingUIenabled;

    private String pageTemplate;

    public ProcessingUIServletFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        prepareUiPageTemplate();

        if (kernelConfig == null) {
            // Lazy load kernel config
            kernelConfig = HttpUtils.lookupKernelConfig(servletContext);

            if (kernelConfig == null) {
                logger.error("No Kernel Configuration Context found!");
                throw new ServletException("No Kernel Configuration Context found!");
            }

            processingUIenabled = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.processingUIenabled", "false"));
            logger.info("Processing UI Filter initialized: processingUIenabled=" + processingUIenabled);
        }
    }

    private void prepareUiPageTemplate() throws ServletException {
        try {
            String html = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/processing-ui/page.html"));
            String jquery = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/processing-ui/jquery.js"));
            String css = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/processing-ui/styles.css"));
            pageTemplate = String.format(html, jquery, css);
        } catch (IOException e) {
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

        // If processing UI is disabled, continue.
        if (!processingUIenabled) {
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
