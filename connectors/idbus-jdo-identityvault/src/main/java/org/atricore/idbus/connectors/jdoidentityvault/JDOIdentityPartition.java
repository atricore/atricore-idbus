package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOGroupDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOUserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.Collection;
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
        JDOGroup jdoGroup = new JDOGroup();
        jdoGroup.setName(groupRequest.getName());
        jdoGroup.setDescription(groupRequest.getDescription());
        jdoGroup = groupDao.createObject(jdoGroup);

        AddGroupResponse groupResponse = new AddGroupResponse();

        Group group = toGroup(jdoGroup);
        groupResponse.setGroup(group);

        return groupResponse;
    }

    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws GroupNotFoundException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws GroupNotFoundException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    public ListGroupsResponse listGroups(ListGroupsRequest groupRequest) throws ProvisioningException {
        Collection<JDOGroup> jdoGroups = groupDao.findAll();

        ListGroupsResponse groupResponse = new ListGroupsResponse();
        groupResponse.setGroups(toGroups(jdoGroups));

        return groupResponse;
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

    protected JDOGroup toJDOGroup(Group group) {
        return toJDOGroup(new JDOGroup(), group);
    }

    protected JDOGroup toJDOGroup(JDOGroup jdoGroup, Group group) {
        jdoGroup.setName(group.getName());
        jdoGroup.setDescription(group.getDescription());

        return jdoGroup;

    }

    protected Group[] toGroups(Collection<JDOGroup> jdoGroups) {

        Group[] groups = new Group[jdoGroups.size()];
        int i = 0;
        for (JDOGroup jdoGroup : jdoGroups) {
            groups[i] = toGroup(jdoGroup);
            i ++;
        }
        return groups;

    }

    protected Group toGroup(JDOGroup jdoGroup) {
        Group group = new Group();

        group.setId(jdoGroup.getId());
        group.setName(jdoGroup.getName());
        group.setDescription(jdoGroup.getDescription());
        return group;
    }
}


