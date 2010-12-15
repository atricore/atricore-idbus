package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ApplianceMarshaller {

    IdentityAppliance unmarshall(byte[] beans) throws IdentityServerException;

    byte[] marshall(IdentityAppliance appliance) throws IdentityServerException;
}
