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

package org.atricore.idbus.capabilities.sso.support.core;

public enum StatusDetails {


    INVALID_DESTINATION
            ("urn:org:atricore:idbus:samlr2:status:InvalidDestination", "Destination URI doesn't match this endpoint."),

    UNKNOWN_REQUEST
            ("urn:org:atricore:idbus:samlr2:status:UnknownRequest", "The received request type is not known"),

    NO_DESTINATION
            ("urn:org:atricore:idbus:samlr2:status:NoDestination", "No 'Destination' information found.."),

    NO_ISSUE_INSTANT
            ("urn:org:atricore:idbus:samlr2:status:NoIssueInstant", "No 'Issue Instant' information found."),

    INVALID_ISSUE_INSTANT
            ("urn:org:atricore:idbus:samlr2:status:InvalidIssueInstant", "Instant is invalid, expired or too early."),

    INVALID_VERSION
            ("urn:org:atricore:idbus:samlr2:status:InvalidVersion", "Invalid SAML Version"),

    INVALID_RESPONSE_ID
            ("urn:org:atricore:idbus:samlr2:status:InvalidResponseID", "The response ID is not expected or invalid"),

    DUPLICATED_ID
            ("urn:org:atricore:idbus:samlr2:status:DuplicatedID", "The ID has already been used"),

    UNSUPPORTED_VERSION
            ("urn:org:atricore:idbus:samlr2:status:UnsupportedVersion", "Usupported SAML Version"),

    NO_IN_RESPONSE_TO
            ("urn:org:atricore:idbus:samlr2:status:NoInResponseTo", "No 'In Response To' information found."),

    INVALID_STATUS_CODE
            ("urn:org:atricore:idbus:samlr2:status:InvalidStatusCode", "Invalid 'Status Code' found."),
    INVALID_RELAY_STATE
            ("urn:org:atricore:idbus:samlr2:status:InvalidRelayState", "Invalid or empty 'Relay State'."),
    NO_STATUS_CODE
            ("urn:org:atricore:idbus:samlr2:status:NoStatusCode", "No 'Status Code' information found"),

    NO_STATUS
            ("urn:org:atricore:idbus:samlr2:status:NoStatus", "No 'Status' information found."),

    INVALID_REQUEST_SIGNATURE
            ("urn:org:atricore:idbus:samlr2:status:InvalidRequestSignature", "Invalid signature in Request"),

    NO_REQUEST_SIGNATURE
            ("urn:org:atricore:idbus:samlr2:status:NoRequestSignature", "No 'Signature' information found in Request"),

    INVALID_RESPONSE_SIGNATURE
            ("urn:org:atricore:idbus:samlr2:status:InvalidResponseSignature", "Invalid signature in Response"),

    INVALID_ASSERTION_SIGNATURE
            ("urn:org:atricore:idbus:samlr2:status:InvalidAssertionSignature", "Invalid signature in Assertion"),

    INVALID_ASSERTION_ENCRYPTION
            ("urn:org:atricore:idbus:samlr2:status:InvalidAssertionEncryption", "Invalid Assertion encryption"),

    INVALID_UTC_VALUE
            ("urn:org:atricore:idbus:samlr2:status:InvalidUTCValue", "Invalid 'UTC' value"),

    NOT_BEFORE_VIOLATED
            ("urn:org:atricore:idbus:samlr2:status:NotBeforeViolated", "'Not Before' condition violated. Value is later than current time"),

    NOT_ONORAFTER_VIOLATED
            ("urn:org:atricore:idbus:samlr2:status:NotOnOrAfterViolated", "'Not On or After' condition violated. Value is later than current time"),

    INVALID_CONDITION
            ("urn:org:atricore:idbus:samlr2:status:InvalidCondition", "Condition is invalid"),

    NO_METHOD
            ("urn:org:atricore:idbus:samlr2:status:NoMethod", "No 'Method' information found."),

    INVALID_SUBJECT_CONF_DATA
            ("urn:org:atricore:idbus:samlr2:status:InvalidSubjectConfirmationData", "Invalid 'Subject Confirmation Data'"),

    NO_SUBJECT
            ("urn:org:atricore:idbus:samlr2:status:NoSubject", "No 'Subject' information found."),

    NO_ACCOUNT_LINK
            ("urn:org:atricore:idbus:samlr2:status:NoAccountLink", "No 'Account Link' information found."),

    NO_AUTHN_INSTANT
            ("urn:org:atricore:idbus:samlr2:status:NoAuthnInstant", "No 'Authentication Instant' information found."),

    NO_AUTHN_CONTEXT
            ("urn:org:atricore:idbus:samlr2:status:NoAuthnContext", "No 'Authentication Context' information found"),

    NOT_IN_AUDIENCE
            ("urn:org:atricore:idbus:samlr2:status:NotInAudience", "Entity is not in audience list."),

    NO_NAMEID_ENCRYPTEDID(
            "urn:org:atricore:idbus:samlr2:status:NoNameIDEncryptedID", "No 'NameID' or 'EncryptedID' found."),

    NO_ID
            ("urn:org:atricore:idbus:samlr2:status:NoID", "No 'ID' found."),

    NO_ISSUER
            ("urn:org:atricore:idbus:samlr2:status:NoIssuer", "No 'Issuer' found"),

    INVALID_ISSUER_FORMAT
            ("urn:org:atricore:idbus:samlr2:status:InvalidIssuerFormat", "Invalid 'Issuer Format'"),

    NO_NEWID_NEWENCRYPTEDID_TERMINATE
            ("urn:org:atricore:idbus:samlr2:status:NoNewIDNewEncryptedIDTerminate", "One of the elements must exist: NewID, NewEncryptedID or Terminate."),

    // A profile with the same ID is in use
    DUPLICATED_USER_ID
            ("urn:org:atricore:idbus:samlr2:status:DuplicatedUserId", "Duplicated user ID."),

    // The user ID is in use, probably by another identifier or key (i.e. social login
    USED_USER_ID
            ("urn:org:atricore:idbus:samlr2:status:UsedUserId", "Duplicated user ID."),


    INTERNAL_ERROR
            ("urn:org:atricore:idbus:samlr2:status:InternalError", "Internal Error");

    private String value;
    private String description;

    StatusDetails(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
