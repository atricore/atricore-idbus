package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;

public class ListGroupAttributesResponse extends AbstractProvisioningResponse {

    private static final long serialVersionUID = -3098476899156498718L;

    private GroupAttributeDefinition[] groupAttributes;

    public GroupAttributeDefinition[] getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(GroupAttributeDefinition[] groupAttributes) {
        this.groupAttributes = groupAttributes;
    }
}
