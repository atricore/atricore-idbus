/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.camel.Message;
import org.apache.camel.component.http.DefaultHttpBinding;
import org.apache.camel.component.http.HttpMessage;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is actually a CAMEL HTTP Binding extension, it's not related with mediation HTTP bindings
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IDBusHttpBinding extends DefaultHttpBinding {

    private static final Log logger = LogFactory.getLog(IDBusHttpBinding.class);

    public IDBusHttpBinding() {
        super();
    }

    public IDBusHttpBinding(HeaderFilterStrategy headerFilterStrategy) {
        super(headerFilterStrategy);
    }

    @Override
    public void readRequest(HttpServletRequest httpServletRequest, HttpMessage httpMessage) {

        if (logger.isTraceEnabled())
            logger.trace("Reading HTTP Servlet Request");

        super.readRequest(httpServletRequest, httpMessage);

        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {

                if (logger.isDebugEnabled())
                    logger.debug("Setting IDBus Cookie header for " + cookie.getName() + "=" + cookie.getValue());

                httpMessage.getHeaders().put("org.atricore.idbus.http.Cookie." + cookie.getName(), cookie.getValue());
            }
        }

        // Export additional information in CAMEL headers
        httpMessage.getHeaders().put("org.atricore.idbus.http.RequestURL", httpServletRequest.getRequestURL().toString());
        httpMessage.getHeaders().put("org.atricore.idbus.http.QueryString", httpServletRequest.getQueryString());

        String remoteAddr = null;
        String remoteHost = null;

        if (httpServletRequest.getAttribute("org.atricore.idbus.http.SecureCookies") != null)
            httpMessage.getHeaders().put("org.atricore.idbus.http.SecureCookies", httpServletRequest.getAttribute("org.atricore.idbus.http.SecureCookies"));

        String parentThread = httpServletRequest.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_PROXIED_REQUEST);
        if (parentThread == null) {
            remoteAddr = httpServletRequest.getRemoteAddr();
            remoteHost = httpServletRequest.getRemoteHost();
            if (logger.isTraceEnabled())
                logger.trace("Using request remote address/host : ["+remoteAddr+"/" + remoteHost + "]");
        } else {
            remoteAddr = httpServletRequest.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_REMOTE_ADDRESS);
            remoteHost = httpServletRequest.getHeader(IDBusHttpConstants.HTTP_HEADER_IDBUS_REMOTE_HOST);
            if (logger.isTraceEnabled())
                logger.trace("Using X-IdBus header remote address/host : ["+remoteAddr+"/" + remoteHost + "]");
        }

        httpMessage.getHeaders().put("org.atricore.idbus.http.RemoteAddress", remoteAddr);
        httpMessage.getHeaders().put("org.atricore.idbus.http.RemoteHost", remoteHost);

        if (logger.isDebugEnabled())
            logger.debug("Publishing HTTP Session as Camel header org.atricore.idbus.http.HttpSession");

        httpMessage.getHeaders().put("org.atricore.idbus.http.HttpSession", httpServletRequest.getSession(true));
    }

    @Override
    public void doWriteResponse(Message message, HttpServletResponse httpServletResponse) throws IOException {

        if (logger.isDebugEnabled())
            logger.debug("Writing HTTP Servlet Response");

        // append headers
        for (String key : message.getHeaders().keySet()) {

            String value = message.getHeader(key, String.class);

            if (getHeaderFilterStrategy() != null && getHeaderFilterStrategy().applyFilterToCamelHeaders(key, value)) {

                // This is a filtered header ... check if is a josso 'set cookie'
                if (key.startsWith("org.atricore.idbus.http.Set-Cookie.")) {
                    
                    String cookieName = key.substring("org.atricore.idbus.http.Set-Cookie.".length());
                    if (!cookieName.equals("JSESSIONID")) {

                        // TODO : Avoid setting the same cookie over and over ...
                        if (logger.isDebugEnabled())
                            logger.debug("Setting HTTP Cookie " + cookieName + "=" + value);

                        httpServletResponse.addHeader("Set-Cookie",cookieName + "=" + value);
                    }
                }
            }

        }

        if (logger.isTraceEnabled())
            logger.trace("Writing HTTP Servlet Response");

        super.doWriteResponse(message, httpServletResponse);
    }
}

