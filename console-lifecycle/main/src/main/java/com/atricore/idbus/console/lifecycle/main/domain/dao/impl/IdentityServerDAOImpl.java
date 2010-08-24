package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityServer;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityServerDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityServerDAOImpl extends GenericDAOImpl<IdentityServer, Long>
        implements IdentityServerDAO {

    private static final Log logger = LogFactory.getLog(IdentityServerDAOImpl.class);

    public IdentityServerDAOImpl() {
        super();
    }
}
