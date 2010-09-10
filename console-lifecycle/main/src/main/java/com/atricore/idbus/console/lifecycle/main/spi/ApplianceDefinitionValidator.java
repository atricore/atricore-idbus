package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ApplianceDefinitionValidator {

    void validate(IdentityAppliance appliance) throws ApplianceValidationException;
}
