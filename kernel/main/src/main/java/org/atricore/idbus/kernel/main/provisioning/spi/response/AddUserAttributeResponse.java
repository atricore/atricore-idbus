package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;

public class AddUserAttributeResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -1498476899156498718L;

    private UserAttributeDefinition userAttribute;

    public UserAttributeDefinition getUserAttribute() {
        return userAttribute;
    }

    public void setUserAttribute(UserAttributeDefinition userAttribute) {
        this.userAttribute = userAttribute;
    }
}
