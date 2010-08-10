package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.AccountLinkagePolicyDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AccountLinkagePolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccountLinkagePolicyDAOImpl extends GenericDAOImpl<AccountLinkagePolicy, Long>
        implements AccountLinkagePolicyDAO {

    private static final Log logger = LogFactory.getLog(AccountLinkagePolicyDAOImpl.class);

    public AccountLinkagePolicyDAOImpl() {
        super(AccountLinkagePolicy.class);
    }
}
