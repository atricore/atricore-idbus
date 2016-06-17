package org.atricore.idbus.connectors.jdoidentityvault.domain.dao;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSearchCriteria;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface JDOUserDAO extends GenericDAO<JDOUser, Long> {

    JDOUser findByUserName(String userName);

    Collection<JDOUser> find(UserSearchCriteria searchCriteria, long fromResult, long resultCount, String sortColumn, boolean sortAscending);

    Long findCount(UserSearchCriteria searchCriteria);

    Collection<String> findUserNames(List<String> usernames);
}
