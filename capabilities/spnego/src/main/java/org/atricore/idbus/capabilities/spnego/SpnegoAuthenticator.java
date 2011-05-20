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

package org.atricore.idbus.capabilities.spnego;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpnegoAuthenticator implements SecurityTokenAuthenticator {

    private static Log logger = LogFactory.getLog(SpnegoAuthenticator.class);

    private String id;

    private Authenticator auth;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean canAuthenticate(Object requestToken) {

        if ( isSpnegoToken(requestToken) ){
            return true;
        }
        return false;
    }

    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {

        String scheme = null;
        Credential[] credentials = null;

        try {

            if (isSpnegoToken(requestToken)) {

                scheme = "spnego-authentication";

                BinarySecurityTokenType binaryToken = (BinarySecurityTokenType) requestToken;

                String spnegoSecurityToken = binaryToken.getOtherAttributes().get( new QName( Constants.SPNEGO_NS) );

                Credential spnegoCredential = getAuthenticator().newCredential(scheme, "spnegoSecurityToken", spnegoSecurityToken);
                credentials = new Credential[] {spnegoCredential};

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

    private Boolean isSpnegoToken(Object requestToken){
        if (requestToken instanceof BinarySecurityTokenType ){
            return ((BinarySecurityTokenType)requestToken).getOtherAttributes().containsKey( new QName( Constants.SPNEGO_NS) );
        }
        return false;
    }

}
