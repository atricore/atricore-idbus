package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityProviderChannelDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProviderChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentityProviderChannelDAOImpl extends GenericDAOImpl<IdentityProviderChannel, Long>
        implements IdentityProviderChannelDAO {

    private static final Log logger = LogFactory.getLog(IdentityProviderChannelDAOImpl.class);

    public IdentityProviderChannelDAOImpl() {
        super(IdentityProviderChannel.class);
    }
}
