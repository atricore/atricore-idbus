package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOGroupDAOImpl extends GenericDAOImpl<JDOGroup, Long> implements JDOGroupDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupDAOImpl.class);

    public JDOGroup findByName(String name) {

        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup" +
                " WHERE this.name == :name");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);

        Collection<JDOGroup> groups = (Collection<JDOGroup>) query.executeWithMap(params);
        if (groups == null || groups.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, groups.size());

        return groups.iterator().next();
    }

    /**
     * TODO : Add case insensitive lookup parameter
     * @param userName
     * @return
     */
    public Collection<JDOGroup> findByUserName(String userName) {
        PersistenceManager pm = getPersistenceManager();

//        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
//                " WHERE this.userName.toLowerCase() == '" + userName.toLowerCase() + "'");

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
                " WHERE this.userName == :userName");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);

        Collection<JDOUser> users = (Collection<JDOUser>) query.executeWithMap(params);
        if (users == null || users.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, users.size());

        JDOUser user = users.iterator().next();

        if (user.getGroups() != null)
            return Arrays.asList(user.getGroups());

        return new ArrayList<JDOGroup>();
    }
}
