package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;

public class FindUserAttributeByIdResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -2598476899156498718L;

    private UserAttributeDefinition userAttribute;

    public UserAttributeDefinition getUserAttribute() {
        return userAttribute;
    }

    public void setUserAttribute(UserAttributeDefinition userAttribute) {
        this.userAttribute = userAttribute;
    }
}
