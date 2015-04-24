package org.atricore.idbus.connectors.jdoidentityvault;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.*;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.*;
import org.atricore.idbus.kernel.common.support.services.IdentityServiceLifecycle;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.authn.SecurityTokenImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.*;
import org.atricore.idbus.kernel.main.provisioning.exception.*;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractIdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.MediationPartition;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.jdo.JdoObjectRetrievalFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jdo.FetchPlan;
import javax.jdo.JDOObjectNotFoundException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a JDO based implementation of both, an IdentityPartition and a MediationPartition
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityPartition extends AbstractIdentityPartition
        implements MediationPartition, InitializingBean,
        DisposableBean,
        IdentityServiceLifecycle {

    private static final Log logger = LogFactory.getLog(JDOIdentityPartition.class);

    private JDOUserDAO userDao;
    private JDOGroupDAO groupDao;
    private JDOAclDAO aclDao;
    private JDOAclEntryDAO aclEntryDao;
    private JDOSecurityQuestionDAO securityQuestionDAO;
    private JDOUserAttributeValueDAO usrAttrValDao;
    private JDOGroupAttributeValueDAO grpAttrValDao;
    private JDOUserSecurityQuestionDAO usrSecQuestionDao;
    private JDOSecurityTokenDAO securityTokenDao;

    private JDOSchemaManager schemaManager;

    // Spring transaction management
    private PlatformTransactionManager transactionManager;

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public JDOSchemaManager getSchemaManager() {
        return schemaManager;
    }

    public void setSchemaManager(JDOSchemaManager schemaManager) {
        this.schemaManager = schemaManager;
    }

    public void setUserDao(JDOUserDAO userDao) {
        this.userDao = userDao;
    }

    public JDOUserDAO getUserDao() {
        return userDao;
    }

    public void setGroupDao(JDOGroupDAO groupDao) {
        this.groupDao = groupDao;
    }

    public JDOGroupDAO getGroupDao() {
        return groupDao;
    }

    public JDOSecurityQuestionDAO getSecurityQuestionDAO() {
        return securityQuestionDAO;
    }

    public void setSecurityQuestionDAO(JDOSecurityQuestionDAO securityQuestionDAO) {
        this.securityQuestionDAO = securityQuestionDAO;
    }

    public JDOUserSecurityQuestionDAO getUsrSecQuestionDao() {
        return usrSecQuestionDao;
    }

    public void setUsrSecQuestionDao(JDOUserSecurityQuestionDAO usrSecQuestionDao) {
        this.usrSecQuestionDao = usrSecQuestionDao;
    }

    public JDOAclEntryDAO getAclEntryDao() {
        return aclEntryDao;
    }

    public void setAclEntryDao(JDOAclEntryDAO aclEntryDao) {
        this.aclEntryDao = aclEntryDao;
    }

    public JDOAclDAO getAclDao() {
        return aclDao;
    }

    public void setAclDao(JDOAclDAO aclDao) {
        this.aclDao = aclDao;
    }

    public JDOSecurityTokenDAO getSecurityTokenDao() {
        return securityTokenDao;
    }

    public void setSecurityTokenDao(JDOSecurityTokenDAO securityTokenDao) {
        this.securityTokenDao = securityTokenDao;
    }

    public void setUsrAttrValDao(JDOUserAttributeValueDAO usrAttrValDao) {
        this.usrAttrValDao = usrAttrValDao;
    }

    public void setGrpAttrValDao(JDOGroupAttributeValueDAO grpAttrValDao) {
        this.grpAttrValDao = grpAttrValDao;
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
            store.setPartition(this);
            setIdentityStore(store);
        }

    }

    // -------------------------------------< Group >

//    @Transactional
    protected Group findGroupById(long id) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroup jdoGroup = groupDao.findById(id);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroup(jdoGroup);

        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new GroupNotFoundException(id);

            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

    @Override
    public Group findGroupByOid(String oid) throws ProvisioningException {
        return findGroupById(Long.parseLong(oid));
    }

    //    @Transactional
    public Group findGroupByName(String name) throws ProvisioningException {


        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );
        
        try {
            JDOGroup jdoGroup = groupDao.findByName(name);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroup(jdoGroup);

        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new GroupNotFoundException(name);

            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<Group> findGroupsByUserName(String userName) throws ProvisioningException {
        
        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOGroup> jdoGroups = groupDao.findByUserName(userName);
            jdoGroups = groupDao.detachCopyAll(jdoGroups, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroups(jdoGroups);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }


//    @Transactional
    public Collection<Group> findAllGroups() throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOGroup> jdoGroups = groupDao.findAll();
            jdoGroups = groupDao.detachCopyAll(jdoGroups, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroups(jdoGroups);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Group updateGroup(Group group) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroup jdoGroup = groupDao.findById(Long.parseLong(group.getOid()));
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            List<JDOGroupAttributeValue> oldAttrsList = new ArrayList<JDOGroupAttributeValue>();
            if (jdoGroup.getAttrs() != null) {
                for (JDOGroupAttributeValue oldGroupAttr : jdoGroup.getAttrs()) {
                    if (oldGroupAttr.getId() > 0) {
                        oldAttrsList.add(oldGroupAttr);
                    }
                }

                if (oldAttrsList.size() != jdoGroup.getAttrs().length) {
                    jdoGroup = groupDao.findById(Long.parseLong(group.getOid()));
                    jdoGroup.setAttrs(oldAttrsList.toArray(new JDOGroupAttributeValue[]{}));
                    jdoGroup = groupDao.save(jdoGroup);
                }
            }

            jdoGroup = groupDao.findById(Long.parseLong(group.getOid()));
            JDOGroupAttributeValue[] oldAttrs = jdoGroup.getAttrs();
            jdoGroup = toJDOGroup(jdoGroup, group);
            jdoGroup = groupDao.save(jdoGroup);
            grpAttrValDao.deleteRemovedValues(oldAttrs, jdoGroup.getAttrs());
            jdoGroup = groupDao.detachCopy(jdoGroup, 99);

            transactionManager.commit(status);

            return toGroup(jdoGroup);

        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(group.getOid());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(group.getOid());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(group.getOid());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Group addGroup(Group group) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroup jdoGroup = toJDOGroup(group);
            jdoGroup = groupDao.save(jdoGroup);
            jdoGroup = groupDao.detachCopy(jdoGroup, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroup(jdoGroup);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public void deleteGroup(String oid) throws ProvisioningException {

        long id = Long.parseLong(oid);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroup jdoGroup = groupDao.findById(id);
            if (jdoGroup != null) {
                JDOGroupAttributeValue[] attrs = jdoGroup.getAttrs();
                jdoGroup.setAttrs(null);
                groupDao.save(jdoGroup);
                groupDao.flush();
                groupDao.delete(id);
                if (attrs != null) {
                    for (JDOGroupAttributeValue value : attrs)
                        grpAttrValDao.delete(value.getId());
                }
            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }

    }


    // -------------------------------------< User >

//    @Transactional
    public Collection<User> getUsersByGroup(Group group) throws ProvisioningException {
        throw new UnsupportedOperationException("Not Implemented yet!");
    }

//    @Transactional
    protected User findUserById(long id) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {

            JDOUser jdoUser = userDao.findById(id);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUser(jdoUser, true);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new UserNotFoundException(id);

            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

    public User findUserByOid(String oid) throws ProvisioningException {
        return findUserById(Long.parseLong(oid));
    }

//    @Transactional
    public User findUserByUserName(String username) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUser jdoUser = userDao.findByUserName(username);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUser(jdoUser, true);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new UserNotFoundException(username);

            throw new ProvisioningException(e);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<User> findAllUsers() throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOUser> jdoUsers = userDao.findAll();
            jdoUsers = userDao.detachCopyAll(jdoUsers, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUsers(jdoUsers, true);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public User addUser(User user) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUser jdoUser = toJDOUser(user, false);
            jdoUser = userDao.save(jdoUser);
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUser(jdoUser, true);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public User updateUser(User user) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUser jdoUser = userDao.findById(Long.parseLong(user.getOid()));
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            List<JDOUserAttributeValue> oldAttrsList = new ArrayList<JDOUserAttributeValue>();
            if (jdoUser.getAttrs() != null) {
                for (JDOUserAttributeValue oldUserAttr : jdoUser.getAttrs()) {
                    if (oldUserAttr.getId() > 0) {
                        oldAttrsList.add(oldUserAttr);
                    }
                }

                if (oldAttrsList.size() != jdoUser.getAttrs().length) {
                    jdoUser = userDao.findById(Long.parseLong(user.getOid()));
                    jdoUser.setAttrs(oldAttrsList.toArray(new JDOUserAttributeValue[]{}));
                    jdoUser = userDao.save(jdoUser);
                }
            }

            jdoUser = userDao.findById(Long.parseLong(user.getOid()));
            JDOUserAttributeValue[] oldAttrs = jdoUser.getAttrs();

            // Do not let users to change the password!
            toJDOUser(jdoUser, user, false);
            jdoUser = userDao.save(jdoUser);
            usrAttrValDao.deleteRemovedValues(oldAttrs, jdoUser.getAttrs());
            jdoUser = userDao.detachCopy(jdoUser, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUser(jdoUser, true);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(user.getOid());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(user.getOid());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(user.getOid());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public void deleteUser(String oid) throws ProvisioningException {

        long id = Long.parseLong(oid);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUser jdoUser = userDao.findById(id);
            if (jdoUser != null) {

                // Get dependent tables
                JDOUserAttributeValue[] attrs = jdoUser.getAttrs();
                JDOUserSecurityQuestion[] secQuestions = jdoUser.getSecurityQuestions();

                // Clear relationship
                //jdoUser.setSecurityQuestions(null);
                jdoUser.setAttrs(null);

                // Save and delete
                userDao.save(jdoUser);
                userDao.flush();
                userDao.delete(id);

                // Delete dependants
                if (attrs != null) {
                    for (JDOUserAttributeValue value : attrs)
                        usrAttrValDao.delete(value.getId());
                }


            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

    // -------------------------------------------< SecurityQuestion >
//    @Transactional
    public Collection<SecurityQuestion> findAllSecurityQuestions() throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOSecurityQuestion> securityQuestions = securityQuestionDAO.findAll();
            Collection<SecurityQuestion> secQuestions = toSecurityQuestion(securityQuestions);
            transactionManager.commit(status);
            return secQuestions;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

    // -------------------------------------< ACLs >

//    @Transactional
    public AclEntry findAclEntryByApprovalToken(String approvalToken) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOAclEntry jdoAclEntry = aclEntryDao.findByApprovalToken(approvalToken);
            jdoAclEntry = aclEntryDao.detachCopy(jdoAclEntry, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toAclEntry(jdoAclEntry);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new AclEntryNotFoundException(approvalToken);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public AclEntry findAclEntryById(String oid) throws ProvisioningException {

        long id = Long.parseLong(oid);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOAclEntry jdoAclEntry = aclEntryDao.findById(id);
            jdoAclEntry = aclEntryDao.detachCopy(jdoAclEntry, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toAclEntry(jdoAclEntry);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new AclEntryNotFoundException(id);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public AclEntry updateAclEntry(AclEntry aclEntry) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOAclEntry jdoAclEntry = aclEntryDao.findById(Long.parseLong(aclEntry.getId()));
            jdoAclEntry = toJDOAclEntry(jdoAclEntry, aclEntry);
            jdoAclEntry = aclEntryDao.save(jdoAclEntry);
            jdoAclEntry = aclEntryDao.detachCopy(jdoAclEntry, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toAclEntry(jdoAclEntry);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(aclEntry.getId());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(aclEntry.getId());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(aclEntry.getId());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }

    }

//    @Transactional
    public void deleteAclEntry(String oid) throws ProvisioningException {

        long id = Long.parseLong(oid);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOAclEntry jdoAclEntry = aclEntryDao.findById(id);
            if (jdoAclEntry != null) {
                aclEntryDao.delete(id);
            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }

    }

    // -------------------------------------< SecurityTokens >

//    @Transactional
    public SecurityToken addSecurityToken(SecurityToken securityToken) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOSecurityToken jdoSecurityToken = toJDOSecurityToken(securityToken);
            jdoSecurityToken = securityTokenDao.save(jdoSecurityToken);
            jdoSecurityToken = securityTokenDao.detachCopy(jdoSecurityToken, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toSecurityToken(jdoSecurityToken);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }

    }

//    @Transactional
    public SecurityToken updateSecurityToken(SecurityToken securityToken) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOSecurityToken jdoSecurityToken = securityTokenDao.findByTokenId(securityToken.getId());
            jdoSecurityToken = toJDOSecurityToken(jdoSecurityToken, securityToken);
            jdoSecurityToken = securityTokenDao.save(jdoSecurityToken);
            jdoSecurityToken = securityTokenDao.detachCopy(jdoSecurityToken, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toSecurityToken(jdoSecurityToken);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new SecurityTokenNotFoundException(securityToken.getId());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new SecurityTokenNotFoundException(securityToken.getId());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new SecurityTokenNotFoundException(securityToken.getId());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }

    }

//    @Transactional
    public void deleteSecurityToken(String tokenId) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOSecurityToken jdoSecurityToken = securityTokenDao.findByTokenId(tokenId);
            if (jdoSecurityToken != null) {
                securityTokenDao.delete(jdoSecurityToken.getId());
            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(tokenId);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(tokenId);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new AclEntryNotFoundException(tokenId);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public SecurityToken findSecurityTokenByTokenId(String tokenId) throws ProvisioningException{

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOSecurityToken jdoSecurityToken = securityTokenDao.findByTokenId(tokenId);
            jdoSecurityToken = securityTokenDao.detachCopy(jdoSecurityToken, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toSecurityToken(jdoSecurityToken);

        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new SecurityTokenNotFoundException(tokenId);

            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<SecurityToken> findSecurityTokensByIssueInstantBefore(long issueInstant ) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOSecurityToken> jdoSecurityTokens = securityTokenDao.findByIssueInstantBefore(issueInstant);
            jdoSecurityTokens = securityTokenDao.detachCopyAll(jdoSecurityTokens, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toSecurityTokens(jdoSecurityTokens);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<SecurityToken> findSecurityTokensByExpiresOnBefore(long expiresOn ) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOSecurityToken> jdoSecurityTokens = securityTokenDao.findByExpiresOnBefore(expiresOn);
            jdoSecurityTokens = securityTokenDao.detachCopyAll(jdoSecurityTokens, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toSecurityTokens(jdoSecurityTokens);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }


    // -------------------------------------------< Utils >

    protected JDOGroup toJDOGroup(Group group) {
        JDOGroup jdoGroup = toJDOGroup(new JDOGroup(), group);
        if (group.getOid() != null)
            jdoGroup.setId(Long.parseLong(group.getOid()));
        return jdoGroup;
    }

    protected JDOGroup toJDOGroup(JDOGroup jdoGroup, Group group) {
        jdoGroup.setName(group.getName());
        jdoGroup.setDescription(group.getDescription());

        if (group.getAttrs() != null) {
            JDOGroupAttributeValue[] jdoAttrs = new JDOGroupAttributeValue[group.getAttrs().length];

            for (int i = 0; i < group.getAttrs().length; i++) {
                GroupAttributeValue attr = group.getAttrs()[i];
                JDOGroupAttributeValue jdoAttr = null;
                if (attr.getId() != null) {
                    jdoAttr = grpAttrValDao.findById(Long.parseLong(attr.getId()));
                }
                if (jdoAttr == null) {
                    jdoAttr = new JDOGroupAttributeValue();
                    jdoAttr.setName(attr.getName());
                }
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

        group.setOid(jdoGroup.getId() + "");
        group.setName(jdoGroup.getName());
        group.setDescription(jdoGroup.getDescription());

        if (jdoGroup.getAttrs() != null) {
            List<GroupAttributeValue> attrs = new ArrayList<GroupAttributeValue>();
            
            for (int i = 0; i < jdoGroup.getAttrs().length; i++) {
                JDOGroupAttributeValue jdoAttr = jdoGroup.getAttrs()[i];
                if (jdoAttr.getId() > 0) {
                    GroupAttributeValue groupAttribute = new GroupAttributeValue();
                    groupAttribute.setId(jdoAttr.getId() + "");
                    groupAttribute.setName(jdoAttr.getName());
                    groupAttribute.setValue(jdoAttr.getValue());

                    attrs.add(groupAttribute);
                }
            }

            if (attrs.size() > 0) {
                group.setAttrs(attrs.toArray(new GroupAttributeValue[]{}));
            }
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

    protected SecurityQuestion toSecurityQuestion(JDOSecurityQuestion jdoSecurityQuestion) {
        if (jdoSecurityQuestion == null)
            return null;

        SecurityQuestion group = new SecurityQuestion();

        group.setId(jdoSecurityQuestion.getId() + "");
        group.setMessageKey(jdoSecurityQuestion.getMessageKey());


        return group;
    }


    protected Collection<SecurityQuestion> toSecurityQuestion(Collection<JDOSecurityQuestion> jdoSecurityQuestions) {
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>(jdoSecurityQuestions.size());
        for (JDOSecurityQuestion jdoSecurityQuestion : jdoSecurityQuestions) {
            securityQuestions.add(toSecurityQuestion(jdoSecurityQuestion));
        }

        return securityQuestions;

    }


    protected JDOUser toJDOUser(User user, boolean keepUserPassword) {
        JDOUser jdoUser = new JDOUser();
        if (user.getOid() != null)
            jdoUser.setId(Long.parseLong(user.getOid()));
        return toJDOUser(jdoUser, user, keepUserPassword);
    }

    protected JDOUser toJDOUser(JDOUser jdoUser, User user, boolean keepUserPassword) {
        String pwd = jdoUser.getUserPassword();

        BeanUtils.copyProperties(user, jdoUser, new String[] {"id", "groups", "acls", "attrs", "securityQuestions"});

        if (keepUserPassword)
            jdoUser.setUserPassword(pwd);

        if (user.getGroups() != null) {
            JDOGroup[] jdoGroups = new JDOGroup[user.getGroups().length];
            for (int i = 0; i < user.getGroups().length; i++) {
                Group group = user.getGroups()[i];
                JDOGroup jdoGroup = groupDao.findById(Long.parseLong(group.getOid()));
                jdoGroups[i] = jdoGroup;
            }
            jdoUser.setGroups(jdoGroups);
        }

        if (user.getAcls() != null) {
            JDOAcl[] jdoAcls = new JDOAcl[user.getAcls().length];
            for (int i = 0; i < user.getAcls().length; i++) {
                Acl acl = user.getAcls()[i];
                JDOAcl jdoAcl;
                if (acl.getId() != null) {
                    jdoAcl = aclDao.findById(Long.parseLong(acl.getId()));
                } else {
                    jdoAcl = new JDOAcl();
                    jdoAcl.setName(acl.getName());
                    jdoAcl.setDescription(acl.getDescription());
                }

                jdoAcls[i] = jdoAcl;
                JDOAclEntry[] jdoAclEntries = new JDOAclEntry[acl.getEntries().length];
                for (int j =0; j < acl.getEntries().length; j++) {
                    AclEntry aclEntry = acl.getEntries()[j];

                    JDOAclEntry jdoAclEntry;
                    if (aclEntry.getId() != null) {
                        jdoAclEntry = aclEntryDao.findById(Long.parseLong(aclEntry.getId()));
                    } else {
                        jdoAclEntry = new JDOAclEntry();
                        jdoAclEntry.setPrincipalNameClaim(aclEntry.getPrincipalNameClaim());
                        jdoAclEntry.setPasswordClaim(aclEntry.getPasswordClaim());
                        jdoAclEntry.setFrom(aclEntry.getFrom());
                        jdoAclEntry.setDecision(JDOAclDecisionType.fromValue(aclEntry.getDecision().toString()));
                        jdoAclEntry.setState(JDOAclEntryStateType.fromValue(aclEntry.getState().toString()));
                        jdoAclEntry.setApprovalToken(aclEntry.getApprovalToken());
                        jdoAclEntry.setSpAlias(aclEntry.getSpAlias());
                    }

                    jdoAclEntries[j] = jdoAclEntry;
                }
                jdoAcl.setAclEntries(jdoAclEntries);
            }
            jdoUser.setAcls(jdoAcls);
        }

        if (user.getAttrs() != null) {
            JDOUserAttributeValue[] jdoAttrs = new JDOUserAttributeValue[user.getAttrs().length];

            for (int i = 0; i < user.getAttrs().length; i++) {
                UserAttributeValue attr = user.getAttrs()[i];
                JDOUserAttributeValue jdoAttr = null;
                if (attr.getId() != null) {
                    jdoAttr = usrAttrValDao.findById(Long.parseLong(attr.getId()));
                }
                if (jdoAttr == null) {
                    jdoAttr = new JDOUserAttributeValue();
                    jdoAttr.setName(attr.getName());
                }
                jdoAttr.setValue(attr.getValue());

                jdoAttrs[i] = jdoAttr;
            }

            jdoUser.setAttrs(jdoAttrs);
        }

        if (user.getSecurityQuestions() != null) {
            JDOUserSecurityQuestion[] jdoSecurityQuestions = new JDOUserSecurityQuestion[user.getSecurityQuestions().length];

            for (int i = 0 ; i < user.getSecurityQuestions().length ; i++ ) {
                UserSecurityQuestion userSecurityQuestion = user.getSecurityQuestions()[i];
                JDOUserSecurityQuestion jdoUserSecurityQuestion = null;

                if (userSecurityQuestion.getId() != null) {
                    jdoUserSecurityQuestion = usrSecQuestionDao.findById(Long.parseLong(userSecurityQuestion.getId()));
                }

                if (jdoUserSecurityQuestion == null) {
                    jdoUserSecurityQuestion = new JDOUserSecurityQuestion();
                }

                jdoUserSecurityQuestion.setAnswer(userSecurityQuestion.getAnswer());
                jdoUserSecurityQuestion.setCustomMessage(userSecurityQuestion.getCustomMessage());
                jdoUserSecurityQuestion.setEncryption(userSecurityQuestion.getEncryption());
                jdoUserSecurityQuestion.setHashing(userSecurityQuestion.getHashing());

                if (userSecurityQuestion.getQuestion() != null) {
                    jdoUserSecurityQuestion.setQuestion(securityQuestionDAO.findById(Long.parseLong(userSecurityQuestion.getQuestion().getId())));
                }

                jdoSecurityQuestions[i] = jdoUserSecurityQuestion;

            }

            jdoUser.setSecurityQuestions(jdoSecurityQuestions);
        }

        return jdoUser;
    }

    protected User toUser(JDOUser jdoUser, boolean retrieveUserPassword) {
        User user = new User();
        BeanUtils.copyProperties(jdoUser, user, new String[] {"groups", "acls", "securityQuestions", "attrs"});

        user.setOid(jdoUser.getId() + "");

        if (!retrieveUserPassword)
            user.setUserPassword(null);

        if (jdoUser.getGroups() != null) {
            Group[] groups = new Group[jdoUser.getGroups().length];

            for (int i = 0; i < jdoUser.getGroups().length; i++) {
                JDOGroup jdoGroup = jdoUser.getGroups()[i];
                Group group = new Group();
                group.setName(jdoGroup.getName());
                group.setDescription(jdoGroup.getDescription());
                group.setOid(jdoGroup.getId() + "");

                groups[i] = group;
            }

            user.setGroups(groups);
        }

        if (jdoUser.getAcls() != null) {
            Acl[] acls = new Acl[jdoUser.getAcls().length];

            for (int i = 0; i < jdoUser.getAcls().length; i++) {
                JDOAcl jdoAcl = jdoUser.getAcls()[i];
                Acl acl = new Acl();
                acl.setName(jdoAcl.getName());
                acl.setDescription(jdoAcl.getDescription());
                acl.setId(jdoAcl.getId() + "");

                if (jdoAcl.getEntries() != null) {
                    AclEntry[] aclEntries = new AclEntry[jdoAcl.getEntries().length];
                    for (int j = 0; j < jdoAcl.getEntries().length; j++) {
                        JDOAclEntry jdoAclEntry = jdoAcl.getEntries()[j];
                        AclEntry aclEntry = toAclEntry(jdoAclEntry);
                        aclEntries[j] = aclEntry;
                    }
                    acl.setAclEntries(aclEntries);
                }
                acls[i] = acl;
            }

            user.setAcls(acls);
        }

        if (jdoUser.getAttrs() != null) {
            List<UserAttributeValue> attrs = new ArrayList<UserAttributeValue>();
            
            for (int i = 0; i < jdoUser.getAttrs().length; i++) {
                JDOUserAttributeValue jdoAttr = jdoUser.getAttrs()[i];
                if (jdoAttr.getId() > 0) {
                    UserAttributeValue userAttribute = new UserAttributeValue();
                    userAttribute.setId(jdoAttr.getId() + "");
                    userAttribute.setName(jdoAttr.getName());
                    userAttribute.setValue(jdoAttr.getValue());

                    attrs.add(userAttribute);
                }
            }

            if (attrs.size() > 0) {
                user.setAttrs(attrs.toArray(new UserAttributeValue[attrs.size()]));
            }
        }

        if (jdoUser.getSecurityQuestions() != null) {
            List<UserSecurityQuestion> securityQuestions = new ArrayList<UserSecurityQuestion>(jdoUser.getSecurityQuestions().length);

            for (int i = 0 ; i < jdoUser.getSecurityQuestions().length ; i++) {
                JDOUserSecurityQuestion jdoUserSecurityQuestion = jdoUser.getSecurityQuestions()[i];
                if (jdoUserSecurityQuestion.getId() > 0) {

                    UserSecurityQuestion userSecurityQuestion = new UserSecurityQuestion();
                    userSecurityQuestion.setId(jdoUserSecurityQuestion.getId() + "");
                    userSecurityQuestion.setAnswer(jdoUserSecurityQuestion.getAnswer());
                    userSecurityQuestion.setCustomMessage(jdoUserSecurityQuestion.getCustomMessage());

                    if (jdoUserSecurityQuestion.getQuestion() != null)
                        userSecurityQuestion.setQuestion(toSecurityQuestion(jdoUserSecurityQuestion.getQuestion()));

                    userSecurityQuestion.setHashing(jdoUserSecurityQuestion.getHashing());
                    userSecurityQuestion.setEncryption(jdoUserSecurityQuestion.getEncryption());
                    securityQuestions.add(userSecurityQuestion);
                }
            }


            if (securityQuestions.size() > 0) {
                user.setSecurityQuestions(securityQuestions.toArray(new UserSecurityQuestion[securityQuestions.size()]));
            }
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

    protected JDOAclEntry toJDOAclEntry(JDOAclEntry jdoAclEntry, AclEntry aclEntry) {
        jdoAclEntry.setPrincipalNameClaim(aclEntry.getPrincipalNameClaim());
        jdoAclEntry.setPasswordClaim(aclEntry.getPasswordClaim());
        jdoAclEntry.setFrom(aclEntry.getFrom());
        jdoAclEntry.setDecision(JDOAclDecisionType.fromValue(aclEntry.getDecision().toString()));
        jdoAclEntry.setState(JDOAclEntryStateType.fromValue(aclEntry.getState().toString()));
        jdoAclEntry.setApprovalToken(aclEntry.getApprovalToken());
        jdoAclEntry.setSpAlias(aclEntry.getSpAlias());

        return jdoAclEntry;
    }

    protected AclEntry toAclEntry(JDOAclEntry jdoAclEntry) {
        AclEntry aclEntry = new AclEntry();
        aclEntry.setPrincipalNameClaim(jdoAclEntry.getPrincipalNameClaim());
        aclEntry.setPasswordClaim(jdoAclEntry.getPasswordClaim());
        aclEntry.setFrom(jdoAclEntry.getFrom());
        aclEntry.setDecision(AclDecisionType.fromValue(jdoAclEntry.getDecision().toString()));
        aclEntry.setApprovalToken(jdoAclEntry.getApprovalToken());
        aclEntry.setState(AclEntryStateType.fromValue(jdoAclEntry.getState().toString()));
        aclEntry.setSpAlias(jdoAclEntry.getSpAlias());
        aclEntry.setId(jdoAclEntry.getId() + "");
        return aclEntry;

    }

    protected JDOSecurityToken toJDOSecurityToken(SecurityToken securityToken) throws IOException {
        JDOSecurityToken jdoUser = new JDOSecurityToken();
        //jdoUser.setId(securityToken.getId());
        return toJDOSecurityToken(jdoUser, securityToken);
    }


    protected JDOSecurityToken toJDOSecurityToken(JDOSecurityToken jdoSecurityToken, SecurityToken securityToken) throws IOException {

        jdoSecurityToken.setTokenId(securityToken.getId());
        jdoSecurityToken.setNameIdentifier(securityToken.getNameIdentifier());
        jdoSecurityToken.setContent(securityToken.getContent());
        jdoSecurityToken.setSerializedContent(securityToken.getSerializedContent());
        jdoSecurityToken.setIssueInstant(securityToken.getIssueInstant());
        jdoSecurityToken.setExpiresOn(securityToken.getExpiresOn());

        jdoSecurityToken = marshall(jdoSecurityToken);

        return jdoSecurityToken;
    }

    protected Collection<SecurityToken> toSecurityTokens(Collection<JDOSecurityToken> jdoSecurityTokens) throws IOException, ClassNotFoundException {
        List<SecurityToken> securityTokens = new ArrayList<SecurityToken>(jdoSecurityTokens.size());
        for (JDOSecurityToken jdoSecurityToken : jdoSecurityTokens) {
            securityTokens.add(toSecurityToken(jdoSecurityToken));
        }

        return securityTokens;

    }


    protected SecurityToken toSecurityToken(JDOSecurityToken jdoSecurityToken) throws IOException, ClassNotFoundException {

        jdoSecurityToken = unmarshal(jdoSecurityToken);

        SecurityTokenImpl st = new SecurityTokenImpl(jdoSecurityToken.getTokenId(),
                jdoSecurityToken.getNameIdentifier(),
                jdoSecurityToken.getContent(),
                jdoSecurityToken.getSerializedContent(),
                jdoSecurityToken.getIssueInstant());

        st.setExpiresOn(jdoSecurityToken.getExpiresOn());

        return st;
    }

    public JDOSecurityToken unmarshal(JDOSecurityToken a) throws IOException, ClassNotFoundException {

        // TODO : Let specific capabilities (saml, oauth2, etc) contribute soken serializers to avoid classloader issues

        String contentStr = a.getContentBin();

        if (contentStr != null) {
            byte[] contentBytes = Base64.decodeBase64(contentStr.getBytes());
            ByteArrayInputStream contentBais = new ByteArrayInputStream(contentBytes);
            ObjectInputStream contentOis;
            contentOis = new ObjectInputStream(contentBais);

            Object definition = (Object) contentOis.readObject();
            a.setContent(definition);
        }

        return a;
    }

    public JDOSecurityToken marshall(JDOSecurityToken a) throws IOException {

        // TODO : Let specific capabilities (saml, oauth2, etc) contribute soken serializers to avoid classloader issues

        Object content = a.getContent();
        if (content != null) {

            ByteArrayOutputStream contentBaos = new ByteArrayOutputStream();
            ObjectOutputStream contentOs = new ObjectOutputStream(contentBaos);
            contentOs.writeObject(content);
            byte[] contentBytes = contentBaos.toByteArray();
            byte[] contentEnc = Base64.encodeBase64(contentBytes);
            String contentStr = new String(contentEnc);

            a.setContentBin(contentStr);
            a.setContent(null);
        }


        return a;
    }    


}


