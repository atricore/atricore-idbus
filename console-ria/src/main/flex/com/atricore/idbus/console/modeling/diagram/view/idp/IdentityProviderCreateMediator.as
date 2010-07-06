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
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormMediator;
import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityProvider;
import org.puremvc.as3.interfaces.INotification;

public class IdentityProviderCreateMediator extends FormMediator {
    public static const NAME:String = "com.atricore.idbus.console.modeling.diagram.view.idp.IdentityProviderCreateMediator";

    private var _proxy:ProjectProxy;
    private var _newIdentityProvider:IdentityProvider;

    public function IdentityProviderCreateMediator(viewComp:IdentityProviderCreateForm) {
        super(NAME, viewComp);
        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        viewComp.btnOk.addEventListener(MouseEvent.CLICK, handleIdentityProviderSave);
        viewComp.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

    }

    override public function bindModel():void {

        var identityProvider:IdentityProvider = new IdentityProvider();

        identityProvider.name = view.identityProviderName.text;
        _newIdentityProvider = identityProvider;
    }

    private function handleIdentityProviderSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _proxy.currentIdentityAppliance.idApplianceDefinition.providers.addItem(_newIdentityProvider);
            _proxy.currentIdentityApplianceElement = _newIdentityProvider;
            sendNotification(ApplicationFacade.NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
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

    protected function get view():IdentityProviderCreateForm
    {
        return viewComponent as IdentityProviderCreateForm;
    }



    override public function registerValidators():void {
        // TODO: wire validators
    }


    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {

        super.handleNotification(notification);


    }
}
}