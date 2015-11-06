package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Extends the default 'RequestAddCookies' initializing the client cookie store with the received cookies from the browser
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IDBusRequestAddCookies extends RequestAddCookies {

    private static final Log logger = LogFactory.getLog(IDBusRequestAddCookies.class);

    public IDBusRequestAddCookies() {

    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {

        HttpServletRequest originalRequest = (HttpServletRequest) context.getAttribute("org.atricorel.idbus.kernel.main.binding.http.HttpServletRequest");
        String cookieDomain = (String) context.getAttribute("org.atricorel.idbus.kernel.main.binding.http.CookieDomain");

        // Obtain cookie store
        CookieStore cookieStore = (CookieStore) context.getAttribute(
                ClientContext.COOKIE_STORE);
        if (cookieStore == null) {
            logger.error("Cookie store not specified in HTTP context");
            throw new HttpException("No CookieStore attribute found in context: " + ClientContext.COOKIE_STORE);
        }

        if (originalRequest != null) {

            // Convert received servlet cookies to HTTP client cookies
            if (originalRequest.getCookies() != null) {
                for (javax.servlet.http.Cookie svltCookie : originalRequest.getCookies()) {
                    Cookie clientCookie = toClientCookie(context, svltCookie, cookieDomain);
                    cookieStore.addCookie(clientCookie);
                }
            }

        }

        for (Cookie c : cookieStore.getCookies()) {
            if (c.isSecure()) {
                logger.trace("Cookie: " + c + " is secure");
            }
        }

        super.process(request, context);

    }

    /**
     * Since internal connections (from our HTTP client) are non-secure, cookies must ALL be set to secure = false
     */
    protected Cookie toClientCookie(HttpContext context, javax.servlet.http.Cookie svltCookie, String cookieDomain) {

        BasicClientCookie cookie = new BasicClientCookie(svltCookie.getName(), svltCookie.getValue());

        cookie.setDomain(svltCookie.getDomain() != null ? svltCookie.getDomain() : cookieDomain);
        // Path is not that important since we're already on the server and the cookie was received.
        cookie.setPath(svltCookie.getPath() != null ? svltCookie.getPath() : "/");

        // TODO : FOR NOW WE ONLY SUPPORT SESSION COOKIES
        // cookie.setExpiryDate();
        cookie.setVersion(svltCookie.getVersion());
        // Send cookies as non-secure internally :
        //cookie.setSecure(svltCookie.getSecure());
        cookie.setSecure(false);
        cookie.setComment(svltCookie.getComment());
        cookie.setExpiryDate(null);

        if (logger.isTraceEnabled())
            logger.trace("Server Cookie: " + toString(svltCookie));

        if (logger.isTraceEnabled())
            logger.trace("Client Cookie: " + cookie.toString());

        return cookie;
    }

    protected String toString(javax.servlet.http.Cookie cookie) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[version: ");
        buffer.append(Integer.toString(cookie.getVersion()));
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(cookie.getName());
        buffer.append("]");
        buffer.append("[value: ");
        buffer.append(cookie.getValue());
        buffer.append("]");
        buffer.append("[domain: ");
        buffer.append(cookie.getDomain());
        buffer.append("]");
        buffer.append("[path: ");
        buffer.append(cookie.getPath());
        buffer.append("]");
        buffer.append("[max-age: ");
        buffer.append(cookie.getMaxAge());
        buffer.append("]");
        return buffer.toString();

    }
}
