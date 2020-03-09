package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import javax.servlet.http.HttpServletRequest;

/**
 * This policy objects signal if a request must be processed internally, or returned to the browser.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface InternalProcessingPolicy {

    boolean match(HttpServletRequest originalRequest, String redirectUrl);

    boolean match(HttpServletRequest request);
}
