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

package org.atricore.idbus.capabilities.sso.main.binding.services;

import oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2ServiceImpl.java 1352 2009-07-10 13:16:03Z sgonzalez $
 */
@javax.jws.WebService(
                      serviceName = "SAMLService",
                      portName = "soap",
                      targetNamespace = "urn:oasis:names:tc:SAML:2.0:wsdl",
                      endpointInterface = "oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType")

public class SamlR2ServiceImpl implements SAMLRequestPortType {

    private static final Logger LOG = Logger.getLogger(SamlR2ServiceImpl.class.getName());

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlLogoutRequest(oasis.names.tc.saml._2_0.protocol.LogoutRequestType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.StatusResponseType samlLogoutRequest(oasis.names.tc.saml._2_0.protocol.LogoutRequestType body) {
        LOG.info("Executing operation samlLogoutRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlArtifactResolveRequest(oasis.names.tc.saml._2_0.protocol.ArtifactResolveType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ArtifactResponseType samlArtifactResolve(oasis.names.tc.saml._2_0.protocol.ArtifactResolveType body) {
        LOG.info("Executing operation samlArtifactResolveRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlAttributeQueryRequest(oasis.names.tc.saml._2_0.protocol.AttributeQueryType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ResponseType samlAttributeQueryRequest(oasis.names.tc.saml._2_0.protocol.AttributeQueryType body) {
        LOG.info("Executing operation samlAttributeQueryRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlAuthnRequest(oasis.names.tc.saml._2_0.protocol.AuthnRequestType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ResponseType samlAuthnRequest(oasis.names.tc.saml._2_0.protocol.AuthnRequestType body) {
        LOG.info("Executing operation samlAuthnRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlNameIDMappingRequest(oasis.names.tc.saml._2_0.protocol.NameIDMappingRequestType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.NameIDMappingResponseType samlNameIDMappingRequest(oasis.names.tc.saml._2_0.protocol.NameIDMappingRequestType body) {
        LOG.info("Executing operation samlNameIDMappingRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlAuthzDecisionQueryRequest(oasis.names.tc.saml._2_0.protocol.AuthzDecisionQueryType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ResponseType samlAuthzDecisionQueryRequest(oasis.names.tc.saml._2_0.protocol.AuthzDecisionQueryType body) {
        LOG.info("Executing operation samlAuthzDecisionQueryRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlAssertionIDRequest(oasis.names.tc.saml._2_0.protocol.AssertionIDRequestType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ResponseType samlAssertionIDRequest(oasis.names.tc.saml._2_0.protocol.AssertionIDRequestType body) {
        LOG.info("Executing operation samlAssertionIDRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlManageNameIDRequest(oasis.names.tc.saml._2_0.protocol.ManageNameIDRequestType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.StatusResponseType samlManageNameIDRequest(oasis.names.tc.saml._2_0.protocol.ManageNameIDRequestType body) {
        LOG.info("Executing operation samlManageNameIDRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    /* (non-Javadoc)
     * @see oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType#samlAuthnQueryRequest(oasis.names.tc.saml._2_0.protocol.AuthnQueryType  body )*
     */
    public oasis.names.tc.saml._2_0.protocol.ResponseType samlAuthnQueryRequest(oasis.names.tc.saml._2_0.protocol.AuthnQueryType body) {
        LOG.info("Executing operation samlAuthnQueryRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }


    public oasis.names.tc.saml._2_0.protocol.ResponseType samlSecTokenAuthnRequest(oasis.names.tc.saml._2_0.idbus.SecTokenAuthnRequestType body) {
        LOG.info("Executing operation samlSecTokenAuthnRequest");
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }
}
