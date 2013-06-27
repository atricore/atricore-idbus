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

package org.atricore.idbus.kernel.main.mediation.camel.component.logging;

import org.apache.camel.Message;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.component.http.HttpMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @org.apache.xbean.XBean element="http-logmsg-builder"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class HttpLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(HttpLogMessageBuilder.class);

    public boolean canHandle(Message message) {
        return message.getExchange() instanceof HttpExchange;
    }

    public String getType() {
        return "http";
    }

    public String buildLogMessage(Message message) {

        HttpExchange httpEx = (HttpExchange) message.getExchange();

        

        if (message instanceof HttpMessage) {
            
            HttpServletRequest hreq = httpEx.getRequest();

            StringBuffer logMsg = new StringBuffer(1024);

            logMsg.append("<http-request method=\"").append(hreq.getMethod()).append("\"").
                    append("\n\t url=\"").append(hreq.getRequestURL()).append("\"").
                    append("\n\t content-type=\"").append(hreq.getContentType()).append("\"").
                    append("\n\t content-length=\"").append(hreq.getContentLength()).append("\"").
                    append("\n\t content-encoding=\"").append(hreq.getCharacterEncoding()).append("\"").
                    append(">");

            Enumeration headerNames = hreq.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                Enumeration headers = hreq.getHeaders(headerName);

                logMsg.append("\n\t<header name=\"").append(headerName).append("\">");

                while (headers.hasMoreElements()) {
                    String headerValue = (String) headers.nextElement();
                    logMsg.append("\n\t\t<header-value>").append(headerValue).append("</header-value>");
                }

                logMsg.append("\n\t</header>");

            }

            Enumeration params = hreq.getParameterNames();
            while (params.hasMoreElements()) {
                String param = (String) params.nextElement();
                logMsg.append("\n\t<parameter name=\"").append(param).append("\">");
                logMsg.append("\n\t\t\t<value>").append(hreq.getParameter(param)).append("</value>");
                logMsg.append("\n\t</parameter>");
            }

            logMsg.append("\n</http-request>");

            return logMsg.toString();
        } else {
            StringBuffer logMsg = new StringBuffer(1024);
            logMsg.append("\t<http-response />");
            return logMsg.toString();
        }
    }
}
