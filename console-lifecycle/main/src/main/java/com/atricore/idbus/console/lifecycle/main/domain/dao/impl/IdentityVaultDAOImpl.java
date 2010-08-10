package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityVaultDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityVault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityVaultDAOImpl extends GenericDAOImpl<IdentityVault, Long>
        implements IdentityVaultDAO {

    private static final Log logger = LogFactory.getLog(IdentityVaultDAOImpl.class);

    public IdentityVaultDAOImpl() {
        super(IdentityVault.class);
    }
}
