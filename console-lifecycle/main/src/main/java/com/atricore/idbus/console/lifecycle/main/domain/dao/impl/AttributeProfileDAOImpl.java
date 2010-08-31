package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.AttributeProfileDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AttributeProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AttributeProfileDAOImpl extends GenericDAOImpl<AttributeProfile, Long>
        implements AttributeProfileDAO {

    private static final Log logger = LogFactory.getLog(AttributeProfileDAOImpl.class);

    public AttributeProfileDAOImpl() {
        super();
    }
}
