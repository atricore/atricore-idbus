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

import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;

public class SSOResponseException extends IdentityMediationFault {

    private StatusResponseType  response;

    private StatusCode topLevelStatusCode;
    private StatusCode secondLevelStatusCode;
    private StatusDetails statusDetails;

    public SSOResponseException(StatusResponseType response,
                                StatusCode topLevelStatusCode,
                                StatusCode secondLevelStatusCode,
                                StatusDetails statusDetails) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : ""),
              (statusDetails != null ? statusDetails.getValue() : ""),
                null,
                null);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDetails = statusDetails;
    }

    public SSOResponseException(StatusResponseType response,
                                StatusCode topLevelStatusCode,
                                StatusCode secondLevelStatusCode,
                                StatusDetails statusDetails,
                                String errorDetails) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDetails != null ? statusDetails.getValue() : null),
               errorDetails,
               null);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDetails = statusDetails;
    }

    public SSOResponseException(StatusResponseType response,
                                StatusCode topLevelStatusCode,
                                StatusCode secondLevelStatusCode,
                                StatusDetails statusDetails,
                                Throwable cause) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDetails != null ? statusDetails.getValue() : null),
               null,
               cause);
        this.response = response;
        this.statusDetails = statusDetails;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
    }


    public SSOResponseException(StatusResponseType response,
                                StatusCode topLevelStatusCode,
                                StatusCode secondLevelStatusCode,
                                StatusDetails statusDetails,
                                String errorDetails,
                                Throwable cause) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDetails != null ? statusDetails.getValue() : null),
               errorDetails,
               cause);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDetails = statusDetails;
    }

    public StatusResponseType  getResponse() {
        return response;
    }

    public StatusCode getTopLevelStatusCode() {
        return topLevelStatusCode;
    }

    public StatusCode getSecondLevelStatusCode() {
        return secondLevelStatusCode;
    }

    public StatusDetails getSamlStatusDetails() {
        return statusDetails;
    }

}
