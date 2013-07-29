package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/26/13
 */
public class UndisposeIdentityApplianceRequest extends AbstractManagementRequest {

    private String id;

    public UndisposeIdentityApplianceRequest() {
    }

    public UndisposeIdentityApplianceRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
