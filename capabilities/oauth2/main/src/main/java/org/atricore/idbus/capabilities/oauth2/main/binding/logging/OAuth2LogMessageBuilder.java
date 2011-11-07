package org.atricore.idbus.capabilities.oauth2.main.binding.logging;

import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthRequestAbstractType;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthResponseAbstractType;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;


import org.atricore.idbus.capabilities.oauth2.common.util.XmlUtils;
/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2LogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(OAuth2LogMessageBuilder.class);

    public boolean canHandle(Message message) {
        if (!(message instanceof CamelMediationMessage))
            return false;

        CamelMediationMessage oauthMsg = (CamelMediationMessage) message;
        if (oauthMsg.getMessage() == null) {
            logger.trace("No message found in mediation message : " + oauthMsg.getMessageId());
            return false;
        }

        Object content = oauthMsg.getMessage().getContent();
        if (content == null) {
            logger.trace("No message content found in mediation message : " + oauthMsg.getMessageId());
            return false;
        }

        if (content instanceof OAuthRequestAbstractType) {
            return true;
        } else if (content instanceof OAuthResponseAbstractType) {
            return true;
        }

        return false;
    }

    public String getType() {
        return "oauth2";
    }

    public String buildLogMessage(Message message) {
        try {
            StringBuffer logMsg = new StringBuffer();
            CamelMediationMessage oauthMsg = (CamelMediationMessage) message;

            if (oauthMsg.getMessage() == null) {
                logger.warn("No message found in mediation message : " + oauthMsg.getMessageId());
                return null;
            }

            Object content = oauthMsg.getMessage().getContent();


            if (content instanceof OAuthRequestAbstractType) {
                logMsg.append(XmlUtils.marshalOAuth2Request((OAuthRequestAbstractType) content, false));

            } else if (content instanceof OAuthResponseAbstractType) {
                logMsg.append(XmlUtils.marshalOAuth2Response((OAuthResponseAbstractType) content, false));

            } else if (content == null) {
                logger.debug("No Message content");
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
