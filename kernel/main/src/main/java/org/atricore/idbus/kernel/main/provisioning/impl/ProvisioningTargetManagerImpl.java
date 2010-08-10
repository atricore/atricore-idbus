package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityVault;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.GroupDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.UserDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.GroupDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.UserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTargetManager;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.datanucleus.jdo.JDOPersistenceManagerFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProvisioningTargetManagerImpl implements ProvisioningTargetManager {
    
    private IdentityVault identityVault;
    
    private IdentityPartition identityPartition;
    
    private UserDAO userDao;
    
    private GroupDAO groupDao;
    
    private PersistenceManagerFactory pmf;
    
    private PersistenceManager pm;


    public ProvisioningTargetManagerImpl(IdentityVault identityVault, IdentityPartition identityPartition) {
        this.identityVault = identityVault;
        this.identityPartition = identityPartition;
    }

    public ProvisioningTargetManagerImpl() {
    }

    public void init() {

        pm = pmf.getPersistenceManager();
        
        // Create DAOs ...
        UserDAOImpl userDao = new UserDAOImpl();
        userDao.setPm(pm);
        this.userDao = userDao;
        
        GroupDAOImpl groupDao = new GroupDAOImpl();
        groupDao.setPm(pm);
        this.groupDao = groupDao;
    }
    
    public void shutdown() {
        
        if (pm  != null)
            try { pm.close(); } catch (Exception e) { /**/ }
        
    }

    public IdentityVault getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentityVault identityVault) {
        this.identityVault = identityVault;
    }

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

    public UserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDAO userDao) {
        this.userDao = userDao;
    }

    public GroupDAO getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDAO groupDao) {
        this.groupDao = groupDao;
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    public PersistenceManager getPm() {
        return pm;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ListGroupResponse getGroups() throws ProvisioningException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ListUserResponse getUsers() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
