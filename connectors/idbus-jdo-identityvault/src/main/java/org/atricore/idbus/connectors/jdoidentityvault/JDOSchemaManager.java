package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAttributeType;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupAttributeDefinitionDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupAttributeValueDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserAttributeDefinitionDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserAttributeValueDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupAttributeNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserAttributeNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractSchemaManager;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.jdo.JdoObjectRetrievalFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.jdo.FetchPlan;
import javax.jdo.JDOObjectNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOSchemaManager extends AbstractSchemaManager {

    private JDOUserAttributeDefinitionDAO usrAttrDefDao;

    private JDOGroupAttributeDefinitionDAO grpAttrDefDao;

    private JDOUserAttributeValueDAO usrAttrValDao;

    private JDOGroupAttributeValueDAO grpAttrValDao;

    // Spring transaction management
    private PlatformTransactionManager transactionManager;

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

//    @Transactional
    public UserAttributeDefinition addUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUserAttributeDefinition jdoUserAttribute = toJDOUserAttribute(attrDef);
            jdoUserAttribute = usrAttrDefDao.save(jdoUserAttribute);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUserAttribute(jdoUserAttribute);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public UserAttributeDefinition updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findById(Long.parseLong(attrDef.getId()));
            if (!jdoUserAttribute.getName().equals(attrDef.getName())) {
                usrAttrValDao.updateName(jdoUserAttribute.getName(), attrDef.getName());
            }
            jdoUserAttribute = toJDOUserAttribute(jdoUserAttribute, attrDef);
            jdoUserAttribute = usrAttrDefDao.save(jdoUserAttribute);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUserAttribute(jdoUserAttribute);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public void deleteUserAttribute(String attributeId) throws ProvisioningException {

        long id = Long.parseLong(attributeId);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findById(id);
            if (jdoUserAttribute != null) {
                usrAttrValDao.deleteValues(jdoUserAttribute.getName());
                usrAttrDefDao.delete(id);
            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public UserAttributeDefinition findUserAttributeById(String attributeId) throws ProvisioningException {

        long id = Long.parseLong(attributeId);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findById(id);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUserAttribute(jdoUserAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new UserAttributeNotFoundException(id);
            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new UserAttributeNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public UserAttributeDefinition findUserAttributeByName(String name) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findByName(name);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUserAttribute(jdoUserAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new UserAttributeNotFoundException(name);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<UserAttributeDefinition> listUserAttributes() throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOUserAttributeDefinition> jdoUserAttributes = usrAttrDefDao.findAll();
            jdoUserAttributes = usrAttrDefDao.detachCopyAll(jdoUserAttributes, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toUserAttributes(jdoUserAttributes);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public GroupAttributeDefinition addGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = toJDOGroupAttribute(attrDef);
            jdoGroupAttribute = grpAttrDefDao.save(jdoGroupAttribute);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public GroupAttributeDefinition updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findById(Long.parseLong(attrDef.getId()));
            if (!jdoGroupAttribute.getName().equals(attrDef.getName())) {
                grpAttrValDao.updateName(jdoGroupAttribute.getName(), attrDef.getName());
            }
            jdoGroupAttribute = toJDOGroupAttribute(jdoGroupAttribute, attrDef);
            jdoGroupAttribute = grpAttrDefDao.save(jdoGroupAttribute);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public void deleteGroupAttribute(String attributeId) throws ProvisioningException {

        long id = Long.parseLong(attributeId);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findById(id);
            if (jdoGroupAttribute != null) {
                grpAttrValDao.deleteValues(jdoGroupAttribute.getName());
                grpAttrDefDao.delete(id);
            }
            transactionManager.commit(status);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public GroupAttributeDefinition findGroupAttributeById(String attributeId) throws ProvisioningException {

        long id = Long.parseLong(attributeId);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findById(id);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new GroupAttributeNotFoundException(id);
            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            transactionManager.rollback(status);
            throw new GroupAttributeNotFoundException(id);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public GroupAttributeDefinition findGroupAttributeByName(String name) throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findByName(name);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            transactionManager.rollback(status);
            if (e.getActualSize() == 0)
                throw new GroupAttributeNotFoundException(name);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

//    @Transactional
    public Collection<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException {

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDef );

        try {
            Collection<JDOGroupAttributeDefinition> jdoGroupAttributes = grpAttrDefDao.findAll();
            jdoGroupAttributes = grpAttrDefDao.detachCopyAll(jdoGroupAttributes, FetchPlan.FETCH_SIZE_GREEDY);
            transactionManager.commit(status);
            return toGroupAttributes(jdoGroupAttributes);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new ProvisioningException(e);
        }
    }

    // -------------------------------------------< Utils >

    // User Attribute Utils
    
    protected JDOUserAttributeDefinition toJDOUserAttribute(UserAttributeDefinition userAttribute) {
        JDOUserAttributeDefinition jdoUserAttribute = toJDOUserAttribute(new JDOUserAttributeDefinition(), userAttribute);
        if (userAttribute.getId() != null)
            jdoUserAttribute.setId(Long.parseLong(userAttribute.getId()));
        return jdoUserAttribute;
    }

    protected JDOUserAttributeDefinition toJDOUserAttribute(JDOUserAttributeDefinition jdoUserAttribute,
                                                    UserAttributeDefinition userAttribute) {

        jdoUserAttribute.setType(JDOAttributeType.fromValue(userAttribute.getType().toString()));
        jdoUserAttribute.setName(userAttribute.getName());
        jdoUserAttribute.setDescription(userAttribute.getDescription());
        jdoUserAttribute.setMultivalued(userAttribute.isMultivalued());
        jdoUserAttribute.setRequired(userAttribute.isRequired());

        return jdoUserAttribute;
    }

    protected UserAttributeDefinition toUserAttribute(JDOUserAttributeDefinition jdoUserAttribute) {
        UserAttributeDefinition userAttribute = new UserAttributeDefinition();

        userAttribute.setId(jdoUserAttribute.getId() + "");
        userAttribute.setType(AttributeType.fromValue(jdoUserAttribute.getType().toString()));
        userAttribute.setName(jdoUserAttribute.getName());
        userAttribute.setDescription(jdoUserAttribute.getDescription());
        userAttribute.setMultivalued(jdoUserAttribute.getMultivalued());
        userAttribute.setRequired(jdoUserAttribute.getRequired());

        return userAttribute;
    }

    protected Collection<UserAttributeDefinition> toUserAttributes(Collection<JDOUserAttributeDefinition> jdoUserAttributes) {
        List<UserAttributeDefinition> userAttributes = new ArrayList<UserAttributeDefinition>(jdoUserAttributes.size());
        for (JDOUserAttributeDefinition jdoUserAttribute : jdoUserAttributes) {
            userAttributes.add(toUserAttribute(jdoUserAttribute));
        }
        return userAttributes;
    }

    // Group Attribute Utils

    protected JDOGroupAttributeDefinition toJDOGroupAttribute(GroupAttributeDefinition groupAttribute) {
        JDOGroupAttributeDefinition jdoGroupAttribute = toJDOGroupAttribute(new JDOGroupAttributeDefinition(), groupAttribute);
        if (groupAttribute.getId() != null)
            jdoGroupAttribute.setId(Long.parseLong(groupAttribute.getId()));
        return jdoGroupAttribute;
    }

    protected JDOGroupAttributeDefinition toJDOGroupAttribute(JDOGroupAttributeDefinition jdoGroupAttribute,
                                                    GroupAttributeDefinition groupAttribute) {

        jdoGroupAttribute.setType(JDOAttributeType.fromValue(groupAttribute.getType().toString()));

        jdoGroupAttribute.setName(groupAttribute.getName());
        jdoGroupAttribute.setDescription(groupAttribute.getDescription());
        jdoGroupAttribute.setMultivalued(groupAttribute.isMultivalued());
        jdoGroupAttribute.setRequired(groupAttribute.isRequired());

        return jdoGroupAttribute;
    }

    protected GroupAttributeDefinition toGroupAttribute(JDOGroupAttributeDefinition jdoGroupAttribute) {
        GroupAttributeDefinition groupAttribute = new GroupAttributeDefinition();

        groupAttribute.setId(jdoGroupAttribute.getId() + "");
        groupAttribute.setType(AttributeType.fromValue(jdoGroupAttribute.getType().toString()));
        groupAttribute.setName(jdoGroupAttribute.getName());
        groupAttribute.setDescription(jdoGroupAttribute.getDescription());
        groupAttribute.setMultivalued(jdoGroupAttribute.getMultivalued());
        groupAttribute.setRequired(jdoGroupAttribute.getRequired());

        return groupAttribute;
    }

    protected Collection<GroupAttributeDefinition> toGroupAttributes(Collection<JDOGroupAttributeDefinition> jdoGroupAttributes) {
        List<GroupAttributeDefinition> groupAttributes = new ArrayList<GroupAttributeDefinition>(jdoGroupAttributes.size());
        for (JDOGroupAttributeDefinition jdoGroupAttribute : jdoGroupAttributes) {
            groupAttributes.add(toGroupAttribute(jdoGroupAttribute));
        }
        return groupAttributes;
    }

    public void setGrpAttrDefDao(JDOGroupAttributeDefinitionDAO grpAttrDefDao) {
        this.grpAttrDefDao = grpAttrDefDao;
    }

    public void setUsrAttrDefDao(JDOUserAttributeDefinitionDAO usrAttrDefDao) {
        this.usrAttrDefDao = usrAttrDefDao;
    }

    public void setUsrAttrValDao(JDOUserAttributeValueDAO usrAttrValDao) {
        this.usrAttrValDao = usrAttrValDao;
    }

    public void setGrpAttrValDao(JDOGroupAttributeValueDAO grpAttrValDao) {
        this.grpAttrValDao = grpAttrValDao;
    }
}
