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

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;

import java.util.HashSet;
import java.util.Set;

/**
 * Signals that an authentication failed.  The policy enforcement statements listed in the error are those that failed
 * during authentication.
 *
 * @see PolicyEnforcementStatement
 */
public class SecurityTokenAuthenticationFailure extends SecurityTokenEmissionException {

    private Set<PolicyEnforcementStatement> ssoPolicyEnforcements = new HashSet<PolicyEnforcementStatement>();

    private String principalName;

    /**
     * Creates a new  Security Token authentication exception
     *
     * @param scheme the authentication scheme or policy that failed.
     * @param ssoPolicyEnforcements List of policies that failed
     * @param cause error that caused the policy to fail (optional)
     *
     */
    public SecurityTokenAuthenticationFailure(String scheme, Set<PolicyEnforcementStatement> ssoPolicyEnforcements, Throwable cause) {
        super("Cannot authenticate token using scheme " + scheme, cause);
        if (cause instanceof AuthenticationFailureException) {
            this.principalName = ((AuthenticationFailureException)cause).getPrincipalName();
        }
        this.ssoPolicyEnforcements = ssoPolicyEnforcements;
    }

    public SecurityTokenAuthenticationFailure(String scheme, Throwable cause) {
        super("Cannot authenticate token using scheme " + scheme, cause);
        if (cause instanceof AuthenticationFailureException) {
            this.principalName = ((AuthenticationFailureException)cause).getPrincipalName();
        }

    }

    public SecurityTokenAuthenticationFailure(String message) {
        super(message);
    }

    public SecurityTokenAuthenticationFailure(String scheme, String message) {
        super("Cannot authenticate token using scheme " + scheme + ". " + message);
    }


    public Set<PolicyEnforcementStatement> getSsoPolicyEnforcements() {
        return ssoPolicyEnforcements;
    }

    public String getPrincipalName() {
        return principalName;
    }
}
