package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.ServiceProviderChannelDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProviderChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceProviderChannelDAOImpl extends GenericDAOImpl<ServiceProviderChannel, Long>
        implements ServiceProviderChannelDAO {

    private static final Log logger = LogFactory.getLog(ServiceProviderChannelDAOImpl.class);

    public ServiceProviderChannelDAOImpl() {
        super();
    }
}
