/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.modeling.propertysheet {
import com.atricore.idbus.console.components.CustomViewStack;
import com.atricore.idbus.console.components.URLValidator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckFoldersRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.diagram.model.response.CheckFoldersResponse;
import com.atricore.idbus.console.modeling.main.controller.AccountLinkagePolicyListCommand;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.main.controller.FoldersExistsCommand;
import com.atricore.idbus.console.modeling.main.controller.GetCertificateInfoCommand;
import com.atricore.idbus.console.modeling.main.controller.GetMetadataInfoCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityMappingPolicyListCommand;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.authenticationservice.directory.DirectoryAuthnServiceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.authenticationservice.directory.DirectoryAuthnServiceLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.authenticationservice.wikid.WikidAuthnServiceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.certificate.CertificateSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.delegatedauthentication.DelegatedAuthenticationCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.ExecutionEnvironmentActivationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.alfresco.AlfrescoExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.apache.ApacheExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.javaee.JavaEEExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jboss.JBossExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jbossportal.JBossPortalExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.liferayportal.LiferayPortalExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.php.PHPExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.phpbb.PhpBBExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.tomcat.TomcatExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.wasce.WASCEExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.weblogic.WeblogicExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.webserver.WebserverExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.windowsiis.WindowsIISExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalidp.ExternalIdentityProviderCertificateSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalidp.ExternalIdentityProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalidp.ExternalIdentityProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalsp.ExternalServiceProviderCertificateSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalsp.ExternalServiceProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.externalsp.ExternalServiceProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionIDPChannelSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionSPChannelSection;
import com.atricore.idbus.console.modeling.propertysheet.view.googleapps.GoogleAppsContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.googleapps.GoogleAppsCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.identitylookup.IdentityLookupCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.identityvault.EmbeddedDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.BasicAuthenticationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.BindAuthenticationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.TwoFactorAuthenticationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.jossoactivation.JOSSOActivationCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.salesforce.SalesforceContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.salesforce.SalesforceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sugarcrm.SugarCRMContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sugarcrm.SugarCRMCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.xmlidentitysource.XmlIdentitySourceCoreSection;
import com.atricore.idbus.console.services.dto.AccountLinkEmitterType;
import com.atricore.idbus.console.services.dto.AlfrescoExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.BindAuthentication;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;
import com.atricore.idbus.console.services.dto.DirectoryAuthenticationService;
import com.atricore.idbus.console.services.dto.EmbeddedIdentitySource;
import com.atricore.idbus.console.services.dto.ExecEnvType;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.GoogleAppsServiceProvider;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceState;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityMappingType;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentityProviderChannel;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JBossPortalExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JEEExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.LiferayExecutionEnvironment;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.PHPExecutionEnvironment;
import com.atricore.idbus.console.services.dto.PhpBBExecutionEnvironment;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.SalesforceServiceProvider;
import com.atricore.idbus.console.services.dto.SamlR2ProviderConfig;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;
import com.atricore.idbus.console.services.dto.SugarCRMServiceProvider;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.TwoFactorAuthentication;
import com.atricore.idbus.console.services.dto.WASCEExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WebserverExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WikidAuthenticationService;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;
import com.atricore.idbus.console.services.spi.response.GetCertificateInfoResponse;
import com.atricore.idbus.console.services.spi.response.GetMetadataInfoResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.events.ValidationResultEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.StringUtil;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Group;
import spark.components.TabBar;
import spark.components.TextInput;
import spark.events.IndexChangeEvent;

public class PropertySheetMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _tabbedPropertiesTabBar:TabBar;
    private var _propertySheetsViewStack:CustomViewStack;
    private var _iaCoreSection:IdentityApplianceCoreSection;
    private var _ipCoreSection:IdentityProviderCoreSection;
    private var _spCoreSection:ServiceProviderCoreSection;
    private var _externalIdpCoreSection:ExternalIdentityProviderCoreSection;
    private var _externalIdpContractSection:ExternalIdentityProviderContractSection;
    private var _externalIdpCertificateSection:ExternalIdentityProviderCertificateSection;
    private var _externalSpCoreSection:ExternalServiceProviderCoreSection;
    private var _externalSpContractSection:ExternalServiceProviderContractSection;
    private var _externalSpCertificateSection:ExternalServiceProviderCertificateSection;
    private var _salesforceCoreSection:SalesforceCoreSection;
    private var _salesforceContractSection:SalesforceContractSection;
    private var _googleAppsCoreSection:GoogleAppsCoreSection;
    private var _googleAppsContractSection:GoogleAppsContractSection;
    private var _sugarCRMCoreSection:SugarCRMCoreSection;
    private var _sugarCRMContractSection:SugarCRMContractSection;
    private var _embeddedDbVaultCoreSection:EmbeddedDBIdentityVaultCoreSection;
    private var _externalDbVaultCoreSection:ExternalDBIdentityVaultCoreSection;
    private var _ldapIdentitySourceCoreSection:LdapIdentitySourceCoreSection;
    private var _ldapIdentitySourceLookupSection:LdapIdentitySourceLookupSection;
    private var _xmlIdentitySourceCoreSection:XmlIdentitySourceCoreSection;
    private var _currentIdentityApplianceElement:Object;
    private var _ipContractSection:IdentityProviderContractSection;
    private var _spContractSection:ServiceProviderContractSection;
    private var _externalDbVaultLookupSection:ExternalDBIdentityVaultLookupSection;
    private var _federatedConnectionCoreSection:FederatedConnectionCoreSection;
    private var _federatedConnectionSPChannelSection:FederatedConnectionSPChannelSection;
    private var _federatedConnectionIDPChannelSection:FederatedConnectionIDPChannelSection;
    private var _jossoActivationCoreSection:JOSSOActivationCoreSection;
    private var _identityLookupCoreSection:IdentityLookupCoreSection;
    private var _delegatedAuthenticationCoreSection:DelegatedAuthenticationCoreSection;
    private var _tomcatExecEnvCoreSection:TomcatExecEnvCoreSection;
    private var _weblogicExecEnvCoreSection:WeblogicExecEnvCoreSection;
    private var _jbossPortalExecEnvCoreSection:JBossPortalExecEnvCoreSection;
    private var _liferayExecEnvCoreSection:LiferayPortalExecEnvCoreSection;
    private var _wasceExecEnvCoreSection:WASCEExecEnvCoreSection;
    private var _jbossExecEnvCoreSection:JBossExecEnvCoreSection;
    private var _apacheExecEnvCoreSection:ApacheExecEnvCoreSection;
    private var _windowsIISExecEnvCoreSection:WindowsIISExecEnvCoreSection;
    private var _alfrescoExecEnvCoreSection:AlfrescoExecEnvCoreSection;
    private var _javaEEExecEnvCoreSection:JavaEEExecEnvCoreSection;
    private var _phpExecEnvCoreSection:PHPExecEnvCoreSection;
    private var _phpBBExecEnvCoreSection:PhpBBExecEnvCoreSection;
    private var _webserverExecEnvCoreSection:WebserverExecEnvCoreSection;
    private var _executionEnvironmentActivateSection:ExecutionEnvironmentActivationSection;
    private var _authenticationPropertyTab:Group;
    private var _basicAuthenticationSection:BasicAuthenticationSection;
    private var _twoFactorAuthenticationSection:TwoFactorAuthenticationSection;
    private var _bindAuthenticationSection:BindAuthenticationSection;
    private var _certificateSection:CertificateSection;
    private var _wikidAuthnServiceCoreSection:WikidAuthnServiceCoreSection;
    private var _directoryAuthnServiceCoreSection:DirectoryAuthnServiceCoreSection;
    private var _directoryAuthnServiceLookupSection:DirectoryAuthnServiceLookupSection;
    private var _dirty:Boolean;
    private var _applianceSaved:Boolean;

    private var _execEnvSaveFunction:Function;
    private var _execEnvHomeDir:TextInput;
    
    protected var _validators : Array;

    [Bindable]
    public var _jdbcDrivers:ArrayCollection;
    
    // keystore
    private var _uploadedFile:ByteArray;
    private var _uploadedFileName:String;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    // metadata file
    private var _uploadedMetadata:ByteArray;
    private var _uploadedMetadataName:String;

    [Bindable]
    private var _metadataFileRef:FileReference;

    [Bindable]
    public var _selectedMetadataFiles:ArrayCollection;

    private var _execEnvLocationValidator:Validator;

    [Bindable]
    public var _accountLinkagePolicies:ArrayCollection;

    [Bindable]
    public var _identityMappingPolicies:ArrayCollection;

    // WiKID
    [Bindable]
    private var _wikidCAStoreFileRef:FileReference;

    [Bindable]
    public var _selectedWikidCAStores:ArrayCollection;

    private var _uploadedWikidCAStoreFile:ByteArray;
    private var _uploadedWikidCAStoreFileName:String;

    [Bindable]
    private var _wikidClientStoreFileRef:FileReference;

    [Bindable]
    public var _selectedWCStores:ArrayCollection;

    private var _uploadedWCStoreFile:ByteArray;
    private var _uploadedWCStoreFileName:String;

    public function PropertySheetMediator(name : String = null, viewComp:PropertySheetView = null) {
        super(name, viewComp);
    }


    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        _tabbedPropertiesTabBar = view.tabbedPropertiesTabBar;
        _propertySheetsViewStack = view.propertySheetsViewStack;
        _dirty = false;
        _applianceSaved = true;
        _validators = [];
        _tabbedPropertiesTabBar.selectedIndex = 0;
        _tabbedPropertiesTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);

        _execEnvLocationValidator = new URLValidator();
        _execEnvLocationValidator.required = true;
    }

    private function stackChanged(event:IndexChangeEvent):void {
        _propertySheetsViewStack.selectedIndex = _tabbedPropertiesTabBar.selectedIndex;
        if (_tabbedPropertiesTabBar.selectedItem.name == "Authentication") {
            handleAuthenticationTabClick();
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE,
            ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.DIAGRAM_ELEMENT_SELECTED,
            ApplicationFacade.APPLIANCE_SAVED,
            ApplicationFacade.IDENTITY_APPLIANCE_CHANGED,
            ApplicationFacade.RESET_EXEC_ENV_ACTIVATION,
            FolderExistsCommand.FOLDER_EXISTS,
            FolderExistsCommand.FOLDER_DOESNT_EXISTS,
            FoldersExistsCommand.FOLDERS_EXISTENCE_CHECKED,
            JDBCDriversListCommand.SUCCESS,
            GetMetadataInfoCommand.SUCCESS,
            GetCertificateInfoCommand.SUCCESS,
            AccountLinkagePolicyListCommand.SUCCESS,
            AccountLinkagePolicyListCommand.FAILURE,
            IdentityMappingPolicyListCommand.SUCCESS,
            IdentityMappingPolicyListCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.UPDATE_IDENTITY_APPLIANCE:
                clearPropertyTabs();
                _dirty = false;
                break;
            case ApplicationFacade.DIAGRAM_ELEMENT_SELECTED:
                enablePropertyTabs();
                _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                if (_currentIdentityApplianceElement is IdentityAppliance) {
                    enableIdentityAppliancePropertyTabs();
                } else if (_currentIdentityApplianceElement is IdentityProvider) {
                    enableIdentityProviderPropertyTabs();
                } else if (_currentIdentityApplianceElement is ServiceProvider) {
                    enableServiceProviderPropertyTabs();
//                } else if (_currentIdentityApplianceElement is IdentityProviderChannel) {
//                    enableIdpChannelPropertyTabs();
//                } else if (_currentIdentityApplianceElement is ServiceProviderChannel) {
//                    enableSpChannelPropertyTabs();
                } else if (_currentIdentityApplianceElement is SalesforceServiceProvider) {
                    enableSalesforcePropertyTabs();
                } else if (_currentIdentityApplianceElement is GoogleAppsServiceProvider) {
                    enableGoogleAppsPropertyTabs();
                } else if (_currentIdentityApplianceElement is SugarCRMServiceProvider) {
                    enableSugarCRMPropertyTabs();
                } else if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
                    enableExternalIdentityProviderPropertyTabs();
                } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
                    enableExternalServiceProviderPropertyTabs();
                } else if (_currentIdentityApplianceElement is WikidAuthenticationService) {
                    enableWikidAuthnServicePropertyTabs();
                } else if (_currentIdentityApplianceElement is DirectoryAuthenticationService) {
                    enableDirectoryAuthnServicePropertyTabs();
                } else if (_currentIdentityApplianceElement is IdentitySource) {
                    if (_currentIdentityApplianceElement is EmbeddedIdentitySource) {
                        enableIdentityVaultPropertyTabs();
                    } else if (_currentIdentityApplianceElement is DbIdentitySource) {
                        enableExternalDbVaultPropertyTabs();
                    } else if (_currentIdentityApplianceElement is LdapIdentitySource) {
                        enableLdapIdentitySourcePropertyTabs();
                    } else if (_currentIdentityApplianceElement is XmlIdentitySource) {
                        enableXmlIdentitySourcePropertyTabs();
                    }
                } else if (_currentIdentityApplianceElement is FederatedConnection) {
                    enableFederatedConnectionPropertyTabs();
                } else if (_currentIdentityApplianceElement is JOSSOActivation) {
                    enableJOSSOActivationPropertyTabs();
                } else if (_currentIdentityApplianceElement is IdentityLookup) {
                    enableIdentityLookupPropertyTabs();
                } else if (_currentIdentityApplianceElement is DelegatedAuthentication) {
                    enableDelegatedAuthenticationPropertyTabs();
                } else if (_currentIdentityApplianceElement is ExecutionEnvironment) {
                    if (_currentIdentityApplianceElement is TomcatExecutionEnvironment) {
                        enableTomcatExecEnvPropertyTabs();
                    } else if(_currentIdentityApplianceElement is WeblogicExecutionEnvironment) {
                        enableWeblogicExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is JBossPortalExecutionEnvironment) {
                        enableJBossPortalExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is LiferayExecutionEnvironment) {
                        enableLiferayExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is WASCEExecutionEnvironment) {
                        enableWASCEExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is JbossExecutionEnvironment){
                        enableJbossExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is ApacheExecutionEnvironment){
                        enableApacheExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is WindowsIISExecutionEnvironment){
                        enableWindowsIISExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is AlfrescoExecutionEnvironment) {
                        enableAlfrescoExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is JEEExecutionEnvironment){
                        enableJavaEEExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is PHPExecutionEnvironment){
                        enablePHPExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is PhpBBExecutionEnvironment){
                        enablePhpBBExecEnvPropertyTabs();
                    } else if (_currentIdentityApplianceElement is WebserverExecutionEnvironment) {
                        enableWebserverExecEnvPropertyTabs();
                    }
                }
                break;
            case FolderExistsCommand.FOLDER_EXISTS:
                if(_execEnvHomeDir != null && _execEnvSaveFunction != null){
                    _execEnvHomeDir.errorString = "";
                    _execEnvSaveFunction.call();
                    _execEnvHomeDir = null;
                    _execEnvSaveFunction = null;
                }
                break;
            case FolderExistsCommand.FOLDER_DOESNT_EXISTS:
                if(_execEnvHomeDir != null){
                    _execEnvHomeDir.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                    _execEnvHomeDir = null;
                    _execEnvSaveFunction = null;                    
                }
                break;
            case FoldersExistsCommand.FOLDERS_EXISTENCE_CHECKED:
                var checkFoldersResp:CheckFoldersResponse = notification.getBody() as CheckFoldersResponse;
                var currentElement:Object = projectProxy.currentIdentityApplianceElement;
                if (_execEnvSaveFunction != null && currentElement != null && checkFoldersResp.environmentName == "n/a") {
                    if (checkFoldersResp.invalidFolders != null && checkFoldersResp.invalidFolders.length > 0) {
                        for each (var invalidFolder:String in checkFoldersResp.invalidFolders) {
                            if (currentElement is LiferayExecutionEnvironment) {
                                if (_liferayExecEnvCoreSection.homeDirectory.text == invalidFolder) {
                                    _liferayExecEnvCoreSection.homeDirectory.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                                }
                                if (_liferayExecEnvCoreSection.containerPath.text == invalidFolder) {
                                    _liferayExecEnvCoreSection.containerPath.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                                }
                            } else if (currentElement is AlfrescoExecutionEnvironment){
                                if (_alfrescoExecEnvCoreSection.homeDirectory.text == invalidFolder) {
                                    _alfrescoExecEnvCoreSection.homeDirectory.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                                }
                                if (_alfrescoExecEnvCoreSection.tomcatInstallDir.text == invalidFolder) {
                                    _alfrescoExecEnvCoreSection.tomcatInstallDir.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "executionenvironment.doesntexist");
                                }
                            }
                        }
                    } else {
                        _execEnvSaveFunction.call();
                        _execEnvSaveFunction = null;
                    }
                }
                break;
            case JDBCDriversListCommand.SUCCESS:
                _jdbcDrivers = projectProxy.jdbcDrivers;
                var dbIdentitySource:DbIdentitySource = _currentIdentityApplianceElement as DbIdentitySource;
                if (dbIdentitySource != null) {
                    for (var i:int = 0; i < _externalDbVaultCoreSection.driver.dataProvider.length; i++) {
                        if (_externalDbVaultCoreSection.driver.dataProvider[i].className == dbIdentitySource.driverName) {
                            _externalDbVaultCoreSection.driver.selectedIndex = i;
                            break;
                        }
                    }
                }
                break;
            case JDBCDriversListCommand.FAILURE:
                _jdbcDrivers = new ArrayCollection();
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.loading.jdbc.error"));

                break;
            case ApplicationFacade.APPLIANCE_SAVED:
                _applianceSaved = true;
                break;
            case GetMetadataInfoCommand.SUCCESS:
                var gmiResp:GetMetadataInfoResponse = notification.getBody() as GetMetadataInfoResponse;
                if (gmiResp != null) {
                    updateMetadataSection(gmiResp);
                }
                break;
            case GetCertificateInfoCommand.SUCCESS:
                var gciResp:GetCertificateInfoResponse = notification.getBody() as GetCertificateInfoResponse;
                if (gciResp != null) {
                    updateInternalProviderCertificateSection(gciResp);
                }
                break;
            case ApplicationFacade.IDENTITY_APPLIANCE_CHANGED:
                var changeAction:String = notification.getBody() as String;
                if (changeAction == null || changeAction != "nodesMoved") {
                    _applianceSaved = false;
                    disableExportButtons();
                }
                break;
            case ApplicationFacade.RESET_EXEC_ENV_ACTIVATION:
                _executionEnvironmentActivateSection.reactivate.selected = false;
                break;
            case AccountLinkagePolicyListCommand.SUCCESS:
                if (_currentIdentityApplianceElement != null) {
                    if (_currentIdentityApplianceElement is ServiceProvider && _spCoreSection != null) {
                        _accountLinkagePolicies = projectProxy.accountLinkagePolicies;
                        var sp:ServiceProvider = _currentIdentityApplianceElement as ServiceProvider;
                        if (sp.accountLinkagePolicy != null) {
                            for (var j:int=0; j < _spCoreSection.accountLinkagePolicyCombo.dataProvider.length; j++) {
                                if (_spCoreSection.accountLinkagePolicyCombo.dataProvider[j].name == sp.accountLinkagePolicy.name) {
                                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = j;
                                    break;
                                }
                            }
                        } else {
                            for (var k:int=0; k < _spCoreSection.accountLinkagePolicyCombo.dataProvider.length; k++) {
                                if (_spCoreSection.accountLinkagePolicyCombo.dataProvider[k].linkEmitterType.toString() == AccountLinkEmitterType.ONE_TO_ONE.toString()) {
                                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = k;
                                    break;
                                }
                            }
                        }
                        _spCoreSection.accountLinkagePolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);
                    } else if (_currentIdentityApplianceElement is FederatedConnection && _federatedConnectionIDPChannelSection != null) {
                        _accountLinkagePolicies = projectProxy.accountLinkagePolicies;
                        var idpChannel:IdentityProviderChannel;
                        var fc:FederatedConnection = _currentIdentityApplianceElement as FederatedConnection;
                        if (fc.channelA is IdentityProviderChannel) {
                            idpChannel = fc.channelA as IdentityProviderChannel;
                        } else if (fc.channelB is IdentityProviderChannel) {
                            idpChannel = fc.channelB as IdentityProviderChannel;
                        }
                        
                        if (idpChannel.accountLinkagePolicy != null) {
                            for (var l:int=0; l < _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider.length; l++) {
                                if (_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider[l].name == idpChannel.accountLinkagePolicy.name) {
                                    _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = l;
                                    break;
                                }
                            }
                        } else {
                            for (var m:int=0; m < _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider.length; m++) {
                                if (_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider[m].linkEmitterType.toString() == AccountLinkEmitterType.ONE_TO_ONE.toString()) {
                                    _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = m;
                                    break;
                                }
                            }
                        }
                        _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);
                    }
                }
                break;
            case IdentityMappingPolicyListCommand.SUCCESS:
                if (_currentIdentityApplianceElement != null) {
                    if (_currentIdentityApplianceElement is ServiceProvider && _spCoreSection != null) {
                        _identityMappingPolicies = projectProxy.identityMappingPolicies;
                        var sp2:ServiceProvider = _currentIdentityApplianceElement as ServiceProvider;
                        if (sp2.identityMappingPolicy != null) {
                            for (var n:int=0; n < _spCoreSection.identityMappingPolicyCombo.dataProvider.length; n++) {
                                if (_spCoreSection.identityMappingPolicyCombo.dataProvider[n].name == sp2.identityMappingPolicy.name) {
                                    _spCoreSection.identityMappingPolicyCombo.selectedIndex = n;
                                    break;
                                }
                            }
                        } else {
                            for (var p:int=0; p < _spCoreSection.identityMappingPolicyCombo.dataProvider.length; p++) {
                                if (_spCoreSection.identityMappingPolicyCombo.dataProvider[p].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                                    _spCoreSection.identityMappingPolicyCombo.selectedIndex = p;
                                    break;
                                }
                            }
                        }
                        _spCoreSection.identityMappingPolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);
                    } else if (_currentIdentityApplianceElement is FederatedConnection && _federatedConnectionIDPChannelSection != null) {
                        _identityMappingPolicies = projectProxy.identityMappingPolicies;
                        var idpChannel2:IdentityProviderChannel;
                        var fc2:FederatedConnection = _currentIdentityApplianceElement as FederatedConnection;
                        if (fc2.channelA is IdentityProviderChannel) {
                            idpChannel2 = fc2.channelA as IdentityProviderChannel;
                        } else if (fc2.channelB is IdentityProviderChannel) {
                            idpChannel2 = fc2.channelB as IdentityProviderChannel;
                        }

                        if (idpChannel2.identityMappingPolicy != null) {
                            for (var q:int=0; q < _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider.length; q++) {
                                if (_federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider[q].name == idpChannel2.identityMappingPolicy.name) {
                                    _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.selectedIndex = q;
                                    break;
                                }
                            }
                        } else {
                            for (var r:int=0; r < _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider.length; r++) {
                                if (_federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider[r].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                                    _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.selectedIndex = r;
                                    break;
                                }
                            }
                        }
                        _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);
                    }
                }
                break;
        }

    }

    protected function enableIdentityAppliancePropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _iaCoreSection = new IdentityApplianceCoreSection();
        corePropertyTab.addElement(_iaCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _iaCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityApplianceCorePropertyTabRollOut);

    }

    private function handleCorePropertyTabCreationComplete(event:Event):void {
        var identityAppliance:IdentityAppliance;

        // fetch appliance object
        identityAppliance = projectProxy.currentIdentityAppliance;

        // bind view
        _iaCoreSection.applianceName.text = identityAppliance.idApplianceDefinition.name;
        _iaCoreSection.applianceDescription.text = identityAppliance.idApplianceDefinition.description;
        _iaCoreSection.applianceNamespace.text = identityAppliance.namespace;

        var location:Location = identityAppliance.idApplianceDefinition.location;
        for (var i:int = 0; i < _iaCoreSection.applianceLocationProtocol.dataProvider.length; i++) {
            if (location != null && location.protocol == _iaCoreSection.applianceLocationProtocol.dataProvider[i].label) {
                _iaCoreSection.applianceLocationProtocol.selectedIndex = i;
                break;
            }
        }
        _iaCoreSection.applianceLocationDomain.text = location.host;
        _iaCoreSection.applianceLocationPort.text = location.port.toString() != "0" ?
                location.port.toString() : "";
        _iaCoreSection.applianceLocationContext.text = location.context;
        _iaCoreSection.applianceLocationPath.text = location.uri;

        _iaCoreSection.applianceName.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceDescription.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceNamespace.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationPath.addEventListener(Event.CHANGE, handleSectionChange);

        _validators = [];
        _validators.push(_iaCoreSection.nameValidator);
        _validators.push(_iaCoreSection.portValidator);
        _validators.push(_iaCoreSection.domainValidator);
        _validators.push(_iaCoreSection.contextValidator);
        _validators.push(_iaCoreSection.pathValidator);
        _validators.push(_iaCoreSection.namespaceValidator);
    }

    private function handleIdentityApplianceCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            // fetch appliance object
            var identityAppliance:IdentityAppliance;
            identityAppliance = projectProxy.currentIdentityAppliance;

            identityAppliance.name = _iaCoreSection.applianceName.text;
            identityAppliance.idApplianceDefinition.name = identityAppliance.name;
            identityAppliance.idApplianceDefinition.description = _iaCoreSection.applianceDescription.text;
            identityAppliance.namespace = _iaCoreSection.applianceNamespace.text;
            identityAppliance.idApplianceDefinition.location.protocol = _iaCoreSection.applianceLocationProtocol.selectedItem.label;
            identityAppliance.idApplianceDefinition.location.host = _iaCoreSection.applianceLocationDomain.text;
            identityAppliance.idApplianceDefinition.location.port = parseInt(_iaCoreSection.applianceLocationPort.text);
            identityAppliance.idApplianceDefinition.location.context = _iaCoreSection.applianceLocationContext.text;
            identityAppliance.idApplianceDefinition.location.uri = _iaCoreSection.applianceLocationPath.text;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableIdentityProviderPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _ipCoreSection = new IdentityProviderCoreSection();
        corePropertyTab.addElement(_ipCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _ipCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _ipContractSection = new IdentityProviderContractSection();
        contractPropertyTab.addElement(_ipContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _ipContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityProviderContractPropertyTabRollOut);

        // Authentication Tab
        _authenticationPropertyTab = new Group();
        _authenticationPropertyTab.id = "propertySheetAuthenticationSection";
        _authenticationPropertyTab.name = "Authentication";
        _authenticationPropertyTab.width = Number("100%");
        _authenticationPropertyTab.height = Number("100%");
        _authenticationPropertyTab.setStyle("borderStyle", "solid");

        _propertySheetsViewStack.addNewChild(_authenticationPropertyTab);

        // Certificate Tab
        var certificatePropertyTab:Group = new Group();
        certificatePropertyTab.id = "propertySheetCertificateSection";
        certificatePropertyTab.name = "Certificate";
        certificatePropertyTab.width = Number("100%");
        certificatePropertyTab.height = Number("100%");
        certificatePropertyTab.setStyle("borderStyle", "solid");

        _certificateSection = new CertificateSection();
        certificatePropertyTab.addElement(_certificateSection);
        _propertySheetsViewStack.addNewChild(certificatePropertyTab);
        _certificateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleProviderCertificatePropertyTabCreationComplete);
        certificatePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleProviderCertificatePropertyTabRollOut);
    }

    protected function enableServiceProviderPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _spCoreSection = new ServiceProviderCoreSection();
        corePropertyTab.addElement(_spCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);

        _spCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleServiceProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _spContractSection = new ServiceProviderContractSection();
        contractPropertyTab.addElement(_spContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _spContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleServiceProviderContractPropertyTabRollOut);

        // Certificate Tab
        var certificatePropertyTab:Group = new Group();
        certificatePropertyTab.id = "propertySheetCertificateSection";
        certificatePropertyTab.name = "Certificate";
        certificatePropertyTab.width = Number("100%");
        certificatePropertyTab.height = Number("100%");
        certificatePropertyTab.setStyle("borderStyle", "solid");

        _certificateSection = new CertificateSection();
        certificatePropertyTab.addElement(_certificateSection);
        _propertySheetsViewStack.addNewChild(certificatePropertyTab);
        _certificateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleProviderCertificatePropertyTabCreationComplete);
        certificatePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleProviderCertificatePropertyTabRollOut);
    }

    private function handleIdentityProviderCorePropertyTabCreationComplete(event:Event):void {
        var identityProvider:IdentityProvider;

        identityProvider = _currentIdentityApplianceElement as IdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            // bind view
            _ipCoreSection.identityProviderName.text = identityProvider.name;
            _ipCoreSection.identityProvDescription.text = identityProvider.description;
            //TODO

            for (var i:int = 0; i < _ipCoreSection.idpLocationProtocol.dataProvider.length; i++) {
                if (identityProvider.location != null && _ipCoreSection.idpLocationProtocol.dataProvider[i].data == identityProvider.location.protocol) {
                    _ipCoreSection.idpLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _ipCoreSection.idpLocationDomain.text = identityProvider.location.host;
            _ipCoreSection.idpLocationPort.text = identityProvider.location.port.toString() != "0" ?
                    identityProvider.location.port.toString() : "";
            _ipCoreSection.idpLocationContext.text = identityProvider.location.context;
            _ipCoreSection.idpLocationPath.text = identityProvider.location.uri;

            // select authentication mechanism (currently there is always only one selected authn. mechanism)
            var selectedAuthnMechanism:String = "basic";
            if (identityProvider.authenticationMechanisms != null && identityProvider.authenticationMechanisms.length > 0) {
                var authnMechanism:AuthenticationMechanism  = identityProvider.authenticationMechanisms.getItemAt(0) as AuthenticationMechanism;
                if (authnMechanism is BasicAuthentication)
                    selectedAuthnMechanism = "basic"
                else if (authnMechanism is TwoFactorAuthentication)
                    selectedAuthnMechanism = "2factor";
                else if (authnMechanism is BindAuthentication)
                    selectedAuthnMechanism = "bind";
            }
            for (var j:int = 0; j < _ipCoreSection.authMechanismCombo.dataProvider.length; j++) {
                if (_ipCoreSection.authMechanismCombo.dataProvider[j].data == selectedAuthnMechanism) {
                    _ipCoreSection.authMechanismCombo.selectedIndex = j;
                    break;
                }
            }

            /*
            for each(var authMech:AuthenticationMechanism in identityProvider.authenticationMechanisms){
                if(authMech is BasicAuthentication){
                    var liv:ListItemValueObject = _ipCoreSection.authMechanismColl.getItemAt(0) as ListItemValueObject;
                    liv.isSelected = true;
                }
                //TODO ADD OTHER AUTH MECHANISMS
            }
            */

            _ipCoreSection.identityProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.identityProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.idpLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.idpLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.idpLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.idpLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _ipCoreSection.idpLocationPath.addEventListener(Event.CHANGE, handleSectionChange);

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_ipCoreSection.nameValidator);
            _validators.push(_ipCoreSection.portValidator);
            _validators.push(_ipCoreSection.domainValidator);
            _validators.push(_ipCoreSection.contextValidator);
            _validators.push(_ipCoreSection.pathValidator);
        }
    }


    private function handleIdentityProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var identityProvider:IdentityProvider;

            identityProvider = _currentIdentityApplianceElement as IdentityProvider;

            identityProvider.name = _ipCoreSection.identityProviderName.text;
            identityProvider.description = _ipCoreSection.identityProvDescription.text;

            identityProvider.location.protocol = _ipCoreSection.idpLocationProtocol.labelDisplay.text;
            identityProvider.location.host = _ipCoreSection.idpLocationDomain.text;
            identityProvider.location.port = parseInt(_ipCoreSection.idpLocationPort.text);
            identityProvider.location.context = _ipCoreSection.idpLocationContext.text;
            identityProvider.location.uri = _ipCoreSection.idpLocationPath.text;

            // For now only Basic Authentication is enabled. Modification is done through "Authentication" tab
            /*
            for each(var liv:ListItemValueObject in  _ipCoreSection.authMechanismCombo.dataProvider){
                if(liv.isSelected){
                    if(identityProvider.authenticationMechanisms == null){
                        identityProvider.authenticationMechanisms = new ArrayCollection();
                    }
                    switch(liv.name){
                        case "basic":
                            var basicAuth:BasicAuthentication = new BasicAuthentication();
                            basicAuth.name = identityProvider.name + "-basic-authn";
                            basicAuth.hashAlgorithm = "MD5";
                            basicAuth.hashEncoding = "HEX";
                            basicAuth.ignoreUsernameCase = false;
                            identityProvider.authenticationMechanisms.addItem(basicAuth);
                            break;
                        case "strong":
                            break;
                    }
                }
            }
            */

            // For now only "Default" contract and emission policy exists and there's no need for modification.
            //authenticationContract
            //authenticationAssertionEmissionPolicy

            // update default sp channels
            if (identityProvider.federatedConnectionsA != null) {
                for (var i:int = 0; i < identityProvider.federatedConnectionsA.length; i++) {
                    var spChannel:ServiceProviderChannel = identityProvider.federatedConnectionsA[i].channelA as ServiceProviderChannel;
                    if (!spChannel.overrideProviderSetup) {
                        updateServiceProviderChannel(spChannel, identityProvider);
                    }
                }
            }

            if (identityProvider.federatedConnectionsB != null) {
                for (var j:int = 0; j < identityProvider.federatedConnectionsB.length; j++) {
                    var spChannel2:ServiceProviderChannel = identityProvider.federatedConnectionsB[j].channelB as ServiceProviderChannel;
                    if (!spChannel2.overrideProviderSetup) {
                        updateServiceProviderChannel(spChannel2, identityProvider);
                    }
                }
            }

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleIdentityProviderContractPropertyTabCreationComplete(event:Event):void {

        var identityProvider:IdentityProvider;

        identityProvider = _currentIdentityApplianceElement as IdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            _ipContractSection.wantAuthnRequestsSignedCheck.selected = identityProvider.wantAuthnRequestsSigned;
            _ipContractSection.signRequestsCheck.selected = identityProvider.signRequests;
            _ipContractSection.wantSignedRequestsCheck.selected = identityProvider.wantSignedRequests;
            
            for (var j:int = 0; j < identityProvider.activeBindings.length; j ++) {
                var tmpBinding:Binding = identityProvider.activeBindings.getItemAt(j) as Binding;
                if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                    _ipContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                    _ipContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                    _ipContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                    _ipContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < identityProvider.activeProfiles.length; j++) {
                var tmpProfile:Profile = identityProvider.activeProfiles.getItemAt(j) as Profile;
                if (tmpProfile.name == Profile.SSO.name) {
                    _ipContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _ipContractSection.samlProfileSLOCheck.selected = true;
                }
            }

            if (_applianceSaved) {
                _ipContractSection.btnExportMetadata.enabled = true;
                _ipContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }

            _ipContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.wantAuthnRequestsSignedCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.signRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.wantSignedRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleIdentityProviderContractPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            var identityProvider:IdentityProvider;

            identityProvider = _currentIdentityApplianceElement as IdentityProvider;

//            var spChannel:ServiceProviderChannel = identityProvider.defaultChannel as ServiceProviderChannel;

            if (identityProvider.activeBindings == null) {
                identityProvider.activeBindings = new ArrayCollection();
            }
            identityProvider.activeBindings.removeAll();
            if (_ipContractSection.samlBindingHttpPostCheck.selected) {
                identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_ipContractSection.samlBindingArtifactCheck.selected) {
                identityProvider.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_ipContractSection.samlBindingHttpRedirectCheck.selected) {
                identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_ipContractSection.samlBindingSoapCheck.selected) {
                identityProvider.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (identityProvider.activeProfiles == null) {
                identityProvider.activeProfiles = new ArrayCollection();
            }
            identityProvider.activeProfiles.removeAll();
            if (_ipContractSection.samlProfileSSOCheck.selected) {
                identityProvider.activeProfiles.addItem(Profile.SSO);
            }
            if (_ipContractSection.samlProfileSLOCheck.selected) {
                identityProvider.activeProfiles.addItem(Profile.SSO_SLO);
            }

//            identityProvider.defaultChannel = spChannel;
            identityProvider.wantAuthnRequestsSigned = _ipContractSection.wantAuthnRequestsSignedCheck.selected;
            identityProvider.signRequests = _ipContractSection.signRequestsCheck.selected;
            identityProvider.wantSignedRequests = _ipContractSection.wantSignedRequestsCheck.selected;

            // update default sp channels
            if (identityProvider.federatedConnectionsA != null) {
                for (var i:int = 0; i < identityProvider.federatedConnectionsA.length; i++) {
                    var spChannel:ServiceProviderChannel = identityProvider.federatedConnectionsA[i].channelA as ServiceProviderChannel;
                    if (!spChannel.overrideProviderSetup) {
                        updateServiceProviderChannel(spChannel, identityProvider);
                    }
                }
            }

            if (identityProvider.federatedConnectionsB != null) {
                for (var j:int = 0; j < identityProvider.federatedConnectionsB.length; j++) {
                    var spChannel2:ServiceProviderChannel = identityProvider.federatedConnectionsB[j].channelB as ServiceProviderChannel;
                    if (!spChannel2.overrideProviderSetup) {
                        updateServiceProviderChannel(spChannel2, identityProvider);
                    }
                }
            }
            
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function updateServiceProviderChannel(spChannel:ServiceProviderChannel, identityProvider:IdentityProvider):void {
        // set location
        if (spChannel.location == null) {
            spChannel.location = new Location();
        }
        spChannel.location.protocol = identityProvider.location.protocol;
        spChannel.location.host = identityProvider.location.host;
        spChannel.location.port = identityProvider.location.port;
        spChannel.location.context = identityProvider.location.context;
        spChannel.location.uri = identityProvider.location.uri;

        // set active bindings
        if (spChannel.activeBindings == null) {
            spChannel.activeBindings = new ArrayCollection();
        }
        spChannel.activeBindings.removeAll();
        for (var i:int = 0; i < identityProvider.activeBindings.length; i++) {
            spChannel.activeBindings.addItem(identityProvider.activeBindings[i]);
        }

        // set active profiles
        if (spChannel.activeProfiles == null) {
            spChannel.activeProfiles = new ArrayCollection();
        }
        spChannel.activeProfiles.removeAll();
        for (var j:int = 0; j < identityProvider.activeProfiles.length; j++) {
            spChannel.activeProfiles.addItem(identityProvider.activeProfiles[j]);
        }

        spChannel.wantAuthnRequestsSigned = identityProvider.wantAuthnRequestsSigned;
    }

    private function handleAuthenticationTabClick():void {
        if (_ipCoreSection.authMechanismCombo.selectedItem.data == "basic") {
            _basicAuthenticationSection = new BasicAuthenticationSection();
            _authenticationPropertyTab.addElement(_basicAuthenticationSection);

            _basicAuthenticationSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleBasicAuthenticationPropertyTabCreationComplete);
            _authenticationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleBasicAuthenticationPropertyTabRollOut);
        } else if (_ipCoreSection.authMechanismCombo.selectedItem.data == "2factor") {
            _twoFactorAuthenticationSection = new TwoFactorAuthenticationSection();
            _authenticationPropertyTab.addElement(_twoFactorAuthenticationSection);

            _twoFactorAuthenticationSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleTwoFactorAuthenticationPropertyTabCreationComplete);
            _authenticationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleTwoFactorAuthenticationPropertyTabRollOut);
        } else if (_ipCoreSection.authMechanismCombo.selectedItem.data == "bind") {
            _bindAuthenticationSection = new BindAuthenticationSection();
            _authenticationPropertyTab.addElement(_bindAuthenticationSection);

            _bindAuthenticationSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleBindAuthenticationPropertyTabCreationComplete);
            _authenticationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleBindAuthenticationPropertyTabRollOut);
        }
    }

    private function handleBasicAuthenticationPropertyTabCreationComplete(event:Event):void {
        var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            // bind view

            // find basic authentication
            var basicAuthentication:BasicAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is BasicAuthentication) {
                    basicAuthentication = authMechanism as BasicAuthentication;
                }
            }

            if (basicAuthentication != null) {
                _basicAuthenticationSection.authName.text = basicAuthentication.name;
                for (var i:int = 0; i < _basicAuthenticationSection.hashAlgorithm.dataProvider.length; i++) {
                    if (_basicAuthenticationSection.hashAlgorithm.dataProvider[i].data == basicAuthentication.hashAlgorithm) {
                        _basicAuthenticationSection.hashAlgorithm.selectedIndex = i;
                        break;
                    }
                }
                for (var j:int = 0; j < _basicAuthenticationSection.hashEncoding.dataProvider.length; j++) {
                    if (_basicAuthenticationSection.hashEncoding.dataProvider[j].data == basicAuthentication.hashEncoding) {
                        _basicAuthenticationSection.hashEncoding.selectedIndex = j;
                        break;
                    }
                }
                _basicAuthenticationSection.ignoreUsernameCase.selected = basicAuthentication.ignoreUsernameCase;

                _basicAuthenticationSection.authName.addEventListener(Event.CHANGE, handleSectionChange);
                _basicAuthenticationSection.hashAlgorithm.addEventListener(Event.CHANGE, handleSectionChange);
                _basicAuthenticationSection.hashEncoding.addEventListener(Event.CHANGE, handleSectionChange);
                _basicAuthenticationSection.ignoreUsernameCase.addEventListener(Event.CHANGE, handleSectionChange);

                //clear all existing validators and add basic auth. section validators
                //_validators = [];
                _validators.push(_basicAuthenticationSection.nameValidator);
            }
        }
    }

    private function handleBasicAuthenticationPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

            // find basic authentication
            var basicAuthentication:BasicAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is BasicAuthentication) {
                    basicAuthentication = authMechanism as BasicAuthentication;
                }
            }

            if (basicAuthentication != null) {
                basicAuthentication.name = _basicAuthenticationSection.authName.text;
                basicAuthentication.hashAlgorithm = _basicAuthenticationSection.hashAlgorithm.selectedItem.data;
                basicAuthentication.hashEncoding = _basicAuthenticationSection.hashEncoding.selectedItem.data;
                basicAuthentication.ignoreUsernameCase = _basicAuthenticationSection.ignoreUsernameCase.selected;

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
                _applianceSaved = false;
                _dirty = false;
            }
        }
    }

    private function handleTwoFactorAuthenticationPropertyTabCreationComplete(event:Event):void {
        var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            // bind view

            // find two-factor authentication
            var twoFactorAuthentication:TwoFactorAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is TwoFactorAuthentication) {
                    twoFactorAuthentication = authMechanism as TwoFactorAuthentication;
                }
            }

            if (twoFactorAuthentication != null) {
                _twoFactorAuthenticationSection.authName.text = twoFactorAuthentication.name;
                
                _twoFactorAuthenticationSection.authName.addEventListener(Event.CHANGE, handleSectionChange);

                //clear all existing validators and add basic auth. section validators
                //_validators = [];
                _validators.push(_twoFactorAuthenticationSection.nameValidator);
            }
        }
    }

    private function handleTwoFactorAuthenticationPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

            // find two-factor authentication
            var twoFactorAuthentication:TwoFactorAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is TwoFactorAuthentication) {
                    twoFactorAuthentication = authMechanism as TwoFactorAuthentication;
                }
            }

            if (twoFactorAuthentication != null) {
                twoFactorAuthentication.name = _twoFactorAuthenticationSection.authName.text;

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
                _applianceSaved = false;
                _dirty = false;
            }
        }
    }

    private function handleBindAuthenticationPropertyTabCreationComplete(event:Event):void {
        var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            // bind view

            // find bind authentication
            var bindAuthentication:BindAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is BindAuthentication) {
                    bindAuthentication = authMechanism as BindAuthentication;
                }
            }

            if (bindAuthentication != null) {
                _bindAuthenticationSection.authName.text = bindAuthentication.name;

                _bindAuthenticationSection.authName.addEventListener(Event.CHANGE, handleSectionChange);

                //clear all existing validators and add basic auth. section validators
                //_validators = [];
                _validators.push(_bindAuthenticationSection.nameValidator);
            }
        }
    }

    private function handleBindAuthenticationPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var identityProvider:IdentityProvider = _currentIdentityApplianceElement as IdentityProvider;

            // find bind authentication
            var bindAuthentication:BindAuthentication = null;
            for each (var authMechanism:AuthenticationMechanism in identityProvider.authenticationMechanisms) {
                if (authMechanism is BindAuthentication) {
                    bindAuthentication = authMechanism as BindAuthentication;
                }
            }

            if (bindAuthentication != null) {
                bindAuthentication.name = _bindAuthenticationSection.authName.text;

                sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
                _applianceSaved = false;
                _dirty = false;
            }
        }
    }

    private function handleExportMetadataClick(event:MouseEvent):void {
        if (_currentIdentityApplianceElement is Provider) {
            var applianceId:String = projectProxy.currentIdentityAppliance.id.toString();
            sendNotification(ApplicationFacade.EXPORT_METADATA, [applianceId, _currentIdentityApplianceElement.name, null, false]);
        }
    }

    private function handleExportSPChannelMetadataClick(event:MouseEvent):void {
        if (_currentIdentityApplianceElement is FederatedConnection) {
            var applianceId:String = projectProxy.currentIdentityAppliance.id.toString();
            var fedConn:FederatedConnection = _currentIdentityApplianceElement as FederatedConnection;
            var provider:Provider;
            var spChannel:ServiceProviderChannel;
            if (fedConn != null) {
                if (fedConn.channelA is ServiceProviderChannel) {
                    spChannel = fedConn.channelA as ServiceProviderChannel;
                    provider = fedConn.roleA;
                } else if (fedConn.channelB is ServiceProviderChannel) {
                    spChannel = fedConn.channelB as ServiceProviderChannel;
                    provider = fedConn.roleB;
                }
            }
            sendNotification(ApplicationFacade.EXPORT_METADATA, [applianceId, provider.name, spChannel.name, spChannel.overrideProviderSetup]);
        }
    }

    private function handleExportIDPChannelMetadataClick(event:MouseEvent):void {
        if (_currentIdentityApplianceElement is FederatedConnection) {
            var applianceId:String = projectProxy.currentIdentityAppliance.id.toString();
            var fedConn:FederatedConnection = _currentIdentityApplianceElement as FederatedConnection;
            var provider:Provider;
            var idpChannel:IdentityProviderChannel;
            if (fedConn != null) {
                if (fedConn.channelA is IdentityProviderChannel) {
                    idpChannel = fedConn.channelA as IdentityProviderChannel;
                    provider = fedConn.roleA;
                } else if (fedConn.channelB is IdentityProviderChannel) {
                    idpChannel = fedConn.channelB as IdentityProviderChannel;
                    provider = fedConn.roleB;
                }
            }
            sendNotification(ApplicationFacade.EXPORT_METADATA, [applianceId, provider.name, idpChannel.name, idpChannel.overrideProviderSetup]);
        }
    }
    
    private function handleExportCertificateClick(event:MouseEvent):void {
        var provider:Provider = _currentIdentityApplianceElement as Provider;
        if (provider != null) {
            sendNotification(ApplicationFacade.EXPORT_PROVIDER_CERTIFICATE);
        }
    }

    private function initCertificateSection(config:SamlR2ProviderConfig):void {
        if (config.useSampleStore) {
            _certificateSection.useDefaultKeystore.selected = true;
            enableDisableUploadFields(false);
        } else {
            _certificateSection.uploadKeystore.selected = true;
            enableDisableUploadFields(true);
            if (config.signer != null) {
                _certificateSection.certificateAlias.text = config.signer.certificateAlias;
                _certificateSection.keyAlias.text = config.signer.privateKeyName;
                _certificateSection.keystorePassword.text = config.signer.password;
                _certificateSection.keyPassword.text = config.signer.privateKeyPassword;
            }
        }

        sendNotification(ApplicationFacade.GET_CERTIFICATE_INFO, config);

        if (_applianceSaved) {
            _certificateSection.btnExportCertificate.enabled = true;
            _certificateSection.btnExportCertificate.addEventListener(MouseEvent.CLICK, handleExportCertificateClick);
        }

        _certificateSection.certificateManagementType.addEventListener(ItemClickEvent.ITEM_CLICK, handleSectionChange);
        _certificateSection.certificateKeyPair.addEventListener(Event.CHANGE, handleSectionChange);
        _certificateSection.keystoreFormat.addEventListener(Event.CHANGE, handleSectionChange);
        _certificateSection.certificateAlias.addEventListener(Event.CHANGE, handleSectionChange);
        _certificateSection.keyAlias.addEventListener(Event.CHANGE, handleSectionChange);
        _certificateSection.keystorePassword.addEventListener(Event.CHANGE, handleSectionChange);
        _certificateSection.keyPassword.addEventListener(Event.CHANGE, handleSectionChange);

        _certificateSection.certificateManagementType.addEventListener(ItemClickEvent.ITEM_CLICK, handleCertManagementTypeClicked);
        _certificateSection.certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);
        BindingUtils.bindProperty(_certificateSection.certificateKeyPair, "dataProvider", this, "_selectedFiles");

        _validators = [];

        var provider:Provider = _currentIdentityApplianceElement as Provider;
        if (provider != null) {
            if (provider is IdentityProvider) {
                _validators.push(_ipCoreSection.nameValidator);
                _validators.push(_ipCoreSection.portValidator);
                _validators.push(_ipCoreSection.domainValidator);
                _validators.push(_ipCoreSection.contextValidator);
                _validators.push(_ipCoreSection.pathValidator);
                if (_basicAuthenticationSection != null) {
                    _validators.push(_basicAuthenticationSection.nameValidator);
                }
                if (_twoFactorAuthenticationSection != null) {
                    _validators.push(_twoFactorAuthenticationSection.nameValidator);
                }
                if (_bindAuthenticationSection != null) {
                    _validators.push(_bindAuthenticationSection.nameValidator);
                }
            } else if (provider is ServiceProvider) {
                _validators.push(_spCoreSection.nameValidator);
                _validators.push(_spCoreSection.portValidator);
                _validators.push(_spCoreSection.domainValidator);
                _validators.push(_spCoreSection.contextValidator);
                _validators.push(_spCoreSection.pathValidator);
            }
        }

        if (_certificateSection.uploadKeystore.selected) {
            _validators.push(_certificateSection.certificateAliasValidator);
            _validators.push(_certificateSection.keyAliasValidator);
            _validators.push(_certificateSection.keystorePasswordValidator);
            _validators.push(_certificateSection.keyPasswordValidator);
        }
    }

    private function updateSamlR2Config(provider:Provider, config:SamlR2ProviderConfig):void {
        if (_certificateSection.useDefaultKeystore.selected) {
            config.useSampleStore = true;
            config.signer = null;
            config.encrypter = null;
        } else {
            var keystore:Keystore = config.signer;
            if (keystore == null) {
                keystore = new Keystore();
                keystore.name = provider.name.toLowerCase().replace(/\s+/g, "-") + "-keystore";
                keystore.displayName = provider.name + " keystore";
            }
            keystore.certificateAlias = _certificateSection.certificateAlias.text;
            keystore.privateKeyName = _certificateSection.keyAlias.text;
            keystore.privateKeyPassword = _certificateSection.keyPassword.text;
            keystore.password = _certificateSection.keystorePassword.text;
            keystore.type = _certificateSection.keystoreFormat.selectedItem.data;
            if (_uploadedFile != null && _uploadedFileName != null) {
                var resource:Resource = keystore.store;
                if (resource == null) {
                    resource = new Resource();
                }
                resource.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
                resource.displayName = _uploadedFileName;
                resource.uri = _uploadedFileName;
                resource.value = _uploadedFile;
                keystore.store = resource;
            }

            config.useSampleStore = false;
            config.signer = keystore;
            config.encrypter = keystore;
        }

        sendNotification(ApplicationFacade.GET_CERTIFICATE_INFO, config);
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function handleServiceProviderCorePropertyTabCreationComplete(event:Event):void {
        var serviceProvider:ServiceProvider;

        serviceProvider = _currentIdentityApplianceElement as ServiceProvider;

        // if serviceProvider is null that means some other element was selected before completing this
        if (serviceProvider != null) {
            // bind view

            BindingUtils.bindProperty(_spCoreSection.accountLinkagePolicyCombo, "dataProvider", this, "_accountLinkagePolicies");
            sendNotification(ApplicationFacade.LIST_ACCOUNT_LINKAGE_POLICIES);

            BindingUtils.bindProperty(_spCoreSection.identityMappingPolicyCombo, "dataProvider", this, "_identityMappingPolicies");
            sendNotification(ApplicationFacade.LIST_IDENTITY_MAPPING_POLICIES);
            
            _spCoreSection.serviceProvName.text = serviceProvider.name;
            _spCoreSection.serviceProvDescription.text = serviceProvider.description;
            //TODO

            for (var i:int = 0; i < _spCoreSection.spLocationProtocol.dataProvider.length; i++) {
                if (serviceProvider.location != null && _spCoreSection.spLocationProtocol.dataProvider[i].data == serviceProvider.location.protocol) {
                    _spCoreSection.spLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _spCoreSection.spLocationDomain.text = serviceProvider.location.host;
            _spCoreSection.spLocationPort.text = serviceProvider.location.port.toString() != "0" ?
                    serviceProvider.location.port.toString() : "";
            _spCoreSection.spLocationContext.text = serviceProvider.location.context;
            _spCoreSection.spLocationPath.text = serviceProvider.location.uri;

            _spCoreSection.serviceProvName.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.serviceProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            
            _validators = [];
            _validators.push(_spCoreSection.nameValidator);
            _validators.push(_spCoreSection.portValidator);
            _validators.push(_spCoreSection.domainValidator);
            _validators.push(_spCoreSection.contextValidator);
            _validators.push(_spCoreSection.pathValidator);
        }
    }

    private function handleServiceProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var serviceProvider:ServiceProvider;

            serviceProvider = _currentIdentityApplianceElement as ServiceProvider;

            serviceProvider.name = _spCoreSection.serviceProvName.text;
            serviceProvider.description = _spCoreSection.serviceProvDescription.text;

            serviceProvider.location.protocol = _spCoreSection.spLocationProtocol.labelDisplay.text;
            serviceProvider.location.host = _spCoreSection.spLocationDomain.text;
            serviceProvider.location.port = parseInt(_spCoreSection.spLocationPort.text);
            serviceProvider.location.context = _spCoreSection.spLocationContext.text;
            serviceProvider.location.uri = _spCoreSection.spLocationPath.text;

            serviceProvider.accountLinkagePolicy = _spCoreSection.accountLinkagePolicyCombo.selectedItem;
            serviceProvider.identityMappingPolicy = _spCoreSection.identityMappingPolicyCombo.selectedItem;

            // update default idp channels
            if (serviceProvider.federatedConnectionsA != null) {
                for (var i:int = 0; i < serviceProvider.federatedConnectionsA.length; i++) {
                    var idpChannel:IdentityProviderChannel = serviceProvider.federatedConnectionsA[i].channelA as IdentityProviderChannel;
                    if (!idpChannel.overrideProviderSetup) {
                        updateIdentityProviderChannel(idpChannel, serviceProvider);
                    }
                }
            }

            if (serviceProvider.federatedConnectionsB != null) {
                for (var j:int = 0; j < serviceProvider.federatedConnectionsB.length; j++) {
                    var idpChannel2:IdentityProviderChannel = serviceProvider.federatedConnectionsB[j].channelB as IdentityProviderChannel;
                    if (!idpChannel2.overrideProviderSetup) {
                        updateIdentityProviderChannel(idpChannel, serviceProvider);
                    }
                }
            }
            
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleServiceProviderContractPropertyTabCreationComplete(event:Event):void {

        var serviceProvider:ServiceProvider;

        serviceProvider = _currentIdentityApplianceElement as ServiceProvider;

        // if serviceProvider is null that means some other element was selected before completing this
        if (serviceProvider != null) {
            _spContractSection.signAuthnRequestsCheck.selected = serviceProvider.signAuthenticationRequests;
            _spContractSection.wantAssertionSignedCheck.selected = serviceProvider.wantAssertionSigned;
            _spContractSection.signRequestsCheck.selected = serviceProvider.signRequests;
            _spContractSection.wantSignedRequestsCheck.selected = serviceProvider.wantSignedRequests;

            for (var j:int = 0; j < serviceProvider.activeBindings.length; j ++) {
                var tmpBinding:Binding = serviceProvider.activeBindings.getItemAt(j) as Binding;
                if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                    _spContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                    _spContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                    _spContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                    _spContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < serviceProvider.activeProfiles.length; j++) {
                var tmpProfile:Profile = serviceProvider.activeProfiles.getItemAt(j) as Profile;
                if (tmpProfile.name == Profile.SSO.name) {
                    _spContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _spContractSection.samlProfileSLOCheck.selected = true;
                }
            }

            if (_applianceSaved) {
                _spContractSection.btnExportMetadata.enabled = true;
                _spContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }

            _spContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.signAuthnRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.wantAssertionSignedCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.signRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.wantSignedRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleServiceProviderContractPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            var serviceProvider:ServiceProvider;

            serviceProvider = _currentIdentityApplianceElement as ServiceProvider;

            if (serviceProvider.activeBindings == null) {
                serviceProvider.activeBindings = new ArrayCollection();
            }
            serviceProvider.activeBindings.removeAll();
            if (_spContractSection.samlBindingHttpPostCheck.selected) {
                serviceProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_spContractSection.samlBindingArtifactCheck.selected) {
                serviceProvider.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_spContractSection.samlBindingHttpRedirectCheck.selected) {
                serviceProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_spContractSection.samlBindingSoapCheck.selected) {
                serviceProvider.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (serviceProvider.activeProfiles == null) {
                serviceProvider.activeProfiles = new ArrayCollection();
            }
            serviceProvider.activeProfiles.removeAll();
            if (_spContractSection.samlProfileSSOCheck.selected) {
                serviceProvider.activeProfiles.addItem(Profile.SSO);
            }
            if (_spContractSection.samlProfileSLOCheck.selected) {
                serviceProvider.activeProfiles.addItem(Profile.SSO_SLO);
            }

            serviceProvider.signAuthenticationRequests = _spContractSection.signAuthnRequestsCheck.selected;
            serviceProvider.wantAssertionSigned = _spContractSection.wantAssertionSignedCheck.selected;
            serviceProvider.signRequests = _spContractSection.signRequestsCheck.selected;
            serviceProvider.wantSignedRequests = _spContractSection.wantSignedRequestsCheck.selected;
            
            // update default idp channels
            if (serviceProvider.federatedConnectionsA != null) {
                for (var i:int = 0; i < serviceProvider.federatedConnectionsA.length; i++) {
                    var idpChannel:IdentityProviderChannel = serviceProvider.federatedConnectionsA[i].channelA as IdentityProviderChannel;
                    if (!idpChannel.overrideProviderSetup) {
                        updateIdentityProviderChannel(idpChannel, serviceProvider);
                    }
                }
            }

            if (serviceProvider.federatedConnectionsB != null) {
                for (var j:int = 0; j < serviceProvider.federatedConnectionsB.length; j++) {
                    var idpChannel2:IdentityProviderChannel = serviceProvider.federatedConnectionsB[j].channelB as IdentityProviderChannel;
                    if (!idpChannel2.overrideProviderSetup) {
                        updateIdentityProviderChannel(idpChannel2, serviceProvider);
                    }
                }
            }
            
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function updateIdentityProviderChannel(idpChannel:IdentityProviderChannel, serviceProvider:ServiceProvider):void {
        // set location
        if (idpChannel.location == null) {
            idpChannel.location = new Location();
        }
        idpChannel.location.protocol = serviceProvider.location.protocol;
        idpChannel.location.host = serviceProvider.location.host;
        idpChannel.location.port = serviceProvider.location.port;
        idpChannel.location.context = serviceProvider.location.context;
        idpChannel.location.uri = serviceProvider.location.uri;

        // set policies
        idpChannel.accountLinkagePolicy = serviceProvider.accountLinkagePolicy;
        idpChannel.identityMappingPolicy = serviceProvider.identityMappingPolicy;

        // set active bindings
        if (idpChannel.activeBindings == null) {
            idpChannel.activeBindings = new ArrayCollection();
        }
        idpChannel.activeBindings.removeAll();
        for (var i:int = 0; i < serviceProvider.activeBindings.length; i++) {
            idpChannel.activeBindings.addItem(serviceProvider.activeBindings[i]);
        }

        // set active profiles
        if (idpChannel.activeProfiles == null) {
            idpChannel.activeProfiles = new ArrayCollection();
        }
        idpChannel.activeProfiles.removeAll();
        for (var j:int = 0; j < serviceProvider.activeProfiles.length; j++) {
            idpChannel.activeProfiles.addItem(serviceProvider.activeProfiles[j]);
        }

        idpChannel.signAuthenticationRequests = serviceProvider.signAuthenticationRequests;
    }
    
    private function handleProviderCertificatePropertyTabCreationComplete(event:Event):void {
        var provider:Provider = _currentIdentityApplianceElement as Provider;

        // if provider is null that means some other element was selected before completing this
        if (provider != null) {
            resetUploadFields();

            // bind view
            var config:SamlR2ProviderConfig = provider.config as SamlR2ProviderConfig;
            initCertificateSection(config);
        }
    }

    private function handleProviderCertificatePropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            var provider:Provider = _currentIdentityApplianceElement as Provider;
            var config:SamlR2ProviderConfig = provider.config as SamlR2ProviderConfig;

            if (_certificateSection.uploadKeystore.selected && (config.signer == null ||
                    config.signer.store == null)) {
                if (_selectedFiles == null || _selectedFiles.length == 0) {
                    _certificateSection.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.keypair.error");
                    _certificateSection.lblUploadMsg.setStyle("color", "Red");
                    _certificateSection.lblUploadMsg.visible = true;
                    return;
                }
            }

            _certificateSection.lblUploadMsg.text = "";
            _certificateSection.lblUploadMsg.setStyle("color", "Green");
            _certificateSection.lblUploadMsg.visible = false;

            if (_certificateSection.uploadKeystore.selected && _selectedFiles != null && _selectedFiles.length > 0) {
                _fileRef.load();
            } else {
                updateSamlR2Config(provider, config);
            }
        }
    }

    protected function enableExternalIdentityProviderPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _externalIdpCoreSection = new ExternalIdentityProviderCoreSection();
        corePropertyTab.addElement(_externalIdpCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _externalIdpCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalIdentityProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalIdentityProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetMetadataSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _externalIdpContractSection = new ExternalIdentityProviderContractSection();
        contractPropertyTab.addElement(_externalIdpContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _externalIdpContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalIdentityProviderContractPropertyTabCreationComplete);

        // Certificate Tab
        var certificatePropertyTab:Group = new Group();
        certificatePropertyTab.id = "propertySheetMetadataSection";
        certificatePropertyTab.name = "Certificate";
        certificatePropertyTab.width = Number("100%");
        certificatePropertyTab.height = Number("100%");
        certificatePropertyTab.setStyle("borderStyle", "solid");

        _externalIdpCertificateSection = new ExternalIdentityProviderCertificateSection();
        certificatePropertyTab.addElement(_externalIdpCertificateSection);
        _propertySheetsViewStack.addNewChild(certificatePropertyTab);

        var identityProvider:ExternalIdentityProvider = _currentIdentityApplianceElement as ExternalIdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            sendNotification(ApplicationFacade.GET_METADATA_INFO, ["IDPSSO", identityProvider.metadata.value]);
        }
    }

    private function handleExternalIdentityProviderCorePropertyTabCreationComplete(event:Event):void {
        var identityProvider:ExternalIdentityProvider = _currentIdentityApplianceElement as ExternalIdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            resetUploadMetadataFields();
            
            // bind view
            _externalIdpCoreSection.identityProviderName.text = identityProvider.name;
            _externalIdpCoreSection.identityProvDescription.text = identityProvider.description;

            _externalIdpCoreSection.identityProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalIdpCoreSection.identityProvDescription.addEventListener(Event.CHANGE, handleSectionChange);

            _externalIdpCoreSection.metadataFile.addEventListener(MouseEvent.CLICK, browseMetadataHandler);
            BindingUtils.bindProperty(_externalIdpCoreSection.metadataFile, "dataProvider", this, "_selectedMetadataFiles");

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_externalIdpCoreSection.nameValidator);
        }
    }

    private function handleExternalIdentityProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {

            if (_selectedMetadataFiles != null && _selectedMetadataFiles.length > 0) {
                _metadataFileRef.load();
            } else {
                updateExternalIdentityProvider();
            }

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleExternalIdentityProviderContractPropertyTabCreationComplete(event:Event):void {
        var identityProvider:ExternalIdentityProvider = _currentIdentityApplianceElement as ExternalIdentityProvider;

        // if identityProvider is null that means some other element was selected before completing this
        if (identityProvider != null) {
            if (_applianceSaved) {
                _externalIdpContractSection.btnExportMetadata.enabled = true;
                _externalIdpContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }
        }
    }

    private function updateExternalIdentityProvider():void {
        var identityProvider:ExternalIdentityProvider = _currentIdentityApplianceElement as ExternalIdentityProvider;

        identityProvider.name = _externalIdpCoreSection.identityProviderName.text;
        identityProvider.description = _externalIdpCoreSection.identityProvDescription.text;

        if (_uploadedMetadata != null && _uploadedMetadataName != null) {
            var resource:Resource = identityProvider.metadata;
            resource.name = _uploadedMetadataName.substring(0, _uploadedMetadataName.lastIndexOf("."));
            resource.displayName = _uploadedMetadataName;
            resource.uri = _uploadedMetadataName;
            resource.value = _uploadedMetadata;
            identityProvider.metadata = resource;
            sendNotification(ApplicationFacade.GET_METADATA_INFO, ["IDPSSO", _uploadedMetadata]);
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    protected function enableExternalServiceProviderPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _externalSpCoreSection = new ExternalServiceProviderCoreSection();
        corePropertyTab.addElement(_externalSpCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _externalSpCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalServiceProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalServiceProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetMetadataSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _externalSpContractSection = new ExternalServiceProviderContractSection();
        contractPropertyTab.addElement(_externalSpContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _externalSpContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalServiceProviderContractPropertyTabCreationComplete);

        // Certificate Tab
        var certificatePropertyTab:Group = new Group();
        certificatePropertyTab.id = "propertySheetMetadataSection";
        certificatePropertyTab.name = "Certificate";
        certificatePropertyTab.width = Number("100%");
        certificatePropertyTab.height = Number("100%");
        certificatePropertyTab.setStyle("borderStyle", "solid");

        _externalSpCertificateSection = new ExternalServiceProviderCertificateSection();
        certificatePropertyTab.addElement(_externalSpCertificateSection);
        _propertySheetsViewStack.addNewChild(certificatePropertyTab);

        var serviceProvider:ExternalServiceProvider = _currentIdentityApplianceElement as ExternalServiceProvider;

        // if serviceProvider is null that means some other element was selected before completing this
        if (serviceProvider != null) {
            sendNotification(ApplicationFacade.GET_METADATA_INFO, ["SPSSO", serviceProvider.metadata.value]);
        }
    }

    private function handleExternalServiceProviderCorePropertyTabCreationComplete(event:Event):void {
        var serviceProvider:ExternalServiceProvider = _currentIdentityApplianceElement as ExternalServiceProvider;

        // if serviceProvider is null that means some other element was selected before completing this
        if (serviceProvider != null) {
            resetUploadMetadataFields();

            // bind view
            _externalSpCoreSection.serviceProviderName.text = serviceProvider.name;
            _externalSpCoreSection.serviceProvDescription.text = serviceProvider.description;

            _externalSpCoreSection.serviceProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalSpCoreSection.serviceProvDescription.addEventListener(Event.CHANGE, handleSectionChange);

            _externalSpCoreSection.metadataFile.addEventListener(MouseEvent.CLICK, browseMetadataHandler);
            BindingUtils.bindProperty(_externalSpCoreSection.metadataFile, "dataProvider", this, "_selectedMetadataFiles");

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_externalSpCoreSection.nameValidator);
        }
    }

    private function handleExternalServiceProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {

            if (_selectedMetadataFiles != null && _selectedMetadataFiles.length > 0) {
                _metadataFileRef.load();
            } else {
                updateExternalServiceProvider();
            }

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleExternalServiceProviderContractPropertyTabCreationComplete(event:Event):void {
        var serviceProvider:ExternalServiceProvider = _currentIdentityApplianceElement as ExternalServiceProvider;

        // if serviceProvider is null that means some other element was selected before completing this
        if (serviceProvider != null) {
            if (_applianceSaved) {
                _externalSpContractSection.btnExportMetadata.enabled = true;
                _externalSpContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }
        }
    }

    private function updateExternalServiceProvider():void {
        var serviceProvider:ExternalServiceProvider = _currentIdentityApplianceElement as ExternalServiceProvider;

        serviceProvider.name = _externalSpCoreSection.serviceProviderName.text;
        serviceProvider.description = _externalSpCoreSection.serviceProvDescription.text;

        if (_uploadedMetadata != null && _uploadedMetadataName != null) {
            var resource:Resource = serviceProvider.metadata;
            resource.name = _uploadedMetadataName.substring(0, _uploadedMetadataName.lastIndexOf("."));
            resource.displayName = _uploadedMetadataName;
            resource.uri = _uploadedMetadataName;
            resource.value = _uploadedMetadata;
            serviceProvider.metadata = resource;
            sendNotification(ApplicationFacade.GET_METADATA_INFO, ["SPSSO", _uploadedMetadata]);
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    protected function enableSalesforcePropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _salesforceCoreSection = new SalesforceCoreSection();
        corePropertyTab.addElement(_salesforceCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _salesforceCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSalesforceCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSalesforceCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetMetadataSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _salesforceContractSection = new SalesforceContractSection();
        contractPropertyTab.addElement(_salesforceContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _salesforceContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSalesforceContractPropertyTabCreationComplete);
    }

    private function handleSalesforceCorePropertyTabCreationComplete(event:Event):void {
        var salesforceProvider:SalesforceServiceProvider = _currentIdentityApplianceElement as SalesforceServiceProvider;

        // if salesforceProvider is null that means some other element was selected before completing this
        if (salesforceProvider != null) {
            // bind view
            _salesforceCoreSection.salesforceProviderName.text = salesforceProvider.name;
            _salesforceCoreSection.salesforceProvDescription.text = salesforceProvider.description;

            _salesforceCoreSection.salesforceProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _salesforceCoreSection.salesforceProvDescription.addEventListener(Event.CHANGE, handleSectionChange);

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_salesforceCoreSection.nameValidator);
        }
    }

    private function handleSalesforceCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {

            var salesforceProvider:SalesforceServiceProvider = _currentIdentityApplianceElement as SalesforceServiceProvider;

            salesforceProvider.name = _salesforceCoreSection.salesforceProviderName.text;
            salesforceProvider.description = _salesforceCoreSection.salesforceProvDescription.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleSalesforceContractPropertyTabCreationComplete(event:Event):void {
        var salesforceProvider:SalesforceServiceProvider = _currentIdentityApplianceElement as SalesforceServiceProvider;

        // if salesforceProvider is null that means some other element was selected before completing this
        if (salesforceProvider != null) {
            if (_applianceSaved) {
                _salesforceContractSection.btnExportMetadata.enabled = true;
                _salesforceContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }
        }
    }

    protected function enableGoogleAppsPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _googleAppsCoreSection = new GoogleAppsCoreSection();
        corePropertyTab.addElement(_googleAppsCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _googleAppsCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleGoogleAppsCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleGoogleAppsCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetMetadataSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _googleAppsContractSection = new GoogleAppsContractSection();
        contractPropertyTab.addElement(_googleAppsContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _googleAppsContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleGoogleAppsContractPropertyTabCreationComplete);
    }

    private function handleGoogleAppsCorePropertyTabCreationComplete(event:Event):void {
        var googleAppsProvider:GoogleAppsServiceProvider = _currentIdentityApplianceElement as GoogleAppsServiceProvider;

        // if googleAppsProvider is null that means some other element was selected before completing this
        if (googleAppsProvider != null) {
            // bind view
            _googleAppsCoreSection.googleAppsProviderName.text = googleAppsProvider.name;
            _googleAppsCoreSection.googleAppsProvDescription.text = googleAppsProvider.description;
            _googleAppsCoreSection.googleAppsProvDomain.text = googleAppsProvider.domain;

            _googleAppsCoreSection.googleAppsProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _googleAppsCoreSection.googleAppsProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _googleAppsCoreSection.googleAppsProvDomain.addEventListener(Event.CHANGE, handleSectionChange);

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_googleAppsCoreSection.nameValidator);
            _validators.push(_googleAppsCoreSection.domainValidator);
        }
    }

    private function handleGoogleAppsCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {

            var googleAppsProvider:GoogleAppsServiceProvider = _currentIdentityApplianceElement as GoogleAppsServiceProvider;

            googleAppsProvider.name = _googleAppsCoreSection.googleAppsProviderName.text;
            googleAppsProvider.description = _googleAppsCoreSection.googleAppsProvDescription.text;
            googleAppsProvider.domain = _googleAppsCoreSection.googleAppsProvDomain.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleGoogleAppsContractPropertyTabCreationComplete(event:Event):void {
        var googleAppsProvider:GoogleAppsServiceProvider = _currentIdentityApplianceElement as GoogleAppsServiceProvider;

        // if googleAppsProvider is null that means some other element was selected before completing this
        if (googleAppsProvider != null) {
            if (_applianceSaved) {
                _googleAppsContractSection.btnExportMetadata.enabled = true;
                _googleAppsContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }
        }
    }

    protected function enableSugarCRMPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _sugarCRMCoreSection = new SugarCRMCoreSection();
        corePropertyTab.addElement(_sugarCRMCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _sugarCRMCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSugarCRMCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSugarCRMCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetMetadataSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _sugarCRMContractSection = new SugarCRMContractSection();
        contractPropertyTab.addElement(_sugarCRMContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _sugarCRMContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSugarCRMContractPropertyTabCreationComplete);
    }

    private function handleSugarCRMCorePropertyTabCreationComplete(event:Event):void {
        var sugarCRMProvider:SugarCRMServiceProvider = _currentIdentityApplianceElement as SugarCRMServiceProvider;

        // if sugarCRMProvider is null that means some other element was selected before completing this
        if (sugarCRMProvider != null) {
            // bind view
            _sugarCRMCoreSection.sugarCRMProviderName.text = sugarCRMProvider.name;
            _sugarCRMCoreSection.sugarCRMProvDescription.text = sugarCRMProvider.description;
            _sugarCRMCoreSection.sugarCRMProvUrl.text = sugarCRMProvider.url;

            _sugarCRMCoreSection.sugarCRMProviderName.addEventListener(Event.CHANGE, handleSectionChange);
            _sugarCRMCoreSection.sugarCRMProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _sugarCRMCoreSection.sugarCRMProvUrl.addEventListener(Event.CHANGE, handleSectionChange);

            //clear all existing validators and add idp core section validators
            _validators = [];
            _validators.push(_sugarCRMCoreSection.nameValidator);
            _validators.push(_sugarCRMCoreSection.urlValidator);
        }
    }

    private function handleSugarCRMCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {

            var sugarCRMProvider:SugarCRMServiceProvider = _currentIdentityApplianceElement as SugarCRMServiceProvider;

            sugarCRMProvider.name = _sugarCRMCoreSection.sugarCRMProviderName.text;
            sugarCRMProvider.description = _sugarCRMCoreSection.sugarCRMProvDescription.text;
            sugarCRMProvider.url = _sugarCRMCoreSection.sugarCRMProvUrl.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleSugarCRMContractPropertyTabCreationComplete(event:Event):void {
        var sugarCRMProvider:SugarCRMServiceProvider = _currentIdentityApplianceElement as SugarCRMServiceProvider;

        // if sugarCRMProvider is null that means some other element was selected before completing this
        if (sugarCRMProvider != null) {
            if (_applianceSaved) {
                _sugarCRMContractSection.btnExportMetadata.enabled = true;
                _sugarCRMContractSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportMetadataClick);
            }
        }
    }

    protected function enableWikidAuthnServicePropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _wikidAuthnServiceCoreSection = new WikidAuthnServiceCoreSection();
        corePropertyTab.addElement(_wikidAuthnServiceCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _wikidAuthnServiceCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleWikidAuthnServiceCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleWikidAuthnServiceCorePropertyTabRollOut);
    }

    private function handleWikidAuthnServiceCorePropertyTabCreationComplete(event:Event):void {
        var wikidAuthnService:WikidAuthenticationService = _currentIdentityApplianceElement as WikidAuthenticationService;

        // if wikidAuthnService is null that means some other element was selected before completing this
        if (wikidAuthnService != null) {
            // bind view
            _wikidAuthnServiceCoreSection.wikidName.text = wikidAuthnService.name;
            _wikidAuthnServiceCoreSection.wikidDescription.text = wikidAuthnService.description;
            _wikidAuthnServiceCoreSection.serverHost.text = wikidAuthnService.serverHost;
            _wikidAuthnServiceCoreSection.serverPort.text = wikidAuthnService.serverPort.toString();
            _wikidAuthnServiceCoreSection.serverCode.text = wikidAuthnService.serverCode;
            _wikidAuthnServiceCoreSection.caStorePass.text = wikidAuthnService.caStore.password;
            _wikidAuthnServiceCoreSection.wcStorePass.text = wikidAuthnService.wcStore.password;

            _wikidAuthnServiceCoreSection.caStore.addEventListener(MouseEvent.CLICK, wikidCAStoreBrowseHandler);
            BindingUtils.bindProperty(_wikidAuthnServiceCoreSection.caStore, "dataProvider", this, "_selectedWikidCAStores");

            _wikidAuthnServiceCoreSection.wcStore.addEventListener(MouseEvent.CLICK, wikidClientStoreBrowseHandler);
            BindingUtils.bindProperty(_wikidAuthnServiceCoreSection.wcStore, "dataProvider", this, "_selectedWCStores");

            _wikidAuthnServiceCoreSection.wikidName.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.wikidDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.serverHost.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.serverPort.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.serverCode.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.caStore.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.caStorePass.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.wcStore.addEventListener(Event.CHANGE, handleSectionChange);
            _wikidAuthnServiceCoreSection.wcStorePass.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_wikidAuthnServiceCoreSection.nameValidator);
            _validators.push(_wikidAuthnServiceCoreSection.serverHostValidator);
            _validators.push(_wikidAuthnServiceCoreSection.serverPortValidator);
            _validators.push(_wikidAuthnServiceCoreSection.serverCodeValidator);
            _validators.push(_wikidAuthnServiceCoreSection.caStorePassValidator);
            _validators.push(_wikidAuthnServiceCoreSection.wcStorePassValidator);

        }
    }

    private function handleWikidAuthnServiceCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            var wikidAuthnService:WikidAuthenticationService = _currentIdentityApplianceElement as WikidAuthenticationService;

            if (wikidAuthnService.caStore == null && (_selectedWikidCAStores == null || _selectedWikidCAStores.length == 0)) {
                _wikidAuthnServiceCoreSection.lblCAStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.ca.store.upload.error");
                _wikidAuthnServiceCoreSection.lblCAStoreMsg.setStyle("color", "Red");
                _wikidAuthnServiceCoreSection.lblCAStoreMsg.visible = true;
                return;
            }
            if (wikidAuthnService.wcStore == null && (_selectedWCStores == null || _selectedWCStores.length == 0)) {
                _wikidAuthnServiceCoreSection.lblWCStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.wc.store.upload.error");
                _wikidAuthnServiceCoreSection.lblWCStoreMsg.setStyle("color", "Red");
                _wikidAuthnServiceCoreSection.lblWCStoreMsg.visible = true;
                return;
            }

            _wikidAuthnServiceCoreSection.lblCAStoreMsg.text = "";
            _wikidAuthnServiceCoreSection.lblCAStoreMsg.setStyle("color", "Green");
            _wikidAuthnServiceCoreSection.lblCAStoreMsg.visible = false;

            _wikidAuthnServiceCoreSection.lblWCStoreMsg.text = "";
            _wikidAuthnServiceCoreSection.lblWCStoreMsg.setStyle("color", "Green");
            _wikidAuthnServiceCoreSection.lblWCStoreMsg.visible = false;
            
            if (_selectedWikidCAStores != null && _selectedWikidCAStores.length > 0) {
                _wikidCAStoreFileRef.load();
            } else if (_selectedWCStores != null && _selectedWCStores.length > 0) {
                _wikidClientStoreFileRef.load();
            } else {
                saveWikidAuthnService();
            }
        }
    }

    private function saveWikidAuthnService():void {
        var wikidAuthnService:WikidAuthenticationService = _currentIdentityApplianceElement as WikidAuthenticationService;
        
        if (wikidAuthnService != null) {
            
            wikidAuthnService.name = _wikidAuthnServiceCoreSection.wikidName.text;
            wikidAuthnService.description = _wikidAuthnServiceCoreSection.wikidDescription.text;
            wikidAuthnService.serverHost = _wikidAuthnServiceCoreSection.serverHost.text;
            wikidAuthnService.serverPort = parseInt(_wikidAuthnServiceCoreSection.serverPort.text);
            wikidAuthnService.serverCode = _wikidAuthnServiceCoreSection.serverCode.text;

            // CA Store
            var caKeystore:Keystore = wikidAuthnService.caStore;
            if (caKeystore == null) {
                caKeystore = new Keystore();
                caKeystore.name = wikidAuthnService.name.toLowerCase().replace(/\s+/g, "-") + "-ca-store";
                caKeystore.displayName = wikidAuthnService.name + " Certificate Authority Store";
                caKeystore.type = "JKS";
            }
            caKeystore.keystorePassOnly = true;
            caKeystore.password = _wikidAuthnServiceCoreSection.caStorePass.text;
            if (_uploadedWikidCAStoreFile != null && _uploadedWikidCAStoreFileName != null) {
                var caResource:Resource = caKeystore.store;
                if (caResource == null) {
                    caResource = new Resource();
                }
                caResource.name = _uploadedWikidCAStoreFileName;
                caResource.displayName = _uploadedWikidCAStoreFileName;
                caResource.uri = _uploadedWikidCAStoreFileName;
                caResource.value = _uploadedWikidCAStoreFile;
                caKeystore.store = caResource;
            }
            wikidAuthnService.caStore = caKeystore;

            // WC Store
            var wcKeystore:Keystore = wikidAuthnService.wcStore;
            if (wcKeystore == null) {
                wcKeystore = new Keystore();
                wcKeystore.name = wikidAuthnService.name.toLowerCase().replace(/\s+/g, "-") + "-wc-store";
                wcKeystore.displayName = wikidAuthnService.name + " WiKID Client Store";
                wcKeystore.type = "PKCS#12";
            }
            wcKeystore.keystorePassOnly = true;
            wcKeystore.password = _wikidAuthnServiceCoreSection.wcStorePass.text;
            if (_uploadedWCStoreFile != null && _uploadedWCStoreFileName != null) {
                var wcResource:Resource = wcKeystore.store;
                if (wcResource == null) {
                    wcResource = new Resource();
                }
                if (_uploadedWCStoreFileName.lastIndexOf(".") > 0) {
                    wcResource.name = _uploadedWCStoreFileName.substring(0, _uploadedWCStoreFileName.lastIndexOf("."));
                } else {
                    wcResource.name = _uploadedWCStoreFileName;
                }
                wcResource.displayName = _uploadedWCStoreFileName;
                wcResource.uri = _uploadedWCStoreFileName;
                wcResource.value = _uploadedWCStoreFile;
                wcKeystore.store = wcResource;
            }
            wikidAuthnService.wcStore = wcKeystore;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableDirectoryAuthnServicePropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _directoryAuthnServiceCoreSection = new DirectoryAuthnServiceCoreSection();
        corePropertyTab.addElement(_directoryAuthnServiceCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _directoryAuthnServiceCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleDirectoryAuthnServiceCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleDirectoryAuthnServiceCorePropertyTabRollOut);

        var lookupPropertyTab:Group = new Group();
        lookupPropertyTab.id = "propertySheetLookuptSection";
        lookupPropertyTab.name = "Lookup";
        lookupPropertyTab.width = Number("100%");
        lookupPropertyTab.height = Number("100%");
        lookupPropertyTab.setStyle("borderStyle", "solid");

        _directoryAuthnServiceLookupSection = new DirectoryAuthnServiceLookupSection();
        lookupPropertyTab.addElement(_directoryAuthnServiceLookupSection);
        _propertySheetsViewStack.addNewChild(lookupPropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _directoryAuthnServiceLookupSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleDirectoryAuthnServiceLookupPropertyTabCreationComplete);
        lookupPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleDirectoryAuthnServiceLookupPropertyTabRollOut);

    }

    private function handleDirectoryAuthnServiceCorePropertyTabCreationComplete(event:Event):void {
        var directoryAuthnService:DirectoryAuthenticationService = _currentIdentityApplianceElement as DirectoryAuthenticationService;

        // if directoryAuthnService is null that means some other element was selected before completing this
        if (directoryAuthnService != null) {
            // bind view
            _directoryAuthnServiceCoreSection.directoryName.text = directoryAuthnService.name;
            _directoryAuthnServiceCoreSection.description.text = directoryAuthnService.description;

            _directoryAuthnServiceCoreSection.initialContextFactory.text = directoryAuthnService.initialContextFactory;
            _directoryAuthnServiceCoreSection.providerUrl.text = directoryAuthnService.providerUrl;
            _directoryAuthnServiceCoreSection.performDnSearch.selected = directoryAuthnService.performDnSearch;
            _directoryAuthnServiceCoreSection.securityPrincipal.text = directoryAuthnService.securityPrincipal;
            _directoryAuthnServiceCoreSection.securityCredential.text = directoryAuthnService.securityCredential;
            for (var i:int = 0; i < _directoryAuthnServiceCoreSection.securityAuthentication.dataProvider.length; i++) {
                if (_directoryAuthnServiceCoreSection.securityAuthentication.dataProvider[i].data == directoryAuthnService.securityAuthentication) {
                    _directoryAuthnServiceCoreSection.securityAuthentication.selectedIndex = i;
                    break;
                }
            }

            _directoryAuthnServiceCoreSection.directoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.description.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.initialContextFactory.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.providerUrl.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.performDnSearch.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.securityPrincipal.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.securityCredential.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceCoreSection.securityAuthentication.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_directoryAuthnServiceCoreSection.nameValidator);
            _validators.push(_directoryAuthnServiceCoreSection.initialContextFactoryValidator);
            _validators.push(_directoryAuthnServiceCoreSection.providerUrlValidator);
            _validators.push(_directoryAuthnServiceCoreSection.securityPrincipalValidator);
            _validators.push(_directoryAuthnServiceCoreSection.securityCredentialValidator);
        }
    }

    private function handleDirectoryAuthnServiceCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var directoryAuthnService:DirectoryAuthenticationService = _currentIdentityApplianceElement as DirectoryAuthenticationService;
            directoryAuthnService.name = _directoryAuthnServiceCoreSection.directoryName.text;
            directoryAuthnService.description = _directoryAuthnServiceCoreSection.description.text;
            directoryAuthnService.initialContextFactory = _directoryAuthnServiceCoreSection.initialContextFactory.text;
            directoryAuthnService.providerUrl = _directoryAuthnServiceCoreSection.providerUrl.text;
            directoryAuthnService.performDnSearch = _directoryAuthnServiceCoreSection.performDnSearch.selected;
            directoryAuthnService.securityPrincipal = _directoryAuthnServiceCoreSection.securityPrincipal.text;
            directoryAuthnService.securityCredential = _directoryAuthnServiceCoreSection.securityCredential.text;
            directoryAuthnService.securityAuthentication = _directoryAuthnServiceCoreSection.securityAuthentication.selectedItem.data;
            
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleDirectoryAuthnServiceLookupPropertyTabCreationComplete(event:Event):void {

        var directoryAuthnService:DirectoryAuthenticationService = _currentIdentityApplianceElement as DirectoryAuthenticationService;

        // if directoryAuthnService is null that means some other element was selected before completing this
        if (directoryAuthnService != null) {
            _directoryAuthnServiceLookupSection.usersCtxDN.text = directoryAuthnService.usersCtxDN;
            _directoryAuthnServiceLookupSection.principalUidAttributeID.text = directoryAuthnService.principalUidAttributeID;

            for (var j:int = 0; j < _directoryAuthnServiceLookupSection.ldapSearchScope.dataProvider.length; j++) {
                if (_directoryAuthnServiceLookupSection.ldapSearchScope.dataProvider[j].data == directoryAuthnService.ldapSearchScope) {
                    _directoryAuthnServiceLookupSection.ldapSearchScope.selectedIndex = j;
                    break;
                }
            }

            _directoryAuthnServiceLookupSection.usersCtxDN.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceLookupSection.principalUidAttributeID.addEventListener(Event.CHANGE, handleSectionChange);
            _directoryAuthnServiceLookupSection.ldapSearchScope.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_directoryAuthnServiceLookupSection.usersCtxDNValidator);
            _validators.push(_directoryAuthnServiceLookupSection.principalUidAttributeIDValidator);
        }
    }

    private function handleDirectoryAuthnServiceLookupPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var directoryAuthnService:DirectoryAuthenticationService = _currentIdentityApplianceElement as DirectoryAuthenticationService;
            directoryAuthnService.usersCtxDN = _directoryAuthnServiceLookupSection.usersCtxDN.text;
            directoryAuthnService.principalUidAttributeID = _directoryAuthnServiceLookupSection.principalUidAttributeID.text;
            directoryAuthnService.ldapSearchScope= _directoryAuthnServiceLookupSection.ldapSearchScope.selectedItem.data;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableIdentityVaultPropertyTabs():void {
        // Attach embedded DB identity vault editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _embeddedDbVaultCoreSection = new EmbeddedDBIdentityVaultCoreSection();
        corePropertyTab.addElement(_embeddedDbVaultCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _embeddedDbVaultCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityVaultCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityVaultCorePropertyTabRollOut);
    }

    private function handleIdentityVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:EmbeddedIdentitySource = _currentIdentityApplianceElement as EmbeddedIdentitySource;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            _embeddedDbVaultCoreSection.userRepositoryName.text = dbIdentityVault.name;
            _embeddedDbVaultCoreSection.description.text = dbIdentityVault.description;
            _embeddedDbVaultCoreSection.idau.text = dbIdentityVault.idau
            _embeddedDbVaultCoreSection.psp.text = dbIdentityVault.psp;
            _embeddedDbVaultCoreSection.pspTarget.text = dbIdentityVault.pspTarget;

            _embeddedDbVaultCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.description.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.idau.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.psp.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.pspTarget.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_embeddedDbVaultCoreSection.nameValidator);
            _validators.push(_embeddedDbVaultCoreSection.idauValidator);
            _validators.push(_embeddedDbVaultCoreSection.pspValidator);
            _validators.push(_embeddedDbVaultCoreSection.pspTargetValidator);
        }
    }

    private function handleIdentityVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var dbIdentityVault:EmbeddedIdentitySource;

            dbIdentityVault = _currentIdentityApplianceElement as EmbeddedIdentitySource;
            dbIdentityVault.name = _embeddedDbVaultCoreSection.userRepositoryName.text;
            dbIdentityVault.description = _embeddedDbVaultCoreSection.description.text;
            dbIdentityVault.idau = _embeddedDbVaultCoreSection.idau.text;
            dbIdentityVault.psp = _embeddedDbVaultCoreSection.psp.text;
            dbIdentityVault.pspTarget = _embeddedDbVaultCoreSection.pspTarget.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableExternalDbVaultPropertyTabs():void {
        // Attach external DB identity vault editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _externalDbVaultCoreSection = new ExternalDBIdentityVaultCoreSection();
        corePropertyTab.addElement(_externalDbVaultCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _externalDbVaultCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalDbVaultCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalDbVaultCorePropertyTabRollOut);

        // Lookup Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Lookup";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _externalDbVaultLookupSection = new ExternalDBIdentityVaultLookupSection();
        contractPropertyTab.addElement(_externalDbVaultLookupSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _externalDbVaultLookupSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalDbVaultLookupPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalDbVaultLookupPropertyTabRollOut);
    }

    private function handleExternalDbVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentitySource = _currentIdentityApplianceElement as DbIdentitySource;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            //resetUploadDriverFields();

            _externalDbVaultCoreSection.userRepositoryName.text = dbIdentityVault.name;
            //_externalDbVaultCoreSection.driverName.text = dbIdentityVault.driverName;
            /*if (_externalDbVaultCoreSection.driver.dataProvider != null) {
                for (var i:int = 0; i < _externalDbVaultCoreSection.driver.dataProvider.length; i++) {
                    if (_externalDbVaultCoreSection.driver.dataProvider[i].className == dbIdentityVault.driverName) {
                        _externalDbVaultCoreSection.driver.selectedIndex = i;
                        break;
                    }
                }
            }*/
            _externalDbVaultCoreSection.connectionUrl.text = dbIdentityVault.connectionUrl;
            _externalDbVaultCoreSection.dbUsername.text = dbIdentityVault.admin;
            _externalDbVaultCoreSection.dbPassword.text = dbIdentityVault.password;

            _externalDbVaultCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.driver.addEventListener(Event.CHANGE, handleSectionChange);
            //_externalDbVaultCoreSection.driverName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.connectionUrl.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbUsername.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbPassword.addEventListener(Event.CHANGE, handleSectionChange);

            //_externalDbVaultCoreSection.driver.addEventListener(MouseEvent.CLICK, browseDriverHandler);
            //BindingUtils.bindProperty(_externalDbVaultCoreSection.driver, "dataProvider", this, "_selectedDriverFiles");

            BindingUtils.bindProperty(_externalDbVaultCoreSection.driver, "dataProvider", this, "_jdbcDrivers");
            _externalDbVaultCoreSection.driver.addEventListener(Event.CHANGE, handleDriverChange);
            sendNotification(ApplicationFacade.LIST_JDBC_DRIVERS);

            _validators = [];
            _validators.push(_externalDbVaultCoreSection.nameValidator);
            _validators.push(_externalDbVaultCoreSection.driverValidator);
            _validators.push(_externalDbVaultCoreSection.connUrlValidator);
            _validators.push(_externalDbVaultCoreSection.dbUsernameValidator);
            _validators.push(_externalDbVaultCoreSection.dbPasswordValidator);
        }
    }

    private function handleDriverChange(event:Event):void {
        _externalDbVaultCoreSection.connectionUrl.text = _externalDbVaultCoreSection.driver.selectedItem.defaultUrl;
    }

    private function handleExternalDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            //if (_selectedDriverFiles != null && _selectedDriverFiles.length > 0) {
            //    _driverFileRef.load();
            //} else {
                updateDbIdentitySource();
            //}
        }
    }

    private function updateDbIdentitySource():void {
        var dbIdentityVault:DbIdentitySource = _currentIdentityApplianceElement as DbIdentitySource;
        
        dbIdentityVault.name = _externalDbVaultCoreSection.userRepositoryName.text;
        //dbIdentityVault.driverName = _externalDbVaultCoreSection.driverName.text;
        dbIdentityVault.driverName = _externalDbVaultCoreSection.driver.selectedItem.className;
        dbIdentityVault.connectionUrl = _externalDbVaultCoreSection.connectionUrl.text;
        dbIdentityVault.admin = _externalDbVaultCoreSection.dbUsername.text;
        dbIdentityVault.password = _externalDbVaultCoreSection.dbPassword.text;

        /*if (_uploadedDriver != null && _uploadedDriverName != null) {
            var driver:Resource = dbIdentityVault.driver;
            driver.name = _uploadedDriverName.substring(0, _uploadedDriverName.lastIndexOf("."));
            driver.displayName = _uploadedDriverName;
            driver.uri = _uploadedDriverName;
            driver.value = _uploadedDriver;
            dbIdentityVault.driver = driver;
        }*/

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function handleExternalDbVaultLookupPropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentitySource = _currentIdentityApplianceElement as DbIdentitySource;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            _externalDbVaultLookupSection.userQuery.text = dbIdentityVault.userQueryString;
            _externalDbVaultLookupSection.rolesQuery.text = dbIdentityVault.rolesQueryString;
            _externalDbVaultLookupSection.credentialsQuery.text = dbIdentityVault.credentialsQueryString;
            _externalDbVaultLookupSection.propertiesQuery.text = dbIdentityVault.userPropertiesQueryString;
            _externalDbVaultLookupSection.credentialsUpdate.text = dbIdentityVault.resetCredentialDml;
            _externalDbVaultLookupSection.relayCredentialQuery.text = dbIdentityVault.relayCredentialQueryString;

            _externalDbVaultLookupSection.userQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.credentialsQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.rolesQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.propertiesQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.credentialsUpdate.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.relayCredentialQuery.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleExternalDbVaultLookupPropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var dbIdentityVault:DbIdentitySource;
            dbIdentityVault = _currentIdentityApplianceElement as DbIdentitySource;

            dbIdentityVault.userQueryString = _externalDbVaultLookupSection.userQuery.text;
            dbIdentityVault.rolesQueryString = _externalDbVaultLookupSection.rolesQuery.text;
            dbIdentityVault.credentialsQueryString =  _externalDbVaultLookupSection.credentialsQuery.text;
            dbIdentityVault.userPropertiesQueryString = _externalDbVaultLookupSection.propertiesQuery.text;
            dbIdentityVault.resetCredentialDml = _externalDbVaultLookupSection.credentialsUpdate.text;
            dbIdentityVault.relayCredentialQueryString = _externalDbVaultLookupSection.relayCredentialQuery.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

   protected function enableLdapIdentitySourcePropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

       // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _ldapIdentitySourceCoreSection = new LdapIdentitySourceCoreSection();
        corePropertyTab.addElement(_ldapIdentitySourceCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _ldapIdentitySourceCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleLdapIdentitySourceCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleLdapIdentitySourceCorePropertyTabRollOut);

        // Lookup Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Lookup";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _ldapIdentitySourceLookupSection = new LdapIdentitySourceLookupSection();
        contractPropertyTab.addElement(_ldapIdentitySourceLookupSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _ldapIdentitySourceLookupSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleLdapIdentitySourceLookupPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleLdapIdentitySourceLookupPropertyTabRollOut);
    }

    private function handleLdapIdentitySourceCorePropertyTabCreationComplete(event:Event):void {
        var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;

        // if ldapIdentitySource is null that means some other element was selected before completing this
        if (ldapIdentitySource != null) {
            // bind view
            _ldapIdentitySourceCoreSection.userRepositoryName.text = ldapIdentitySource.name;
            _ldapIdentitySourceCoreSection.description.text = ldapIdentitySource.description;
            _ldapIdentitySourceCoreSection.initialContextFactory.text = ldapIdentitySource.initialContextFactory;
            _ldapIdentitySourceCoreSection.providerUrl.text = ldapIdentitySource.providerUrl;
            _ldapIdentitySourceCoreSection.securityPrincipal.text = ldapIdentitySource.securityPrincipal;
            _ldapIdentitySourceCoreSection.securityCredential.text = ldapIdentitySource.securityCredential;
            for (var i:int = 0; i < _ldapIdentitySourceCoreSection.securityAuthentication.dataProvider.length; i++) {
                if (_ldapIdentitySourceCoreSection.securityAuthentication.dataProvider[i].data == ldapIdentitySource.securityAuthentication) {
                    _ldapIdentitySourceCoreSection.securityAuthentication.selectedIndex = i;
                    break;
                }
            }
            for (var j:int = 0; j < _ldapIdentitySourceCoreSection.ldapSearchScope.dataProvider.length; j++) {
                if (_ldapIdentitySourceCoreSection.ldapSearchScope.dataProvider[j].data == ldapIdentitySource.ldapSearchScope) {
                    _ldapIdentitySourceCoreSection.ldapSearchScope.selectedIndex = j;
                    break;
                }
            }

            _ldapIdentitySourceCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.description.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.initialContextFactory.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.providerUrl.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.securityPrincipal.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.securityCredential.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.securityAuthentication.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceCoreSection.ldapSearchScope.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_ldapIdentitySourceCoreSection.nameValidator);
            _validators.push(_ldapIdentitySourceCoreSection.initialContextFactoryValidator);
            _validators.push(_ldapIdentitySourceCoreSection.providerUrlValidator);
            _validators.push(_ldapIdentitySourceCoreSection.securityPrincipalValidator);
            _validators.push(_ldapIdentitySourceCoreSection.securityCredentialValidator);
        }
    }

    private function handleLdapIdentitySourceCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;
            ldapIdentitySource.name = _ldapIdentitySourceCoreSection.userRepositoryName.text;
            ldapIdentitySource.description = _ldapIdentitySourceCoreSection.description.text;
            ldapIdentitySource.initialContextFactory = _ldapIdentitySourceCoreSection.initialContextFactory.text;
            ldapIdentitySource.providerUrl = _ldapIdentitySourceCoreSection.providerUrl.text;
            ldapIdentitySource.securityPrincipal = _ldapIdentitySourceCoreSection.securityPrincipal.text;
            ldapIdentitySource.securityCredential = _ldapIdentitySourceCoreSection.securityCredential.text;
            ldapIdentitySource.securityAuthentication = _ldapIdentitySourceCoreSection.securityAuthentication.selectedItem.data;
            ldapIdentitySource.ldapSearchScope = _ldapIdentitySourceCoreSection.ldapSearchScope.selectedItem.data;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleLdapIdentitySourceLookupPropertyTabCreationComplete(event:Event):void {
        var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;

        // if ldapIdentitySource is null that means some other element was selected before completing this
        if (ldapIdentitySource != null) {
            _ldapIdentitySourceLookupSection.usersCtxDN.text = ldapIdentitySource.usersCtxDN;
            _ldapIdentitySourceLookupSection.principalUidAttributeID.text = ldapIdentitySource.principalUidAttributeID;

            for (var i:int = 0; i < _ldapIdentitySourceLookupSection.roleMatchingMode.dataProvider.length; i++) {
                if (_ldapIdentitySourceLookupSection.roleMatchingMode.dataProvider[i].data == ldapIdentitySource.roleMatchingMode) {
                    _ldapIdentitySourceLookupSection.roleMatchingMode.selectedIndex = i;
                    break;
                }
            }

            _ldapIdentitySourceLookupSection.uidAttributeID.text = ldapIdentitySource.uidAttributeID;
            _ldapIdentitySourceLookupSection.rolesCtxDN.text = ldapIdentitySource.rolesCtxDN;
            _ldapIdentitySourceLookupSection.roleAttributeID.text = ldapIdentitySource.roleAttributeID;
            _ldapIdentitySourceLookupSection.credentialQueryString.text = ldapIdentitySource.credentialQueryString;
            _ldapIdentitySourceLookupSection.updateableCredentialAttribute.text = ldapIdentitySource.updateableCredentialAttribute;
            _ldapIdentitySourceLookupSection.userPropertiesQueryString.text = ldapIdentitySource.userPropertiesQueryString;

            _ldapIdentitySourceLookupSection.usersCtxDN.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.principalUidAttributeID.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.roleMatchingMode.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.uidAttributeID.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.rolesCtxDN.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.roleAttributeID.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.credentialQueryString.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.updateableCredentialAttribute.addEventListener(Event.CHANGE, handleSectionChange);
            _ldapIdentitySourceLookupSection.userPropertiesQueryString.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_ldapIdentitySourceLookupSection.usersCtxDNValidator);
            _validators.push(_ldapIdentitySourceLookupSection.principalUidAttributeIDValidator);
            _validators.push(_ldapIdentitySourceLookupSection.uidAttributeIDValidator);
            _validators.push(_ldapIdentitySourceLookupSection.rolesCtxDNValidator);
            _validators.push(_ldapIdentitySourceLookupSection.roleAttributeIDValidator);
            _validators.push(_ldapIdentitySourceLookupSection.credentialQueryStringValidator);
            _validators.push(_ldapIdentitySourceLookupSection.userPropertiesQueryStringValidator);
        }
    }

    private function handleLdapIdentitySourceLookupPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;
            ldapIdentitySource.usersCtxDN = _ldapIdentitySourceLookupSection.usersCtxDN.text;
            ldapIdentitySource.principalUidAttributeID = _ldapIdentitySourceLookupSection.principalUidAttributeID.text;
            ldapIdentitySource.roleMatchingMode = _ldapIdentitySourceLookupSection.roleMatchingMode.selectedItem.data as String;
            ldapIdentitySource.uidAttributeID = _ldapIdentitySourceLookupSection.uidAttributeID.text;
            ldapIdentitySource.rolesCtxDN = _ldapIdentitySourceLookupSection.rolesCtxDN.text;
            ldapIdentitySource.roleAttributeID = _ldapIdentitySourceLookupSection.roleAttributeID.text;
            ldapIdentitySource.credentialQueryString = _ldapIdentitySourceLookupSection.credentialQueryString.text;
            ldapIdentitySource.updateableCredentialAttribute = _ldapIdentitySourceLookupSection.updateableCredentialAttribute.text;
            ldapIdentitySource.userPropertiesQueryString = _ldapIdentitySourceLookupSection.userPropertiesQueryString.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableXmlIdentitySourcePropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _xmlIdentitySourceCoreSection = new XmlIdentitySourceCoreSection();
        corePropertyTab.addElement(_xmlIdentitySourceCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _xmlIdentitySourceCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleXmlIdentitySourceCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleXmlIdentitySourceCorePropertyTabRollOut);
    }

    private function handleXmlIdentitySourceCorePropertyTabCreationComplete(event:Event):void {
        var xmlIdentitySource:XmlIdentitySource;

        xmlIdentitySource = _currentIdentityApplianceElement as XmlIdentitySource;

        // if xmlIdentitySource is null that means some other element was selected before completing this
        if (xmlIdentitySource != null) {
            // bind view
            _xmlIdentitySourceCoreSection.userRepositoryName.text = xmlIdentitySource.name;
            _xmlIdentitySourceCoreSection.description.text = xmlIdentitySource.description
            _xmlIdentitySourceCoreSection.xmlUrl.text = xmlIdentitySource.xmlUrl;

            _xmlIdentitySourceCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _xmlIdentitySourceCoreSection.description.addEventListener(Event.CHANGE, handleSectionChange);
            _xmlIdentitySourceCoreSection.xmlUrl.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_xmlIdentitySourceCoreSection.nameValidator);
            _validators.push(_xmlIdentitySourceCoreSection.xmlValidator);
        }
    }

    private function handleXmlIdentitySourceCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var xmlIdentitySource:XmlIdentitySource;

            xmlIdentitySource = _currentIdentityApplianceElement as XmlIdentitySource;
            xmlIdentitySource.name = _xmlIdentitySourceCoreSection.userRepositoryName.text;
            xmlIdentitySource.description = _xmlIdentitySourceCoreSection.description.text;
            xmlIdentitySource.xmlUrl = _xmlIdentitySourceCoreSection.xmlUrl.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableFederatedConnectionPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _federatedConnectionCoreSection = new FederatedConnectionCoreSection();
        corePropertyTab.addElement(_federatedConnectionCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _federatedConnectionCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleFederatedConnectionCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleFederatedConnectionCorePropertyTabRollOut);

        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;

        // SP Channel Tab
        if (connection.roleA is IdentityProvider || connection.roleB is IdentityProvider) {
            var spChannelPropertyTab:Group = new Group();
            spChannelPropertyTab.id = "propertySheetSPChannelSection";
            spChannelPropertyTab.name = "SP Channel";
            spChannelPropertyTab.width = Number("100%");
            spChannelPropertyTab.height = Number("100%");
            spChannelPropertyTab.setStyle("borderStyle", "solid");

            _federatedConnectionSPChannelSection = new FederatedConnectionSPChannelSection();
            spChannelPropertyTab.addElement(_federatedConnectionSPChannelSection);
            _propertySheetsViewStack.addNewChild(spChannelPropertyTab);
            _federatedConnectionSPChannelSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleFederatedConnectionSpChannelPropertyTabCreationComplete);
            spChannelPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleFederatedConnectionSpChannelPropertyTabRollOut);
        }

        //IDP Channel Tab
        if (connection.roleA is ServiceProvider || connection.roleB is ServiceProvider) {
            var idpChannelPropertyTab:Group = new Group();
            idpChannelPropertyTab.id = "propertySheetIDPChannelSection";
            idpChannelPropertyTab.name = "IDP Channel";
            idpChannelPropertyTab.width = Number("100%");
            idpChannelPropertyTab.height = Number("100%");
            idpChannelPropertyTab.setStyle("borderStyle", "solid");

            _federatedConnectionIDPChannelSection = new FederatedConnectionIDPChannelSection();
            idpChannelPropertyTab.addElement(_federatedConnectionIDPChannelSection);
            _propertySheetsViewStack.addNewChild(idpChannelPropertyTab);
            _federatedConnectionIDPChannelSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleFederatedConnectionIdpChannelPropertyTabCreationComplete);
            idpChannelPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleFederatedConnectionIdpChannelPropertyTabRollOut);
        }
    }

    private function handleFederatedConnectionCorePropertyTabCreationComplete(event:Event):void {
        var connection:Connection = projectProxy.currentIdentityApplianceElement as FederatedConnection;

        if (connection != null) {
            // bind view
            _federatedConnectionCoreSection.connectionName.text = connection.name;
            _federatedConnectionCoreSection.connectionDescription.text = connection.description;

            _federatedConnectionCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_federatedConnectionCoreSection.nameValidator);
        }
    }

    private function handleFederatedConnectionCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var connection:Connection = projectProxy.currentIdentityApplianceElement as FederatedConnection;

            connection.name = _federatedConnectionCoreSection.connectionName.text;
            connection.description = _federatedConnectionCoreSection.connectionDescription.text;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleFederatedConnectionSpChannelPropertyTabCreationComplete(event:Event):void {
        var spChannel:ServiceProviderChannel;

        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
        if (connection != null) {
            if(connection.channelA is ServiceProviderChannel){
                spChannel = connection.channelA as ServiceProviderChannel;
            } else if (connection.channelB is ServiceProviderChannel){
                spChannel = connection.channelB as ServiceProviderChannel;
            }
        }

        // if spChannel is null that means some other element was selected before completing this
        if (spChannel != null) {

            _federatedConnectionSPChannelSection.useInheritedIDPSettings.addEventListener(Event.CHANGE, handleUseInheritedIDPSettingsChange);
            _federatedConnectionSPChannelSection.useInheritedIDPSettings.selected = !spChannel.overrideProviderSetup;
            reflectIdpSettingsInSpChannelTab();

            if (spChannel.overrideProviderSetup) {
                _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.selected = false;
                _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.selected = false;
                _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.selected = false;
                _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.selected = false;
                _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected = false;
                _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected = false;
                _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.selected = false;
            }
            for each (var tmpBinding:Binding in spChannel.activeBindings) {
                if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.selected = true;
                }
            }
            for each(var tmpProfile:Profile in spChannel.activeProfiles) {
                if (tmpProfile.name == Profile.SSO.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected = true;
                }
            }

            // set location
            if (spChannel.location != null) {
                for (var i:int = 0; i < _federatedConnectionSPChannelSection.spChannelLocationProtocol.dataProvider.length; i++) {
                    if (_federatedConnectionSPChannelSection.spChannelLocationProtocol.dataProvider[i].data == spChannel.location.protocol) {
                        _federatedConnectionSPChannelSection.spChannelLocationProtocol.selectedIndex = i;
                        break;
                    }
                }
                _federatedConnectionSPChannelSection.spChannelLocationDomain.text = spChannel.location.host;
                _federatedConnectionSPChannelSection.spChannelLocationPort.text = spChannel.location.port.toString() != "0" ?
                        spChannel.location.port.toString() : "";
                _federatedConnectionSPChannelSection.spChannelLocationContext.text = spChannel.location.context;
                _federatedConnectionSPChannelSection.spChannelLocationPath.text = spChannel.location.uri;
            }

            _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.selected = spChannel.wantAuthnRequestsSigned;

            if (spChannel.overrideProviderSetup) {
                _federatedConnectionSPChannelSection.useInheritedIDPSettings.selected = false;
            }
            setSpChannelFields();

            if (_applianceSaved) {
                _federatedConnectionSPChannelSection.btnExportMetadata.enabled = true;
                _federatedConnectionSPChannelSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportSPChannelMetadataClick);
            }

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelAuthContractCombo.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelAuthMechanism.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelAuthAssertionEmissionPolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);

            //clear all existing validators and add sp channel section validators
            if (spChannel.overrideProviderSetup) {
                _validators = [];
                _validators.push(_federatedConnectionSPChannelSection.portValidator);
                _validators.push(_federatedConnectionSPChannelSection.domainValidator);
                _validators.push(_federatedConnectionSPChannelSection.contextValidator);
                _validators.push(_federatedConnectionSPChannelSection.pathValidator);
            }
        }
    }

    private function handleFederatedConnectionSpChannelPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            var spChannel:ServiceProviderChannel;

            var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
            if(connection.channelA is ServiceProviderChannel){
                spChannel = connection.channelA as ServiceProviderChannel;
            } else if (connection.channelB is ServiceProviderChannel){
                spChannel = connection.channelB as ServiceProviderChannel;
            }
            spChannel.overrideProviderSetup = !_federatedConnectionSPChannelSection.useInheritedIDPSettings.selected;

            if (spChannel.activeBindings == null) {
                spChannel.activeBindings = new ArrayCollection();
            }
            spChannel.activeBindings.removeAll();
            if (_federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (spChannel.activeProfiles == null) {
                spChannel.activeProfiles = new ArrayCollection();
            }
            spChannel.activeProfiles.removeAll();
            if (_federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            if (spChannel.location == null) {
                spChannel.location = new Location();
            }
            spChannel.location.protocol = _federatedConnectionSPChannelSection.spChannelLocationProtocol.labelDisplay.text;
            spChannel.location.host = _federatedConnectionSPChannelSection.spChannelLocationDomain.text;
            spChannel.location.port = parseInt(_federatedConnectionSPChannelSection.spChannelLocationPort.text);
            spChannel.location.context = _federatedConnectionSPChannelSection.spChannelLocationContext.text;
            spChannel.location.uri = _federatedConnectionSPChannelSection.spChannelLocationPath.text;

            spChannel.wantAuthnRequestsSigned = _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.selected;
            
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleFederatedConnectionIdpChannelPropertyTabCreationComplete(event:Event):void {
        var idpChannel:IdentityProviderChannel;

        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
        if (connection != null) {
            if(connection.channelA is IdentityProviderChannel){
                idpChannel = connection.channelA as IdentityProviderChannel;
            } else if (connection.channelB is IdentityProviderChannel){
                idpChannel = connection.channelB as IdentityProviderChannel;
            }
        }

        // if idpChannel is null that means some other element was selected before completing this
        if (idpChannel != null) {

            _federatedConnectionIDPChannelSection.useInheritedSPSettings.addEventListener(Event.CHANGE, handleUseInheritedSPSettingsChange);
            _federatedConnectionIDPChannelSection.preferredIDPChannel.selected = idpChannel.preferred;
            _federatedConnectionIDPChannelSection.useInheritedSPSettings.selected = !idpChannel.overrideProviderSetup;
            reflectSPSettingsInIdpChannelTab();

            BindingUtils.bindProperty(_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo, "dataProvider", this, "_accountLinkagePolicies");
            sendNotification(ApplicationFacade.LIST_ACCOUNT_LINKAGE_POLICIES);

            BindingUtils.bindProperty(_federatedConnectionIDPChannelSection.identityMappingPolicyCombo, "dataProvider", this, "_identityMappingPolicies");
            sendNotification(ApplicationFacade.LIST_IDENTITY_MAPPING_POLICIES);
            
            if (idpChannel.overrideProviderSetup) {
                _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = false;
                _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.selected = false;
                _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.selected = false;
            }
            for each(var tmpBinding:Binding in idpChannel.activeBindings) {
                if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                    _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                    _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                    _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                    _federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected = true;
                }
            }
            for each(var tmpProfile:Profile in idpChannel.activeProfiles) {
                if (tmpProfile.name == Profile.SSO.name) {
                    _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = true;
                }
            }

            // set location
            if (idpChannel.location != null) {
                for (var i:int = 0; i < _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider.length; i++) {
                    if (_federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider[i].data == idpChannel.location.protocol) {
                        _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.selectedIndex = i;
                        break;
                    }
                }
                _federatedConnectionIDPChannelSection.idpChannelLocationDomain.text = idpChannel.location.host;
                _federatedConnectionIDPChannelSection.idpChannelLocationPort.text = idpChannel.location.port.toString() != "0" ?
                        idpChannel.location.port.toString() : "";
                _federatedConnectionIDPChannelSection.idpChannelLocationContext.text = idpChannel.location.context;
                _federatedConnectionIDPChannelSection.idpChannelLocationPath.text = idpChannel.location.uri;
            }

            _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.selected = idpChannel.signAuthenticationRequests;
            _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.selected = idpChannel.wantAssertionSigned;
            
            if (idpChannel.overrideProviderSetup) {
                _federatedConnectionIDPChannelSection.useInheritedSPSettings.selected = false;
            }
            setIdpChannelFields();

            if (_applianceSaved) {
                _federatedConnectionIDPChannelSection.btnExportMetadata.enabled = true;
                _federatedConnectionIDPChannelSection.btnExportMetadata.addEventListener(MouseEvent.CLICK, handleExportIDPChannelMetadataClick);
            }

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.addEventListener(Event.CHANGE, handleSectionChange);
            
            //clear all existing validators and add idp channel section validators
            if (idpChannel.overrideProviderSetup) {
                _validators = [];
                _validators.push(_federatedConnectionIDPChannelSection.portValidator);
                _validators.push(_federatedConnectionIDPChannelSection.domainValidator);
                _validators.push(_federatedConnectionIDPChannelSection.contextValidator);
                _validators.push(_federatedConnectionIDPChannelSection.pathValidator);
            }
        }
    }

    private function handleFederatedConnectionIdpChannelPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var idpChannel:IdentityProviderChannel;
            var sp:ServiceProvider;

            var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
            if(connection.channelA is IdentityProviderChannel){
                idpChannel = connection.channelA as IdentityProviderChannel;
                sp = connection.roleA as ServiceProvider;
            } else if (connection.channelB is IdentityProviderChannel){
                idpChannel = connection.channelB as IdentityProviderChannel;
                sp = connection.roleB as ServiceProvider;
            }

            idpChannel.preferred = _federatedConnectionIDPChannelSection.preferredIDPChannel.selected;
            if(idpChannel.preferred){
                //if idpchannel is preferred, go through all the idp channels in a SP and deselect previously preferred
                for each(var conn:FederatedConnection in sp.federatedConnectionsA){
                    if(conn.channelA != null && conn.channelA != idpChannel){
                        (conn.channelA as IdentityProviderChannel).preferred = false;
                    }
                }
                for each(conn in sp.federatedConnectionsB){
                    if(conn.channelB != null && conn.channelB != idpChannel){
                        (conn.channelB as IdentityProviderChannel).preferred = false;
                    }
                }
            }


            idpChannel.overrideProviderSetup = !_federatedConnectionIDPChannelSection.useInheritedSPSettings.selected;

            if (idpChannel.activeBindings == null) {
                idpChannel.activeBindings = new ArrayCollection();
            }
            idpChannel.activeBindings.removeAll();
            if (_federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (idpChannel.activeProfiles == null) {
                idpChannel.activeProfiles = new ArrayCollection();
            }
            idpChannel.activeProfiles.removeAll();
            if (_federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            if (idpChannel.location == null) {
                idpChannel.location = new Location();
            }
            idpChannel.location.protocol = _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.labelDisplay.text;
            idpChannel.location.host = _federatedConnectionIDPChannelSection.idpChannelLocationDomain.text;
            idpChannel.location.port = parseInt(_federatedConnectionIDPChannelSection.idpChannelLocationPort.text);
            idpChannel.location.context = _federatedConnectionIDPChannelSection.idpChannelLocationContext.text;
            idpChannel.location.uri = _federatedConnectionIDPChannelSection.idpChannelLocationPath.text;

            idpChannel.signAuthenticationRequests = _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.selected;
            idpChannel.wantAssertionSigned = _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.selected;

            if (!idpChannel.overrideProviderSetup) {
                idpChannel.accountLinkagePolicy = sp.accountLinkagePolicy;
                idpChannel.identityMappingPolicy = sp.identityMappingPolicy;
            } else {
                idpChannel.accountLinkagePolicy = _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedItem;
                idpChannel.identityMappingPolicy = _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.selectedItem;
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableJOSSOActivationPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _jossoActivationCoreSection = new JOSSOActivationCoreSection();
        corePropertyTab.addElement(_jossoActivationCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _jossoActivationCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleJOSSOActivationCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleJOSSOActivationCorePropertyTabRollOut);
    }

    private function handleJOSSOActivationCorePropertyTabCreationComplete(event:Event):void {
        var activation:JOSSOActivation = projectProxy.currentIdentityApplianceElement as JOSSOActivation;

        if (activation != null) {
            // bind view
            _jossoActivationCoreSection.connectionName.text = activation.name;
            _jossoActivationCoreSection.connectionDescription.text = activation.description;
            _jossoActivationCoreSection.partnerAppId.text = activation.partnerAppId;

            var location:Location = activation.partnerAppLocation;
            for (var i:int = 0; i < _jossoActivationCoreSection.partnerAppLocationProtocol.dataProvider.length; i++) {
                if (location != null && location.protocol == _jossoActivationCoreSection.partnerAppLocationProtocol.dataProvider[i].label) {
                    _jossoActivationCoreSection.partnerAppLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _jossoActivationCoreSection.partnerAppLocationDomain.text = location.host;
            _jossoActivationCoreSection.partnerAppLocationPort.text = location.port.toString() != "0" ?
                    location.port.toString() : "";
            _jossoActivationCoreSection.partnerAppLocationPath.text = location.context;

            var ignoredWebResources:String = "";
            if (activation.ignoredWebResources != null) {
                for (var j:int = 0; j < activation.ignoredWebResources.length; j++) {
                    if (ignoredWebResources != "") {
                        ignoredWebResources += ", ";
                    }
                    ignoredWebResources += activation.ignoredWebResources[j] as String;
                }
            }
            _jossoActivationCoreSection.ignoredWebResources.text = ignoredWebResources;

            _jossoActivationCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.partnerAppId.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.partnerAppLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.partnerAppLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.partnerAppLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.partnerAppLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _jossoActivationCoreSection.ignoredWebResources.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_jossoActivationCoreSection.nameValidator);
            _validators.push(_jossoActivationCoreSection.domainValidator);
            _validators.push(_jossoActivationCoreSection.portValidator);
            _validators.push(_jossoActivationCoreSection.pathValidator);
        }
    }

    private function handleJOSSOActivationCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var activation:JOSSOActivation = projectProxy.currentIdentityApplianceElement as JOSSOActivation;
            activation.name = _jossoActivationCoreSection.connectionName.text;
            activation.description = _jossoActivationCoreSection.connectionDescription.text;
            activation.partnerAppId = _jossoActivationCoreSection.partnerAppId.text;
            activation.partnerAppLocation.protocol = _jossoActivationCoreSection.partnerAppLocationProtocol.selectedItem.label;
            activation.partnerAppLocation.host = _jossoActivationCoreSection.partnerAppLocationDomain.text;
            activation.partnerAppLocation.port = parseInt(_jossoActivationCoreSection.partnerAppLocationPort.text);
            activation.partnerAppLocation.context = _jossoActivationCoreSection.partnerAppLocationPath.text;
            var ignoredWebResources:Array = _jossoActivationCoreSection.ignoredWebResources.text.split(",");
            if (activation.ignoredWebResources == null) {
                activation.ignoredWebResources = new ArrayCollection();
            } else {
                activation.ignoredWebResources.removeAll();
            }
            for each (var ignoredWebResource:String in ignoredWebResources) {
                ignoredWebResource = StringUtil.trim(ignoredWebResource);
                if (ignoredWebResource != "") {
                    activation.ignoredWebResources.addItem(ignoredWebResource);
                }
            }
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function enableTomcatExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _tomcatExecEnvCoreSection = new TomcatExecEnvCoreSection();
        corePropertyTab.addElement(_tomcatExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _tomcatExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleTomcatExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleTomcatExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleTomcatExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var tomcatExecEnv:TomcatExecutionEnvironment = projectProxy.currentIdentityApplianceElement as TomcatExecutionEnvironment;

        if (tomcatExecEnv != null) {
            // bind view
            _tomcatExecEnvCoreSection.executionEnvironmentName.text = tomcatExecEnv.name;
            _tomcatExecEnvCoreSection.executionEnvironmentDescription.text = tomcatExecEnv.description;

            for(var i:int=0; i < _tomcatExecEnvCoreSection.platform.dataProvider.length; i++){
                if(_tomcatExecEnvCoreSection.platform.dataProvider[i].data == tomcatExecEnv.platformId){
                    _tomcatExecEnvCoreSection.platform.selectedIndex = i;
                    break;
                }
            }

            for(var j:int=0; j < _tomcatExecEnvCoreSection.selectedHost.dataProvider.length; j++){
                if(_tomcatExecEnvCoreSection.selectedHost.dataProvider[j].data == tomcatExecEnv.type.toString()){
                    _tomcatExecEnvCoreSection.selectedHost.selectedIndex = j;
                    break;
                }
            }

            if (_tomcatExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _tomcatExecEnvCoreSection.locationItem.includeInLayout = true;
                _tomcatExecEnvCoreSection.locationItem.visible = true;
            }

            _tomcatExecEnvCoreSection.homeDirectory.text = tomcatExecEnv.installUri;
            if (tomcatExecEnv.type.name == ExecEnvType.REMOTE.name)
                _tomcatExecEnvCoreSection.location.text = tomcatExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _tomcatExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _tomcatExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_tomcatExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_tomcatExecEnvCoreSection.nameValidator);
            _validators.push(_tomcatExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleTomcatExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _tomcatExecEnvCoreSection.homeDirectory.errorString = "";
        _tomcatExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _tomcatExecEnvCoreSection.homeDirValidator.validate(_tomcatExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _tomcatExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_tomcatExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = tomcatSave;
                _execEnvHomeDir = _tomcatExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _tomcatExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_tomcatExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    tomcatSave();
                } else {
                    _tomcatExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function tomcatSave(): void {
        var tomcatExecEnv:TomcatExecutionEnvironment = projectProxy.currentIdentityApplianceElement as TomcatExecutionEnvironment;
        tomcatExecEnv.name = _tomcatExecEnvCoreSection.executionEnvironmentName.text;
        tomcatExecEnv.description = _tomcatExecEnvCoreSection.executionEnvironmentDescription.text;
        tomcatExecEnv.platformId = _tomcatExecEnvCoreSection.platform.selectedItem.data;
        tomcatExecEnv.type = ExecEnvType.valueOf(_tomcatExecEnvCoreSection.selectedHost.selectedItem.data);
        tomcatExecEnv.installUri = _tomcatExecEnvCoreSection.homeDirectory.text;
        if (tomcatExecEnv.type.name == ExecEnvType.REMOTE.name) {
            tomcatExecEnv.location = _tomcatExecEnvCoreSection.location.text;
        } else {
            tomcatExecEnv.location = null;
        }
        
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function enableWeblogicExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _weblogicExecEnvCoreSection = new WeblogicExecEnvCoreSection();
        corePropertyTab.addElement(_weblogicExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _weblogicExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleWeblogicExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleWeblogicExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleWeblogicExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var weblogicExecEnv:WeblogicExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WeblogicExecutionEnvironment;

        if (weblogicExecEnv != null) {
            // bind view
            _weblogicExecEnvCoreSection.executionEnvironmentName.text = weblogicExecEnv.name;
            _weblogicExecEnvCoreSection.executionEnvironmentDescription.text = weblogicExecEnv.description;

            _weblogicExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _weblogicExecEnvCoreSection.selectedHost.enabled = false;

            for (var i:int=0; i < _weblogicExecEnvCoreSection.platform.dataProvider.length; i++){
                if (_weblogicExecEnvCoreSection.platform.dataProvider[i].data == weblogicExecEnv.platformId) {
                    _weblogicExecEnvCoreSection.platform.selectedIndex = i;
                    break;
                }
            }

            _weblogicExecEnvCoreSection.domain.text = weblogicExecEnv.domain;

            for (var j:int=0; j < _weblogicExecEnvCoreSection.selectedHost.dataProvider.length; j++) {
                if (_weblogicExecEnvCoreSection.selectedHost.dataProvider[j].data == weblogicExecEnv.type.toString()) {
                    _weblogicExecEnvCoreSection.selectedHost.selectedIndex = j;
                    break;
                }
            }

            if (_weblogicExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _weblogicExecEnvCoreSection.locationItem.includeInLayout = true;
                _weblogicExecEnvCoreSection.locationItem.visible = true;
            }

            _weblogicExecEnvCoreSection.homeDirectory.text = weblogicExecEnv.installUri;
            if (weblogicExecEnv.type.name == ExecEnvType.REMOTE.name)
                _weblogicExecEnvCoreSection.location.text = weblogicExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _weblogicExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.domain.addEventListener(Event.CHANGE, handleSectionChange);

            _weblogicExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_weblogicExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_weblogicExecEnvCoreSection.nameValidator);
            _validators.push(_weblogicExecEnvCoreSection.domainValidator);
            _validators.push(_weblogicExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWeblogicExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _weblogicExecEnvCoreSection.homeDirectory.errorString = "";
        _weblogicExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _weblogicExecEnvCoreSection.homeDirValidator.validate(_weblogicExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _weblogicExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_weblogicExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = weblogicSave;
                _execEnvHomeDir = _weblogicExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _weblogicExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_weblogicExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    weblogicSave();
                } else {
                    _weblogicExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function weblogicSave(): void {
         // bind model
        var weblogicExecEnv:WeblogicExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WeblogicExecutionEnvironment;
        weblogicExecEnv.name = _weblogicExecEnvCoreSection.executionEnvironmentName.text;
        weblogicExecEnv.description = _weblogicExecEnvCoreSection.executionEnvironmentDescription.text;
        weblogicExecEnv.platformId = _weblogicExecEnvCoreSection.platform.selectedItem.data;
        weblogicExecEnv.domain = _weblogicExecEnvCoreSection.domain.text;

        weblogicExecEnv.type = ExecEnvType.valueOf(_weblogicExecEnvCoreSection.selectedHost.selectedItem.data);
        weblogicExecEnv.installUri = _weblogicExecEnvCoreSection.homeDirectory.text;
        if (weblogicExecEnv.type.name == ExecEnvType.REMOTE.name) {
            weblogicExecEnv.location = _weblogicExecEnvCoreSection.location.text;
        } else {
            weblogicExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function enableJBossPortalExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _jbossPortalExecEnvCoreSection = new JBossPortalExecEnvCoreSection();
        corePropertyTab.addElement(_jbossPortalExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _jbossPortalExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleJBossPortalExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleJBossPortalExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleJBossPortalExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var jbossPortalExecEnv:JBossPortalExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JBossPortalExecutionEnvironment;

        if (jbossPortalExecEnv != null) {
            // bind view
            _jbossPortalExecEnvCoreSection.executionEnvironmentName.text = jbossPortalExecEnv.name;
            _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.text = jbossPortalExecEnv.description;

            for (var i:int=0; i < _jbossPortalExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_jbossPortalExecEnvCoreSection.selectedHost.dataProvider[i].data == jbossPortalExecEnv.type.toString()) {
                    _jbossPortalExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_jbossPortalExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _jbossPortalExecEnvCoreSection.locationItem.includeInLayout = true;
                _jbossPortalExecEnvCoreSection.locationItem.visible = true;
            }

            _jbossPortalExecEnvCoreSection.homeDirectory.text = jbossPortalExecEnv.installUri;
            if (jbossPortalExecEnv.type.name == ExecEnvType.REMOTE.name)
                _jbossPortalExecEnvCoreSection.location.text = jbossPortalExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _jbossPortalExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _jbossPortalExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_jbossPortalExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_jbossPortalExecEnvCoreSection.nameValidator);
            _validators.push(_jbossPortalExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleJBossPortalExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _jbossPortalExecEnvCoreSection.homeDirectory.errorString = "";
        _jbossPortalExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _jbossPortalExecEnvCoreSection.homeDirValidator.validate(_jbossPortalExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _jbossPortalExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_jbossPortalExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = jbossPortalSave;
                _execEnvHomeDir = _jbossPortalExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _jbossPortalExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_jbossPortalExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    jbossPortalSave();
                } else {
                    _jbossPortalExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function jbossPortalSave(): void {
         // bind model
        var jbossPortalExecEnv:JBossPortalExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JBossPortalExecutionEnvironment;
        jbossPortalExecEnv.name = _jbossPortalExecEnvCoreSection.executionEnvironmentName.text;
        jbossPortalExecEnv.description = _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.text;
        jbossPortalExecEnv.platformId = "jbp";

        jbossPortalExecEnv.type = ExecEnvType.valueOf(_jbossPortalExecEnvCoreSection.selectedHost.selectedItem.data);
        jbossPortalExecEnv.installUri = _jbossPortalExecEnvCoreSection.homeDirectory.text;
        if (jbossPortalExecEnv.type.name == ExecEnvType.REMOTE.name) {
            jbossPortalExecEnv.location = _jbossPortalExecEnvCoreSection.location.text;
        } else {
            jbossPortalExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function enableLiferayExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _liferayExecEnvCoreSection = new LiferayPortalExecEnvCoreSection();
        corePropertyTab.addElement(_liferayExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _liferayExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleLiferayExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleLiferayExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleLiferayExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var liferayExecEnv:LiferayExecutionEnvironment = projectProxy.currentIdentityApplianceElement as LiferayExecutionEnvironment;

        if (liferayExecEnv != null) {
            // bind view
            _liferayExecEnvCoreSection.executionEnvironmentName.text = liferayExecEnv.name;
            _liferayExecEnvCoreSection.executionEnvironmentDescription.text = liferayExecEnv.description;

            for (var i:int=0; i < _liferayExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_liferayExecEnvCoreSection.selectedHost.dataProvider[i].data == liferayExecEnv.type.toString()) {
                    _liferayExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_liferayExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _liferayExecEnvCoreSection.locationItem.includeInLayout = true;
                _liferayExecEnvCoreSection.locationItem.visible = true;
            }

            _liferayExecEnvCoreSection.homeDirectory.text = liferayExecEnv.installUri;
            if (liferayExecEnv.type.name == ExecEnvType.REMOTE.name)
                _liferayExecEnvCoreSection.location.text = liferayExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            for (var j:int=0; j < _liferayExecEnvCoreSection.containerType.dataProvider.length; j++){
                if (_liferayExecEnvCoreSection.containerType.dataProvider[j].data == liferayExecEnv.containerType) {
                    _liferayExecEnvCoreSection.containerType.selectedIndex = j;
                    break;
                }
            }
            _liferayExecEnvCoreSection.containerPath.text = liferayExecEnv.containerPath;

            _liferayExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.containerType.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.containerPath.addEventListener(Event.CHANGE, handleSectionChange);

            _liferayExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_liferayExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_liferayExecEnvCoreSection.nameValidator);
            _validators.push(_liferayExecEnvCoreSection.containerPathValidator);
            _validators.push(_liferayExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleLiferayExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _liferayExecEnvCoreSection.homeDirectory.errorString = "";
        _liferayExecEnvCoreSection.location.errorString = "";
        _liferayExecEnvCoreSection.containerPath.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _liferayExecEnvCoreSection.homeDirValidator.validate(_liferayExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _liferayExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }
            
            if (_liferayExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_liferayExecEnvCoreSection.location.text);
                if (lvResult.type != ValidationResultEvent.VALID) {
                    _liferayExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                    return;
                }
            }

            _execEnvSaveFunction = liferaySave;

            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            var folders:ArrayCollection = new ArrayCollection();

            if (_liferayExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                folders.addItem(_liferayExecEnvCoreSection.homeDirectory.text);
            }
            
            folders.addItem(_liferayExecEnvCoreSection.containerPath.text);
            cf.folders = folders;
            cf.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, cf);
        }
    }

    private function liferaySave(): void {
         // bind model
        var liferayExecEnv:LiferayExecutionEnvironment = projectProxy.currentIdentityApplianceElement as LiferayExecutionEnvironment;
        liferayExecEnv.name = _liferayExecEnvCoreSection.executionEnvironmentName.text;
        liferayExecEnv.description = _liferayExecEnvCoreSection.executionEnvironmentDescription.text;
        liferayExecEnv.platformId = "liferay";

        liferayExecEnv.type = ExecEnvType.valueOf(_liferayExecEnvCoreSection.selectedHost.selectedItem.data);
        liferayExecEnv.installUri = _liferayExecEnvCoreSection.homeDirectory.text;
        if (liferayExecEnv.type.name == ExecEnvType.REMOTE.name) {
            liferayExecEnv.location = _liferayExecEnvCoreSection.location.text;
        } else {
            liferayExecEnv.location = null;
        }

        liferayExecEnv.containerType = _liferayExecEnvCoreSection.containerType.selectedItem.data;
        liferayExecEnv.containerPath = _liferayExecEnvCoreSection.containerPath.text;

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function enableWASCEExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _wasceExecEnvCoreSection = new WASCEExecEnvCoreSection();
        corePropertyTab.addElement(_wasceExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _wasceExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleWASCEExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleWASCEExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleWASCEExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var wasceExecEnv:WASCEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WASCEExecutionEnvironment;

        if (wasceExecEnv != null) {
            // bind view
            _wasceExecEnvCoreSection.executionEnvironmentName.text = wasceExecEnv.name;
            _wasceExecEnvCoreSection.executionEnvironmentDescription.text = wasceExecEnv.description;

            for (var i:int=0; i < _wasceExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_wasceExecEnvCoreSection.selectedHost.dataProvider[i].data == wasceExecEnv.type.toString()) {
                    _wasceExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_wasceExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _wasceExecEnvCoreSection.locationItem.includeInLayout = true;
                _wasceExecEnvCoreSection.locationItem.visible = true;
            }

            _wasceExecEnvCoreSection.homeDirectory.text = wasceExecEnv.installUri;
            if (wasceExecEnv.type.name == ExecEnvType.REMOTE.name)
                _wasceExecEnvCoreSection.location.text = wasceExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _wasceExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _wasceExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_wasceExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_wasceExecEnvCoreSection.nameValidator);
            _validators.push(_wasceExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWASCEExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _wasceExecEnvCoreSection.homeDirectory.errorString = "";
        _wasceExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _wasceExecEnvCoreSection.homeDirValidator.validate(_wasceExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _wasceExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_wasceExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = wasceSave;
                _execEnvHomeDir = _wasceExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _wasceExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_wasceExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    wasceSave();
                } else {
                    _wasceExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function wasceSave(): void {
         // bind model
        var wasceExecEnv:WASCEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WASCEExecutionEnvironment;
        wasceExecEnv.name = _wasceExecEnvCoreSection.executionEnvironmentName.text;
        wasceExecEnv.description = _wasceExecEnvCoreSection.executionEnvironmentDescription.text;
        wasceExecEnv.platformId = "wc21";

        wasceExecEnv.type = ExecEnvType.valueOf(_wasceExecEnvCoreSection.selectedHost.selectedItem.data);
        wasceExecEnv.installUri = _wasceExecEnvCoreSection.homeDirectory.text;
        if (wasceExecEnv.type.name == ExecEnvType.REMOTE.name) {
            wasceExecEnv.location = _wasceExecEnvCoreSection.location.text;
        } else {
            wasceExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****JBOSS*****/
    private function enableJbossExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _jbossExecEnvCoreSection = new JBossExecEnvCoreSection();
        corePropertyTab.addElement(_jbossExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _jbossExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleJbossExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleJbossExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleJbossExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var jbossExecEnv:JbossExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JbossExecutionEnvironment;

        if (jbossExecEnv != null) {
            // bind view
            _jbossExecEnvCoreSection.executionEnvironmentName.text = jbossExecEnv.name;
            _jbossExecEnvCoreSection.executionEnvironmentDescription.text = jbossExecEnv.description;

            for(var i:int=0; i < _jbossExecEnvCoreSection.platform.dataProvider.length; i++){
                if(_jbossExecEnvCoreSection.platform.dataProvider[i].data == jbossExecEnv.platformId){
                    _jbossExecEnvCoreSection.platform.selectedIndex = i;
                    break;
                }
            }

            for (var j:int=0; j < _jbossExecEnvCoreSection.selectedHost.dataProvider.length; j++) {
                if (_jbossExecEnvCoreSection.selectedHost.dataProvider[j].data == jbossExecEnv.type.toString()) {
                    _jbossExecEnvCoreSection.selectedHost.selectedIndex = j;
                    break;
                }
            }

            if (_jbossExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _jbossExecEnvCoreSection.locationItem.includeInLayout = true;
                _jbossExecEnvCoreSection.locationItem.visible = true;
            }

            _jbossExecEnvCoreSection.homeDirectory.text = jbossExecEnv.installUri;
            if (jbossExecEnv.type.name == ExecEnvType.REMOTE.name)
                _jbossExecEnvCoreSection.location.text = jbossExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _jbossExecEnvCoreSection.instance.text = jbossExecEnv.instance;

            _jbossExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.instance.addEventListener(Event.CHANGE, handleSectionChange);

            _jbossExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_jbossExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_jbossExecEnvCoreSection.nameValidator);
            _validators.push(_jbossExecEnvCoreSection.instanceValidator);
            _validators.push(_jbossExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleJbossExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _jbossExecEnvCoreSection.homeDirectory.errorString = "";
        _jbossExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _jbossExecEnvCoreSection.homeDirValidator.validate(_jbossExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _jbossExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_jbossExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = jbossSave;
                _execEnvHomeDir = _jbossExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _jbossExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_jbossExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    jbossSave();
                } else {
                    _jbossExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function jbossSave(): void {
         // bind model
        var jbossExecEnv:JbossExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JbossExecutionEnvironment;
        jbossExecEnv.name = _jbossExecEnvCoreSection.executionEnvironmentName.text;
        jbossExecEnv.description = _jbossExecEnvCoreSection.executionEnvironmentDescription.text;
        jbossExecEnv.platformId = _jbossExecEnvCoreSection.platform.selectedItem.data;
        jbossExecEnv.instance = _jbossExecEnvCoreSection.instance.text;

        jbossExecEnv.type = ExecEnvType.valueOf(_jbossExecEnvCoreSection.selectedHost.selectedItem.data);
        jbossExecEnv.installUri = _jbossExecEnvCoreSection.homeDirectory.text;
        if (jbossExecEnv.type.name == ExecEnvType.REMOTE.name) {
            jbossExecEnv.location = _jbossExecEnvCoreSection.location.text;
        } else {
            jbossExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****APACHE*****/
    private function enableApacheExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _apacheExecEnvCoreSection = new ApacheExecEnvCoreSection();
        corePropertyTab.addElement(_apacheExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _apacheExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleApacheExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleApacheExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
//        var execEnvActivationPropertyTab:Group = new Group();
//        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
//        execEnvActivationPropertyTab.name = "Activation";
//        execEnvActivationPropertyTab.width = Number("100%");
//        execEnvActivationPropertyTab.height = Number("100%");
//        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");
//
//        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
//        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
//        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
//        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleApacheExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var apacheExecEnv:ApacheExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ApacheExecutionEnvironment;

        if (apacheExecEnv != null) {
            // bind view
            _apacheExecEnvCoreSection.executionEnvironmentName.text = apacheExecEnv.name;
            _apacheExecEnvCoreSection.executionEnvironmentDescription.text = apacheExecEnv.description;

            for (var i:int=0; i < _apacheExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_apacheExecEnvCoreSection.selectedHost.dataProvider[i].data == apacheExecEnv.type.toString()) {
                    _apacheExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_apacheExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _apacheExecEnvCoreSection.locationItem.includeInLayout = true;
                _apacheExecEnvCoreSection.locationItem.visible = true;
            }

            _apacheExecEnvCoreSection.homeDirectory.text = apacheExecEnv.installUri;
            if (apacheExecEnv.type.name == ExecEnvType.REMOTE.name)
                _apacheExecEnvCoreSection.location.text = apacheExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _apacheExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _apacheExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_apacheExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_apacheExecEnvCoreSection.nameValidator);
            _validators.push(_apacheExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleApacheExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _apacheExecEnvCoreSection.homeDirectory.errorString = "";
        _apacheExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _apacheExecEnvCoreSection.homeDirValidator.validate(_apacheExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _apacheExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_apacheExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = apacheSave;
                _execEnvHomeDir = _apacheExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _apacheExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_apacheExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    apacheSave();
                } else {
                    _apacheExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function apacheSave(): void {
         // bind model
        var apacheExecEnv:ApacheExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ApacheExecutionEnvironment;
        apacheExecEnv.name = _apacheExecEnvCoreSection.executionEnvironmentName.text;
        apacheExecEnv.description = _apacheExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        apacheExecEnv.platformId = "apache";

        apacheExecEnv.type = ExecEnvType.valueOf(_apacheExecEnvCoreSection.selectedHost.selectedItem.data);
        apacheExecEnv.installUri = _apacheExecEnvCoreSection.homeDirectory.text;
        if (apacheExecEnv.type.name == ExecEnvType.REMOTE.name) {
            apacheExecEnv.location = _apacheExecEnvCoreSection.location.text;
        } else {
            apacheExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****WINDOWS IIS*****/
    private function enableWindowsIISExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _windowsIISExecEnvCoreSection = new WindowsIISExecEnvCoreSection();
        corePropertyTab.addElement(_windowsIISExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _windowsIISExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleWindowsIISExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleWindowsIISExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);
    }

    private function handleWindowsIISExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;

        if (windowsIISExecEnv != null) {
            // bind view
            _windowsIISExecEnvCoreSection.executionEnvironmentName.text = windowsIISExecEnv.name;
            _windowsIISExecEnvCoreSection.executionEnvironmentDescription.text = windowsIISExecEnv.description;

            for (var i:int=0; i < _windowsIISExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_windowsIISExecEnvCoreSection.selectedHost.dataProvider[i].data == windowsIISExecEnv.type.toString()) {
                    _windowsIISExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_windowsIISExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _windowsIISExecEnvCoreSection.locationItem.includeInLayout = true;
                _windowsIISExecEnvCoreSection.locationItem.visible = true;
            }

            _windowsIISExecEnvCoreSection.homeDirectory.text = windowsIISExecEnv.installUri;
            if (windowsIISExecEnv.type.name == ExecEnvType.REMOTE.name)
                _windowsIISExecEnvCoreSection.location.text = windowsIISExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            for (var j:int=0; j < _windowsIISExecEnvCoreSection.architecture.dataProvider.length; j++) {
                if (_windowsIISExecEnvCoreSection.architecture.dataProvider[j].data == windowsIISExecEnv.platformId) {
                    _windowsIISExecEnvCoreSection.architecture.selectedIndex = j;
                    break;
                }
            }

            _windowsIISExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.architecture.addEventListener(Event.CHANGE, handleSectionChange);

            _windowsIISExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_windowsIISExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_windowsIISExecEnvCoreSection.nameValidator);
            _validators.push(_windowsIISExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWindowsIISExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _windowsIISExecEnvCoreSection.homeDirectory.errorString = "";
        _windowsIISExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _windowsIISExecEnvCoreSection.homeDirValidator.validate(_windowsIISExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _windowsIISExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_windowsIISExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = windowsIISSave;
                _execEnvHomeDir = _windowsIISExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _windowsIISExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_windowsIISExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    windowsIISSave();
                } else {
                    _windowsIISExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function windowsIISSave(): void {
         // bind model
        var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;
        windowsIISExecEnv.name = _windowsIISExecEnvCoreSection.executionEnvironmentName.text;
        windowsIISExecEnv.description = _windowsIISExecEnvCoreSection.executionEnvironmentDescription.text;
        windowsIISExecEnv.platformId = _windowsIISExecEnvCoreSection.architecture.selectedItem.data;

        windowsIISExecEnv.type = ExecEnvType.valueOf(_windowsIISExecEnvCoreSection.selectedHost.selectedItem.data);
        windowsIISExecEnv.installUri = _windowsIISExecEnvCoreSection.homeDirectory.text;
        if (windowsIISExecEnv.type.name == ExecEnvType.REMOTE.name) {
            windowsIISExecEnv.location = _windowsIISExecEnvCoreSection.location.text;
        } else {
            windowsIISExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /***ALFRESCO***/
    private function enableAlfrescoExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _alfrescoExecEnvCoreSection = new AlfrescoExecEnvCoreSection();
        corePropertyTab.addElement(_alfrescoExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _alfrescoExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleAlfrescoExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleAlfrescoExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleAlfrescoExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var alfrescoExecEnv:AlfrescoExecutionEnvironment = projectProxy.currentIdentityApplianceElement as AlfrescoExecutionEnvironment;

        if (alfrescoExecEnv != null) {
            // bind view
            _alfrescoExecEnvCoreSection.executionEnvironmentName.text = alfrescoExecEnv.name;
            _alfrescoExecEnvCoreSection.executionEnvironmentDescription.text = alfrescoExecEnv.description;
            _alfrescoExecEnvCoreSection.tomcatInstallDir.text = alfrescoExecEnv.tomcatInstallDir;

            for (var i:int=0; i < _alfrescoExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_alfrescoExecEnvCoreSection.selectedHost.dataProvider[i].data == alfrescoExecEnv.type.toString()) {
                    _alfrescoExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_alfrescoExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _alfrescoExecEnvCoreSection.locationItem.includeInLayout = true;
                _alfrescoExecEnvCoreSection.locationItem.visible = true;
            }

            _alfrescoExecEnvCoreSection.homeDirectory.text = alfrescoExecEnv.installUri;
            if (alfrescoExecEnv.type.name == ExecEnvType.REMOTE.name)
                _alfrescoExecEnvCoreSection.location.text = alfrescoExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _alfrescoExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.tomcatInstallDir.addEventListener(Event.CHANGE, handleSectionChange);

            _alfrescoExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_alfrescoExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_alfrescoExecEnvCoreSection.nameValidator);
            _validators.push(_alfrescoExecEnvCoreSection.containerDirValidator);
            _validators.push(_alfrescoExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleAlfrescoExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _alfrescoExecEnvCoreSection.homeDirectory.errorString = "";
        _alfrescoExecEnvCoreSection.location.errorString = "";
        _alfrescoExecEnvCoreSection.tomcatInstallDir.errorString = "";        
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _alfrescoExecEnvCoreSection.homeDirValidator.validate(_alfrescoExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _alfrescoExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_alfrescoExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_alfrescoExecEnvCoreSection.location.text);
                if (lvResult.type != ValidationResultEvent.VALID) {
                    _alfrescoExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                    return;
                }
            }

            _execEnvSaveFunction = alfrescoSave;

            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            var folders:ArrayCollection = new ArrayCollection();

            if (_alfrescoExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                folders.addItem(_alfrescoExecEnvCoreSection.homeDirectory.text);
            }

            folders.addItem(_alfrescoExecEnvCoreSection.tomcatInstallDir.text);
            cf.folders = folders;
            cf.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_FOLDERS_EXISTENCE, cf);
        }
    }

    private function alfrescoSave(): void {
         // bind model
        var alfrescoExecEnv:AlfrescoExecutionEnvironment = projectProxy.currentIdentityApplianceElement as AlfrescoExecutionEnvironment;
        alfrescoExecEnv.name = _alfrescoExecEnvCoreSection.executionEnvironmentName.text;
        alfrescoExecEnv.description = _alfrescoExecEnvCoreSection.executionEnvironmentDescription.text;
        alfrescoExecEnv.platformId = "alfresco";
        alfrescoExecEnv.tomcatInstallDir = _alfrescoExecEnvCoreSection.tomcatInstallDir.text;

        alfrescoExecEnv.type = ExecEnvType.valueOf(_alfrescoExecEnvCoreSection.selectedHost.selectedItem.data);
        alfrescoExecEnv.installUri = _alfrescoExecEnvCoreSection.homeDirectory.text;
        if (alfrescoExecEnv.type.name == ExecEnvType.REMOTE.name) {
            alfrescoExecEnv.location = _alfrescoExecEnvCoreSection.location.text;
        } else {
            alfrescoExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****JAVA EE*****/
    private function enableJavaEEExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _javaEEExecEnvCoreSection = new JavaEEExecEnvCoreSection();
        corePropertyTab.addElement(_javaEEExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _javaEEExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleJavaEEExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleJavaEEExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
//        var execEnvActivationPropertyTab:Group = new Group();
//        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
//        execEnvActivationPropertyTab.name = "Activation";
//        execEnvActivationPropertyTab.width = Number("100%");
//        execEnvActivationPropertyTab.height = Number("100%");
//        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");
//
//        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
//        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
//        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
//        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleJavaEEExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var javaEEExecEnv:JEEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JEEExecutionEnvironment;

        if (javaEEExecEnv != null) {
            // bind view
            _javaEEExecEnvCoreSection.executionEnvironmentName.text = javaEEExecEnv.name;
            _javaEEExecEnvCoreSection.executionEnvironmentDescription.text = javaEEExecEnv.description;

            for (var i:int=0; i < _javaEEExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_javaEEExecEnvCoreSection.selectedHost.dataProvider[i].data == javaEEExecEnv.type.toString()) {
                    _javaEEExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_javaEEExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _javaEEExecEnvCoreSection.locationItem.includeInLayout = true;
                _javaEEExecEnvCoreSection.locationItem.visible = true;
            }

            _javaEEExecEnvCoreSection.homeDirectory.text = javaEEExecEnv.installUri;
            if (javaEEExecEnv.type.name == ExecEnvType.REMOTE.name)
                _javaEEExecEnvCoreSection.location.text = javaEEExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _javaEEExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _javaEEExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_javaEEExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_javaEEExecEnvCoreSection.nameValidator);
            _validators.push(_javaEEExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleJavaEEExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _javaEEExecEnvCoreSection.homeDirectory.errorString = "";
        _javaEEExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _javaEEExecEnvCoreSection.homeDirValidator.validate(_javaEEExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _javaEEExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_javaEEExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = javaEESave;
                _execEnvHomeDir = _javaEEExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _javaEEExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_javaEEExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    javaEESave();
                } else {
                    _javaEEExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function javaEESave(): void {
         // bind model
        var javaEEExecEnv:JEEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JEEExecutionEnvironment;
        javaEEExecEnv.name = _javaEEExecEnvCoreSection.executionEnvironmentName.text;
        javaEEExecEnv.description = _javaEEExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        javaEEExecEnv.platformId = "jee";

        javaEEExecEnv.type = ExecEnvType.valueOf(_javaEEExecEnvCoreSection.selectedHost.selectedItem.data);
        javaEEExecEnv.installUri = _javaEEExecEnvCoreSection.homeDirectory.text;
        if (javaEEExecEnv.type.name == ExecEnvType.REMOTE.name) {
            javaEEExecEnv.location = _javaEEExecEnvCoreSection.location.text;
        } else {
            javaEEExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****PHP*****/
    private function enablePHPExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _phpExecEnvCoreSection = new PHPExecEnvCoreSection();
        corePropertyTab.addElement(_phpExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _phpExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handlePHPExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handlePHPExecEnvCorePropertyTabRollOut);
    }

    private function handlePHPExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var phpExecEnv:PHPExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PHPExecutionEnvironment;

        if (phpExecEnv != null) {
            // bind view
            _phpExecEnvCoreSection.executionEnvironmentName.text = phpExecEnv.name;
            _phpExecEnvCoreSection.executionEnvironmentDescription.text = phpExecEnv.description;

            for (var i:int=0; i < _phpExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_phpExecEnvCoreSection.selectedHost.dataProvider[i].data == phpExecEnv.type.toString()) {
                    _phpExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_phpExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _phpExecEnvCoreSection.locationItem.includeInLayout = true;
                _phpExecEnvCoreSection.locationItem.visible = true;
            }

            _phpExecEnvCoreSection.homeDirectory.text = phpExecEnv.installUri;
            if (phpExecEnv.type.name == ExecEnvType.REMOTE.name)
                _phpExecEnvCoreSection.location.text = phpExecEnv.location;

            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _phpExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _phpExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _phpExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _phpExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _phpExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _phpExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_phpExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_phpExecEnvCoreSection.nameValidator);
            _validators.push(_phpExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handlePHPExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _phpExecEnvCoreSection.homeDirectory.errorString = "";
        _phpExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _phpExecEnvCoreSection.homeDirValidator.validate(_phpExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _phpExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_phpExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = phpSave;
                _execEnvHomeDir = _phpExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _phpExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_phpExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    phpSave();
                } else {
                    _phpExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function phpSave(): void {
         // bind model
        var phpExecEnv:PHPExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PHPExecutionEnvironment;
        phpExecEnv.name = _phpExecEnvCoreSection.executionEnvironmentName.text;
        phpExecEnv.description = _phpExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        phpExecEnv.platformId = "php";

        phpExecEnv.type = ExecEnvType.valueOf(_phpExecEnvCoreSection.selectedHost.selectedItem.data);
        phpExecEnv.installUri = _phpExecEnvCoreSection.homeDirectory.text;
        if (phpExecEnv.type.name == ExecEnvType.REMOTE.name) {
            phpExecEnv.location = _phpExecEnvCoreSection.location.text;
        } else {
            phpExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****PHPBB*****/
    private function enablePhpBBExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _phpBBExecEnvCoreSection = new PhpBBExecEnvCoreSection();
        corePropertyTab.addElement(_phpBBExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _phpBBExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handlePhpBBExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handlePhpBBExecEnvCorePropertyTabRollOut);

        // Exec.Environment Activation Tab
        var execEnvActivationPropertyTab:Group = new Group();
        execEnvActivationPropertyTab.id = "propertySheetActivationSection";
        execEnvActivationPropertyTab.name = "Activation";
        execEnvActivationPropertyTab.width = Number("100%");
        execEnvActivationPropertyTab.height = Number("100%");
        execEnvActivationPropertyTab.setStyle("borderStyle", "solid");

        _executionEnvironmentActivateSection = new ExecutionEnvironmentActivationSection();
        execEnvActivationPropertyTab.addElement(_executionEnvironmentActivateSection);
        _propertySheetsViewStack.addNewChild(execEnvActivationPropertyTab);
        _executionEnvironmentActivateSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExecEnvActivationPropertyTabCreationComplete);
        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handlePhpBBExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var phpBBExecEnv:PhpBBExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PhpBBExecutionEnvironment;

        if (phpBBExecEnv != null) {
            // bind view
            _phpBBExecEnvCoreSection.executionEnvironmentName.text = phpBBExecEnv.name;
            _phpBBExecEnvCoreSection.executionEnvironmentDescription.text = phpBBExecEnv.description;

            for (var i:int=0; i < _phpBBExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_phpBBExecEnvCoreSection.selectedHost.dataProvider[i].data == phpBBExecEnv.type.toString()) {
                    _phpBBExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_phpBBExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _phpBBExecEnvCoreSection.locationItem.includeInLayout = true;
                _phpBBExecEnvCoreSection.locationItem.visible = true;
            }

            _phpBBExecEnvCoreSection.homeDirectory.text = phpBBExecEnv.installUri;
            if (phpBBExecEnv.type.name == ExecEnvType.REMOTE.name)
                _phpBBExecEnvCoreSection.location.text = phpBBExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _phpBBExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _phpBBExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_phpBBExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_phpBBExecEnvCoreSection.nameValidator);
            _validators.push(_phpBBExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handlePhpBBExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _phpBBExecEnvCoreSection.homeDirectory.errorString = "";
        _phpBBExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _phpBBExecEnvCoreSection.homeDirValidator.validate(_phpBBExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _phpBBExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_phpBBExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = phpBBSave;
                _execEnvHomeDir = _phpBBExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _phpBBExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_phpBBExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    phpBBSave();
                } else {
                    _phpBBExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function phpBBSave(): void {
         // bind model
        var phpBBExecEnv:PhpBBExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PhpBBExecutionEnvironment;
        phpBBExecEnv.name = _phpBBExecEnvCoreSection.executionEnvironmentName.text;
        phpBBExecEnv.description = _phpBBExecEnvCoreSection.executionEnvironmentDescription.text;
        phpBBExecEnv.platformId = "phpBB";

        phpBBExecEnv.type = ExecEnvType.valueOf(_phpBBExecEnvCoreSection.selectedHost.selectedItem.data);
        phpBBExecEnv.installUri = _phpBBExecEnvCoreSection.homeDirectory.text;
        if (phpBBExecEnv.type.name == ExecEnvType.REMOTE.name) {
            phpBBExecEnv.location = _phpBBExecEnvCoreSection.location.text;
        } else {
            phpBBExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    /*****WEBSERVER*****/
    private function enableWebserverExecEnvPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _webserverExecEnvCoreSection = new WebserverExecEnvCoreSection();
        corePropertyTab.addElement(_webserverExecEnvCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _webserverExecEnvCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleWebserverExecEnvCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleWebserverExecEnvCorePropertyTabRollOut);
    }

    private function handleWebserverExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var webserverExecEnv:WebserverExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WebserverExecutionEnvironment;

        if (webserverExecEnv != null) {
            // bind view
            _webserverExecEnvCoreSection.executionEnvironmentName.text = webserverExecEnv.name;
            _webserverExecEnvCoreSection.executionEnvironmentDescription.text = webserverExecEnv.description;
            _webserverExecEnvCoreSection.executionEnvironmentType.text = webserverExecEnv.containerType;

            for (var i:int=0; i < _webserverExecEnvCoreSection.selectedHost.dataProvider.length; i++) {
                if (_webserverExecEnvCoreSection.selectedHost.dataProvider[i].data == webserverExecEnv.type.toString()) {
                    _webserverExecEnvCoreSection.selectedHost.selectedIndex = i;
                    break;
                }
            }

            if (_webserverExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
                _webserverExecEnvCoreSection.locationItem.includeInLayout = true;
                _webserverExecEnvCoreSection.locationItem.visible = true;
            }

            _webserverExecEnvCoreSection.homeDirectory.text = webserverExecEnv.installUri;
            if (webserverExecEnv.type.name == ExecEnvType.REMOTE.name)
                _webserverExecEnvCoreSection.location.text = webserverExecEnv.location;
            
            _execEnvLocationValidator = new URLValidator();
            _execEnvLocationValidator.required = true;

            _webserverExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.location.addEventListener(Event.CHANGE, handleSectionChange);

            _webserverExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, function(event:Event):void {
                handleHostChange(_webserverExecEnvCoreSection);
            });

            _validators = [];
            _validators.push(_webserverExecEnvCoreSection.nameValidator);
            _validators.push(_webserverExecEnvCoreSection.typeValidator);
            _validators.push(_webserverExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWebserverExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _webserverExecEnvCoreSection.homeDirectory.errorString = "";
        _webserverExecEnvCoreSection.location.errorString = "";
        if (_dirty && validate(true)) {
            var hvResult:ValidationResultEvent;
            if ((hvResult = _webserverExecEnvCoreSection.homeDirValidator.validate(_webserverExecEnvCoreSection.homeDirectory.text)).type != ValidationResultEvent.VALID) {
                _webserverExecEnvCoreSection.homeDirectory.errorString = hvResult.results[0].errorMessage;
                return;
            }

            if (_webserverExecEnvCoreSection.selectedHost.selectedItem.data == ExecEnvType.LOCAL.name) {
                _execEnvSaveFunction = webserverSave;
                _execEnvHomeDir = _webserverExecEnvCoreSection.homeDirectory;
                var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
                cif.homeDir = _webserverExecEnvCoreSection.homeDirectory.text;
                cif.environmentName = "n/a";
                sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
            } else {
                var lvResult:ValidationResultEvent = _execEnvLocationValidator.validate(_webserverExecEnvCoreSection.location.text);
                if (lvResult.type == ValidationResultEvent.VALID) {
                    webserverSave();
                } else {
                    _webserverExecEnvCoreSection.location.errorString = lvResult.results[0].errorMessage;
                }
            }
        }
    }

    private function webserverSave(): void {
         // bind model
        var webserverExecEnv:WebserverExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WebserverExecutionEnvironment;
        webserverExecEnv.name = _webserverExecEnvCoreSection.executionEnvironmentName.text;
        webserverExecEnv.description = _webserverExecEnvCoreSection.executionEnvironmentDescription.text;
        webserverExecEnv.containerType = _webserverExecEnvCoreSection.executionEnvironmentType.text;
        //TODO CHECK PLATFORM ID
        webserverExecEnv.platformId = "webserver";

        webserverExecEnv.type = ExecEnvType.valueOf(_webserverExecEnvCoreSection.selectedHost.selectedItem.data);
        webserverExecEnv.installUri = _webserverExecEnvCoreSection.homeDirectory.text;
        if (webserverExecEnv.type.name == ExecEnvType.REMOTE.name) {
            webserverExecEnv.location = _webserverExecEnvCoreSection.location.text;
        } else {
            webserverExecEnv.location = null;
        }

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function handleExecEnvActivationPropertyTabCreationComplete(event:Event):void {
        var execEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        if (execEnv != null) {
            _executionEnvironmentActivateSection.replaceConfFiles.selected = execEnv.overwriteOriginalSetup;
            _executionEnvironmentActivateSection.installSamples.selected = execEnv.installDemoApps;
            if (execEnv is LiferayExecutionEnvironment || execEnv is AlfrescoExecutionEnvironment ||
                    execEnv is WindowsIISExecutionEnvironment || execEnv is PhpBBExecutionEnvironment) {
                _executionEnvironmentActivateSection.installSamples.selected = false;
                _executionEnvironmentActivateSection.installSamples.enabled = false;
            }
            //TODO add click handler for _executionEnvironmentActivateSection.activate checkbox
            _executionEnvironmentActivateSection.reactivate.addEventListener(MouseEvent.CLICK, reactivateClickHandler);
            _executionEnvironmentActivateSection.installSamples.addEventListener(Event.CHANGE, handleSectionChange);
            _executionEnvironmentActivateSection.replaceConfFiles.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleExecEnvActivationPropertyTabRollOut(event:Event):void {
        if (projectProxy.currentIdentityAppliance.state != IdentityApplianceState.DISPOSED.toString()){
            //activateExecutionEnvironment(event);
            if (_executionEnvironmentActivateSection.reactivate.selected && _applianceSaved) {
                sendNotification(ApplicationFacade.DISPLAY_ACTIVATION_DIALOG,
                        [_executionEnvironmentActivateSection.reactivate.selected,
                         _executionEnvironmentActivateSection.replaceConfFiles.selected,
                         _executionEnvironmentActivateSection.installSamples.selected]);
            }
        }
        if (_dirty){
            var execEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
            execEnv.installDemoApps = _executionEnvironmentActivateSection.installSamples.selected ;
            execEnv.overwriteOriginalSetup = _executionEnvironmentActivateSection.replaceConfFiles.selected;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function reactivateClickHandler(event:Event):void {
        if(!_applianceSaved){
            Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, "activation.save.info"),
                    resourceManager.getString(AtricoreConsole.BUNDLE, "activation.save.title"), Alert.OK, null, null, null, Alert.OK);
            _executionEnvironmentActivateSection.reactivate.selected = false;
        }
    }

    /*
    private function activateExecutionEnvironment(event:Event):void {
        var currentExecEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        if(_executionEnvironmentActivateSection.reactivate.selected && _applianceSaved){
            var text:String = currentExecEnv.name + " "
                    + resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.line1")
                    + "\n"
                    + resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.line2");
            var alert:Alert = Alert.show(text,
                    resourceManager.getString(AtricoreConsole.BUNDLE, "activation.confirm.title"), 
                    Alert.OK | Alert.CANCEL, null, activationConfirmationHandler, null, Alert.OK);
            alert.width = 450;
            alert.callLater(function():void {
                var textField:IUITextField =  IUITextField(alert.mx_internal::alertForm.mx_internal::textField);

                var textFormat:TextFormat = new TextFormat();
                textFormat.align = "center";

                textField.width = alert.width - 10;
                textField.x = 0;
                textField.setTextFormat(textFormat);
            });
        }
    }


    private function activationConfirmationHandler(event:CloseEvent):void {
        if (event.detail == Alert.OK) {
            var currentExecEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
            var activateExecEnvReq:ActivateExecutionEnvironmentRequest = new ActivateExecutionEnvironmentRequest();
            activateExecEnvReq.reactivate = _executionEnvironmentActivateSection.reactivate.selected;
            activateExecEnvReq.replaceConfFiles = _executionEnvironmentActivateSection.replaceConfFiles.selected;
            activateExecEnvReq.executionEnvironment = currentExecEnv;
            activateExecEnvReq.installSamples = _executionEnvironmentActivateSection.installSamples.selected;

            sendNotification(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvReq);
        }
        _executionEnvironmentActivateSection.reactivate.selected = false;
    }
    */

    protected function enableIdentityLookupPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _identityLookupCoreSection = new IdentityLookupCoreSection();
        corePropertyTab.addElement(_identityLookupCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _identityLookupCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityLookupCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityLookupCorePropertyTabRollOut);
    }

    private function handleIdentityLookupCorePropertyTabCreationComplete(event:Event):void {
        var identityLookup:IdentityLookup = projectProxy.currentIdentityApplianceElement as IdentityLookup;

        if (identityLookup != null) {
            // bind view
            _identityLookupCoreSection.connectionName.text = identityLookup.name;
            _identityLookupCoreSection.connectionDescription.text = identityLookup.description;

            _identityLookupCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
            _identityLookupCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_identityLookupCoreSection.nameValidator);
        }
    }

    private function handleIdentityLookupCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var identityLookup:IdentityLookup = projectProxy.currentIdentityApplianceElement as IdentityLookup;

            identityLookup.name = _identityLookupCoreSection.connectionName.text;
            identityLookup.description = _identityLookupCoreSection.connectionDescription.text;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    protected function enableDelegatedAuthenticationPropertyTabs():void {
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _delegatedAuthenticationCoreSection = new DelegatedAuthenticationCoreSection();
        corePropertyTab.addElement(_delegatedAuthenticationCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _delegatedAuthenticationCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleDelegatedAuthenticationCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleDelegatedAuthenticationCorePropertyTabRollOut);
    }

    private function handleDelegatedAuthenticationCorePropertyTabCreationComplete(event:Event):void {
        var delegatedAuthentication:DelegatedAuthentication = projectProxy.currentIdentityApplianceElement as DelegatedAuthentication;

        if (delegatedAuthentication != null) {
            // bind view
            _delegatedAuthenticationCoreSection.connectionName.text = delegatedAuthentication.name;
            _delegatedAuthenticationCoreSection.connectionDescription.text = delegatedAuthentication.description;

            _delegatedAuthenticationCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
            _delegatedAuthenticationCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_delegatedAuthenticationCoreSection.nameValidator);
        }
    }

    private function handleDelegatedAuthenticationCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
             // bind model
            var delegatedAuthentication:DelegatedAuthentication = projectProxy.currentIdentityApplianceElement as DelegatedAuthentication;

            delegatedAuthentication.name = _delegatedAuthenticationCoreSection.connectionName.text;
            delegatedAuthentication.description = _delegatedAuthenticationCoreSection.connectionDescription.text;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    // keystore functions
    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function fileSelectHandler(evt:Event):void {
        _certificateSection.certificateKeyPair.prompt = null;
        _selectedFiles = new ArrayCollection();
        _selectedFiles.addItem(_fileRef.name);
        _certificateSection.certificateKeyPair.selectedIndex = 0;

        _certificateSection.lblUploadMsg.text = "";
        _certificateSection.lblUploadMsg.visible = false;

        _dirty = true;
        disableExportButtons();
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _certificateSection.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "manageCertificate.form.upload.success");
        _certificateSection.lblUploadMsg.setStyle("color", "Green");
        _certificateSection.lblUploadMsg.visible = true;
        _certificateSection.fadeFx.play([_certificateSection.lblUploadMsg]);

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        _certificateSection.certificateKeyPair.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "browse.keypair");

        var provider:Provider = _currentIdentityApplianceElement as Provider;
        var config:SamlR2ProviderConfig = provider.config as SamlR2ProviderConfig;
        updateSamlR2Config(provider, config);
    }

    private function resetUploadFields():void {
        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        _uploadedFile = null;
        _uploadedFileName = null;
    }

    private function handleCertManagementTypeClicked(event:ItemClickEvent):void {
        _validators = [];
        var provider:Provider = _currentIdentityApplianceElement as Provider;
        if (provider != null) {
            if (provider is IdentityProvider) {
                _validators.push(_ipCoreSection.nameValidator);
                _validators.push(_ipCoreSection.portValidator);
                _validators.push(_ipCoreSection.domainValidator);
                _validators.push(_ipCoreSection.contextValidator);
                _validators.push(_ipCoreSection.pathValidator);
                if (_basicAuthenticationSection != null) {
                    _validators.push(_basicAuthenticationSection.nameValidator);
                }
                if (_twoFactorAuthenticationSection != null) {
                    _validators.push(_twoFactorAuthenticationSection.nameValidator);
                }
                if (_bindAuthenticationSection != null) {
                    _validators.push(_bindAuthenticationSection.nameValidator);
                }
            } else if (provider is ServiceProvider) {
                _validators.push(_spCoreSection.nameValidator);
                _validators.push(_spCoreSection.portValidator);
                _validators.push(_spCoreSection.domainValidator);
                _validators.push(_spCoreSection.contextValidator);
                _validators.push(_spCoreSection.pathValidator);
            }
        }
        if (_certificateSection.uploadKeystore.selected) {
            enableDisableUploadFields(true);
            _validators.push(_certificateSection.certificateAliasValidator);
            _validators.push(_certificateSection.keyAliasValidator);
            _validators.push(_certificateSection.keystorePasswordValidator);
            _validators.push(_certificateSection.keyPasswordValidator);
        } else {
            enableDisableUploadFields(false);
        }
    }

    private function enableDisableUploadFields(enable:Boolean):void {
        _certificateSection.certificateKeyPair.enabled = enable;
        _certificateSection.keystoreFormat.enabled = enable;
        _certificateSection.certificateAlias.enabled = enable;
        _certificateSection.keyAlias.enabled = enable;
        _certificateSection.keystorePassword.enabled = enable;
        _certificateSection.keyPassword.enabled = enable;
    }

    // metadata file upload functions
    private function browseMetadataHandler(event:MouseEvent):void {
        if (_metadataFileRef == null) {
            _metadataFileRef = new FileReference();
            _metadataFileRef.addEventListener(Event.SELECT, metadataSelectHandler);
            _metadataFileRef.addEventListener(Event.COMPLETE, uploadMetadataCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("XML(*.xml)", "*.xml");
        var fileTypes:Array = new Array(fileFilter);
        _metadataFileRef.browse(fileTypes);
    }

    private function metadataSelectHandler(evt:Event):void {
        if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
            _externalIdpCoreSection.metadataFile.prompt = null;
            _selectedMetadataFiles = new ArrayCollection();
            _selectedMetadataFiles.addItem(_metadataFileRef.name);
            _externalIdpCoreSection.metadataFile.selectedIndex = 0;
            _externalIdpCoreSection.lblUploadMsg.text = "";
            _externalIdpCoreSection.lblUploadMsg.visible = false;
        } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
            _externalSpCoreSection.metadataFile.prompt = null;
            _selectedMetadataFiles = new ArrayCollection();
            _selectedMetadataFiles.addItem(_metadataFileRef.name);
            _externalSpCoreSection.metadataFile.selectedIndex = 0;
            _externalSpCoreSection.lblUploadMsg.text = "";
            _externalSpCoreSection.lblUploadMsg.visible = false;
        }

        _dirty = true;
        disableExportButtons();
    }

    private function uploadMetadataCompleteHandler(event:Event):void {
        _uploadedMetadata = _metadataFileRef.data;
        _uploadedMetadataName = _metadataFileRef.name;

        _metadataFileRef = null;
        _selectedMetadataFiles = new ArrayCollection();

        if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
            _externalIdpCoreSection.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "externalIdentityProv.metadata.uploadSuccess");            
            _externalIdpCoreSection.lblUploadMsg.setStyle("color", "Green");
            _externalIdpCoreSection.lblUploadMsg.visible = true;
            _externalIdpCoreSection.fadeFx.play([_externalIdpCoreSection.lblUploadMsg]);
            _externalIdpCoreSection.metadataFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "externalIdentityProv.metadata.browseFile");
            updateExternalIdentityProvider();
        } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
            _externalSpCoreSection.lblUploadMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "externalServiceProv.metadata.uploadSuccess");
            _externalSpCoreSection.lblUploadMsg.setStyle("color", "Green");
            _externalSpCoreSection.lblUploadMsg.visible = true;
            _externalSpCoreSection.fadeFx.play([_externalSpCoreSection.lblUploadMsg]);
            _externalSpCoreSection.metadataFile.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "externalServiceProv.metadata.browseFile");            
            updateExternalServiceProvider();
        }
    }

    private function resetUploadMetadataFields():void {
        _metadataFileRef = null;
        _selectedMetadataFiles = new ArrayCollection();
        _uploadedMetadata = null;
        _uploadedMetadataName = null;
    }

    private function updateMetadataSection(resp:GetMetadataInfoResponse):void {
        if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
            // entity id
            _externalIdpContractSection.entityId.text = resp.entityId;

            // profiles
            if (resp.ssoEnabled)
                _externalIdpContractSection.samlProfileSSOCheck.selected = true;
            if (resp.sloEnabled)
                _externalIdpContractSection.samlProfileSLOCheck.selected = true;

            // bindings
            if (resp.postEnabled)
                _externalIdpContractSection.samlBindingHttpPostCheck.selected = true;
            if (resp.redirectEnabled)
                _externalIdpContractSection.samlBindingHttpRedirectCheck.selected = true;
            if (resp.artifactEnabled)
                _externalIdpContractSection.samlBindingArtifactCheck.selected = true;
            if (resp.soapEnabled)
                _externalIdpContractSection.samlBindingSoapCheck.selected = true;

            if (resp.wantAuthnRequestsSigned)
                _externalIdpContractSection.wantAuthnRequestsSignedCheck.selected = true;
            
            // signing certificate
            if (resp.signingCertIssuerDN != null) {
                _externalIdpContractSection.signAuthAssertionCheck.selected = true;
                _externalIdpCertificateSection.signingCertIssuerDN.text = resp.signingCertIssuerDN;
            }
            if (resp.signingCertSubjectDN != null)
                _externalIdpCertificateSection.signingCertSubjectDN.text = resp.signingCertSubjectDN;
            if (resp.signingCertNotBefore != null)
                _externalIdpCertificateSection.signingCertNotBefore.text = _externalIdpCertificateSection.dateFormatter.format(resp.signingCertNotBefore);
            if (resp.signingCertNotAfter != null)
                _externalIdpCertificateSection.signingCertNotAfter.text = _externalIdpCertificateSection.dateFormatter.format(resp.signingCertNotAfter);

            // encryption certificate
            if (resp.encryptionCertIssuerDN != null) {
                _externalIdpContractSection.encryptAuthAssertionCheck.selected = true;
                _externalIdpCertificateSection.encryptionCertIssuerDN.text = resp.encryptionCertIssuerDN;
            }
            if (resp.encryptionCertSubjectDN != null)
                _externalIdpCertificateSection.encryptionCertSubjectDN.text = resp.encryptionCertSubjectDN;
            if (resp.encryptionCertNotBefore != null)
                _externalIdpCertificateSection.encryptionCertNotBefore.text = _externalIdpCertificateSection.dateFormatter.format(resp.encryptionCertNotBefore);
            if (resp.encryptionCertNotAfter != null)
                _externalIdpCertificateSection.encryptionCertNotAfter.text = _externalIdpCertificateSection.dateFormatter.format(resp.encryptionCertNotAfter);

        } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
            // entity id
            _externalSpContractSection.entityId.text = resp.entityId;

            // profiles
            if (resp.ssoEnabled)
                _externalSpContractSection.samlProfileSSOCheck.selected = true;
            if (resp.sloEnabled)
                _externalSpContractSection.samlProfileSLOCheck.selected = true;

            // bindings
            if (resp.postEnabled)
                _externalSpContractSection.samlBindingHttpPostCheck.selected = true;
            if (resp.redirectEnabled)
                _externalSpContractSection.samlBindingHttpRedirectCheck.selected = true;
            if (resp.artifactEnabled)
                _externalSpContractSection.samlBindingArtifactCheck.selected = true;
            if (resp.soapEnabled)
                _externalSpContractSection.samlBindingSoapCheck.selected = true;

            if (resp.signAuthnRequests)
                _externalSpContractSection.signAuthnRequestsCheck.selected = true;
            if (resp.wantAssertionSigned)
                _externalSpContractSection.wantAssertionSignedCheck.selected = true;

            // signing certificate
            if (resp.signingCertIssuerDN != null) {
                _externalSpContractSection.signAuthAssertionCheck.selected = true;
                _externalSpCertificateSection.signingCertIssuerDN.text = resp.signingCertIssuerDN;
            }
            if (resp.signingCertSubjectDN != null)
                _externalSpCertificateSection.signingCertSubjectDN.text = resp.signingCertSubjectDN;
            if (resp.signingCertNotBefore != null)
                _externalSpCertificateSection.signingCertNotBefore.text = _externalSpCertificateSection.dateFormatter.format(resp.signingCertNotBefore);
            if (resp.signingCertNotAfter != null)
                _externalSpCertificateSection.signingCertNotAfter.text = _externalSpCertificateSection.dateFormatter.format(resp.signingCertNotAfter);

            // encryption certificate
            if (resp.encryptionCertIssuerDN != null) {
                _externalSpContractSection.encryptAuthAssertionCheck.selected = true;
                _externalSpCertificateSection.encryptionCertIssuerDN.text = resp.encryptionCertIssuerDN;
            }
            if (resp.encryptionCertSubjectDN != null)
                _externalSpCertificateSection.encryptionCertSubjectDN.text = resp.encryptionCertSubjectDN;
            if (resp.encryptionCertNotBefore != null)
                _externalSpCertificateSection.encryptionCertNotBefore.text = _externalSpCertificateSection.dateFormatter.format(resp.encryptionCertNotBefore);
            if (resp.encryptionCertNotAfter != null)
                _externalSpCertificateSection.encryptionCertNotAfter.text = _externalSpCertificateSection.dateFormatter.format(resp.encryptionCertNotAfter);
        }
    }

    private function updateInternalProviderCertificateSection(resp:GetCertificateInfoResponse):void {
        if (_certificateSection != null && (_currentIdentityApplianceElement is IdentityProvider ||
                _currentIdentityApplianceElement is ServiceProvider)) {

            // signing certificate
            if (resp.signingCertIssuerDN != null)
                _certificateSection.signingCertIssuerDN.text = resp.signingCertIssuerDN;
            if (resp.signingCertSubjectDN != null)
                _certificateSection.signingCertSubjectDN.text = resp.signingCertSubjectDN;
            if (resp.signingCertNotBefore != null)
                _certificateSection.signingCertNotBefore.text = _certificateSection.dateFormatter.format(resp.signingCertNotBefore);
            if (resp.signingCertNotAfter != null)
                _certificateSection.signingCertNotAfter.text = _certificateSection.dateFormatter.format(resp.signingCertNotAfter);

            // encryption certificate
            if (resp.encryptionCertIssuerDN != null)
                _certificateSection.encryptionCertIssuerDN.text = resp.encryptionCertIssuerDN;
            if (resp.encryptionCertSubjectDN != null)
                _certificateSection.encryptionCertSubjectDN.text = resp.encryptionCertSubjectDN;
            if (resp.encryptionCertNotBefore != null)
                _certificateSection.encryptionCertNotBefore.text = _certificateSection.dateFormatter.format(resp.encryptionCertNotBefore);
            if (resp.encryptionCertNotAfter != null)
                _certificateSection.encryptionCertNotAfter.text = _certificateSection.dateFormatter.format(resp.encryptionCertNotAfter);
        }
    }

    // WiKID upload functions
    private function wikidCAStoreBrowseHandler(event:MouseEvent):void {
        if (_wikidCAStoreFileRef == null) {
            _wikidCAStoreFileRef = new FileReference();
            _wikidCAStoreFileRef.addEventListener(Event.SELECT, wikidCAStoreFileSelectHandler);
            _wikidCAStoreFileRef.addEventListener(Event.COMPLETE, wikidCAStoreUploadCompleteHandler);
        }
        //var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        //var fileTypes:Array = new Array(fileFilter);
        //_wikidCAStoreFileRef.browse(fileTypes);
        _wikidCAStoreFileRef.browse();
    }

    private function wikidCAStoreFileSelectHandler(event:Event):void {
        _wikidAuthnServiceCoreSection.caStore.prompt = null;
        _selectedWikidCAStores = new ArrayCollection();
        _selectedWikidCAStores.addItem(_wikidCAStoreFileRef.name);
        _wikidAuthnServiceCoreSection.caStore.selectedIndex = 0;

        _wikidAuthnServiceCoreSection.lblCAStoreMsg.text = "";
        _wikidAuthnServiceCoreSection.lblCAStoreMsg.visible = false;

        _dirty = true;
    }

    private function wikidCAStoreUploadCompleteHandler(event:Event):void {
        _uploadedWikidCAStoreFile = _wikidCAStoreFileRef.data;
        _uploadedWikidCAStoreFileName = _wikidCAStoreFileRef.name;

        _wikidAuthnServiceCoreSection.lblCAStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.ca.store.upload.success");
        _wikidAuthnServiceCoreSection.lblCAStoreMsg.setStyle("color", "Green");
        _wikidAuthnServiceCoreSection.lblCAStoreMsg.visible = true;
        _wikidAuthnServiceCoreSection.caFadeFx.play([_wikidAuthnServiceCoreSection.lblCAStoreMsg]);

        _uploadedWikidCAStoreFile = null;
        _selectedWikidCAStores = new ArrayCollection();0
        _wikidAuthnServiceCoreSection.caStore.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.ca.store.browse");

        if (_selectedWCStores != null && _selectedWCStores.length > 0) {
            _wikidClientStoreFileRef.load();
        } else {
            saveWikidAuthnService();
        }
    }

    private function wikidClientStoreBrowseHandler(event:MouseEvent):void {
        if (_wikidClientStoreFileRef == null) {
            _wikidClientStoreFileRef = new FileReference();
            _wikidClientStoreFileRef.addEventListener(Event.SELECT, wikidClientStoreFileSelectHandler);
            _wikidClientStoreFileRef.addEventListener(Event.COMPLETE, wikidClientStoreUploadCompleteHandler);
        }
        var fileFilter:FileFilter = new FileFilter("PKCS#12(*.p12)", "*.p12");
        var fileTypes:Array = new Array(fileFilter);
        _wikidClientStoreFileRef.browse(fileTypes);
    }

    private function wikidClientStoreFileSelectHandler(event:Event):void {
        _wikidAuthnServiceCoreSection.wcStore.prompt = null;
        _selectedWCStores = new ArrayCollection();
        _selectedWCStores.addItem(_wikidClientStoreFileRef.name);
        _wikidAuthnServiceCoreSection.wcStore.selectedIndex = 0;

        _wikidAuthnServiceCoreSection.lblWCStoreMsg.text = "";
        _wikidAuthnServiceCoreSection.lblWCStoreMsg.visible = false;

        _dirty = true;
    }

    private function wikidClientStoreUploadCompleteHandler(event:Event):void {
        _uploadedWCStoreFile = _wikidClientStoreFileRef.data;
        _uploadedWCStoreFileName = _wikidClientStoreFileRef.name;

        _wikidAuthnServiceCoreSection.lblWCStoreMsg.text = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.wc.store.upload.success");
        _wikidAuthnServiceCoreSection.lblWCStoreMsg.setStyle("color", "Green");
        _wikidAuthnServiceCoreSection.lblWCStoreMsg.visible = true;
        _wikidAuthnServiceCoreSection.wcFadeFx.play([_wikidAuthnServiceCoreSection.lblWCStoreMsg]);

        _uploadedWCStoreFile = null;
        _selectedWCStores = new ArrayCollection();
        _wikidAuthnServiceCoreSection.wcStore.prompt = resourceManager.getString(AtricoreConsole.BUNDLE, "wikid.wc.store.browse");

        saveWikidAuthnService();
    }

    protected function clearPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();
        _tabbedPropertiesTabBar.visible = false;
        _propertySheetsViewStack.visible = false;
    }

    protected function enablePropertyTabs():void {
        _tabbedPropertiesTabBar.visible = true;
        _propertySheetsViewStack.visible = true;
    }
    private function handleSectionChange(event:Event):void {
        _dirty = true;
        disableExportButtons();
    }

    private function disableExportButtons():void {
        if (_certificateSection != null)
            _certificateSection.btnExportCertificate.enabled = false;
        if (_ipContractSection != null)
            _ipContractSection.btnExportMetadata.enabled = false;
        if (_spContractSection != null)
            _spContractSection.btnExportMetadata.enabled = false;
        if (_federatedConnectionIDPChannelSection != null)
            _federatedConnectionIDPChannelSection.btnExportMetadata.enabled = false;
        if (_federatedConnectionSPChannelSection != null)
            _federatedConnectionSPChannelSection.btnExportMetadata.enabled = false;
        if (_externalIdpContractSection != null)
            _externalIdpContractSection.btnExportMetadata.enabled = false;
        if (_externalSpContractSection != null)
            _externalSpContractSection.btnExportMetadata.enabled = false;
        if (_salesforceContractSection != null)
            _salesforceContractSection.btnExportMetadata.enabled = false;
        if (_googleAppsContractSection != null)
            _googleAppsContractSection.btnExportMetadata.enabled = false;
        if (_sugarCRMContractSection != null)
            _sugarCRMContractSection.btnExportMetadata.enabled = false;
    }

    private function handleHostChange(execEnvView:Object):void {
        if (execEnvView.selectedHost.selectedItem.data == ExecEnvType.REMOTE.name) {
            execEnvView.locationItem.includeInLayout = true;
            execEnvView.locationItem.visible = true;
        } else {
            execEnvView.locationItem.includeInLayout = false;
            execEnvView.locationItem.visible = false;
        }
    }
    
    protected function get view():PropertySheetView
    {
        return viewComponent as PropertySheetView;
    }

   public function validate(revalidate : Boolean) : Boolean {
      return FormUtility.validateAll(_validators, revalidate);
   }

    public function resetValidation() : void {
      for each(var validator : Validator in _validators) {
         validator.source.errorString = "";
      }
    }

    /**
     * Used instead of matchValidator because changing the confirmField doesn't delete the error message
     * from password field (although the next button becomes enabled)
     * @return
     */
    private function comparePasswords(adminPass:TextInput, confirmAdminPass:TextInput):Boolean {
        if (adminPass.text == "") {
            adminPass.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "compare.pass.required");
            return false;
        }
        if (confirmAdminPass.text == "") {
            confirmAdminPass.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "compare.pass.required");
            return false;
        }
        if (adminPass.text != confirmAdminPass.text) {
            adminPass.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "compare.pass.not.identical");
            return false;
        }
        confirmAdminPass.errorString = "";
        adminPass.errorString = "";
        return true;
    }

    private function handleUseInheritedIDPSettingsChange(event:Event):void {
        setSpChannelFields();
        if(_federatedConnectionSPChannelSection.useInheritedIDPSettings.selected){
            reflectIdpSettingsInSpChannelTab();
        } else {
            _validators.push(_federatedConnectionSPChannelSection.portValidator);
            _validators.push(_federatedConnectionSPChannelSection.domainValidator);
            _validators.push(_federatedConnectionSPChannelSection.contextValidator);
            _validators.push(_federatedConnectionSPChannelSection.pathValidator);
        }
        _dirty = true;
        disableExportButtons();
    }

    private function handleUseInheritedSPSettingsChange(event:Event):void {
        setIdpChannelFields();
        if(_federatedConnectionIDPChannelSection.useInheritedSPSettings.selected){
            reflectSPSettingsInIdpChannelTab();
        } else {
            _validators.push(_federatedConnectionIDPChannelSection.portValidator);
            _validators.push(_federatedConnectionIDPChannelSection.domainValidator);
            _validators.push(_federatedConnectionIDPChannelSection.contextValidator);
            _validators.push(_federatedConnectionIDPChannelSection.pathValidator);
        }
        _dirty = true;
        disableExportButtons();
    }

    private function reflectSPSettingsInIdpChannelTab():void {
        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;        
        if(connection.roleA is ServiceProvider){
            var sp:ServiceProvider = connection.roleA as ServiceProvider;
        } else if (connection.roleB is ServiceProvider){
            sp = connection.roleB as ServiceProvider;
        }

        _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.selected = sp.signAuthenticationRequests;
        _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.selected = sp.wantAssertionSigned;

        _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected = false;
        _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected = false;
        _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected = false;
        _federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected = false;
        _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = false;
        _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = false;

        for each (var tmpBinding:Binding in sp.activeBindings) {
            if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                _federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected = true;
            }
        }
        for each (var tmpProfile:Profile in sp.activeProfiles) {
            if (tmpProfile.name == Profile.SSO.name) {
                _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = true;
            }
            if (tmpProfile.name == Profile.SSO_SLO.name) {
                _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = true;
            }
        }

        // set provider location
        for (var j:int = 0; j < _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider.length; j++) {
            if (sp.location.protocol == _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider[j].data) {
                _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.selectedIndex = j;
                break;
            }
        }
        _federatedConnectionIDPChannelSection.idpChannelLocationDomain.text = sp.location.host;
        _federatedConnectionIDPChannelSection.idpChannelLocationPort.text = sp.location.port.toString() != "0" ? sp.location.port.toString() : "";
        _federatedConnectionIDPChannelSection.idpChannelLocationContext.text = sp.location.context;
        _federatedConnectionIDPChannelSection.idpChannelLocationPath.text = sp.location.uri;

        // set account linkage policy
        if (_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider != null) {
            if (sp.accountLinkagePolicy != null) {
                for (var k:int=0; k < _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider.length; k++) {
                    if (_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider[k].name == sp.accountLinkagePolicy.name) {
                        _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = k;
                        break;
                    }
                }
            } else {
                for (var l:int=0; l < _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider.length; l++) {
                    if (_federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.dataProvider[l].linkEmitterType.toString() == AccountLinkEmitterType.ONE_TO_ONE.toString()) {
                        _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = l;
                        break;
                    }
                }
            }
        }

        // set identity mapping policy
        if (_federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider != null) {
            if (sp.identityMappingPolicy != null) {
                for (var m:int=0; m < _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider.length; m++) {
                    if (_federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider[m].name == sp.identityMappingPolicy.name) {
                        _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.selectedIndex = m;
                        break;
                    }
                }
            } else {
                for (var n:int=0; n < _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider.length; n++) {
                    if (_federatedConnectionIDPChannelSection.identityMappingPolicyCombo.dataProvider[n].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                        _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.selectedIndex = n;
                        break;
                    }
                }
            }
        }

        _federatedConnectionIDPChannelSection.useInheritedSPSettings.selected = true;
        setIdpChannelFields();
    }

    private function reflectIdpSettingsInSpChannelTab():void {
        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
        if(connection.roleA is IdentityProvider){
            var idp:IdentityProvider = connection.roleA as IdentityProvider;
        } else if (connection.roleB is IdentityProvider){
            idp = connection.roleB as IdentityProvider;
        }

        _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.selected = idp.wantAuthnRequestsSigned;
        
        _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.selected = false;
        _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.selected = false;
        _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.selected = false;
        _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.selected = false;
        _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected = false;
        _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected = false;

        for each(var tmpBinding:Binding in idp.activeBindings) {
            if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.selected = true;
            }
        }
        for each(var tmpProfile:Profile in idp.activeProfiles) {
            if (tmpProfile.name == Profile.SSO.name) {
                _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected = true;
            }
            if (tmpProfile.name == Profile.SSO_SLO.name) {
                _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected = true;
            }
        }

        // set provider location
        for (var i:int = 0; i < _federatedConnectionSPChannelSection.spChannelLocationProtocol.dataProvider.length; i++) {
            if (idp.location.protocol == _federatedConnectionSPChannelSection.spChannelLocationProtocol.dataProvider[i].data) {
                _federatedConnectionSPChannelSection.spChannelLocationProtocol.selectedIndex = i;
                break;
            }
        }
        _federatedConnectionSPChannelSection.spChannelLocationDomain.text = idp.location.host;
        _federatedConnectionSPChannelSection.spChannelLocationPort.text = idp.location.port.toString() != "0" ? idp.location.port.toString() : "";
        _federatedConnectionSPChannelSection.spChannelLocationContext.text = idp.location.context;
        _federatedConnectionSPChannelSection.spChannelLocationPath.text = idp.location.uri;

        // select authentication mechanism (currently there is always only one selected authn. mechanism)
        var selectedAuthnMechanism:String = "basic";
        if (idp.authenticationMechanisms != null && idp.authenticationMechanisms.length > 0) {
            var authnMechanism:AuthenticationMechanism  = idp.authenticationMechanisms.getItemAt(0) as AuthenticationMechanism;
            if (authnMechanism is BasicAuthentication)
                selectedAuthnMechanism = "basic"
            else if (authnMechanism is TwoFactorAuthentication)
                selectedAuthnMechanism = "2factor";
            else if (authnMechanism is BindAuthentication)
                selectedAuthnMechanism = "bind";
        }
        for (var j:int = 0; j < _federatedConnectionSPChannelSection.spChannelAuthMechanism.dataProvider.length; j++) {
            if (_federatedConnectionSPChannelSection.spChannelAuthMechanism.dataProvider[j].data == selectedAuthnMechanism) {
                _federatedConnectionSPChannelSection.spChannelAuthMechanism.selectedIndex = j;
                break;
            }
        }

        _federatedConnectionSPChannelSection.useInheritedIDPSettings.selected = true;
        setSpChannelFields();
    }

    private function setIdpChannelFields():void {
        if(_federatedConnectionIDPChannelSection.useInheritedSPSettings.selected){
//            reflectSPSettingsInIdpChannelTab();
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.enabled = false;

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingSoapCheck.enabled = false;

            _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.enabled = false;
            _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.enabled = false;

//            view.authMechanism.enabled = false;
//            view.configureAuthMechanism.enabled = false;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = false;
            //_federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = false;
            _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.enabled = false;

            _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.enabled = false;
            _federatedConnectionIDPChannelSection.idpChannelLocationDomain.enabled = false;
            _federatedConnectionIDPChannelSection.idpChannelLocationPort.enabled = false;
            _federatedConnectionIDPChannelSection.idpChannelLocationContext.enabled = false;
            _federatedConnectionIDPChannelSection.idpChannelLocationPath.enabled = false;
        } else {
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.enabled = true;

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingSoapCheck.enabled = true;

            _federatedConnectionIDPChannelSection.signAuthnRequestsCheck.enabled = true;
            _federatedConnectionIDPChannelSection.wantAssertionSignedCheck.enabled = true;

//            view.authMechanism.enabled = true;
//            view.configureAuthMechanism.enabled = true;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = true;
            //_federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = true;
            _federatedConnectionIDPChannelSection.identityMappingPolicyCombo.enabled = true;

            _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.enabled = true;
            _federatedConnectionIDPChannelSection.idpChannelLocationDomain.enabled = true;
            _federatedConnectionIDPChannelSection.idpChannelLocationPort.enabled = true;
            _federatedConnectionIDPChannelSection.idpChannelLocationContext.enabled = true;
            _federatedConnectionIDPChannelSection.idpChannelLocationPath.enabled = true;
        }
    }

    private function setSpChannelFields():void {
        if(_federatedConnectionSPChannelSection.useInheritedIDPSettings.selected){
//            reflectIdpSettingsInSpChannelTab();
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.enabled = false;

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.enabled = false;

            _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.enabled = false;

            _federatedConnectionSPChannelSection.spChannelAuthContractCombo.enabled = false;
            _federatedConnectionSPChannelSection.spChannelAuthMechanism.enabled = false;
            _federatedConnectionSPChannelSection.spChannelAuthAssertionEmissionPolicyCombo.enabled = false;

            _federatedConnectionSPChannelSection.spChannelLocationProtocol.enabled = false;
            _federatedConnectionSPChannelSection.spChannelLocationDomain.enabled = false;
            _federatedConnectionSPChannelSection.spChannelLocationPort.enabled = false;
            _federatedConnectionSPChannelSection.spChannelLocationContext.enabled = false;
            _federatedConnectionSPChannelSection.spChannelLocationPath.enabled = false;
        } else {
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.enabled = true;

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.enabled = true;

            _federatedConnectionSPChannelSection.wantAuthnRequestsSignedCheck.enabled = true;
            
            _federatedConnectionSPChannelSection.spChannelAuthContractCombo.enabled = true;
            _federatedConnectionSPChannelSection.spChannelAuthMechanism.enabled = false; //dont enable auth mechanism
            _federatedConnectionSPChannelSection.spChannelAuthAssertionEmissionPolicyCombo.enabled = true;

            _federatedConnectionSPChannelSection.spChannelLocationProtocol.enabled = true;
            _federatedConnectionSPChannelSection.spChannelLocationDomain.enabled = true;
            _federatedConnectionSPChannelSection.spChannelLocationPort.enabled = true;
            _federatedConnectionSPChannelSection.spChannelLocationContext.enabled = true;
            _federatedConnectionSPChannelSection.spChannelLocationPath.enabled = true;
        }
    }


}
}