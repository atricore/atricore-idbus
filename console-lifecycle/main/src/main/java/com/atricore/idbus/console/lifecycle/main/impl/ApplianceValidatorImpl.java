package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceNotFoundException;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceValidationException;
import com.atricore.idbus.console.lifecycle.main.spi.ApplianceValidator;
import com.atricore.idbus.console.lifecycle.main.spi.ExecEnvType;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionWalker;
import com.atricore.idbus.console.lifecycle.main.util.MetadataUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceValidatorImpl extends AbstractApplianceDefinitionVisitor
        implements ApplianceValidator {

    private static final Log logger = LogFactory.getLog(ApplianceValidatorImpl.class);

    private IdentityApplianceDefinitionWalker walker;

    private IdentityApplianceDAO dao;

    private static ThreadLocal<ValidationContext> ctx = new ThreadLocal<ValidationContext>();

    public void validate(IdentityAppliance appliance) throws ApplianceValidationException {
        validate(appliance, Operation.ANY);
    }

    public void validate(IdentityAppliance appliance, Operation op) throws ApplianceValidationException {

        ValidationContext vctx = new ValidationContext();
        vctx.setOperation(op);
        ctx.set(vctx);

        try {
            arrive(appliance);
            walker.walk(appliance.getIdApplianceDefinition(), this);
            arrive(appliance.getIdApplianceDeployment());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addError("Fatal error", e);
        }

        if (vctx.getErrors().size() > 0) {
            throw new ApplianceValidationException(appliance, vctx.getErrors());
        }
    }

    public IdentityApplianceDefinitionWalker getWalker() {
        return walker;
    }

    public void setWalker(IdentityApplianceDefinitionWalker walker) {
        this.walker = walker;
    }

    public IdentityApplianceDAO getDao() {
        return dao;
    }

    public void setDao(IdentityApplianceDAO dao) {
        this.dao = dao;
    }

    /**
     * @param appliance
     */
    public void arrive(IdentityAppliance appliance) {
        validatePackageName("Appliance namespace", appliance.getNamespace(), appliance.getId());

        if (getOperation().equals(Operation.UPDATE)) {

            if (appliance.getId() < 1) {
                addError("Appliance instance has invalid ID " + appliance.getId() + ", is it new ?" );
                return;
            }

            // Make sure that the appliance exists!
            if (!dao.exists(appliance.getId())) {
                addError("Appliance does not exist with ID : " + appliance.getId());
                return ;
            }

            IdentityAppliance oldAppliance = dao.findById(appliance.getId());
            ctx.get().setOldAppliance(oldAppliance);

            if (!oldAppliance.getState().equals(appliance.getState()))
                addError("Identity Appliance state cannot be modified");

            if (oldAppliance.getIdApplianceDefinition() == null &&
                    appliance.getIdApplianceDefinition() != null)
                addError("Identity Appliance deployment information cannot be added");

            if (oldAppliance.getIdApplianceDefinition() != null &&
                    appliance.getIdApplianceDefinition() == null)
                addError("Identity Appliance deployment information cannot be deleted");

        }
    }

    public void arrive(IdentityApplianceDeployment applianceDep) {
        switch (getOperation()) {
            case UPDATE:
                IdentityApplianceDeployment oldApplianceDep = ctx.get().getOldAppliance().getIdApplianceDeployment();

                if (oldApplianceDep == null) {
                    // TODO : Somewhere this dep.info is  added, check it! addError("Deployment information cannot be added");
                    return;
                }

                // TODO : Validate other lifecycle related infomation ...
                if (oldApplianceDep.getDeployedRevision() != applianceDep.getDeployedRevision())
                    addError("Identity Appliance deployed revision cannot be modified");

                if (oldApplianceDep.getDeploymentTime() != null && !oldApplianceDep.getDeploymentTime().equals(applianceDep.getDeploymentTime()))
                    addError("Identity Appliance deployment time cannot be modified [ " +
                            oldApplianceDep.getDeploymentTime() + " to " +
                            applianceDep.getDeploymentTime() + "]");
                break;
            case ADD:
            case IMPORT:
                if (applianceDep != null)
                    addError("New appliances can't have deployment information ");
                break;
            default:
                break;

        }

    }

    @Override
    public void arrive(IdentityApplianceDefinition node) throws Exception {

        validateName("Appliance name", node.getName(), node);
        validateDisplayName("Appliance display name", node.getDisplayName());
        validateLocation("Appliance", node.getLocation(), node, false);

        if (getOperation() == Operation.ADD ||
            getOperation() == Operation.IMPORT) {

            try {
                IdentityAppliance oldAppliance = dao.findByName(node.getName());
                addError("Appliance name already in use '" +
                        node.getName() + "' by " + oldAppliance.getId());

            } catch (ApplianceNotFoundException e) {
                // OK!
            }

        }


    }

    @Override
    public void arrive(IdentityProvider node) throws Exception {
        validateName("IDP name", node.getName(), node);
        validateDisplayName("IDP display name", node.getDisplayName());

        validateLocation("IDP", node.getLocation(), node, true);

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
            addError("Identity Provider needs at least one Authentication Mechanism");
        }

        if (node.getIdentityLookup() == null)
            addError("Identity Provider needs an Indentity Lookup connection");

        if (node.getConfig() ==null)
            addError("No configuration found for IDP " + node.getName());
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
        validateName("SP name", node.getName(), node);
        validateDisplayName("SP display name", node.getDisplayName());
        validateLocation("SP", node.getLocation(), node, true);
        validateIDPChannels(node);

        if (node.getConfig() ==null)
            addError("No provider configuration found for SP " + node.getName());
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

        if (node.getActivation() == null) {
            addError("Local Serivice Provider requires an activation connection " + node.getName());
        }

        if (node.getAccountLinkagePolicy() == null)
            addError("No account linkage policy for " + node.getName());

        if (node.getIdentityMappingPolicy() == null)
            addError("No identity mapping policy for " + node.getName());
    }

    @Override
    public void arrive(ExternalIdentityProvider node) throws Exception {
        validateName("IDP name", node.getName(), node);
        validateDisplayName("IDP display name", node.getDisplayName());
        validateMetadata("Metadata", node.getMetadata(), node);
    }

    @Override
    public void arrive(ExternalServiceProvider node) throws Exception {
        validateName("SP name", node.getName(), node);
        validateDisplayName("SP display name", node.getDisplayName());
        validateMetadata("Metadata", node.getMetadata(), node);
        validateIDPChannels(node);
    }

    @Override
    public void arrive(SalesforceServiceProvider node) throws Exception {
        validateName("Salesforce provider name", node.getName(), node);
        validateDisplayName("Salesforce provider display name", node.getDisplayName());
        validateIDPChannels(node);
    }

    @Override
    public void arrive(GoogleAppsServiceProvider node) throws Exception {
        validateName("Google Apps provider name", node.getName(), node);
        validateDisplayName("Google Apps provider display name", node.getDisplayName());
        validateDomain("Google Apps domain", node.getDomain());
        validateIDPChannels(node);
    }

    @Override
    public void arrive(SugarCRMServiceProvider node) throws Exception {
        validateName("SugarCRM provider name", node.getName(), node);
        validateDisplayName("SugarCRM provider display name", node.getDisplayName());
        if (StringUtils.isBlank(node.getUrl())) {
            addError("No SugarCRM unique instance URL for " + node.getName());
        }
        validateIDPChannels(node);
    }

    @Override
    public void arrive(FederatedConnection node) throws Exception {
        validateName("Federated Connection name", node.getName(), node);

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
        validateName("Identity Lookup name", node.getName(), node);

        if (node.getProvider() == null)
            addError("Identity Lookup provider cannot be null " + node.getName());

        if (node.getProvider() == null)
            addError("Identity Lookup " + node.getName() + " Provider cannot be null");
        else {
            if (node.getProvider().getIdentityLookup() != node) {
                addError("Provider Identity Lookup is not this Identity Lookup " +
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

        validateName("JOSSO Activation name", node.getName(), node);
        validateDisplayName("JOSSO Activation display name", node.getDisplayName());

        if (node.getPartnerAppId() == null)
            addError("JOSSO Activation partner app. ID cannot be null ");

        validateLocation("JOSSO Activation partner app.", node.getPartnerAppLocation(), node, false);

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

    public void arrive(DelegatedAuthentication node) throws Exception {
        validateName("Delegated Authentication name", node.getName(), node);
        validateDisplayName("Delegated Authentication display name", node.getDisplayName());

        if (node.getIdp() == null)
            addError("Delegated Authentication " + node.getName() + " IDP cannot be null");
        else if (node.getIdp().getDelegatedAuthentication() != node) {
            addError("IDP Delegated Authentication is not this Delegated Authentication " +
                    node.getName() +
                    " [" +node.getIdp().getDelegatedAuthentication() + "]");
        }

        if (node.getAuthnService() == null)
            addError("Delegated Authentication " + node.getName() + " Authentication Service cannot be null");
    }

    @Override
    public void arrive(ExecutionEnvironment node) throws Exception {

        validateName("Execution Environment name" , node.getName(), node);
        validateDisplayName("Execution Environment display name" , node.getDisplayName());

        if (node.getPlatformId() == null)
            addError("Execution Environment platform ID cannot be null");

        if (node.getType() == null)
            addError("Execution Environment type cannot be null");

        if (node.getType() == ExecEnvType.LOCAL && node.getInstallUri() == null)
            addError("Execution Environment install URI cannot be null");

        if (node.getType() == ExecEnvType.REMOTE && node.getLocation() == null)
            addError("Execution Environment location cannot be null");

        if (node instanceof JBossExecutionEnvironment) {
            JBossExecutionEnvironment jbExecEnv = (JBossExecutionEnvironment) node;
            if (jbExecEnv.getInstance() == null)
                addError("JBoss Execution Environment instance name cannot be null");
        } else if (node instanceof WeblogicExecutionEnvironment) {
            WeblogicExecutionEnvironment wlExecEnv = (WeblogicExecutionEnvironment) node;
            if (wlExecEnv.getDomain() == null)
                addError("Weblogic Execution Environment domain name cannot be null");
        }

    }

    @Override
    public void arrive(AuthenticationMechanism node) throws Exception {
        if (node instanceof BasicAuthentication) {

            BasicAuthentication basicAuthn = (BasicAuthentication) node;
            validateName("Basic Authentication name", node.getName(), node);

            if (StringUtils.isBlank(basicAuthn.getHashAlgorithm()))
                addError("Basic Authentication hash algorithm cannot be null or empty");

            if (StringUtils.isBlank(basicAuthn.getHashEncoding()))
                addError("Basic Authentication hash encoding cannot be null or empty");
        }
    }

    @Override
    public void arrive(AuthenticationService node) throws Exception {
        validateName("Authentication Service name", node.getName(), node);
        validateDisplayName("Authentication Service display name", node.getDisplayName());

        if (node.getDelegatedAuthentications() == null || node.getDelegatedAuthentications().size() == 0)
            addError("Authentication Service [" + node.getName() + "] should have at least one connection to an IDP");

        if (node instanceof WikidAuthenticationService) {
            WikidAuthenticationService wikidAuthnService = (WikidAuthenticationService) node;
            if (StringUtils.isBlank(wikidAuthnService.getServerHost()))
                addError("WiKID Authentication Service [" + node.getName() + "] server host cannot be null");
            if (wikidAuthnService.getServerPort() < 0 || wikidAuthnService.getServerPort() > 65535)
                addError("WiKID Authentication Service [" + node.getName() + "] server port must be between 1 and 65535");

            if (StringUtils.isBlank(wikidAuthnService.getServerCode()))
                addError("WiKID Authentication Service [" + node.getName() + "] server code cannot be null");
            else {
                Pattern serverCodePattern = Pattern.compile("\\d{12}");
                Matcher serverCodeMatcher = serverCodePattern.matcher(wikidAuthnService.getServerCode());
                if (!serverCodeMatcher.matches())
                    addError("WiKID Authentication Service [" + node.getName() + "] server code must be a 12 digit string");
            }

            if (wikidAuthnService.getCaStore() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] CA Store cannot be null");
            else if (wikidAuthnService.getCaStore().getPassword() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] CA Store Password cannot be null");
            else if (wikidAuthnService.getCaStore().getStore() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] CA Store Resource cannot be null");
            
            if (wikidAuthnService.getWcStore() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] Client Store cannot be null");
            else if (wikidAuthnService.getWcStore().getPassword() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] Client Store Password cannot be null");
            else if (wikidAuthnService.getWcStore().getStore() == null)
                addError("WiKID Authentication Service [" + node.getName() + "] Client Store Resource cannot be null");
        } else if (node instanceof DirectoryAuthenticationService) {
            DirectoryAuthenticationService directoryAuthnService = (DirectoryAuthenticationService) node;

            if (StringUtils.isBlank(directoryAuthnService.getInitialContextFactory()))
                addError("Directory Authentication Service [" + node.getName() + "] Initial Context Factory cannot be null or empty");
            if (StringUtils.isBlank(directoryAuthnService.getProviderUrl()))
                addError("Directory Authentication Service [" + node.getName() + "] Provider URL cannot be null or empty");
            /*
            if (StringUtils.isBlank(directoryAuthnService.getSecurityPrincipal()))
                addError("Directory Authentication Service [" + node.getName() + "] Security Principal cannot be null or empty");
            if (StringUtils.isBlank(directoryAuthnService.getSecurityCredential()))
                addError("Directory Authentication Service [" + node.getName() + "] Security Credential cannot be null or empty");
            */

            if (StringUtils.isBlank(directoryAuthnService.getSecurityAuthentication()))
                addError("Directory Authentication Service [" + node.getName() + "] Security Authentication cannot be null or empty");

        }
    }

    @Override
    public void arrive(DbIdentitySource node) throws Exception {
        validateName("DB Identity Source name" , node.getName(), node);
        validateDisplayName("DB Identity Source display name" , node.getDisplayName());

        // TODO !
    }

    @Override
    public void arrive(EmbeddedIdentitySource node) throws Exception {
        validateName("Embedded Identity Source name" , node.getName(), node);
        validateDisplayName("Ebmedded Identity Source display name" , node.getDisplayName());
        // TODO !
    }

    @Override
    public void arrive(LdapIdentitySource node) throws Exception {
        validateName("LDAP Identity Source name" , node.getName(), node);
        validateDisplayName("LDAP Identity Source display name" , node.getDisplayName());

        // TODO !

        if (StringUtils.isBlank(node.getInitialContextFactory()))
            addError("Directory Authentication Service [" + node.getName() + "] Initial Context Factory cannot be null or empty");
        if (StringUtils.isBlank(node.getProviderUrl()))
            addError("Directory Authentication Service [" + node.getName() + "] Provider URL cannot be null or empty");
        if (StringUtils.isBlank(node.getSecurityPrincipal()))
            addError("Directory Authentication Service [" + node.getName() + "] Security Principal cannot be null or empty");
        if (StringUtils.isBlank(node.getSecurityCredential()))
            addError("Directory Authentication Service [" + node.getName() + "] Security Credential cannot be null or empty");
        if (StringUtils.isBlank(node.getSecurityAuthentication()))
            addError("Directory Authentication Service [" + node.getName() + "] Security Authentication cannot be null or empty");
        if (StringUtils.isBlank(node.getLdapSearchScope()))
            addError("Directory Authentication Service [" + node.getName() + "] LDAP search scope cannot be null or empty");
        if (StringUtils.isBlank(node.getUsersCtxDN()))
            addError("Directory Authentication Service [" + node.getName() + "] User DN cannot be null or empty");
        if (StringUtils.isBlank(node.getPrincipalUidAttributeID()))
            addError("Directory Authentication Service [" + node.getName() + "] Principal UID attribute ID cannot be null or empty");
        if (StringUtils.isBlank(node.getRoleMatchingMode()))
            addError("Directory Authentication Service [" + node.getName() + "] Role Matching Mode cannot be null or empty");
        if (StringUtils.isBlank(node.getUidAttributeID()))
            addError("Directory Authentication Service [" + node.getName() + "] UID Attribute ID cannot be null or empty");
        if (StringUtils.isBlank(node.getRolesCtxDN()))
            addError("Directory Authentication Service [" + node.getName() + "] Roles DN cannot be null or empty");
        if (StringUtils.isBlank(node.getRoleAttributeID()))
            addError("Directory Authentication Service [" + node.getName() + "] Role Attribute ID cannot be null or empty");
        if (StringUtils.isBlank(node.getCredentialQueryString()))
            addError("Directory Authentication Service [" + node.getName() + "] Credential Query cannot be null or empty");
        if (StringUtils.isBlank(node.getUserPropertiesQueryString()))
            addError("Directory Authentication Service [" + node.getName() + "] User Properties Query cannot be null or empty");
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
        validateName("Service Provider channel", node.getName(), node);
        if (node.isOverrideProviderSetup())
            validateLocation("Serivce Provider channel ", node.getLocation(), node, true);
    }

    @Override
    public void arrive(IdentityProviderChannel node) throws Exception {
        validateName("Identity Provider channel", node.getName(), node);
        if (node.isOverrideProviderSetup())
            validateLocation("Identity Provider channel ", node.getLocation(), node, true);

        // validate policies only for ServiceProvider (not for ExternalServiceProvider)
        if ((node.getConnectionA() != null && node.getConnectionA().getRoleA() instanceof ServiceProvider) ||
                (node.getConnectionB() != null && node.getConnectionB().getRoleB() instanceof ServiceProvider)) {
            if (node.getAccountLinkagePolicy() == null)
                addError("No account linkage policy for " + node.getName());

            if (node.getIdentityMappingPolicy() == null)
                addError("No identity mapping policy for " + node.getName());
        }
    }

    public void arrive(Keystore node) throws Exception {
        if (node == null)
            return;

        if (StringUtils.isBlank(node.getName()))
            addError("Keystore name cannot be null or empty");

        validateDisplayName("Keystore display name" , node.getDisplayName());

        if (StringUtils.isBlank(node.getType()))
            addError("Keystore type cannot be null or empty");

        if (StringUtils.isBlank(node.getPassword()))
            addError("Keystore password cannot be null or empty");

        if (!node.isKeystorePassOnly()) {
            if (StringUtils.isBlank(node.getPrivateKeyName()))
                addError("Keystore private key name cannot be null or empty");

            if (StringUtils.isBlank(node.getPrivateKeyPassword()))
                addError("Keystore private key password cannot be null or empty");

            if (StringUtils.isBlank(node.getCertificateAlias()))
                addError("Keystore certificate alias cannot be null or empty");
        }

        Resource ks = node.getStore();

        if (ks == null) {
            addError("Keystore file cannot be null");
        } else {
            if (StringUtils.isBlank(ks.getName()))
                addError("Keystore file name cannot be null or empty");

            validateDisplayName("Keystore file display name", ks.getDisplayName());

            if (StringUtils.isBlank(ks.getUri()))
                addError("Keystore file uri cannot be null or empty");

            if (ks.getValue() == null) {
                addError("Keystore file value cannot be null");
            } else if (node.getType() != null && node.getPassword() != null) {
                try {
                    KeyStore keyStore = KeyStore.getInstance("PKCS#12".equals(node.getType()) ? "PKCS12" : "JKS");
                    keyStore.load(new ByteArrayInputStream(ks.getValue()), node.getPassword().toCharArray());
                    if (node.getCertificateAlias() != null) {
                        Certificate certificate = keyStore.getCertificate(node.getCertificateAlias());
                        if (certificate == null)
                            addError("No certificate associated with alias '" + node.getCertificateAlias() + "'");
                    }
                } catch (KeyStoreException e) {
                    addError("Keystore type is not available");
                } catch (NoSuchAlgorithmException e) {
                    addError("Algorithm used to check the integrity of the keystore cannot be found");
                } catch (CertificateException e) {
                    addError("Certificates in the keystore cannot be loaded");
                } catch (EOFException e) {
                    addError("Keystore data is corrupted");
                } catch (IOException e) {
                    addError("Keystore was tampered with, or password was incorrect");
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // UTILS
    // ---------------------------------------------------------------------

    protected void validateName(String propertyName, String name, Object o) {

        //
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

        ValidationContext vctx = ctx.get();
        if (vctx.isNameUsed(name, o))
            addError(propertyName + " already in use in other component : " + name);

        vctx.registerName(name, o);

    }

    protected void validateDisplayName(String propertyName, String name) {

        /* TODO ! Disabled for now ... 
        if (name == null || name.length() == 0) {
            addError(propertyName + " cannot be null or empty");
            return;
        } */

    }


    protected void validatePackageName(String propertyName, String pkgName, long applianceId) {
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

        if (dao.namespaceExists(applianceId, pkgName)) {
            addError(propertyName + " is already used in some other appliance");
        }
    }

    protected void validateLocation(String propertyName, Location location, Object obj, boolean validateUri) {
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

        String locationStr = location.getLocationAsString();
        if (ctx.get().isLocationUsed(locationStr, obj)) {
            addError(propertyName + " location is already in use " + locationStr);
        }

        ctx.get().registerLocation(locationStr, obj);

    }

    protected void validateMetadata(String propertyName, Resource metadata, Object obj) {
        if (metadata == null) {
            addError(propertyName + " cannot be null");
            return;
        }

        if (metadata.getName() == null)
            addError(propertyName + " name cannot be null");

        if (metadata.getUri() == null)
            addError(propertyName + " uri cannot be null");

        if (metadata.getValue() == null)
            addError(propertyName + " value cannot be null");
        else {
            try {
                // load metadata definition
                MetadataDefinition md = MetadataUtil.loadMetadataDefinition(metadata.getValue());
                // find entityID
                MetadataUtil.findEntityId(md);
                // find SSODescriptor
                String descriptorName = null;
                if (obj instanceof ExternalIdentityProvider) {
                    descriptorName = "IDPSSODescriptor";
                } else if (obj instanceof ExternalServiceProvider) {
                    descriptorName = "SPSSODescriptor";
                }
                MetadataUtil.findSSODescriptor(md, descriptorName);
            } catch (Exception e) {
                addError(propertyName + " is not valid");
            }
        }
    }

    protected void validateIDPChannels(FederatedProvider node) {
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
            addError("Too many preferred IDP Channels defined for SP " + node.getName() + ", found " + preferred);
    }

    protected void validateDomain(String propertyName, String domain) {
        if (domain == null || domain.length() == 0) {
            addError(propertyName + " cannot be null or empty");
            return;
        }

        for (int i = 0 ; i < domain.length() ; i ++) {
            char c = domain.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                if (c != '.') {
                    addError(propertyName + " must contain only letters, numbers and '.' characters. value : " + domain);
                    return ;
                }
            }
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

    protected Operation getOperation() {
        return ctx.get().getOperation();
    }

    protected class ValidationContext {

        private IdentityAppliance oldAppliance;

        private Operation operation;

        private List<ValidationError> errors = new ArrayList<ValidationError>();

        private Map<String, Set<Object>> usedNames = new HashMap<String, Set<Object>>();

        private Map<String, Set<Object>> usedLocations = new HashMap<String, Set<Object>>();

        boolean isLocationUsed(String location, Object o) {
            Set objs = usedLocations.get(location);
            if (objs == null) {
                objs = new HashSet<Object>();
                usedLocations.put(location, objs);
            }

            return objs.size() > 0 && !objs.contains(o);

        }

        void registerLocation(String location, Object o) {
            Set objs = usedLocations.get(location);
            if (objs == null) {
                objs = new HashSet<Object>();
                usedLocations.put(location, objs);
            }
            objs.add(o);
        }

        boolean isNameUsed(String name, Object o) {
            Set objs = usedNames.get(name);
            if (objs == null) {
                objs = new HashSet<Object>();
                usedNames.put(name, objs);
            }

            return objs.size() > 0 && !objs.contains(o);

        }

        void registerName(String name, Object o) {
            Set objs = usedNames.get(name);
            if (objs == null) {
                objs = new HashSet<Object>();
                usedNames.put(name, objs);
            }
            objs.add(o);
        }

        public List<ValidationError> getErrors() {
            return errors;
        }

        public void setOperation(Operation op) {
            this.operation = op;
        }

        public Operation getOperation() {
            return operation;
        }

        public void setOldAppliance(IdentityAppliance oldAppliance) {
            this.oldAppliance = oldAppliance;
        }

        public IdentityAppliance getOldAppliance() {
            return oldAppliance;
        }
    }


}
