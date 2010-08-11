package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOGroupDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOUserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityPartition extends AbstractIdentityPartition implements InitializingBean, DisposableBean {

    private PersistenceManagerFactory pmf;
    private PersistenceManager pm;

    private JDOUserDAO userDao;

    private JDOGroupDAO groupDao;

    private Properties jdoProperties;

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void destroy() throws Exception {
        if (pm != null)
            try { pm.close(); } catch (Exception e) {/**/}
    }

    public void init() throws Exception {
        pm = pmf.getPersistenceManager();
        userDao = new JDOUserDAOImpl(pm);
        groupDao = new JDOGroupDAOImpl(pm);
    }

    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public ListGroupResponse getGroups() throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public AddUserResponse addUser(AddUserRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public ListUserResponse getUsers() throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws Exception {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public void setJdoProperties(Properties jdoProperties) {
        this.jdoProperties = jdoProperties;
    }

    public Properties getJdoProperties() {
        return jdoProperties;
    }
}


