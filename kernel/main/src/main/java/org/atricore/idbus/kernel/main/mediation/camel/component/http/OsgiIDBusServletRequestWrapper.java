package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by sgonzalez on 6/26/14.
 */
public class OsgiIDBusServletRequestWrapper extends HttpServletRequestWrapper {

    private boolean secureCookies = false;

    public OsgiIDBusServletRequestWrapper(HttpServletRequest request) {
        this(request, false);
    }

    public OsgiIDBusServletRequestWrapper(HttpServletRequest request, boolean secureCookies) {
        super(request);
        this.secureCookies = secureCookies;
    }

    @Override
    public boolean isSecure() {
        boolean s1 = getRequest().isSecure();
        String s2 = getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_SECURE);

        return secureCookies || s1 ||  s2 != null;
    }
}