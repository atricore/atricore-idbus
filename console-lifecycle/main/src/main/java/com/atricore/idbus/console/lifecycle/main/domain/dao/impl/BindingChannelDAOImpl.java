package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.BindingChannelDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.BindingChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BindingChannelDAOImpl extends GenericDAOImpl<BindingChannel, Long>
        implements BindingChannelDAO {

    private static final Log logger = LogFactory.getLog(BindingChannelDAOImpl.class);

    public BindingChannelDAOImpl() {
        super();
    }
}
