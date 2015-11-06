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

package org.atricore.idbus.capabilities.sso.support.binding;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOBinding.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public enum SSOBinding {
    
    /** URI for SAML 2 Artifact binding. [SAMLBinding 3.6]*/
    SAMLR2_ARTIFACT("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact", true),
    
    /** URI for SAML 2 POST binding. [SAMLBinding 3.5]*/
    SAMLR2_POST("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST", true),
    
    /** URI for SAML 2 HTTP redirect binding. [SAMLBinding 3.4]*/
    SAMLR2_REDIRECT("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", true),
    
    /** URI for SAML 2 SOAP binding. [SAMLBinding 3.2]*/
    SAMLR2_SOAP("urn:oasis:names:tc:SAML:2.0:bindings:SOAP", false),

    /** URI for SAML2 Reverse SOAP (PAOS) binding [SAMLBinding 3.3]*/
    SAMLR2_PAOS("urn:oasis:names:tc:SAML:2.0:bindings:PAOS", false),

    /** URI for SAML2 URI Binding (not really a binding) [SAMLBinding 3.7] */
    SAMLR2_URI("urn:oasis:names:tc:SAML:2.0:bindings:URI", false),

    /** Non-normative, only useful between local providers, for perfomrance issues */
    SAMLR2_LOCAL("urn:oasis:names:tc:SAML:2.0:bindings:LOCAL", false),

    // Extended SAMLR2 Bindings, because SAMLR2 is a core capability, we call this extensions IDBUS

    /** URI for IDBUS HTTP Artifact binding, this is NOT SAML Normative*/
    SSO_ARTIFACT("urn:org:atricore:idbus:sso:bindings:HTTP-Artifact", true),

    /** URI for IDBUS HTTP Redirect binding, this is NOT SAML Normative*/
    SSO_REDIRECT("urn:org:atricore:idbus:sso:bindings:HTTP-Redirect", true),

    /** URI for IDBUS HTTP Redirect binding, this is NOT SAML Normative*/
    SSO_POST("urn:org:atricore:idbus:sso:bindings:HTTP-POST", true),

    /** URI for IDBUS HTTP Redirect binding, this is NOT SAML Normative*/
    AJAX_POST("urn:org:atricore:idbus:sso:bindings:AJAX-POST", true),


    /** URI for IDBUS SOAP binding, this is NOT SAML Normtive */
    SSO_SOAP("urn:org:atricore:idbus:sso:bindings:SOAP", false),

    /** URI for IDBUS LOCAL binding, this is NOT SAML Normative */
    SSO_LOCAL("urn:org:atricore:idbus:sso:bindings:LOCAL", false),

    /** URI for SAML 2.0 IdP initiated bindings */
    SSO_IDP_INITIATED_SSO_HTTP_SAML2("urn:org:atricore:idbus:sso:bindings:SAML:2:0:IDP-Initiated-SSO-http", true),

    /** URI for SAML 2.0 IdP initiated bindings */
    SSO_PREAUTHN("urn:org:atricore:idbus:sso:bindings:HTTP-PreAuthn", true),

    /** URI for IDBUS HTTP Artifact binding, this is NOT SAML Normative*/
    SAMLR11_ARTIFACT("urn:oasis:names:tc:SAML:1.0:bindings:HTTP-Artifact", true),

    /** URI for SAML 2 SOAP binding. [SAMLBinding 3.2]*/
    SAMLR11_SOAP("urn:oasis:names:tc:SAML:1.1:bindings:SOAP", false),

    /** URI for SAML 1.1 IdP initiated endpoints */
    SSO_IDP_INITIATED_SSO_HTTP_SAML11("urn:org:atricore:idbus:sso:bindings:SAML:1:1:IDP-Initiated-SSO-http", true),

    ;

    private String binding;
    boolean frontChannel;

    SSOBinding(String binding, boolean frontChannel) {
        this.binding = binding;
        this.frontChannel = frontChannel;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public boolean isFrontChannel() {
        return frontChannel;
    }

    public static SSOBinding asEnum(String binding) {
        for (SSOBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid SSOBinding '" + binding + "'");
    }
}
