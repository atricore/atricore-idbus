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

package org.atricore.idbus.capabilities.sso.main.binding.logging;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 * @org.apache.xbean.XBean element="samlr2-logmsg-builder"
 */
public class SamlR2LogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(SamlR2LogMessageBuilder.class);

    public boolean canHandle(Message message) {

        if (!(message instanceof CamelMediationMessage))
            return false;

        CamelMediationMessage samlMsg = (CamelMediationMessage) message;
        if (samlMsg.getMessage() == null) {
            logger.trace("No message found in mediation message : " + samlMsg.getMessageId());
            return false;
        }

        Object content = samlMsg.getMessage().getContent();
        if (content == null) {
            logger.trace("No message content found in mediation message : " + samlMsg.getMessageId());
            return false;
        }

        if (content instanceof RequestAbstractType) {
            return true;
        } else if (content instanceof StatusResponseType) {
            return true;
        } else if (content instanceof SSOCredentialClaimsRequest) {
            return true;
        } else if (content instanceof SSOCredentialClaimsResponse) {
            return true;
        } else if (content instanceof oasis.names.tc.saml._1_0.protocol.ResponseType) {
            return true;
        }

        return false;

    }

    public String getType() {
        return "saml2";
    }

    public String buildLogMessage(Message message) {

        try {
            StringBuffer logMsg = new StringBuffer();
            CamelMediationMessage samlMsg = (CamelMediationMessage) message;

            if (samlMsg.getMessage() == null) {
                logger.warn("No message found in mediation message : " + samlMsg.getMessageId());
                return null;
            }

            Object content = samlMsg.getMessage().getContent();

            if (content instanceof RequestAbstractType) {
                logMsg.append(XmlUtils.marshalSamlR2Request((RequestAbstractType) content, false));

            } else if (content instanceof StatusResponseType) {
                logMsg.append(XmlUtils.marshalSamlR2Response((StatusResponseType) content, false));

            } else if (content instanceof SSOCredentialClaimsRequest) {

                logMsg.append("SSOClaimsRequest>:");
                logMsg.append(content.toString());


            } else if (content instanceof SSOCredentialClaimsResponse) {
                logMsg.append(" SSOClaimsResponse:");
                logMsg.append(content.toString());


            } else if (content instanceof oasis.names.tc.saml._1_0.protocol.ResponseType) {
                logMsg.append(XmlUtils.marshalSamlR11Response((oasis.names.tc.saml._1_0.protocol.ResponseType) content, false));
            } else if (content == null) {
                logger.warn("No Message content");
            } else {
                logger.warn("Unknown Message content " + content);
            }

            return logMsg.toString();

        } catch (Exception e) {
            logger.error("Cannot generate mediation log message: " + e.getMessage(), e);
            return null;
        }
    }
}
