package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAcl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOAclDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOAclEntryDAO;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class JDOAclDAOImpl extends GenericDAOImpl<JDOAcl, Long> implements JDOAclDAO {

    private static final Log logger = LogFactory.getLog(JDOAclDAOImpl.class);

    public JDOAcl findByName(String name) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAcl" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOAcl> acls = (Collection<JDOAcl>) query.execute();

        return (acls == null) ? null : acls.iterator().next();
    }

}
