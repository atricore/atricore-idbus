package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.authn.SecurityToken;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AddSecurityTokenResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -1398476899156498718L;

    private SecurityToken securityToken;

    public SecurityToken getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(SecurityToken securityToken) {
        this.securityToken = securityToken;
    }
}
