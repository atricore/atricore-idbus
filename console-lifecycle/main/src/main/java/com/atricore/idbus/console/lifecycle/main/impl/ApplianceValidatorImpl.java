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
        // TODO : Validate more !!!
        /*
        if (node.getName() == null) {
            addError("Appliance name cannot be null");
        } else {
            if (!nameIsValid(node.getName()))
                addError("Appliance name is not valid '"+node.getName()+"'. Use only alphanumeric characters are supported");
        }

        if (node.getDisplayName() == null)
            addError("Appliance display name cannot be null");


        if (node.getNamespace() == null)
            addError("Appliance namespace cannot be null");
            */
    }

    protected void validateName(String propertyName, String name) {
        if (name == null)
            addError(propertyName + " cannot be null");
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
