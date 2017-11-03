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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

import java.util.Map;

/**
 * @org.apache.xbean.XBean element="camel-logmsg-builder"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CamelLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(CamelLogMessageBuilder.class);

    public boolean canHandle(Message message) {
        return true;
    }

    public String getType() {
        return "camel";
    }

    public String buildLogMessage(Message message) {

        StringBuffer logMsg = new StringBuffer(1024);

        // CAREFUL! Camel will eagerly create message parts if you try to access them and are not yet initialized!

        logMsg.append(" camel-message-id=\"").append(message.getMessageId()).append("\" ").
                append("class=\"").append(message.getClass().getSimpleName()).append("\" ").
                append("exchange-id=\"").append(message.getExchange().getExchangeId()).append("\" ").
                append("exchange-class=\"").append(message.getExchange().getClass().getSimpleName()).append("\" ");

        Map<String, Object> headers  =message.getHeaders();
        for (String header : headers.keySet()) {
            logMsg.append(" header-name=\"").append(header).append("\"");
            logMsg.append(" header-value=\"").append(headers.get(header)).append("\"");
        }

        if (message.hasAttachments()) {
            for (String attachment : message.getAttachmentNames()) {
                logMsg.append(" attachment-name=\"").append(attachment).append("\"");
            }
        }

        return logMsg.toString();
    }
}
