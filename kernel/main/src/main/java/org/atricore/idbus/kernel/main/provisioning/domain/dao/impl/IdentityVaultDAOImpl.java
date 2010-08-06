package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityVault;
import org.atricore.idbus.kernel.main.provisioning.exception.IdentityVaultNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityVaultDAOImpl {
    
    private static final Log logger = LogFactory.getLog(IdentityVaultDAOImpl.class);

    private PersistenceManagerFactory pmf;
    private PersistenceManager pm;

    public void init() {
        pm = pmf.getPersistenceManager();
    }

    public void destroy() {
        if (pm != null) {
            try {
                pm.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    // IdentityVault CRUD Operations

    public IdentityVault create(String name, String description) throws ProvisioningException {

        try {
            pm.getFetchPlan().addGroup("identityvault_f_identityvault");

            IdentityVault newIdentityVault = new IdentityVault();
            newIdentityVault.setName(name);
            newIdentityVault.setDescription(description);

            if (logger.isTraceEnabled())
                logger.trace("Create IdentityVault : '" + name + "'");

            return pm.makePersistent(newIdentityVault);

        } catch (Exception e){
            throw new ProvisioningException("Error persisting IdentityVault '"+name+"' ",e);
        }
    }

    public IdentityVault retrieve(long id) throws IdentityVaultNotFoundException {

        pm.getFetchPlan().addGroup("identityvault_f_identityvault");
        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Retrieve IdentityVault. Query '" + qryStr + "'");

        Query q = pm.newQuery(IdentityVault.class, qryStr);
        Collection result = (Collection)q.execute();

        if (result.isEmpty())
            throw new IdentityVaultNotFoundException(id);

        Iterator iter = result.iterator();
        IdentityVault g = pm.detachCopy((IdentityVault)iter.next());

        if (logger.isTraceEnabled())
            logger.trace("Retrieved IdentityVault " + g.getId() + " '" + g.getName() + "'");

        return g;

    }

    public IdentityVault update(IdentityVault identityvault) throws IdentityVaultNotFoundException {
        pm.getFetchPlan().addGroup("identityvault_f_identityvault");

        String qryStr = "id == "+ identityvault.getId();

        if (logger.isTraceEnabled())
            logger.trace("Update IdentityVault. Query '" + qryStr + "'");

        Query q = pm.newQuery(IdentityVault.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new IdentityVaultNotFoundException(identityvault.getId(), identityvault.getName());

        IdentityVault oldIdentityVault = (IdentityVault)it.next();
        oldIdentityVault.setName(identityvault.getName());
        oldIdentityVault.setDescription(identityvault.getDescription());

        pm.detachCopy(identityvault);

        return oldIdentityVault;


    }

    public void delete(long id) throws IdentityVaultNotFoundException {

        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Delete IdentityVault " + id);

        Query q = pm.newQuery(IdentityVault.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new IdentityVaultNotFoundException(id);

        pm.deletePersistent(it.next());


    }

    // More retrieve operations

    public Collection<IdentityVault> retrieveAll() {
        return null;
    }
    
}
