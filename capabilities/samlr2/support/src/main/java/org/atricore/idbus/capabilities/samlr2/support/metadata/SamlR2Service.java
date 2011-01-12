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
 * SAML 2.0 Services realized by several roles.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Service.java 1372 2009-07-23 15:03:28Z chromy96 $
 */
public enum SamlR2Service {

    SingleSignOnService(SAMLR2MetadataConstants.SingleSignOnService_QNAME),

    SingleLogoutService(SAMLR2MetadataConstants.SingleLogoutService_QNAME),

    AssertionConsumerService(SAMLR2MetadataConstants.AssertionConsumerService_QNAME),

    ArtifactResolutionService(SAMLR2MetadataConstants.ArtifactResolutionService_QNAME),

    AttributeService(SAMLR2MetadataConstants.AttributeService_QNAME),

    AssertionIDRequestService(SAMLR2MetadataConstants.AssertionIDRequestService_QNAME),

    AuthzService(SAMLR2MetadataConstants.AuthzService_QNAME),

    AuthnQueryService(SAMLR2MetadataConstants.AuthnQueryService_QNAME),

    /**
     * This service is an extension to SAML 
     */
    SPInitiatedSingleSignOnService(SAMLR2MetadataConstants.SPInitiatedSingleSignOnService_QNAME),

    SPInitiatedSingleLogoutService(SAMLR2MetadataConstants.SPInitiatedSingleLogoutService_QNAME),

    IDPInitiatedSingleLogoutService(SAMLR2MetadataConstants.IDPInitiatedSingleLogoutService_QNAME),

    AssertIdentityWithSimpleAuthenticationService(SAMLR2MetadataConstants.AssertIdentityWithSimpleAuthenticationService_QNAME),

    SPSessionHeartBeatService(SAMLR2MetadataConstants.SPSessionHeartBeatService_QNAME),

    IDPSessionHeartBeatService(SAMLR2MetadataConstants.IDPSessionHeartBeatService_QNAME),
    
    ManageNameIDService(SAMLR2MetadataConstants.ManageNameIDService_QNAME),
    
    SPInitiatedManageNameIDService(SAMLR2MetadataConstants.SPInitiatedManageNameIDService_QNAME);

    private QName qname;


    SamlR2Service(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    SamlR2Service(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }


    public static SamlR2Service asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";

        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static SamlR2Service asEnum(QName qname) {
        for (SamlR2Service et : values()) {
            if (et.getQname().equals(qname))
                return et;
        }

        throw new IllegalArgumentException("Invalid endpoint type: " + qname);
    }

    @Override
    public String toString() {
        return qname.toString();
    }
}

