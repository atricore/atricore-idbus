package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;

public class UpdateUserAttributeResponse extends AbstractProvisioningResponse {

    private UserAttributeDefinition userAttribute;

    public UserAttributeDefinition getUserAttribute() {
        return userAttribute;
    }

    public void setUserAttribute(UserAttributeDefinition userAttribute) {
        this.userAttribute = userAttribute;
    }
}
