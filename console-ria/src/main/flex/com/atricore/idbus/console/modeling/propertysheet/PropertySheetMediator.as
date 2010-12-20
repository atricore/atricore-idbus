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
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.modeling.diagram.model.request.ActivateExecutionEnvironmentRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckFoldersRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.modeling.diagram.model.response.CheckFoldersResponse;
import com.atricore.idbus.console.modeling.main.controller.FolderExistsCommand;
import com.atricore.idbus.console.modeling.main.controller.FoldersExistsCommand;
import com.atricore.idbus.console.modeling.main.controller.GetCertificateInfoCommand;
import com.atricore.idbus.console.modeling.main.controller.GetMetadataInfoCommand;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.certificate.CertificateSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.ExecutionEnvironmentActivationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.alfresco.AlfrescoExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.apache.ApacheExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.javaee.JavaEEExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jboss.JBossExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jbossportal.JBossPortalExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.liferayportal.LiferayPortalExecEnvCoreSection;
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
import com.atricore.idbus.console.modeling.propertysheet.view.identitylookup.IdentityLookupCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.identityvault.EmbeddedDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.BasicAuthenticationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.jossoactivation.JOSSOActivationCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.xmlidentitysource.XmlIdentitySourceCoreSection;
import com.atricore.idbus.console.services.dto.AccountLinkagePolicy;
import com.atricore.idbus.console.services.dto.AlfrescoExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.EmbeddedIdentitySource;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ExternalIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
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
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.SamlR2ProviderConfig;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WASCEExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WebserverExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;
import com.atricore.idbus.console.services.spi.response.GetCertificateInfoResponse;
import com.atricore.idbus.console.services.spi.response.GetMetadataInfoResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.text.TextFormat;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.core.IUITextField;
import mx.core.mx_internal;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
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
    private var _phpBBExecEnvCoreSection:PhpBBExecEnvCoreSection;
    private var _webserverExecEnvCoreSection:WebserverExecEnvCoreSection;
    private var _executionEnvironmentActivateSection:ExecutionEnvironmentActivationSection;
    private var _authenticationPropertyTab:Group;
    private var _basicAuthenticationSection:BasicAuthenticationSection;
    private var _certificateSection:CertificateSection;
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
            FolderExistsCommand.FOLDER_EXISTS,
            FolderExistsCommand.FOLDER_DOESNT_EXISTS,
            FoldersExistsCommand.FOLDERS_EXISTENCE_CHECKED,
            JDBCDriversListCommand.SUCCESS,
            GetMetadataInfoCommand.SUCCESS,
            GetCertificateInfoCommand.SUCCESS];
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
                } else if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
                    enableExternalIdentityProviderPropertyTabs();
                } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
                    enableExternalServiceProviderPropertyTabs();
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
                    _execEnvHomeDir.errorString = "Directory doesn't exist";
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
                                    _liferayExecEnvCoreSection.homeDirectory.errorString = "Directory doesn't exist";
                                }
                                if (_liferayExecEnvCoreSection.containerPath.text == invalidFolder) {
                                    _liferayExecEnvCoreSection.containerPath.errorString = "Directory doesn't exist";
                                }
                            } else if (currentElement is AlfrescoExecutionEnvironment){
                                if (_alfrescoExecEnvCoreSection.homeDirectory.text == invalidFolder) {
                                    _alfrescoExecEnvCoreSection.homeDirectory.errorString = "Directory doesn't exist";
                                }
                                if (_alfrescoExecEnvCoreSection.tomcatInstallDir.text == invalidFolder) {
                                    _alfrescoExecEnvCoreSection.tomcatInstallDir.errorString = "Directory doesn't exist";
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
                        "There was an error loading JDBC drivers list.");
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

            /*
            for each(var authMech:AuthenticationMechanism in identityProvider.authenticationMechanisms){
                if(authMech is BasicAuthentication){
                    var liv:ListItemValueObject = _ipCoreSection.authMechanismColl.getItemAt(0) as ListItemValueObject;
                    liv.isSelected = true;
                }
                //TODO ADD OTHER AUTH MECHANISMS
            }

//            for each(var liv:ListItemValueObject in  _ipCoreSection.authMechanismCombo.dataProvider){
//                if(liv.isSelected){
//                    if(identityProvider.authenticationMechanisms == null){
//                        identityProvider.authenticationMechanisms = new ArrayCollection();
//                    }
//                    switch(liv.name){
//                        case "basic":
//                            var basicAuth:BasicAuthentication = new BasicAuthentication();
//                            basicAuth.name = identityProvider.name + "-basic-authn";
//                            //TODO MAKE CONFIGURABLE
//                            basicAuth.hashAlgorithm = "MD5";
//                            basicAuth.hashEncoding = "HEX";
//                            basicAuth.ignoreUsernameCase = false;
//                            identityProvider.authenticationMechanisms.addItem(basicAuth);
//                            break;
//                        case "strong":
//                            break;
//                    }
//                }
//            }
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
            _ipContractSection.signAuthAssertionCheck.selected = identityProvider.signAuthenticationAssertions;
            _ipContractSection.encryptAuthAssertionCheck.selected = identityProvider.encryptAuthenticationAssertions;

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

            _ipContractSection.signAuthAssertionCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.encryptAuthAssertionCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _ipContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
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
            identityProvider.signAuthenticationAssertions = _ipContractSection.signAuthAssertionCheck.selected;
            identityProvider.encryptAuthenticationAssertions = _ipContractSection.encryptAuthAssertionCheck.selected;

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
    }

    private function handleAuthenticationTabClick():void {
        if (_ipCoreSection.authMechanismCombo.selectedItem.data == "basic") {
            _basicAuthenticationSection = new BasicAuthenticationSection();
            _authenticationPropertyTab.addElement(_basicAuthenticationSection);

            _basicAuthenticationSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleBasicAuthenticationPropertyTabCreationComplete);
            _authenticationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleBasicAuthenticationPropertyTabRollOut);
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
                for (var i:int = 0; i < _basicAuthenticationSection.hashEncoding.dataProvider.length; i++) {
                    if (_basicAuthenticationSection.hashEncoding.dataProvider[i].data == basicAuthentication.hashEncoding) {
                        _basicAuthenticationSection.hashEncoding.selectedIndex = i;
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

            if (serviceProvider.accountLinkagePolicy != null) {
                if (serviceProvider.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = 0;
                } else if (serviceProvider.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.LOCAL.toString()) {
                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = 1;
                } else if (serviceProvider.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.MERGED.toString()) {
                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = 2;
                }
            }

            /*for (var i:int = 0; i < _spCoreSection.accountLinkagePolicyCombo.dataProvider.length; i++) {
                if (serviceProvider.accountLinkagePolicy != null &&
                        _spCoreSection.accountLinkagePolicyCombo.dataProvider[i].name == serviceProvider.accountLinkagePolicy.name) {
                    _spCoreSection.accountLinkagePolicyCombo.selectedIndex = i;
                    break;
                }
            }*/

            _spCoreSection.serviceProvName.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.serviceProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.accountLinkagePolicyCombo.addEventListener(Event.CHANGE, handleSectionChange);

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

            var accountLinkagePolicy:AccountLinkagePolicy = serviceProvider.accountLinkagePolicy;
            if (accountLinkagePolicy == null) {
                accountLinkagePolicy = new AccountLinkagePolicy();
            }
            accountLinkagePolicy.name = _spCoreSection.accountLinkagePolicyCombo.selectedItem.name;
            var selectedPolicy:String = _spCoreSection.accountLinkagePolicyCombo.selectedItem.data;
            if (selectedPolicy == "theirs") {
                accountLinkagePolicy.mappingType = IdentityMappingType.REMOTE;
            } else if (selectedPolicy == "ours") {
                accountLinkagePolicy.mappingType = IdentityMappingType.LOCAL;
            } else if (selectedPolicy == "aggregate") {
                accountLinkagePolicy.mappingType = IdentityMappingType.MERGED;
            }
            serviceProvider.accountLinkagePolicy = accountLinkagePolicy;

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
            //_spContractSection.signAuthRequestCheck.selected = serviceProvider.signAuthenticationAssertions;
            //_spContractSection.encryptAuthRequestCheck.selected = serviceProvider.encryptAuthenticationAssertions;

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

            _spContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
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

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _applianceSaved = false;
            _dirty = false;
        }
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
                    _certificateSection.lblUploadMsg.text = "You must select a keystore!!!";
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
        var identityProvider:ExternalIdentityProvider;

        identityProvider = _currentIdentityApplianceElement as ExternalIdentityProvider;

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
        var serviceProvider:ExternalServiceProvider;

        serviceProvider = _currentIdentityApplianceElement as ExternalServiceProvider;

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
            for (var i:int = 0; i < _ldapIdentitySourceCoreSection.ldapSearchScope.dataProvider.length; i++) {
                if (_ldapIdentitySourceCoreSection.ldapSearchScope.dataProvider[i].data == ldapIdentitySource.ldapSearchScope) {
                    _ldapIdentitySourceCoreSection.ldapSearchScope.selectedIndex = i;
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

            if (spChannel.overrideProviderSetup) {
                _federatedConnectionSPChannelSection.useInheritedIDPSettings.selected = false;
            }
            setSpChannelFields();

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelAuthMechanism.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);

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

            if (idpChannel.overrideProviderSetup) {
                _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlBindingSoapCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = false;
                _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = false;
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

            if (idpChannel.overrideProviderSetup) {
                _federatedConnectionIDPChannelSection.useInheritedSPSettings.selected = false;
            }
            setIdpChannelFields();

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.idpChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);

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

            _tomcatExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _tomcatExecEnvCoreSection.selectedHost.enabled = false;

            for(var i:int=0; i < _tomcatExecEnvCoreSection.platform.dataProvider.length; i++){
                if(_tomcatExecEnvCoreSection.platform.dataProvider[i].data == tomcatExecEnv.platformId){
                    _tomcatExecEnvCoreSection.platform.selectedIndex = i;
                    break;
                }
            }
            _tomcatExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _tomcatExecEnvCoreSection.homeDirectory.text = tomcatExecEnv.installUri;

            _tomcatExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _tomcatExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_tomcatExecEnvCoreSection.nameValidator);
            _validators.push(_tomcatExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleTomcatExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _tomcatExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = tomcatSave;
            _execEnvHomeDir = _tomcatExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _tomcatExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _tomcatExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function tomcatSave(): void {
        var tomcatExecEnv:TomcatExecutionEnvironment = projectProxy.currentIdentityApplianceElement as TomcatExecutionEnvironment;
        tomcatExecEnv.name = _tomcatExecEnvCoreSection.executionEnvironmentName.text;
        tomcatExecEnv.description = _tomcatExecEnvCoreSection.executionEnvironmentDescription.text;
        tomcatExecEnv.platformId = _tomcatExecEnvCoreSection.platform.selectedItem.data;
        tomcatExecEnv.installUri = _tomcatExecEnvCoreSection.homeDirectory.text;

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
            _weblogicExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _weblogicExecEnvCoreSection.homeDirectory.text = weblogicExecEnv.installUri;
            _weblogicExecEnvCoreSection.domain.text = weblogicExecEnv.domain;

            _weblogicExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _weblogicExecEnvCoreSection.domain.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_weblogicExecEnvCoreSection.nameValidator);
            _validators.push(_weblogicExecEnvCoreSection.homeDirValidator);
            _validators.push(_weblogicExecEnvCoreSection.domainValidator);
        }
    }

    private function handleWeblogicExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _weblogicExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = weblogicSave;
            _execEnvHomeDir = _weblogicExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _weblogicExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _weblogicExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function weblogicSave(): void {
         // bind model
        var weblogicExecEnv:WeblogicExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WeblogicExecutionEnvironment;
        weblogicExecEnv.name = _weblogicExecEnvCoreSection.executionEnvironmentName.text;
        weblogicExecEnv.description = _weblogicExecEnvCoreSection.executionEnvironmentDescription.text;
        weblogicExecEnv.platformId = _weblogicExecEnvCoreSection.platform.selectedItem.data;
        weblogicExecEnv.installUri = _weblogicExecEnvCoreSection.homeDirectory.text;
        weblogicExecEnv.domain = _weblogicExecEnvCoreSection.domain.text;

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
            _jbossPortalExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _jbossPortalExecEnvCoreSection.homeDirectory.text = jbossPortalExecEnv.installUri;

            _jbossPortalExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _jbossPortalExecEnvCoreSection.selectedHost.enabled = false;

            _jbossPortalExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossPortalExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_jbossPortalExecEnvCoreSection.nameValidator);
            _validators.push(_jbossPortalExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleJBossPortalExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _jbossPortalExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = jbossPortalSave;
            _execEnvHomeDir = _jbossPortalExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _jbossPortalExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _jbossPortalExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function jbossPortalSave(): void {
         // bind model
        var jbossPortalExecEnv:JBossPortalExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JBossPortalExecutionEnvironment;
        jbossPortalExecEnv.name = _jbossPortalExecEnvCoreSection.executionEnvironmentName.text;
        jbossPortalExecEnv.description = _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.text;
        //jbossPortalExecEnv.platformId = "jbp";
        jbossPortalExecEnv.installUri = _jbossPortalExecEnvCoreSection.homeDirectory.text;

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
            _liferayExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _liferayExecEnvCoreSection.selectedHost.enabled = false;
            _liferayExecEnvCoreSection.homeDirectory.text = liferayExecEnv.installUri;
            for (var i:int=0; i < _liferayExecEnvCoreSection.containerType.dataProvider.length; i++){
                if (_liferayExecEnvCoreSection.containerType.dataProvider[i].data == liferayExecEnv.containerType) {
                    _liferayExecEnvCoreSection.containerType.selectedIndex = i;
                    break;
                }
            }
            _liferayExecEnvCoreSection.containerPath.text = liferayExecEnv.containerPath;

            _liferayExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.containerType.addEventListener(Event.CHANGE, handleSectionChange);
            _liferayExecEnvCoreSection.containerPath.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_liferayExecEnvCoreSection.nameValidator);
            _validators.push(_liferayExecEnvCoreSection.homeDirValidator);
            _validators.push(_liferayExecEnvCoreSection.containerPathValidator);
        }
    }

    private function handleLiferayExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _liferayExecEnvCoreSection.homeDirectory.errorString = "";
        _liferayExecEnvCoreSection.containerPath.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = liferaySave;
            
            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            var folders:ArrayCollection = new ArrayCollection();
            folders.addItem(_liferayExecEnvCoreSection.homeDirectory.text);
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
        liferayExecEnv.installUri = _liferayExecEnvCoreSection.homeDirectory.text;
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
            _wasceExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _wasceExecEnvCoreSection.homeDirectory.text = wasceExecEnv.installUri;

            _wasceExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _wasceExecEnvCoreSection.selectedHost.enabled = false;

            _wasceExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _wasceExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_wasceExecEnvCoreSection.nameValidator);
            _validators.push(_wasceExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWASCEExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _wasceExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = wasceSave;
            _execEnvHomeDir = _wasceExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _wasceExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _wasceExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function wasceSave(): void {
         // bind model
        var wasceExecEnv:WASCEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WASCEExecutionEnvironment;
        wasceExecEnv.name = _wasceExecEnvCoreSection.executionEnvironmentName.text;
        wasceExecEnv.description = _wasceExecEnvCoreSection.executionEnvironmentDescription.text;
        //wasceExecEnv.platformId = "wc21";
        wasceExecEnv.installUri = _wasceExecEnvCoreSection.homeDirectory.text;

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

            _jbossExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _jbossExecEnvCoreSection.selectedHost.enabled = false;

            for(var i:int=0; i < _jbossExecEnvCoreSection.platform.dataProvider.length; i++){
                if(_jbossExecEnvCoreSection.platform.dataProvider[i].data == jbossExecEnv.platformId){
                    _jbossExecEnvCoreSection.platform.selectedIndex = i;
                    break;
                }
            }
            _jbossExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _jbossExecEnvCoreSection.homeDirectory.text = jbossExecEnv.installUri;
            _jbossExecEnvCoreSection.instance.text = jbossExecEnv.instance;

            _jbossExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.platform.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _jbossExecEnvCoreSection.instance.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_jbossExecEnvCoreSection.nameValidator);
            _validators.push(_jbossExecEnvCoreSection.homeDirValidator);
            _validators.push(_jbossExecEnvCoreSection.instanceValidator);
        }
    }

    private function handleJbossExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _jbossExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = jbossSave;
            _execEnvHomeDir = _jbossExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _jbossExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _jbossExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function jbossSave(): void {
         // bind model
        var jbossExecEnv:JbossExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JbossExecutionEnvironment;
        jbossExecEnv.name = _jbossExecEnvCoreSection.executionEnvironmentName.text;
        jbossExecEnv.description = _jbossExecEnvCoreSection.executionEnvironmentDescription.text;
        jbossExecEnv.platformId = _jbossExecEnvCoreSection.platform.selectedItem.data;
        jbossExecEnv.installUri = _jbossExecEnvCoreSection.homeDirectory.text;
        jbossExecEnv.instance = _jbossExecEnvCoreSection.instance.text;

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
            _apacheExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _apacheExecEnvCoreSection.homeDirectory.text = apacheExecEnv.installUri;

            _apacheExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _apacheExecEnvCoreSection.selectedHost.enabled = false;

            _apacheExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _apacheExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_apacheExecEnvCoreSection.nameValidator);
            _validators.push(_apacheExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleApacheExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _apacheExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = apacheSave;
            _execEnvHomeDir = _apacheExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _apacheExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _apacheExecEnvCoreSection.homeDirectory.text);

        }
    }

    private function apacheSave(): void {
         // bind model
        var apacheExecEnv:ApacheExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ApacheExecutionEnvironment;
        apacheExecEnv.name = _apacheExecEnvCoreSection.executionEnvironmentName.text;
        apacheExecEnv.description = _apacheExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        apacheExecEnv.platformId = "apache";
        apacheExecEnv.installUri = _apacheExecEnvCoreSection.homeDirectory.text;

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

    private function handleWindowsIISExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;

        if (windowsIISExecEnv != null) {
            // bind view
            _windowsIISExecEnvCoreSection.executionEnvironmentName.text = windowsIISExecEnv.name;
            _windowsIISExecEnvCoreSection.executionEnvironmentDescription.text = windowsIISExecEnv.description;
            _windowsIISExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _windowsIISExecEnvCoreSection.homeDirectory.text = windowsIISExecEnv.installUri;

            _windowsIISExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _windowsIISExecEnvCoreSection.selectedHost.enabled = false;

            _windowsIISExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _windowsIISExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_windowsIISExecEnvCoreSection.nameValidator);
            _validators.push(_windowsIISExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleWindowsIISExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _windowsIISExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = windowsIISSave;
            _execEnvHomeDir = _windowsIISExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _windowsIISExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
//            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, _windowsIISExecEnvCoreSection.homeDirectory.text);
        }
    }

    private function windowsIISSave(): void {
         // bind model
        var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;
        windowsIISExecEnv.name = _windowsIISExecEnvCoreSection.executionEnvironmentName.text;
        windowsIISExecEnv.description = _windowsIISExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        windowsIISExecEnv.platformId = "iis";
        windowsIISExecEnv.installUri = _windowsIISExecEnvCoreSection.homeDirectory.text;

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
            _alfrescoExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _alfrescoExecEnvCoreSection.homeDirectory.text = alfrescoExecEnv.installUri;
            _alfrescoExecEnvCoreSection.tomcatInstallDir.text = alfrescoExecEnv.tomcatInstallDir;

            _alfrescoExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _alfrescoExecEnvCoreSection.selectedHost.enabled = false;

            _alfrescoExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);
            _alfrescoExecEnvCoreSection.tomcatInstallDir.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_alfrescoExecEnvCoreSection.nameValidator);
            _validators.push(_alfrescoExecEnvCoreSection.homeDirValidator);
            _validators.push(_alfrescoExecEnvCoreSection.containerDirValidator);
        }
    }

    private function handleAlfrescoExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _alfrescoExecEnvCoreSection.homeDirectory.errorString = "";
        _alfrescoExecEnvCoreSection.tomcatInstallDir.errorString = "";        
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = alfrescoSave;

            var cf:CheckFoldersRequest = new CheckFoldersRequest();
            var folders:ArrayCollection = new ArrayCollection();
            folders.addItem(_alfrescoExecEnvCoreSection.homeDirectory.text);
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
        alfrescoExecEnv.installUri = _alfrescoExecEnvCoreSection.homeDirectory.text;
        alfrescoExecEnv.tomcatInstallDir = _alfrescoExecEnvCoreSection.tomcatInstallDir.text;

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
            _javaEEExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _javaEEExecEnvCoreSection.homeDirectory.text = javaEEExecEnv.installUri;

            _javaEEExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _javaEEExecEnvCoreSection.selectedHost.enabled = false;

            _javaEEExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _javaEEExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_javaEEExecEnvCoreSection.nameValidator);
            _validators.push(_javaEEExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handleJavaEEExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _javaEEExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = javaEESave;
            _execEnvHomeDir = _javaEEExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _javaEEExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
        }
    }

    private function javaEESave(): void {
         // bind model
        var javaEEExecEnv:JEEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JEEExecutionEnvironment;
        javaEEExecEnv.name = _javaEEExecEnvCoreSection.executionEnvironmentName.text;
        javaEEExecEnv.description = _javaEEExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        javaEEExecEnv.platformId = "jee";
        javaEEExecEnv.installUri = _javaEEExecEnvCoreSection.homeDirectory.text;

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
        var phpBBExecEnv:PHPExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PHPExecutionEnvironment;

        if (phpBBExecEnv != null) {
            // bind view
            _phpBBExecEnvCoreSection.executionEnvironmentName.text = phpBBExecEnv.name;
            _phpBBExecEnvCoreSection.executionEnvironmentDescription.text = phpBBExecEnv.description;
            _phpBBExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _phpBBExecEnvCoreSection.homeDirectory.text = phpBBExecEnv.installUri;

            _phpBBExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _phpBBExecEnvCoreSection.selectedHost.enabled = false;

            _phpBBExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _phpBBExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_phpBBExecEnvCoreSection.nameValidator);
            _validators.push(_phpBBExecEnvCoreSection.homeDirValidator);
        }
    }

    private function handlePhpBBExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _phpBBExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = phpBBSave;
            _execEnvHomeDir = _phpBBExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _phpBBExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
        }
    }

    private function phpBBSave(): void {
         // bind model
        var phpBBExecEnv:PHPExecutionEnvironment = projectProxy.currentIdentityApplianceElement as PHPExecutionEnvironment;
        phpBBExecEnv.name = _phpBBExecEnvCoreSection.executionEnvironmentName.text;
        phpBBExecEnv.description = _phpBBExecEnvCoreSection.executionEnvironmentDescription.text;
        //TODO CHECK PLATFORM ID
        phpBBExecEnv.platformId = "phpbb";
        phpBBExecEnv.installUri = _phpBBExecEnvCoreSection.homeDirectory.text;

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
            _webserverExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _webserverExecEnvCoreSection.homeDirectory.text = webserverExecEnv.installUri;
            _webserverExecEnvCoreSection.executionEnvironmentType.text = webserverExecEnv.type;

            _webserverExecEnvCoreSection.selectedHost.selectedIndex = 0;
            _webserverExecEnvCoreSection.selectedHost.enabled = false;

            _webserverExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
            _webserverExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_webserverExecEnvCoreSection.nameValidator);
            _validators.push(_webserverExecEnvCoreSection.homeDirValidator);
            _validators.push(_webserverExecEnvCoreSection.typeValidator);
        }
    }

    private function handleWebserverExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        _webserverExecEnvCoreSection.homeDirectory.errorString = "";
        if (_dirty && validate(true)) {
            _execEnvSaveFunction = webserverSave;
            _execEnvHomeDir = _webserverExecEnvCoreSection.homeDirectory;

            var cif:CheckInstallFolderRequest = new CheckInstallFolderRequest();
            cif.homeDir = _webserverExecEnvCoreSection.homeDirectory.text;
            cif.environmentName = "n/a";
            sendNotification(ApplicationFacade.CHECK_INSTALL_FOLDER_EXISTENCE, cif);
        }
    }

    private function webserverSave(): void {
         // bind model
        var webserverExecEnv:WebserverExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WebserverExecutionEnvironment;
        webserverExecEnv.name = _webserverExecEnvCoreSection.executionEnvironmentName.text;
        webserverExecEnv.description = _webserverExecEnvCoreSection.executionEnvironmentDescription.text;
        webserverExecEnv.type = _webserverExecEnvCoreSection.executionEnvironmentType.text;
        //TODO CHECK PLATFORM ID
        webserverExecEnv.platformId = "web";
        webserverExecEnv.installUri = _webserverExecEnvCoreSection.homeDirectory.text;

        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        _applianceSaved = false;
        _dirty = false;
    }

    private function handleExecEnvActivationPropertyTabCreationComplete(event:Event) {
        var execEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        if (execEnv != null) {
            _executionEnvironmentActivateSection.replaceConfFiles.selected = execEnv.overwriteOriginalSetup;
            _executionEnvironmentActivateSection.installSamples.selected = execEnv.installDemoApps;
            if (execEnv is LiferayExecutionEnvironment || execEnv is AlfrescoExecutionEnvironment) {
                _executionEnvironmentActivateSection.installSamples.selected = false;
                _executionEnvironmentActivateSection.installSamples.enabled = false;
            }
            //TODO add click handler for _executionEnvironmentActivateSection.activate checkbox
            _executionEnvironmentActivateSection.reactivate.addEventListener(MouseEvent.CLICK, reactivateClickHandler);
        }
    }

    private function handleExecEnvActivationPropertyTabRollOut(event:Event):void {
        if (projectProxy.currentIdentityAppliance.state != IdentityApplianceState.DISPOSED.toString()){
            activateExecutionEnvironment(event);
        }
    }

    private function reactivateClickHandler(event:Event):void {
        if(!_applianceSaved){
            Alert.show("The Identity Appliance needs to be saved first in order to be able to run activation procedures onto execution environments",
                    "Information", Alert.OK, null, null, null, Alert.OK);
            _executionEnvironmentActivateSection.reactivate.selected = false;
        }
    }

    private function activateExecutionEnvironment(event:Event):void {
        var currentExecEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        if(_executionEnvironmentActivateSection.reactivate.selected && _applianceSaved){
            var text:String = currentExecEnv.name +  " execution environment is about to be activated.\nYou must restart the execution environment for the changes to take effect.";
            var alert:Alert = Alert.show(text,
                    "Confirm Activation", Alert.OK | Alert.CANCEL, null, activationConfirmationHandler, null, Alert.OK);
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
    }

    private function uploadCompleteHandler(event:Event):void {
        _uploadedFile = _fileRef.data;
        _uploadedFileName = _fileRef.name;

        _certificateSection.lblUploadMsg.text = "Keystore successfully saved.";
        _certificateSection.lblUploadMsg.setStyle("color", "Green");
        _certificateSection.lblUploadMsg.visible = true;
        _certificateSection.fadeFx.play([_certificateSection.lblUploadMsg]);

        _fileRef = null;
        _selectedFiles = new ArrayCollection();
        _certificateSection.certificateKeyPair.prompt = "Browse Key Pair";

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
    }

    private function uploadMetadataCompleteHandler(event:Event):void {
        _uploadedMetadata = _metadataFileRef.data;
        _uploadedMetadataName = _metadataFileRef.name;

        _metadataFileRef = null;
        _selectedMetadataFiles = new ArrayCollection();

        if (_currentIdentityApplianceElement is ExternalIdentityProvider) {
            _externalIdpCoreSection.lblUploadMsg.text = "Metadata file successfully saved.";
            _externalIdpCoreSection.lblUploadMsg.setStyle("color", "Green");
            _externalIdpCoreSection.lblUploadMsg.visible = true;
            _externalIdpCoreSection.fadeFx.play([_externalIdpCoreSection.lblUploadMsg]);
            _externalIdpCoreSection.metadataFile.prompt = "Browse metadata file";
            updateExternalIdentityProvider();
        } else if (_currentIdentityApplianceElement is ExternalServiceProvider) {
            _externalSpCoreSection.lblUploadMsg.text = "Metadata file successfully saved.";
            _externalSpCoreSection.lblUploadMsg.setStyle("color", "Green");
            _externalSpCoreSection.lblUploadMsg.visible = true;
            _externalSpCoreSection.fadeFx.play([_externalSpCoreSection.lblUploadMsg]);
            _externalSpCoreSection.metadataFile.prompt = "Browse metadata file";
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
    private function handleSectionChange(event:Event) {
        _dirty = true;
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
            adminPass.errorString = "This field is required!";
            return false;
        }
        if (confirmAdminPass.text == "") {
            confirmAdminPass.errorString = "This field is required!";
            return false;
        }
        if (adminPass.text != confirmAdminPass.text) {
            adminPass.errorString = "Passwords are not identical!";
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
    }

    private function reflectSPSettingsInIdpChannelTab():void {
        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;        
        if(connection.roleA is ServiceProvider){
            var sp:ServiceProvider = connection.roleA as ServiceProvider;
        } else if (connection.roleB is ServiceProvider){
            sp = connection.roleB as ServiceProvider;
        }
        //_federatedConnectionIDPChannelSection.signAuthRequestCheck.selected = sp.signAuthenticationAssertions;
        //_federatedConnectionIDPChannelSection.encryptAuthRequestCheck.selected = sp.encryptAuthenticationAssertions;
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

        if(sp.accountLinkagePolicy != null) {
            if(sp.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.LOCAL.toString()){
                _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = 1;
            } else if (sp.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = 0;
            } else if (sp.accountLinkagePolicy.mappingType.toString() == IdentityMappingType.MERGED.toString()) {
                _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = 2;
            }
        } else {
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.selectedIndex = 0;
        }

        // set provider location
        if (_federatedConnectionIDPChannelSection.idpChannelLocationPath.text == null ||
                _federatedConnectionIDPChannelSection.idpChannelLocationPath.text == "") {
            for (var i:int = 0; i < _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider.length; i++) {
                if (sp.location.protocol == _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.dataProvider[i].data) {
                    _federatedConnectionIDPChannelSection.idpChannelLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _federatedConnectionIDPChannelSection.idpChannelLocationDomain.text = sp.location.host;
            _federatedConnectionIDPChannelSection.idpChannelLocationPort.text = sp.location.port.toString() != "0" ? sp.location.port.toString() : "";
            _federatedConnectionIDPChannelSection.idpChannelLocationContext.text = sp.location.context;
            _federatedConnectionIDPChannelSection.idpChannelLocationPath.text = sp.location.uri;
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

        _federatedConnectionSPChannelSection.signAuthAssertionCheck.selected = idp.signAuthenticationAssertions;
        _federatedConnectionSPChannelSection.encryptAuthAssertionCheck.selected = idp.encryptAuthenticationAssertions;

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
        if (_federatedConnectionSPChannelSection.spChannelLocationPath.text == null ||
                _federatedConnectionSPChannelSection.spChannelLocationPath.text == "") {
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

//            view.signAuthRequestCheck.enabled = false;
//            view.encryptAuthRequestCheck.enabled = false;

//            view.authMechanism.enabled = false;
//            view.configureAuthMechanism.enabled = false;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = false;
            _federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = false;

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

//            view.signAuthRequestCheck.enabled = true;
//            view.encryptAuthRequestCheck.enabled = true;

//            view.authMechanism.enabled = true;
//            view.configureAuthMechanism.enabled = true;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = true;
            _federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = true;

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

            _federatedConnectionSPChannelSection.signAuthAssertionCheck.enabled = false;
            _federatedConnectionSPChannelSection.encryptAuthAssertionCheck.enabled = false;
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

            _federatedConnectionSPChannelSection.signAuthAssertionCheck.enabled = true;
            _federatedConnectionSPChannelSection.encryptAuthAssertionCheck.enabled = true;
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