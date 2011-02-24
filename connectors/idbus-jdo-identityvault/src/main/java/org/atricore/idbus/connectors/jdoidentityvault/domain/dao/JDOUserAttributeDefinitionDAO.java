package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeDefinition;

public interface JDOUserAttributeDefinitionDAO extends GenericDAO<JDOUserAttributeDefinition, Long> {

    JDOUserAttributeDefinition findByName(String name);
}
