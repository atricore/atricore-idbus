package org.atricore.idbus.kernel.main.mediation.camel.component.http;


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

    private InternalProcessingPolicy internalProcessingPolicy;

    private ConfigurationContext kernelConfig;

    private ServletContext servletContext;

    private boolean followRedirects;

    private boolean processingUIenabled;

    public ProcessingUIServletFilter() {
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        servletContext = filterConfig.getServletContext();

        if (kernelConfig == null) {

            // Lazy load kernel config


            kernelConfig = HttpUtils.lookupKernelConfig(servletContext);

            if (kernelConfig == null) {
                logger.error("No Kernel Configuration Context found!");
                throw new ServletException("No Kernel Configuration Context found!");
            }

            followRedirects = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.followRedirects", "true"));
            processingUIenabled = Boolean.parseBoolean(kernelConfig.getProperty("binding.http.cors.allowAll", "false"));

            logger.info("Following Redirects internally : " + followRedirects);


        }

        if (kernelConfig == null) {
            return;
        }

    }


    /**
     * This will render an HTTP page that will actually trigger the processing of the original request.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hReq = (HttpServletRequest) req;
        HttpServletResponse hRes = (HttpServletResponse) res;

        // If processing UI is disabled, continue.
        if (!processingUIenabled) {
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


        if (!followRedirects || !internalProcessingPolicy.match(hReq)) {
            // This request MUST not be handled by the IDBUS processing UI page
            logger.trace("Request must be processed directly : " + ((HttpServletRequest) req).getRequestURL());
            chain.doFilter(req, res);

        } else if (hReq.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROCESS_UI) != null) {
            // This request was issued by the IDBUS Processing UI page, let the servlet handle it
            logger.trace("Request has already being processed by the UI : " + ((HttpServletRequest) req).getRequestURL());
            chain.doFilter(req, res);
        } else {

            logger.trace("Request must be processed by the UI : " + ((HttpServletRequest) req).getRequestURL());

            // TODO : Remove this and instead render the process UI page, send the original request so that it can be
            //  re-submitted with the HTTP_HEADER_IDBUS_PROCESS_UI header set.
            chain.doFilter(req, res);

        }
    }

    @Override
    public void destroy() {

    }
}
