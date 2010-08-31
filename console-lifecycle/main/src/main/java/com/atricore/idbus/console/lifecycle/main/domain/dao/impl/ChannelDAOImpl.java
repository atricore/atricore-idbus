package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.ChannelDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChannelDAOImpl extends GenericDAOImpl<Channel, Long> implements ChannelDAO {

    private static final Log logger = LogFactory.getLog(ChannelDAOImpl.class);

    public ChannelDAOImpl() {
        super();
    }
}
