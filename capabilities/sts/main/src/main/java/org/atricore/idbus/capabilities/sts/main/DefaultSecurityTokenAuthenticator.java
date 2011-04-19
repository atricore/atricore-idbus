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

package org.atricore.idbus.capabilities.sts.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

/**
 *
 *  @org.apache.xbean.XBean element="security-token-authenticator"
 *
 * This implementation supports built-in authentication schemes.
 * This will probably be replaced once the provisioning infrastructure is in place.
 *
 * For now, the component will addapt authenticaiton requests and redirect them to legacy authenticator component.
 *
 * TODO : Replace with pluggable authentication infrastructure!
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: DefaultSecurityTokenAuthenticator.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class DefaultSecurityTokenAuthenticator implements SecurityTokenAuthenticator {

    private static Log logger = LogFactory.getLog(DefaultSecurityTokenAuthenticator.class);

    private String id;

    private Authenticator auth;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean canAuthenticate(Object requestToken) {

        if (requestToken instanceof UsernameTokenType)
            return true;
        if ( isRememberMeToken( requestToken ) ){
            return true;
        }

        // TODO : Add X509, SAML2 and other token types!

        return false;
    }

    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {

        String scheme = null;
        Credential[] credentials = null;

        try {

            // Username/password (basic) authentication scheme
            if (isRememberMeToken( requestToken )) {
                scheme = "rememberme-authentication";

                BinarySecurityTokenType rememberMeToken = (BinarySecurityTokenType) requestToken;

                Credential rememberMeCredential = getAuthenticator().newCredential( scheme, "remembermeToken", rememberMeToken.getValue() );

                credentials = new Credential[] { rememberMeCredential };
                
            } else if (is2FactorAuthnToken(requestToken)) {
                scheme = "2factor-authentication";

                UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

                String username = usernameToken.getUsername().getValue();
                String passcode = usernameToken.getOtherAttributes().get( new QName( Constants.PASSCODE_NS) );

                Credential usernameCredential = getAuthenticator().newCredential(scheme, "username", username);
                Credential passcodeCredential = getAuthenticator().newCredential(scheme, "passcode", passcode);

                credentials = new Credential[] {usernameCredential, passcodeCredential};


            } else if (requestToken instanceof UsernameTokenType) {

                scheme = "basic-authentication";

                UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

                String username = usernameToken.getUsername().getValue();
                String password = usernameToken.getOtherAttributes().get( new QName( Constants.PASSWORD_NS) );

                Credential usernameCredential = getAuthenticator().newCredential(scheme, "username", username);
                Credential passwordCredential = getAuthenticator().newCredential(scheme, "password", password);

                credentials = new Credential[] {usernameCredential, passwordCredential};

            }


            // TODO : Add X509, SAML2 and other token types!

            // Authenticate
            if (scheme ==  null)
                throw new SecurityTokenEmissionException("Unsupported token type : " + requestToken.getClass().getSimpleName());

            logger.debug("Authenticating " + requestToken.getClass().getSimpleName() + " using '" + scheme + "'");


            // Addapt authentication, use existing components
            return getAuthenticator().check(credentials, scheme);


        } catch (AuthenticationFailureException e) {
            throw new SecurityTokenAuthenticationFailure(scheme, e);

        } catch (SSOAuthenticationException e) {
            throw new SecurityTokenEmissionException(e);
        }

    }

    public Authenticator getAuthenticator() {
        return auth;
    }

    public void setAuthenticator(Authenticator auth) {
        this.auth = auth;
    }

    private Boolean isRememberMeToken(Object requestToken){
        if (requestToken instanceof BinarySecurityTokenType ){
            return ((BinarySecurityTokenType)requestToken).getOtherAttributes().containsKey( new QName( Constants.REMEMBERME_NS) );
        }
        return false;
    }

    private Boolean is2FactorAuthnToken(Object requestToken){
        if (requestToken instanceof UsernameTokenType ){
            return ((UsernameTokenType)requestToken).getOtherAttributes().containsKey( new QName( Constants.PASSCODE_NS) );
        }
        return false;
    }

}
