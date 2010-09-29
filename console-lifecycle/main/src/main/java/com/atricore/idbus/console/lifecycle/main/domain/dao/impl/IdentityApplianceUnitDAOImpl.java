package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceUnitDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class IdentityApplianceUnitDAOImpl extends GenericDAOImpl<IdentityApplianceUnit, Long> 
        implements IdentityApplianceUnitDAO {

    private static final Log logger = LogFactory.getLog(IdentityApplianceUnitDAOImpl.class);

    public IdentityApplianceUnitDAOImpl() {
        super();
    }

    public void deleteUnitsByGroup(String group) {
        PersistenceManager pm = getPersistenceManager();
        Query query = pm.newQuery("SELECT FROM com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit" +
                " WHERE this.group == '" + group + "'");
        Collection<IdentityApplianceUnit> units = (Collection<IdentityApplianceUnit>) query.execute();
        if (units != null) {
            for (IdentityApplianceUnit unit : units) {
                delete(unit.getId());
            }
        }
    }
}
