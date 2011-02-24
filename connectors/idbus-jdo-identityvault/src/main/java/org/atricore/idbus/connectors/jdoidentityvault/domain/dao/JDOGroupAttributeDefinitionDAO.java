package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeDefinition;

public interface JDOGroupAttributeDefinitionDAO extends GenericDAO<JDOGroupAttributeDefinition, Long> {

    JDOGroupAttributeDefinition findByName(String name);
}
