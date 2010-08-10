package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.LdapIdentityVaultDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.LdapIdentityVault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapIdentityVaultDAOImpl extends GenericDAOImpl<LdapIdentityVault, Long>
        implements LdapIdentityVaultDAO {

    private static final Log logger = LogFactory.getLog(LdapIdentityVaultDAOImpl.class);

    public LdapIdentityVaultDAOImpl() {
        super(LdapIdentityVault.class);
    }
}
