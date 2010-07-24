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
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
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
import com.atricore.idbus.console.services.dto.BindingDTO;
import com.atricore.idbus.console.services.dto.ChannelDTO;
import com.atricore.idbus.console.services.dto.DbIdentityVaultDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderChannelDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderDTO;
import com.atricore.idbus.console.services.dto.LocationDTO;
import com.atricore.idbus.console.services.dto.ProfileDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderChannelDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;
import com.atricore.idbus.console.services.dto.UserInformationLookupDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.containers.Canvas;
import mx.containers.ViewStack;
import mx.controls.TabBar;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class PropertySheetMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _tabbedPropertiesTabBar:TabBar;
    private var _propertySheetsViewStack:ViewStack;
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
                if (_projectProxy.currentIdentityApplianceElement is IdentityApplianceDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityAppliancePropertyTabs();
                } else
                if (_projectProxy.currentIdentityApplianceElement is IdentityProviderDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityProviderPropertyTabs();
                } else
                if(_projectProxy.currentIdentityApplianceElement is ServiceProviderDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableServiceProviderPropertyTabs();
                } else
                if(_projectProxy.currentIdentityApplianceElement is IdentityProviderChannelDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdpChannelPropertyTabs();
                }
                if(_projectProxy.currentIdentityApplianceElement is ServiceProviderChannelDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableSpChannelPropertyTabs();
                }
                if(_projectProxy.currentIdentityApplianceElement is DbIdentityVaultDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    if((_currentIdentityApplianceElement as DbIdentityVaultDTO).embedded){
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

        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _iaCoreSection = new IdentityApplianceCoreSection();
        corePropertyTab.addChild(_iaCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _iaCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityApplianceCorePropertyTabRollOut);

    }

    private function handleCorePropertyTabCreationComplete(event:Event):void {
        var identityAppliance:IdentityApplianceDTO;

        // fetch appliance object
        identityAppliance = projectProxy.currentIdentityAppliance;

        // bind view
        _iaCoreSection.applianceName.text = identityAppliance.idApplianceDefinition.name;
        _iaCoreSection.applianceDescription.text = identityAppliance.idApplianceDefinition.description;

        var location:LocationDTO = identityAppliance.idApplianceDefinition.location;
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
    }

    private function handleIdentityApplianceCorePropertyTabRollOut(e:Event):void {
        trace(e);
        if (_dirty) {
             // bind model
            // fetch appliance object
            var identityAppliance:IdentityApplianceDTO;
            identityAppliance = projectProxy.currentIdentityAppliance;

            identityAppliance.idApplianceDefinition.name = _iaCoreSection.applianceName.text;
            identityAppliance.idApplianceDefinition.description = _iaCoreSection.applianceDescription.text;
            identityAppliance.idApplianceDefinition.location.protocol = _iaCoreSection.applianceLocationProtocol.selectedLabel;
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
        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _ipCoreSection = new IdentityProviderCoreSection();
        corePropertyTab.addChild(_ipCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _ipCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Canvas = new Canvas();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.label = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _ipContractSection = new IdentityProviderContractSection();
        contractPropertyTab.addChild(_ipContractSection);
        _propertySheetsViewStack.addChild(contractPropertyTab);

        _ipContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityProviderContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdentityProviderContractPropertyTabRollOut);
    }

    protected function enableServiceProviderPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _spCoreSection = new ServiceProviderCoreSection();
        corePropertyTab.addChild(_spCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _spCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleServiceProviderCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Canvas = new Canvas();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.label = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _spContractSection = new ServiceProviderContractSection();
        contractPropertyTab.addChild(_spContractSection);
        _propertySheetsViewStack.addChild(contractPropertyTab);

        _spContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleServiceProviderContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleServiceProviderContractPropertyTabRollOut);
    }

    private function handleIdentityProviderCorePropertyTabCreationComplete(event:Event):void {
        var identityProvider:IdentityProviderDTO;

        identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

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
    }


    private function handleIdentityProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var identityProvider:IdentityProviderDTO;

            identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

            identityProvider.name = _ipCoreSection.identityProviderName.text;
            identityProvider.description = _ipCoreSection.identityProvDescription.text;

            identityProvider.location.protocol = _ipCoreSection.idpLocationProtocol.selectedLabel;
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

        var identityProvider:IdentityProviderDTO;

        identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

        _ipContractSection.signAuthAssertionCheck.selected = identityProvider.signAuthenticationAssertions;
        _ipContractSection.encryptAuthAssertionCheck.selected = identityProvider.encryptAuthenticationAssertions;

        var defaultChannel:ChannelDTO = identityProvider.defaultChannel;
        if (defaultChannel != null) {
            for (var j:int = 0; j < defaultChannel.activeBindings.length; j ++) {
                var tmpBinding:BindingDTO = defaultChannel.activeBindings.getItemAt(j) as BindingDTO;
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_POST.name) {
                    _ipContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_REDIRECT.name) {
                    _ipContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_ARTIFACT.name) {
                    _ipContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_SOAP.name) {
                    _ipContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < defaultChannel.activeProfiles.length; j++) {
                var tmpProfile:ProfileDTO = defaultChannel.activeProfiles.getItemAt(j) as ProfileDTO;
                if (tmpProfile.name == ProfileDTO.SSO.name) {
                    _ipContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == ProfileDTO.SSO_SLO.name) {
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

    private function handleIdentityProviderContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var identityProvider:IdentityProviderDTO;

            identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

            var spChannel:ServiceProviderChannelDTO = identityProvider.defaultChannel as ServiceProviderChannelDTO;

            if (spChannel.activeBindings == null) {
                spChannel.activeBindings = new ArrayCollection();
            }
            spChannel.activeBindings.removeAll();
            if (_ipContractSection.samlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
            }
            if (_ipContractSection.samlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
            }
            if (_ipContractSection.samlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
            }
            if (_ipContractSection.samlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
            }

            if (spChannel.activeProfiles == null) {
                spChannel.activeProfiles = new ArrayCollection();
            }
            spChannel.activeProfiles.removeAll();
            if (_ipContractSection.samlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(ProfileDTO.SSO);
            }
            if (_ipContractSection.samlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
            }

            identityProvider.defaultChannel = spChannel; 
            identityProvider.signAuthenticationAssertions = _ipContractSection.signAuthAssertionCheck.selected;
            identityProvider.encryptAuthenticationAssertions = _ipContractSection.encryptAuthAssertionCheck.selected;

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    private function handleServiceProviderCorePropertyTabCreationComplete(event:Event):void {
        var serviceProvider:ServiceProviderDTO;

        serviceProvider = _currentIdentityApplianceElement as ServiceProviderDTO;
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
    }

    private function handleServiceProviderCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var serviceProvider:ServiceProviderDTO;

            serviceProvider = _currentIdentityApplianceElement as ServiceProviderDTO;

            serviceProvider.name = _spCoreSection.serviceProvName.text;
            serviceProvider.description = _spCoreSection.serviceProvDescription.text;

            serviceProvider.location.protocol = _spCoreSection.spLocationProtocol.selectedLabel;
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

        var serviceProvider:ServiceProviderDTO;

        serviceProvider = _currentIdentityApplianceElement as ServiceProviderDTO;

//        _spContractSection.signAuthRequestCheck.selected = serviceProvider.signAuthenticationAssertions;
//        _spContractSection.encryptAuthRequestCheck.selected = serviceProvider.encryptAuthenticationAssertions;

        var defaultChannel:ChannelDTO = serviceProvider.defaultChannel;
        if (defaultChannel != null) {
            for (var j:int = 0; j < defaultChannel.activeBindings.length; j ++) {
                var tmpBinding:BindingDTO = defaultChannel.activeBindings.getItemAt(j) as BindingDTO;
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_POST.name) {
                    _spContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_REDIRECT.name) {
                    _spContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_ARTIFACT.name) {
                    _spContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_SOAP.name) {
                    _spContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < defaultChannel.activeProfiles.length; j++) {
                var tmpProfile:ProfileDTO = defaultChannel.activeProfiles.getItemAt(j) as ProfileDTO;
                if (tmpProfile.name == ProfileDTO.SSO.name) {
                    _spContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == ProfileDTO.SSO_SLO.name) {
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

    private function handleServiceProviderContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var serviceProvider:ServiceProviderDTO;

            serviceProvider = _currentIdentityApplianceElement as ServiceProviderDTO;

            var idpChannel:IdentityProviderChannelDTO = serviceProvider.defaultChannel as IdentityProviderChannelDTO;
            if(idpChannel == null) {
                idpChannel = new IdentityProviderChannelDTO();
            }

            if (idpChannel.activeBindings == null) {
                idpChannel.activeBindings = new ArrayCollection();
            }
            idpChannel.activeBindings.removeAll();
            if (_spContractSection.samlBindingHttpPostCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
            }
            if (_spContractSection.samlBindingArtifactCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
            }
            if (_spContractSection.samlBindingHttpRedirectCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
            }
            if (_spContractSection.samlBindingSoapCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
            }

            if (idpChannel.activeProfiles == null) {
                idpChannel.activeProfiles = new ArrayCollection();
            }
            idpChannel.activeProfiles.removeAll();
            if (_spContractSection.samlProfileSSOCheck.selected) {
                idpChannel.activeProfiles.addItem(ProfileDTO.SSO);
            }
            if (_spContractSection.samlProfileSLOCheck.selected) {
                idpChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
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
        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _idpChannelCoreSection = new IDPChannelCoreSection();
        corePropertyTab.addChild(_idpChannelCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _idpChannelCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdpChannelCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdpChannelCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Canvas = new Canvas();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.label = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _idpChannelContractSection = new IDPChannelContractSection();
        contractPropertyTab.addChild(_idpChannelContractSection);
        _propertySheetsViewStack.addChild(contractPropertyTab);

        _idpChannelContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdpChannelContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleIdpChannelContractPropertyTabRollOut);
    }

    private function handleIdpChannelCorePropertyTabCreationComplete(event:Event):void {
        var idpChannel:IdentityProviderChannelDTO;

        idpChannel = _currentIdentityApplianceElement as IdentityProviderChannelDTO;
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
    }

    private function handleIdpChannelCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var idpChannel:IdentityProviderChannelDTO;

            idpChannel = _currentIdentityApplianceElement as IdentityProviderChannelDTO;

            idpChannel.name = _idpChannelCoreSection.identityProvChannelName.text;
            idpChannel.description = _idpChannelCoreSection.identityProvChannelDescription.text;

            if(idpChannel.location == null){
                idpChannel.location = new LocationDTO();
            }

            idpChannel.location.protocol = _idpChannelCoreSection.idpChannelLocationProtocol.selectedLabel;
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

        var idpChannel:IdentityProviderChannelDTO;

        idpChannel = _currentIdentityApplianceElement as IdentityProviderChannelDTO;

//        _spContractSection.signAuthRequestCheck.selected = serviceProvider.signAuthenticationAssertions;
//        _spContractSection.encryptAuthRequestCheck.selected = serviceProvider.encryptAuthenticationAssertions;

        if (idpChannel != null) {
            for (var j:int = 0; j < idpChannel.activeBindings.length; j ++) {
                var tmpBinding:BindingDTO = idpChannel.activeBindings.getItemAt(j) as BindingDTO;
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_POST.name) {
                    _idpChannelContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_REDIRECT.name) {
                    _idpChannelContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_ARTIFACT.name) {
                    _idpChannelContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_SOAP.name) {
                    _idpChannelContractSection.samlBindingSoapCheck.selected = true;
                }                      
            }
            for (j = 0; j < idpChannel.activeProfiles.length; j++) {
                var tmpProfile:ProfileDTO = idpChannel.activeProfiles.getItemAt(j) as ProfileDTO;
                if (tmpProfile.name == ProfileDTO.SSO.name) {
                    _idpChannelContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == ProfileDTO.SSO_SLO.name) {
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

    private function handleIdpChannelContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var idpChannel:IdentityProviderChannelDTO;

            idpChannel = _currentIdentityApplianceElement as IdentityProviderChannelDTO;

            idpChannel.activeBindings = new ArrayCollection();
            if (_idpChannelContractSection.samlBindingHttpPostCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
            }
            if (_idpChannelContractSection.samlBindingArtifactCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
            }
            if (_idpChannelContractSection.samlBindingHttpRedirectCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
            }
            if (_idpChannelContractSection.samlBindingSoapCheck.selected) {
                idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
            }

            idpChannel.activeProfiles = new ArrayCollection();
            if (_idpChannelContractSection.samlProfileSSOCheck.selected) {
                idpChannel.activeProfiles.addItem(ProfileDTO.SSO);
            }
            if (_idpChannelContractSection.samlProfileSLOCheck.selected) {
                idpChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    protected function enableSpChannelPropertyTabs():void {
        // Attach sp channel editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        // Core Tab
        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _spChannelCoreSection = new SPChannelCoreSection();
        corePropertyTab.addChild(_spChannelCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _spChannelCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSpChannelCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSpChannelCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Canvas = new Canvas();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.label = "Contract";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _spChannelContractSection = new SPChannelContractSection();
        contractPropertyTab.addChild(_spChannelContractSection);
        _propertySheetsViewStack.addChild(contractPropertyTab);

        _spChannelContractSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSpChannelContractPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleSpChannelContractPropertyTabRollOut);
    }

    private function handleSpChannelCorePropertyTabCreationComplete(event:Event):void {
        var spChannel:ServiceProviderChannelDTO;

        spChannel = _currentIdentityApplianceElement as ServiceProviderChannelDTO;
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
    }

    private function handleSpChannelCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var spChannel:ServiceProviderChannelDTO;

            spChannel = _currentIdentityApplianceElement as ServiceProviderChannelDTO;

            spChannel.name = _spChannelCoreSection.serviceProvChannelName.text;
            spChannel.description = _spChannelCoreSection.serviceProvChannelDescription.text;

            if(spChannel.location == null){
                spChannel.location = new LocationDTO();
            }

            spChannel.location.protocol = _spChannelCoreSection.spChannelLocationProtocol.selectedLabel;
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

        var spChannel:ServiceProviderChannelDTO;

        spChannel = _currentIdentityApplianceElement as ServiceProviderChannelDTO;

        if (spChannel != null) {
            for (var j:int = 0; j < spChannel.activeBindings.length; j ++) {
                var tmpBinding:BindingDTO = spChannel.activeBindings.getItemAt(j) as BindingDTO;
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_POST.name) {
                    _spChannelContractSection.samlBindingHttpPostCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_HTTP_REDIRECT.name) {
                    _spChannelContractSection.samlBindingHttpRedirectCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_ARTIFACT.name) {
                    _spChannelContractSection.samlBindingArtifactCheck.selected = true;
                }
                if (tmpBinding.name == BindingDTO.SAMLR2_SOAP.name) {
                    _spChannelContractSection.samlBindingSoapCheck.selected = true;
                }
            }
            for (j = 0; j < spChannel.activeProfiles.length; j++) {
                var tmpProfile:ProfileDTO = spChannel.activeProfiles.getItemAt(j) as ProfileDTO;
                if (tmpProfile.name == ProfileDTO.SSO.name) {
                    _spChannelContractSection.samlProfileSSOCheck.selected = true;
                }
                if (tmpProfile.name == ProfileDTO.SSO_SLO.name) {
                    _spChannelContractSection.samlProfileSLOCheck.selected = true;
                }
            }
        }

        _spChannelContractSection.samlBindingHttpPostCheck.addEventListener(Event.CHANGE, handleSectionChange);
        _spChannelContractSection.samlBindingHttpRedirectCheck.addEventListener(Event.CHANGE, handleSectionChange);
        _spChannelContractSection.samlBindingArtifactCheck.addEventListener(Event.CHANGE, handleSectionChange);
        _spChannelContractSection.samlProfileSSOCheck.addEventListener(Event.CHANGE, handleSectionChange);
        _spChannelContractSection.samlProfileSLOCheck.addEventListener(Event.CHANGE, handleSectionChange);
    }

    private function handleSpChannelContractPropertyTabRollOut(event:Event):void {
        if (_dirty) {
            var spChannel:ServiceProviderChannelDTO;

            spChannel = _currentIdentityApplianceElement as ServiceProviderChannelDTO;

            spChannel.activeBindings = new ArrayCollection();
            if (_spChannelContractSection.samlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
            }
            if (_spChannelContractSection.samlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
            }
            if (_spChannelContractSection.samlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
            }
            if (_spChannelContractSection.samlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
            }

            spChannel.activeProfiles = new ArrayCollection();
            if (_spChannelContractSection.samlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(ProfileDTO.SSO);
            }
            if (_spChannelContractSection.samlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
            }

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            _dirty = false;
        }
    }

    protected function enableEmbeddedDbVaultPropertyTabs():void {
        // Attach embedded DB identity vault editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _embeddedDbVaultCoreSection = new EmbeddedDBIdentityVaultCoreSection();
        corePropertyTab.addChild(_embeddedDbVaultCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _embeddedDbVaultCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleEmbeddedDbVaultCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleEmbeddedDbVaultCorePropertyTabRollOut);
    }

    private function handleEmbeddedDbVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentityVaultDTO;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;
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
    }

    private function handleEmbeddedDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var dbIdentityVault:DbIdentityVaultDTO;

            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;
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
        var corePropertyTab:Canvas = new Canvas();
        corePropertyTab.id = "propertySheetCoreSection";
        corePropertyTab.label = "Core";
        corePropertyTab.width = Number("100%");
        corePropertyTab.height = Number("100%");
        corePropertyTab.setStyle("borderStyle", "solid");

        _externalDbVaultCoreSection = new ExternalDBIdentityVaultCoreSection();
        corePropertyTab.addChild(_externalDbVaultCoreSection);
        _propertySheetsViewStack.addChild(corePropertyTab);

        _externalDbVaultCoreSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalDbVaultCorePropertyTabCreationComplete);
        corePropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalDbVaultCorePropertyTabRollOut);

        // Contract Tab
        var contractPropertyTab:Canvas = new Canvas();
        contractPropertyTab.id = "propertySheetContractSection";
        contractPropertyTab.label = "Lookup";
        contractPropertyTab.width = Number("100%");
        contractPropertyTab.height = Number("100%");
        contractPropertyTab.setStyle("borderStyle", "solid");

        _externalDbVaultLookupSection = new ExternalDBIdentityVaultLookupSection();
        contractPropertyTab.addChild(_externalDbVaultLookupSection);
        _propertySheetsViewStack.addChild(contractPropertyTab);

        _externalDbVaultLookupSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExternalDbVaulLookupPropertyTabCreationComplete);
        contractPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleExternalDbVaultLookupPropertyTabRollOut);
    }

    private function handleExternalDbVaultCorePropertyTabCreationComplete(event:Event):void {
        var dbIdentityVault:DbIdentityVaultDTO;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;
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
    }

    private function handleExternalDbVaultCorePropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var dbIdentityVault:DbIdentityVaultDTO;

            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;
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
        var dbIdentityVault:DbIdentityVaultDTO;

        dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;
        // bind view
//        if(dbIdentityVault.userInformationLookup == null){
//           dbIdentityVault.userInformationLookup = new UserInformationLookupDTO();
//        }

        _externalDbVaultLookupSection.userQuery.text = dbIdentityVault.userInformationLookup.userQueryString;
        _externalDbVaultLookupSection.rolesQuery.text = dbIdentityVault.userInformationLookup.rolesQueryString;
        _externalDbVaultLookupSection.credentialsQuery.text = dbIdentityVault.userInformationLookup.credentialsQueryString;
        _externalDbVaultLookupSection.propertiesQuery.text = dbIdentityVault.userInformationLookup.userPropertiesQueryString;

        _externalDbVaultLookupSection.userQuery.addEventListener(Event.CHANGE, handleSectionChange);
        _externalDbVaultLookupSection.credentialsQuery.addEventListener(Event.CHANGE, handleSectionChange);
        _externalDbVaultLookupSection.rolesQuery.addEventListener(Event.CHANGE, handleSectionChange);
        _externalDbVaultLookupSection.propertiesQuery.addEventListener(Event.CHANGE, handleSectionChange);
    }

    private function handleExternalDbVaultLookupPropertyTabRollOut(e:Event):void {
        if (_dirty) {
            // bind model
            var dbIdentityVault:DbIdentityVaultDTO;
            dbIdentityVault = _currentIdentityApplianceElement as DbIdentityVaultDTO;

            if(dbIdentityVault.userInformationLookup == null){
               dbIdentityVault.userInformationLookup = new UserInformationLookupDTO();
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

    }

    private function handleSectionChange(event:Event) {
        _dirty = true;
    }
    
    protected function get view():PropertySheetView
    {
        return viewComponent as PropertySheetView;
    }


}
}