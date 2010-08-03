package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.spi.StatusCode;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractManagementResponse implements ManagementResponse {

    private StatusCode statusCode;

    private String errorMsg;

    public AbstractManagementResponse() {

    }

    public AbstractManagementResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public AbstractManagementResponse(StatusCode statusCode, String msg) {
        this(statusCode);
        this.errorMsg = msg;
    }


    public AbstractManagementResponse(String msg) {
        this(StatusCode.STS_ERROR, msg);
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

}
