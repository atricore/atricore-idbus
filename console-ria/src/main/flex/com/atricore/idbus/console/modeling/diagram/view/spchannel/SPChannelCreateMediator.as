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

package com.atricore.idbus.console.modeling.diagram.view.spchannel {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.services.dto.BindingDTO;
import com.atricore.idbus.console.services.dto.IdentityProviderDTO;
import com.atricore.idbus.console.services.dto.LocationDTO;
import com.atricore.idbus.console.services.dto.ProfileDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderChannelDTO;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class SPChannelCreateMediator extends IocFormMediator {

    private var _proxy:ProjectProxy;
    private var _newSpChannel:ServiceProviderChannelDTO;

    public function SPChannelCreateMediator(name : String = null, viewComp:SPChannelCreateForm = null) {
        super(name, viewComp);

    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleSpChannelSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleSpChannelSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    override public function bindModel():void {

        var spChannel:ServiceProviderChannelDTO = new ServiceProviderChannelDTO();

        spChannel.name = view.serviceProvChannelName.text;
        spChannel.description = view.serviceProvChannelDescription.text;

        var loc:LocationDTO = new LocationDTO();
        loc.protocol = view.spChannelLocationProtocol.selectedLabel;
        loc.host = view.spChannelLocationDomain.text;
        loc.port = parseInt(view.spChannelLocationPort.text);
        loc.context = view.spChannelLocationContext.text;
        loc.uri = view.spChannelLocationPath.text;
        spChannel.location = loc;

//        spChannel.signAuthenticationAssertions = view.signAuthAssertionCheck.selected;
//        spChannel.encryptAuthenticationAssertions = view.encryptAuthAssertionCheck.selected;

        spChannel.activeBindings = new ArrayCollection();
        if(view.samlBindingHttpPostCheck.selected){
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
        }
        if(view.samlBindingArtifactCheck.selected){
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
        }
        if(view.samlBindingHttpRedirectCheck.selected){
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
        }
        if(view.samlBindingSoapCheck.selected){
            spChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
        }        

        spChannel.activeProfiles = new ArrayCollection();
        if(view.samlProfileSSOCheck.selected){
            spChannel.activeProfiles.addItem(ProfileDTO.SSO);
        }
        if(view.samlProfileSLOCheck.selected){
            spChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
        }
        
        // TODO save remaining fields, calling appropriate lookup methods
        //userInformationLookup
        //authenticationContract
        //authenticationMechanism
        //authenticationAssertionEmissionPolicy

        _newSpChannel = spChannel;
    }

    private function handleSpChannelSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            var idp:IdentityProviderDTO = _proxy.currentIdentityApplianceElementOwner as IdentityProviderDTO;
            if(idp.channels == null){
                idp.channels = new ArrayCollection();
            }
            idp.channels.addItem(_newSpChannel);
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
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():SPChannelCreateForm
    {
        return viewComponent as SPChannelCreateForm;
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