package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.OrganizationDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Organization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrganizationDAOImpl extends GenericDAOImpl<Organization, Long>
        implements OrganizationDAO {

    private static final Log logger = LogFactory.getLog(OrganizationDAOImpl.class);

    public OrganizationDAOImpl() {
        super();
    }
}
