package org.atricore.idbus.capabilities.oauth2.common;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface OAuthMessagingConstants {

    static final QName SERVICE_NAME = new QName("urn:org:atricore:idbus:common:oauth:2.0:wsdl", "OAuthService");

    static final QName PORT_NAME  = new QName("urn:org:atricore:idbus:common:oauth:2.0:wsdl", "soap");

}
