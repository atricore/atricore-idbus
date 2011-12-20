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
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.CreateFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.main.controller.AccountLinkagePolicyListCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityMappingPoliciesListCommand;
import com.atricore.idbus.console.modeling.main.controller.SubjectNameIDPolicyListCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.AccountLinkEmitterType;
import com.atricore.idbus.console.services.dto.AuthenticationAssertionEmissionPolicy;
import com.atricore.idbus.console.services.dto.AuthenticationContract;
import com.atricore.idbus.console.services.dto.AuthenticationMechanism;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.BindAuthentication;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.ExternalIdentityProvider;
import com.atricore.idbus.console.services.dto.ExternalServiceProvider;
import com.atricore.idbus.console.services.dto.FederatedConnection;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityMappingType;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.IdentityProviderChannel;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;
import com.atricore.idbus.console.services.dto.TwoFactorAuthentication;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class FederatedConnectionCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _roleA:FederatedProvider;
    private var _roleB:FederatedProvider;
    
    private var _federatedConnection:FederatedConnection;

    private var _spLocation:Location;
    private var _idpLocation:Location;
    private var _spName:String;
    private var _idpName:String;

    [Bindable]
    public var _accountLinkagePolicies:ArrayCollection;

    [Bindable]
    public var _identityMappingPolicies:ArrayCollection;

    [Bindable]
    public var _subjectNameIdentifierPolicies:ArrayCollection;

    
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
        BindingUtils.bindProperty(view.accountLinkagePolicyCombo, "dataProvider", this, "_accountLinkagePolicies");
        BindingUtils.bindProperty(view.identityMappingPolicyCombo, "dataProvider", this, "_identityMappingPolicies");
        BindingUtils.bindProperty(view.subjectNameIdPolicyCombo, "dataProvider", this, "_subjectNameIdentifierPolicies");

        view.btnOk.addEventListener(MouseEvent.CLICK, handleFederatedConnectionSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.federatedConnectionName);
    }

    public function registerListeners():void {
        view.useInheritedSPSettings.addEventListener(Event.CHANGE, handleInheritedSpChkboxChanged);
        view.useInheritedIDPSettings.addEventListener(Event.CHANGE, handleInheritedIdpChkboxChanged);
    }

    private function reflectSPSettingsInIdpChannelTab():void {
        if (_roleA is ExternalServiceProvider || _roleB is ExternalServiceProvider) {
            view.idpChannelTab.enabled = false;
            view.channelNavigator.selectedIndex = 1;
            return;
        }

        if(_roleA is ServiceProvider){
            var sp:ServiceProvider = _roleA as ServiceProvider;
        } else if (_roleB is ServiceProvider){
            sp = _roleB as ServiceProvider;
        }

        if (_roleA is IdentityProvider) {
            _idpName = (_roleA as IdentityProvider).name;
        } else if (_roleA is ExternalIdentityProvider) {
            _idpName = (_roleA as ExternalIdentityProvider).name;
        } else if (_roleB is IdentityProvider) {
            _idpName = (_roleB as IdentityProvider).name;
        } else if (_roleB is ExternalIdentityProvider) {
            _idpName = (_roleB as ExternalIdentityProvider).name;
        }

        view.signAuthnRequestsCheck.selected = sp.signAuthenticationRequests;
        view.wantAssertionSignedCheck.selected = sp.wantAssertionSigned;
        view.samlBindingHttpPostCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;
        view.samlProfileSSOCheck.selected = false;
        view.samlProfileSLOCheck.selected = false;

        for each (var tmpBinding:Binding in sp.activeBindings) {
            if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                view.samlBindingHttpPostCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                view.samlBindingHttpRedirectCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                view.samlBindingArtifactCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                view.samlBindingSoapCheck.selected = true;
            }
        }
        for each (var tmpProfile:Profile in sp.activeProfiles) {
            if (tmpProfile.name == Profile.SSO.name) {
                view.samlProfileSSOCheck.selected = true;
            }
            if (tmpProfile.name == Profile.SSO_SLO.name) {
                view.samlProfileSLOCheck.selected = true;
            }
        }

        // set account linkage policy
        if (view.accountLinkagePolicyCombo.dataProvider != null) {
            if (sp.accountLinkagePolicy != null) {
                for (var i:int=0; i < view.accountLinkagePolicyCombo.dataProvider.length; i++) {
                    if(view.accountLinkagePolicyCombo.dataProvider[i].name == sp.accountLinkagePolicy.name){
                        view.accountLinkagePolicyCombo.selectedIndex = i;
                        break;
                    }
                }
            } else {
                for (var j:int=0; j < view.accountLinkagePolicyCombo.dataProvider.length; j++) {
                    if (view.accountLinkagePolicyCombo.dataProvider[j].linkEmitterType.toString() == AccountLinkEmitterType.ONE_TO_ONE.toString()) {
                        view.accountLinkagePolicyCombo.selectedIndex = j;
                        break;
                    }
                }
            }
        }

        // set identity mapping policy
        if (view.identityMappingPolicyCombo.dataProvider != null) {
            if (sp.identityMappingPolicy != null) {
                for (var k:int=0; k < view.identityMappingPolicyCombo.dataProvider.length; k++) {
                    if(view.identityMappingPolicyCombo.dataProvider[k].name == sp.identityMappingPolicy.name){
                        view.identityMappingPolicyCombo.selectedIndex = k;
                        break;
                    }
                }
            } else {
                for (var l:int=0; l < view.identityMappingPolicyCombo.dataProvider.length; l++) {
                    if (view.identityMappingPolicyCombo.dataProvider[l].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                        view.identityMappingPolicyCombo.selectedIndex = l;
                        break;
                    }
                }
            }
        }

        // set location
        _spLocation = sp.location;
        _spName = sp.name;
        updateIdpChannelLocation();

        view.useInheritedSPSettings.selected = true;
        setIdpChannelFields();
    }

    private function reflectIdpSettingsInSpChannelTab():void {
        if (_roleA is ExternalIdentityProvider || _roleB is ExternalIdentityProvider) {
            view.spChannelTab.enabled = false;
            return;
        }

        if(_roleA is IdentityProvider){
            var idp:IdentityProvider = _roleA as IdentityProvider;
        } else if (_roleB is IdentityProvider){
            idp = _roleB as IdentityProvider;
        }

        if (_roleA is ServiceProvider) {
            _spName = (_roleA as ServiceProvider).name;
        } else if (_roleA is ExternalServiceProvider) {
            _spName = (_roleA as ExternalServiceProvider).name;
        } else if (_roleB is ServiceProvider) {
            _spName = (_roleB as ServiceProvider).name;
        } else if (_roleB is ExternalServiceProvider) {
            _spName = (_roleB as ExternalServiceProvider).name;
        }

        view.wantAuthnRequestsSignedCheck.selected = idp.wantAuthnRequestsSigned;

        view.spChannelSamlBindingHttpPostCheck.selected = false;
        view.spChannelSamlBindingHttpRedirectCheck.selected = false;
        view.spChannelSamlBindingArtifactCheck.selected = false;
        view.spChannelSamlBindingSoapCheck.selected = false;
        view.spChannelSamlProfileSSOCheck.selected = false;
        view.spChannelSamlProfileSLOCheck.selected = false;

        for each(var tmpBinding:Binding in idp.activeBindings) {
            if (tmpBinding.name == Binding.SAMLR2_HTTP_POST.name) {
                view.spChannelSamlBindingHttpPostCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_HTTP_REDIRECT.name) {
                view.spChannelSamlBindingHttpRedirectCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_ARTIFACT.name) {
                view.spChannelSamlBindingArtifactCheck.selected = true;
            }
            if (tmpBinding.name == Binding.SAMLR2_SOAP.name) {
                view.spChannelSamlBindingSoapCheck.selected = true;
            }
        }
        for each(var tmpProfile:Profile in idp.activeProfiles) {
            if (tmpProfile.name == Profile.SSO.name) {
                view.spChannelSamlProfileSSOCheck.selected = true;
            }
            if (tmpProfile.name == Profile.SSO_SLO.name) {
                view.spChannelSamlProfileSLOCheck.selected = true;
            }
        }

        // set location
        _idpLocation = idp.location;
        _idpName = idp.name;
        updateSpChannelLocation();

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
        for (var j:int = 0; j < view.spChannelAuthMechanism.dataProvider.length; j++) {
            if (view.spChannelAuthMechanism.dataProvider[j].data == selectedAuthnMechanism) {
                view.spChannelAuthMechanism.selectedIndex = j;
                break;
            }
        }
        
        view.useInheritedIDPSettings.selected = true;
        setSpChannelFields();
    }
    

    private function handleInheritedSpChkboxChanged(event:Event):void {
        setIdpChannelFields();
        if(view.useInheritedSPSettings.selected){
            reflectSPSettingsInIdpChannelTab();
        }
        updateIdpChannelLocation();
        registerValidators();
    }

    private function handleInheritedIdpChkboxChanged(event:Event):void {
        setSpChannelFields();
        if(view.useInheritedIDPSettings.selected){
            reflectIdpSettingsInSpChannelTab();
        }
        updateSpChannelLocation();
        registerValidators();
    }

    private function resetForm():void {
        view.federatedConnectionName.text = "";
        view.federatedConnectionDescription.text = "";

        view.channelNavigator.selectedIndex = 0;

        //RESET IDP CHANNEL
        view.preferredIDPChannel.selected = true;
        view.useInheritedSPSettings.selected = true;
        handleInheritedSpChkboxChanged(null);

        view.samlProfileSSOCheck.selected = false;
        view.samlProfileSLOCheck.selected = false;

        view.samlBindingHttpPostCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;

        view.signAuthnRequestsCheck.selected = false;
        view.wantAssertionSignedCheck.selected = false;

        view.accountLinkagePolicyCombo.selectedIndex = 0;
        view.identityMappingPolicyCombo.selectedIndex = 0;

        // reset location
        view.idpChannelLocationProtocol.selectedIndex = 0;
        view.idpChannelLocationDomain.text = "";
        view.idpChannelLocationPort.text = "";
        view.idpChannelLocationContext.text = "";
        view.idpChannelLocationPath.text = "";

        _spLocation = null;

        //RESET SP CHANNEL
        //if any checkbox is null means that the SP channel tab is not initialized
        if(view.spChannelSamlProfileSSOCheck != null){
            view.useInheritedIDPSettings.selected = true;
            handleInheritedIdpChkboxChanged(null);
            view.spChannelSamlProfileSSOCheck.selected = false;
            view.spChannelSamlProfileSLOCheck.selected = false;

            view.spChannelSamlBindingHttpPostCheck.selected = false;
            view.spChannelSamlBindingHttpRedirectCheck.selected = false;
            view.spChannelSamlBindingArtifactCheck.selected = false;
            view.spChannelSamlBindingSoapCheck.selected = false;

            view.wantAuthnRequestsSignedCheck.selected = false;
            view.spChannelAuthContractCombo.selectedIndex = 0;
            view.spChannelAuthMechanism.selectedIndex = 0;
            view.spChannelAuthAssertionEmissionPolicyCombo.selectedIndex = 0;

            // reset location
            view.spChannelLocationProtocol.selectedIndex = 0;
            view.spChannelLocationDomain.text = "";
            view.spChannelLocationPort.text = "";
            view.spChannelLocationContext.text = "";
            view.spChannelLocationPath.text = "";

            _idpLocation = null;;
        }

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var federatedConnection:FederatedConnection = new FederatedConnection();

        if(_roleA is ServiceProvider){
            var sp:ServiceProvider = _roleA as ServiceProvider;
        } else if (_roleB is ServiceProvider){
            sp = _roleB as ServiceProvider;
        }

        if(_roleA is IdentityProvider){
            var idp:IdentityProvider = _roleA as IdentityProvider;
        } else if (_roleB is IdentityProvider){
            idp = _roleB as IdentityProvider;
        }

        federatedConnection.name = view.federatedConnectionName.text;
        federatedConnection.description = view.federatedConnectionDescription.text;

        //IDP CHANNEL
        var idpChannel:IdentityProviderChannel = new IdentityProviderChannel();
        if (sp != null) {
            idpChannel.preferred = view.preferredIDPChannel.selected;

            if(!view.useInheritedSPSettings.selected){
                idpChannel.overrideProviderSetup = true;
            } else {
                idpChannel.overrideProviderSetup = false;
            }

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

            if(view.useInheritedSPSettings.selected){
                idpChannel.accountLinkagePolicy = sp.accountLinkagePolicy;
                idpChannel.identityMappingPolicy = sp.identityMappingPolicy;
            } else {
                idpChannel.accountLinkagePolicy = view.accountLinkagePolicyCombo.selectedItem;
                idpChannel.identityMappingPolicy = view.identityMappingPolicyCombo.selectedItem;
            }

            idpChannel.signAuthenticationRequests = view.signAuthnRequestsCheck.selected;
            idpChannel.wantAssertionSigned = view.wantAssertionSignedCheck.selected;

            // set location
            var loc:Location = new Location();
            loc.protocol = view.idpChannelLocationProtocol.labelDisplay.text;
            loc.host = view.idpChannelLocationDomain.text;
            loc.port = parseInt(view.idpChannelLocationPort.text);
            loc.context = view.idpChannelLocationContext.text;
            loc.uri = view.idpChannelLocationPath.text;
            idpChannel.location = loc;
        }

        //SP CHANNEL
        var spChannel:ServiceProviderChannel = new ServiceProviderChannel();
        if (idp != null) {
            if(!view.useInheritedIDPSettings.selected){
                spChannel.overrideProviderSetup = true;
            } else {
                spChannel.overrideProviderSetup = false;
            }

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

            /*if (view.spChannelAuthMechanism.selectedItem.data == "basic") {
                var basicAuth:BasicAuthentication = new BasicAuthentication();
                basicAuth.name = federatedConnection.name.replace(/\s+/g, "-").toLowerCase() + "-basic-authn";
                basicAuth.hashAlgorithm = "MD5";
                basicAuth.hashEncoding = "HEX";
                basicAuth.ignoreUsernameCase = false;
                spChannel.authenticationMechanism = basicAuth;
            }*/

            if (view.spChannelAuthContractCombo.selectedItem.data == "default") {
                var authContract:AuthenticationContract = new AuthenticationContract();
                authContract.name = "Default";
                spChannel.authenticationContract = authContract;
            }

            if (view.spChannelAuthAssertionEmissionPolicyCombo.selectedItem.data == "default") {
                var authAssertionEmissionPolicy:AuthenticationAssertionEmissionPolicy = new AuthenticationAssertionEmissionPolicy();
                authAssertionEmissionPolicy.name = "Default";
                spChannel.emissionPolicy = authAssertionEmissionPolicy;
            }

            spChannel.wantAuthnRequestsSigned = view.wantAuthnRequestsSignedCheck.selected;

            spChannel.ignoreRequestedNameIDPolicy = view.ignoreRequestedNameIDPolicy.selected;
            spChannel.subjectNameIDPolicy = view.subjectNameIdPolicyCombo.selectedItem;
            
            // set location
            var newLoc:Location = new Location();
            newLoc.protocol = view.spChannelLocationProtocol.labelDisplay.text;
            newLoc.host = view.spChannelLocationDomain.text;
            newLoc.port = parseInt(view.spChannelLocationPort.text);
            newLoc.context = view.spChannelLocationContext.text;
            newLoc.uri = view.spChannelLocationPath.text;
            spChannel.location = newLoc;
        }

        if((_roleA is ServiceProvider || _roleA is ExternalServiceProvider) && (_roleB is IdentityProvider || _roleB is ExternalIdentityProvider)){
            if(idpChannel.preferred){
                //if idpchannel is preferred, go through all the idp channels in a SP and deselect previously preferred
                for each(var conn:FederatedConnection in _roleA.federatedConnectionsA){
                    if(conn.channelA != null){
                        (conn.channelA as IdentityProviderChannel).preferred = false;
                    }
                }
                for each(conn in _roleA.federatedConnectionsB){
                    if(conn.channelB != null){
                        (conn.channelB as IdentityProviderChannel).preferred = false;
                    }
                }
            } else {
                if((_roleA.federatedConnectionsA == null || _roleA.federatedConnectionsA.length == 0)
                        && (_roleA.federatedConnectionsB == null || _roleA.federatedConnectionsB.length == 0)){
                    //in case this is the only IDP Channel for that SP, force it to be preferred
                    idpChannel.preferred = true;
                }
            }
            idpChannel.name = _roleA.name + "-to-" + _roleB.name;
            idpChannel.connectionA = federatedConnection;
            federatedConnection.channelA = idpChannel;

            spChannel.name = _roleB.name + "-to-" + _roleA.name;
            spChannel.connectionB = federatedConnection;
            federatedConnection.channelB = spChannel;
        } else if((_roleA is IdentityProvider || _roleA is ExternalIdentityProvider) && (_roleB is ServiceProvider || _roleB is ExternalServiceProvider)){
            if(idpChannel.preferred){
                //if idpchannel is preferred, go through all the idp channels in a SP and deselect previously preferred
                for each(conn in _roleB.federatedConnectionsA){
                    if(conn.channelA != null){
                        (conn.channelA as IdentityProviderChannel).preferred = false;
                    }
                }
                for each(conn in _roleB.federatedConnectionsB){
                    if(conn.channelB != null){
                        (conn.channelB as IdentityProviderChannel).preferred = false;
                    }
                }
            } else {
                if((_roleB.federatedConnectionsA == null || _roleB.federatedConnectionsA.length == 0)
                        && (_roleB.federatedConnectionsB == null || _roleB.federatedConnectionsB.length == 0)){
                    //in case this is the only IDP Channel for that SP, force it to be preferred
                    idpChannel.preferred = true;
                }
            }
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
            sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
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
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():FederatedConnectionCreateForm
    {
        return viewComponent as FederatedConnectionCreateForm;
    }


    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.nameValidator);
        if (!view.useInheritedSPSettings.selected) {
            _validators.push(view.idpChannelPortValidator);
            _validators.push(view.idpChannelDomainValidator);
            _validators.push(view.idpChannelContextValidator);
            _validators.push(view.idpChannelPathValidator);
        }
        if (!view.useInheritedIDPSettings.selected) {
            _validators.push(view.spChannelPortValidator);
            _validators.push(view.spChannelDomainValidator);
            _validators.push(view.spChannelContextValidator);
            _validators.push(view.spChannelPathValidator);
        }
    }


    override public function listNotificationInterests():Array {
        return [AccountLinkagePolicyListCommand.SUCCESS,
            IdentityMappingPoliciesListCommand.SUCCESS,
        SubjectNameIDPolicyListCommand.SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case AccountLinkagePolicyListCommand.SUCCESS:
                if (view != null && view.parent != null) {
                    _accountLinkagePolicies = projectProxy.accountLinkagePolicies;

                    var sp:ServiceProvider;
                    if (_roleA is ServiceProvider) {
                        sp = _roleA as ServiceProvider;
                    } else if (_roleB is ServiceProvider) {
                        sp = _roleB as ServiceProvider;
                    }

                    if (sp.accountLinkagePolicy != null) {
                        for (var i:int=0; i < view.accountLinkagePolicyCombo.dataProvider.length; i++) {
                            if (view.accountLinkagePolicyCombo.dataProvider[i].name == sp.accountLinkagePolicy.name) {
                                view.accountLinkagePolicyCombo.selectedIndex = i;
                                break;
                            }
                        }
                    } else {
                        for (var j:int=0; j < view.accountLinkagePolicyCombo.dataProvider.length; j++) {
                            if (view.accountLinkagePolicyCombo.dataProvider[j].linkEmitterType.toString() == AccountLinkEmitterType.ONE_TO_ONE.toString()) {
                                view.accountLinkagePolicyCombo.selectedIndex = j;
                                break;
                            }
                        }
                    }
                }
                break;
            case IdentityMappingPoliciesListCommand.SUCCESS:
                if (view != null && view.parent != null) {
                    _identityMappingPolicies = projectProxy.identityMappingPolicies;

                    var sp2:ServiceProvider;
                    if (_roleA is ServiceProvider) {
                        sp2 = _roleA as ServiceProvider;
                    } else if (_roleB is ServiceProvider) {
                        sp2 = _roleB as ServiceProvider;
                    }

                    if (sp2.identityMappingPolicy != null) {
                        for (var k:int=0; k < view.identityMappingPolicyCombo.dataProvider.length; k++) {
                            if (view.identityMappingPolicyCombo.dataProvider[k].name == sp2.identityMappingPolicy.name) {
                                view.identityMappingPolicyCombo.selectedIndex = k;
                                break;
                            }
                        }
                    } else {
                        for (var l:int=0; l < view.identityMappingPolicyCombo.dataProvider.length; l++) {
                            if (view.identityMappingPolicyCombo.dataProvider[l].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                                view.identityMappingPolicyCombo.selectedIndex = l;
                                break;
                            }
                        }
                    }
                }
                break;
            case SubjectNameIDPolicyListCommand.SUCCESS:
                if (view != null && view.parent != null) {
                    _subjectNameIdentifierPolicies = projectProxy.subjectNameIdentifierPolicies;

                    var sp3:IdentityProvider;
                    if (_roleA is IdentityProvider) {
                        sp3 = _roleA as IdentityProvider;
                    } else if (_roleB is IdentityProvider) {
                        sp3 = _roleB as IdentityProvider;
                    }

                    if (sp3.subjectNameIDPolicy != null) {
                        for (var k2:int=0; k2 < view.subjectNameIdPolicyCombo.dataProvider.length; k2++) {
                            if (view.subjectNameIdPolicyCombo.dataProvider[k2].name == sp3.subjectNameIDPolicy.name) {
                                view.subjectNameIdPolicyCombo.selectedIndex = k2;
                                break;
                            }
                        }
                    } else {
                        for (var l2:int=0; l2 < view.subjectNameIdPolicyCombo.dataProvider.length; l2++) {
                            if (view.subjectNameIdPolicyCombo.dataProvider[l2].mappingType.toString() == IdentityMappingType.REMOTE.toString()) {
                                view.subjectNameIdPolicyCombo.selectedIndex = l2;
                                break;
                            }
                        }
                    }
                }
                break;

                break;
            default:
                var cfc:CreateFederatedConnectionElementRequest = notification.getBody() as CreateFederatedConnectionElementRequest;
                _roleA = cfc.roleA;
                _roleB = cfc.roleB;
                //init channels
                reflectSPSettingsInIdpChannelTab();
                reflectIdpSettingsInSpChannelTab();
                bindForm();
                if (_roleA is ServiceProvider || _roleB is ServiceProvider) {
                    sendNotification(ApplicationFacade.LIST_ACCOUNT_LINKAGE_POLICIES);
                    sendNotification(ApplicationFacade.LIST_IDENTITY_MAPPING_POLICIES);
                }

                if (_roleA is IdentityProvider || _roleB is IdentityProvider) {
                    sendNotification(ApplicationFacade.LIST_NAMEID_POLICIES);
                }
                break;
        }
    }

    override public function bindForm():void {
        //set default name
        view.federatedConnectionName.text = _roleA.name.toLowerCase().replace(/\s+/g, "-") + "-" +
                _roleB.name.toLowerCase().replace(/\s+/g, "-") + "-fed";
    }


    private function setIdpChannelFields():void {
        if(view.useInheritedSPSettings.selected){
//            reflectSPSettingsInIdpChannelTab();
            view.samlProfileSSOCheck.enabled = false;
            view.samlProfileSLOCheck.enabled = false;

            view.samlBindingHttpPostCheck.enabled = false;
            view.samlBindingHttpRedirectCheck.enabled = false;
            view.samlBindingArtifactCheck.enabled = false;
            view.samlBindingSoapCheck.enabled = false;

            view.signAuthnRequestsCheck.enabled = false;
            view.wantAssertionSignedCheck.enabled = false;

            view.accountLinkagePolicyCombo.enabled = false;
            view.identityMappingPolicyCombo.enabled = false;
            view.subjectNameIdPolicyCombo.enabled = false;
            view.ignoreRequestedNameIDPolicy.enabled = false;

            view.idpChannelLocationProtocol.enabled = false;
            view.idpChannelLocationDomain.enabled = false;
            view.idpChannelLocationPort.enabled = false;
            view.idpChannelLocationContext.enabled = false;
            view.idpChannelLocationPath.enabled = false;
        } else {
            view.samlProfileSSOCheck.enabled = true;
            view.samlProfileSLOCheck.enabled = true;

            view.samlBindingHttpPostCheck.enabled = true;
            view.samlBindingHttpRedirectCheck.enabled = true;
            view.samlBindingArtifactCheck.enabled = true;
            view.samlBindingSoapCheck.enabled = true;

            view.signAuthnRequestsCheck.enabled = true;
            view.wantAssertionSignedCheck.enabled = true;

            view.accountLinkagePolicyCombo.enabled = true;
            view.identityMappingPolicyCombo.enabled = true;
            view.subjectNameIdPolicyCombo.enabled = true;
            view.ignoreRequestedNameIDPolicy.enabled = true;

            view.idpChannelLocationProtocol.enabled = true;
            view.idpChannelLocationDomain.enabled = true;
            view.idpChannelLocationPort.enabled = true;
            view.idpChannelLocationContext.enabled = true;
            view.idpChannelLocationPath.enabled = true;

        }
    }

    private function setSpChannelFields():void {
        if(view.useInheritedIDPSettings.selected){
//            reflectIdpSettingsInSpChannelTab();
            view.spChannelSamlProfileSSOCheck.enabled = false;
            view.spChannelSamlProfileSLOCheck.enabled = false;

            view.spChannelSamlBindingHttpPostCheck.enabled = false;
            view.spChannelSamlBindingHttpRedirectCheck.enabled = false;
            view.spChannelSamlBindingArtifactCheck.enabled = false;
            view.spChannelSamlBindingSoapCheck.enabled = false;

            view.wantAuthnRequestsSignedCheck.enabled = false;
            
            view.spChannelAuthContractCombo.enabled = false;
            view.subjectNameIdPolicyCombo.enabled = false;
            view.ignoreRequestedNameIDPolicy.enabled = false;

            view.spChannelAuthMechanism.enabled = false;
            view.spChannelAuthAssertionEmissionPolicyCombo.enabled = false;

            view.spChannelLocationProtocol.enabled = false;
            view.spChannelLocationDomain.enabled = false;
            view.spChannelLocationPort.enabled = false;
            view.spChannelLocationContext.enabled = false;
            view.spChannelLocationPath.enabled = false;
        } else {
            view.spChannelSamlProfileSSOCheck.enabled = true;
            view.spChannelSamlProfileSLOCheck.enabled = true;

            view.spChannelSamlBindingHttpPostCheck.enabled = true;
            view.spChannelSamlBindingHttpRedirectCheck.enabled = true;
            view.spChannelSamlBindingArtifactCheck.enabled = true;
            view.spChannelSamlBindingSoapCheck.enabled = true;

            view.wantAuthnRequestsSignedCheck.enabled = true;
            
            view.spChannelAuthContractCombo.enabled = true;
            view.subjectNameIdPolicyCombo.enabled = true;
            view.ignoreRequestedNameIDPolicy.enabled = true;
            view.spChannelAuthMechanism.enabled = false; //dont enable auth mechanism
            view.spChannelAuthAssertionEmissionPolicyCombo.enabled = true;

            view.spChannelLocationProtocol.enabled = true;
            view.spChannelLocationDomain.enabled = true;
            view.spChannelLocationPort.enabled = true;
            view.spChannelLocationContext.enabled = true;
            view.spChannelLocationPath.enabled = true;
        }
    }
    
    private function updateIdpChannelLocation():void {
        if (_spLocation != null) {
            for (var i:int = 0; i < view.idpChannelLocationProtocol.dataProvider.length; i++) {
                if (_spLocation.protocol == view.idpChannelLocationProtocol.dataProvider[i].data) {
                    view.idpChannelLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            view.idpChannelLocationDomain.text = _spLocation.host;
            view.idpChannelLocationPort.text = _spLocation.port.toString() != "0" ? _spLocation.port.toString() : "";
            view.idpChannelLocationContext.text = _spLocation.context;
            view.idpChannelLocationPath.text = _spLocation.uri;
            if (!view.useInheritedSPSettings.selected) {
                view.idpChannelLocationPath.text += "/" + _idpName.toUpperCase().replace(/\s+/g, "-");
            }
        }
    }

    private function updateSpChannelLocation():void {
        if (_idpLocation != null) {
            for (var i:int = 0; i < view.spChannelLocationProtocol.dataProvider.length; i++) {
                if (_idpLocation.protocol == view.spChannelLocationProtocol.dataProvider[i].data) {
                    view.spChannelLocationProtocol.selectedIndex = i;
                    break;
                }
            }
            view.spChannelLocationDomain.text = _idpLocation.host;
            view.spChannelLocationPort.text = _idpLocation.port.toString() != "0" ? _idpLocation.port.toString() : "";
            view.spChannelLocationContext.text = _idpLocation.context;
            view.spChannelLocationPath.text = _idpLocation.uri;
            if (!view.useInheritedIDPSettings.selected) {
                view.spChannelLocationPath.text += "/" + _spName.toUpperCase().replace(/\s+/g, "-");
            }
        }
    }
}
}