package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.SubjectNameIdentifierPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListSubjectNameIDPoliciesResponse {

    private List<SubjectNameIdentifierPolicy> policies;

    public List<SubjectNameIdentifierPolicy> getPolicies() {
        if(policies == null){
            policies = new ArrayList<SubjectNameIdentifierPolicy>();
        }
        return policies;
    }

    public void setPolicies(List<SubjectNameIdentifierPolicy> policies) {
        this.policies = policies;
    }
}
