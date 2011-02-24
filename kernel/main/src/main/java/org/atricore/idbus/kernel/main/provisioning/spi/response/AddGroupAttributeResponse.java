package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;

public class AddGroupAttributeResponse extends AbstractProvisioningResponse {

    private GroupAttributeDefinition groupAttribute;

    public GroupAttributeDefinition getGroupAttribute() {
        return groupAttribute;
    }

    public void setGroupAttribute(GroupAttributeDefinition groupAttribute) {
        this.groupAttribute = groupAttribute;
    }
}
