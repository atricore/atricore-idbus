package com.atricore.idbus.console.lifecycle.main.exception;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.impl.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceValidationException extends IdentityServerException {

    private List<ValidationError> errors;

    public ApplianceValidationException(IdentityAppliance appliance, Collection<ValidationError> errors) {
        super("There" + ( errors.size() == 1 ? " is " : " are " ) +
                errors.size() + " validation " + (errors.size() == 1 ? "error" : " errors") +
                " for appliance " +
        (appliance != null &&
                appliance.getIdApplianceDefinition() != null ?
                appliance.getIdApplianceDefinition().getName() :"UNKNOWN"));

        this.errors = new ArrayList<ValidationError>();
        this.errors.addAll(errors);

    }

    public ApplianceValidationException(Collection<ValidationError> errors) {
        this(null, errors);
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }
}
