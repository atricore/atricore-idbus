package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class AgreggatedProcessingPolicy implements InternalProcessingPolicy {

    private List<InternalProcessingPolicy> policies = new ArrayList<>();

    @Override
    public boolean match(HttpServletRequest originalRequest, String redirectUrl) {
        for (InternalProcessingPolicy p : policies) {
            if (!p.match(originalRequest, redirectUrl))
                return false;
        }
        return true;
    }

    @Override
    public boolean match(HttpServletRequest request) {
        for (InternalProcessingPolicy p : policies) {
            if (!p.match(request))
            return false;
        }
        return true;
    }

    public List<InternalProcessingPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<InternalProcessingPolicy> policies) {
        this.policies = policies;
    }
}
