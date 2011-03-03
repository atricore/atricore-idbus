package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeValue;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserAttributeValueDAO;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class JDOUserAttributeValueDAOImpl extends GenericDAOImpl<JDOUserAttributeValue, Long>
        implements JDOUserAttributeValueDAO {

    private static final Log logger = LogFactory.getLog(JDOUserAttributeValueDAOImpl.class);

    public void updateName(String oldName, String newName) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeValue" +
                " WHERE this.name == '" + oldName + "'");

        Collection<JDOUserAttributeValue> values = (Collection<JDOUserAttributeValue>) query.execute();
        if (values != null) {
            for (JDOUserAttributeValue value : values) {
                value.setName(newName);
                save(value);
            }
        }
    }

    public void deleteValues(String name) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUserAttributeValue" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOUserAttributeValue> values = (Collection<JDOUserAttributeValue>) query.execute();
        if (values != null) {
            for (JDOUserAttributeValue value : values) {
                delete(value.getId());
            }
        }
    }

    public void deleteRemovedValues(JDOUserAttributeValue[] oldValues, JDOUserAttributeValue[] newValues) {
        if (oldValues == null)
            return;

        if (newValues == null)
            newValues = new JDOUserAttributeValue[]{};
        
        for (JDOUserAttributeValue oldValue : oldValues) {
            boolean removed = true;
            for (JDOUserAttributeValue newValue : newValues) {
                if (oldValue.getId() == newValue.getId()) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                delete(oldValue.getId());
            }
        }
    }
}
