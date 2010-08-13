package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOGroupDAOImpl extends GenericDAOImpl<JDOGroup, Long> implements JDOGroupDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupDAOImpl.class);

    public JDOGroup findByName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOGroup> groups = (Collection<JDOGroup>) query.execute();
        if (groups == null || groups.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, groups.size());

        return groups.iterator().next();
    }
}
