package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.ProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProviderDAOImpl extends GenericDAOImpl<Provider, Long> implements ProviderDAO {

    private static final Log logger = LogFactory.getLog(ProviderDAOImpl.class);

    public ProviderDAOImpl() {
        super();
    }
}
