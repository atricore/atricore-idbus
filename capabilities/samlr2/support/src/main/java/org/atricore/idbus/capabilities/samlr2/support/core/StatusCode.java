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

package org.atricore.idbus.capabilities.samlr2.support.core;

/**
 * The <StatusCode> element specifies a code or a set of nested codes representing the status of the
 * corresponding request.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum StatusCode {

    // Status codes
    TOP_SUCCESS
            ("urn:oasis:names:tc:SAML:2.0:status:Success", "Success"),
    TOP_REQUESTER
            ("urn:oasis:names:tc:SAML:2.0:status:Requester","The request could not be performed due to an error on the part of the requester"),
    TOP_RESPONDER
            ("urn:oasis:names:tc:SAML:2.0:status:Responder", "The request could not be performed due to an error on the part of the SAML responder or SAML authority"),
    TOP_VERSION_MISSMATCH
            ("urn:oasis:names:tc:SAML:2.0:status:VersionMismatch", "The SAML responder could not process the request because the version of the request message was incorrect."),

    // Secondary status codes
    AUTHN_FAILED
            ("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed", "The responding provider was unable to successfully authenticate the principal."),
    INVALID_ATTR_NAME_OR_VALUE
            ("urn:oasis:names:tc:SAML:2.0:status:InvalidAttrNameOrValue", "Unexpected or invalid content was encountered within a <saml:Attribute> or <saml:AttributeValue> element."),
    INVALID_NAMEID_POLICY
            ("urn:oasis:names:tc:SAML:2.0:status:InvalidNameIDPolicy", "The responding provider cannot or will not support the requested name identifier policy."),
    NO_AUTHN_CONTEXT
            ("urn:oasis:names:tc:SAML:2.0:status:NoAuthnContext", "The specified authentication context requirements cannot be met by the responder."),
    NO_AVAILABLE_IDP(
            "urn:oasis:names:tc:SAML:2.0:status:NoAvailableIDP", "None of the supported identity provider <Loc> elements in the <IDPList> can be resolved or none of the supported identity providers are available."),
    NO_PASSIVE
            ("urn:oasis:names:tc:SAML:2.0:status:NoPassive", "The responding provider cannot authenticate the principal passively, as has been requested."),
    NO_SUPPORTED_IDP
            ("urn:oasis:names:tc:SAML:2.0:status:NoSupportedIDP", "None of the identity providers in an <IDPList> are supported."),
    PARTIAL_LOGOUT
            ("urn:oasis:names:tc:SAML:2.0:status:PartialLogout", "It was not able to propagate logout to all other session participants."),
    PROXY_COUNT_EXCEEDED
            ("urn:oasis:names:tc:SAML:2.0:status:ProxyCountExceeded", "Indicates that a responding provider cannot authenticate the principal directly and is not permitted to proxy the request further."),
    REQUEST_DENIED
            ("urn:oasis:names:tc:SAML:2.0:status:RequestDenied", "The SAML responder or SAML authority is able to process the request but has chosen not to respond."),
    REQUEST_UNSUPPORTED
            ("urn:oasis:names:tc:SAML:2.0:status:RequestUnsupported", "The SAML responder or SAML authority does not support the request."),
    REQUEST_VERSION_DEPRECATED
            ("urn:oasis:names:tc:SAML:2.0:status:RequestVersionDeprecated", "The SAML responder cannot process any requests with the protocol version specified in the request."),
    REQUEST_VERSION_TOO_HIGH
            ("urn:oasis:names:tc:SAML:2.0:status:RequestVersionTooHigh", "The SAML responder cannot process the request because the protocol version specified in the request message is a major upgrade from the highest protocol version supported by the responder."),
    REQUEST_VERSION_TOO_LOW
            ("urn:oasis:names:tc:SAML:2.0:status:RequestVersionTooLow", "The SAML responder cannot process the request because the protocol version specified in the request message is too low."),
    RESOURCE_NOT_RECOGNIZED
            ("urn:oasis:names:tc:SAML:2.0:status:ResourceNotRecognized", "The resource value provided in the request message is invalid or unrecognized."),
    TOO_MANY_RESPONSES
            ("urn:oasis:names:tc:SAML:2.0:status:TooManyResponses", "The response message would contain more elements than the SAML responder is able to return."),
    UNKNOWN_ATTR_PROFILE
            ("urn:oasis:names:tc:SAML:2.0:status:UnknownAttrProfile", "An entity that has no knowledge of a particular attribute profile has been presented with an attribute drawn from that profile."),
    UNKNOWN_PRINCIPAL
            ("urn:oasis:names:tc:SAML:2.0:status:UnknownPrincipal", "The responding provider does not recognize the principal specified or implied by the request."),
    UNSUPPORTED_BINDING
            ("urn:oasis:names:tc:SAML:2.0:status:UnsupportedBinding", "The SAML responder cannot properly fulfill the request using the protocol binding specified in the request."),


    ;

    StatusCode(String value, String description) {
        this.value = value;
        this.description = description;
    }

    private String value;
    private String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
    
    public static StatusCode asEnum(String a) {
        for (StatusCode ac : values()) {
            if (ac.getValue().equals(a))
                return ac;
        }

        throw new IllegalArgumentException("Invalid Status Code '" + a + "'");
    }
}
