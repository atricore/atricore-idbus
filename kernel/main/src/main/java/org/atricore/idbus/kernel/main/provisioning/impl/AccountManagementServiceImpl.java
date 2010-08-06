package org.atricore.idbus.kernel.main.provisioning.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityVault;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.IdentityPartitionDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.IdentityVaultDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.spi.AccountManagementService;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccountManagementServiceImpl implements AccountManagementService, InitializingBean, DisposableBean {

    private static Log logger = LogFactory.getLog(AccountManagementServiceImpl.class.getName() );

    private static String AND = "AND";

    IdentityPartitionDAOImpl identityPartitionDao;

    IdentityVaultDAOImpl identityVaultDao;

    private Map<String, IdentityPartitionManagerImpl> partitionManagers = new HashMap<String, IdentityPartitionManagerImpl>();

    public AccountManagementServiceImpl() {
    }

    public void destroy() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() {
        Collection<IdentityPartition> partitions = identityPartitionDao.findAll();
        for (IdentityPartition partition : partitions) {

            IdentityVault vault = partition.getVault();

            // TODO Initialize Derby server for vault, if not already done.

            // TODO Initialize PMF

            // TODO Partition Manager
        }


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
