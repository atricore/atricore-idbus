package org.atricore.idbus.capabilities.atricoreid.as.main.binding.logging;

import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AtricoreIDRequestAbstractType;
import org.atricore.idbus.capabilities.atricoreid._1_0.protocol.AtricoreIDResponseAbstractType;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;


import org.atricore.idbus.capabilities.atricoreid.common.util.XmlUtils;
/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(AtricoreIDLogMessageBuilder.class);

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

        if (content instanceof AtricoreIDRequestAbstractType) {
            return true;
        } else if (content instanceof AtricoreIDResponseAbstractType) {
            return true;
        }

        return false;
    }

    public String getType() {
        return "atricoreid";
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


            if (content instanceof AtricoreIDRequestAbstractType) {
                logMsg.append(XmlUtils.marshalAtricoreIDRequest((AtricoreIDRequestAbstractType) content, false));

            } else if (content instanceof AtricoreIDResponseAbstractType) {
                logMsg.append(XmlUtils.marshalAtricoreIDResponse((AtricoreIDResponseAbstractType) content, false));

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
