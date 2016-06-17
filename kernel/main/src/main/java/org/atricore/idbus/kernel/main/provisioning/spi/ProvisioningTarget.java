package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;

import java.util.Collection;
import java.util.List;

/**
 * This represents a destination to provision identity and mediation information (when available)
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProvisioningTarget {

    String getName();

    void init();

    void shutDown();

    String getHashAlgorithm();

    String getHashEncoding();

    //<--------------- Transactions -------------------->

    void purgeOldTransactions();

    boolean isTransactionValid(String transactionId);

    AbstractProvisioningRequest lookupTransactionRequest(String transactionId);

    /**
     * Is Mediation Partition available, true when mediation partition is supported
     * by this instance.
     */
    boolean isMediationPartitionAvailable();

    /**
     * Is Schema management available, true when schema management is supported
     * by this instance.
     */
    boolean isSchemaManagementAvailable();


    //<--------------- Groups -------------------->

    RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest)
            throws ProvisioningException;

    AddGroupResponse addGroup(AddGroupRequest groupRequest)
            throws ProvisioningException;

    FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest)
            throws ProvisioningException;

    FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest)
            throws ProvisioningException;

    ListGroupsResponse listGroups(ListGroupsRequest groupRequest)
            throws ProvisioningException;

    SearchGroupResponse searchGroups(SearchGroupRequest groupRequest)
            throws ProvisioningException;

    UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest)
            throws ProvisioningException;

    //<--------------- Users -------------------->

    RemoveUserResponse removeUser(RemoveUserRequest userRequest)
            throws ProvisioningException;

    AddUserResponse addUser(AddUserRequest userRequest)
            throws ProvisioningException;

    List<User> addUsers(List<User> users)
            throws ProvisioningException;

    PrepareAddUserResponse prepareAddUser(AddUserRequest userRequest)
            throws ProvisioningException;

    AddUserResponse confirmAddUser(ConfirmAddUserRequest userRequest)
            throws ProvisioningException;

    FindUserByIdResponse findUserById(FindUserByIdRequest userRequest)
            throws ProvisioningException;

    FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest)
            throws ProvisioningException;


    ListUsersResponse listUsers(ListUsersRequest userRequest)
            throws ProvisioningException;

    SearchUserResponse searchUsers(SearchUserRequest userRequest)
            throws ProvisioningException;

    Collection<String> findUserNames(List<String> usernames)
            throws ProvisioningException;

    UpdateUserResponse updateUser(UpdateUserRequest userRequest)
            throws ProvisioningException;

    GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest)
    		throws ProvisioningException;

    SetPasswordResponse setPassword(SetPasswordRequest setPwdRequest)
            throws ProvisioningException;

    ResetPasswordResponse resetPassword(ResetPasswordRequest resetPwdRequest) throws ProvisioningException;

    PrepareResetPasswordResponse prepareResetPassword(ResetPasswordRequest resetPwdRequest) throws ProvisioningException;

    ResetPasswordResponse confirmResetPassword(ConfirmResetPasswordRequest resetPwdRequest) throws ProvisioningException;

    ListSecurityQuestionsResponse listSecurityQuestions(ListSecurityQuestionsRequest request) throws ProvisioningException;

    //<--------------- ACLs -------------------->
    FindAclEntryByApprovalTokenResponse findAclEntryByApprovalToken(FindAclEntryByApprovalTokenRequest aclEntryRequest) throws ProvisioningException;

    UpdateAclEntryResponse updateAclEntry(UpdateAclEntryRequest aclEntryRequest) throws ProvisioningException;

    RemoveAclEntryResponse removeAclEntry(RemoveAclEntryRequest aclEntryRequest) throws ProvisioningException;

    //<--------------- Security Tokens-------------------->

    AddSecurityTokenResponse addSecurityToken(AddSecurityTokenRequest addSecurityTokenRequest)
            throws ProvisioningException;

    UpdateSecurityTokenResponse updateSecurityToken(UpdateSecurityTokenRequest updateSecurityTokenRequest)
            throws ProvisioningException;


    RemoveSecurityTokenResponse removeSecurityToken(RemoveSecurityTokenRequest removeSecurityTokenRequest)
            throws ProvisioningException;


    FindSecurityTokenByTokenIdResponse findSecurityTokenByTokenId(FindSecurityTokenByTokenIdRequest findSecurityTokenByTokenIdRequest)
            throws ProvisioningException;

    FindSecurityTokensByExpiresOnBeforeResponse findSecurityTokensByExpiresOnBefore(FindSecurityTokensByExpiresOnBeforeRequest findSecurityTokensByExpiresOnBeforeRequest)
            throws ProvisioningException;

    FindSecurityTokensByIssueInstantBeforeResponse findSecurityTokensByIssueInstantBefore(FindSecurityTokensByIssueInstantBeforeRequest findSecurityTokensByIssueInstantBeforeRequest)
            throws ProvisioningException;


    //<--------------- Schema -------------------->

    AddUserAttributeResponse addUserAttribute(AddUserAttributeRequest userAttributeRequest)
            throws ProvisioningException;

    UpdateUserAttributeResponse updateUserAttribute(UpdateUserAttributeRequest userAttributeRequest)
            throws ProvisioningException;

    RemoveUserAttributeResponse removeUserAttribute(RemoveUserAttributeRequest userAttributeRequest)
            throws ProvisioningException;

    FindUserAttributeByIdResponse findUserAttributeById(FindUserAttributeByIdRequest userAttributeRequest)
            throws ProvisioningException;

    FindUserAttributeByNameResponse findUserAttributeByName(FindUserAttributeByNameRequest userAttributeRequest)
            throws ProvisioningException;

    ListUserAttributesResponse listUserAttributes(ListUserAttributesRequest userAttributeRequest)
            throws ProvisioningException;

    AddGroupAttributeResponse addGroupAttribute(AddGroupAttributeRequest groupAttributeRequest)
            throws ProvisioningException;

    UpdateGroupAttributeResponse updateGroupAttribute(UpdateGroupAttributeRequest groupAttributeRequest)
            throws ProvisioningException;

    RemoveGroupAttributeResponse removeGroupAttribute(RemoveGroupAttributeRequest groupAttributeRequest)
            throws ProvisioningException;

    FindGroupAttributeByIdResponse findGroupAttributeById(FindGroupAttributeByIdRequest groupAttributeRequest)
            throws ProvisioningException;

    FindGroupAttributeByNameResponse findGroupAttributeByName(FindGroupAttributeByNameRequest groupAttributeRequest)
            throws ProvisioningException;

    ListGroupAttributesResponse listGroupAttributes(ListGroupAttributesRequest groupAttributeRequest)
            throws ProvisioningException;

}
