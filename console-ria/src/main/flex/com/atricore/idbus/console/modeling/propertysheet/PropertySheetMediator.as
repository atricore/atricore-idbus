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
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.ExecutionEnvironmentActivationSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.apache.ApacheExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jboss.JBossExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.jbossportal.JBossPortalExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.liferayportal.LiferayPortalExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.tomcat.TomcatExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.wasce.WASCEExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.weblogic.WeblogicExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.executionenvironment.windowsiis.WindowsIISExecEnvCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentitysource.ExternalDBIdentityVaultLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionIDPChannelSection;
import com.atricore.idbus.console.modeling.propertysheet.view.federatedconnection.FederatedConnectionSPChannelSection;
import com.atricore.idbus.console.modeling.propertysheet.view.identitylookup.IdentityLookupCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.identityvault.EmbeddedDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idpchannel.IDPChannelContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idpchannel.IDPChannelCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.jossoactivation.JOSSOActivationCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.ldapidentitysource.LdapIdentitySourceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.spchannel.SPChannelContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.spchannel.SPChannelCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.xmlidentitysource.XmlIdentitySourceCoreSection;
import com.atricore.idbus.console.services.dto.ApacheExecutionEnvironment;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.Connection;
import com.atricore.idbus.console.services.dto.DbIdentitySource;
import com.atricore.idbus.console.services.dto.EmbeddedIdentitySource;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceState;
import com.atricore.idbus.console.services.dto.IdentityLookup;
import com.atricore.idbus.console.services.dto.IdentityProviderChannel;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.JBossPortalExecutionEnvironment;
import com.atricore.idbus.console.services.dto.JOSSOActivation;
import com.atricore.idbus.console.services.dto.JbossExecutionEnvironment;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;
import com.atricore.idbus.console.services.dto.LiferayExecutionEnvironment;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.TomcatExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WASCEExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WeblogicExecutionEnvironment;
import com.atricore.idbus.console.services.dto.WindowsIISExecutionEnvironment;
import com.atricore.idbus.console.services.dto.XmlIdentitySource;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.FlexEvent;
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
    private var _idpChannelCoreSection:IDPChannelCoreSection;
    private var _spChannelCoreSection:SPChannelCoreSection;
    private var _embeddedDbVaultCoreSection:EmbeddedDBIdentityVaultCoreSection;
    private var _externalDbVaultCoreSection:ExternalDBIdentityVaultCoreSection;
    private var _ldapIdentitySourceCoreSection:LdapIdentitySourceCoreSection;
    private var _ldapIdentitySourceLookupSection:LdapIdentitySourceLookupSection;
    private var _xmlIdentitySourceCoreSection:XmlIdentitySourceCoreSection;
    private var _currentIdentityApplianceElement:Object;
    private var _ipContractSection:IdentityProviderContractSection;
    private var _spContractSection:ServiceProviderContractSection;
    private var _idpChannelContractSection:IDPChannelContractSection;
    private var _spChannelContractSection:SPChannelContractSection;
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
    private var _executionEnvironmentActivateSection:ExecutionEnvironmentActivationSection;
    private var _dirty:Boolean;

    protected var _validators : Array;

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
        _validators = [];
        _tabbedPropertiesTabBar.selectedIndex = 0;
        _tabbedPropertiesTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
    }

    private function stackChanged(event:IndexChangeEvent):void {
        _propertySheetsViewStack.selectedIndex = _tabbedPropertiesTabBar.selectedIndex;
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE,
            ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.DIAGRAM_ELEMENT_SELECTED];
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
                } else if (_currentIdentityApplianceElement is IdentityProviderChannel) {
                    enableIdpChannelPropertyTabs();
                } else if (_currentIdentityApplianceElement is ServiceProviderChannel) {
                    enableSpChannelPropertyTabs();
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

        var location:Location = identityAppliance.idApplianceDefinition.location;
        for (var i:int = 0; i < _iaCoreSection.applianceLocationProtocol.dataProvider.length; i++) {
            if (location != null && location.protocol == _iaCoreSection.applianceLocationProtocol.dataProvider[i].label) {
                _iaCoreSection.applianceLocationProtocol.selectedIndex = i;
                break;
            }
        }
        _iaCoreSection.applianceLocationDomain.text = location.host;
        _iaCoreSection.applianceLocationPort.text = location.port.toString();
        _iaCoreSection.applianceLocationPath.text = location.context;

        _iaCoreSection.applianceName.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceDescription.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
        _iaCoreSection.applianceLocationPath.addEventListener(Event.CHANGE, handleSectionChange);

        _validators = [];
        _validators.push(_iaCoreSection.nameValidator);
        _validators.push(_iaCoreSection.portValidator);
        _validators.push(_iaCoreSection.domainValidator);
        _validators.push(_iaCoreSection.pathValidator);
    }

    private function handleIdentityApplianceCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            // fetch appliance object
            var identityAppliance:IdentityAppliance;
            identityAppliance = projectProxy.currentIdentityAppliance;

            identityAppliance.idApplianceDefinition.name = _iaCoreSection.applianceName.text;
            identityAppliance.idApplianceDefinition.description = _iaCoreSection.applianceDescription.text;
            identityAppliance.idApplianceDefinition.location.protocol = _iaCoreSection.applianceLocationProtocol.selectedItem.label;
            identityAppliance.idApplianceDefinition.location.host = _iaCoreSection.applianceLocationDomain.text;
            identityAppliance.idApplianceDefinition.location.port = parseInt(_iaCoreSection.applianceLocationPort.text);
            identityAppliance.idApplianceDefinition.location.context = _iaCoreSection.applianceLocationPath.text;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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
                if (identityProvider.location != null && _ipCoreSection.idpLocationProtocol.dataProvider[i].label == identityProvider.location.protocol) {
                    _ipCoreSection.idpLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _ipCoreSection.idpLocationDomain.text = identityProvider.location.host;
            _ipCoreSection.idpLocationPort.text = identityProvider.location.port.toString();
            _ipCoreSection.idpLocationContext.text = "/" + identityProvider.location.context + "/";
            _ipCoreSection.idpLocationPath.text = identityProvider.location.uri;

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
            identityProvider.location.context = _ipCoreSection.idpLocationContext.text.substring(1,
                    _ipCoreSection.idpLocationContext.text.length - 1);
            identityProvider.location.uri = _ipCoreSection.idpLocationPath.text;
            
            // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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
        if (_dirty) {
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
            _dirty = false;
        }
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
                if (serviceProvider.location != null && _spCoreSection.spLocationProtocol.dataProvider[i].label == serviceProvider.location.protocol) {
                    _spCoreSection.spLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _spCoreSection.spLocationDomain.text = serviceProvider.location.host;
            _spCoreSection.spLocationPort.text = serviceProvider.location.port.toString();
            _spCoreSection.spLocationContext.text = "/" + serviceProvider.location.context + "/";
            _spCoreSection.spLocationPath.text = serviceProvider.location.uri;

            _spCoreSection.serviceProvName.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.serviceProvDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.spLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _spCoreSection.authMechanismCombo.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_spCoreSection.nameValidator);
            _validators.push(_spCoreSection.portValidator);
            _validators.push(_spCoreSection.domainValidator);
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
            serviceProvider.location.context = _spCoreSection.spLocationContext.text.substring(1,
                    _spCoreSection.spLocationContext.text.length - 1);
            serviceProvider.location.uri = _spCoreSection.spLocationPath.text;
            
            // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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
        if (_dirty) {
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
            _dirty = false;
        }
    }

    protected function enableIdpChannelPropertyTabs():void {
        // Attach idp channel editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _idpChannelCoreSection = new IDPChannelCoreSection();
        corePropertyTab.addElement(_idpChannelCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _idpChannelCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdpChannelCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdpChannelCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _idpChannelContractSection = new IDPChannelContractSection();
        contractPropertyTab.addElement(_idpChannelContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _idpChannelContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdpChannelContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdpChannelContractPropertyTabRollOut);
    }

    private function handleIdpChannelCorePropertyTabCreationComplete(event:Event):void {
        var idpChannel:IdentityProviderChannel;

        idpChannel = _currentIdentityApplianceElement as IdentityProviderChannel;

        // if idpChannel is null that means some other element was selected before completing this
        if (idpChannel != null) {
            // bind view
            _idpChannelCoreSection.identityProvChannelName.text = idpChannel.name;
            _idpChannelCoreSection.identityProvChannelDescription.text = idpChannel.description;
            //TODO

            for (var i:int = 0; i < _idpChannelCoreSection.idpChannelLocationProtocol.dataProvider.length; i++) {
                if (idpChannel.location != null && _idpChannelCoreSection.idpChannelLocationProtocol.dataProvider[i].label == idpChannel.location.protocol) {
                    _idpChannelCoreSection.idpChannelLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _idpChannelCoreSection.idpChannelLocationDomain.text = idpChannel.location.host;
            _idpChannelCoreSection.idpChannelLocationPort.text = idpChannel.location.port.toString();
            _idpChannelCoreSection.idpChannelLocationContext.text = "/" + idpChannel.location.context + "/";
            _idpChannelCoreSection.idpChannelLocationPath.text = idpChannel.location.uri;

            _idpChannelCoreSection.identityProvChannelName.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.identityProvChannelDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.idpChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.idpChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.idpChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.idpChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.idpChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelCoreSection.authMechanismCombo.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_idpChannelCoreSection.nameValidator);
            _validators.push(_idpChannelCoreSection.portValidator);
            _validators.push(_idpChannelCoreSection.domainValidator);
            _validators.push(_idpChannelCoreSection.pathValidator);
        }
    }

    private function handleIdpChannelCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var idpChannel:IdentityProviderChannel;

            idpChannel = _currentIdentityApplianceElement as IdentityProviderChannel;

            idpChannel.name = _idpChannelCoreSection.identityProvChannelName.text;
            idpChannel.description = _idpChannelCoreSection.identityProvChannelDescription.text;

            if(idpChannel.location == null){
                idpChannel.location = new Location();
            }

            idpChannel.location.protocol = _idpChannelCoreSection.idpChannelLocationProtocol.labelDisplay.text;
            idpChannel.location.host = _idpChannelCoreSection.idpChannelLocationDomain.text;
            idpChannel.location.port = parseInt(_idpChannelCoreSection.idpChannelLocationPort.text);
            idpChannel.location.context = _idpChannelCoreSection.idpChannelLocationContext.text.substring(1,
                    _idpChannelCoreSection.idpChannelLocationContext.text.length - 1);
            idpChannel.location.uri = _idpChannelCoreSection.idpChannelLocationPath.text;

            // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleIdpChannelContractPropertyTabCreationComplete(event:Event):void {

        var idpChannel:IdentityProviderChannel;

        idpChannel = _currentIdentityApplianceElement as IdentityProviderChannel;

        // if idpChannel is null that means some other element was selected before completing this
        if (idpChannel != null) {
            //_spContractSection.signAuthRequestCheck.selected = serviceProvider.signAuthenticationAssertions;
            //_spContractSection.encryptAuthRequestCheck.selected = serviceProvider.encryptAuthenticationAssertions;

            if (idpChannel != null) {
                for (var j:int = 0; j < idpChannel.activeBindings.length; j ++) {
                    var tmpBinding:Binding = idpChannel.activeBindings.getItemAt(j) as Binding;
                    if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                        _idpChannelContractSection.samlBindingHttpPostCheck.selected = true;
                    }
                    if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                        _idpChannelContractSection.samlBindingHttpRedirectCheck.selected = true;
                    }
                    if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                        _idpChannelContractSection.samlBindingArtifactCheck.selected = true;
                    }
                    if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                        _idpChannelContractSection.samlBindingSoapCheck.selected = true;
                    }
                }
                for (j = 0; j < idpChannel.activeProfiles.length; j++) {
                    var tmpProfile:Profile = idpChannel.activeProfiles.getItemAt(j) as Profile;
                    if (tmpProfile.name == Profile.SSO.name) {
                        _idpChannelContractSection.samlProfileSSOCheck.selected = true;
                    }
                    if (tmpProfile.name == Profile.SSO_SLO.name) {
                        _idpChannelContractSection.samlProfileSLOCheck.selected = true;
                    }
                }
            }

            _idpChannelContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _idpChannelContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleIdpChannelContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var idpChannel:IdentityProviderChannel;

            idpChannel = _currentIdentityApplianceElement as IdentityProviderChannel;

            idpChannel.activeBindings = new ArrayCollection();
            if (_idpChannelContractSection.samlBindingHttpPostCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_idpChannelContractSection.samlBindingArtifactCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_idpChannelContractSection.samlBindingHttpRedirectCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_idpChannelContractSection.samlBindingSoapCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            idpChannel.activeProfiles = new ArrayCollection();
            if (_idpChannelContractSection.samlProfileSSOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_idpChannelContractSection.samlProfileSLOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    protected function enableSpChannelPropertyTabs():void {
        // Attach sp channel editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Group = new Group();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.name = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _spChannelCoreSection = new SPChannelCoreSection();
        corePropertyTab.addElement(_spChannelCoreSection);
        _propertySheetsViewStack.addNewChild(corePropertyTab);
        _tabbedPropertiesTabBar.selectedIndex = 0;

        _spChannelCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSpChannelCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSpChannelCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _spChannelContractSection = new SPChannelContractSection();
        contractPropertyTab.addElement(_spChannelContractSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _spChannelContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSpChannelContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSpChannelContractPropertyTabRollOut);
    }

    private function handleSpChannelCorePropertyTabCreationComplete(event:Event):void {
        var spChannel:ServiceProviderChannel;

        spChannel = _currentIdentityApplianceElement as ServiceProviderChannel;

        // if spChannel is null that means some other element was selected before completing this
        if (spChannel != null) {
            // bind view
            _spChannelCoreSection.serviceProvChannelName.text = spChannel.name;
            _spChannelCoreSection.serviceProvChannelDescription.text = spChannel.description;
            //TODO

            for (var i:int = 0; i < _spChannelCoreSection.spChannelLocationProtocol.dataProvider.length; i++) {
                if (spChannel.location != null && _spChannelCoreSection.spChannelLocationProtocol.dataProvider[i].label == spChannel.location.protocol) {
                    _spChannelCoreSection.spChannelLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            _spChannelCoreSection.spChannelLocationDomain.text = spChannel.location.host;
            _spChannelCoreSection.spChannelLocationPort.text = spChannel.location.port.toString();
            _spChannelCoreSection.spChannelLocationContext.text = "/" + spChannel.location.context + "/";
            _spChannelCoreSection.spChannelLocationPath.text = spChannel.location.uri;

            _spChannelCoreSection.serviceProvChannelName.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.serviceProvChannelDescription.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.spChannelLocationProtocol.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.spChannelLocationDomain.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.spChannelLocationPort.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.spChannelLocationContext.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.spChannelLocationPath.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelCoreSection.authMechanismCombo.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_spChannelCoreSection.nameValidator);
            _validators.push(_spChannelCoreSection.portValidator);
            _validators.push(_spChannelCoreSection.domainValidator);
            _validators.push(_spChannelCoreSection.pathValidator);
        }
    }

    private function handleSpChannelCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var spChannel:ServiceProviderChannel;

            spChannel = _currentIdentityApplianceElement as ServiceProviderChannel;

            spChannel.name = _spChannelCoreSection.serviceProvChannelName.text;
            spChannel.description = _spChannelCoreSection.serviceProvChannelDescription.text;

            if(spChannel.location == null){
                spChannel.location = new Location();
            }

            spChannel.location.protocol = _spChannelCoreSection.spChannelLocationProtocol.labelDisplay.text;
            spChannel.location.host = _spChannelCoreSection.spChannelLocationDomain.text;
            spChannel.location.port = parseInt(_spChannelCoreSection.spChannelLocationPort.text);
            spChannel.location.context = _spChannelCoreSection.spChannelLocationContext.text.substring(1,
                    _spChannelCoreSection.spChannelLocationContext.text.length - 1);
            spChannel.location.uri = _spChannelCoreSection.spChannelLocationPath.text;

            // TODO save remaining fields to channel, calling appropriate lookup methods
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleSpChannelContractPropertyTabCreationComplete(event:Event):void {

        var spChannel:ServiceProviderChannel;

        spChannel = _currentIdentityApplianceElement as ServiceProviderChannel;

        // if spChannel is null that means some other element was selected before completing this
        if (spChannel != null) {
            for (var j:int = 0; j < spChannel.activeBindings.length; j ++) {
                var tmpBinding:Binding = spChannel.activeBindings.getItemAt(j) as Binding;
                if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                    _spChannelContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                    _spChannelContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                    _spChannelContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                    _spChannelContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < spChannel.activeProfiles.length; j++) {
                var tmpProfile:Profile = spChannel.activeProfiles.getItemAt(j) as Profile;
                if (tmpProfile.name == Profile.SSO.name) {
                    _spChannelContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _spChannelContractSection.samlProfileSLOCheck.selected = true;
                }
            }

            _spChannelContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _spChannelContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleSpChannelContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var spChannel:ServiceProviderChannel;

            spChannel = _currentIdentityApplianceElement as ServiceProviderChannel;

            spChannel.activeBindings = new ArrayCollection();
            if (_spChannelContractSection.samlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_spChannelContractSection.samlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_spChannelContractSection.samlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_spChannelContractSection.samlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            spChannel.activeProfiles = new ArrayCollection();
            if (_spChannelContractSection.samlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_spChannelContractSection.samlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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
            _externalDbVaultCoreSection.userRepositoryName.text = dbIdentityVault.name;
            _externalDbVaultCoreSection.driverName.text = dbIdentityVault.driverName;
            _externalDbVaultCoreSection.connectionUrl.text = dbIdentityVault.connectionUrl;
            _externalDbVaultCoreSection.dbUsername.text = dbIdentityVault.admin;
            _externalDbVaultCoreSection.dbPassword.text = dbIdentityVault.password;

            _externalDbVaultCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.driverName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.connectionUrl.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbUsername.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbPassword.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_externalDbVaultCoreSection.nameValidator);
            _validators.push(_externalDbVaultCoreSection.driverNameValidator);
            _validators.push(_externalDbVaultCoreSection.connUrlValidator);
            _validators.push(_externalDbVaultCoreSection.dbUsernameValidator);
            _validators.push(_externalDbVaultCoreSection.dbPasswordValidator);
        }
    }

    private function handleExternalDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var dbIdentityVault:DbIdentitySource;

            dbIdentityVault = _currentIdentityApplianceElement as DbIdentitySource;
            dbIdentityVault.name = _externalDbVaultCoreSection.userRepositoryName.text;
            dbIdentityVault.driverName = _externalDbVaultCoreSection.driverName.text;
            dbIdentityVault.connectionUrl = _externalDbVaultCoreSection.connectionUrl.text;
            dbIdentityVault.admin = _externalDbVaultCoreSection.dbUsername.text;
            dbIdentityVault.password = _externalDbVaultCoreSection.dbPassword.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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
        if (_dirty) {
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
            _dirty = false;
        }
    }

    private function handleLdapIdentitySourceLookupPropertyTabCreationComplete(event:Event):void {
        var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;

        // if ldapIdentitySource is null that means some other element was selected before completing this
        if (ldapIdentitySource != null) {
            _ldapIdentitySourceLookupSection.usersCtxDN.text = ldapIdentitySource.usersCtxDN;
            _ldapIdentitySourceLookupSection.principalUidAttributeID.text = ldapIdentitySource.principalUidAttributeID;
            _ldapIdentitySourceLookupSection.roleMatchingMode.text = ldapIdentitySource.roleMatchingMode;
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
            _validators.push(_ldapIdentitySourceLookupSection.roleMatchingModeValidator);
            _validators.push(_ldapIdentitySourceLookupSection.uidAttributeIDValidator);
            _validators.push(_ldapIdentitySourceLookupSection.rolesCtxDNValidator);
            _validators.push(_ldapIdentitySourceLookupSection.roleAttributeIDValidator);
            _validators.push(_ldapIdentitySourceLookupSection.credentialQueryStringValidator);
            _validators.push(_ldapIdentitySourceLookupSection.updateableCredentialAttributeValidator);
            _validators.push(_ldapIdentitySourceLookupSection.userPropertiesQueryStringValidator);
        }
    }

    private function handleLdapIdentitySourceLookupPropertyTabRollOut(event:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var ldapIdentitySource:LdapIdentitySource = _currentIdentityApplianceElement as LdapIdentitySource;
            ldapIdentitySource.usersCtxDN = _ldapIdentitySourceLookupSection.usersCtxDN.text;
            ldapIdentitySource.principalUidAttributeID = _ldapIdentitySourceLookupSection.principalUidAttributeID.text;
            ldapIdentitySource.roleMatchingMode = _ldapIdentitySourceLookupSection.roleMatchingMode.text;
            ldapIdentitySource.uidAttributeID = _ldapIdentitySourceLookupSection.uidAttributeID.text;
            ldapIdentitySource.rolesCtxDN = _ldapIdentitySourceLookupSection.rolesCtxDN.text;
            ldapIdentitySource.roleAttributeID = _ldapIdentitySourceLookupSection.roleAttributeID.text;
            ldapIdentitySource.credentialQueryString = _ldapIdentitySourceLookupSection.credentialQueryString.text;
            ldapIdentitySource.updateableCredentialAttribute = _ldapIdentitySourceLookupSection.updateableCredentialAttribute.text;
            ldapIdentitySource.userPropertiesQueryString = _ldapIdentitySourceLookupSection.userPropertiesQueryString.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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

        // SP Channel Tab
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

        //IDP Channel Tab
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

    private function handleFederatedConnectionCorePropertyTabCreationComplete(event:Event):void {
        var connection:Connection = projectProxy.currentIdentityApplianceElement as FederatedConnection;

        // bind view
        _federatedConnectionCoreSection.connectionName.text = connection.name;
        _federatedConnectionCoreSection.connectionDescription.text = connection.description;

        _federatedConnectionCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
        _federatedConnectionCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);
        
        _validators = [];
        _validators.push(_federatedConnectionCoreSection.nameValidator);
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
            _dirty = false;
        }
    }

    private function handleFederatedConnectionSpChannelPropertyTabCreationComplete(event:Event):void {
        var spChannel:ServiceProviderChannel;

        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
        if(connection.channelA is ServiceProviderChannel){
            spChannel = connection.channelA as ServiceProviderChannel;
        } else if (connection.channelB is ServiceProviderChannel){
            spChannel = connection.channelB as ServiceProviderChannel;
        }

        // if spChannel is null that means some other element was selected before completing this
        if (spChannel != null) {

            _federatedConnectionSPChannelSection.useInheritedIDPSettings.addEventListener(Event.CHANGE, handleUseInheritedIDPSettingsChange);
            _federatedConnectionSPChannelSection.useInheritedIDPSettings.selected = !spChannel.overrideProviderSetup;
            handleUseInheritedIDPSettingsChange(null);
            
            for (var j:int = 0; j < spChannel.activeBindings.length; j ++) {
                var tmpBinding:Binding = spChannel.activeBindings.getItemAt(j) as Binding;
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
            for (j = 0; j < spChannel.activeProfiles.length; j++) {
                var tmpProfile:Profile = spChannel.activeProfiles.getItemAt(j) as Profile;
                if (tmpProfile.name == Profile.SSO.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == Profile.SSO_SLO.name) {
                    _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected = true;
                }
            }

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionSPChannelSection.spChannelAuthMechanismCombo.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleFederatedConnectionSpChannelPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var spChannel:ServiceProviderChannel;
            
            var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
            if(connection.channelA is ServiceProviderChannel){
                spChannel = connection.channelA as ServiceProviderChannel;
            } else if (connection.channelB is ServiceProviderChannel){
                spChannel = connection.channelB as ServiceProviderChannel;
            }
            spChannel.overrideProviderSetup = !_federatedConnectionSPChannelSection.useInheritedIDPSettings.selected;
            if(spChannel.overrideProviderSetup){
                spChannel.activeBindings = new ArrayCollection();
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

                spChannel.activeProfiles = new ArrayCollection();
                if (_federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.selected) {
                    spChannel.activeProfiles.addItem(Profile.SSO);
                }
                if (_federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.selected) {
                    spChannel.activeProfiles.addItem(Profile.SSO_SLO);
                }
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleFederatedConnectionIdpChannelPropertyTabCreationComplete(event:Event):void {
        var idpChannel:IdentityProviderChannel;

        var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
        if(connection.channelA is IdentityProviderChannel){
            idpChannel = connection.channelA as IdentityProviderChannel;
        } else if (connection.channelB is IdentityProviderChannel){
            idpChannel = connection.channelB as IdentityProviderChannel;
        }


        // if idpChannel is null that means some other element was selected before completing this
        if (idpChannel != null) {

            if (idpChannel != null) {
                _federatedConnectionIDPChannelSection.useInheritedSPSettings.addEventListener(Event.CHANGE, handleUseInheritedSPSettingsChange);
                _federatedConnectionIDPChannelSection.useInheritedSPSettings.selected = !idpChannel.overrideProviderSetup;
                handleUseInheritedSPSettingsChange(null);

                for (var j:int = 0; j < idpChannel.activeBindings.length; j ++) {
                    var tmpBinding:Binding = idpChannel.activeBindings.getItemAt(j) as Binding;
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
                for (j = 0; j < idpChannel.activeProfiles.length; j++) {
                    var tmpProfile:Profile = idpChannel.activeProfiles.getItemAt(j) as Profile;
                    if (tmpProfile.name == Profile.SSO.name) {
                        _federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected = true;
                    }
                    if (tmpProfile.name == Profile.SSO_SLO.name) {
                        _federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected = true;
                    }
                }
            }

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleFederatedConnectionIdpChannelPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            // bind model
            var idpChannel:IdentityProviderChannel;

            var idpChannel:IdentityProviderChannel;

            var connection:FederatedConnection = projectProxy.currentIdentityApplianceElement as FederatedConnection;
            if(connection.channelA is IdentityProviderChannel){
                idpChannel = connection.channelA as IdentityProviderChannel;
            } else if (connection.channelB is IdentityProviderChannel){
                idpChannel = connection.channelB as IdentityProviderChannel;
            }

            idpChannel.overrideProviderSetup = !_federatedConnectionIDPChannelSection.useInheritedSPSettings.selected;

            if(idpChannel.overrideProviderSetup){
                idpChannel.activeBindings = new ArrayCollection();
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

                idpChannel.activeProfiles = new ArrayCollection();
                if (_federatedConnectionIDPChannelSection.samlProfileSSOCheck.selected) {
                    idpChannel.activeProfiles.addItem(Profile.SSO);
                }
                if (_federatedConnectionIDPChannelSection.samlProfileSLOCheck.selected) {
                    idpChannel.activeProfiles.addItem(Profile.SSO_SLO);
                }
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
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
        _jossoActivationCoreSection.partnerAppLocationPort.text = location.port.toString();
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
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleTomcatExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var tomcatExecEnv:TomcatExecutionEnvironment = projectProxy.currentIdentityApplianceElement as TomcatExecutionEnvironment;

        // bind view
        _tomcatExecEnvCoreSection.executionEnvironmentName.text = tomcatExecEnv.name;
        _tomcatExecEnvCoreSection.executionEnvironmentDescription.text = tomcatExecEnv.description;

        _tomcatExecEnvCoreSection.selectedHost.selectedIndex = 0;
        _tomcatExecEnvCoreSection.selectedHost.enabled = false;

        for(var i:int=0; i < _tomcatExecEnvCoreSection.platform.dataProvider.length; i++){
            if(_tomcatExecEnvCoreSection.platform.dataProvider[i].data == tomcatExecEnv.platformId){
                _tomcatExecEnvCoreSection.platform.selectedIndex = i;
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

    private function handleTomcatExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var tomcatExecEnv:TomcatExecutionEnvironment = projectProxy.currentIdentityApplianceElement as TomcatExecutionEnvironment;
            tomcatExecEnv.name = _tomcatExecEnvCoreSection.executionEnvironmentName.text;
            tomcatExecEnv.description = _tomcatExecEnvCoreSection.executionEnvironmentDescription.text;
            tomcatExecEnv.platformId = _tomcatExecEnvCoreSection.platform.selectedItem.data;
            tomcatExecEnv.installUri = _tomcatExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleWeblogicExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var weblogicExecEnv:WeblogicExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WeblogicExecutionEnvironment;

        // bind view
        _weblogicExecEnvCoreSection.executionEnvironmentName.text = weblogicExecEnv.name;
        _weblogicExecEnvCoreSection.executionEnvironmentDescription.text = weblogicExecEnv.description;

        _weblogicExecEnvCoreSection.selectedHost.selectedIndex = 0;
        _weblogicExecEnvCoreSection.selectedHost.enabled = false;

        for (var i:int=0; i < _weblogicExecEnvCoreSection.platform.dataProvider.length; i++){
            if (_weblogicExecEnvCoreSection.platform.dataProvider[i].data == weblogicExecEnv.platformId) {
                _weblogicExecEnvCoreSection.platform.selectedIndex = i;
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

    private function handleWeblogicExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var weblogicExecEnv:WeblogicExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WeblogicExecutionEnvironment;
            weblogicExecEnv.name = _weblogicExecEnvCoreSection.executionEnvironmentName.text;
            weblogicExecEnv.description = _weblogicExecEnvCoreSection.executionEnvironmentDescription.text;
            weblogicExecEnv.platformId = _weblogicExecEnvCoreSection.platform.selectedItem.data;
            weblogicExecEnv.installUri = _weblogicExecEnvCoreSection.homeDirectory.text;
            weblogicExecEnv.domain = _weblogicExecEnvCoreSection.domain.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleJBossPortalExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var jbossPortalExecEnv:JBossPortalExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JBossPortalExecutionEnvironment;

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

    private function handleJBossPortalExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var jbossPortalExecEnv:JBossPortalExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JBossPortalExecutionEnvironment;
            jbossPortalExecEnv.name = _jbossPortalExecEnvCoreSection.executionEnvironmentName.text;
            jbossPortalExecEnv.description = _jbossPortalExecEnvCoreSection.executionEnvironmentDescription.text;
            jbossPortalExecEnv.platformId = "";
            jbossPortalExecEnv.installUri = _jbossPortalExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleLiferayExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var liferayExecEnv:LiferayExecutionEnvironment = projectProxy.currentIdentityApplianceElement as LiferayExecutionEnvironment;

        // bind view
        _liferayExecEnvCoreSection.executionEnvironmentName.text = liferayExecEnv.name;
        _liferayExecEnvCoreSection.executionEnvironmentDescription.text = liferayExecEnv.description;
        _liferayExecEnvCoreSection.selectedHost.selectedIndex = 0;
        _liferayExecEnvCoreSection.homeDirectory.text = liferayExecEnv.installUri;

        _liferayExecEnvCoreSection.selectedHost.selectedIndex = 0;
        _liferayExecEnvCoreSection.selectedHost.enabled = false;

        _liferayExecEnvCoreSection.executionEnvironmentName.addEventListener(Event.CHANGE, handleSectionChange);
        _liferayExecEnvCoreSection.executionEnvironmentDescription.addEventListener(Event.CHANGE, handleSectionChange);
        _liferayExecEnvCoreSection.selectedHost.addEventListener(Event.CHANGE, handleSectionChange);
        _liferayExecEnvCoreSection.homeDirectory.addEventListener(Event.CHANGE, handleSectionChange);

        _validators = [];
        _validators.push(_liferayExecEnvCoreSection.nameValidator);
        _validators.push(_liferayExecEnvCoreSection.homeDirValidator);
    }

    private function handleLiferayExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var liferayExecEnv:LiferayExecutionEnvironment = projectProxy.currentIdentityApplianceElement as LiferayExecutionEnvironment;
            liferayExecEnv.name = _liferayExecEnvCoreSection.executionEnvironmentName.text;
            liferayExecEnv.description = _liferayExecEnvCoreSection.executionEnvironmentDescription.text;
            liferayExecEnv.platformId = "";
            liferayExecEnv.installUri = _liferayExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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
//        execEnvActivationPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExecEnvActivationPropertyTabRollOut);

    }

    private function handleWASCEExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var wasceExecEnv:WASCEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WASCEExecutionEnvironment;

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

    private function handleWASCEExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var wasceExecEnv:WASCEExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WASCEExecutionEnvironment;
            wasceExecEnv.name = _wasceExecEnvCoreSection.executionEnvironmentName.text;
            wasceExecEnv.description = _wasceExecEnvCoreSection.executionEnvironmentDescription.text;
            wasceExecEnv.platformId = "";
            wasceExecEnv.installUri = _wasceExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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

    }

    private function handleJbossExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var jbossExecEnv:JbossExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JbossExecutionEnvironment;

        // bind view
        _jbossExecEnvCoreSection.executionEnvironmentName.text = jbossExecEnv.name;
        _jbossExecEnvCoreSection.executionEnvironmentDescription.text = jbossExecEnv.description;

        _jbossExecEnvCoreSection.selectedHost.selectedIndex = 0;
        _jbossExecEnvCoreSection.selectedHost.enabled = false;

        for(var i:int=0; i < _jbossExecEnvCoreSection.platform.dataProvider.length; i++){
            if(_jbossExecEnvCoreSection.platform.dataProvider[i].data == jbossExecEnv.platformId){
                _jbossExecEnvCoreSection.platform.selectedIndex = i;
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

    private function handleJbossExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var jbossExecEnv:JbossExecutionEnvironment = projectProxy.currentIdentityApplianceElement as JbossExecutionEnvironment;
            jbossExecEnv.name = _jbossExecEnvCoreSection.executionEnvironmentName.text;
            jbossExecEnv.description = _jbossExecEnvCoreSection.executionEnvironmentDescription.text;
            jbossExecEnv.platformId = _jbossExecEnvCoreSection.platform.selectedItem.data;
            jbossExecEnv.installUri = _jbossExecEnvCoreSection.homeDirectory.text;
            jbossExecEnv.instance = _jbossExecEnvCoreSection.instance.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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

    }

    private function handleApacheExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var apacheExecEnv:ApacheExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ApacheExecutionEnvironment;

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

    private function handleApacheExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var apacheExecEnv:ApacheExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ApacheExecutionEnvironment;
            apacheExecEnv.name = _apacheExecEnvCoreSection.executionEnvironmentName.text;
            apacheExecEnv.description = _apacheExecEnvCoreSection.executionEnvironmentDescription.text;
            //TODO CHECK PLATFORM ID
            apacheExecEnv.platformId = "apache";
            apacheExecEnv.installUri = _apacheExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
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

    }

    private function handleWindowsIISExecEnvCorePropertyTabCreationComplete(event:Event):void {
        var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;

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

    private function handleWindowsIISExecEnvCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty && validate(true)) {
             // bind model
            var windowsIISExecEnv:WindowsIISExecutionEnvironment = projectProxy.currentIdentityApplianceElement as WindowsIISExecutionEnvironment;
            windowsIISExecEnv.name = _windowsIISExecEnvCoreSection.executionEnvironmentName.text;
            windowsIISExecEnv.description = _windowsIISExecEnvCoreSection.executionEnvironmentDescription.text;
            //TODO CHECK PLATFORM ID
            windowsIISExecEnv.platformId = "iis";
            windowsIISExecEnv.installUri = _windowsIISExecEnvCoreSection.homeDirectory.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleExecEnvActivationPropertyTabCreationComplete(event:Event):void{
        if(projectProxy.currentIdentityAppliance.state == IdentityApplianceState.DEPLOYED.toString()
                || projectProxy.currentIdentityAppliance.state == IdentityApplianceState.STARTED.toString()){
            _executionEnvironmentActivateSection.activate.enabled = true;
            _executionEnvironmentActivateSection.activate.addEventListener(MouseEvent.CLICK, activateExecutionEnvironment);
        } else {
            _executionEnvironmentActivateSection.activate.enabled = false;
        }
    }

    private function activateExecutionEnvironment(event:Event):void {
        //ovo ide u onClick handler za activate Btn
        var currentExecEnv:ExecutionEnvironment = projectProxy.currentIdentityApplianceElement as ExecutionEnvironment;
        var activateExecEnvReq:ActivateExecutionEnvironmentRequest = new ActivateExecutionEnvironmentRequest();
        activateExecEnvReq.reactivate = _executionEnvironmentActivateSection.reactivate.selected;
        activateExecEnvReq.replaceConfFiles = _executionEnvironmentActivateSection.replaceConfFiles.selected;
        activateExecEnvReq.executionEnvironment = currentExecEnv;
        activateExecEnvReq.installSamples = _executionEnvironmentActivateSection.installSamples.selected;

        sendNotification(ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT, activateExecEnvReq);        
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

        // bind view
        _identityLookupCoreSection.connectionName.text = identityLookup.name;
        _identityLookupCoreSection.connectionDescription.text = identityLookup.description;

        _identityLookupCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
        _identityLookupCoreSection.connectionDescription.addEventListener(Event.CHANGE, handleSectionChange);

        _validators = [];
        _validators.push(_identityLookupCoreSection.nameValidator);
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
            _dirty = false;
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
        if(_federatedConnectionSPChannelSection.useInheritedIDPSettings.selected){
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.enabled = false;

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.enabled = false;

            _federatedConnectionSPChannelSection.signAuthAssertionCheck.enabled = false;
            _federatedConnectionSPChannelSection.encryptAuthAssertionCheck.enabled = false;
            _federatedConnectionSPChannelSection.spChannelUserInfoLookupCombo.enabled = false;
            _federatedConnectionSPChannelSection.spChannelAuthContractCombo.enabled = false;
            _federatedConnectionSPChannelSection.spChannelAuthMechanismCombo.enabled = false;
            _federatedConnectionSPChannelSection.spChannelAuthAssertionEmissionPolicyCombo.enabled = false;
        } else {
            _federatedConnectionSPChannelSection.spChannelSamlProfileSSOCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlProfileSLOCheck.enabled = true;

            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpPostCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingHttpRedirectCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingArtifactCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelSamlBindingSoapCheck.enabled = true;

            _federatedConnectionSPChannelSection.signAuthAssertionCheck.enabled = true;
            _federatedConnectionSPChannelSection.encryptAuthAssertionCheck.enabled = true;
            _federatedConnectionSPChannelSection.spChannelUserInfoLookupCombo.enabled = true;
            _federatedConnectionSPChannelSection.spChannelAuthContractCombo.enabled = true;
            _federatedConnectionSPChannelSection.spChannelAuthMechanismCombo.enabled = true;
            _federatedConnectionSPChannelSection.spChannelAuthAssertionEmissionPolicyCombo.enabled = true;
        }
        _dirty = true;
    }

    private function handleUseInheritedSPSettingsChange(event:Event):void {
        if(_federatedConnectionIDPChannelSection.useInheritedSPSettings.selected){
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.enabled = false;

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.enabled = false;
            _federatedConnectionIDPChannelSection.samlBindingSoapCheck.enabled = false;

            _federatedConnectionIDPChannelSection.signAuthRequestCheck.enabled = false;
            _federatedConnectionIDPChannelSection.encryptAuthRequestCheck.enabled = false;

            _federatedConnectionIDPChannelSection.userInfoLookupCombo.enabled = false;
            _federatedConnectionIDPChannelSection.authMechanismCombo.enabled = false;
            _federatedConnectionIDPChannelSection.configureAuthMechanism.enabled = false;
            _federatedConnectionIDPChannelSection.authContractCombo.enabled = false;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = false;
            _federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = false;
        } else {
            _federatedConnectionIDPChannelSection.samlProfileSSOCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlProfileSLOCheck.enabled = true;

            _federatedConnectionIDPChannelSection.samlBindingHttpPostCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingHttpRedirectCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingArtifactCheck.enabled = true;
            _federatedConnectionIDPChannelSection.samlBindingSoapCheck.enabled = true;

            _federatedConnectionIDPChannelSection.signAuthRequestCheck.enabled = true;
            _federatedConnectionIDPChannelSection.encryptAuthRequestCheck.enabled = true;

            _federatedConnectionIDPChannelSection.userInfoLookupCombo.enabled = true;
            _federatedConnectionIDPChannelSection.authMechanismCombo.enabled = true;
            _federatedConnectionIDPChannelSection.configureAuthMechanism.enabled = true;
            _federatedConnectionIDPChannelSection.authContractCombo.enabled = true;
            _federatedConnectionIDPChannelSection.accountLinkagePolicyCombo.enabled = true;
            _federatedConnectionIDPChannelSection.configureAccLinkagePolicy.enabled = true;
        }
        _dirty = true;
    }


}
}