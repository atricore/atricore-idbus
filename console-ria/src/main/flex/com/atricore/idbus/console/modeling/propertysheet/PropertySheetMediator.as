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
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderContractSection;
import com.atricore.idbus.console.services.dto.BindingDTO;
import com.atricore.idbus.console.services.dto.ChannelDTO;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

import com.atricore.idbus.console.services.dto.IdentityProviderDTO;

import com.atricore.idbus.console.services.dto.LocationDTO;
import com.atricore.idbus.console.services.dto.ProfileDTO;

import com.atricore.idbus.console.services.dto.ServiceProviderChannelDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.containers.Canvas;
import mx.containers.ViewStack;
import mx.controls.TabBar;
import mx.events.FlexEvent;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.propertysheet.view.appliance.IdentityApplianceCoreSection;
import com.atricore.idbus.console.modeling.propertysheet.view.idp.IdentityProviderCoreSection;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class PropertySheetMediator extends Mediator {
    public static const NAME:String = "PropertySheetMediator";
    private var _tabbedPropertiesTabBar:TabBar;
    private var _propertySheetsViewStack:ViewStack;
    private var _iaCoreSection:IdentityApplianceCoreSection;
    private var _ipCoreSection:IdentityProviderCoreSection;
    private var _projectProxy:ProjectProxy;
    private var _currentIdentityApplianceElement:Object;
    private var _ipContractSection:IdentityProviderContractSection;

    public function PropertySheetMediator(viewComp:PropertySheetView) {
        super(NAME, viewComp);
        _tabbedPropertiesTabBar = viewComp.tabbedPropertiesTabBar
        _propertySheetsViewStack = viewComp.propertySheetsViewStack;
        _projectProxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE,
            ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE:
                clearPropertyTabs();
                break;
            case ApplicationFacade.NOTE_DIAGRAM_ELEMENT_SELECTED:
                if (_projectProxy.currentIdentityApplianceElement is IdentityApplianceDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityAppliancePropertyTabs();
                } else
                if (_projectProxy.currentIdentityApplianceElement is IdentityProviderDTO) {
                    _currentIdentityApplianceElement = _projectProxy.currentIdentityApplianceElement;
                    enableIdentityProviderPropertyTabs();
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
        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        identityAppliance = proxy.currentIdentityAppliance;

        // bind view
        _iaCoreSection.applianceName.text = identityAppliance.idApplianceDefinition.name;
    }

    private function handleIdentityApplianceCorePropertyTabRollOut(e:Event):void {
        trace(e);
        // bind model
        // fetch appliance object
        var identityAppliance:IdentityApplianceDTO;
        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        identityAppliance = proxy.currentIdentityAppliance;

        identityAppliance.idApplianceDefinition.name = _iaCoreSection.applianceName.text;
        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED);
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
    }


    private function handleIdentityProviderCorePropertyTabRollOut(e:Event):void {
        // bind model
        var identityProvider:IdentityProviderDTO;

        identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

        identityProvider.name = _ipCoreSection.identityProviderName.text;
        identityProvider.description = _ipCoreSection.identityProvDescription.text;

        var loc:LocationDTO = new LocationDTO();
        loc.protocol = _ipCoreSection.idpLocationProtocol.selectedLabel;
        loc.host = _ipCoreSection.idpLocationDomain.text;
        loc.port = parseInt(_ipCoreSection.idpLocationPort.text);
        loc.context = _ipCoreSection.idpLocationContext.text;
        loc.uri = _ipCoreSection.idpLocationPath.text;
        identityProvider.location = loc;


        // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
        //userInformationLookup
        //authenticationContract
        //authenticationMechanism
        //authenticationAssertionEmissionPolicy

        sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_UPDATED);
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
    }

    private function handleIdentityProviderContractPropertyTabRollOut(event:Event):void {

        var identityProvider:IdentityProviderDTO;

        identityProvider = _currentIdentityApplianceElement as IdentityProviderDTO;

        var spChannel:ServiceProviderChannelDTO = new ServiceProviderChannelDTO();

        spChannel.activeBindings = new ArrayCollection();
        if (_ipContractSection.samlBindingHttpPostCheck.selected) {
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
        }
        if (_ipContractSection.samlBindingArtifactCheck.selected) {
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
        }
        if (_ipContractSection.samlBindingHttpRedirectCheck.selected) {
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
        }

        spChannel.activeProfiles = new ArrayCollection();
        if (_ipContractSection.samlProfileSSOCheck.selected) {
            spChannel.activeProfiles.addItem(ProfileDTO.SSO);
        }
        if (_ipContractSection.samlProfileSSOCheck.selected) {
            spChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
        }

        identityProvider.defaultChannel = spChannel;
        identityProvider.signAuthenticationAssertions = _ipContractSection.signAuthAssertionCheck.selected;
        identityProvider.encryptAuthenticationAssertions = _ipContractSection.encryptAuthAssertionCheck.selected;

    }


    protected function clearPropertyTabs():void {
        // Attach appliance editor form to property tabbed view
        _propertySheetsViewStack.removeAllChildren();

    }

    protected function get view():PropertySheetView
    {
        return viewComponent as PropertySheetView;
    }


}
}