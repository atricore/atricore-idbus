package org.atricore.idbus.capabilities.atricoreid.common;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface AtricoreIDMessagingConstants {

    static final QName SERVICE_NAME = new QName("urn:org:atricore:idbus:common:oauth:2.0:wsdl", "AtricoreIDService");

    static final QName PORT_NAME  = new QName("urn:org:atricore:idbus:common:oauth:2.0:wsdl", "soap");

}
