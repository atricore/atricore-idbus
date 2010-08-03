package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class StopIdentityApplianceRequest extends AbstractManagementRequest {

    private String id;

    public StopIdentityApplianceRequest() {
    }

    public StopIdentityApplianceRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
