package org.atricore.idbus.kernel.main.provisioning.spi.request;

import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;

public class UpdateUserAttributeRequest extends AbstractProvisioningRequest {

    private UserAttributeDefinition userAttribute;

    public UserAttributeDefinition getUserAttribute() {
        return userAttribute;
    }

    public void setUserAttribute(UserAttributeDefinition userAttribute) {
        this.userAttribute = userAttribute;
    }
}
