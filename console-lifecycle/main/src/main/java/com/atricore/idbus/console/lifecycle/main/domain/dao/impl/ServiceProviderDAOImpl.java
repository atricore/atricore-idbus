package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.ServiceProviderDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.InternalSaml2ServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceProviderDAOImpl extends GenericDAOImpl<InternalSaml2ServiceProvider, Long>
        implements ServiceProviderDAO {

    private static final Log logger = LogFactory.getLog(ServiceProviderDAOImpl.class);

    public ServiceProviderDAOImpl() {
        super();
    }
}
