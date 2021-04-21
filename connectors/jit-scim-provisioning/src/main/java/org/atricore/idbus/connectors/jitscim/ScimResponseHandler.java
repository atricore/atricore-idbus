/*
 * Atricore IDBus
 *
 * Copyright (c) 2016, Atricore Inc.
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
package org.atricore.idbus.connectors.jitscim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.client.ClientRequest;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.handlers.ClientHandler;
import org.apache.wink.client.handlers.HandlerContext;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMConstants;

/**
 * Manages the RESTful message exchange between the SCIM client and server.
 */
public class ScimResponseHandler implements ClientHandler {

    private static final Log logger = LogFactory.getLog(ScimResponseHandler.class);

    ExtendedSCIMClient scimClient = null;

    public void setSCIMClient(ExtendedSCIMClient scimClient) {
        this.scimClient = scimClient;
    }

    @Override
    public ClientResponse handle(ClientRequest clientRequest, HandlerContext handlerContext)
            throws Exception {

        //obtain client response
        ClientResponse cr = handlerContext.doChain(clientRequest);
        if (scimClient != null) {
            //see whether the response indicates a failure or success according to the status code
            logger.debug("Status code [" + cr.getStatusCode() + "], message is [" + cr.getMessage() + "]");
            if (!(scimClient.evaluateResponseStatus(cr.getStatusCode()))) {
                //if it is a failure,
                AbstractCharonException charonException = null;
                try {
                    if (cr.getHeaders().getFirst(SCIMConstants.CONTENT_TYPE_HEADER) != null) {

                        String format = SCIMConstants.identifyFormat(cr.getHeaders().getFirst(SCIMConstants.CONTENT_TYPE_HEADER));
                        if (format != null) {
                            charonException = scimClient.decodeSCIMException(cr.getEntity(String.class), format);
                            logger.debug("Decoded exception is [" + charonException + "]");
                        } else {
                            charonException = new CharonException(cr.getEntity(String.class));
                        }
                    } else {
                        charonException = new CharonException(cr.getEntity(String.class));
                    }
                } catch (CharonException e) {
                    logger.error(e);
                }
                if (charonException != null) {
                    throw charonException;
                }
            } else {
                return cr;
            }
        }
        return cr;
    }
}
