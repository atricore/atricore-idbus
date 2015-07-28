package org.atricore.idbus.capabilities.openidconnect.main.common;

import javax.xml.namespace.QName;

/**
 * Created by sgonzalez on 3/11/14.
 */
public interface OpenIDConnectConstants {


    // Open ID Connect Identity Provider Services
    final static QName AuthorizationService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "AuthorizationService");

    final static QName TokenService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "TokenService");

    final static QName SSOAssertionConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "AssertionConsumerService");

    final static QName SSOSingleSignOnService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "SSOSingleSignOnService");

    final static QName SSOSingleLogoutService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "SSOSingleLogoutService");

    // Open ID Connect Relaying Party Services
    final static QName AuthorizationConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:rp", "AuthorizationConsumerService");

    final static QName TokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:rp", "TokenConsumerService");

    // Open ID Connect Identity Provider Proxy Services

    final static QName GoogleAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "GoogleAuthzTokenConsumerService");

    final static QName FacebookAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "FacebookAuthzTokenConsumerService");

    final static QName AuthzCodeProviderService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "AuthzCodeProviderService");

    final static QName IDPSSODescriptor_QNAME = new QName("urn:openidconnect:1.0", "IDPDescriptor");

    final static QName SPSSODescriptor_QNAME = new QName("urn:openidconnect:1.0", "RelayingParty");
}
