package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;

public class ListUserAttributesResponse extends AbstractProvisioningResponse {

    private UserAttributeDefinition[] userAttributes;

    public UserAttributeDefinition[] getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(UserAttributeDefinition[] userAttributes) {
        this.userAttributes = userAttributes;
    }
}
