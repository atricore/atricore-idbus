package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroup;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOAclEntryDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class JDOAclEntryDAOImpl extends GenericDAOImpl<JDOAclEntry, Long> implements JDOAclEntryDAO {

    private static final Log logger = LogFactory.getLog(JDOAclEntryDAOImpl.class);

    public JDOAclEntry findByFrom(String from) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry" +
                " WHERE this.from == '" + from + "'");

        Collection<JDOAclEntry> entries = (Collection<JDOAclEntry>) query.execute();

        if (entries == null || entries.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, entries.size());

        return (entries == null) ? null : entries.iterator().next();
    }

    public JDOAclEntry findByApprovalToken(String approvalToken) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOAclEntry" +
                " WHERE this.approvalToken == '" + approvalToken + "'");

        Collection<JDOAclEntry> entries = (Collection<JDOAclEntry>) query.execute();

        if (entries == null || entries.size() != 1)
            throw new IncorrectResultSizeDataAccessException(1, entries.size());

        return (entries == null) ? null : entries.iterator().next();
    }
}
