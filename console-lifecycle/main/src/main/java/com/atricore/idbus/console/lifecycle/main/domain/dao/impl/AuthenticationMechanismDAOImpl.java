package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.AuthenticationMechanismDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationMechanismDAOImpl extends GenericDAOImpl<AuthenticationMechanism, Long>
        implements AuthenticationMechanismDAO {

    private static final Log logger = LogFactory.getLog(AuthenticationMechanismDAOImpl.class);

    public AuthenticationMechanismDAOImpl() {
        super(AuthenticationMechanism.class);
    }
}
