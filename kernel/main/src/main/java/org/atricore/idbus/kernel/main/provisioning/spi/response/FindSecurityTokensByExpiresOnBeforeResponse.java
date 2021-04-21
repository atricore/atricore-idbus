package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.authn.SecurityToken;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FindSecurityTokensByExpiresOnBeforeResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -2398476899156498718L;

    private SecurityToken[] securityTokens;

    public SecurityToken[] getSecurityTokens() {
        return securityTokens;
    }

    public void setSecurityTokens(SecurityToken[] securityTokens) {
        this.securityTokens = securityTokens;
    }

}
