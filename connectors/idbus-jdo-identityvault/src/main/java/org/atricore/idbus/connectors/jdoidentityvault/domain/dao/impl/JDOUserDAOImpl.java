package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import javax.jdo.PersistenceManager;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOUserDAOImpl extends GenericDAOImpl<JDOUser, Long> implements JDOUserDAO {

    private static final Log logger = LogFactory.getLog(JDOUserDAOImpl.class);

    public JDOUserDAOImpl() {
        super();
    }

}
