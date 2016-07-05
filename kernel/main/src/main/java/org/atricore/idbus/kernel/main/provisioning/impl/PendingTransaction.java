package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.spi.request.AbstractProvisioningRequest;
import org.atricore.idbus.kernel.main.provisioning.spi.response.AbstractProvisioningResponse;

import java.io.Serializable;

/**
 * Created by sgonzalez.
 */
public class PendingTransaction implements Serializable {

    private static final long serialVersionUID = -2497495462156498718L;

    private String id;

    private String code;

    private String username;

    private long expiresOn;

    private AbstractProvisioningRequest request;

    private AbstractProvisioningResponse response;

    public PendingTransaction(String id, String username, long expiresOn, AbstractProvisioningRequest request, AbstractProvisioningResponse response) {
        this.id = id;
        this.username = username;
        this.expiresOn = expiresOn;
        this.request = request;
        this.response = response;
    }

    public PendingTransaction(String id, String code, String username, long expiresOn, AbstractProvisioningRequest request, AbstractProvisioningResponse response) {
        this.id = id;
        this.code = code;
        this.username = username;
        this.expiresOn = expiresOn;
        this.request = request;
        this.response = response;
    }


    public String getId() {
        return id;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public AbstractProvisioningRequest getRequest() {
        return request;
    }

    public AbstractProvisioningResponse getResponse() {
        return response;
    }

    public String getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public void rollback() {
        // TODO : Undo some change, like deleting a temp user that did not confirmed the registration
    }
}