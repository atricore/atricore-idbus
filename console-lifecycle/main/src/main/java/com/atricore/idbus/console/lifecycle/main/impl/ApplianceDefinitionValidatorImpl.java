package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceDefinitionValidator;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceDefinitionValidatorImpl extends AbstractApplianceDefinitionVisitor
        implements ApplianceDefinitionValidator {

    private static final Log logger = LogFactory.getLog(ApplianceDefinitionValidatorImpl.class);

    private IdentityApplianceDefinitionWalker walker;

    private static ThreadLocal<ValidationContext> ctx = new ThreadLocal<ValidationContext>();

    public void validate(IdentityAppliance appliance) throws ApplianceValidationException {
        ValidationContext vctx = new ValidationContext();
        ctx.set(vctx);

        try {
            walker.walk(appliance.getIdApplianceDefinition(), this);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addError("Fatal error", e);
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

        if (node.getAuthenticationMechanisms() == null || node.getAuthenticationMechanisms().size() < 1) {
            addError("Serivce Provider needs at least one Authentication Mechanism");
        }

        if (node.getIdentityLookup() == null)
            addError("Serivce Provider needs an Indentity Lookup connection");

        if (node.getConfig() ==null)
            addError("No configuration found for SP " + node.getName());
        else {
            if (node.getConfig() instanceof SamlR2ProviderConfig) {
                SamlR2ProviderConfig samlCfg = (SamlR2ProviderConfig) node.getConfig();

                if (samlCfg.getSigner() == null && !samlCfg.isUseSampleStore()) {
                    addError("No singer found and use sample store is set to false for " + node.getName());
                }

                if (samlCfg.getEncrypter() == null && !samlCfg.isUseSampleStore()) {
                    addError("No encrypter found and use sample store is set to false for " + node.getName());
                }

            }
        }
    }

    @Override
    public void arrive(ServiceProvider node) throws Exception {
        validateName("SP name", node.getName());
        validateDisplayName("SP display name", node.getDisplayName());

        validateLocation("SP", node.getLocation(), true);

        int preferred = 0;

        for (FederatedConnection fcA : node.getFederatedConnectionsA()) {

            if (fcA.getChannelA() instanceof IdentityProviderChannel) {
                IdentityProviderChannel c = (IdentityProviderChannel) fcA.getChannelA();
                if (c.isPreferred())
                    preferred ++;
            } else {
                addError("Federated Connection A does not relate this SP with an IDP : " + fcA.getChannelA().getClass().getSimpleName());
            }

            if (fcA.getRoleA() != node) {
                addError("Federated Connection A does not point to this provider " + node.getName() + "["+fcA.getRoleA().getName()+"]");
            }
        }

        for (FederatedConnection fcB : node.getFederatedConnectionsB()) {

            if (fcB.getChannelB() instanceof IdentityProviderChannel) {
                IdentityProviderChannel c = (IdentityProviderChannel) fcB.getChannelB();
                if (c.isPreferred())
                    preferred ++;
            } else {
                addError("Federated Connection B does not relate this SP with an IDP : " + fcB.getChannelB().getClass().getSimpleName());
            }

            if (fcB.getRoleB() != node) {
                addError("Federated Connection B does not point to this provider " + node.getName() + "["+fcB.getRoleA().getName()+"]");
            }
        }

        if (preferred < 1)
            addError("No preferred Identity Provider Channel defined for SP " + node.getName());

        if (preferred > 1)
            addError("Too many Identity Provider Channels defined for SP " + node.getName() + ", found " + preferred);

        if (node.getConfig() ==null)
            addError("No configuration found for SP " + node.getName());
        else {
            if (node.getConfig() instanceof SamlR2ProviderConfig) {
                SamlR2ProviderConfig samlCfg = (SamlR2ProviderConfig) node.getConfig();

                if (samlCfg.getSigner() == null && !samlCfg.isUseSampleStore()) {
                    addError("No singer found and use sample store is set to false for " + node.getName());
                }

                if (samlCfg.getEncrypter() == null && !samlCfg.isUseSampleStore()) {
                    addError("No encrypter found and use sample store is set to false for " + node.getName());
                }

            }
        }
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

        if (node.getIdentitySource() == null) {
            addError("Identity Lookup " + node.getName() + " Identity Source cannot be null");
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

    @Override
    public void arrive(XmlIdentitySource node) throws Exception {
        validateDisplayName("XML Identity Source name" , node.getName());
        validateDisplayName("XML Identity Source display name" , node.getDisplayName());

        if (node.getXmlUrl() == null)
            addError("XML Idenity Source must define a XML Url");
    }


    @Override
    public void arrive(ServiceProviderChannel node) throws Exception {
        if (node.isOverrideProviderSetup())
            validateLocation("Serivce Provider channel ", node.getLocation());
    }

    @Override
    public void arrive(IdentityProviderChannel node) throws Exception {
        if (node.isOverrideProviderSetup())
            validateLocation("Identity Provider channel ", node.getLocation());
    }




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
        if (pkgName == null || pkgName.length() == 0) {
            addError(propertyName + " cannot be null or empty");
            return;
        }

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
        else {
            if (location.getContext().startsWith("/"))
                addError(propertyName + " location context must be relative (do not start it with '/')");

            if (location.getContext().lastIndexOf("/") > 1)
                addError(propertyName + " location context must not be a path (do not use '/')");

        }

        if (validateUri) {
            if (location.getUri() == null)
                addError(propertyName + " location URI cannot be null");
            else if (location.getUri().startsWith("/"))
                addError(propertyName + " location URI must be relative (do not start it with '/')");
        }




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
