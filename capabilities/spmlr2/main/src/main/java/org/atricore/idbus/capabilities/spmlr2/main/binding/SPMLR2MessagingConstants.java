package org.atricore.idbus.capabilities.spmlr2.main.binding;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SPMLR2MessagingConstants {

    // TODO : REMFACTOR!
    static final String SPMLR2_INBOUND_MSG = "urn:org:atricore:idbus:spmlr2:inbound-msg";

    static final QName SERVICE_NAME = new QName("urn:oasis:names:tc:SPML:2:0:wsdl", "SPMLService");

    static final QName PORT_NAME  = new QName("urn:oasis:names:tc:SPML:2:0:wsdl", "soap");

}
