package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.GroupDAO;

import javax.jdo.PersistenceManagerFactory;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class GroupDAOImpl extends GenericDAOImpl<Group> implements GroupDAO {

    private static final Log logger = LogFactory.getLog(GroupDAOImpl.class);

    public GroupDAOImpl() {
        super();
    }

    public GroupDAOImpl(PersistenceManagerFactory pmf) {
        super(pmf);
    }
}
