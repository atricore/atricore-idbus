package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOGroupDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOUserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityPartition extends AbstractIdentityPartition implements InitializingBean, DisposableBean {

    private JDOUserDAOImpl userDao;
    private JDOGroupDAOImpl groupDao;

    public void setUserDao(JDOUserDAOImpl userDao) {
        this.userDao = userDao;
    }

    public JDOUserDAOImpl getUserDao() {
        return userDao;
    }

    public void setGroupDao(JDOGroupDAOImpl groupDao) {
        this.groupDao = groupDao;
    }

    public JDOGroupDAOImpl getGroupDao() {
        return groupDao;
    }


    public void afterPropertiesSet() throws ProvisioningException {
        init();
    }

    public void destroy() throws ProvisioningException {

    }

    public void init() throws ProvisioningException {
    }

    @Transactional
    public RemoveGroupResponse removeGroup(RemoveGroupRequest groupRequest) throws ProvisioningException {
        try {
            groupDao.delete(groupRequest.getId());
            return new RemoveGroupResponse ();
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

    }

    @Transactional
    public AddGroupResponse addGroup(AddGroupRequest groupRequest) throws ProvisioningException {

        try {
            JDOGroup jdoGroup = new JDOGroup();
            jdoGroup.setName(groupRequest.getName());
            jdoGroup.setDescription(groupRequest.getDescription());
            jdoGroup = groupDao.save(jdoGroup);

            AddGroupResponse groupResponse = new AddGroupResponse();

            Group group = toGroup(jdoGroup);
            groupResponse.setGroup(group);

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest groupRequest) throws ProvisioningException {

        // TODO : Throw group not found exception
        try {
            JDOGroup jdoGroup = groupDao.findById(groupRequest.getId());
            FindGroupByIdResponse groupResponse = new FindGroupByIdResponse ();
            groupResponse.setGroup(toGroup(jdoGroup));
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public FindGroupByNameResponse findGroupByName(FindGroupByNameRequest groupRequest) throws ProvisioningException {

        // TODO : Throw group not found exception
        try {
            JDOGroup jdoGroup = groupDao.findByName(groupRequest.getName());
            FindGroupByNameResponse groupResponse = new FindGroupByNameResponse ();
            groupResponse.setGroup(toGroup(jdoGroup));
            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public ListGroupsResponse listGroups(ListGroupsRequest groupRequest) throws ProvisioningException {
        try {
            Collection<JDOGroup> jdoGroups = groupDao.findAll();

            ListGroupsResponse groupResponse = new ListGroupsResponse();
            groupResponse.setGroups(toGroups(jdoGroups));

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public SearchGroupResponse searchGroups(SearchGroupRequest groupRequest) throws ProvisioningException {
        String name = groupRequest.getName();
        String descr = groupRequest.getDescription();
        if (descr != null)
            throw new ProvisioningException("Group search by description not supported");

        if (name == null)
            throw new ProvisioningException("Name or description must be specified");
        try {

            JDOGroup jdoGroup = groupDao.findByName(name);
            List<Group> groups = new ArrayList<Group>();
            groups.add(toGroup(jdoGroup));

            SearchGroupResponse groupResponse = new SearchGroupResponse();
            groupResponse.setGroups(groups);

            return groupResponse;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public UpdateGroupResponse updateGroup(UpdateGroupRequest groupRequest) throws ProvisioningException {
        try {
            JDOGroup jdoGroup = groupDao.findById(groupRequest.getId());

            jdoGroup.setName(groupRequest.getName());
            jdoGroup.setDescription(groupRequest.getDescription());

            jdoGroup = groupDao.save(jdoGroup);

            UpdateGroupResponse groupResponse = new UpdateGroupResponse();
            groupResponse.setGroup(toGroup(jdoGroup));

            return groupResponse;

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public RemoveUserResponse removeUser(RemoveUserRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public AddUserResponse addUser(AddUserRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public FindUserByIdResponse findUserById(FindUserByIdRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public FindUserByUsernameResponse findUserByUsername(FindUserByUsernameRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public ListUsersResponse listUsers(ListUsersRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public SearchUserResponse searchUsers(SearchUserRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public GetUsersByGroupResponse getUsersByGroup(GetUsersByGroupRequest usersByGroupRequest) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    // -------------------------------------------< Utils >

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


