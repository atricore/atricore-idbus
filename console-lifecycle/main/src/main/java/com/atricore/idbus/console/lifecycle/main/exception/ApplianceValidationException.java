package com.atricore.idbus.console.lifecycle.main.exception;

import com.atricore.idbus.console.lifecycle.main.impl.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceValidationException extends IdentityServerException {

    private List<ValidationError> errors;

    public ApplianceValidationException(Collection<ValidationError> errors) {
        super("There are " + errors.size() + " validation errors");
        this.errors = new ArrayList<ValidationError>();
        this.errors.addAll(errors);
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }
}
