package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.AuthenticationAssertionEmissionPolicyDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationAssertionEmissionPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationAssertionEmissionPolicyDAOImpl extends GenericDAOImpl<AuthenticationAssertionEmissionPolicy, Long>
        implements AuthenticationAssertionEmissionPolicyDAO {

    private static final Log logger = LogFactory.getLog(AuthenticationAssertionEmissionPolicyDAOImpl.class);

    public AuthenticationAssertionEmissionPolicyDAOImpl() {
        super(AuthenticationAssertionEmissionPolicy.class);
    }
}
