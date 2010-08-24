package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.SamlR2ProviderConfigDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.SamlR2ProviderConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SamlR2ProviderConfigDAOImpl extends GenericDAOImpl<SamlR2ProviderConfig, Long>
        implements SamlR2ProviderConfigDAO {

    private static final Log logger = LogFactory.getLog(SamlR2ProviderConfigDAOImpl.class);

    public SamlR2ProviderConfigDAOImpl() {
        super();
    }
}
