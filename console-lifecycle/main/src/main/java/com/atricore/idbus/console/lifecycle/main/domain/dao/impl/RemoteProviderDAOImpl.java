package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.RemoteProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.RemoteProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoteProviderDAOImpl extends GenericDAOImpl<RemoteProvider, Long>
        implements RemoteProviderDAO {

    private static final Log logger = LogFactory.getLog(RemoteProviderDAOImpl.class);

    public RemoteProviderDAOImpl() {
        super(RemoteProvider.class);
    }
}
