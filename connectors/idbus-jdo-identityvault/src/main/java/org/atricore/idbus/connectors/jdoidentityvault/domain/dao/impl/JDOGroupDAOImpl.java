package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;

import javax.jdo.PersistenceManager;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOGroupDAOImpl extends GenericDAOImpl<JDOGroup> implements JDOGroupDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupDAOImpl.class);

    public JDOGroupDAOImpl() {
        super();
    }

    public JDOGroupDAOImpl(PersistenceManager pm) {
        super(pm);
    }
}
