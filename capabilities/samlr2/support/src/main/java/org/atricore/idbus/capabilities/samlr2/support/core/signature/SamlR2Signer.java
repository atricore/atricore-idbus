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

package org.atricore.idbus.capabilities.samlr2.support.core.signature;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.metadata.RoleDescriptorType;
import oasis.names.tc.saml._2_0.protocol.LogoutRequestType;
import oasis.names.tc.saml._2_0.protocol.ManageNameIDRequestType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.w3c.dom.Document;

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
public interface SamlR2Signer {

    /**
     * Signs a SAMLR2 Assertion
     */
    AssertionType sign(AssertionType assertion) throws SamlR2SignatureException;

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
    RequestAbstractType sign(RequestAbstractType request) throws SamlR2SignatureException;

    /**
     * @param response the SAML 2.0 Response
     * @return the signed response
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    StatusResponseType sign(StatusResponseType response) throws SamlR2SignatureException;

    /**
     * @param md       The signer SAML 2.0 Metadata
     * @param response The signed SAML 2.0 Response
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, StatusResponseType response) throws SamlR2SignatureException, SamlR2SignatureValidationException;

    /**
     * @param md       The signer SAML 2.0 Metadata
     * @param request The signed SAML 2.0 Response
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, LogoutRequestType request) throws SamlR2SignatureException, SamlR2SignatureValidationException;


    /**
     * @param md                  The signer SAML 2.0 Metadata
     * @param manageNameIDRequest The signed SAML 2.0 Manage Name ID Request
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException;

    /**
     * @param md     The signer SAML 2.0 Metadata
     * @param domStr The signed SAML 2.0 element, serialized
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, String domStr) throws SamlR2SignatureException;

    /**
     * @param md  The signer SAML 2.0 Metadata
     * @param dom The signed SAML 2.0 element, DOM.
     * @throws SamlR2SignatureValidationException
     *          if the assertion signature is invalid
     */
    void validate(RoleDescriptorType md, Document dom) throws SamlR2SignatureException;

    /**
     * @param manageNameIDRequest the Manager Name ID Request
     * @return the signed request
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    ManageNameIDRequestType sign(ManageNameIDRequestType manageNameIDRequest) throws SamlR2SignatureException;

    // --------------------------------------------------------< SAML 1.1 >

    // TODO: Unify all signature-specific behavior in one single component or separate in different classes.

    /**
     * SAML 1.1
     *
     * @param response the SAML 1.1 response
     * @return the signed response
     * @throws SamlR2SignatureException if an error occurs when signing.
     */
    oasis.names.tc.saml._1_0.protocol.ResponseType sign(oasis.names.tc.saml._1_0.protocol.ResponseType response)
            throws SamlR2SignatureException;


}
