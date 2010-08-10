package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.JossoBPConfigDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.JossoBPConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JossoBPConfigDAOImpl extends GenericDAOImpl<JossoBPConfig, Long>
        implements JossoBPConfigDAO {

    private static final Log logger = LogFactory.getLog(JossoBPConfigDAOImpl.class);

    public JossoBPConfigDAOImpl() {
        super(JossoBPConfig.class);
    }
}
