package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/10/13
 */
public class ListSecurityQuestionsResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -3298476899156498718L;

    private SecurityQuestion[] securityQuestions;

    public SecurityQuestion[] getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(SecurityQuestion[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }
}
