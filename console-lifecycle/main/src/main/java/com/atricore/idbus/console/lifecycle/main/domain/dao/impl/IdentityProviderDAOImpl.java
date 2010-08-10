package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityProviderDAOImpl extends GenericDAOImpl<IdentityProvider, Long>
        implements IdentityProviderDAO {

    private static final Log logger = LogFactory.getLog(IdentityProviderDAOImpl.class);

    public IdentityProviderDAOImpl() {
        super(IdentityProvider.class);
    }
}
