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

package com.atricore.idbus.console.modeling.diagram.view.federatedconnection {
import com.atricore.idbus.console.components.ListItemValueObject;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.view.activation.*;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;

import com.atricore.idbus.console.modeling.diagram.model.request.CreateActivationElementRequest;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentityProviderChannel;
import com.atricore.idbus.console.services.dto.JOSSOActivation;

import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import com.atricore.idbus.console.services.dto.ServiceProviderChannel;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import mx.events.FlexEvent;

import mx.events.IndexChangedEvent;

import org.puremvc.as3.interfaces.INotification;

public class FederatedConnectionCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _roleA:FederatedProvider;
    private var _roleB:FederatedProvider;

    private var _federatedConnection:FederatedConnection;

    public function FederatedConnectionCreateMediator(name:String = null, viewComp:FederatedConnectionCreateForm = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }
    
    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleFederatedConnectionSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleFederatedConnectionSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    public function registerListeners():void {
        view.useInheritedSPSettings.addEventListener(Event.CHANGE, handleInheritedSpChkboxChanged);
        view.channelNavigator.addEventListener(IndexChangedEvent.CHANGE, tabIndexChangedHandler);
    }

    private function tabIndexChangedHandler(event:Event):void {
        if(view.channelNavigator.selectedChild == view.spChannelTab){
            view.useInheritedIDPSettings.addEventListener(Event.CHANGE, handleInheritedIdpChkboxChanged);
        }
    }
    

    private function handleInheritedSpChkboxChanged(event:Event):void {
        if(view.useInheritedSPSettings.selected){
            view.samlProfileSSOCheck.enabled = false;
            view.samlProfileSLOCheck.enabled = false;

            view.samlBindingHttpPostCheck.enabled = false;
            view.samlBindingHttpRedirectCheck.enabled = false;
            view.samlBindingArtifactCheck.enabled = false;
            view.samlBindingSoapCheck.enabled = false;
            
            view.signAuthRequestCheck.enabled = false;
            view.encryptAuthRequestCheck.enabled = false;

            view.userInfoLookupCombo.enabled = false;
            view.authMechanismCombo.enabled = false;
            view.configureAuthMechanism.enabled = false;
            view.authContractCombo.enabled = false;            
            view.accountLinkagePolicyCombo.enabled = false;
            view.configureAccLinkagePolicy.enabled = false;
        } else {
            view.samlProfileSSOCheck.enabled = true;
            view.samlProfileSLOCheck.enabled = true;

            view.samlBindingHttpPostCheck.enabled = true;
            view.samlBindingHttpRedirectCheck.enabled = true;
            view.samlBindingArtifactCheck.enabled = true;
            view.samlBindingSoapCheck.enabled = true;

            view.signAuthRequestCheck.enabled = true;
            view.encryptAuthRequestCheck.enabled = true;

            view.userInfoLookupCombo.enabled = true;
            view.authMechanismCombo.enabled = true;
            view.configureAuthMechanism.enabled = true;
            view.authContractCombo.enabled = true;
            view.accountLinkagePolicyCombo.enabled = true;
            view.configureAccLinkagePolicy.enabled = true;
        }
    }

    private function handleInheritedIdpChkboxChanged(event:Event):void {
        if(view.useInheritedIDPSettings.selected){
            view.spChannelSamlProfileSSOCheck.enabled = false;
            view.spChannelSamlProfileSLOCheck.enabled = false;

            view.spChannelSamlBindingHttpPostCheck.enabled = false;
            view.spChannelSamlBindingHttpRedirectCheck.enabled = false;
            view.spChannelSamlBindingArtifactCheck.enabled = false;
            view.spChannelSamlBindingSoapCheck.enabled = false;

            view.signAuthAssertionCheck.enabled = false;
            view.encryptAuthAssertionCheck.enabled = false;
            view.spChannelUserInfoLookupCombo.enabled = false;
            view.spChannelAuthContractCombo.enabled = false;
            view.spChannelAuthMechanismCombo.enabled = false;
            view.spChannelAuthAssertionEmissionPolicyCombo.enabled = false;
        } else {
            view.spChannelSamlProfileSSOCheck.enabled = true;
            view.spChannelSamlProfileSLOCheck.enabled = true;

            view.spChannelSamlBindingHttpPostCheck.enabled = true;
            view.spChannelSamlBindingHttpRedirectCheck.enabled = true;
            view.spChannelSamlBindingArtifactCheck.enabled = true;
            view.spChannelSamlBindingSoapCheck.enabled = true;

            view.signAuthAssertionCheck.enabled = true;
            view.encryptAuthAssertionCheck.enabled = true;
            view.spChannelUserInfoLookupCombo.enabled = true;
            view.spChannelAuthContractCombo.enabled = true;
            view.spChannelAuthMechanismCombo.enabled = true;
            view.spChannelAuthAssertionEmissionPolicyCombo.enabled = true;            
        }
    }

    private function resetForm():void {
        view.federatedConnectionName.text = "";
        view.federatedConnectionDescription.text = "";

        //RESET IDP CHANNEL
        view.preferredIDPChannel.selected = false;
        view.useInheritedSPSettings.selected = false;

        view.samlProfileSSOCheck.selected = false;
        view.samlProfileSLOCheck.selected = false;

        view.samlBindingHttpPostCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;

        view.signAuthRequestCheck.selected = false;
        view.encryptAuthRequestCheck.selected = false;

        view.userInfoLookupCombo.selectedIndex = 0;
        for each(var obj:ListItemValueObject in view.authMechanismCombo.dataProvider){
            obj.isSelected = false;
        }
        view.authContractCombo.selectedIndex = 0;
        view.accountLinkagePolicyCombo.selectedIndex = 0;

        //RESET SP CHANNEL
        view.spChannelSamlProfileSSOCheck.selected = false;
        view.spChannelSamlProfileSLOCheck.selected = false;

        view.spChannelSamlBindingHttpPostCheck.selected = false;
        view.spChannelSamlBindingHttpRedirectCheck.selected = false;
        view.spChannelSamlBindingArtifactCheck.selected = false;
        view.spChannelSamlBindingSoapCheck.selected = false;

        view.signAuthAssertionCheck.selected = false;
        view.encryptAuthAssertionCheck.selected = false;
        view.spChannelUserInfoLookupCombo.selectedIndex = 0;
        view.spChannelAuthContractCombo.selectedIndex = 0;
        view.spChannelAuthMechanismCombo.selectedIndex = 0;
        view.spChannelAuthAssertionEmissionPolicyCombo.selectedIndex = 0;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var federatedConnection:FederatedConnection = new FederatedConnection();

        federatedConnection.name = view.federatedConnectionName.text;
        federatedConnection.description = view.federatedConnectionDescription.text;

        //IDP CHANNEL
        var idpChannel:IdentityProviderChannel = new IdentityProviderChannel();
        idpChannel.preferred = view.preferredIDPChannel.selected;

        if(!view.useInheritedSPSettings.selected){
            idpChannel.overrideProviderSetup = true;
            
            idpChannel.activeBindings = new ArrayCollection();
            if(view.samlBindingHttpPostCheck.selected){
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if(view.samlBindingArtifactCheck.selected){
                idpChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if(view.samlBindingHttpRedirectCheck.selected){
                idpChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if(view.samlBindingSoapCheck.selected){
                idpChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            idpChannel.activeProfiles = new ArrayCollection();
            if(view.samlProfileSSOCheck.selected){
                idpChannel.activeProfiles.addItem(Profile.SSO);
            }
            if(view.samlProfileSLOCheck.selected){
                idpChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            // TODO save remaining fields
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy
        } else {
            idpChannel.overrideProviderSetup = false;
        }

        //SP CHANNEL
        var spChannel:ServiceProviderChannel = new ServiceProviderChannel();
        if(!view.useInheritedIDPSettings.selected){
            spChannel.overrideProviderSetup = true;

            spChannel.activeBindings = new ArrayCollection();
            if (view.spChannelSamlBindingHttpPostCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
            }
            if (view.spChannelSamlBindingArtifactCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
            }
            if (view.spChannelSamlBindingHttpRedirectCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
            }
            if (view.spChannelSamlBindingSoapCheck.selected) {
                spChannel.activeBindings.addItem(Binding.SAMLR2_SOAP);
            }

            spChannel.activeProfiles = new ArrayCollection();
            if (view.spChannelSamlProfileSSOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO);
            }
            if (view.spChannelSamlProfileSLOCheck.selected) {
                spChannel.activeProfiles.addItem(Profile.SSO_SLO);
            }

            // TODO save remaining fields
            //userInformationLookup
            //authenticationContract
            //authenticationMechanism
            //authenticationAssertionEmissionPolicy
        } else {
            spChannel.overrideProviderSetup = false;
        }

        if(_roleA is ServiceProvider && _roleB is IdentityProvider){
            idpChannel.name = _roleA.name + "-to-" + _roleB.name;
            idpChannel.connectionA = federatedConnection;
            federatedConnection.channelA = idpChannel;

            spChannel.name = _roleB.name + "-to-" + _roleA.name;
            spChannel.connectionB = federatedConnection;
            federatedConnection.channelB = spChannel;
        } else if(_roleA is IdentityProvider && _roleB is ServiceProvider){
            idpChannel.name = _roleB.name + "-to-" + _roleA.name;
            idpChannel.connectionB = federatedConnection;
            federatedConnection.channelB = idpChannel;

            spChannel.name = _roleA.name + "-to-" + _roleB.name;
            spChannel.connectionA = federatedConnection;
            federatedConnection.channelA = spChannel;
        }
        
        _federatedConnection = federatedConnection;
    }

    private function handleFederatedConnectionSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _federatedConnection.roleA = _roleA;
            _federatedConnection.roleB = _roleB;

            //TODO if idpchannel is preferred, go through all the idp channels in a SP and deselect preferred

            if(_roleA.federatedConnectionsA == null){
               _roleA.federatedConnectionsA = new ArrayCollection(); 
            }
            _roleA.federatedConnectionsA.addItem(_federatedConnection);
            if(_roleB.federatedConnectionsB == null){
               _roleB.federatedConnectionsB = new ArrayCollection(); 
            }
            _roleB.federatedConnectionsB.addItem(_federatedConnection);

            _projectProxy.currentIdentityApplianceElement = _federatedConnection;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():FederatedConnectionCreateForm
    {
        return viewComponent as FederatedConnectionCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
    }


    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
        var cfc:CreateFederatedConnectionElementRequest = notification.getBody() as CreateFederatedConnectionElementRequest;
        _roleA = cfc.roleA;
        _roleB = cfc.roleB;

    }


}
}