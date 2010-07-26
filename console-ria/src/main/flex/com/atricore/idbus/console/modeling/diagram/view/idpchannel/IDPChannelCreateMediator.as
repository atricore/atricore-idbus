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

package com.atricore.idbus.console.modeling.diagram.view.idpchannel {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
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

public class IDPChannelCreateMediator extends IocFormMediator {
    private var _projectProxy:ProjectProxy;
    private var _newIdpChannel:IdentityProviderChannelDTO;


    public function IDPChannelCreateMediator(name:String = null, viewComp:IDPChannelCreateForm = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleIdpChannelSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleIdpChannelSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }


    override public function bindModel():void {
        var idpChannel:IdentityProviderChannelDTO = new IdentityProviderChannelDTO();

        idpChannel.name = view.identityProvChannelName.text;
        idpChannel.description = view.identityProvChannelDescription.text;

        var loc:LocationDTO = new LocationDTO();
        loc.protocol = view.idpChannelLocationProtocol.selectedLabel;
        loc.host = view.idpChannelLocationDomain.text;
        loc.port = parseInt(view.idpChannelLocationPort.text);
        loc.context = view.idpChannelLocationContext.text;
        loc.uri = view.idpChannelLocationPath.text;
        idpChannel.location = loc;

        //        serviceProvider.signAuthenticationRequest = view.signAuthRequestCheck.selected;
        //        serviceProvider.encryptAuthenticationRequest = view.encryptAuthRequestCheck.selected;

        idpChannel.activeBindings = new ArrayCollection();
        if (view.samlBindingHttpPostCheck.selected) {
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_POST);
        }
        if (view.samlBindingArtifactCheck.selected) {
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_ARTIFACT);
        }
        if (view.samlBindingHttpRedirectCheck.selected) {
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_HTTP_REDIRECT);
        }
        if (view.samlBindingSoapCheck.selected) {
            idpChannel.activeBindings.addItem(BindingDTO.SAMLR2_SOAP);
        }

        idpChannel.activeProfiles = new ArrayCollection();
        if (view.samlProfileSSOCheck.selected) {
            idpChannel.activeProfiles.addItem(ProfileDTO.SSO);
        }
        if (view.samlProfileSLOCheck.selected) {
            idpChannel.activeProfiles.addItem(ProfileDTO.SSO_SLO);
        }

        // TODO save remaining fields to defaultChannel, calling appropriate lookup methods
        //userInformationLookup
        //authenticationContract
        //authenticationMechanism
        //authenticationAssertionEmissionPolicy

        _newIdpChannel = idpChannel;
    }

    private function handleIdpChannelSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            var sp:ServiceProviderDTO = _projectProxy.currentIdentityApplianceElementOwner as ServiceProviderDTO;
            if (sp.channels == null) {
                sp.channels = new ArrayCollection();
            }
            sp.channels.addItem(_newIdpChannel);
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

    protected function get view():IDPChannelCreateForm
    {
        return viewComponent as IDPChannelCreateForm;
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