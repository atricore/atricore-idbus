package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupAttributeDefinitionDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class JDOGroupAttributeDefinitionDAOImpl extends GenericDAOImpl<JDOGroupAttributeDefinition, Long> 
        implements JDOGroupAttributeDefinitionDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupAttributeDefinitionDAOImpl.class);

    public JDOGroupAttributeDefinition findByName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeDefinition" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOGroupAttributeDefinition> groupAttributes = (Collection<JDOGroupAttributeDefinition>) query.execute();
        if (groupAttributes == null || groupAttributes.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, groupAttributes.size());

        return groupAttributes.iterator().next();
    }
}
