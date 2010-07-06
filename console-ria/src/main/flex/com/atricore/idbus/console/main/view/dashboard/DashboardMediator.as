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

package com.atricore.idbus.console.main.view.dashboard
{
import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Menu;
import mx.events.FlexEvent;
import mx.events.MenuEvent;

import com.atricore.idbus.console.main.controller.LoginCommand;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class DashboardMediator extends Mediator
{
    public static const NAME:String = "DashboardViewMediator";

    private var mainMenu:Menu;

    public function DashboardMediator(viewComp:DashboardView) {
        super(NAME, viewComp);

        Menu.createMenu(null, viewComp.mainDashboardMenuData, false);
        mainMenu.addEventListener(MenuEvent.ITEM_CLICK, handleMenuItemClick);

        viewComp.btnIdentityApplianceDetail.addEventListener(MouseEvent.CLICK, handleIdentityApplianceButton);
        viewComp.btnManageAppliances.addEventListener(MouseEvent.CLICK, handleManageAppliancesButton);
        viewComp.btnProjectedIdentityApplianceDetail.addEventListener(MouseEvent.CLICK, handleProjectedIdentityApplianceButton);
        viewComp.btnCreateNewAppliance.addEventListener(MouseEvent.CLICK, handleCreateNewApplianceButton);
        viewComp.btnGoToDashboard.addEventListener(MouseEvent.CLICK, handleGoToDashboardButton)
        //viewComp.startedAppliancesRepeater.dataProvider = _startedAppliances;
        //viewComp.projectedAppliancesRepeater.dataProvider = _projectedAppliances;
        viewComp.addEventListener(FlexEvent.SHOW, handleShowDashboard);


        handleShowDashboard(null);
    }


    private function handleMenuItemClick():void {

    }

    private function handleGoToDashboardButton():void {
    }

    private function handleCreateNewApplianceButton():void {
    }

    override public function listNotificationInterests():Array {
        return [LoginCommand.FAILURE, LoginCommand.EMAIL_SUCCESS];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case LoginCommand.EMAIL_SUCCESS :
                //handleEmailSuccess();
                break;
            case LoginCommand.FAILURE :
                //handleLoginFailure();
                break;
        }
    }

    public function handleShowDashboard(event:Event):void {
        mainMenu.show();
    }


    private function handleIdentityApplianceButton(oEvent:Object):void {
    }

    private function handleProjectedIdentityApplianceButton(oEvent:Object):void {
    }

    private function handleManageAppliancesButton(oEvent:Object):void {
    }

    protected function get view():DashboardView
    {
        return viewComponent as DashboardView;
    }


}
}