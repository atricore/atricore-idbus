package org.atricore.idbus.capabilities.oauth2.client;

import org.atricore.idbus.common.oauth._2_0.protocol.SendPasswordlessLinkRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.SendPasswordlessLinkResponseType;
import org.atricore.idbus.common.oauth._2_0.protocol.TemplatePropertyType;

import java.util.Properties;

public class PasswordlessLinkClient extends AbstractWSClient {

    public PasswordlessLinkClient(String clientId, String clientSecret, String endpoint, String wsdlLocation) {
        super(clientId, clientSecret, endpoint, wsdlLocation);
    }

    /**
     *
     * @param username
     * @param targetSP
     * @param template Velocity template to build the message with the link
     * @throws Exception
     */
    public void sendPasswordlessLink(String username, String targetSP, String template, Properties templateProperties) throws Exception {

        SendPasswordlessLinkRequestType req = new SendPasswordlessLinkRequestType();
        req.setClientId(getClientId());
        req.setClientSecret(getClientSecret());
        req.setUsername(username);
        req.setTargetSP(targetSP);
        req.setTemplate(template);

        if (templateProperties != null) {
            while (templateProperties.propertyNames().hasMoreElements()) {
                String name = (String) templateProperties.propertyNames().nextElement();
                String value = templateProperties.getProperty(name);

                TemplatePropertyType prop = new TemplatePropertyType();
                prop.setName(name);
                prop.getValues().add(value);

                req.getProperties().add(prop);

            }

        }
        SendPasswordlessLinkResponseType res = getWsClient().sendPasswordlessLinkRequest(req);

    }

}
