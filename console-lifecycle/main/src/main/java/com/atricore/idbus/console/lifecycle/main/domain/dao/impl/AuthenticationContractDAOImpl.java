package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.AuthenticationContractDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationContract;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationContractDAOImpl extends GenericDAOImpl<AuthenticationContract, Long>
        implements AuthenticationContractDAO {

    private static final Log logger = LogFactory.getLog(AuthenticationContractDAOImpl.class);

    public AuthenticationContractDAOImpl() {
        super();
    }
}
