package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.text.AttributedCharacterIterator;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SchemaManager {

    String getSchemaName();

    void addUserAttribute(String name, String description, AttributeType type, boolean required, boolean multivalued) throws ProvisioningException;
    
    void updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException;
    
    void deleteUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException;
    
    List<UserAttributeDefinition> listUserAttributes() throws ProvisioningException;
    
    void addGroupAttribute(String name, String description, AttributeType type, boolean required, boolean multivalued) throws ProvisioningException;
    
    void updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException;
    
    void deleteGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException;
    
    List<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException;
    
    
}
