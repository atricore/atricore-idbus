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

package org.atricore.idbus.capabilities.samlr2.support.auth;

/**
 * TODO : Check correct values for 'passive' attribute.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AuthnCtxClass.java 1278 2009-06-14 06:14:41Z sgonzalez $
 */
public enum AuthnCtxClass {
    
    /** URI for Internet Protocol authentication context. */
    IP_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocol", true),

    /** URI for Internet Protocol Password authentication context. */
    IP_PASSWORD_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocolPassword", true),

    /** URI for Kerberos authentication context. */
    KERBEROS_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos", true),

    /** URI for Mobile One Factor Unregistered authentication context. */
    MOFU_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorUnregistered", true),

    /** URI for Mobile Two Factor Unregistered authentication context. */
    MTFU_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorUnregistered", true),

    /** URI for Mobile One Factor Contract authentication context. */
    MOFC_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorContract", true),

    /** URI for Mobile Two Factor Contract authentication context. */
    MTFC_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorContract", true),

    /** URI for Password authentication context. */
    PASSWORD_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:Password", false),

    /** URI for Password Protected Transport authentication context. */
    PPT_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport", true),

    /** URI for Previous Session authentication context. */
    PREVIOUS_SESSION_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession", true),

    /** URI for X509 Public Key authentication context. */
    X509_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:X509", true),

    /** URI for PGP authentication context. */
    PGP_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:PGP", true),

    /** URI for SPKI authentication context. */
    SPKI_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:SPKI", true),

    /** URI for XML Digital Signature authentication context. */
    XML_DSIG_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:XMLDSig", true),

    /** URI for Smart Card authentication context. */
    SMARTCARD_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard", true),

    /** URI for Smart Card PKI authentication context. */
    SMARTCARD_PKI_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI", true),

    /** URI for Software PKU authentication context. */
    SOFTWARE_PKI_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:SoftwarePKI", true),

    /** URI for Telephony authentication context. */
    TELEPHONY_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:Telephony", true),

    /** URI for Nomadic Telephony authentication context. */
    NOMAD_TELEPHONY_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:NomadTelephony", true),

    /** URI for Personalized Telephony authentication context. */
    PERSONAL_TELEPHONY_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:PersonalTelephony", true),

    /** URI for Authenticated Telephony authentication context. */
    AUTHENTICATED_TELEPHONY_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:AuthenticatedTelephony", true),

    /** URI for Secure Remote Password authentication context. */
    SRP_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:SecureRemotePassword", true),

    /** URI for SSL/TLS Client authentication context. */
    TLS_CLIENT_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:TLSClient", true),

    /** URI for Time Synchornized Token authentication context. */
    TIME_SYNC_TOKEN_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:TimeSyncToken", false),

    /** URI for unspecified authentication context. */
    UNSPECIFIED_AUTHN_CTX("urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified", true);

    private String ac;
    private boolean isPassive;

    AuthnCtxClass(String ac) {
        this.ac = ac;
    }

    AuthnCtxClass(String ac, boolean passive) {
        this.ac = ac;
        isPassive = passive;
    }

    public static AuthnCtxClass asEnum(String a) {
        for (AuthnCtxClass ac : values()) {
            if (ac.getValue().equals(a))
                return ac;
        }

        throw new IllegalArgumentException("Invalid Authentication Context Class '" + a + "'");
    }

    public String getValue() {
        return ac;
    }

    public boolean isPassive() {
        return isPassive;
    }

    @Override
    public String toString() {
        return ac;
    }
}