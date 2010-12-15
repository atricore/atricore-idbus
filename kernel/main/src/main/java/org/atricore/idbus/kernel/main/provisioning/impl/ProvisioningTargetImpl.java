package org.atricore.idbus.kernel.main.provisioning.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.authn.util.Crypt;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProvisioningTargetImpl implements ProvisioningTarget {

    private static final Log logger = LogFactory.getLog(ProvisioningTargetImpl.class);

    private String name;

    private String description;
    
    private String hashEncoding;
    
    private String hashAlgorithm;
    
    private String hashCharset;
    
    private int saltLenght;
    
    private IdentityPartition identityPartition;

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

    public int getSaltLenght() {
        return saltLenght;
    }

    public void setSaltLenght(int saltLenght) {
        this.saltLenght = saltLenght;
    }

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

   
    public void deleteGroup(long id) throws ProvisioningException {
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

        try {
            Group group = new Group();
            group.setName(groupRequest.getName());
            group.setDescription(groupRequest.getDescription());
            group = identityPartition.addGroup(group);
            AddGroupResponse groupResponse = new AddGroupResponse();
            groupResponse.setGroup(group);
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }


    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        try {
            
            Group group = identityPartition.findGroupById(groupRequest.getId());

            if (groupRequest.getName() != null)
                group.setName(groupRequest.getName());

            if (groupRequest.getDescription() != null)
                group.setDescription(groupRequest.getDescription());

            group = identityPartition.updateGroup(group);

            UpdateGroupResponse groupResponse = new UpdateGroupResponse();
            groupResponse.setGroup(group);

            return groupResponse;
        } catch (GroupNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        try {
            identityPartition.deleteGroup(groupRequest.getId());
            return new RemoveGroupResponse();
        } catch (GroupNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws ProvisioningException {
        try {
            identityPartition.deleteUser(userRequest.getId());
            return new RemoveUserResponse();
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws ProvisioningException {
        try {
            
            User user = new User();

            BeanUtils.copyProperties(userRequest, user, new String[] {"groups", "userPassword"});

            // TODO : Apply password validation rules
            user.setUserPassword(createPasswordHash(userRequest.getUserPassword()));
                
            Group[] groups = userRequest.getGroups();
            user.setGroups(groups);
            
            user = identityPartition.addUser(user);
            AddUserResponse userResponse = new AddUserResponse();
            userResponse.setUser(user);

            return userResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
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
        // TODO
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws ProvisioningException {
        try {
            
            User user = userRequest.getUser();

            User oldUser = identityPartition.findUserById(user.getId());


            BeanUtils.copyProperties(user, oldUser, new String[] {"groups", "userPassword", "id"});
            oldUser.setGroups(user.getGroups());

            if (user.getUserPassword() != null && !"".equals(user.getUserPassword())) {
                // TODO : Apply password validation rules
                oldUser.setUserPassword(createPasswordHash(user.getUserPassword()));
            }

            user = identityPartition.updateUser(oldUser);

            UpdateUserResponse userResponse = new UpdateUserResponse();
            userResponse.setUser(user);

            return userResponse;

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    
    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByUserRequest) throws ProvisioningException {
        // TODO !
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public SetPasswordResponse setPassword(SetPasswordRequest setPwdRequest) throws ProvisioningException {
        try {
            User user = identityPartition.findUserById(setPwdRequest.getUserId());


            String currentPwd = createPasswordHash(setPwdRequest.getCurrentPassword());
            if (!user.getUserPassword().equals(currentPwd)) {
                throw new ProvisioningException("Provided password is invalid");
            }

            // TODO : Apply password validation rules
            String newPwd = createPasswordHash(setPwdRequest.getNewPassword());
            user.setUserPassword(newPwd);
            identityPartition.updateUser(user);
            SetPasswordResponse setPwdResponse = new SetPasswordResponse();
            
            return setPwdResponse;
            
        } catch (Exception e) {
            throw new ProvisioningException("Cannot update user password", e);
        }
    }
    
    protected String createPasswordHash(String password) throws ProvisioningException {

        // If none of this properties are set, do nothing ...
        if (getHashAlgorithm() == null && getHashEncoding() == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding [" + getHashAlgorithm() + "/" + getHashEncoding() + "]");

        // Check for spetial encryption mechanisms, not supported by the JDK
        /* TODO
        if ("CRYPT".equalsIgnoreCase(getHashAlgorithm())) {
            // Get known password
            String knownPassword = getPassword(getKnownCredentials());
            String salt = knownPassword != null && knownPassword.length() > 1 ? knownPassword.substring(0, saltLenght) : "";

            return Crypt.crypt(salt, password);

        } */

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
    
}
