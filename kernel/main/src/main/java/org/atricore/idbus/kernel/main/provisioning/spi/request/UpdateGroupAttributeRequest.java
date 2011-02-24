package org.atricore.idbus.kernel.main.provisioning.spi.request;

import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;

public class UpdateGroupAttributeRequest extends AbstractProvisioningRequest {

    private GroupAttributeDefinition groupAttribute;

    public GroupAttributeDefinition getGroupAttribute() {
        return groupAttribute;
    }

    public void setGroupAttribute(GroupAttributeDefinition groupAttribute) {
        this.groupAttribute = groupAttribute;
    }
}
