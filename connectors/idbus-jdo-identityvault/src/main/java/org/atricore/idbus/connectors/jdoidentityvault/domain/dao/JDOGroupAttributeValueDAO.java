package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeValue;

public interface JDOGroupAttributeValueDAO extends GenericDAO<JDOGroupAttributeValue, Long> {

    void updateName(String oldName, String newName);
    
    void deleteValues(String name);

    void deleteRemovedValues(JDOGroupAttributeValue[] oldValues, JDOGroupAttributeValue[] newValues); 
}
