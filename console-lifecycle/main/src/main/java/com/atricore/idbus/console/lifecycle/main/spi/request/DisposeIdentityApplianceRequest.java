package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @version $Id$
 */
public class DisposeIdentityApplianceRequest extends AbstractManagementRequest {

    private String id;

    public DisposeIdentityApplianceRequest() {
    }

    public DisposeIdentityApplianceRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
