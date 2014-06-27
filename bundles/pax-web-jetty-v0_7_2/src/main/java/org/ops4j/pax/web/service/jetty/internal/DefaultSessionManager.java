package org.ops4j.pax.web.service.jetty.internal;

import org.mortbay.jetty.HttpOnlyCookie;
import org.mortbay.jetty.servlet.HashSessionManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * Created by sgonzalez on 6/26/14.
 */
public class DefaultSessionManager extends HashSessionManager {

    public Cookie getSessionCookie(HttpSession session, String contextPath, boolean requestIsSecure)
    {
        if (isUsingCookies())
        {
            String id = getNodeId(session);
            Cookie cookie=getHttpOnly()?new HttpOnlyCookie(_sessionCookie,id):new Cookie(_sessionCookie,id);

            cookie.setPath((contextPath==null||contextPath.length()==0)?"/":contextPath);
            cookie.setMaxAge(getMaxCookieAge());
            cookie.setSecure(requestIsSecure&&getSecureCookies());

            // set up the overrides
            if (_sessionDomain!=null)
                cookie.setDomain(_sessionDomain);
            if (_sessionPath!=null)
                cookie.setPath(_sessionPath);

            return cookie;
        }
        return null;
    }
}
