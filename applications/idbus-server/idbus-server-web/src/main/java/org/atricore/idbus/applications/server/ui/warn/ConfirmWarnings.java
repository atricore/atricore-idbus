package org.atricore.idbus.applications.server.ui.warn;

import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequest;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConfirmWarnings {

    private Set<WarningData> warningData = new HashSet<WarningData>();

    private PolicyEnforcementRequest request;

    public PolicyEnforcementRequest getRequest() {
        return request;
    }

    public void setRequest(PolicyEnforcementRequest request) {
        this.request = request;
    }

    public Set<WarningData> getWarningData() {
        return warningData;
    }
}
