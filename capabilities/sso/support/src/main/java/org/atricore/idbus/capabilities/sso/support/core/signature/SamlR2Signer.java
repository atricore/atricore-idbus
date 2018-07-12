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

package org.atricore.idbus.capabilities.sso.support.core.signature;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.Serializable;

/**
 * <p>
 * This can digitally sign and verify SAMLR Assertions, Requests and Reponses signatures.
 * </p>
 * <p>
 * See <strong>SAML 2.0 Core, section 5</strong> for further references.
 * </p>
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SamlR2Signer extends Serializable {

    /**
     * Signs a SAMLR2 Assertion
     */
    AssertionType sign(AssertionType assertion, String digest) throws SamlR2SignatureException;

    /**
     * @param md        The signer SAML 2.0 Metadata
     * @param assertion The signed SAML 2.0 Assertion
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, AssertionType assertion) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     * @param request the SAML 2.0 Request
     * @return the signed request
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    RequestAbstractType sign(RequestAbstractType request, String digest) throws SamlR2SignatureException;

    /**
     * @param response the SAML 2.0 Response
     * @return the signed response
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    StatusResponseType sign(StatusResponseType response, String element, String digest) throws SamlR2SignatureException;

    /**
     * @param queryString  the SAML 2.0 Query string (HTTP-Redirect binding)
     * @return
     * @throws SamlR2SignatureException
     */
    String signQueryString(String queryString, String digest) throws SamlR2SignatureException;

    /**
     * @param md       The signer SAML 2.0 Metadata
     * @param response The signed SAML 2.0 Response
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, StatusResponseType response, String element) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     * @param md       The signer SAML 2.0 Metadata
     * @param request The signed SAML 2.0 Response
     * @throws SamlR2SignatureValidationException if the signature is invalid
     */
    void validate(RoleDescriptorType md, LogoutRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     * @param md       The signer SAML 2.0 Metadata
     * @param request The signed SAML 2.0 Response
     * @throws SamlR2SignatureValidationException if the signature is invalid
     */
    void validate(RoleDescriptorType md, AuthnRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     *
     * @param md The signer SAML 2.0 Metadata
     * @param queryString The signed SAML 2.0 Query String , URL Encoded (HTTP-Redirect binding)
     * @throws SamlR2SignatureException
     * @throws SamlR2SignatureValidationException if the signature is invalid
     */
    void validateQueryString(RoleDescriptorType md, String queryString) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     *
     @param md The signer SAML 2.0 Metadata
     * @param msg the SAML Msg
     * @param relayState
     * @param sigAlg
     * @param signature non URL Encoded
     * @param isResponse non URL Encoded
     * @throws SamlR2SignatureException
     * @throws SamlR2SignatureValidationException
     */
    void validateQueryString(RoleDescriptorType md, String msg, String relayState, String sigAlg, String signature, boolean isResponse) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     * @param md                  The signer SAML 2.0 Metadata
     * @param manageNameIDRequest The signed SAML 2.0 Manage Name ID Request
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException;

    /**
     * @param md     The signer SAML 2.0 Metadata
     * @param domStr The signed SAML 2.0 document, serialized
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validateDom(RoleDescriptorType md, String domStr) throws SamlR2SignatureException;

    /**
     * @param md     The signer SAML 2.0 Metadata
     * @param domStr The signed SAML 2.0 document, serialized
     * @param elementId The dom element's ID
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validateDom(RoleDescriptorType md, String domStr, String elementId) throws SamlR2SignatureException;

    /**
     * @param md     The signer SAML 2.0 Metadata
     * @param doc The signed SAML 2.0 DOM document, serialized
     * @param elementId The dom element's ID
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validateDom(RoleDescriptorType md, Document doc, String elementId) throws SamlR2SignatureException;

    /**
     * @param md  The signer SAML 2.0 Metadata
     * @param dom The signed SAML 2.0 document, DOM.
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, Document dom) throws SamlR2SignatureException;

    /**
     * @param md  The signer SAML 2.0 Metadata
     * @param dom The signed SAML 2.0 document, DOM.
     * @param root The signed SAML 2.0 element, DOM.
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, Document dom, Node root) throws SamlR2SignatureException;

    /**
     * @param manageNameIDRequest the Manager Name ID Request
     * @return the signed request
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    ManageNameIDRequestType sign(ManageNameIDRequestType manageNameIDRequest, String digest) throws SamlR2SignatureException;

    // --------------------------------------------------------< SAML 1.1 >

    // TODO: Unify all signature-specific behavior in one single component or separate in different classes.

    /**
     * SAML 1.1
     *
     * @param response the SAML 1.1 response
     * @return the signed response
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    oasis.names.tc.saml._1_0.protocol.ResponseType sign(oasis.names.tc.saml._1_0.protocol.ResponseType response, String digest)
            throws SamlR2SignatureException;


}
