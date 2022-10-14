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

package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityMediationFault extends IdentityMediationException {

    private String faultCode;
    private String secFaultCode;
    private String statusDetails;
    private String errorDetails;

    // List of policy enforcment statements
    private Set<PolicyEnforcementStatement> policies;

    private Throwable fault;

    public IdentityMediationFault(String faultCode,
                                  String secFaultCode,
                                  String statusDetails,
                                  String errorDetails,
                                  Throwable fault) {
        super(faultCode + ":" +
                secFaultCode + ":" +
                statusDetails + ":" +
                errorDetails + ":", fault);
        this.faultCode = faultCode;
        this.secFaultCode = secFaultCode;
        this.statusDetails = statusDetails;
        this.errorDetails = errorDetails;
        this.fault = fault;
    }

    public Throwable getFault() {
        return fault;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public String getSecFaultCode() {
        return secFaultCode;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public Set<PolicyEnforcementStatement> getPolicies() {
        return policies;
    }

    public void setPolicies(Set<PolicyEnforcementStatement> policies) {
        this.policies = policies;
    }
}
