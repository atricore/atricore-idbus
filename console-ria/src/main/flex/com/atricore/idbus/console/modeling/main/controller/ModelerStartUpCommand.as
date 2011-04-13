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
    private var _serviceProviderCreateMediator:IIocMediator;
    private var _externalIdentityProviderCreateMediator:IIocMediator;
    private var _externalServiceProviderCreateMediator:IIocMediator;
    private var _salesforceCreateMediator:IIocMediator;
    private var _googleAppsCreateMediator:IIocMediator;
    private var _sugarCRMCreateMediator:IIocMediator;
    private var _identityVaultCreateMediator:IIocMediator;
    private var _dbIdentitySourceCreateMediator:IIocMediator;
    private var _ldapIdentitySourceCreateMediator:IIocMediator;
    private var _xmlIdentitySourceCreateMediator:IIocMediator;
    private var _jbossExecutionEnvironmentCreateMediator:IIocMediator;
    private var _weblogicExecutionEnvironmentCreateMediator:IIocMediator;
    private var _tomcatExecutionEnvironmentCreateMediator:IIocMediator;
    private var _jbossPortalExecutionEnvironmentCreateMediator:IIocMediator;
    private var _liferayPortalExecutionEnvironmentCreateMediator:IIocMediator;
    private var _wasceExecutionEnvironmentCreateMediator:IIocMediator;
    private var _apacheExecutionEnvironmentCreateMediator:IIocMediator;
    private var _alfrescoExecutionEnvironmentCreateMediator:IIocMediator;
    private var _javaEEExecutionEnvironmentCreateMediator:IIocMediator;
    private var _phpExecutionEnvironmentCreateMediator:IIocMediator;
    private var _phpBBExecutionEnvironmentCreateMediator:IIocMediator;
    private var _windowsIISExecutionEnvironmentCreateMediator:IIocMediator;
    private var _webserverExecutionEnvironmentCreateMediator:IIocMediator;
    private var _simpleSSOWizardViewMediator:IIocMediator;
    private var _activationCreateMediator:IIocMediator;
    private var _federatedConnectionCreateMediator:IIocMediator;
    private var _exportIdentityApplianceMediator:IIocMediator;
    private var _exportProviderCertificateMediator:IIocMediator;
    private var _exportMetadataMediator:IIocMediator;
    private var _activationMediator:IIocMediator;
    private var _wikidCreateMediator:IIocMediator;
    
    // Commands
    private var _createSimpleSSOIdentityApplianceCommand:IIocCommand;
    private var _identityApplianceListLoadCommand:IIocCommand;
    private var _identityApplianceCreateCommand:IIocCommand;
    private var _identityApplianceImportCommand:IIocCommand;
    private var _identityApplianceRemoveCommand:IIocCommand;
    private var _identityProviderRemoveCommand:IIocCommand;
    private var _serviceProviderRemoveCommand:IIocCommand;
    private var _externalIdentityProviderRemoveCommand:IIocCommand;
    private var _externalServiceProviderRemoveCommand:IIocCommand;
    private var _lookupIdentityApplianceByIdCommand:IIocCommand;
    private var _identityApplianceUpdateCommand:IIocCommand;
    private var _identityVaultRemoveCommand:IIocCommand;
    private var _activationRemoveCommand:IIocCommand;
    private var _identityLookupRemoveCommand:IIocCommand;
    private var _federatedConnectionRemoveCommand:IIocCommand;
    private var _delegatedAuthenticationRemoveCommand:IIocCommand;
    private var _executionEnvironmentRemoveCommand:IIocCommand;
    private var _authenticationServiceRemoveCommand:IIocCommand;
    private var _activateExecEnvironmentCommand:IIocCommand;
    private var _createIdentityLookupCommand:IIocCommand;
    private var _createDelegatedAuthenticationCommand:IIocCommand;
    private var _folderExistsCommand:IIocCommand;
    private var _foldersExistsCommand:IIocCommand;
    private var _jdbcDriversListCommand:IIocCommand;
    private var _getMetadataInfoCommand:IIocCommand;
    private var _getCertificateInfoCommand:IIocCommand;
    private var _exportIdentityApplianceCommand:IIocCommand;
    private var _exportProviderCertificateCommand:IIocCommand;
    private var _exportMetadataCommand:IIocCommand;
    private var _accountLinkagePolicyListCommand:IIocCommand;
    private var _identityMappingPolicyListCommand:IIocCommand;
    
    public function ModelerStartUpCommand() {
    }

    public function get modelerMediator():ModelerMediator {
        return appSectionMediator as ModelerMediator;
    }

    public function set modelerMediator(value:ModelerMediator):void {
        appSectionMediator = value;
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
        iocFacade.registerMediatorByConfigName(serviceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalIdentityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(externalServiceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(salesforceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(googleAppsCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(sugarCRMCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityVaultCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dbIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(ldapIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(xmlIdentitySourceCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(weblogicExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(tomcatExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(jbossPortalExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(liferayPortalExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(wasceExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(apacheExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(alfrescoExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(javaEEExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(phpExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(phpBBExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(windowsIISExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(webserverExecutionEnvironmentCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(activationCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(federatedConnectionCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportIdentityApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportProviderCertificateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(exportMetadataMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(activationMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(wikidCreateMediator.getConfigName());
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, createSimpleSSOIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, identityApplianceCreateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IMPORT_IDENTITY_APPLIANCE, identityApplianceImportCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_REMOVE, identityApplianceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_PROVIDER_REMOVE, identityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SERVICE_PROVIDER_REMOVE, serviceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_IDENTITY_PROVIDER_REMOVE, externalIdentityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXTERNAL_SERVICE_PROVIDER_REMOVE, externalServiceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_SOURCE_REMOVE, identityVaultRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATION_REMOVE, activationRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_LOOKUP_REMOVE, identityLookupRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.FEDERATED_CONNECTION_REMOVE, federatedConnectionRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DELEGATED_AUTHENTICATION_REMOVE, delegatedAuthenticationRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EXECUTION_ENVIRONMENT_REMOVE, executionEnvironmentRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, authenticationServiceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, lookupIdentityApplianceByIdCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD, identityApplianceListLoadCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE, identityApplianceUpdateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvironmentCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_LOOKUP, createIdentityLookupCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_DELEGATED_AUTHENTICATION, createDelegatedAuthenticationCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, folderExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, foldersExistsCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_JDBC_DRIVERS, jdbcDriversListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_METADATA_INFO, getMetadataInfoCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_CERTIFICATE_INFO, getCertificateInfoCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_EXPORT, exportIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.PROVIDER_CERTIFICATE_EXPORT, exportProviderCertificateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.METADATA_EXPORT, exportMetadataCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_ACCOUNT_LINKAGE_POLICIES, accountLinkagePolicyListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_IDENTITY_MAPPING_POLICIES, identityMappingPolicyListCommand.getConfigName());
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

    public function get serviceProviderCreateMediator():IIocMediator {
        return _serviceProviderCreateMediator;
    }

    public function set serviceProviderCreateMediator(value:IIocMediator):void {
        _serviceProviderCreateMediator = value;
    }

    public function get externalIdentityProviderCreateMediator():IIocMediator {
        return _externalIdentityProviderCreateMediator;
    }

    public function set externalIdentityProviderCreateMediator(value:IIocMediator):void {
        _externalIdentityProviderCreateMediator = value;
    }

    public function get externalServiceProviderCreateMediator():IIocMediator {
        return _externalServiceProviderCreateMediator;
    }

    public function set externalServiceProviderCreateMediator(value:IIocMediator):void {
        _externalServiceProviderCreateMediator = value;
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

    public function get identityVaultCreateMediator():IIocMediator {
        return _identityVaultCreateMediator;
    }

    public function set identityVaultCreateMediator(value:IIocMediator):void {
        _identityVaultCreateMediator = value;
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

    public function get jbossExecutionEnvironmentCreateMediator():IIocMediator {
        return _jbossExecutionEnvironmentCreateMediator;
    }

    public function set jbossExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _jbossExecutionEnvironmentCreateMediator = value;
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

    public function get jbossPortalExecutionEnvironmentCreateMediator():IIocMediator {
        return _jbossPortalExecutionEnvironmentCreateMediator;
    }

    public function set jbossPortalExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _jbossPortalExecutionEnvironmentCreateMediator = value;
    }

    public function get liferayPortalExecutionEnvironmentCreateMediator():IIocMediator {
        return _liferayPortalExecutionEnvironmentCreateMediator;
    }

    public function set liferayPortalExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _liferayPortalExecutionEnvironmentCreateMediator = value;
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

    public function get alfrescoExecutionEnvironmentCreateMediator():IIocMediator {
        return _alfrescoExecutionEnvironmentCreateMediator;
    }

    public function set alfrescoExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _alfrescoExecutionEnvironmentCreateMediator = value;
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

    public function get phpBBExecutionEnvironmentCreateMediator():IIocMediator {
        return _phpBBExecutionEnvironmentCreateMediator;
    }

    public function set phpBBExecutionEnvironmentCreateMediator(value:IIocMediator):void {
        _phpBBExecutionEnvironmentCreateMediator = value;
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

    public function get identityProviderRemoveCommand():IIocCommand {
        return _identityProviderRemoveCommand;
    }

    public function set identityProviderRemoveCommand(value:IIocCommand):void {
        _identityProviderRemoveCommand = value;
    }

    public function get serviceProviderRemoveCommand():IIocCommand {
        return _serviceProviderRemoveCommand;
    }

    public function set serviceProviderRemoveCommand(value:IIocCommand):void {
        _serviceProviderRemoveCommand = value;
    }

    public function get externalIdentityProviderRemoveCommand():IIocCommand {
        return _externalIdentityProviderRemoveCommand;
    }

    public function set externalIdentityProviderRemoveCommand(value:IIocCommand):void {
        _externalIdentityProviderRemoveCommand = value;
    }

    public function get externalServiceProviderRemoveCommand():IIocCommand {
        return _externalServiceProviderRemoveCommand;
    }

    public function set externalServiceProviderRemoveCommand(value:IIocCommand):void {
        _externalServiceProviderRemoveCommand = value;
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

    public function get accountLinkagePolicyListCommand():IIocCommand {
        return _accountLinkagePolicyListCommand;
    }

    public function set accountLinkagePolicyListCommand(value:IIocCommand):void {
        _accountLinkagePolicyListCommand = value;
    }

    public function get identityMappingPolicyListCommand():IIocCommand {
        return _identityMappingPolicyListCommand;
    }

    public function set identityMappingPolicyListCommand(value:IIocCommand):void {
        _identityMappingPolicyListCommand = value;
    }
}
}
