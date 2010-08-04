package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;

/**
 */
public interface IdentityApplianceDeployer {

    IdentityAppliance deploy(IdentityAppliance ia) throws IdentityServerException;

    IdentityAppliance undeploy(IdentityAppliance ia) throws IdentityServerException;

    IdentityAppliance start(IdentityAppliance ia) throws IdentityServerException;

    IdentityAppliance stop(IdentityAppliance ia) throws IdentityServerException;
}
