package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.SamlR2IDPConfigDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.SamlR2IDPConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SamlR2IDPConfigDAOImpl extends GenericDAOImpl<SamlR2IDPConfig, Long>
        implements SamlR2IDPConfigDAO {

    private static final Log logger = LogFactory.getLog(SamlR2IDPConfigDAOImpl.class);

    public SamlR2IDPConfigDAOImpl() {
        super(SamlR2IDPConfig.class);
    }
}
