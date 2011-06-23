package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.SubjectNameIdentifierPolicyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListSubjectNameIDPoliciesResponse {

    private List<SubjectNameIdentifierPolicyDTO> policies;

    public List<SubjectNameIdentifierPolicyDTO> getPolicies() {
        if (policies == null) {
            return new ArrayList<SubjectNameIdentifierPolicyDTO>();
        }
        return policies;
    }

    public void setPolicies(List<SubjectNameIdentifierPolicyDTO> policies) {
        this.policies = policies;
    }
}
