package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.DbIdentitySourceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.DbIdentitySource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DbIdentitySourceDAOImpl extends GenericDAOImpl<DbIdentitySource, Long>
        implements DbIdentitySourceDAO {

    private static final Log logger = LogFactory.getLog(DbIdentitySourceDAOImpl.class);

    public DbIdentitySourceDAOImpl() {
        super();
    }
}
