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

package org.atricore.idbus.capabilities.samlr2.support.core;

import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;

public class SamlR2ResponseException extends IdentityMediationFault {

    private StatusResponseType  response;

    private StatusCode topLevelStatusCode;
    private StatusCode secondLevelStatusCode;
    private StatusDetails statusDtails;

    public SamlR2ResponseException(StatusResponseType  response,
                                             StatusCode topLevelStatusCode,
                                             StatusCode secondLevelStatusCode,
                                             StatusDetails statusDtails) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : ""),
              (statusDtails != null ? statusDtails.getValue() : ""),
                null,
                null);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
    }

    public SamlR2ResponseException(StatusResponseType  response,
                                             StatusCode topLevelStatusCode,
                                             StatusCode secondLevelStatusCode,
                                             StatusDetails statusDtails,
                                             String errorDetails) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDtails != null ? statusDtails.getValue() : null),
               errorDetails,
               null);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
    }

    public SamlR2ResponseException(StatusResponseType  response,
                                             StatusCode topLevelStatusCode,
                                             StatusCode secondLevelStatusCode,
                                             StatusDetails statusDtails,
                                             Throwable cause) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDtails != null ? statusDtails.getValue() : null),
               null,
               cause);
        this.response = response;
        this.statusDtails = statusDtails;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
    }


    public SamlR2ResponseException(StatusResponseType response,
                                             StatusCode topLevelStatusCode,
                                             StatusCode secondLevelStatusCode,
                                             StatusDetails statusDtails,
                                             String errorDetails,
                                             Throwable cause) {
        super(topLevelStatusCode.getValue(),
              (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : null),
              (statusDtails != null ? statusDtails.getValue() : null),
               errorDetails,
               cause);

        this.response = response;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
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
        return statusDtails;
    }

}
