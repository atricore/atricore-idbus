package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.LocationDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocationDAOImpl extends GenericDAOImpl<Location, Long> implements LocationDAO {

    private static final Log logger = LogFactory.getLog(LocationDAOImpl.class);

    public LocationDAOImpl() {
        super(Location.class);
    }
}
