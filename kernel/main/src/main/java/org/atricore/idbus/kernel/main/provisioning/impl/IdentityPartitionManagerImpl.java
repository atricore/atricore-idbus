package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityVault;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.GroupDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.UserDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.GroupDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.UserDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.datanucleus.jdo.JDOPersistenceManagerFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityPartitionManagerImpl {
    
    private IdentityVault identityVault;
    
    private IdentityPartition identityPartition;
    
    private UserDAO userDao;
    
    private GroupDAO groupDao;
    
    private PersistenceManagerFactory pmf;
    
    private PersistenceManager pm;

    public IdentityPartitionManagerImpl(IdentityVault identityVault, IdentityPartition identityPartition) {
        this.identityVault = identityVault;
        this.identityPartition = identityPartition;
    }
    
    public void init() {
        // Create PMF Instance ....
        JDOPersistenceManagerFactory jdoPmf = new JDOPersistenceManagerFactory();
        
        // TODO : Configure connection properties ...
        
        pmf = jdoPmf;

        pm = jdoPmf.getPersistenceManager();
        
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
        
        if (pmf != null)
            try { pmf.close(); } catch (Exception e) { /**/ }
        
    }
    
    // GROUP Operations
    
    public Group addGroup(Group group) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            Group g = groupDao.createObject(group);
            tx.commit();
            return g;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public Group findGroupById(long id) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            Group g = groupDao.findObjectById(id);
            tx.commit();
            return g;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public Group updateGroup(Group group) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            group = groupDao.updateObject(group);
            tx.commit();
            return group;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public void removeGroup(Group group) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            groupDao.deleteObject(group);
            tx.commit();
            
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    // USER CRUD Operations    
    
    public User addUser(User user) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            User g = userDao.createObject(user);
            pm.detachCopy(g);
            tx.commit();
            return g;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public User findUserById(long id) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            User g = userDao.findObjectById(id);
            tx.commit();
            return g;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public User updateUser(User user) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            User g = userDao.updateObject(user);
            tx.commit();
            return g;
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    public void removeUser(User user) throws ProvisioningException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            userDao.deleteObject(user);
            tx.commit();
            
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
        }
    }
    
    
}
