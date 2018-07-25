package org.atricore.idbus.kernel.main.provisioning.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.auditing.core.Action;
import org.atricore.idbus.kernel.auditing.core.ActionOutcome;
import org.atricore.idbus.kernel.auditing.core.AuditingServer;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.provisioning.domain.*;
import org.atricore.idbus.kernel.main.provisioning.exception.*;
import org.atricore.idbus.kernel.main.provisioning.spi.*;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProvisioningTargetImpl implements ProvisioningTarget {

    private static final Log logger = LogFactory.getLog(ProvisioningTargetImpl.class);

    private UUIDGenerator uuid = new UUIDGenerator();

    private UUIDGenerator shortIdGen = new UUIDGenerator(4);

    final Random saltRandomizer = new SecureRandom();

    private String name;

    private String description;
    
    private String hashEncoding;
    
    private String hashAlgorithm;
    
    private String hashCharset;
    
    private int saltLength;

    private String saltValue;
    
    private IdentityPartition identityPartition;

    private MediationPartition mediationPartition;

    private SchemaManager schemaManager;

    private int maxTimeToLive = 600; // seconds

    private AuditingServer aServer;

    private String auditCategory = "";

    private static final Set<String> dictionary = new HashSet<String>();

    private List<PasswordPolicy> passwordPolicies = new ArrayList<PasswordPolicy>();

    static {
        // TODO : Take this from resource/file
        dictionary.add("password");
        dictionary.add("123456");
    }

    private TransactionStore transactionStore;

    //private Map<String, >

    public void init() {
        shortIdGen.setPrefix(null);

    }

    public void shutDown() {

    }

    @Override
    public boolean isMediationPartitionAvailable() {
        return mediationPartition != null;
    }

    @Override
    public boolean isSchemaManagementAvailable() {
        return schemaManager != null;
    }

    public boolean isTransactionValid(String transactionId) {
        if (transactionId != null) {
            PendingTransaction t = transactionStore.retrieve(transactionId);
            long now = System.currentTimeMillis();
            return t.getExpiresOn() < now;
        }

        return false;
    }

    public AbstractProvisioningRequest lookupTransactionRequest(String transactionId) {
        PendingTransaction t = transactionStore.retrieve(transactionId);
        if (t != null)
            return t.getRequest();

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getHashCharset() {
        return hashCharset;
    }

    public void setHashCharset(String hashCharset) {
        this.hashCharset = hashCharset;
    }

    public String getHashEncoding() {
        return hashEncoding;
    }

    public void setHashEncoding(String hashEncoding) {
        this.hashEncoding = hashEncoding;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public int getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

    public MediationPartition getMediationPartition() {
        return mediationPartition;
    }

    public void setMediationPartition(MediationPartition mediationPartition) {
        this.mediationPartition = mediationPartition;
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    public void setSchemaManager(SchemaManager schemaManager) {
        this.schemaManager = schemaManager;
    }

    public List<PasswordPolicy> getPasswordPolicies() {
        return passwordPolicies;
    }

    public void setPasswordPolicies(List<PasswordPolicy> passwordPolicies) {
        this.passwordPolicies = passwordPolicies;
    }

    public void deleteGroup(String id) throws ProvisioningException {
        try {
            identityPartition.deleteGroup(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws ProvisioningException {

        try {
            Group group = identityPartition.findGroupById(groupRequest.getId());
            FindGroupByIdResponse groupResponse = new FindGroupByIdResponse ();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (GroupNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws ProvisioningException {

        try {
            Group group = identityPartition.findGroupByName(groupRequest.getName());
            FindGroupByNameResponse groupResponse = new FindGroupByNameResponse ();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (GroupNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public ListGroupsResponse listGroups(ListGroupsRequest groupRequest) throws ProvisioningException {
        try {
            Collection<Group> groups = identityPartition.findAllGroups();

            ListGroupsResponse groupResponse = new ListGroupsResponse();
            groupResponse.setGroups(groups.toArray(new Group[groups.size()]));

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningException {
        String name = groupRequest.getName();
        String descr = groupRequest.getDescription();
        
        if (descr != null)
            throw new ProvisioningException("Group search by description not supported");

        if (name == null)
            throw new ProvisioningException("Name or description must be specified");
        
        try {

            Group group =  identityPartition.findGroupByName(name);
            List<Group> groups = new ArrayList<Group>();
            groups.add(group);

            SearchGroupResponse groupResponse = new SearchGroupResponse();
            groupResponse.setGroups(groups);

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningException {

        Properties auditProps = new Properties();
        auditProps.setProperty("groupName", groupRequest.getName());

        try {
            Group group = new Group();
            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());
            group.setAttrs(groupRequest.getAttrs());
            group = identityPartition.addGroup(group);
            AddGroupResponse groupResponse = new AddGroupResponse();
            groupResponse.setGroup(group);
            recordInfoAuditTrail(Action.ADD_GROUP.getValue(), ActionOutcome.SUCCESS, null, auditProps);
            return groupResponse;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.ADD_GROUP.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }


    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("groupId", groupRequest.getId());

        try {
            
            Group group = identityPartition.findGroupById(groupRequest.getId());

            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());
            group.setAttrs(groupRequest.getAttrs());
            
            group = identityPartition.updateGroup(group);

            auditProps.setProperty("groupName", group.getName());
            recordInfoAuditTrail(Action.UPDATE_GROUP.getValue(), ActionOutcome.SUCCESS, null, auditProps);

            UpdateGroupResponse groupResponse = new UpdateGroupResponse();
            groupResponse.setGroup(group);

            return groupResponse;
        } catch (GroupNotFoundException e) {
            auditProps.setProperty("groupNotFound", "true");
            recordInfoAuditTrail(Action.UPDATE_GROUP.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.UPDATE_GROUP.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("groupId", groupRequest.getId());
        try {
            identityPartition.deleteGroup(groupRequest.getId());
            recordInfoAuditTrail(Action.REMOVE_GROUP.getValue(), ActionOutcome.SUCCESS, null, auditProps);
            return new RemoveGroupResponse();
        } catch (GroupNotFoundException e) {
            auditProps.setProperty("groupNotFound", "true");
            recordInfoAuditTrail(Action.REMOVE_GROUP.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.REMOVE_GROUP.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("userId", userRequest.getId());
        try {
            identityPartition.deleteUser(userRequest.getId());
            recordInfoAuditTrail(Action.REMOVE_USER.getValue(), ActionOutcome.SUCCESS, null, auditProps);
            return new RemoveUserResponse();
        } catch (UserNotFoundException e) {
            auditProps.setProperty("userNotFound", "true");
            recordInfoAuditTrail(Action.REMOVE_USER.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.REMOVE_USER.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    @Override
    public RemoveUsersResponse removeUsers(RemoveUsersRequest usersRequest) throws ProvisioningException {
        try {
            identityPartition.deleteUsers(usersRequest.getUsers());
            return new RemoveUsersResponse();
        } catch (Exception e) {
            recordInfoAuditTrail(Action.REMOVE_USERS.getValue(), ActionOutcome.FAILURE, null, null);
            throw new ProvisioningException(e);
        }
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws ProvisioningException {

        // Make sure that the password meets the security requirements
        validatePassword(userRequest.getUserPassword());

        try {

            User user = new User();

            BeanUtils.copyProperties(userRequest, user, new String[] { "groups",
                    "securityQuestions",
                    "acls",
                    "userPassword",
            "accountExpirationDate",
            "lastAuthentication",
            "passwordExpirationDate"});

            if (userRequest.getAccountExpirationDate() != null)
                user.setAccountExpirationDate(userRequest.getAccountExpirationDate().getTime());

            if (userRequest.getLastAuthentication() != null)
                user.setLastAuthentication(userRequest.getLastAuthentication().getTime());

            if (userRequest.getPasswordExpirationDate() != null)
                user.setPasswordExpirationDate(userRequest.getPasswordExpirationDate().getTime());

            String salt = generateSalt();

            user.setSalt(salt);
            user.setUserPassword(createPasswordHash(userRequest.getUserPassword(), salt));

            Group[] groups = userRequest.getGroups();
            user.setGroups(groups);

            UserSecurityQuestion[] securityQuestions = userRequest.getSecurityQuestions();
            user.setSecurityQuestions(securityQuestions);

            user = identityPartition.addUser(user);
            AddUserResponse userResponse = new AddUserResponse();
            userResponse.setUser(user);

            recordInfoAuditTrail(Action.ADD_USER.getValue(), ActionOutcome.SUCCESS, userRequest.getUserName(), null);

            return userResponse;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.ADD_USER.getValue(), ActionOutcome.FAILURE, userRequest.getUserName(), null);
            throw new ProvisioningException(e);
        }
    }

    public List<User> addUsers(List<User> users) throws ProvisioningException {

        for (User user : users) {
            // Make sure that the password meets the security requirements
            validatePassword(user.getUserPassword());

            try {

                String salt = generateSalt();

                user.setSalt(salt);
                user.setLastPasswordChangeDate(System.currentTimeMillis());
                user.setUserPassword(createPasswordHash(user.getUserPassword(), salt));
            } catch (Exception e) {
                recordInfoAuditTrail(Action.ADD_USER.getValue(), ActionOutcome.FAILURE, user.getUserName(), null);
                throw new ProvisioningException(e);
            }
        }

        try {
            users = identityPartition.addUsers(users);
        } catch (Exception e) {
            recordInfoAuditTrail(Action.ADD_USER.getValue(), ActionOutcome.FAILURE, null, null);
            throw new ProvisioningException(e);
        }

        return users;
    }

    public PrepareAddUserResponse prepareAddUser(AddUserRequest userRequest) throws ProvisioningException {

        // TODO : We should be able to use different authentication mechanisms and let the IDP handle the authn ....
        String transactionId = uuid.generateId();

        AddUserResponse userResponse = new AddUserResponse();

        User u = new User();
        BeanUtils.copyProperties(userRequest, u, new String[]{"groups", "securityQuestions", "userPassword"});

        String salt = generateSalt();

        // Random password
        String tmpPassword = RandomStringUtils.randomAlphanumeric(6);
        u.setSalt(salt);
        u.setUserPassword(createPasswordHash(tmpPassword, salt));
        u.setLastPasswordChangeDate(System.currentTimeMillis());
        u.setAccountDisabled(true);

        u = identityPartition.addUser(u);

        userResponse.setUser(u);

        PendingTransaction t = new PendingTransaction(transactionId, u.getUserName(),
                System.currentTimeMillis() + (1000L * getMaxTimeToLive()),
                userRequest,
                userResponse);

        storePendingTransaction(t);

        recordInfoAuditTrail(Action.PREPARE_ADD_USER.getValue(), ActionOutcome.SUCCESS, userRequest.getUserName(), null);

        return new PrepareAddUserResponse(t.getId(), u, tmpPassword);
    }

    public AddUserResponse confirmAddUser(ConfirmAddUserRequest confirmReq) throws ProvisioningException {
        String transactionId = confirmReq.getTransactionId();

        // Make sure that the password meets the security requirements
        validatePassword(confirmReq.getUserPassword());
        validateSecurityQuestions(confirmReq.getSecurityQuestions());

        PendingTransaction t = this.consumePendingTransaction(transactionId);

        // Retrieve the user from the partition
        AddUserResponse response = (AddUserResponse) t.getResponse();
        String uid = response.getUser().getId();
        User tmpUser = identityPartition.findUserById(uid);

        // New user information

        // Get remaining user information from confirmation
        // BeanUtils.copyProperties(confirmReq, tmpUser, new String[] {"id", "groups", "securityQuestions", "userPassword"});

        // Password
        tmpUser.setUserPassword(createPasswordHash(confirmReq.getUserPassword(), tmpUser.getSalt()));
        tmpUser.setLastPasswordChangeDate(System.currentTimeMillis());

        // Groups
        // tmpUser.setGroups(confirmReq.getGroups());

        // Security Questions
        if (confirmReq.getSecurityQuestions() != null) {
            if (getHashAlgorithm() != null) {
                for (UserSecurityQuestion usq : confirmReq.getSecurityQuestions()) {
                    usq.setAnswer(createPasswordHash(usq.getAnswer(), tmpUser.getSalt()));
                }
            }
            tmpUser.setSecurityQuestions(confirmReq.getSecurityQuestions());
        }

        // Enable account
        tmpUser.setAccountDisabled(false);

        // Store user information
        User newUser = identityPartition.updateUser(tmpUser);

        recordInfoAuditTrail(Action.CONFIRM_ADD_USER.getValue(), ActionOutcome.SUCCESS, newUser.getUserName(), null);

        // Send response message
        response.setUser(newUser);

        return response;
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws ProvisioningException {
        try {
            User user = identityPartition.findUserById(userRequest.getId());
            FindUserByIdResponse userResponse = new FindUserByIdResponse();
            userResponse.setUser(user);
            return userResponse;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws ProvisioningException {
        try {
            User user = identityPartition.findUserByUserName(userRequest.getUsername());
            FindUserByUsernameResponse userResponse = new FindUserByUsernameResponse();
            userResponse.setUser(user);
            return userResponse;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public ListUsersResponse listUsers(ListUsersRequest userRequest) throws ProvisioningException {
        try {
            Collection<User> users = identityPartition.findAllUsers();

            ListUsersResponse userResponse = new ListUsersResponse();
            userResponse.setUsers(users.toArray(new User[users.size()]));

            return userResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }


    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws ProvisioningException {
        try {
            Collection<User> users = identityPartition.findUsers(userRequest.getSearchCriteria(),
                    userRequest.getFromResult(), userRequest.getResultCount(), "userName", true);

            SearchUserResponse userResponse = new SearchUserResponse();
            userResponse.setUsers(new ArrayList<User>(users));
            userResponse.setNumOfUsers(identityPartition.findUsersCount(userRequest.getSearchCriteria()));

            return userResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public Collection<String> findUserNames(List<String> usernames) throws ProvisioningException {
        try {
            return identityPartition.findUserNames(usernames);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws ProvisioningException {
        try {
            
            User user = userRequest.getUser();
            User oldUser = identityPartition.findUserById(user.getId());

            // DO NOT UPDATE USER PASSWORD OR LIFE QUESTIONS HERE
            BeanUtils.copyProperties(user, oldUser, new String[] {"groups", "securityQuestions", "acls", "userPassword", "id"});
            oldUser.setGroups(user.getGroups());
            oldUser.setAcls(user.getAcls());

            // DO NOT UPDATE USER PASSWORD HERE : oldUser.setUserPassword(createPasswordHash(user.getUserPassword()));

            user = identityPartition.updateUser(oldUser);

            recordInfoAuditTrail(Action.UPDATE_USER.getValue(), ActionOutcome.SUCCESS, userRequest.getUser().getUserName(), null);

            UpdateUserResponse userResponse = new UpdateUserResponse();
            userResponse.setUser(user);

            return userResponse;

        } catch (UserNotFoundException e) {
            recordInfoAuditTrail(Action.UPDATE_USER.getValue(), ActionOutcome.FAILURE, userRequest.getUser().getUserName(), null);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.UPDATE_USER.getValue(), ActionOutcome.FAILURE, userRequest.getUser().getUserName(), null);
            throw new ProvisioningException(e);
        }
    }

    @Override
    public UpdateUsersResponse updateUsers(UpdateUsersRequest usersRequest) throws ProvisioningException {

        try {
            List<User> users = identityPartition.updateUsers(usersRequest.getUsers());
            UpdateUsersResponse resp = new UpdateUsersResponse();
            resp.setUsers(users);
            return resp;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.UPDATE_USERS.getValue(), ActionOutcome.FAILURE, null, null);
            throw new ProvisioningException(e);
        }


    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByUserRequest) throws ProvisioningException {
        // TODO !
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public SetPasswordResponse setPassword(SetPasswordRequest setPwdRequest) throws ProvisioningException {

        // Validate new password
        validatePassword(setPwdRequest.getNewPassword());

        try {
            User user = identityPartition.findUserById(setPwdRequest.getUserId());

            String currentPwd = createPasswordHash(setPwdRequest.getCurrentPassword(), user.getSalt());
            if (!user.getUserPassword().equals(currentPwd)) {
                throw new InvalidPasswordException("Provided password is invalid");
            }

            // TODO : Apply password validation rules
            String newPwdHash = createPasswordHash(setPwdRequest.getNewPassword(), user.getSalt());
            user.setUserPassword(newPwdHash);
            user.setLastPasswordChangeDate(System.currentTimeMillis());
            identityPartition.updateUser(user);
            SetPasswordResponse setPwdResponse = new SetPasswordResponse();
            
            return setPwdResponse;
        } catch (ProvisioningException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException("Cannot update user password", e);
        }
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPwdRequest) throws ProvisioningException {

        // Generate a password (TODO : improve ?!)
        // Passwords with alphabetic and numeric characters.
        String pwd = resetPwdRequest.getNewPassword();

        // Make sure that the password meets the security requirements
        validatePassword(pwd);

        Properties auditProps = new Properties();
        auditProps.setProperty("userId", resetPwdRequest.getUser().getId());

        try {
            User user = identityPartition.findUserById(resetPwdRequest.getUser().getId());
            User providedUser = resetPwdRequest.getUser();
            if (!user.getUserName().equals(providedUser.getUserName()))
                throw new ProvisioningException("Invalid user information");

            String pwdHash = createPasswordHash(pwd, user.getSalt());
            user.setUserPassword(pwdHash);
            user.setLastPasswordChangeDate(System.currentTimeMillis());

            identityPartition.updateUser(user);

            recordInfoAuditTrail(Action.PWD_RESET.getValue(), ActionOutcome.SUCCESS, resetPwdRequest.getUser().getUserName(), auditProps);

            ResetPasswordResponse resetPwdResponse = new ResetPasswordResponse();
            resetPwdResponse.setNewPassword(pwd);

            return resetPwdResponse;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.PWD_RESET.getValue(), ActionOutcome.FAILURE, resetPwdRequest.getUser().getUserName(), auditProps);
            throw new ProvisioningException("Cannot reset user password", e);
        }
    }

    public PrepareResetPasswordResponse prepareResetPassword(ResetPasswordRequest resetPwdRequest) throws ProvisioningException {

        String transactionId = uuid.generateId();
        String code = shortIdGen.generateId();

        String pwd = RandomStringUtils.randomAlphanumeric(8);

        ResetPasswordResponse resetPwdResp = new ResetPasswordResponse();
        resetPwdResp.setNewPassword(pwd);

        // TODO : Make confiurable
        PendingTransaction t = new PendingTransaction(transactionId, code, resetPwdRequest.getUser().getUserName(),
                System.currentTimeMillis() + (1000L * getMaxTimeToLive()), resetPwdRequest, resetPwdResp);

        storePendingTransaction(t);

        Properties auditProps = new Properties();
        auditProps.setProperty("userId", resetPwdRequest.getUser().getId());
        recordInfoAuditTrail(Action.PREPARE_PWD_RESET.getValue(), ActionOutcome.SUCCESS, resetPwdRequest.getUser().getUserName(), auditProps);

        return new PrepareResetPasswordResponse(t.getId(), t.getCode(), pwd);
    }

    public ResetPasswordResponse confirmResetPassword(ConfirmResetPasswordRequest resetPwdRequest) throws ProvisioningException {

        User user = null;

        Properties auditProps = new Properties();
        //auditProps.setProperty("transactionId", resetPwdRequest.getTransactionId());

        try {

            // Either the user provides a new password, or we use the one we created.
            boolean usedGeneratedPwd = resetPwdRequest.getNewPassword() == null;
            if (!usedGeneratedPwd) ;
            validatePassword(resetPwdRequest.getNewPassword());

            if (logger.isDebugEnabled())
                logger.trace("Password is valid ");

            String id = resetPwdRequest.getCode() != null ?
                    resetPwdRequest.getCode() : resetPwdRequest.getTransactionId();

            PendingTransaction t = consumePendingTransaction(id);


            // Recover original request ...
            ResetPasswordRequest req = (ResetPasswordRequest) t.getRequest();
            ResetPasswordResponse resp = (ResetPasswordResponse) t.getResponse();

            user = identityPartition.findUserById(req.getUser().getId());

            // Make a case insensitive check!
            if (!t.getUsername().equalsIgnoreCase(user.getUserName()))
                throw new TransactionExpiredException(t.getId());

            String newPwd = usedGeneratedPwd ? req.getNewPassword() : resetPwdRequest.getNewPassword();
            String pwdHash = createPasswordHash(newPwd, user.getSalt());
            resp.setNewPassword(newPwd);

            // Set user's password

            user.setUserPassword(pwdHash);
            user.setLastPasswordChangeDate(System.currentTimeMillis());

            user = identityPartition.updateUser(user);

            recordInfoAuditTrail(Action.CONFIRM_PWD_RESET.getValue(), ActionOutcome.SUCCESS, user.getUserName(), auditProps);

            if (logger.isDebugEnabled())
                logger.debug("Password has been updated using " + (usedGeneratedPwd ? "GENERATED" : "USER PROVIDED") + " password");

            return resp;
        } catch (ProvisioningException e) {
            recordInfoAuditTrail(Action.CONFIRM_PWD_RESET.getValue(), ActionOutcome.FAILURE, user != null ? user.getUserName() : null, auditProps);
            throw e;
        }
    }

    public FindAclEntryByApprovalTokenResponse findAclEntryByApprovalToken(FindAclEntryByApprovalTokenRequest aclEntryRequest) throws ProvisioningException {

        try {
            AclEntry aclEntry = mediationPartition.findAclEntryByApprovalToken(aclEntryRequest.getApprovalToken());
            FindAclEntryByApprovalTokenResponse aclEntryResponse = new FindAclEntryByApprovalTokenResponse();
            aclEntryResponse.setAclEntry(aclEntry);
            return aclEntryResponse;
        } catch (AclEntryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public UpdateAclEntryResponse updateAclEntry(UpdateAclEntryRequest aclEntryRequest) throws ProvisioningException {
        try {
            AclEntry aclEntry = aclEntryRequest.getAclEntry();
            AclEntry oldAclEntry = mediationPartition.findAclEntryById(aclEntry.getId());

            BeanUtils.copyProperties(aclEntry, oldAclEntry, new String[] {"id"});

            aclEntry = mediationPartition.updateAclEntry(oldAclEntry);

            UpdateAclEntryResponse aclEntryResponse = new UpdateAclEntryResponse();
            aclEntryResponse.setAclEntry(aclEntry);

            return aclEntryResponse;
        } catch (AclEntryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveAclEntryResponse removeAclEntry(RemoveAclEntryRequest aclEntryRequest) throws ProvisioningException {
        try {
            mediationPartition.deleteAclEntry(aclEntryRequest.getId());
            return new RemoveAclEntryResponse();
        } catch (AclEntryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    // ------------------------------------------------------------------------------------

    public AddSecurityTokenResponse addSecurityToken(AddSecurityTokenRequest addSecurityTokenRequest) throws ProvisioningException {
        try {
            SecurityToken securityToken = new SecurityTokenImpl(addSecurityTokenRequest.getTokenId(),
                    addSecurityTokenRequest.getNameIdentifier(),
                    addSecurityTokenRequest.getContent(),
                    addSecurityTokenRequest.getSerializedContent(),
                    addSecurityTokenRequest.getIssueInstant());

            securityToken = mediationPartition.addSecurityToken(securityToken);

            AddSecurityTokenResponse resp = new AddSecurityTokenResponse();
            resp.setSecurityToken(securityToken);
            return resp;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public UpdateSecurityTokenResponse updateSecurityToken(UpdateSecurityTokenRequest req) throws ProvisioningException {
        try {
            SecurityTokenImpl st = (SecurityTokenImpl) mediationPartition.findSecurityTokenByTokenId(req.getTokenId());
            st.setContent(req.getContent());
            st.setSerializedContent(req.getSerializedContent());
            st.setIssueInstant(req.getIssueInstant());
            st.setNameIdentifier(req.getNameIdentifier());

            st = (SecurityTokenImpl) mediationPartition.updateSecurityToken(st);

            UpdateSecurityTokenResponse resp = new UpdateSecurityTokenResponse();
            resp.setSecurityToken(st);
            return resp;
        } catch (SecurityTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveSecurityTokenResponse removeSecurityToken(RemoveSecurityTokenRequest req) throws ProvisioningException {
        try {
            SecurityTokenImpl st = (SecurityTokenImpl) mediationPartition.findSecurityTokenByTokenId(req.getTokenId());
            mediationPartition.deleteSecurityToken(st.getId());
            RemoveSecurityTokenResponse resp = new RemoveSecurityTokenResponse();

            return resp;

        } catch (SecurityTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public FindSecurityTokenByTokenIdResponse findSecurityTokenByTokenId(FindSecurityTokenByTokenIdRequest req) throws ProvisioningException {
        try {
            SecurityToken st = mediationPartition.findSecurityTokenByTokenId(req.getTokenId());
            FindSecurityTokenByTokenIdResponse resp = new FindSecurityTokenByTokenIdResponse();
            resp.setSecurityToken(st);
            return resp;
        } catch (SecurityTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public FindSecurityTokensByExpiresOnBeforeResponse findSecurityTokensByExpiresOnBefore(FindSecurityTokensByExpiresOnBeforeRequest req) throws ProvisioningException {
        try {
            Collection<SecurityToken> st = mediationPartition.findSecurityTokensByExpiresOnBefore(req.getExpiresOnBefore());
            FindSecurityTokensByExpiresOnBeforeResponse resp = new FindSecurityTokensByExpiresOnBeforeResponse();
            if (st != null)
                resp.setSecurityTokens(st.toArray(new SecurityToken[st.size()]));
            else
                resp.setSecurityTokens(new SecurityToken[0]);
            return resp;
        } catch (SecurityTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public FindSecurityTokensByIssueInstantBeforeResponse findSecurityTokensByIssueInstantBefore(FindSecurityTokensByIssueInstantBeforeRequest req) throws ProvisioningException {
        try {
            Collection<SecurityToken> st = mediationPartition.findSecurityTokensByIssueInstantBefore(req.getIssueInstant());
            FindSecurityTokensByIssueInstantBeforeResponse resp = new FindSecurityTokensByIssueInstantBeforeResponse();
            if (st != null)
                resp.setSecurityTokens(st.toArray(new SecurityToken[st.size()]));
            else
                resp.setSecurityTokens(new SecurityToken[0]);
            return resp;
        } catch (SecurityTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

    }


    // ------------------------------------------------------------------------------------

    public AddUserAttributeResponse addUserAttribute(AddUserAttributeRequest userAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("userAttributeName", userAttributeRequest.getName());

        try {
            // create user attribute
            UserAttributeDefinition userAttribute = new UserAttributeDefinition();
            userAttribute.setName(userAttributeRequest.getName());
            userAttribute.setDescription(userAttributeRequest.getDescription());
            userAttribute.setType(userAttributeRequest.getType());
            userAttribute.setRequired(userAttributeRequest.isRequired());
            userAttribute.setMultivalued(userAttributeRequest.isMultivalued());

            // add user attribute
            userAttribute = schemaManager.addUserAttribute(userAttribute);
            AddUserAttributeResponse userAttributeResponse = new AddUserAttributeResponse();
            userAttributeResponse.setUserAttribute(userAttribute);

            recordInfoAuditTrail(Action.ADD_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);

            return userAttributeResponse;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.ADD_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public UpdateUserAttributeResponse updateUserAttribute(UpdateUserAttributeRequest userAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("userAttributeId", userAttributeRequest.getUserAttribute().getId());

        try {
            UserAttributeDefinition userAttribute = userAttributeRequest.getUserAttribute();

            UserAttributeDefinition oldUserAttribute = schemaManager.findUserAttributeById(userAttribute.getId());

            BeanUtils.copyProperties(userAttribute, oldUserAttribute, new String[] {"id"});
            
            userAttribute = schemaManager.updateUserAttribute(oldUserAttribute);

            UpdateUserAttributeResponse userAttributeResponse = new UpdateUserAttributeResponse();
            userAttributeResponse.setUserAttribute(userAttribute);

            auditProps.setProperty("userAttributeName", userAttribute.getName());
            recordInfoAuditTrail(Action.UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);

            return userAttributeResponse;

        } catch (UserAttributeNotFoundException e) {
            auditProps.setProperty("userAttributeNotFound", "true");
            recordInfoAuditTrail(Action.UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.UPDATE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public RemoveUserAttributeResponse removeUserAttribute(RemoveUserAttributeRequest userAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("userAttributeId", userAttributeRequest.getId());

        try {
            schemaManager.deleteUserAttribute(userAttributeRequest.getId());
            recordInfoAuditTrail(Action.REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);
            return new RemoveUserAttributeResponse();
        } catch (UserAttributeNotFoundException e) {
            auditProps.setProperty("userAttributeNotFound", "true");
            recordInfoAuditTrail(Action.REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.REMOVE_USER_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public FindUserAttributeByIdResponse findUserAttributeById(FindUserAttributeByIdRequest userAttributeRequest) throws ProvisioningException {
        try {
            UserAttributeDefinition userAttribute = schemaManager.findUserAttributeById(userAttributeRequest.getId());
            FindUserAttributeByIdResponse userAttributeResponse = new FindUserAttributeByIdResponse();
            userAttributeResponse.setUserAttribute(userAttribute);
            return userAttributeResponse;
        } catch (UserAttributeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public FindUserAttributeByNameResponse findUserAttributeByName(FindUserAttributeByNameRequest userAttributeRequest) throws ProvisioningException {
        try {
            UserAttributeDefinition userAttribute = schemaManager.findUserAttributeByName(userAttributeRequest.getName());
            FindUserAttributeByNameResponse userAttributeResponse = new FindUserAttributeByNameResponse();
            userAttributeResponse.setUserAttribute(userAttribute);
            return userAttributeResponse;
        } catch (UserAttributeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public ListUserAttributesResponse listUserAttributes(ListUserAttributesRequest userAttributeRequest) throws ProvisioningException {
        try {
            Collection<UserAttributeDefinition> userAttributes = schemaManager.listUserAttributes();

            ListUserAttributesResponse userAttributeResponse = new ListUserAttributesResponse();
            userAttributeResponse.setUserAttributes(userAttributes.toArray(new UserAttributeDefinition[userAttributes.size()]));

            return userAttributeResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public AddGroupAttributeResponse addGroupAttribute(AddGroupAttributeRequest groupAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("groupAttributeName", groupAttributeRequest.getName());

        try {
            // create group attribute
            GroupAttributeDefinition groupAttribute = new GroupAttributeDefinition();
            groupAttribute.setName(groupAttributeRequest.getName());
            groupAttribute.setDescription(groupAttributeRequest.getDescription());
            groupAttribute.setType(groupAttributeRequest.getType());
            groupAttribute.setRequired(groupAttributeRequest.isRequired());
            groupAttribute.setMultivalued(groupAttributeRequest.isMultivalued());

            // add group attribute
            groupAttribute = schemaManager.addGroupAttribute(groupAttribute);
            AddGroupAttributeResponse groupAttributeResponse = new AddGroupAttributeResponse();
            groupAttributeResponse.setGroupAttribute(groupAttribute);

            recordInfoAuditTrail(Action.ADD_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);

            return groupAttributeResponse;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.ADD_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public UpdateGroupAttributeResponse updateGroupAttribute(UpdateGroupAttributeRequest groupAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("groupAttributeId", groupAttributeRequest.getGroupAttribute().getId());

        try {
            GroupAttributeDefinition groupAttribute = groupAttributeRequest.getGroupAttribute();

            GroupAttributeDefinition oldGroupAttribute = schemaManager.findGroupAttributeById(groupAttribute.getId());

            BeanUtils.copyProperties(groupAttribute, oldGroupAttribute, new String[] {"id"});

            groupAttribute = schemaManager.updateGroupAttribute(oldGroupAttribute);

            UpdateGroupAttributeResponse groupAttributeResponse = new UpdateGroupAttributeResponse();
            groupAttributeResponse.setGroupAttribute(groupAttribute);

            auditProps.setProperty("groupAttributeName", groupAttribute.getName());
            recordInfoAuditTrail(Action.UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);

            return groupAttributeResponse;

        } catch (GroupAttributeNotFoundException e) {
            auditProps.setProperty("groupAttributeNotFound", "true");
            recordInfoAuditTrail(Action.UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.UPDATE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public RemoveGroupAttributeResponse removeGroupAttribute(RemoveGroupAttributeRequest groupAttributeRequest) throws ProvisioningException {
        Properties auditProps = new Properties();
        auditProps.setProperty("groupAttributeId", groupAttributeRequest.getId());

        try {
            schemaManager.deleteGroupAttribute(groupAttributeRequest.getId());
            recordInfoAuditTrail(Action.REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.SUCCESS, null, auditProps);
            return new RemoveGroupAttributeResponse();
        } catch (GroupAttributeNotFoundException e) {
            auditProps.setProperty("groupAttributeNotFound", "true");
            recordInfoAuditTrail(Action.REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw e;
        } catch (Exception e) {
            recordInfoAuditTrail(Action.REMOVE_GROUP_ATTRIBUTE.getValue(), ActionOutcome.FAILURE, null, auditProps);
            throw new ProvisioningException(e);
        }
    }

    public FindGroupAttributeByIdResponse findGroupAttributeById(FindGroupAttributeByIdRequest groupAttributeRequest) throws ProvisioningException {
        try {
            GroupAttributeDefinition groupAttribute = schemaManager.findGroupAttributeById(groupAttributeRequest.getId());
            FindGroupAttributeByIdResponse groupAttributeResponse = new FindGroupAttributeByIdResponse();
            groupAttributeResponse.setGroupAttribute(groupAttribute);
            return groupAttributeResponse;
        } catch (GroupAttributeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public FindGroupAttributeByNameResponse findGroupAttributeByName(FindGroupAttributeByNameRequest groupAttributeRequest) throws ProvisioningException {
        try {
            GroupAttributeDefinition groupAttribute = schemaManager.findGroupAttributeByName(groupAttributeRequest.getName());
            FindGroupAttributeByNameResponse groupAttributeResponse = new FindGroupAttributeByNameResponse();
            groupAttributeResponse.setGroupAttribute(groupAttribute);
            return groupAttributeResponse;
        } catch (GroupAttributeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public ListGroupAttributesResponse listGroupAttributes(ListGroupAttributesRequest groupAttributeRequest) throws ProvisioningException {
        try {
            Collection<GroupAttributeDefinition> groupAttributes = schemaManager.listGroupAttributes();

            ListGroupAttributesResponse groupAttributeResponse = new ListGroupAttributesResponse();
            groupAttributeResponse.setGroupAttributes(groupAttributes.toArray(new GroupAttributeDefinition[groupAttributes.size()]));

            return groupAttributeResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }


    public ListSecurityQuestionsResponse listSecurityQuestions(ListSecurityQuestionsRequest request) throws ProvisioningException {
        try {
            Collection<SecurityQuestion> securityQuestions = mediationPartition.findAllSecurityQuestions();
            ListSecurityQuestionsResponse response = new ListSecurityQuestionsResponse();
            response.setSecurityQuestions(securityQuestions.toArray(new SecurityQuestion[securityQuestions.size()]));

            return response;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    protected String createPasswordHash(String password, String salt) throws ProvisioningException {

        // If none of this properties are set, do nothing ...
        if (getHashAlgorithm() == null && getHashEncoding() == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding/salt [" + getHashAlgorithm() + "/" + getHashEncoding() + "/" + salt + "]");

        if (salt != null)
            password = salt + password;

        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        try {
            if (hashCharset == null)
                passBytes = password.getBytes();
            else
                passBytes = password.getBytes(hashCharset);
        } catch (UnsupportedEncodingException e) {
            logger.error("charset " + hashCharset + " not found. Using platform default.");
            passBytes = password.getBytes();
        }

        // calculate the hash and apply the encoding.
        try {

            byte[] hash;
            // Hash algorithm is optional
            if (hashAlgorithm != null)
                hash = getDigest().digest(passBytes);
            else
                hash = passBytes;

            // At this point, hashEncoding is required.
            if ("BASE64".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase64(hash);

            } else if ("HEX".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase16(hash);

            } else if (hashEncoding == null) {
                logger.error("You must specify a hashEncoding when using hashAlgorithm");

            } else {
                logger.error("Unsupported hash encoding format " + hashEncoding);

            }

        } catch (Exception e) {
            logger.error("Password hash calculation failed : \n" + e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }

        return passwordHash;

    }

    protected void storePendingTransaction(PendingTransaction t) {
        transactionStore.store(t);
    }

    /**
     * Looks for transactions by ID and Code
     */
    protected PendingTransaction consumePendingTransaction(String idOrCode) throws ProvisioningException {

        // Transaction Code is optional
        PendingTransaction t = transactionStore.retrieve(idOrCode);
        if (t != null) {
            // Did the transaction already expired
            long now = System.currentTimeMillis();
            if (t.getExpiresOn() < now) {
                if (logger.isDebugEnabled())
                    logger.trace("Transaction has expired ID Or Code : " + idOrCode);

                throw new TransactionExpiredException(idOrCode);
            }
            return t;
        }

        if (logger.isDebugEnabled())
            logger.trace("Transaction not found ID Or Code : " + idOrCode);

        throw new TransactionInvalidException(idOrCode);
    }
    
    /**
     * Only invoke this if algorithm is set.
     *
     * @throws ProvisioningException
     */
    protected MessageDigest getDigest() throws ProvisioningException {

        MessageDigest digest = null;
        if (hashAlgorithm != null) {

            try {
                digest = MessageDigest.getInstance(hashAlgorithm);
                logger.debug("Using hash algorithm/encoding : " + hashAlgorithm + "/" + hashEncoding);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + hashAlgorithm, e);
                throw new ProvisioningException(e.getMessage(), e);
            }
        }

        return digest;

    }

    public int getMaxTimeToLive() {
        return maxTimeToLive;
    }

    public void setMaxTimeToLive(int maxTimeToLive) {
        this.maxTimeToLive = maxTimeToLive;
    }

    public String getSaltValue() {
        return saltValue;
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }


    public void validatePassword(String password) throws IllegalPasswordException {

        if (passwordPolicies.size() > 0) {

            List<PolicyEnforcementStatement> allStmts = new ArrayList<PolicyEnforcementStatement>();

            for (PasswordPolicy passwordPolicy : passwordPolicies) {
                List<PolicyEnforcementStatement> stmts = passwordPolicy.validate(password);
                if (stmts != null)
                    allStmts.addAll(stmts);

            }

            if (allStmts.size() > 0)
                throw new IllegalPasswordException(allStmts);

            return;
        }


        if (password == null || password.length() < 6) {
            throw new IllegalPasswordException("Password is too short");
        }

        if (dictionary.contains(password))
            throw new IllegalPasswordException("Password is in dictionary");

        // Look for password in dictionary

        // Keep password history/avoid repeating last N passwords

    }

    public void validateSecurityQuestions(UserSecurityQuestion[] secQuestions) throws IllegalCredentialException {
        Set<Long> usedIds = new HashSet<Long>();
        Set<String> usedAnswers = new HashSet<String>();
        Set<String> usedQuestions = new HashSet<String>();

        for (UserSecurityQuestion usq : secQuestions) {

            if (usq.getQuestion() != null) {
                if (usedIds.contains(usq.getQuestion().getId())) {
                    throw new IllegalCredentialException("Duplicated Security Question " + usq.getQuestion().getMessageKey());
                }
            } else if (usq.getCustomMessage() != null) {

                if (usedQuestions.contains(usq.getCustomMessage().toLowerCase()))
                    throw new IllegalCredentialException("Duplicated custom question");

                usedQuestions.add(usq.getCustomMessage().toLowerCase());

            } else {
                throw new IllegalCredentialException("User security question custom message and referred question cannot be null");
            }

            if (usedAnswers.contains(usq.getAnswer()))
                throw new IllegalCredentialException("Duplicated answer");

            usedAnswers.add(usq.getAnswer());

        }
    }

    @Override
    public void purgeOldTransactions() {
        // Not required  ...
    }

    protected String generateSalt() {
        if (saltLength < 1)
            return saltValue;
        byte[] saltBytes = new byte[saltLength];
        saltRandomizer.nextBytes(saltBytes);

        String salt = new String(Base64.encodeBase64(saltBytes));

        return salt;
    }

    protected void recordInfoAuditTrail(String action, ActionOutcome actionOutcome, String principal, Properties props) {
        if (aServer != null)
            aServer.processAuditTrail(auditCategory, "INFO", action, actionOutcome, principal != null ? principal : "UNKNOWN", new Date(), null, props);
    }

    public AuditingServer getAuditingServer() {
        return aServer;
    }

    public void setAuditingServer(AuditingServer aServer) {
        this.aServer = aServer;
    }

    public String getAuditCategory() {
        return auditCategory;
    }

    public void setAuditCategory(String auditCategory) {
        this.auditCategory = auditCategory;
    }

    public UUIDGenerator getUuid() {
        return uuid;
    }

    public void setUuid(UUIDGenerator uuid) {
        this.uuid = uuid;
    }

    public UUIDGenerator getShortIdGen() {
        return shortIdGen;
    }

    public void setShortIdGen(UUIDGenerator shortIdGen) {
        this.shortIdGen = shortIdGen;
    }

    public TransactionStore getTransactionStore() {
        return transactionStore;
    }

    public void setTransactionStore(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }
}
