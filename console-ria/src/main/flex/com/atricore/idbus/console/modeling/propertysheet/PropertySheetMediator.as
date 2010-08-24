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
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentityvault.EmbeddedDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentityvault.ExternalDBIdentityVaultCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.dbidentityvault.ExternalDBIdentityVaultLookupSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idpchannel.IDPChannelContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idpchannel.IDPChannelCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.sp.ServiceProviderCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.spchannel.SPChannelContractSection;
import com.atricore.idbus.console.modeling.propertysheet.view.spchannel.SPChannelCoreSection;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.Channel;
import com.atricore.idbus.console.services.dto.DbIdentityVault;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityProviderChannel;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.UserInformationLookup;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.FlexEvent;
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
    private var _currentIdentityApplianceElement:Object;
    private var _ipContractSection:IdentityProviderContractSection;
    private var _spContractSection:ServiceProviderContractSection;
    private var _idpChannelContractSection:IDPChannelContractSection;
    private var _spChannelContractSection:SPChannelContractSection;
    private var _externalDbVaultLookupSection:ExternalDBIdentityVaultLookupSection;
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
                if (_projectProxy.currentIdentityApplianceElement is IdentityAppliance) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityAppliancePropertyTabs();
                } else
                if (_projectProxy.currentIdentityApplianceElement is IdentityProvider) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityProviderPropertyTabs();
                } else
                if(_projectProxy.currentIdentityApplianceElement is ServiceProvider) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableServiceProviderPropertyTabs();
                } else
                if(_projectProxy.currentIdentityApplianceElement is IdentityProviderChannel) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdpChannelPropertyTabs();
                }
                if(_projectProxy.currentIdentityApplianceElement is ServiceProviderChannel) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableSpChannelPropertyTabs();
                }
                if(_projectProxy.currentIdentityApplianceElement is DbIdentityVault) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    if((_currentIdentityApplianceElement as DbIdentityVault).embedded){
                        enableEmbeddedDbVaultPropertyTabs();
                    } else {
                        enableExternalDbVaultPropertyTabs();
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
            _ipCoreSection.idpLocationContext.text = identityProvider.location.context;
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
            identityProvider.location.context = _ipCoreSection.idpLocationContext.text;
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

            var defaultChannel:Channel = identityProvider.defaultChannel;
            if (defaultChannel != null) {
                for (var j:int = 0; j < defaultChannel.activeBindings.length; j ++) {
                    var tmpBinding:Binding = defaultChannel.activeBindings.getItemAt(j) as Binding;
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
                for (j = 0; j < defaultChannel.activeProfiles.length; j++) {
                    var tmpProfile:Profile = defaultChannel.activeProfiles.getItemAt(j) as Profile;
                    if (tmpProfile.name == Profile.SSO.name) {
                        _ipContractSection.samlProfileSSOCheck.selected = true;
                    }
                    if (tmpProfile.name == Profile.SSO_SLO.name) {
                        _ipContractSection.samlProfileSLOCheck.selected = true;
                    }
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

            var spChannel:ServiceProviderChannel = identityProvider.defaultChannel as ServiceProviderChannel;

            if (spChannel.activeBindings == null) {
                spChannel.activeBindings = new ArrayCollection();
            }
            spChannel.activeBindings.removeAll();
            if (_ipContractSection.samlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_ipContractSection.samlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_ipContractSection.samlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_ipContractSection.samlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (spChannel.activeProfiles == null) {
                spChannel.activeProfiles = new ArrayCollection();
            }
            spChannel.activeProfiles.removeAll();
            if (_ipContractSection.samlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_ipContractSection.samlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            identityProvider.defaultChannel = spChannel; 
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
            _spCoreSection.spLocationContext.text = serviceProvider.location.context;
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
            serviceProvider.location.context = _spCoreSection.spLocationContext.text;
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
    
            var defaultChannel:Channel = serviceProvider.defaultChannel;
            if (defaultChannel != null) {
                for (var j:int = 0; j < defaultChannel.activeBindings.length; j ++) {
                    var tmpBinding:Binding = defaultChannel.activeBindings.getItemAt(j) as Binding;
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
                for (j = 0; j < defaultChannel.activeProfiles.length; j++) {
                    var tmpProfile:Profile = defaultChannel.activeProfiles.getItemAt(j) as Profile;
                    if (tmpProfile.name == Profile.SSO.name) {
                        _spContractSection.samlProfileSSOCheck.selected = true;
                    }
                    if (tmpProfile.name == Profile.SSO_SLO.name) {
                        _spContractSection.samlProfileSLOCheck.selected = true;
                    }
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

            var idpChannel:IdentityProviderChannel = serviceProvider.defaultChannel as IdentityProviderChannel;
            if(idpChannel == null) {
                idpChannel = new IdentityProviderChannel();
            }

            if (idpChannel.activeBindings == null) {
                idpChannel.activeBindings = new ArrayCollection();
            }
            idpChannel.activeBindings.removeAll();
            if (_spContractSection.samlBindingHttpPostCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (_spContractSection.samlBindingArtifactCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (_spContractSection.samlBindingHttpRedirectCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (_spContractSection.samlBindingSoapCheck.selected) {
                idpChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            if (idpChannel.activeProfiles == null) {
                idpChannel.activeProfiles = new ArrayCollection();
            }
            idpChannel.activeProfiles.removeAll();
            if (_spContractSection.samlProfileSSOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (_spContractSection.samlProfileSLOCheck.selected) {
                idpChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            serviceProvider.defaultChannel = idpChannel;
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
            _idpChannelCoreSection.idpChannelLocationContext.text = idpChannel.location.context;
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
            idpChannel.location.context = _idpChannelCoreSection.idpChannelLocationContext.text;
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
            _spChannelCoreSection.spChannelLocationContext.text = spChannel.location.context;
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
            spChannel.location.context = _spChannelCoreSection.spChannelLocationContext.text;
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

    protected function enableEmbeddedDbVaultPropertyTabs():void {
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

        _embeddedDbVaultCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleEmbeddedDbVaultCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleEmbeddedDbVaultCorePropertyTabRollOut);
    }

    private function handleEmbeddedDbVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentityVault;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            _embeddedDbVaultCoreSection.userRepositoryName.text = dbIdentityVault.name;
            _embeddedDbVaultCoreSection.serverPort.text = dbIdentityVault.port.toString();
            _embeddedDbVaultCoreSection.schema.text = dbIdentityVault.schema;
            _embeddedDbVaultCoreSection.admin.text = dbIdentityVault.admin;
            _embeddedDbVaultCoreSection.adminPass.text = dbIdentityVault.password;
            _embeddedDbVaultCoreSection.confirmAdminPass.text = dbIdentityVault.password;

            _embeddedDbVaultCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.serverPort.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.schema.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.admin.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.adminPass.addEventListener(Event.CHANGE, handleSectionChange);
            _embeddedDbVaultCoreSection.confirmAdminPass.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_embeddedDbVaultCoreSection.nameValidator);
            _validators.push(_embeddedDbVaultCoreSection.serverPortValidator);
            _validators.push(_embeddedDbVaultCoreSection.schemaValidator);
            _validators.push(_embeddedDbVaultCoreSection.adminValidator);
        }
    }

    private function handleEmbeddedDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true) && comparePasswords(_embeddedDbVaultCoreSection.adminPass, _embeddedDbVaultCoreSection.confirmAdminPass)) {
            // bind model
            var dbIdentityVault:DbIdentityVault;

            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;
            dbIdentityVault.name = _embeddedDbVaultCoreSection.userRepositoryName.text;
            dbIdentityVault.port = parseInt(_embeddedDbVaultCoreSection.serverPort.text);
            dbIdentityVault.schema = _embeddedDbVaultCoreSection.schema.text;
            dbIdentityVault.admin = _embeddedDbVaultCoreSection.admin.text;
            dbIdentityVault.password = _embeddedDbVaultCoreSection.adminPass.text;

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

        // Contract Tab
        var contractPropertyTab:Group = new Group();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.name = "Lookup";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _externalDbVaultLookupSection = new ExternalDBIdentityVaultLookupSection();
        contractPropertyTab.addElement(_externalDbVaultLookupSection);
        _propertySheetsViewStack.addNewChild(contractPropertyTab);
        _externalDbVaultLookupSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalDbVaulLookupPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalDbVaultLookupPropertyTabRollOut);
    }

    private function handleExternalDbVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentityVault;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            _externalDbVaultCoreSection.userRepositoryName.text = dbIdentityVault.name;
            _externalDbVaultCoreSection.connectionName.text = dbIdentityVault.connectionName;
            //TODO DRIVER
            _externalDbVaultCoreSection.driverName.text = dbIdentityVault.driverName;
            _externalDbVaultCoreSection.connectionUrl.text = dbIdentityVault.connectionUrl;
            _externalDbVaultCoreSection.dbUsername.text = dbIdentityVault.admin;
            _externalDbVaultCoreSection.dbPassword.text = dbIdentityVault.password;

            _externalDbVaultCoreSection.userRepositoryName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.connectionName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.driverName.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.connectionUrl.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbUsername.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultCoreSection.dbPassword.addEventListener(Event.CHANGE, handleSectionChange);

            _validators = [];
            _validators.push(_externalDbVaultCoreSection.nameValidator);
            _validators.push(_externalDbVaultCoreSection.connNameValidator);
            _validators.push(_externalDbVaultCoreSection.driverNameValidator);
            _validators.push(_externalDbVaultCoreSection.connUrlValidator);
            _validators.push(_externalDbVaultCoreSection.dbUsernameValidator);
            _validators.push(_externalDbVaultCoreSection.dbPasswordValidator);
        }
    }

    private function handleExternalDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty && validate(true)) {
            // bind model
            var dbIdentityVault:DbIdentityVault;

            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;
            dbIdentityVault.name = _externalDbVaultCoreSection.userRepositoryName.text;
            dbIdentityVault.connectionName = _externalDbVaultCoreSection.connectionName.text;
            dbIdentityVault.driverName = _externalDbVaultCoreSection.driverName.text;
            dbIdentityVault.connectionUrl = _externalDbVaultCoreSection.connectionUrl.text;
            dbIdentityVault.admin = _externalDbVaultCoreSection.dbUsername.text;
            dbIdentityVault.password = _externalDbVaultCoreSection.dbPassword.text;

            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_UPDATED);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleExternalDbVaulLookupPropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentityVault;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;

        // if dbIdentityVault is null that means some other element was selected before completing this
        if (dbIdentityVault != null) {
            // bind view
            //if(dbIdentityVault.userInformationLookup == null){
                //dbIdentityVault.userInformationLookup = new UserInformationLookup();
            //}

            _externalDbVaultLookupSection.userQuery.text = dbIdentityVault.userInformationLookup.userQueryString;
            _externalDbVaultLookupSection.rolesQuery.text = dbIdentityVault.userInformationLookup.rolesQueryString;
            _externalDbVaultLookupSection.credentialsQuery.text = dbIdentityVault.userInformationLookup.credentialsQueryString;
            _externalDbVaultLookupSection.propertiesQuery.text = dbIdentityVault.userInformationLookup.userPropertiesQueryString;

            _externalDbVaultLookupSection.userQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.credentialsQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.rolesQuery.addEventListener(Event.CHANGE, handleSectionChange);
            _externalDbVaultLookupSection.propertiesQuery.addEventListener(Event.CHANGE, handleSectionChange);
        }
    }

    private function handleExternalDbVaultLookupPropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var dbIdentityVault:DbIdentityVault;
            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVault;

            if(dbIdentityVault.userInformationLookup == null){
               dbIdentityVault.userInformationLookup = new UserInformationLookup();
            }

            dbIdentityVault.userInformationLookup.userQueryString = _externalDbVaultLookupSection.userQuery.text;
            dbIdentityVault.userInformationLookup.rolesQueryString = _externalDbVaultLookupSection.rolesQuery.text;
            dbIdentityVault.userInformationLookup.credentialsQueryString =  _externalDbVaultLookupSection.credentialsQuery.text;
            dbIdentityVault.userInformationLookup.userPropertiesQueryString = _externalDbVaultLookupSection.propertiesQuery.text;

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

}
}