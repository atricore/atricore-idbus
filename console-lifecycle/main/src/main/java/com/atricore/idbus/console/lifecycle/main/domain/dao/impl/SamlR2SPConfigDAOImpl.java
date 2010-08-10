package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.SamlR2SPConfigDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.SamlR2SPConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SamlR2SPConfigDAOImpl extends GenericDAOImpl<SamlR2SPConfig, Long>
        implements SamlR2SPConfigDAO {

    private static final Log logger = LogFactory.getLog(SamlR2SPConfigDAOImpl.class);

    public SamlR2SPConfigDAOImpl() {
        super(SamlR2SPConfig.class);
    }
}
