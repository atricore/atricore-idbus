package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.*;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOGroupDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.JDOUserDAOImpl;
import org.atricore.idbus.kernel.common.support.services.IdentityServiceLifecycle;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeValue;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeValue;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.jdo.JdoObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.FetchPlan;
import javax.jdo.JDOObjectNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityPartition extends AbstractIdentityPartition
        implements InitializingBean,
        DisposableBean,
        IdentityServiceLifecycle {

    private JDOUserDAOImpl userDao;
    private JDOGroupDAOImpl groupDao;

    private JDOSchemaManager schemaManager;

    public JDOSchemaManager getSchemaManager() {
        return schemaManager;
    }

    public void setSchemaManager(JDOSchemaManager schemaManager) {
        this.schemaManager = schemaManager;
    }

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

    public void boot() throws Exception {

    }

    public void afterPropertiesSet() throws ProvisioningException {
        init();
    }

    public void destroy() throws ProvisioningException {

    }

    public void init() throws ProvisioningException {

        if (getIdentityStore() == null) {
            JDOIdentityStore store = new JDOIdentityStore();
            store.setIdPartition(this);
            setIdentityStore(store);
        }

    }

    // -------------------------------------< Group >

    @Transactional
    public Group findGroupById(long id) throws ProvisioningException {

        try {
            JDOGroup jdoGroup = groupDao.findById(id);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroup(jdoGroup);

        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new GroupNotFoundException(id);

            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new GroupNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Group findGroupByName(String name) throws ProvisioningException {

        try {
            JDOGroup jdoGroup = groupDao.findByName(name);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroup(jdoGroup);

        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new GroupNotFoundException(name);

            throw new ProvisioningException(e);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<Group> findGroupsByUserName(String userName) throws ProvisioningException {

        try {
            Collection<JDOGroup> jdoGroups = groupDao.findByUserName(userName);
            jdoGroups = groupDao.detachCopyAll(jdoGroups, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroups(jdoGroups);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }


    @Transactional
    public Collection<Group> findAllGroups() throws ProvisioningException {
        try {
            Collection<JDOGroup> jdoGroups = groupDao.findAll();
            jdoGroups = groupDao.detachCopyAll(jdoGroups, FetchPlan.FETCH_SIZE_GREEDY);
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
            jdoGroup = groupDao.detachCopy(jdoGroup, 99);

            return toGroup(jdoGroup);

        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupNotFoundException(group.getId());
        } catch (JDOObjectNotFoundException e) {
            throw new GroupNotFoundException(group.getId());
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupNotFoundException(group.getId());
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Group addGroup(Group group) throws ProvisioningException {

        try {
            JDOGroup jdoGroup = toJDOGroup(group);
            jdoGroup = groupDao.save(jdoGroup);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroup(jdoGroup);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }
    @Transactional
    public void deleteGroup(long id) throws ProvisioningException {
        try {
            groupDao.delete(id);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new GroupNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupNotFoundException(id);
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

            JDOUser jdoUser = userDao.findById(id);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            return toUser(jdoUser, true);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new UserNotFoundException(id);

            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new UserNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new UserNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User findUserByUserName(String username) throws ProvisioningException {
        
        try {
            JDOUser jdoUser = userDao.findByUserName(username);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            return toUser(jdoUser, true);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new UserNotFoundException(username);

            throw new ProvisioningException(e);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<User> findAllUsers() throws ProvisioningException {
        try {
            Collection<JDOUser> jdoUsers = userDao.findAll();
            jdoUsers = userDao.detachCopyAll(jdoUsers, FetchPlan.FETCH_SIZE_GREEDY);
            return toUsers(jdoUsers, true);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User addUser(User user) throws ProvisioningException {
        try {
            JDOUser jdoUser = toJDOUser(user, false);
            jdoUser = userDao.save(jdoUser);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            return toUser(jdoUser, true);

        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public User updateUser(User user) throws ProvisioningException {
        try {
            JDOUser jdoUser = userDao.findById(user.getId());
            
            // Do not let users to change the password!
            toJDOUser(jdoUser, user, false);
            jdoUser = userDao.save(jdoUser);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            return toUser(jdoUser, true);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserNotFoundException(user.getId());
        } catch (JDOObjectNotFoundException e) {
            throw new UserNotFoundException(user.getId());
        } catch (NucleusObjectNotFoundException e) {
            throw new UserNotFoundException(user.getId());
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public void deleteUser(long id) throws ProvisioningException {
        try {
            userDao.delete(id);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new UserNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new UserNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    // -------------------------------------------< Utils >

    protected JDOGroup toJDOGroup(Group group) {
        JDOGroup jdoGroup = toJDOGroup(new JDOGroup(), group);
        jdoGroup.setId(group.getId());
        return jdoGroup;
    }

    protected JDOGroup toJDOGroup(JDOGroup jdoGroup, Group group) {
        jdoGroup.setName(group.getName());
        jdoGroup.setDescription(group.getDescription());

        if (group.getAttrs() != null) {
            JDOGroupAttributeValue[] jdoAttrs = new JDOGroupAttributeValue[group.getAttrs().length];

            for (int i = 0; i < group.getAttrs().length; i++) {
                GroupAttributeValue attr = group.getAttrs()[i];
                JDOGroupAttributeValue jdoAttr = new JDOGroupAttributeValue();
                jdoAttr.setId(attr.getId());
                jdoAttr.setName(attr.getName());
                jdoAttr.setValue(attr.getValue());

                jdoAttrs[i] = jdoAttr;
            }

            jdoGroup.setAttrs(jdoAttrs);
        }

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

        if (jdoGroup.getAttrs() != null) {
            GroupAttributeValue[] attrs = new GroupAttributeValue[jdoGroup.getAttrs().length];

            for (int i = 0; i < jdoGroup.getAttrs().length; i++) {
                JDOGroupAttributeValue jdoAttr = jdoGroup.getAttrs()[i];
                GroupAttributeValue groupAttribute = new GroupAttributeValue();
                groupAttribute.setId(jdoAttr.getId());
                groupAttribute.setName(jdoAttr.getName());
                groupAttribute.setValue(jdoAttr.getValue());

                attrs[i] = groupAttribute;
            }

            group.setAttrs(attrs);
        }

        return group;
    }

    protected Collection<Group> toGroups(Collection<JDOGroup> jdoGroups) {
        List<Group> groups = new ArrayList<Group>(jdoGroups.size());
        for (JDOGroup jdoGroup : jdoGroups) {
            groups.add(toGroup(jdoGroup));
        }

        return groups;

    }

    protected JDOUser toJDOUser(User user, boolean keepUserPassword) {
        JDOUser jdoUser = new JDOUser();
        jdoUser.setId(user.getId());
        return toJDOUser(jdoUser, user, keepUserPassword);
    }

    protected JDOUser toJDOUser(JDOUser jdoUser, User user, boolean keepUserPassword) {


        String pwd = jdoUser.getUserPassword();

        BeanUtils.copyProperties(user, jdoUser, new String[] {"id", "groups", "attrs"});

        if (keepUserPassword)
            jdoUser.setUserPassword(pwd);

        if (user.getGroups() != null) {
            JDOGroup[] jdoGroups = new JDOGroup[user.getGroups().length];
            for (int i = 0; i < user.getGroups().length; i++) {
                Group group = user.getGroups()[i];
                JDOGroup jdoGroup = groupDao.findById(group.getId());
                jdoGroups[i] = jdoGroup;
            }
            jdoUser.setGroups(jdoGroups);
        }

        if (user.getAttrs() != null) {
            JDOUserAttributeValue[] jdoAttrs = new JDOUserAttributeValue[user.getAttrs().length];

            for (int i = 0; i < user.getAttrs().length; i++) {
                UserAttributeValue attr = user.getAttrs()[i];
                JDOUserAttributeValue jdoAttr = new JDOUserAttributeValue();
                jdoAttr.setId(attr.getId());
                jdoAttr.setName(attr.getName());
                jdoAttr.setValue(attr.getValue());

                jdoAttrs[i] = jdoAttr;
            }

            jdoUser.setAttrs(jdoAttrs);
        }

        return jdoUser;
    }

    protected User toUser(JDOUser jdoUser, boolean retrieveUserPassword) {
        User user = new User();
        BeanUtils.copyProperties(jdoUser, user, new String[] {"groups", "attrs"});

        if (!retrieveUserPassword)
            user.setUserPassword(null);

        if (jdoUser.getGroups() != null) {
            Group[] groups = new Group[jdoUser.getGroups().length];

            for (int i = 0; i < jdoUser.getGroups().length; i++) {
                JDOGroup jdoGroup = jdoUser.getGroups()[i];
                Group group = new Group();
                group.setName(jdoGroup.getName());
                group.setDescription(jdoGroup.getDescription());
                group.setId(jdoGroup.getId());

                groups[i] = group;
            }

            user.setGroups(groups);
        }

        if (jdoUser.getAttrs() != null) {
            UserAttributeValue[] attrs = new UserAttributeValue[jdoUser.getAttrs().length];

            for (int i = 0; i < jdoUser.getAttrs().length; i++) {
                JDOUserAttributeValue jdoAttr = jdoUser.getAttrs()[i];
                UserAttributeValue userAttribute = new UserAttributeValue();
                userAttribute.setId(jdoAttr.getId());
                userAttribute.setName(jdoAttr.getName());
                userAttribute.setValue(jdoAttr.getValue());

                attrs[i] = userAttribute;
            }

            user.setAttrs(attrs);
        }

        return user;

    }
    
    protected Collection<User> toUsers(Collection<JDOUser> jdoUsers, boolean retrieveUserPassword) {
        List<User> users = new ArrayList<User>(jdoUsers.size());
        for (JDOUser jdoUser : jdoUsers) {
            users.add(toUser(jdoUser, retrieveUserPassword));
        }

        return users;

    }
    

}


