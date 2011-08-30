/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.support.metadata;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SAMLR2MetadataConstants.java 1372 2009-07-23 15:03:28Z chromy96 $
 */
public interface SAMLR2MetadataConstants {


    final static QName SPInitiatedSingleSignOnService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "SPInitiatedSingleSignOnService");

    final static QName SPInitiatedSingleLogoutService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "SPInitiatedSingleLogoutService");

    final static QName IDPInitiatedSingleLogoutService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "IDPInitiatedSingleLogoutService");
    
    final static QName SPInitiatedManageNameIDService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "SPInitiatedManageNameIDService");

    final static QName AssertIdentityWithSimpleAuthenticationService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "AssertIdentityWithSimpleAuthenticationService");

    final static QName SPSessionHeartBeatService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "SPSessionHeartBeatService");

    final static QName IDPSessionHeartBeatService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "IDPSessionHeartBeatService");

    final static QName SPBindingAssertionConsumerService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "AssertionConsumerService");

    final static QName SPCredentialsCallbackService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "CredentialsCallbackService");


    final static QName ManageNameIDService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "ManageNameIDService");
    final static QName AssertionConsumerService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AssertionConsumerService");
    final static QName ServiceName_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "ServiceName");
    final static QName SurName_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "SurName");
    final static QName Extensions_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "Extensions");
    final static QName EncryptionMethod_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "EncryptionMethod");
    final static QName AttributeConsumingService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AttributeConsumingService");
    final static QName AuthnQueryService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AuthnQueryService");
    final static QName OrganizationURL_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "OrganizationURL");
    final static QName EntitiesDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "EntitiesDescriptor");
    final static QName EmailAddress_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "EmailAddress");
    final static QName SPSSODescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "SPSSODescriptor");
    final static QName ArtifactResolutionService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "ArtifactResolutionService");
    final static QName Organization_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "Organization");
    final static QName OrganizationName_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "OrganizationName");
    final static QName SingleLogoutService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "SingleLogoutService");
    final static QName EntityDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "EntityDescriptor");
    final static QName AssertionIDRequestService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AssertionIDRequestService");
    final static QName AffiliationDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AffiliationDescriptor");
    final static QName NameIDFormat_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "NameIDFormat");
    final static QName ServiceDescription_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "ServiceDescription");
    final static QName KeyDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "KeyDescriptor");
    final static QName AuthzService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AuthzService");
    final static QName TelephoneNumber_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "TelephoneNumber");
    final static QName RequestedAttribute_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "RequestedAttribute");
    final static QName RoleDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "RoleDescriptor");
    final static QName ContactPerson_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "ContactPerson");
    final static QName AffiliateMember_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AffiliateMember");
    final static QName IDPSSODescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "IDPSSODescriptor");
    final static QName NameIDMappingService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "NameIDMappingService");
    final static QName Company_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "Company");
    final static QName PDPDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "PDPDescriptor");
    final static QName AttributeAuthorityDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AttributeAuthorityDescriptor");
    final static QName OrganizationDisplayName_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "OrganizationDisplayName");
    final static QName AdditionalMetadataLocation_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AdditionalMetadataLocation");
    final static QName SingleSignOnService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "SingleSignOnService");
    final static QName GivenName_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "GivenName");
    final static QName AttributeProfile_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AttributeProfile");
    final static QName AuthnAuthorityDescriptor_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AuthnAuthorityDescriptor");
    final static QName AttributeService_QNAME = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "AttributeService");

}
