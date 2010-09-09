package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplinaceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceDefinitionValidator;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceValidatorImpl extends AbstractApplianceDefinitionVisitor implements ApplianceDefinitionValidator {

    private IdentityApplianceDefinitionWalker walker;

    private static ThreadLocal<ValidationContext> ctx = new ThreadLocal<ValidationContext>();

    public void validate(IdentityAppliance appliance) throws ApplinaceValidationException {
        ValidationContext vctx = new ValidationContext();
        ctx.set(vctx);

        try {
            walker.walk(appliance.getIdApplianceDefinition(), this);
        } catch (Exception e) {
            addError("Fatal error : " + e.getMessage(), e);
        }

        if (vctx.getErrors().size() > 0) {
            // TODO : Send errors in exception
            throw new ApplinaceValidationException("errors found");
        }
    }

    public IdentityApplianceDefinitionWalker getWalker() {
        return walker;
    }

    public void setWalker(IdentityApplianceDefinitionWalker walker) {
        this.walker = walker;
    }

    @Override
    public void arrive(IdentityApplianceDefinition node) throws Exception {

        validateName(node.getName(), "Appliance name");
        validateName(node.getDisplayName(), "Appliance display name");
        validateName(node.getNamespace(), "Appliance namespace");

        if (node.getLocation() == null) {
            addError("Appliance location cannot be null");
        } else {
            Location l = node.getLocation();

            if (l.getHost() == null)
                addError("Appliance location host cannot be null");
        }

    }

    protected void validateName(String propertyName, String name) {
        if (name == null || name.length() == 0) {
            addError(propertyName + " cannot be null");
            return;
        }

        for (int i = 0 ; i < name.length() ; i ++) {
            if (Character.isLetterOrDigit(name.charAt(i)));
        }


    }


    protected boolean nameIsValid(String name) {
        return true;
    }

    protected void addError(String msg, Throwable t) {
        ctx.get().getErrors().add(new ValidationError(msg, t));
    }

    protected void addError(String msg) {
        ctx.get().getErrors().add(new ValidationError(msg));
    }

    protected class ValidationContext {
        private List<ValidationError> errors = new ArrayList<ValidationError>();

        public List<ValidationError> getErrors() {
            return errors;
        }
    }

    protected class ValidationError {

        private String msg;

        private Throwable error;

        public ValidationError(String msg) {
            this.msg = msg;
        }

        public ValidationError(String msg, Throwable error) {
            this.msg = msg;
            this.error = error;
        }

        public String getMsg() {
            return msg;
        }

        public Throwable getError() {
            return error;
        }
    }


}
