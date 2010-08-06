package org.atricore.idbus.kernel.main.provisioning.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.UserDAO;

import javax.jdo.PersistenceManagerFactory;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO {

    private static final Log logger = LogFactory.getLog(UserDAOImpl.class);

    public UserDAOImpl() {
        super();
    }

    public UserDAOImpl(PersistenceManagerFactory pmf) {
        super(pmf);
    }
}
