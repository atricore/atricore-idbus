package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeValue;

public interface JDOUserAttributeValueDAO extends GenericDAO<JDOUserAttributeValue, Long> {

    void updateName(String oldName, String newName);
    
    void deleteValues(String name);

    void deleteRemovedValues(JDOUserAttributeValue[] oldValues, JDOUserAttributeValue[] newValues); 
}
