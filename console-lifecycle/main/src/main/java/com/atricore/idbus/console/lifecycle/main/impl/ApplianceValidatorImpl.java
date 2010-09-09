package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplinaceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceDefinitionValidator;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionVisitor;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceValidatorImpl extends AbstractApplianceDefinitionVisitor implements ApplianceDefinitionValidator {

    private IdentityApplianceDefinitionWalker walker;

    public void validate(IdentityAppliance appliance) throws ApplinaceValidationException {
        try {
            walker.walk(appliance.getIdApplianceDefinition(), this);

        } catch (ApplinaceValidationException e) {
            throw e;

        } catch (Exception e) {
            throw new ApplinaceValidationException("Invalid Appliance Definition : " + e.getMessage(), e);
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
        if (node.getName() == null)
            throw new ApplinaceValidationException("Appliance name cannot be null");
    }
}
