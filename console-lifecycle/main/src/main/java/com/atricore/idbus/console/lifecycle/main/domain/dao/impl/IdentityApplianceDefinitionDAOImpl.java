package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceDefinitionDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityApplianceDefinitionDAOImpl extends GenericDAOImpl<IdentityApplianceDefinition, Long>
        implements IdentityApplianceDefinitionDAO {

    private static final Log logger = LogFactory.getLog(IdentityApplianceDefinitionDAOImpl.class);

    public IdentityApplianceDefinitionDAOImpl() {
        super(IdentityApplianceDefinition.class);
    }
}
