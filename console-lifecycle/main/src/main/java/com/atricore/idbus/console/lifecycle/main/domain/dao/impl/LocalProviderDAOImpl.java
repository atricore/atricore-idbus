package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.LocalProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.LocalProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocalProviderDAOImpl extends GenericDAOImpl<LocalProvider, Long>
        implements LocalProviderDAO {

    private static final Log logger = LogFactory.getLog(LocalProviderDAOImpl.class);

    public LocalProviderDAOImpl() {
        super();
    }
}
