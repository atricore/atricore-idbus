package com.atricore.idbus.console.lifecycle.main.domain.dao;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceNotFoundException;

import java.util.Collection;

public interface IdentityApplianceDAO extends GenericDAO<IdentityAppliance, Long> {

    Collection<IdentityAppliance> list(boolean deployedOnly);

    IdentityAppliance findByName(String name) throws ApplianceNotFoundException;

    IdentityAppliance findByNamespace(String namespace) throws ApplianceNotFoundException;

    boolean namespaceExists(long applianceId, String namespace);
}
