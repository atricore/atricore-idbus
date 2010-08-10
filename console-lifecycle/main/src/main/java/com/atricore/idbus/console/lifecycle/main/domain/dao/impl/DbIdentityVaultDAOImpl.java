package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.DbIdentityVaultDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.DbIdentityVault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DbIdentityVaultDAOImpl extends GenericDAOImpl<DbIdentityVault, Long>
        implements DbIdentityVaultDAO {

    private static final Log logger = LogFactory.getLog(DbIdentityVaultDAOImpl.class);

    public DbIdentityVaultDAOImpl() {
        super(DbIdentityVault.class);
    }
}
