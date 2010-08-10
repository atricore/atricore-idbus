package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceDeploymentDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityApplianceDeploymentDAOImpl extends GenericDAOImpl<IdentityApplianceDeployment, Long>
        implements IdentityApplianceDeploymentDAO {

    private static final Log logger = LogFactory.getLog(IdentityApplianceDeploymentDAOImpl.class);

    public IdentityApplianceDeploymentDAOImpl() {
        super(IdentityApplianceDeployment.class);
    }
}
