package org.atricore.idbus.kernel.main.provisioning.domain;

import java.util.Collection;
import java.util.List;

/**
 * Created by sgonzalez on 5/12/15.
 */
public class OperationResult {

    private String status;

    private String token;

    private String action;

    private String operation;

    private String message;

    private List<OperationParam> params;

    private List<OperationResult> partialResults;

    public Collection<OperationResult> getPartialResults() {
        return partialResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OperationParam> getParams() {
        return params;
    }

    public void setParams(List<OperationParam> params) {
        this.params = params;
    }

    public void setPartialResults(List<OperationResult> partialResults) {
        this.partialResults = partialResults;
    }


}
