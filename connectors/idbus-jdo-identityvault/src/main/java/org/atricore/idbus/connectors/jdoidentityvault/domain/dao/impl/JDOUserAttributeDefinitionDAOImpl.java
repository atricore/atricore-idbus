package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserAttributeDefinitionDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class JDOUserAttributeDefinitionDAOImpl extends GenericDAOImpl<JDOUserAttributeDefinition, Long>
        implements JDOUserAttributeDefinitionDAO {

    private static final Log logger = LogFactory.getLog(JDOUserAttributeDefinitionDAOImpl.class);

    public JDOUserAttributeDefinition findByName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeDefinition" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOUserAttributeDefinition> userAttributes = (Collection<JDOUserAttributeDefinition>) query.execute();
        if (userAttributes == null || userAttributes.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, userAttributes.size());

        return userAttributes.iterator().next();
    }
}
