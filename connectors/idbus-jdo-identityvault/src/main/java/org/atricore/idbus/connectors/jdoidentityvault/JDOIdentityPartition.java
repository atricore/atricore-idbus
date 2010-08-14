package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOGroupDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOUserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.request.*;
import org.atricore.idbus.kernel.main.provisioning.spi.response.*;
import org.springframework.beans.BeanUtils;
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

    // -------------------------------------< Group >

    @Transactional
    public Group findGroupById(long id) throws ProvisioningException {

        // TODO : Throw group not found exception
        try {
            JDOGroup jdoGroup = groupDao.findById(id);
            return toGroup(jdoGroup);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Group findGroupByName(String name) throws ProvisioningException {
        // TODO : Throw group not found exception
        try {
            JDOGroup jdoGroup = groupDao.findByName(name);
            return toGroup(jdoGroup);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<Group> findAllGroups() throws ProvisioningException {
        try {
            Collection<JDOGroup> jdoGroups = groupDao.findAll();
            return toGroups(jdoGroups);

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Group updateGroup(Group group) throws ProvisioningException {
        try {
            JDOGroup jdoGroup = groupDao.findById(group.getId());
            jdoGroup = toJDOGroup(jdoGroup, group);
            jdoGroup = groupDao.save(jdoGroup);
            return toGroup(jdoGroup);

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Group addGroup(Group group) throws ProvisioningException {

        try {
            JDOGroup jdoGroup = toJDOGroup(group);
            jdoGroup = groupDao.save(jdoGroup);
            return toGroup(jdoGroup);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }
    @Transactional
    public void deleteGroup(long id) throws ProvisioningException {
        try {
            groupDao.delete(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

    }


    // -------------------------------------< User >

    @Transactional
    public Collection<User> getUsersByGroup(Group group) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

    @Transactional
    public User findUserById(long id) throws ProvisioningException {
        try {
            // TODO : Throw group not found exception
            JDOUser jdoUser = userDao.findById(id);
            return toUser(jdoUser);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User findUserByUserName(String username) throws ProvisioningException {
        // TODO : Throw group not found exception
        try {
            JDOUser jdoUser = userDao.findByUserName(username);
            return toUser(jdoUser);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<User> findAllUsers() throws ProvisioningException {
        try {
            Collection<JDOUser> jdoUsers = userDao.findAll();
            return toUsers(jdoUsers);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User addUser(User user) throws ProvisioningException {
        try {
            JDOUser jdoUser = toJDOUser(user);
            jdoUser = userDao.save(jdoUser);
            return toUser(jdoUser);

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User updateUser(User user) throws ProvisioningException {
        try {
            JDOUser jdoUser = userDao.findById(user.getId());
            toJDOUser(jdoUser, user);
            jdoUser = userDao.save(jdoUser);
            return toUser(jdoUser);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public void deleteUser(long id) throws ProvisioningException {
        try {
            userDao.delete(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
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

    protected Group[] toGroupsArray(Collection<JDOGroup> jdoGroups) {

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

    protected Collection<Group> toGroups(Collection<JDOGroup> jdoGroups) {
        List<Group> groups = new ArrayList<Group>(jdoGroups.size());
        for (JDOGroup jdoGroup : jdoGroups) {
            groups.add(toGroup(jdoGroup));
        }

        return groups;

    }

    protected JDOUser toJDOUser(User user) {
        JDOUser jdoUser = new JDOUser();
        return toJDOUser(jdoUser, user);
    }

    protected JDOUser toJDOUser(JDOUser jdoUser, User user) {
        BeanUtils.copyProperties(user, jdoUser);
        // TODO : Groups
        return jdoUser;
    }

    protected User toUser(JDOUser jdoUser) {
        User user = new User();
        // TODO : Groups
        BeanUtils.copyProperties(jdoUser, user);
        return user;

    }
    
    protected Collection<User> toUsers(Collection<JDOUser> jdoUsers) {
        List<User> users = new ArrayList<User>(jdoUsers.size());
        for (JDOUser jdoUser : jdoUsers) {
            users.add(toUser(jdoUser));
        }

        return users;

    }
    

}


