package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSearchCriteria;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * TODO : Add case insensitive lookup parameter
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOUserDAOImpl extends GenericDAOImpl<JDOUser, Long> implements JDOUserDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupDAOImpl.class);

    public JDOUser findByUserName(String name) {

        PersistenceManager pm = getPersistenceManager();

//         Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
//                " WHERE this.userName.toLowerCase() == '" + name.toLowerCase() + "'");

         Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
                " WHERE this.userName == '" + name + "'");


        Collection<JDOUser> users = (Collection<JDOUser>) query.execute();
        if (users == null || users.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, users.size());

        return users.iterator().next();
    }

    public Collection<JDOUser> find(UserSearchCriteria searchCriteria, long fromResult, long resultCount, String sortColumn, boolean sortAscending) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery(JDOUser.class);

        if (StringUtils.isNotBlank(sortColumn))
            query.setOrdering(sortColumn + " " + (sortAscending ? "ascending" : "descending"));

        if (fromResult < 0)
            fromResult = 0;

        if (resultCount > 0)
            query.setRange(fromResult, fromResult + resultCount);

        UserSearchCriteriaHelper searchCriteriaHelper = new UserSearchCriteriaHelper(searchCriteria);
        searchCriteriaHelper.createFilterData();

        if (searchCriteriaHelper.getParams().size() > 0)
            query.setFilter(searchCriteriaHelper.getSearchCriteriaQuery());

        return (Collection<JDOUser>) query.executeWithMap(searchCriteriaHelper.getParams());
    }

    public Long findCount(UserSearchCriteria searchCriteria) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery(JDOUser.class);

        UserSearchCriteriaHelper searchCriteriaHelper = new UserSearchCriteriaHelper(searchCriteria);
        searchCriteriaHelper.createFilterData();

        if (searchCriteriaHelper.getParams().size() > 0)
            query.setFilter(searchCriteriaHelper.getSearchCriteriaQuery());

        query.setResult("count(this)");

        return (Long) query.executeWithMap(searchCriteriaHelper.getParams());
    }

    public Collection<String> findUserNames(List<String> usernames) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT userName FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser" +
                " WHERE :usernames.contains(userName)");

        return (Collection<String>) query.execute(usernames);
    }
}
