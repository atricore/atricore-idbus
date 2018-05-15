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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.CredentialStore;
import org.atricore.idbus.kernel.main.store.identity.CredentialStoreKeyAdapter;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

/**
 * Specific authentiation schemes can extend this base implementation providing
 * specific logic in the authenticate method.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractAuthenticationScheme.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public abstract class AbstractAuthenticationScheme implements AuthenticationScheme {

    private static final Log logger = LogFactory.getLog(AbstractAuthenticationScheme.class);

    private int priority;

    private boolean _authenticated;

    // The subjtect beeng authenticated.
    protected Subject _subject;

    // The credential store used to retrieve konw or trusted credentials.
    protected CredentialStore _credentialStore;
    protected CredentialStoreKeyAdapter _credentialStoreKeyAdapter;
    protected CredentialProvider _credentialProvider;

    // The credentials provided by the user as input.
    protected Credential[] _inputCredentials;
    protected String _name;

    public AbstractAuthenticationScheme() {
        _credentialProvider = doMakeCredentialProvider();
    }

    /**
     * Initializes the authentication scheme.
     *
     * @param userCredentials
     */
    @Override
    public void initialize(Credential[] userCredentials, Subject s) {
        _inputCredentials = userCredentials;
        _subject = s;
        _authenticated = false;
        _credentialProvider = doMakeCredentialProvider();
    }

    /**
     * Confirms the authentication process, populates the Subject with Principal and Credentials information.
     */
    @Override
    public void confirm() {

        // Only add security information if authentication was successful.
        if (!isAuthenticated()) {
            if (logger.isDebugEnabled())
                logger.debug("[cancel()], ignored. Not authenticated for this scheme.");
            return;
        }

        // Get the username associated with input credentials.
        Principal principal = getPrincipal();

        // Public / Private credentials.
        Credential[] pc = null;

        // Populate the Subject
        Set principals = _subject.getPrincipals();
        principals.add(principal);

        // Private credentials :
        Set privateCredentials = _subject.getPrivateCredentials();
        pc = getPrivateCredentials();
        for (int i = 0; i < pc.length; i++) {
            privateCredentials.add(pc[i]);
        }

        // Public credentials :
        Set publicCredentials = _subject.getPublicCredentials();
        pc = getPublicCredentials();
        for (int i = 0; i < pc.length; i++) {
            publicCredentials.add(pc[i]);
        }

        if (logger.isDebugEnabled())
            logger.debug("[confirm()], ok");


    }

    /**
     * Cancels the authentication process.
     */
    @Override
    public void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("[cancel()], ok");
        setAuthenticated(false);
    }

    @Override
    public Credential newCredential(String name, Object value) {
        if (_credentialProvider == null)
            return null;

        return _credentialProvider.newCredential(name, value);
    }

    @Override
    public Credential newEncodedCredential(String name, Object value) {
        if (_credentialProvider == null)
            return null;

        return _credentialProvider.newEncodedCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {
        if (_credentialProvider == null)
            return null;

        return _credentialProvider.newCredentials(user);

    }

    // ------------------------------------------------------------------------------
    // Protected utils
    // ------------------------------------------------------------------------------

    /**
     * Gets the authentication status associated to the scheme. Subclasses should set this flag
     * in the authenticate method implementation.
     */
    protected boolean isAuthenticated() {
        return _authenticated;
    }

    /**
     * Sets the authentication status associated to the scheme. Subclasses should set this flag
     * in the authenticate method implementation.
     */
    protected void setAuthenticated(boolean a) {
        _authenticated = a;
    }

    /**
     * Utility to load credentials from the store.
     *
     * @return the array of konw credentials associated with the authenticated Principal.
     * @throws SSOAuthenticationException if an error occures while accessing the store.
     */
    protected Credential[] getKnownCredentials() throws SSOAuthenticationException {
        try {
            CredentialKey key = getCredentialStoreKeyAdapter().getKeyForPrincipal(getInputPrincipal());
            return _credentialStore.loadCredentials(key, this);
        } catch (SSOIdentityException e) {
            throw new SSOAuthenticationException(e.getMessage(), e);
        }
    }

    protected CredentialStore getCredentialStore() {
        return _credentialStore;
    }

    protected CredentialStoreKeyAdapter getCredentialStoreKeyAdapter() {
        return _credentialStoreKeyAdapter;
    }

    @Override
    public void setCredentialStore(CredentialStore c) {
        _credentialStore = c;
    }

    @Override
    public void setCredentialStoreKeyAdapter(CredentialStoreKeyAdapter a) {
        _credentialStoreKeyAdapter = a;
    }

    @Override
    public Set<PolicyEnforcementStatement> getSSOPolicies() {
        return _subject.getPrincipals(PolicyEnforcementStatement.class);
    }

    /**
     * Clones this authentication scheme.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) { /* Ignore this ... */ }
        return null;
    }

    /**
     * Subclasses must provide specific credential providers.
     *
     * @return
     */
    protected abstract CredentialProvider doMakeCredentialProvider();

    /*------------------------------------------------------------ Properties

    /**
     * Sets Authentication Scheme name
     */
    public void setName(String name) {
        logger.debug("setName() = " + name);
        _name = name;
    }

    /**
     * Obtains the Authentication Scheme name
     */
    @Override
    public String getName() {
        return _name;
    }


    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
