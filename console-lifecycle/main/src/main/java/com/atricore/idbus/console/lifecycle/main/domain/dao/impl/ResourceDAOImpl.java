package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.ResourceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResourceDAOImpl extends GenericDAOImpl<Resource, Long> implements ResourceDAO {

    private static final Log logger = LogFactory.getLog(ResourceDAOImpl.class);

    public ResourceDAOImpl() {
        super();
    }
}
