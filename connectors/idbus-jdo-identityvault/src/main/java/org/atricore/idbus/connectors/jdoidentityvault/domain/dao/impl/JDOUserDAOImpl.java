package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOUserDAOImpl extends GenericDAOImpl<JDOUser, Long> implements JDOUserDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupDAOImpl.class);

    public JDOUser findByUserName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
                " WHERE this.userName == '" + name + "'");

        Collection<JDOUser> users = (Collection<JDOUser>) query.execute();
        if (users == null || users.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, users.size());

        return users.iterator().next();
    }
    
    

}
