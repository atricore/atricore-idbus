package org.atricore.idbus.capabilities.sts.main;

import org.atricore.idbus.kernel.main.authn.SSOPolicyEnforcementStatement;

import javax.security.auth.Subject;
import java.util.Set;

/**
 * Verifies if a subject can be authenticated.  This is invoked after successfully the received token/credentials
 */
public interface SubjectAuthenticationPolicy {

    String getName();

    String getDescription();

    /**
     *
     * @param subject the subject to be verified.
     * @param context additional context information
     *
     * @throws SecurityTokenAuthenticationFailure if the Subject cannot be verified. (Error status information included)
     */
    Set<SSOPolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure;
}
