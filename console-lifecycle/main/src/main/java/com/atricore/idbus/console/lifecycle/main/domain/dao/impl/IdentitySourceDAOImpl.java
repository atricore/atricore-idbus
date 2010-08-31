package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentitySourceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentitySourceDAOImpl extends GenericDAOImpl<IdentitySource, Long>
        implements IdentitySourceDAO {

    private static final Log logger = LogFactory.getLog(IdentitySourceDAOImpl.class);

    public IdentitySourceDAOImpl() {
        super();
    }
}
