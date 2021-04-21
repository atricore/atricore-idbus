package org.atricore.idbus.kernel.main.mediation.policy;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface PolicyEnforcementRequest extends Serializable {

    String getId();

    EndpointDescriptor getReplyTo();

    Set<PolicyEnforcementStatement> getStatements();
}
