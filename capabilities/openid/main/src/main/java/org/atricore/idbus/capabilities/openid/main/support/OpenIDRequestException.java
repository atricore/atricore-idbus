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

package org.atricore.idbus.capabilities.openid.main.support;

import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;

public class OpenIDRequestException extends Exception {

    private RequestAbstractType Request;

    private StatusCode topLevelStatusCode;
    private StatusCode secondLevelStatusCode;
    private StatusDetails statusDtails;
    private String errorDetails;

    public OpenIDRequestException() {
		super();
	}

    public OpenIDRequestException(RequestAbstractType Request,
                                  StatusCode topLevelStatusCode,
                                  StatusCode secondLevelStatusCode,
                                  StatusDetails statusDtails) {
        super("OpenID Request [" + (Request != null ? Request.getID() : "<NULL>") +
                "] error. (" + topLevelStatusCode.getValue() + ":" +
                (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : "") + ":" +
                (statusDtails != null ? statusDtails.getValue() : "")  +  ")");

        this.Request = Request;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
    }

    public OpenIDRequestException(RequestAbstractType Request,
                                  StatusCode topLevelStatusCode,
                                  StatusCode secondLevelStatusCode,
                                  StatusDetails statusDtails,
                                  String errorDetails) {
        super("OpenID Request [" + (Request != null ? Request.getID() : "<NULL>") +
                "] error. (" + topLevelStatusCode.getValue() + ":" +
                (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : "") + ":" +
                (statusDtails != null ? statusDtails.getValue() : "")  + ":" +
                errorDetails + ")");

        this.Request = Request;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
        this.errorDetails = errorDetails;
    }

    public OpenIDRequestException(RequestAbstractType Request,
                                  StatusCode topLevelStatusCode,
                                  StatusCode secondLevelStatusCode,
                                  StatusDetails statusDtails,
                                  Throwable cause) {
        super("OpenID Request [" + (Request != null ? Request.getID() : "<NULL>") +
                "] error. (" + topLevelStatusCode.getValue() + ":" +
                (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : "") + ":" +
                (statusDtails != null ? statusDtails.getValue() : "")  + ")",
                cause);
        this.Request = Request;
        this.statusDtails = statusDtails;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
    }


    public OpenIDRequestException(RequestAbstractType Request,
                                  StatusCode topLevelStatusCode,
                                  StatusCode secondLevelStatusCode,
                                  StatusDetails statusDtails,
                                  String errorDetails,
                                  Throwable cause) {
        super("OpenID Request [" + (Request != null ? Request.getID() : "<NULL>") +
                "] error. (" + topLevelStatusCode.getValue() + ":" +
                (secondLevelStatusCode != null ? secondLevelStatusCode.getValue() : "") + ":" +
                (statusDtails != null ? statusDtails.getValue() : "")  + ":" +
                errorDetails + ")", 
                cause);
        
        this.Request = Request;
        this.topLevelStatusCode = topLevelStatusCode;
        this.secondLevelStatusCode = secondLevelStatusCode;
        this.statusDtails = statusDtails;
        this.errorDetails = errorDetails;
    }

    public RequestAbstractType getRequest() {
        return Request;
    }

    public StatusCode getTopLevelStatusCode() {
        return topLevelStatusCode;
    }

    public StatusCode getSecondLevelStatusCode() {
        return secondLevelStatusCode;
    }

    public StatusDetails getStatusDtails() {
        return statusDtails;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
