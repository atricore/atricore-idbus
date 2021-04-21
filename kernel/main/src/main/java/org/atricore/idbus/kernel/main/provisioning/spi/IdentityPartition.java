package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSearchCriteria;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityPartition {

    String getName();

    String getDescription();

    IdentityStore getIdentityStore();

    Group findGroupById(String id) throws ProvisioningException;

    Group findGroupByName(String name) throws ProvisioningException;

    Collection<Group> findGroupsByUserName(String name) throws ProvisioningException;

    Collection<Group> findAllGroups() throws ProvisioningException;

    Group addGroup(Group group) throws ProvisioningException;

    Group updateGroup(Group group) throws ProvisioningException;

    void deleteGroup(String id) throws ProvisioningException;

    User addUser(User user) throws ProvisioningException;

    List<User> addUsers(List<User> users) throws ProvisioningException;

    void deleteUser(String id) throws ProvisioningException;

    void deleteUsers(List<User> users) throws ProvisioningException;

    User findUserById(String id) throws ProvisioningException;

    User findUserByUserName(String username) throws ProvisioningException;

    Collection<User> findAllUsers() throws ProvisioningException;

    User updateUser(User user) throws ProvisioningException;

    List<User> updateUsers(List<User> users) throws ProvisioningException;

    Collection<User> getUsersByGroup(Group group) throws ProvisioningException;

    Collection<User> findUsers(UserSearchCriteria searchCriteria, long fromResult, long resultCount, String sortColumn, boolean sortAscending) throws ProvisioningException;

    Long findUsersCount(UserSearchCriteria searchCriteria) throws ProvisioningException;

    Collection<String> findUserNames(List<String> usernames) throws ProvisioningException;
}


