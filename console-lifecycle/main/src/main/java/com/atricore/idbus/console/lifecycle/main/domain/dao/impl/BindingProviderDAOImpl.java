package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.BindingProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.BindingProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BindingProviderDAOImpl extends GenericDAOImpl<BindingProvider, Long>
        implements BindingProviderDAO {

    private static final Log logger = LogFactory.getLog(BindingProviderDAOImpl.class);

    public BindingProviderDAOImpl() {
        super();
    }
}
