package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityFlowComponentReferenceDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class ListIdentityFlowComponentsResponse {
    private List<IdentityFlowComponentReferenceDTO> identityFlowComponentReferences;

    public List<IdentityFlowComponentReferenceDTO> getIdentityFlowComponentReferences() {
        if(identityFlowComponentReferences == null){
            identityFlowComponentReferences = new ArrayList<IdentityFlowComponentReferenceDTO>();
        }
        return identityFlowComponentReferences;
    }

    public void setIdentityFlowComponentReferences(List<IdentityFlowComponentReferenceDTO> identityFlowComponentReferences) {
        this.identityFlowComponentReferences = identityFlowComponentReferences;
    }
}
