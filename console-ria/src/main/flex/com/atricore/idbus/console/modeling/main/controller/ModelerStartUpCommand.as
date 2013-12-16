/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.modeling.main.ModelerMediator;

import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class ModelerStartUpCommand extends AppSectionStartUpCommand {

    // Mediators
    private var _browserMediator:IIocMediator;
    private var _diagramMediator:IIocMediator;
    private var _paletteMediator:IIocMediator;
    private var _propertySheetMediator:IIocMediator;

    private var _identityApplianceWizardMediator:IIocMediator;
    private var _identityProviderCreateMediator:IIocMediator;
    private var _internalSaml2ServiceProviderCreateMediator:IIocMediator;
    private var _externalSaml2IdentityProviderCreateMediator:IIocMediator;
    private var _externalSaml2ServiceProviderCreateMediator:IIocMediator;
    private var _externalOpenIDIdentityProviderCreateMediator:IIocMediator;
    private var _oauth2IdentityProviderCreateMediator:IIocMediator;
    private var _oauth2ServiceProviderCreateMediator:IIocMediator;
    private var _salesforceCreateMediator:IIocMediator;
    private var _googleAppsCreateMediator:IIocMediator;
    private var _sugarCRMCreateMediator:IIocMediator;
    private var _embeddedIdentityVaultCreateMediator:IIocMediator;
    private var _dbIdentityVaultCreateMediator:IIocMediator;
    private var _dbIdentitySourceCreateMediator:IIocMediator;
    private var _ldapIdentitySourceCreateMediator:IIocMediator;
    private var _xmlIdentitySourceCreateMediator:IIocMediator;
    private var _josso1ResourceCreateMediator:IIocMediator;
    private var _josso2ResourceCreateMediator:IIocMediator;
    private var _jbossExecutionEnvironmentCreateMediator:IIocMediator;
    private var _weblogicExecutionEnvironmentCreateMediator:IIocMediator;
    private var _tomcatExecutionEnvironmentCreateMediator:IIocMediator;
    private var _jbossPortalResourceCreateMediator:IIocMediator;
    private var _liferayPortalResourceCreateMediator:IIocMediator;
    private var _jbosseppResourceCreateMediator:IIocMediator;
    private var _selfServicesResourceCreateMediator:IIocMediator;
    private var _dominoResourceCreateMediator:IIocMediator;
    private var _blackboardResourceCreateMediator:IIocMediator;
    private var _wasceExecutionEnvironmentCreateMediator:IIocMediator;
    private var _apacheExecutionEnvironmentCreateMediator:IIocMediator;
    private var _alfrescoResourceCreateMediator:IIocMediator;
    private var _javaEEExecutionEnvironmentCreateMediator:IIocMediator;
    private var _phpExecutionEnvironmentCreateMediator:IIocMediator;
    private var _phpBBResourceCreateMediator:IIocMediator;
    private var _windowsIISExecutionEnvironmentCreateMediator:IIocMediator;
    private var _webserverExecutionEnvironmentCreateMediator:IIocMediator;
    private var _sharepointResourceCreateMediator:IIocMediator;
    private var _coldfusionResourceCreateMediator:IIocMediator;
    private var _microStrategyResourceCreateMediator:IIocMediator;
    private var _simpleSSOWizardViewMediator:IIocMediator;
    private var _activationCreateMediator:IIocMediator;
    private var _federatedConnectionCreateMediator:IIocMediator;
    private var _exportIdentityApplianceMediator:IIocMediator;
    private var _exportProviderCertificateMediator:IIocMediator;
    private var _exportMetadataMediator:IIocMediator;
    private var _exportAgentConfigMediator:IIocMediator;
    private var _activationMediator:IIocMediator;
    private var _wikidCreateMediator:IIocMediator;
    private var _dominoCreateMediator:IIocMediator;
    private var _clientCertCreateMediator:IIocMediator;
    private var _directoryServiceCreateMediator:IIocMediator;
    private var _windowsIntegratedAuthnCreateMediator:IIocMediator;
    private var _jbosseppAuthenticationCreateMediator:IIocMediator;

    // Commands
    private var _createSimpleSSOIdentityApplianceCommand:IIocCommand;
    private var _identityApplianceListLoadCommand:IIocCommand;
    private var _identityApplianceCreateCommand:IIocCommand;
    private var _identityApplianceImportCommand:IIocCommand;
    private var _identityApplianceRemoveCommand:IIocCommand;
    private var _identityApplianceUndisposeCommand:IIocCommand;
    private var _identityProviderRemoveCommand:IIocCommand;
    private var _internalSaml2ServiceProviderRemoveCommand:IIocCommand;
    private var _externalSaml2IdentityProviderRemoveCommand:IIocCommand;
    private var _externalSaml2ServiceProviderRemoveCommand:IIocCommand;
    private var _externalOpenIDIdentityProviderRemoveCommand:IIocCommand;
    private var _oauth2IdentityProviderRemoveCommand:IIocCommand;
    private var _oauth2ServiceProviderRemoveCommand:IIocCommand;
    private var _josso1ResourceRemoveCommand:IIocCommand;
    private var _josso2ResourceRemoveCommand:IIocCommand;
    private var _jbosseppResourceRemoveCommand:IIocCommand;
    private var _liferayResourceRemoveCommand:IIocCommand;
    private var _selfServicesResourceRemoveCommand:IIocCommand;
    private var _dominoResourceRemoveCommand:IIocCommand;
    private var _blackboardResourceRemoveCommand:IIocCommand;
    private var _lookupIdentityApplianceByIdCommand:IIocCommand;
    private var _identityApplianceUpdateCommand:IIocCommand;
    private var _identityVaultRemoveCommand:IIocCommand;
    private var _activationRemoveCommand:IIocCommand;
    private var _serviceConnectionRemoveCommand:IIocCommand;
    private var _identityLookupRemoveCommand:IIocCommand;
    private var _federatedConnectionRemoveCommand:IIocCommand;
    private var _delegatedAuthenticationRemoveCommand:IIocCommand;
    private var _executionEnvironmentRemoveCommand:IIocCommand;
    private var _authenticationServiceRemoveCommand:IIocCommand;
    private var _activateExecEnvironmentCommand:IIocCommand;
    private var _createServiceConnectionCommand:IIocCommand;
    private var _createActivationCommand:IIocCommand;
    private var _createIdentityLookupCommand:IIocCommand;
    private var _createDelegatedAuthenticationCommand:IIocCommand;
    private var _folderExistsCommand:IIocCommand;
    private var _foldersExistsCommand:IIocCommand;
    private var _jdbcDriversListCommand:IIocCommand;
    private var _embeddedIdentityVaultsListCommand:IIocCommand;
    private var _getMetadataInfoCommand:IIocCommand;
    private var _getCertificateInfoCommand:IIocCommand;
    private var _exportIdentityApplianceCommand:IIocCommand;
    private var _exportProviderCertificateCommand:IIocCommand;
    private var _exportMetadataCommand:IIocCommand;
    private var _exportAgentConfigCommand:IIocCommand;
    private var _accountLinkagePolicyListCommand:IIocCommand;
    private var _userDashboardBrandingsListCommand:IIocCommand;
    private var _idpSelectorsListCommand:IIocCommand;
    private var _identityFlowComponentsListCommand:IIocCommand;
    private var _identityMappingPoliciesListCommand:IIocCommand;
    private var _subjectNameIDPolicyListCommand:IIocCommand;
    private var _impersonateUserPoliciesListCommand:IIocCommand;
    
    public function ModelerStartUpCommand() {
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        super.setupMediators(ctx);

        iocFacade.registerMediatorByConfigName(browserMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(diagramMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(paletteMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(propertySheetMediator.getConfigName());

        iocFacade.registerMediatorByConfigName(simpleSSOWizardViewMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityApplianceWizardMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(internalSaml2ServiceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalSaml2IdentityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalSaml2ServiceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalOpenIDIdentityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(oauth2IdentityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(oauth2ServiceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(salesforceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(googleAppsCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(sugarCRMCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(embeddedIdentityVaultCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dbIdentityVaultCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dbIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(ldapIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(xmlIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(josso1ResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(josso2ResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(weblogicExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(tomcatExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossPortalResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(liferayPortalResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbosseppResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(wasceExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(apacheExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(alfrescoResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(javaEEExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(phpExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(phpBBResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(windowsIISExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(webserverExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(sharepointResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(coldfusionResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(microStrategyResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(activationCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(federatedConnectionCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportIdentityApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportProviderCertificateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportMetadataMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportAgentConfigMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(activationMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(wikidCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dominoCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(blackboardResourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(clientCertCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(directoryServiceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbosseppAuthenticationCreateMediator.getConfigName());

    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, createSimpleSSOIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, identityApplianceCreateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IMPORT_IDENTITY_APPLIANCE, identityApplianceImportCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_REMOVE, identityApplianceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_PROVIDER_REMOVE, identityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.INTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, internalSaml2ServiceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_SAML2_IDENTITY_PROVIDER_REMOVE, externalSaml2IdentityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, externalSaml2ServiceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.OPENID_IDENTITY_PROVIDER_REMOVE, externalOpenIDIdentityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.OAUTH2_IDENTITY_PROVIDER_REMOVE, oauth2IdentityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.OAUTH2_SERVICE_PROVIDER_REMOVE, oauth2ServiceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.JOSSO1_RESOURCE_REMOVE, josso1ResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.JOSSO2_RESOURCE_REMOVE, josso2ResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.JBOSSEPP_RESOURCE_REMOVE, jbosseppResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIFERAY_RESOURCE_REMOVE, liferayResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SELFSERVICES_RESOURCE_REMOVE, selfServicesResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DOMINO_RESOURCE_REMOVE, dominoResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.BLACKBOARD_RESOURCE_REMOVE, blackboardResourceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_SOURCE_REMOVE, identityVaultRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATION_REMOVE, activationRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SERVICE_CONNECTION_REMOVE, serviceConnectionRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_LOOKUP_REMOVE, identityLookupRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.FEDERATED_CONNECTION_REMOVE, federatedConnectionRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DELEGATED_AUTHENTICATION_REMOVE, delegatedAuthenticationRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXECUTION_ENVIRONMENT_REMOVE, executionEnvironmentRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, authenticationServiceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, lookupIdentityApplianceByIdCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD, identityApplianceListLoadCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE, identityApplianceUpdateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvironmentCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_SERVICE_CONNECTION, createServiceConnectionCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_ACTIVATION, createActivationCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_LOOKUP, createIdentityLookupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_DELEGATED_AUTHENTICATION, createDelegatedAuthenticationCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, folderExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, foldersExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_JDBC_DRIVERS, jdbcDriversListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_EMBEDDED_IDVAUTLS, embeddedIdentityVaultsListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_METADATA_INFO, getMetadataInfoCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_CERTIFICATE_INFO, getCertificateInfoCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_EXPORT, exportIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.PROVIDER_CERTIFICATE_EXPORT, exportProviderCertificateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.METADATA_EXPORT, exportMetadataCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.AGENT_CONFIG_EXPORT, exportAgentConfigCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_ACCOUNT_LINKAGE_POLICIES, accountLinkagePolicyListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_USER_DASHBOARD_BRANDINGS, userDashboardBrandingsListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_IDP_SELECTORS, idpSelectorsListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_IDENTITY_FLOW_COMPONENTS, identityFlowComponentsListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_NAMEID_POLICIES, subjectNameIDPolicyListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_IDENTITY_MAPPING_POLICIES, identityMappingPoliciesListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_IMPERSONATE_USER_POLICIES, impersonateUserPoliciesListCommand.getConfigName());
    }

    public function get browserMediator():IIocMediator {
        return _browserMediator;
    }

    public function set browserMediator(value:IIocMediator):void {
        _browserMediator = value;
    }

    public function get diagramMediator():IIocMediator {
        return _diagramMediator;
    }

    public function set diagramMediator(value:IIocMediator):void {
        _diagramMediator = value;
    }

    public function get paletteMediator():IIocMediator {
        return _paletteMediator;
    }

    public function set paletteMediator(value:IIocMediator):void {
        _paletteMediator = value;
    }

    public function get propertySheetMediator():IIocMediator {
        return _propertySheetMediator;
    }

    public function set propertySheetMediator(value:IIocMediator):void {
        _propertySheetMediator = value;
    }

    public function get identityApplianceWizardMediator():IIocMediator {
        return _identityApplianceWizardMediator;
    }

    public function set identityApplianceWizardMediator(value:IIocMediator):void {
        _identityApplianceWizardMediator = value;
    }

    public function get identityProviderCreateMediator():IIocMediator {
        return _identityProviderCreateMediator;
    }

    public function set identityProviderCreateMediator(value:IIocMediator):void {
        _identityProviderCreateMediator = value;
    }

    public function get internalSaml2ServiceProviderCreateMediator():IIocMediator {
        return _internalSaml2ServiceProviderCreateMediator;
    }

    public function set internalSaml2ServiceProviderCreateMediator(value:IIocMediator):void {
        _internalSaml2ServiceProviderCreateMediator = value;
    }

    public function get externalSaml2IdentityProviderCreateMediator():IIocMediator {
        return _externalSaml2IdentityProviderCreateMediator;
    }

    public function set externalSaml2IdentityProviderCreateMediator(value:IIocMediator):void {
        _externalSaml2IdentityProviderCreateMediator = value;
    }

    public function get externalSaml2ServiceProviderCreateMediator():IIocMediator {
        return _externalSaml2ServiceProviderCreateMediator;
    }

    public function set externalSaml2ServiceProviderCreateMediator(value:IIocMediator):void {
        _externalSaml2ServiceProviderCreateMediator = value;
    }

    public function get externalOpenIDIdentityProviderCreateMediator():IIocMediator {
        return _externalOpenIDIdentityProviderCreateMediator;
    }

    public function set externalOpenIDIdentityProviderCreateMediator(value:IIocMediator):void {
        _externalOpenIDIdentityProviderCreateMediator = value;
    }

    public function get oauth2IdentityProviderCreateMediator():IIocMediator {
        return _oauth2IdentityProviderCreateMediator;
    }

    public function set oauth2IdentityProviderCreateMediator(value:IIocMediator):void {
        _oauth2IdentityProviderCreateMediator = value;
    }

    public function get oauth2ServiceProviderCreateMediator():IIocMediator {
        return _oauth2ServiceProviderCreateMediator;
    }

    public function set oauth2ServiceProviderCreateMediator(value:IIocMediator):void {
        _oauth2ServiceProviderCreateMediator = value;
    }

    public function get salesforceCreateMediator():IIocMediator {
        return _salesforceCreateMediator;
    }

    public function set salesforceCreateMediator(value:IIocMediator):void {
        _salesforceCreateMediator = value;
    }

    public function get googleAppsCreateMediator():IIocMediator {
        return _googleAppsCreateMediator;
    }

    public function set googleAppsCreateMediator(value:IIocMediator):void {
        _googleAppsCreateMediator = value;
    }

    public function get sugarCRMCreateMediator():IIocMediator {
        return _sugarCRMCreateMediator;
    }

    public function set sugarCRMCreateMediator(value:IIocMediator):void {
        _sugarCRMCreateMediator = value;
    }

    public function get embeddedIdentityVaultCreateMediator():IIocMediator {
        return _embeddedIdentityVaultCreateMediator;
    }

    public function set embeddedIdentityVaultCreateMediator(value:IIocMediator):void {
        _embeddedIdentityVaultCreateMediator = value;
    }

    public function get dbIdentityVaultCreateMediator():IIocMediator {
        return _dbIdentityVaultCreateMediator;
    }

    public function set dbIdentityVaultCreateMediator(value:IIocMediator):void {
        _dbIdentityVaultCreateMediator = value;
    }

    public function get dbIdentitySourceCreateMediator():IIocMediator {
        return _dbIdentitySourceCreateMediator;
    }

    public function set dbIdentitySourceCreateMediator(value:IIocMediator):void {
        _dbIdentitySourceCreateMediator = value;
    }

    public function get ldapIdentitySourceCreateMediator():IIocMediator {
        return _ldapIdentitySourceCreateMediator;
    }

    public function set ldapIdentitySourceCreateMediator(value:IIocMediator):void {
        _ldapIdentitySourceCreateMediator = value;
    }

    public function get xmlIdentitySourceCreateMediator():IIocMediator {
        return _xmlIdentitySourceCreateMediator;
    }

    public function set xmlIdentitySourceCreateMediator(value:IIocMediator):void {
        _xmlIdentitySourceCreateMediator = value;
    }

    public function get josso1ResourceCreateMediator():IIocMediator {
        return _josso1ResourceCreateMediator;
    }

    public function set josso1ResourceCreateMediator(value:IIocMediator):void {
        _josso1ResourceCreateMediator = value;
    }

    public function get josso2ResourceCreateMediator():IIocMediator {
        return _josso2ResourceCreateMediator;
    }

    public function set josso2ResourceCreateMediator(value:IIocMediator):void {
        _josso2ResourceCreateMediator = value;
    }

    public function get jbossExecutionEnvironmentCreateMediator():IIocMediator {
        return _jbossExecutionEnvironmentCreateMediator;
    }

    public function set jbossExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _jbossExecutionEnvironmentCreateMediator = value;
    }

    public function get selfServicesResourceCreateMediator():IIocMediator {
        return _selfServicesResourceCreateMediator;
    }

    public function set selfServicesResourceCreateMediator(value:IIocMediator):void {
        _selfServicesResourceCreateMediator = value;
    }

    public function get dominoResourceCreateMediator():IIocMediator {
        return _dominoResourceCreateMediator;
    }

    public function set dominoResourceCreateMediator(value:IIocMediator):void {
        _dominoResourceCreateMediator = value;
    }

    public function get blackboardResourceCreateMediator():IIocMediator {
        return _blackboardResourceCreateMediator;
    }

    public function set blackboardResourceCreateMediator(value:IIocMediator):void {
        _blackboardResourceCreateMediator = value;
    }

    public function get weblogicExecutionEnvironmentCreateMediator():IIocMediator {
        return _weblogicExecutionEnvironmentCreateMediator;
    }

    public function set weblogicExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _weblogicExecutionEnvironmentCreateMediator = value;
    }

    public function get tomcatExecutionEnvironmentCreateMediator():IIocMediator {
        return _tomcatExecutionEnvironmentCreateMediator;
    }

    public function set tomcatExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _tomcatExecutionEnvironmentCreateMediator = value;
    }

    public function get jbossPortalResourceCreateMediator():IIocMediator {
        return _jbossPortalResourceCreateMediator;
    }

    public function set jbossPortalResourceCreateMediator(value:IIocMediator):void {
        _jbossPortalResourceCreateMediator = value;
    }

    public function get liferayPortalResourceCreateMediator():IIocMediator {
        return _liferayPortalResourceCreateMediator;
    }

    public function set liferayPortalResourceCreateMediator(value:IIocMediator):void {
        _liferayPortalResourceCreateMediator = value;
    }

    public function get jbosseppResourceCreateMediator():IIocMediator {
        return _jbosseppResourceCreateMediator;
    }

    public function set jbosseppResourceCreateMediator(value:IIocMediator):void {
        _jbosseppResourceCreateMediator = value;
    }

    public function get wasceExecutionEnvironmentCreateMediator():IIocMediator {
        return _wasceExecutionEnvironmentCreateMediator;
    }

    public function set wasceExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _wasceExecutionEnvironmentCreateMediator = value;
    }

    public function get apacheExecutionEnvironmentCreateMediator():IIocMediator {
        return _apacheExecutionEnvironmentCreateMediator;
    }

    public function set apacheExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _apacheExecutionEnvironmentCreateMediator = value;
    }

    public function get alfrescoResourceCreateMediator():IIocMediator {
        return _alfrescoResourceCreateMediator;
    }

    public function set alfrescoResourceCreateMediator(value:IIocMediator):void {
        _alfrescoResourceCreateMediator = value;
    }

    public function get javaEEExecutionEnvironmentCreateMediator():IIocMediator {
        return _javaEEExecutionEnvironmentCreateMediator;
    }

    public function set javaEEExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _javaEEExecutionEnvironmentCreateMediator = value;
    }

    public function get phpExecutionEnvironmentCreateMediator():IIocMediator {
        return _phpExecutionEnvironmentCreateMediator;
    }

    public function set phpExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _phpExecutionEnvironmentCreateMediator = value;
    }

    public function get phpBBResourceCreateMediator():IIocMediator {
        return _phpBBResourceCreateMediator;
    }

    public function set phpBBResourceCreateMediator(value:IIocMediator):void {
        _phpBBResourceCreateMediator = value;
    }

    public function get windowsIISExecutionEnvironmentCreateMediator():IIocMediator {
        return _windowsIISExecutionEnvironmentCreateMediator;
    }

    public function set windowsIISExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _windowsIISExecutionEnvironmentCreateMediator = value;
    }

    public function get webserverExecutionEnvironmentCreateMediator():IIocMediator {
        return _webserverExecutionEnvironmentCreateMediator;
    }

    public function set webserverExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _webserverExecutionEnvironmentCreateMediator = value;
    }


    public function get sharepointResourceCreateMediator():IIocMediator {
        return _sharepointResourceCreateMediator;
    }

    public function set sharepointResourceCreateMediator(value:IIocMediator):void {
        _sharepointResourceCreateMediator = value;
    }

    public function get coldfusionResourceCreateMediator():IIocMediator {
        return _coldfusionResourceCreateMediator;
    }

    public function set coldfusionResourceCreateMediator(value:IIocMediator):void {
        _coldfusionResourceCreateMediator = value;
    }

    public function get microStrategyResourceCreateMediator():IIocMediator {
        return _microStrategyResourceCreateMediator;
    }

    public function set microStrategyResourceCreateMediator(value:IIocMediator):void {
        _microStrategyResourceCreateMediator = value;
    }

    public function get simpleSSOWizardViewMediator():IIocMediator {
        return _simpleSSOWizardViewMediator;
    }

    public function set simpleSSOWizardViewMediator(value:IIocMediator):void {
        _simpleSSOWizardViewMediator = value;
    }

    public function get activationCreateMediator():IIocMediator {
        return _activationCreateMediator;
    }

    public function set activationCreateMediator(value:IIocMediator):void {
        _activationCreateMediator = value;
    }

    public function get federatedConnectionCreateMediator():IIocMediator {
        return _federatedConnectionCreateMediator;
    }

    public function set federatedConnectionCreateMediator(value:IIocMediator):void {
        _federatedConnectionCreateMediator = value;
    }

    public function get exportIdentityApplianceMediator():IIocMediator {
        return _exportIdentityApplianceMediator;
    }

    public function set exportIdentityApplianceMediator(value:IIocMediator):void {
        _exportIdentityApplianceMediator = value;
    }

    public function get exportProviderCertificateMediator():IIocMediator {
        return _exportProviderCertificateMediator;
    }

    public function set exportProviderCertificateMediator(value:IIocMediator):void {
        _exportProviderCertificateMediator = value;
    }

    public function get exportMetadataMediator():IIocMediator {
        return _exportMetadataMediator;
    }

    public function set exportMetadataMediator(value:IIocMediator):void {
        _exportMetadataMediator = value;
    }

    public function get exportAgentConfigMediator():IIocMediator {
        return _exportAgentConfigMediator;
    }

    public function set exportAgentConfigMediator(value:IIocMediator):void {
        _exportAgentConfigMediator = value;
    }

    public function get activationMediator():IIocMediator {
        return _activationMediator;
    }

    public function set activationMediator(value:IIocMediator):void {
        _activationMediator = value;
    }

    public function get wikidCreateMediator():IIocMediator {
        return _wikidCreateMediator;
    }

    public function set wikidCreateMediator(value:IIocMediator):void {
        _wikidCreateMediator = value;
    }

    public function get dominoCreateMediator():IIocMediator {
        return _dominoCreateMediator;
    }

    public function set dominoCreateMediator(value:IIocMediator):void {
        _dominoCreateMediator = value;
    }

    public function get clientCertCreateMediator():IIocMediator {
        return _clientCertCreateMediator;
    }

    public function set clientCertCreateMediator(value:IIocMediator):void {
        _clientCertCreateMediator = value;
    }

    public function get directoryServiceCreateMediator():IIocMediator {
        return _directoryServiceCreateMediator;
    }

    public function set directoryServiceCreateMediator(value:IIocMediator):void {
        _directoryServiceCreateMediator = value;
    }

    public function get windowsIntegratedAutCreateMediator():IIocMediator {
        return _windowsIntegratedAuthnCreateMediator;
    }

    public function set windowsIntegratedAuthnCreateMediator(value:IIocMediator):void {
        _windowsIntegratedAuthnCreateMediator = value;
    }

    public function get jbosseppAuthenticationCreateMediator():IIocMediator {
        return _jbosseppAuthenticationCreateMediator;
    }

    public function set jbosseppAuthenticationCreateMediator(value:IIocMediator):void {
        _jbosseppAuthenticationCreateMediator = value;
    }


    public function get createSimpleSSOIdentityApplianceCommand():IIocCommand {
        return _createSimpleSSOIdentityApplianceCommand;
    }

    public function set createSimpleSSOIdentityApplianceCommand(value:IIocCommand):void {
        _createSimpleSSOIdentityApplianceCommand = value;
    }

    public function get identityApplianceListLoadCommand():IIocCommand {
        return _identityApplianceListLoadCommand;
    }

    public function set identityApplianceListLoadCommand(value:IIocCommand):void {
        _identityApplianceListLoadCommand = value;
    }

    public function get identityApplianceCreateCommand():IIocCommand {
        return _identityApplianceCreateCommand;
    }

    public function set identityApplianceCreateCommand(value:IIocCommand):void {
        _identityApplianceCreateCommand = value;
    }

    public function get identityApplianceImportCommand():IIocCommand {
        return _identityApplianceImportCommand;
    }

    public function set identityApplianceImportCommand(value:IIocCommand):void {
        _identityApplianceImportCommand = value;
    }

    public function get identityApplianceRemoveCommand():IIocCommand {
        return _identityApplianceRemoveCommand;
    }

    public function set identityApplianceRemoveCommand(value:IIocCommand):void {
        _identityApplianceRemoveCommand = value;
    }

    public function get identityApplianceUndisposeCommand():IIocCommand {
        return _identityApplianceUndisposeCommand;
    }

    public function set identityApplianceUndisposeCommand(value:IIocCommand):void {
        _identityApplianceUndisposeCommand = value;
    }

    public function get identityProviderRemoveCommand():IIocCommand {
        return _identityProviderRemoveCommand;
    }

    public function set identityProviderRemoveCommand(value:IIocCommand):void {
        _identityProviderRemoveCommand = value;
    }

    public function get externalSaml2IdentityProviderRemoveCommand():IIocCommand {
        return _externalSaml2IdentityProviderRemoveCommand;
    }

    public function set externalSaml2IdentityProviderRemoveCommand(value:IIocCommand):void {
        _externalSaml2IdentityProviderRemoveCommand = value;
    }

    public function get externalSaml2ServiceProviderRemoveCommand():IIocCommand {
        return _externalSaml2ServiceProviderRemoveCommand;
    }

    public function set externalSaml2ServiceProviderRemoveCommand(value:IIocCommand):void {
        _externalSaml2ServiceProviderRemoveCommand = value;
    }

    public function get internalSaml2ServiceProviderRemoveCommand():IIocCommand {
        return _internalSaml2ServiceProviderRemoveCommand;
    }

    public function set internalSaml2ServiceProviderRemoveCommand(value:IIocCommand):void {
        _internalSaml2ServiceProviderRemoveCommand = value;
    }

    public function get externalOpenIDIdentityProviderRemoveCommand():IIocCommand {
        return _externalOpenIDIdentityProviderRemoveCommand;
    }

    public function set externalOpenIDIdentityProviderRemoveCommand(value:IIocCommand):void {
        _externalOpenIDIdentityProviderRemoveCommand = value;
    }

    public function get oauth2IdentityProviderRemoveCommand():IIocCommand {
        return _oauth2IdentityProviderRemoveCommand;
    }

    public function set oauth2IdentityProviderRemoveCommand(value:IIocCommand):void {
        _oauth2IdentityProviderRemoveCommand = value;
    }

    public function get oauth2ServiceProviderRemoveCommand():IIocCommand {
        return _oauth2ServiceProviderRemoveCommand;
    }

    public function set oauth2ServiceProviderRemoveCommand(value:IIocCommand):void {
        _oauth2ServiceProviderRemoveCommand = value;
    }

    public function get josso1ResourceRemoveCommand():IIocCommand {
        return _josso1ResourceRemoveCommand;
    }

    public function set josso1ResourceRemoveCommand(value:IIocCommand):void {
        _josso1ResourceRemoveCommand = value;
    }

    public function get josso2ResourceRemoveCommand():IIocCommand {
        return _josso2ResourceRemoveCommand;
    }

    public function set josso2ResourceRemoveCommand(value:IIocCommand):void {
        _josso2ResourceRemoveCommand = value;
    }

    public function get jbosseppResourceRemoveCommand():IIocCommand {
        return _jbosseppResourceRemoveCommand;
    }

    public function set jbosseppResourceRemoveCommand(value:IIocCommand):void {
        _jbosseppResourceRemoveCommand = value;
    }

    public function get liferayResourceRemoveCommand():IIocCommand {
        return _liferayResourceRemoveCommand;
    }

    public function set liferayResourceRemoveCommand(value:IIocCommand):void {
        _liferayResourceRemoveCommand = value;
    }


    public function get selfServicesResourceRemoveCommand():IIocCommand {
        return _selfServicesResourceRemoveCommand;
    }

    public function set selfServicesResourceRemoveCommand(value:IIocCommand):void {
        _selfServicesResourceRemoveCommand = value;
    }

    public function get dominoResourceRemoveCommand():IIocCommand {
        return _dominoResourceRemoveCommand;
    }

    public function set dominoResourceRemoveCommand(value:IIocCommand):void {
        _dominoResourceRemoveCommand = value;
    }

    public function get blackboardResourceRemoveCommand():IIocCommand {
        return _blackboardResourceRemoveCommand;
    }

    public function set blackboardResourceRemoveCommand(value:IIocCommand):void {
        _blackboardResourceRemoveCommand = value;
    }

    public function get lookupIdentityApplianceByIdCommand():IIocCommand {
        return _lookupIdentityApplianceByIdCommand;
    }

    public function set lookupIdentityApplianceByIdCommand(value:IIocCommand):void {
        _lookupIdentityApplianceByIdCommand = value;
    }

    public function get identityApplianceUpdateCommand():IIocCommand {
        return _identityApplianceUpdateCommand;
    }

    public function set identityApplianceUpdateCommand(value:IIocCommand):void {
        _identityApplianceUpdateCommand = value;
    }

    public function get identityVaultRemoveCommand():IIocCommand {
        return _identityVaultRemoveCommand;
    }

    public function set identityVaultRemoveCommand(value:IIocCommand):void {
        _identityVaultRemoveCommand = value;
    }

    public function get activationRemoveCommand():IIocCommand {
        return _activationRemoveCommand;
    }

    public function set activationRemoveCommand(value:IIocCommand):void {
        _activationRemoveCommand = value;
    }

    public function get serviceConnectionRemoveCommand():IIocCommand {
        return _serviceConnectionRemoveCommand;
    }

    public function set serviceConnectionRemoveCommand(value:IIocCommand):void {
        _serviceConnectionRemoveCommand = value;
    }

    public function get identityLookupRemoveCommand():IIocCommand {
        return _identityLookupRemoveCommand;
    }

    public function set identityLookupRemoveCommand(value:IIocCommand):void {
        _identityLookupRemoveCommand = value;
    }

    public function get federatedConnectionRemoveCommand():IIocCommand {
        return _federatedConnectionRemoveCommand;
    }

    public function set federatedConnectionRemoveCommand(value:IIocCommand):void {
        _federatedConnectionRemoveCommand = value;
    }

    public function get delegatedAuthenticationRemoveCommand():IIocCommand {
        return _delegatedAuthenticationRemoveCommand;
    }

    public function set delegatedAuthenticationRemoveCommand(value:IIocCommand):void {
        _delegatedAuthenticationRemoveCommand = value;
    }

    public function get executionEnvironmentRemoveCommand():IIocCommand {
        return _executionEnvironmentRemoveCommand;
    }

    public function set executionEnvironmentRemoveCommand(value:IIocCommand):void {
        _executionEnvironmentRemoveCommand = value;
    }

    public function get authenticationServiceRemoveCommand():IIocCommand {
        return _authenticationServiceRemoveCommand;
    }

    public function set authenticationServiceRemoveCommand(value:IIocCommand):void {
        _authenticationServiceRemoveCommand = value;
    }

    public function get activateExecEnvironmentCommand():IIocCommand {
        return _activateExecEnvironmentCommand;
    }

    public function set activateExecEnvironmentCommand(value:IIocCommand):void {
        _activateExecEnvironmentCommand = value;
    }

    public function get createServiceConnectionCommand():IIocCommand {
        return _createServiceConnectionCommand;
    }

    public function set createServiceConnectionCommand(value:IIocCommand):void {
        _createServiceConnectionCommand = value;
    }

    public function get createActivationCommand():IIocCommand {
        return _createActivationCommand;
    }

    public function set createActivationCommand(value:IIocCommand):void {
        _createActivationCommand = value;
    }

    public function get createIdentityLookupCommand():IIocCommand {
        return _createIdentityLookupCommand;
    }

    public function set createIdentityLookupCommand(value:IIocCommand):void {
        _createIdentityLookupCommand = value;
    }

    public function get createDelegatedAuthenticationCommand():IIocCommand {
        return _createDelegatedAuthenticationCommand;
    }

    public function set createDelegatedAuthenticationCommand(value:IIocCommand):void {
        _createDelegatedAuthenticationCommand = value;
    }

    public function get folderExistsCommand():IIocCommand {
        return _folderExistsCommand;
    }

    public function set folderExistsCommand(value:IIocCommand):void {
        _folderExistsCommand = value;
    }

    public function get foldersExistsCommand():IIocCommand {
        return _foldersExistsCommand;
    }

    public function set foldersExistsCommand(value:IIocCommand):void {
        _foldersExistsCommand = value;
    }

    public function get jdbcDriversListCommand():IIocCommand {
        return _jdbcDriversListCommand;
    }

    public function set jdbcDriversListCommand(value:IIocCommand):void {
        _jdbcDriversListCommand = value;
    }


    public function get embeddedIdentityVaultsListCommand():IIocCommand {
        return _embeddedIdentityVaultsListCommand;
    }

    public function set embeddedIdentityVaultsListCommand(value:IIocCommand):void {
        _embeddedIdentityVaultsListCommand = value;
    }

    public function get getMetadataInfoCommand():IIocCommand {
        return _getMetadataInfoCommand;
    }

    public function set getMetadataInfoCommand(value:IIocCommand):void {
        _getMetadataInfoCommand = value;
    }

    public function get getCertificateInfoCommand():IIocCommand {
        return _getCertificateInfoCommand;
    }

    public function set getCertificateInfoCommand(value:IIocCommand):void {
        _getCertificateInfoCommand = value;
    }

    public function get exportIdentityApplianceCommand():IIocCommand {
        return _exportIdentityApplianceCommand;
    }

    public function set exportIdentityApplianceCommand(value:IIocCommand):void {
        _exportIdentityApplianceCommand = value;
    }

    public function get exportProviderCertificateCommand():IIocCommand {
        return _exportProviderCertificateCommand;
    }

    public function set exportProviderCertificateCommand(value:IIocCommand):void {
        _exportProviderCertificateCommand = value;
    }

    public function get exportMetadataCommand():IIocCommand {
        return _exportMetadataCommand;
    }

    public function set exportMetadataCommand(value:IIocCommand):void {
        _exportMetadataCommand = value;
    }

    public function get exportAgentConfigCommand():IIocCommand {
        return _exportAgentConfigCommand;
    }

    public function set exportAgentConfigCommand(value:IIocCommand):void {
        _exportAgentConfigCommand = value;
    }

    public function get accountLinkagePolicyListCommand():IIocCommand {
        return _accountLinkagePolicyListCommand;
    }

    public function set accountLinkagePolicyListCommand(value:IIocCommand):void {
        _accountLinkagePolicyListCommand = value;
    }


    public function get userDashboardBrandingsListCommand():IIocCommand {
        return _userDashboardBrandingsListCommand;
    }

    public function set userDashboardBrandingsListCommand(value:IIocCommand):void {
        _userDashboardBrandingsListCommand = value;
    }


    public function get idpSelectorsListCommand():IIocCommand {
        return _idpSelectorsListCommand;
    }

    public function set idpSelectorsListCommand(value:IIocCommand):void {
        _idpSelectorsListCommand = value;
    }

    public function get identityFlowComponentsListCommand():IIocCommand {
        return _identityFlowComponentsListCommand;
    }

    public function set identityFlowComponentsListCommand(value:IIocCommand):void {
        _identityFlowComponentsListCommand = value;
    }

    public function get impersonateUserPoliciesListCommand():IIocCommand {
        return _impersonateUserPoliciesListCommand;
    }

    public function set impersonateUserPoliciesListCommand(value:IIocCommand):void {
        _impersonateUserPoliciesListCommand = value;
    }

    public function get identityMappingPoliciesListCommand():IIocCommand {
        return _identityMappingPoliciesListCommand;
    }

    public function set identityMappingPoliciesListCommand(value:IIocCommand):void {
        _identityMappingPoliciesListCommand = value;
    }

    public function get subjectNameIDPolicyListCommand():IIocCommand {
        return _subjectNameIDPolicyListCommand;
    }

    public function set subjectNameIDPolicyListCommand(value:IIocCommand):void {
        _subjectNameIDPolicyListCommand = value;
    }

}
}
