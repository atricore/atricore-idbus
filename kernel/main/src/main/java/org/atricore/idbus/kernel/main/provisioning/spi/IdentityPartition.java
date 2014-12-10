package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.provisioning.domain.AclEntry;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityPartition {

    String getName();

    String getDescription();

    @Deprecated
    IdentityVault getIdentityVault();

    IdentityStore getIdentityStore();

    SchemaManager getSchemaManager();

    Group findGroupById(long id) throws ProvisioningException;

    Group findGroupByName(String name) throws ProvisioningException;

    Collection<Group> findGroupsByUserName(String name) throws ProvisioningException;

    Collection<Group> findAllGroups() throws ProvisioningException;

    Group addGroup(Group group) throws ProvisioningException;

    Group updateGroup(Group group) throws ProvisioningException;

    void deleteGroup(long id) throws ProvisioningException;

    User addUser(User user) throws ProvisioningException;

    void deleteUser(long id) throws ProvisioningException;

    User findUserById(long id) throws ProvisioningException;

    User findUserByOid(String oid) throws ProvisioningException;

    User findUserByUserName(String username) throws ProvisioningException;

    Collection<User> findAllUsers() throws ProvisioningException;

    User updateUser(User user) throws ProvisioningException;

    Collection<User> getUsersByGroup(Group group) throws ProvisioningException;

    AclEntry findAclEntryByApprovalToken(String approvalToken) throws ProvisioningException;

    AclEntry findAclEntryById(long id) throws ProvisioningException;

    AclEntry updateAclEntry(AclEntry aclEntry) throws ProvisioningException;

    void deleteAclEntry(long id) throws ProvisioningException;

    Collection<SecurityQuestion> findAllSecurityQuestions() throws ProvisioningException;

    SecurityToken addSecurityToken(SecurityToken securityToken) throws ProvisioningException;

    SecurityToken updateSecurityToken(SecurityToken securityToken) throws ProvisioningException;

    void deleteSecurityToken(String id) throws ProvisioningException;

    SecurityToken findSecurityTokenByTokenId(String tokenId) throws ProvisioningException;

    Collection<SecurityToken> findSecurityTokensByIssueInstantBefore(long issueInstant) throws ProvisioningException;

    Collection<SecurityToken> findSecurityTokensByExpiresOnBefore(long expiresOn) throws ProvisioningException;
}


