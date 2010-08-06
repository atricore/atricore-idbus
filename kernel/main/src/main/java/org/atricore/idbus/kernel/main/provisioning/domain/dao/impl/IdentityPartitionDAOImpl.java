package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.exception.IdentityPartitionNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityPartitionDAOImpl {
    
    private static final Log logger = LogFactory.getLog(IdentityPartitionDAOImpl.class);

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

    // IdentityPartition CRUD Operations

    public IdentityPartition create(String name, String description) throws ProvisioningException {

        try {
            pm.getFetchPlan().addGroup("identitypartition_f_identitypartition");

            IdentityPartition newIdentityPartition = new IdentityPartition();
            newIdentityPartition.setName(name);
            newIdentityPartition.setDescription(description);

            if (logger.isTraceEnabled())
                logger.trace("Create IdentityPartition : '" + name + "'");

            return pm.makePersistent(newIdentityPartition);

        } catch (Exception e){
            throw new ProvisioningException("Error persisting IdentityPartition '"+name+"' ",e);
        }
    }

    public IdentityPartition retrieve(long id) throws IdentityPartitionNotFoundException {

        pm.getFetchPlan().addGroup("identitypartition_f_identitypartition");
        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Retrieve IdentityPartition. Query '" + qryStr + "'");

        Query q = pm.newQuery(IdentityPartition.class, qryStr);
        Collection result = (Collection)q.execute();

        if (result.isEmpty())
            throw new IdentityPartitionNotFoundException(id);

        Iterator iter = result.iterator();
        IdentityPartition g = pm.detachCopy((IdentityPartition)iter.next());

        if (logger.isTraceEnabled())
            logger.trace("Retrieved IdentityPartition " + g.getId() + " '" + g.getName() + "'");

        return g;

    }

    public IdentityPartition update(IdentityPartition identitypartition) throws IdentityPartitionNotFoundException {
        pm.getFetchPlan().addGroup("identitypartition_f_identitypartition");

        String qryStr = "id == "+ identitypartition.getId();

        if (logger.isTraceEnabled())
            logger.trace("Update IdentityPartition. Query '" + qryStr + "'");

        Query q = pm.newQuery(IdentityPartition.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new IdentityPartitionNotFoundException(identitypartition.getId(), identitypartition.getName());

        IdentityPartition oldIdentityPartition = (IdentityPartition)it.next();
        oldIdentityPartition.setName(identitypartition.getName());
        oldIdentityPartition.setDescription(identitypartition.getDescription());

        pm.detachCopy(identitypartition);

        return oldIdentityPartition;


    }

    public void delete(long id) throws IdentityPartitionNotFoundException {

        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Delete IdentityPartition " + id);

        Query q = pm.newQuery(IdentityPartition.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new IdentityPartitionNotFoundException(id);

        pm.deletePersistent(it.next());


    }

    // More retrieve operations

    public Collection<IdentityPartition> retrieveAll() {
        return null;
    }
    
}
