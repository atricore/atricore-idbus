package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.UserInformationLookupDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.UserInformationLookup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserInformationLookupDAOImpl extends GenericDAOImpl<UserInformationLookup, Long>
        implements UserInformationLookupDAO {

    private static final Log logger = LogFactory.getLog(UserInformationLookupDAOImpl.class);

    public UserInformationLookupDAOImpl() {
        super(UserInformationLookup.class);
    }
}
