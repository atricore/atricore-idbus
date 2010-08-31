package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceUnitDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityApplianceUnitDAOImpl extends GenericDAOImpl<IdentityApplianceUnit, Long> 
        implements IdentityApplianceUnitDAO {

    private static final Log logger = LogFactory.getLog(IdentityApplianceUnitDAOImpl.class);

    public IdentityApplianceUnitDAOImpl() {
        super();
    }
}
