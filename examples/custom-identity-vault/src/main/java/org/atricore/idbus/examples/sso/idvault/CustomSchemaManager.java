package org.atricore.idbus.examples.sso.idvault;

import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractSchemaManager;

import java.util.Collection;

/**
 * Created by sgonzalez on 3/16/15.
 */
public class CustomSchemaManager extends AbstractSchemaManager {

    @Override
    public UserAttributeDefinition addUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        return null;
    }

    @Override
    public UserAttributeDefinition updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        return null;
    }

    @Override
    public void deleteUserAttribute(long id) throws ProvisioningException {

    }

    @Override
    public UserAttributeDefinition findUserAttributeById(long id) throws ProvisioningException {
        return null;
    }

    @Override
    public UserAttributeDefinition findUserAttributeByName(String name) throws ProvisioningException {
        return null;
    }

    @Override
    public Collection<UserAttributeDefinition> listUserAttributes() throws ProvisioningException {
        return null;
    }

    @Override
    public GroupAttributeDefinition addGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        return null;
    }

    @Override
    public GroupAttributeDefinition updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        return null;
    }

    @Override
    public void deleteGroupAttribute(long id) throws ProvisioningException {

    }

    @Override
    public GroupAttributeDefinition findGroupAttributeById(long id) throws ProvisioningException {
        return null;
    }

    @Override
    public GroupAttributeDefinition findGroupAttributeByName(String name) throws ProvisioningException {
        return null;
    }

    @Override
    public Collection<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException {
        return null;
    }
}
