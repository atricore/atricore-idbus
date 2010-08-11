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

package com.atricore.idbus.console.modeling.diagram.view.sp {
import com.atricore.idbus.console.components.ListItemValueObject;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.BindingDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderChannelDTO;
import com.atricore.idbus.console.services.dto.LocationDTO;
import com.atricore.idbus.console.services.dto.ProfileDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class ServiceProviderCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newServiceProvider:ServiceProviderDTO;

    public function ServiceProviderCreateMediator(name : String = null, viewComp:ServiceProviderCreateForm = null) {
        super(name, viewComp);

    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent()) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleServiceProviderSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleServiceProviderSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    private function resetForm():void {
        view.serviceProvName.text = "";
        view.serviceProvDescription.text = "";
        view.spLocationProtocol.selectedIndex = 0;
        view.spLocationDomain.text = "";
        view.spLocationPort.text = "";
        view.spLocationContext.text = "";
        view.spLocationPath.text = "";
        view.signAuthRequestCheck.selected = false;
        view.encryptAuthRequestCheck.selected = false;
        view.samlBindingHttpPostCheck.selected = false;
        view.samlBindingArtifactCheck.selected = false;
        view.samlBindingHttpRedirectCheck.selected = false;
        view.samlBindingSoapCheck.selected = false;
        view.samlProfileSSOCheck.selected = false;
        view.samlProfileSLOCheck.selected = false;

        for each(var liv:ListItemValueObject in  view.authMechanismCombo.dataProvider){
            liv.isSelected = false;
        }        

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var serviceProvider:ServiceProviderDTO = new ServiceProviderDTO();

        serviceProvider.name = view.serviceProvName.text;
        serviceProvider.description = view.serviceProvDescription.text;

        var loc:LocationDTO = new LocationDTO();
        loc.protocol = view.spLocationProtocol.labelDisplay.text;
        loc.host = view.spLocationDomain.text;
        loc.port = parseInt(view.spLocationPort.text);
        loc.context = view.spLocationContext.text;
        loc.uri = view.spLocationPath.text;
        serviceProvider.location = loc;

//        serviceProvider.signAuthenticationRequest = view.signAuthRequestCheck.selected;
//        serviceProvider.encryptAuthenticationRequest = view.encryptAuthRequestCheck.selected;

        var idpChannel:IdentityProviderChannelDTO = new IdentityProviderChannelDTO();
        idpChannel.name = serviceProvider.name + " to idp default channel";
        var idpChannelLoc:LocationDTO = new LocationDTO();
        idpChannelLoc.protocol = view.spLocationProtocol.labelDisplay.text;
        idpChannelLoc.host = view.spLocationDomain.text;
        idpChannelLoc.port = parseInt(view.spLocationPort.text);
        idpChannelLoc.context = view.spLocationContext.text;
        idpChannelLoc.uri = view.spLocationPath.text + "/SAML2";

        idpChannel.location = idpChannelLoc;
        
        idpChannel.activeBindings = new ArrayCollection();
        if(view.samlBindingHttpPostCheck.selected){
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
        }
        if(view.samlBindingArtifactCheck.selected){
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
        }
        if(view.samlBindingHttpRedirectCheck.selected){
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
        }
        if(view.samlBindingSoapCheck.selected){
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
        }

        idpChannel.activeProfiles = new ArrayCollection();
        if(view.samlProfileSSOCheck.selected){
            idpChannel.activeProfiles.addItem(ProfileDTO.SSO);
        }
        if(view.samlProfileSLOCheck.selected){
            idpChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
        }
        
        // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
        //userInformationLookup
        //authenticationContract
        //authenticationMechanism
        //authenticationAssertionEmissionPolicy
        serviceProvider.defaultChannel = idpChannel;

        _newServiceProvider = serviceProvider;
    }

    private function handleServiceProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newServiceProvider);
            _projectProxy.currentIdentityApplianceElement = _newServiceProvider;
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

    protected function get view():ServiceProviderCreateForm
    {
        return viewComponent as ServiceProviderCreateForm;
    }



    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
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