package org.atricore.idbus.kernel.main.mediation.policy;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PolicyEnforcementRequestImpl implements PolicyEnforcementRequest {

    private String id;

    private EndpointDescriptor replyTo;

    private Set<PolicyEnforcementStatement> stmts = new HashSet<PolicyEnforcementStatement>();

    public PolicyEnforcementRequestImpl(String id, EndpointDescriptor replyTo) {
        this.id = id;
        this.replyTo = replyTo;
    }

    public String getId() {
        return id;
    }

    public EndpointDescriptor getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(EndpointDescriptor replyTo) {
        this.replyTo = replyTo;
    }

    public Set<PolicyEnforcementStatement> getStatements() {
        return stmts;
    }
}
