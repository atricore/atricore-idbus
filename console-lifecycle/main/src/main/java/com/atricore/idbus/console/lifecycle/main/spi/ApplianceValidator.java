package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ApplianceValidator {

    void validate(IdentityAppliance appliance) throws ApplianceValidationException;

    void validate(IdentityAppliance appliance, Operation operation) throws ApplianceValidationException;

    public enum Operation {
        ADD,
        UPDATE,
        DELETE,
        IMPORT,
        EXPORT,
        ANY
    }

}
