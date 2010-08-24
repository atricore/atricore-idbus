package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.KeystoreDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Keystore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeystoreDAOImpl extends GenericDAOImpl<Keystore, Long> implements KeystoreDAO {

    private static final Log logger = LogFactory.getLog(KeystoreDAOImpl.class);

    public KeystoreDAOImpl() {
        super();
    }
}
