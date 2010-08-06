package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class GroupDAOImpl {

    private static final Log logger = LogFactory.getLog(GroupDAOImpl.class);

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

    // Group CRUD Operations

    public Group create(String name, String description) throws ProvisioningException {

        try {
            pm.getFetchPlan().addGroup("group_f_group");

            Group newGroup = new Group();
            newGroup.setName(name);
            newGroup.setDescription(description);

            if (logger.isTraceEnabled())
                logger.trace("Create Group : '" + name + "'");

            return pm.makePersistent(newGroup);

        } catch (Exception e){
            throw new ProvisioningException("Error persisting Group '"+name+"' ",e);
        }
    }

    public Group retrieve(long id) throws GroupNotFoundException {

        pm.getFetchPlan().addGroup("group_f_group");
        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Retrieve Group. Query '" + qryStr + "'");

        Query q = pm.newQuery(Group.class, qryStr);
        Collection result = (Collection)q.execute();

        if (result.isEmpty())
            throw new GroupNotFoundException(id);

        Iterator iter = result.iterator();
        Group g = pm.detachCopy((Group)iter.next());

        if (logger.isTraceEnabled())
            logger.trace("Retrieved Group " + g.getId() + " '" + g.getName() + "'");

        return g;

    }

    public Group update(Group group) throws GroupNotFoundException {
        pm.getFetchPlan().addGroup("group_f_group");

        String qryStr = "id == "+ group.getId();

        if (logger.isTraceEnabled())
            logger.trace("Update Group. Query '" + qryStr + "'");

        Query q = pm.newQuery(Group.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new GroupNotFoundException(group.getId(), group.getName());

        Group oldGroup = (Group)it.next();
        oldGroup.setName(group.getName());
        oldGroup.setDescription(group.getDescription());

        pm.detachCopy(group);

        return oldGroup;


    }

    public void delete(long id) throws GroupNotFoundException {

        String qryStr = "id == "+ id;

        if (logger.isTraceEnabled())
            logger.trace("Delete Group " + id);

        Query q = pm.newQuery(Group.class, qryStr);
        Collection result = (Collection)q.execute();
        Iterator it = result.iterator();

        if (!it.hasNext())
            throw new GroupNotFoundException(id);

        pm.deletePersistent(it.next());


    }

    // More retrieve operations

    public Collection<Group> retreiveAll() {
        return null;
    }
}
