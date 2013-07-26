package com.atricore.idbus.console.services.spi.request;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/26/13
 */
public class UndisposeIdentityApplianceRequest extends AbstractManagementRequest {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
