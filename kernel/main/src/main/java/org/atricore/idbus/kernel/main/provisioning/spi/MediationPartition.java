package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.provisioning.domain.AclEntry;
import org.atricore.idbus.kernel.main.provisioning.domain.SecurityQuestion;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.util.Collection;

/**
 * Partition type that stores mediation related information
 */
public interface MediationPartition {

    AclEntry findAclEntryByApprovalToken(String approvalToken) throws ProvisioningException;

    AclEntry findAclEntryById(String id) throws ProvisioningException;

    AclEntry updateAclEntry(AclEntry aclEntry) throws ProvisioningException;

    void deleteAclEntry(String id) throws ProvisioningException;

    Collection<SecurityQuestion> findAllSecurityQuestions() throws ProvisioningException;

    SecurityToken addSecurityToken(SecurityToken securityToken) throws ProvisioningException;

    SecurityToken updateSecurityToken(SecurityToken securityToken) throws ProvisioningException;

    void deleteSecurityToken(String id) throws ProvisioningException;

    SecurityToken findSecurityTokenByTokenId(String tokenId) throws ProvisioningException;

    Collection<SecurityToken> findSecurityTokensByIssueInstantBefore(long issueInstant) throws ProvisioningException;

    Collection<SecurityToken> findSecurityTokensByExpiresOnBefore(long expiresOn) throws ProvisioningException;
}
