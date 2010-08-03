package org.atricore.idbus.capabilities.josso.main.binding.logging;

import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.util.XmlUtils;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.LogMessageBuilder;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JossoLogMessageBuilder implements LogMessageBuilder {

    private static final Log logger = LogFactory.getLog(JossoLogMessageBuilder.class);

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

        return content.getClass().getPackage().getName().equals("org.josso.gateway.ws._1_2.protocol");

    }

    public String getType() {
        return "josso";
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


            // TODO : Implement JOSSO SOAP Messages XML Utils :
            logMsg.append(XmlUtils.marshall(content, false));

            return logMsg.toString();

        } catch (Exception e) {
            logger.error("Cannot generate mediation log message: " + e.getMessage(), e);
            return null;
        }
    }
}
