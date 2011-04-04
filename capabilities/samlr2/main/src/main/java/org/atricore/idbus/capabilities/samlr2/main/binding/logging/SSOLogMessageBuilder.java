package org.atricore.idbus.capabilities.samlr2.main.binding.logging;

import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.common.sso._1_0.protocol.SSORequestAbstractType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SSOLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(SSOLogMessageBuilder.class);

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

        if (content instanceof SSORequestAbstractType) {
            return true;
        } else if (content instanceof SSOResponseType) {
            return true;
        }

        return false;
    }

    public String getType() {
        return "sso";
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


            if (content instanceof SSORequestAbstractType) {
                logMsg.append(XmlUtils.marshalSSORequest((SSORequestAbstractType) content, false));

            } else if (content instanceof SSOResponseType) {
                logMsg.append(XmlUtils.marshalSSOResponse((SSOResponseType) content, false));

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
