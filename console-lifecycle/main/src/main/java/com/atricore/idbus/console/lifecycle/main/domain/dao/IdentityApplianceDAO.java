package com.atricore.idbus.console.lifecycle.main.domain.dao;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

import java.util.Collection;

public interface IdentityApplianceDAO extends GenericDAO<IdentityAppliance, Long> {

    Collection<IdentityAppliance> list(boolean deployedOnly);
}
