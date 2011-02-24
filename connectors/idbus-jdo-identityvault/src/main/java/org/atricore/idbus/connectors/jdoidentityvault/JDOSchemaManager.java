package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAttributeType;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeDefinition;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupAttributeDefinitionDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserAttributeDefinitionDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.AttributeType;
import org.atricore.idbus.kernel.main.provisioning.domain.GroupAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeDefinition;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupAttributeNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserAttributeNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.impl.AbstractSchemaManager;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.springframework.beans.BeanUtils;
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
public class JDOSchemaManager extends AbstractSchemaManager {

    private JDOUserAttributeDefinitionDAO usrAttrDefDao;

    private JDOGroupAttributeDefinitionDAO grpAttrDefDao;

    @Transactional
    public UserAttributeDefinition addUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        try {
            JDOUserAttributeDefinition jdoUserAttribute = toJDOUserAttribute(attrDef);
            jdoUserAttribute = usrAttrDefDao.save(jdoUserAttribute);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toUserAttribute(jdoUserAttribute);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public UserAttributeDefinition updateUserAttribute(UserAttributeDefinition attrDef) throws ProvisioningException {
        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findById(attrDef.getId());
            jdoUserAttribute = toJDOUserAttribute(jdoUserAttribute, attrDef);
            jdoUserAttribute = usrAttrDefDao.save(jdoUserAttribute);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, 99);
            return toUserAttribute(jdoUserAttribute);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (JDOObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (NucleusObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(attrDef.getId());
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public void deleteUserAttribute(long id) throws ProvisioningException {
        try {
            usrAttrDefDao.delete(id);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public UserAttributeDefinition findUserAttributeById(long id) throws ProvisioningException {
        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findById(id);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toUserAttribute(jdoUserAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new UserAttributeNotFoundException(id);
            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new UserAttributeNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public UserAttributeDefinition findUserAttributeByName(String name) throws ProvisioningException {
        try {
            JDOUserAttributeDefinition jdoUserAttribute = usrAttrDefDao.findByName(name);
            jdoUserAttribute = usrAttrDefDao.detachCopy(jdoUserAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toUserAttribute(jdoUserAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new UserAttributeNotFoundException(name);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<UserAttributeDefinition> listUserAttributes() throws ProvisioningException {
        try {
            Collection<JDOUserAttributeDefinition> jdoUserAttributes = usrAttrDefDao.findAll();
            jdoUserAttributes = usrAttrDefDao.detachCopyAll(jdoUserAttributes, FetchPlan.FETCH_SIZE_GREEDY);
            return toUserAttributes(jdoUserAttributes);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public GroupAttributeDefinition addGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = toJDOGroupAttribute(attrDef);
            jdoGroupAttribute = grpAttrDefDao.save(jdoGroupAttribute);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public GroupAttributeDefinition updateGroupAttribute(GroupAttributeDefinition attrDef) throws ProvisioningException {
        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findById(attrDef.getId());
            jdoGroupAttribute = toJDOGroupAttribute(jdoGroupAttribute, attrDef);
            jdoGroupAttribute = grpAttrDefDao.save(jdoGroupAttribute);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, 99);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (JDOObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(attrDef.getId());
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public void deleteGroupAttribute(long id) throws ProvisioningException {
        try {
            grpAttrDefDao.delete(id);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public GroupAttributeDefinition findGroupAttributeById(long id) throws ProvisioningException {
        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findById(id);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new GroupAttributeNotFoundException(id);
            throw new ProvisioningException(e);
        } catch (JdoObjectRetrievalFailureException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (JDOObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (NucleusObjectNotFoundException e) {
            throw new GroupAttributeNotFoundException(id);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public GroupAttributeDefinition findGroupAttributeByName(String name) throws ProvisioningException {
        try {
            JDOGroupAttributeDefinition jdoGroupAttribute = grpAttrDefDao.findByName(name);
            jdoGroupAttribute = grpAttrDefDao.detachCopy(jdoGroupAttribute, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroupAttribute(jdoGroupAttribute);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0)
                throw new GroupAttributeNotFoundException(name);
            throw new ProvisioningException(e);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    @Transactional
    public Collection<GroupAttributeDefinition> listGroupAttributes() throws ProvisioningException {
        try {
            Collection<JDOGroupAttributeDefinition> jdoGroupAttributes = grpAttrDefDao.findAll();
            jdoGroupAttributes = grpAttrDefDao.detachCopyAll(jdoGroupAttributes, FetchPlan.FETCH_SIZE_GREEDY);
            return toGroupAttributes(jdoGroupAttributes);
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }
    }

    // -------------------------------------------< Utils >

    // User Attribute Utils
    
    protected JDOUserAttributeDefinition toJDOUserAttribute(UserAttributeDefinition userAttribute) {
        JDOUserAttributeDefinition jdoUserAttribute = toJDOUserAttribute(new JDOUserAttributeDefinition(), userAttribute);
        jdoUserAttribute.setId(userAttribute.getId());
        return jdoUserAttribute;
    }

    protected JDOUserAttributeDefinition toJDOUserAttribute(JDOUserAttributeDefinition jdoUserAttribute,
                                                    UserAttributeDefinition userAttribute) {

        BeanUtils.copyProperties(userAttribute, jdoUserAttribute, new String[] {"id", "type"});
        jdoUserAttribute.setType(JDOAttributeType.fromValue(userAttribute.getType().toString()));
        return jdoUserAttribute;
    }

    protected UserAttributeDefinition toUserAttribute(JDOUserAttributeDefinition jdoUserAttribute) {
        UserAttributeDefinition userAttribute = new UserAttributeDefinition();
        BeanUtils.copyProperties(jdoUserAttribute, userAttribute, new String[] {"type"});
        userAttribute.setType(AttributeType.fromValue(jdoUserAttribute.getType().toString()));
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
        jdoGroupAttribute.setId(groupAttribute.getId());
        return jdoGroupAttribute;
    }

    protected JDOGroupAttributeDefinition toJDOGroupAttribute(JDOGroupAttributeDefinition jdoGroupAttribute,
                                                    GroupAttributeDefinition groupAttribute) {
        BeanUtils.copyProperties(groupAttribute, jdoGroupAttribute, new String[] {"id", "type"});
        jdoGroupAttribute.setType(JDOAttributeType.fromValue(groupAttribute.getType().toString()));
        return jdoGroupAttribute;
    }

    protected GroupAttributeDefinition toGroupAttribute(JDOGroupAttributeDefinition jdoGroupAttribute) {
        GroupAttributeDefinition groupAttribute = new GroupAttributeDefinition();
        BeanUtils.copyProperties(jdoGroupAttribute, groupAttribute, new String[] {"type"});
        groupAttribute.setType(AttributeType.fromValue(jdoGroupAttribute.getType().toString()));
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
}
