package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.IdentityPartitionDAO;

import javax.jdo.PersistenceManagerFactory;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityPartitionDAOImpl extends GenericDAOImpl<IdentityPartition> implements IdentityPartitionDAO {
    
    private static final Log logger = LogFactory.getLog(IdentityPartitionDAOImpl.class);

    public IdentityPartitionDAOImpl() {
        super();
    }

    public IdentityPartitionDAOImpl(PersistenceManagerFactory pmf) {
        super(pmf);
    }
}
