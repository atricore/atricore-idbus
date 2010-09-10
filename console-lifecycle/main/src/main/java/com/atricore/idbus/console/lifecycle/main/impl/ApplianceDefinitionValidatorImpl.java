package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceDefinitionValidator;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceDefinitionValidatorImpl extends AbstractApplianceDefinitionVisitor
        implements ApplianceDefinitionValidator {

    private IdentityApplianceDefinitionWalker walker;

    private static ThreadLocal<ValidationContext> ctx = new ThreadLocal<ValidationContext>();

    public void validate(IdentityAppliance appliance) throws ApplianceValidationException {
        ValidationContext vctx = new ValidationContext();
        ctx.set(vctx);

        try {
            walker.walk(appliance.getIdApplianceDefinition(), this);
        } catch (Exception e) {
            addError("Fatal error : " + e.getMessage(), e);
        }

        if (vctx.getErrors().size() > 0) {
            throw new ApplianceValidationException(vctx.getErrors());
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

        validateName("Appliance name", node.getName());
        validateDisplayName("Appliance display name", node.getDisplayName());
        validatePackageName("Appliance namespace", node.getNamespace());

        validateLocation("Appliance", node.getLocation());

    }

    @Override
    public void arrive(IdentityProvider node) throws Exception {
        validateName("IDP name", node.getName());
        validateDisplayName("IDP display name", node.getDisplayName());

        validateLocation("IDP", node.getLocation(), true);

        for (FederatedConnection fcA : node.getFederatedConnectionsA()) {
            if (fcA.getRoleA() != node) {
                addError("Federated Connection A does not point to this provider " + node.getName() + "["+fcA.getRoleA().getName()+"]");
            }
        }

        for (FederatedConnection fcB : node.getFederatedConnectionsB()) {
            if (fcB.getRoleB() != node) {
                addError("Federated Connection B does not point to this provider " + node.getName() + "["+fcB.getRoleA().getName()+"]");
            }
        }

        // TODO !
    }

    @Override
    public void arrive(ServiceProvider node) throws Exception {
        validateName("SP name", node.getName());
        validateDisplayName("SP display name", node.getDisplayName());

        validateLocation("SP", node.getLocation(), true);

        for (FederatedConnection fcA : node.getFederatedConnectionsA()) {
            if (fcA.getRoleA() != node) {
                addError("Federated Connection A does not point to this provider " + node.getName() + "["+fcA.getRoleA().getName()+"]");
            }
        }

        for (FederatedConnection fcB : node.getFederatedConnectionsB()) {
            if (fcB.getRoleB() != node) {
                addError("Federated Connection B does not point to this provider " + node.getName() + "["+fcB.getRoleA().getName()+"]");
            }
        }

        // TODO !
    }

    @Override
    public void arrive(FederatedConnection node) throws Exception {
        validateName("Federated Connection name", node.getName());

        // Role A
        if (node.getRoleA() == null)
            addError("Federated Connection roleA cannot be null for " + node.getName());

        if (node.getChannelA() == null)
            addError("Federated Connection channelA cannot be null for " + node.getName());

        // Role B
        if (node.getRoleB() == null)
            addError("Federated Connection roleB cannot be null for " + node.getName());

        if (node.getChannelB() == null)
            addError("Federated Connection channelB cannot be null for " + node.getName());

        // TODO !
    }

    @Override
    public void arrive(IdentityLookup node) throws Exception {
        validateName("Identity Lookup name", node.getName());

        if (node.getProvider() == null)
            addError("Identity Lookup provider cannot be null " + node.getName());

        if (node.getProvider() == null)
            addError("Identity Lookup " + node.getName() + " Provider cannot be null");
        else {
            if (node.getProvider().getIdentityLookup() != node) {
                addError("Provider Identity Lookup is not this Identity Lookup" +
                        node.getName() +
                        " ["+node.getProvider().getIdentityLookup()+"]");
            }
        }

    }

    @Override
    public void arrive(JOSSOActivation node) throws Exception {

        validateName("JOSSO Activation name", node.getName());
        validateDisplayName("JOSSO Activation display name", node.getDisplayName());

        if (node.getPartnerAppId() == null)
            addError("JOSSO Activation partner app. ID cannot be null ");

        validateLocation("JOSSO Activation partner app.", node.getPartnerAppLocation());

        if (node.getSp() == null)
            addError("JOSSO Activation " + node.getName() + " SP cannot be null");
        else {
            if (node.getSp().getActivation() != node) {
                addError("SP Activation is not this activation " +
                        node.getName() +
                        " ["+node.getSp().getActivation().getName()+"]");
            }
        }

    }

    @Override
    public void arrive(ExecutionEnvironment node) throws Exception {

        validateName("Execution Environment name" , node.getName());
        validateDisplayName("Execution Environment display name" , node.getDisplayName());

        if (node.getPlatformId() == null)
            addError("Execution Environment platform ID cannot be null");

        if (node.getInstallUri() == null)
            addError("Execution Environment install URI cannot be null");

    }

    @Override
    public void arrive(DbIdentitySource node) throws Exception {
        validateName("DB Identity Source name" , node.getName());
        validateDisplayName("DB Identity Source display name" , node.getDisplayName());

        // TODO !
    }

    @Override
    public void arrive(EmbeddedIdentitySource node) throws Exception {
        validateName("Embedded Identity Source name" , node.getName());
        validateDisplayName("Ebmedded Identity Source display name" , node.getDisplayName());
        // TODO !
    }

    @Override
    public void arrive(LdapIdentitySource node) throws Exception {
        validateName("LDAP Identity Source name" , node.getName());
        validateDisplayName("LDAP Identity Source display name" , node.getDisplayName());

        // TODO !
    }

    /*
    @Override
    public void arrive(XMLIdentitySource node) throws Exception {
        validateDisplayName("XML Identity Source name" , node.getName());
        validateDisplayName("XML Identity Source display name" , node.getDisplayName());

        // TODO !
    }
    */

    // ---------------------------------------------------------------------
    // UTILS
    // ---------------------------------------------------------------------

    protected void validateName(String propertyName, String name) {

        if (name == null || name.length() == 0) {
            addError(propertyName + " cannot be null or empty");
            return;
        }

        for (int i = 0 ; i < name.length() ; i ++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                if (c != '-') {
                    addError(propertyName + " must contain only letters, numbers and '-' characters, value : " + name);
                }
            }
        }
    }

    protected void validateDisplayName(String propertyName, String name) {
        if (name == null || name.length() == 0) {
            addError(propertyName + " cannot be null or empty");
            return;
        }

    }


    protected void validatePackageName(String propertyName, String pkgName) {
        for (int i = 0 ; i < pkgName.length() ; i ++) {
            char c = pkgName.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                if (c != '.') {
                    addError(propertyName + " must contain only letters, numbers and '.' characters. value : " + pkgName);
                    return ;
                }
            }

        }
    }

    protected void validateLocation(String propertyName, Location location) {
        validateLocation(propertyName, location, false);
    }

    protected void validateLocation(String propertyName, Location location, boolean validateUri) {
        if (location == null) {
            addError(propertyName + " location cannot be null");
            return ;
        }

        if (location.getHost() == null)
            addError(propertyName + " location host cannot be null");

        if (location.getPort() == 0)
            addError(propertyName + " location port cannot be zero");

        if (location.getProtocol() == null)
            addError(propertyName + " location protocol cannot be null");

        if (location.getContext() == null)
            addError(propertyName + " location context cannot be null");

        if (validateUri && location.getUri() == null)
            addError(propertyName +" location URI cannot be null");

    }

    protected boolean isNumeric(String v) {
        for (int i = 0 ; i < v.length() ; i ++) {
            if (!Character.isDigit(v.charAt(i)))
                return false;
        }
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

}
