package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeValue;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupAttributeValueDAO;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class JDOGroupAttributeValueDAOImpl extends GenericDAOImpl<JDOGroupAttributeValue, Long>
        implements JDOGroupAttributeValueDAO {

    private static final Log logger = LogFactory.getLog(JDOGroupAttributeValueDAOImpl.class);

    public void updateName(String oldName, String newName) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeValue" +
                " WHERE this.name == '" + oldName + "'");

        Collection<JDOGroupAttributeValue> values = (Collection<JDOGroupAttributeValue>) query.execute();
        if (values != null) {
            for (JDOGroupAttributeValue value : values) {
                value.setName(newName);
                save(value);
            }
        }
    }

    public void deleteValues(String name) {
        PersistenceManager pm = getPersistenceManager();

        Query query = pm.newQuery("SELECT FROM org.atricore.idbus.connectors.jdoidentityvault.domain.JDOGroupAttributeValue" +
                " WHERE this.name == '" + name + "'");

        Collection<JDOGroupAttributeValue> values = (Collection<JDOGroupAttributeValue>) query.execute();
        if (values != null) {
            for (JDOGroupAttributeValue value : values) {
                delete(value.getId());
            }
        }
    }

    public void deleteRemovedValues(JDOGroupAttributeValue[] oldValues, JDOGroupAttributeValue[] newValues) {
        if (oldValues == null)
            return;

        if (newValues == null)
            newValues = new JDOGroupAttributeValue[]{};
        
        for (JDOGroupAttributeValue oldValue : oldValues) {
            boolean removed = true;
            for (JDOGroupAttributeValue newValue : newValues) {
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
