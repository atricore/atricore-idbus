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

package org.atricore.idbus.kernel.main.authn.scheme;

import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.store.identity.CredentialStore;
import org.atricore.idbus.kernel.main.store.identity.CredentialStoreKeyAdapter;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

/**
 * Represents any authentication scheme, like user&password, certificate, kerveros 5, etc.
 * The authentication mechanism should be :
 * <p/>
 * 1. Initialize the AuthenticationScheme withe input credentials received from user.
 * 2. Optionally get the principal name derived from input credentials.
 * 3. Authenticate the user providing known or trusted credentials.
 * 4. Confirm or cancel the authentication process.
 * 5. Optinalliy get private / public credentials.
 * <p/>
 * Authentication schemes are cloneable, a first instance is used as a "prototype" to build new ones.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AuthenticationScheme.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public interface AuthenticationScheme extends CredentialProvider, Cloneable {


    /**
     * Authentication scheme priority
     */
    int getPriority();

    /**
     * Obtains the Authentication Scheme name
     */
    String getName();

    /**
     * Initializes this authentication scheme with the received credentials.
     *
     * @param inputCredentials the list of credentials used to authenticate the user, like username and password.
     */
    void initialize(Credential[] inputCredentials, Subject s);

    /**
     * This method authenticates a user based on its credentials.
     *
     * @return The Principal associated with the user if the authentication success, null otherwise.
     */
    boolean authenticate()
            throws SSOAuthenticationException;

    /**
     * Confirms the authentication process, populates the subject with Principal and credential information.
     */
    void confirm();

    /**
     * Cancels the authentication process.
     */
    void cancel();

    /**
     * Get the list of policies enforced during authentication
     */
    Set<SSOPolicyEnforcementStatement> getSSOPolicies();

    /**
     * This method returns the principal name derived from input credentials.
     */
    Principal getPrincipal();

    /**
     * This method returns the principal name derived from provided credentials.
     */
    Principal getPrincipal(Credential[] credentials);

    /**
     * Returns an array of private credentials that can be associated to a Subject by the authenticator.
     */
    Credential[] getPrivateCredentials();

    /**
     * Returns an array of public credentials that can be associated to a Subject by the authenticator.
     */
    Credential[] getPublicCredentials();

    /**
     * Setter for the CredentialStore used by the scheme to retrieve known credentials if necessary.
     */
    void setCredentialStore(CredentialStore cs);

    /**
     * Setter for the CredentialStoreKeyAdapter used by the scheme to retrieve known credentials if necessary.
     */
    void setCredentialStoreKeyAdapter(CredentialStoreKeyAdapter a);

    /**
     * All authenticatio schemes should be cloneable.
     */
    Object clone();


}
