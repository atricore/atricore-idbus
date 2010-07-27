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

package com.atricore.idbus.console.main.view.setup
{
import com.atricore.idbus.console.components.wizard.WizardEvent;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.controller.ApplicationStartUpCommand;
import com.atricore.idbus.console.main.controller.SetupServerCommand;
import com.atricore.idbus.console.main.model.ProfileProxy;
import com.atricore.idbus.console.services.dto.UserDTO;

import mx.events.CloseEvent;
import mx.utils.ObjectProxy;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class SetupWizardViewMediator extends IocMediator
{
    public static const RUN:String = "Note.start.RunServerSetup";

    private var _wizardDataModel:ObjectProxy = new ObjectProxy();
    private var _profileProxy : ProfileProxy;

    public function SetupWizardViewMediator(name:String = null, viewComp:SetupWizardView = null) {
        super(name, viewComp);

    }

    public function set profileProxy(value:ProfileProxy):void {
        _profileProxy = value;
    }

    public function get profileProxy():ProfileProxy {
        return _profileProxy;
    }

    override public function listNotificationInterests():Array {
        return [SetupServerCommand.FAILURE, SetupServerCommand.SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationStartUpCommand.FAILURE:
                view.dataModel = _wizardDataModel;
                view.addEventListener(WizardEvent.WIZARD_COMPLETE, onServerSetupWizardComplete);
                view.addEventListener(WizardEvent.WIZARD_CANCEL, onServerSetupWizardCancelled);
                break;
            case SetupServerCommand.SUCCESS :
                handleServerSetupSuccess();
                break;
            case SetupServerCommand.FAILURE :
                handleServerSetupFailure();
                break;
        }
    }

    private function onServerSetupWizardComplete(event:WizardEvent):void {
        // setup server using the supplied data
        //save user
        var user:UserDTO = _wizardDataModel.user;
        _profileProxy.user = user;

        view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));

        sendNotification(ApplicationFacade.SETUP_SERVER);
    }

    private function onServerSetupWizardCancelled(event:WizardEvent):void {

    }

    public function handleServerSetupSuccess():void {
        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                "The server has been setup successfully.");
    }

    public function handleServerSetupFailure():void {
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "There was an error initializing the server");
    }

    protected function get view():SetupWizardView
    {
        return viewComponent as SetupWizardView;
    }
}
}