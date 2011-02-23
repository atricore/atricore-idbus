package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractSchemaManager;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOSchemaManager extends AbstractSchemaManager {

    // TODO : private UserAttributeDefinitionDAO usrAttrDefDao;

    // TODO : private GroupAttributeDefinitionDAO grpAttrDefDao;

    public String getSchemaName() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void addUserAttribute(String name, String description, AttributeType type, boolean required, boolean multivalued) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void deleteUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public List<UserAttributeDefinition> listUserAttributes() throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void addGroupAttribute(String name, String description, AttributeType type, boolean required, boolean multivalued) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public void deleteGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public List<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
