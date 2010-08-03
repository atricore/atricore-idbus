package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LookupIdentityApplianceDefinitionRequest extends AbstractManagementRequest {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
