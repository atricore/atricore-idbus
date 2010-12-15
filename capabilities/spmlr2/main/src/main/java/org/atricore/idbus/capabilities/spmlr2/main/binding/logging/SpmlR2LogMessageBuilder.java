package org.atricore.idbus.capabilities.spmlr2.main.binding.logging;

import oasis.names.tc.spml._2._0.RequestType;
import oasis.names.tc.spml._2._0.ResponseType;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.util.XmlUtils;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2LogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(SpmlR2LogMessageBuilder.class);

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

        if (content instanceof RequestType) {
            return true;
        } else if (content instanceof ResponseType) {
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

            if (content instanceof RequestType) {
                logMsg.append(XmlUtils.marshallSpmlR2Request((RequestType) content, false));

            } else if (content instanceof ResponseType) {
                logMsg.append(XmlUtils.marshallSpmlR2Response((ResponseType) content, false));

            } else if (content == null) {
                logger.warn("No Message content");
            } else {
                // TODO : Support Atricore SSO Messages
                logger.warn("Unknown Message content " + content);
            }

            return logMsg.toString();

        } catch (Exception e) {
            logger.error("Cannot generate mediation log message: " + e.getMessage(), e);
            return null;
        }
    }
}

