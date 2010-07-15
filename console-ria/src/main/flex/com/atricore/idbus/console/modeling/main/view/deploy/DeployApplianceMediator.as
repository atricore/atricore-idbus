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

package com.atricore.idbus.console.modeling.main.view.deploy {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.DeployIdentityApplianceCommand;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class DeployApplianceMediator extends FormMediator
{
    public static const NAME:String = "DeployApplianceMediator";
    public static const RUN:String = "DeployApplianceMediator.RUN";

    private var _proxy:ProjectProxy;

    private var _processingStarted:Boolean;

    public function DeployApplianceMediator(viewComp:DeployApplianceView) {
        super(NAME, viewComp);
        _proxy = ProjectProxy(facade.retrieveProxy(ProjectProxy.NAME));
        viewComp.selectedAppliance.text = _proxy.currentIdentityAppliance.idApplianceDefinition.name;
        viewComp.btnNext.addEventListener(MouseEvent.CLICK, handleNextClick);
        viewComp.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }
    
    override public function listNotificationInterests():Array {
        return [DeployIdentityApplianceCommand.SUCCESS,
                DeployIdentityApplianceCommand.FAILURE,
                ProcessingMediator.CREATED];
    }
    
    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ProcessingMediator.CREATED:
                sendNotification(ApplicationFacade.NOTE_DEPLOY_IDENTITY_APPLIANCE,
                        [_proxy.currentIdentityAppliance.id.toString(), view.startAppliance.selected]);
                break;
            case DeployIdentityApplianceCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE);
                var msg:String = "Appliance has been successfully deployed.";
                if (view.startAppliance.selected) {
                    msg =  "Appliance has been successfully deployed and started.";
                }
                sendNotification(ApplicationFacade.NOTE_SHOW_SUCCESS_MSG, msg);
                facade.removeMediator(DeployApplianceMediator.NAME);
                break;
            case DeployIdentityApplianceCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.NOTE_SHOW_ERROR_MSG,
                    "There was an error deploying appliance.");
                facade.removeMediator(DeployApplianceMediator.NAME);
                break;
        }

    }
    
    private function handleNextClick(event:MouseEvent):void {
        _processingStarted = true;
        closeWindow();
        sendNotification(ProcessingMediator.START, "Deploying appliance ...");
    }
    
    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
        if (!_processingStarted) {
            facade.removeMediator(DeployApplianceMediator.NAME);
        }
    }

    protected function get view():DeployApplianceView
    {
        return viewComponent as DeployApplianceView;
    }
}
}