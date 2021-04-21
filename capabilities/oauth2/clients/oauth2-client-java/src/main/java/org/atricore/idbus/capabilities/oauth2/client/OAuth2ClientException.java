package org.atricore.idbus.capabilities.oauth2.client;

import org.atricore.idbus.common.oauth._2_0.protocol.SSOPolicyEnforcementStatementType;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2ClientException extends Exception {

    private List<SSOPolicyEnforcementStatementType> ssoPolicyEnforcements;

    public OAuth2ClientException() {
    }

    public OAuth2ClientException(String message) {
        super(message);
    }

    public OAuth2ClientException(String message, List<SSOPolicyEnforcementStatementType> ssoPolicyEnforcements) {
        super(message);
        this.ssoPolicyEnforcements = ssoPolicyEnforcements;
    }

    public OAuth2ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2ClientException(Throwable cause) {
        super(cause);
    }

    public List<SSOPolicyEnforcementStatementType> getSsoPolicyEnforcements() {
        return ssoPolicyEnforcements;
    }

    public void setSsoPolicyEnforcements(List<SSOPolicyEnforcementStatementType> ssoPolicyEnforcements) {
        this.ssoPolicyEnforcements = ssoPolicyEnforcements;
    }
}
