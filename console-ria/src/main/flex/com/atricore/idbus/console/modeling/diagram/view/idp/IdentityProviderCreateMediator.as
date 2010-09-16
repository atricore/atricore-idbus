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

package com.atricore.idbus.console.modeling.diagram.view.idp {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.AuthenticationAssertionEmissionPolicy;
import com.atricore.idbus.console.services.dto.AuthenticationContract;
import com.atricore.idbus.console.services.dto.BasicAuthentication;
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class IdentityProviderCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newIdentityProvider:IdentityProvider;

    public function IdentityProviderCreateMediator(name:String = null, viewComp:IdentityProviderCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        initLocation();
    }

    private function resetForm():void {
        view.identityProviderName.text = "";
        view.identityProvDescription.text = "";
        view.idpLocationProtocol.selectedIndex = 0;
        view.idpLocationDomain.text = "";
        view.idpLocationPort.text = "";
        view.idpLocationContext.text = "";
        view.idpLocationPath.text = "";
        view.signAuthAssertionCheck.selected = true;
        view.encryptAuthAssertionCheck.selected = false;
        view.samlBindingHttpPostCheck.selected = true;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;
        view.samlProfileSSOCheck.selected = true;
        view.samlProfileSLOCheck.selected = true;
        view.authContract.selectedIndex = 0;
        view.authAssertionEmissionPolicy.selectedIndex = 0;

        /*for each(var liv:ListItemValueObject in  view.authMechanism.dataProvider){
            liv.isSelected = false;
        }*/

        FormUtility.clearValidationErrors(_validators);
    }

    public function initLocation():void {
        // set location
        var location:Location = _projectProxy.currentIdentityAppliance.idApplianceDefinition.location;
        for (var i:int = 0; i < view.idpLocationProtocol.dataProvider.length; i++) {
            if (location.protocol == view.idpLocationProtocol.dataProvider[i].data) {
                view.idpLocationProtocol.selectedIndex = i;
                break;
            }
        }
        view.idpLocationDomain.text = location.host;
        view.idpLocationPort.text = location.port.toString() != "0" ? location.port.toString() : "";
        view.idpLocationContext.text = location.context;
        view.idpLocationPath.text = location.uri;
    }

    override public function bindModel():void {

        var identityProvider:IdentityProvider = new IdentityProvider();

        identityProvider.name = view.identityProviderName.text;
        identityProvider.description = view.identityProvDescription.text;

        var loc:Location = new Location();
        loc.protocol = view.idpLocationProtocol.labelDisplay.text;
        loc.host = view.idpLocationDomain.text;
        loc.port = parseInt(view.idpLocationPort.text);
        loc.context = view.idpLocationContext.text;
        loc.uri = view.idpLocationPath.text;
        identityProvider.location = loc;

        identityProvider.signAuthenticationAssertions = view.signAuthAssertionCheck.selected;
        identityProvider.encryptAuthenticationAssertions = view.encryptAuthAssertionCheck.selected;

        identityProvider.activeBindings = new ArrayCollection();
        if (view.samlBindingHttpPostCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_POST);
        }
        if (view.samlBindingArtifactCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_ARTIFACT);
        }
        if (view.samlBindingHttpRedirectCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_HTTP_REDIRECT);
        }
        if (view.samlBindingSoapCheck.selected) {
            identityProvider.activeBindings.addItem(Binding.SAMLR2_SOAP);
        }

        identityProvider.activeProfiles = new ArrayCollection();
        if (view.samlProfileSSOCheck.selected) {
            identityProvider.activeProfiles.addItem(Profile.SSO);
        }
        if (view.samlProfileSLOCheck.selected) {
            identityProvider.activeProfiles.addItem(Profile.SSO_SLO);
        }

        if (identityProvider.authenticationMechanisms == null) {
            identityProvider.authenticationMechanisms = new ArrayCollection();
        }

        if (view.authMechanism.selectedItem.data == "basic") {
            var basicAuth:BasicAuthentication = new BasicAuthentication();
            basicAuth.name = identityProvider.name.replace(/\s+/g, "-").toLowerCase() + "-basic-authn";
            basicAuth.hashAlgorithm = "MD5";
            basicAuth.hashEncoding = "HEX";
            basicAuth.ignoreUsernameCase = false;
            identityProvider.authenticationMechanisms.addItem(basicAuth);
        }
/*
        for each(var liv:ListItemValueObject in  view.authMechanism.dataProvider){
            if(liv.isSelected){
                if(identityProvider.authenticationMechanisms == null){
                    identityProvider.authenticationMechanisms = new ArrayCollection();
                }
                switch(liv.name){
                    case "basic":
                        var basicAuth:BasicAuthentication = new BasicAuthentication();
                        basicAuth.name = identityProvider.name + "-basic-authn";
                        //TODO MAKE CONFIGURABLE
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
        if (view.authContract.selectedItem.data == "default") {
            var authContract:AuthenticationContract = new AuthenticationContract();
            authContract.name = "Default";
            identityProvider.authenticationContract = authContract;
        }

        if (view.authAssertionEmissionPolicy.selectedItem.data == "default") {
            var authAssertionEmissionPolicy:AuthenticationAssertionEmissionPolicy = new AuthenticationAssertionEmissionPolicy();
            authAssertionEmissionPolicy.name = "Default";
            identityProvider.emissionPolicy = authAssertionEmissionPolicy;
        }

        _newIdentityProvider = identityProvider;
    }

    private function handleIdentityProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newIdentityProvider);
            _projectProxy.currentIdentityApplianceElement = _newIdentityProvider;
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

    protected function get view():IdentityProviderCreateForm
    {
        return viewComponent as IdentityProviderCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.contextValidator);
        _validators.push(view.pathValidator);
    }


    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {

        super.handleNotification(notification);


    }
}
}