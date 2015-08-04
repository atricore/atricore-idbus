package org.atricore.idbus.capabilities.openidconnect.main.op.binding.logging;

import com.nimbusds.oauth2.sdk.Request;
import com.nimbusds.oauth2.sdk.Response;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

/**
 * 
 */
public class OpenIDConnectLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(OpenIDConnectLogMessageBuilder.class);

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

        if (content instanceof Request) {
            return true;
        } else if (content instanceof Response) {
            return true;
        }

        return false;
    }

    public String getType() {
        return "openid-connect";
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

            if (content instanceof Request) {
                Request r = (Request) content;
                // TODO : Format for log
                logMsg.append(r.toString());

            } else if (content instanceof Response) {
                Response r = (Response) content;
                // TODO : Format for log
                logMsg.append(r.toString());

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


