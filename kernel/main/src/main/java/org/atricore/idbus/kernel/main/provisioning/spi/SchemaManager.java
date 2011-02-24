package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SchemaManager {

    String getSchemaName();

    UserAttributeDefinition addUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException;
    
    UserAttributeDefinition updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException;
    
    void deleteUserAttribute(long id) throws ProvisioningException;

    UserAttributeDefinition findUserAttributeById(long id) throws ProvisioningException;

    UserAttributeDefinition findUserAttributeByName(String name) throws ProvisioningException;
    
    Collection<UserAttributeDefinition> listUserAttributes() throws ProvisioningException;
    
    GroupAttributeDefinition addGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException;
    
    GroupAttributeDefinition updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException;
    
    void deleteGroupAttribute(long id) throws ProvisioningException;

    GroupAttributeDefinition findGroupAttributeById(long id) throws ProvisioningException;

    GroupAttributeDefinition findGroupAttributeByName(String name) throws ProvisioningException;

    Collection<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException;
}
