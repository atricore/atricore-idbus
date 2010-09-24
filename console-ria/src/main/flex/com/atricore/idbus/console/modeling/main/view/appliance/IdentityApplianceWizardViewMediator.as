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

package com.atricore.idbus.console.modeling.main.view.appliance {
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceCreateCommand;
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import flash.events.Event;

import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class IdentityApplianceWizardViewMediator extends IocMediator
{
    public static const RUN:String = "IdentityApplianceWizardViewMediator.RUN";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();

    private var _processingStarted:Boolean;

    public function IdentityApplianceWizardViewMediator(name:String = null, viewComp:IdentityApplianceWizardView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.addEventListener(WizardEvent.WIZARD_COMPLETE, onIdentityApplianceWizardComplete);
            view.addEventListener(WizardEvent.WIZARD_CANCEL, onIdentityApplianceWizardCancelled);
            view.addEventListener(CloseEvent.CLOSE, handleClose);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.dataModel = _wizardDataModel;
        view.addEventListener(WizardEvent.WIZARD_COMPLETE, onIdentityApplianceWizardComplete);
        view.addEventListener(WizardEvent.WIZARD_CANCEL, onIdentityApplianceWizardCancelled);
        view.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function listNotificationInterests():Array {
        return [IdentityApplianceCreateCommand.SUCCESS,
            IdentityApplianceCreateCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case IdentityApplianceCreateCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
//                sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
//                        "The appliance has been successfully created.");
                break;
            case IdentityApplianceCreateCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error creating appliance.");
                break;
        }

    }

    private function onIdentityApplianceWizardComplete(event:WizardEvent):void {
        _processingStarted = true;
        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        sendNotification(ProcessingMediator.START, "Saving Identity Appliance...");
        var identityAppliance:IdentityAppliance = _wizardDataModel.applianceData;
        sendNotification(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, identityAppliance);
    }

    private function onIdentityApplianceWizardCancelled(event:WizardEvent):void {
        //closeWizard();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():IdentityApplianceWizardView {
        return viewComponent as IdentityApplianceWizardView;
    }
}
}