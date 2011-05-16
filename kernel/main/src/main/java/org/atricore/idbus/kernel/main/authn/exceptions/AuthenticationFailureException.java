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

package org.atricore.idbus.kernel.main.authn.exceptions;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcement;

import java.util.HashSet;
import java.util.Set;

/**
 * This exception is thrown when an authentication fails
 * An authentication failure can happen, for example, when the credentials supplied by the user are invalid.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AuthenticationFailureException.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class AuthenticationFailureException extends SSOAuthenticationException {

    /**
     * This stores the error type detected during authentication, for example AUTH_FAILED.
     */
    private String errorType;

    private Set<SSOPolicyEnforcement> policyEnforcements = new HashSet<SSOPolicyEnforcement>();

    /**
     * It uses AUTH_FAILDE as error type.
     *
     * @param message El mensaje asociado al error.
     */
    public AuthenticationFailureException(String message) {
        this(message, "AUTH_FAILED");
    }

    /**
     * It uses AUTH_FAILDE as error type.
     *
     * @param message El mensaje asociado al error.
     */
    public AuthenticationFailureException(String message, Set<SSOPolicyEnforcement> policyEnforcements) {
        this(message, "AUTH_FAILED");
        this.policyEnforcements.addAll(policyEnforcements);
    }


    /**
     * Allows error type specification, usefull when extending the Authenticator to provide business specific rules.
     *
     * @param message   The error message
     * @param errorType The error type
     */
    public AuthenticationFailureException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public Set<SSOPolicyEnforcement> getSSOPolicies() {
        return policyEnforcements;
    }
}
