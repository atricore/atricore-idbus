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

package org.atricore.idbus.kernel.main.authn;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.*;

/**
 * This is the default authenticator implementation.
 *
 * @org.apache.xbean.XBean element="authenticator"
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AuthenticatorImpl.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public class AuthenticatorImpl implements Authenticator {

    private static final Log logger = LogFactory.getLog(AuthenticatorImpl.class);

    private long _authCount;
    private long _authFailures;

    // Prototype instance for authentication scheme.
    private List<AuthenticationScheme> _as;

    /**
     * Validates user identity.  Populates the Subject with Principal and Credential information.
     *
     * @param credentials the credentials to be checked
     * @param schemeName  the authentication scheme to be used to check the supplied credentials.
     */
    public Subject check(Credential[] credentials, String schemeName)
            throws SSOAuthenticationException {

        // Initialize the AuthenticationScheme
        Subject s = new Subject();

        List<AuthenticationScheme> schemes = getSchemes(schemeName);
        Set<SSOPolicyEnforcementStatement> ssoPolicies = new HashSet<SSOPolicyEnforcementStatement>();
        String lastPrincipal = null;

        for (AuthenticationScheme scheme : schemes) {

            if (logger.isTraceEnabled())
                logger.trace("Authenticating with " + scheme);

            scheme.initialize(credentials, s);

            if (scheme.authenticate()) {
                // If authentication succeeds, return the subject.
                scheme.confirm();
                _authCount++;

                // Add all SSO Policies to authenticated Subject
                s.getPrincipals().addAll(scheme.getSSOPolicies());
                return s;
            }

            scheme.cancel();
            if (scheme.getSSOPolicies() != null) {
                ssoPolicies.addAll(scheme.getSSOPolicies());
            }
            if (scheme.getPrincipal() != null)
                lastPrincipal = scheme.getPrincipal().getName();


        }
        // Send SSO Policies with Authn error
        _authFailures++;
        throw new AuthenticationFailureException(lastPrincipal, ssoPolicies);

    }

    public Credential newCredential(String schemeName, String name, Object value) throws SSOAuthenticationException {
        return getScheme(schemeName).newCredential(name, value);
    }

    public Principal getPrincipal(String schemeName, Credential[] credentials) {
        return getScheme(schemeName).getPrincipal(credentials);
    }

    /**
     * A prototype instance of the used authentication scheme is injected.
     * This isntance will be cloned for each authentication process.
     */
    public void setAuthenticationSchemes(AuthenticationScheme[] as) {
        _as = new ArrayList<AuthenticationScheme>();
        for (int i = 0; i < as.length; i++) {
            AuthenticationScheme a = as[i];
            logger.info("[setAuthenticationScheme()] : " + a.getName() + "," + a.getClass().getName());
            _as.add(a);

            // Sort the list, based on priority
            Collections.sort(_as, new AuthenticationSchemePriorityComparator());
        }

    }

    public AuthenticationScheme[] getAuthenticationSchemes() {
        return this._as.toArray(new AuthenticationScheme[_as.size()]);
    }

    public AuthenticationScheme getAuthenticationScheme(String name) {
        return this.getScheme(name);
    }

    /**
     * @org.apache.xbean.Property alias="schemes" nestedType="org.josso.auth.AuthenticationScheme"
     * @return
     */
    public List<AuthenticationScheme> getSchemes() {
        return _as;
    }

    public void setSchemes(List<AuthenticationScheme> schemes) {
        this._as = schemes;
    }



    public long getAuthCount() {
        return _authCount;
    }

    public long getAuthFailures() {
        return _authFailures;
    }

    public List<String> getSchemeNames() {
        List<String> names = new ArrayList<String>(_as.size());
        for (AuthenticationScheme s: _as) {
            names.add(s.getName());
        }
        return names;

    }

    // --------------------------------------------------------------
    // Protected utils
    // --------------------------------------------------------------

    /**
     * This method clones the configured authentication scheme because
     * authentication schemes are not thread safe.  It's a "prototype" pattern.
     *
     * @param schemeName the name of the authentication scheme to instantiate.
     * @return the cloned AuthenticationScheme
     */
    protected List<AuthenticationScheme> getSchemes(String schemeName) {
        List<AuthenticationScheme> as = new ArrayList<AuthenticationScheme>(10);

        for (AuthenticationScheme a : _as) {
            if (logger.isDebugEnabled())
                logger.debug("getScheme() : checking " + a.getName());

            // Important to clone it, to make it thread-safe.
            if (a.getName().equals(schemeName))
                as.add((AuthenticationScheme) a.clone());
        }

        logger.debug("Found " + as.size() + " Authentication Schemes  for ["+schemeName+"]");

        if (as.size() < 1)
            logger.error("Found " + as.size() + " Authentication Schemes  for ["+schemeName+"], scheme not registered!");

        return as;

    }

    /**
     * This method clones the configured authentication scheme because
     * authentication schemes are not thread safe.  It's a "prototype" pattern.
     *
     * @param schemeName the name of the authentication scheme to instantiate.
     * @return the cloned AuthenticationScheme
     */
    @Deprecated
    protected AuthenticationScheme getScheme(String schemeName) {

        for (AuthenticationScheme a : _as) {
            if (logger.isDebugEnabled())
                logger.debug("getScheme() : checking " + a.getName());

            // Important to clone it, to make it thread-safe.
            if (a.getName().equals(schemeName))
                return (AuthenticationScheme) a.clone();
        }

        logger.error("Authentication scheme ["+schemeName+"] not registered!");

        return null;
    }


    protected class AuthenticationSchemePriorityComparator implements Comparator<AuthenticationScheme> {
        public int compare(AuthenticationScheme o1, AuthenticationScheme o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }

}
