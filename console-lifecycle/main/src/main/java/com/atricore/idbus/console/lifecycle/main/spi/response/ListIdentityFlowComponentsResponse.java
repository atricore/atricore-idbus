package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityFlowComponentReference;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityMappingPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class ListIdentityFlowComponentsResponse {
    private List<IdentityFlowComponentReference> identityFlowComponentReferences;

    public List<IdentityFlowComponentReference> getIdentityFlowComponentReferences() {
        if(identityFlowComponentReferences == null){
            identityFlowComponentReferences = new ArrayList<IdentityFlowComponentReference>();
        }
        return identityFlowComponentReferences;
    }

    public void setIdentityFlowComponentReferences(List<IdentityFlowComponentReference> identityFlowComponentReferences) {
        this.identityFlowComponentReferences = identityFlowComponentReferences;
    }
}
