package org.atricore.idbus.capabilities.sso.support;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SSOMessagingConstants {

    static final QName SERVICE_NAME = new QName("urn:org:atricore:idbus:common:sso:1.0:wsdl", "SSOService");

    static final QName PORT_NAME  = new QName("urn:org:atricore:idbus:common:sso:1.0:wsdl", "soap");

}
