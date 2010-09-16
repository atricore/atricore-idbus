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

package com.atricore.idbus.console.main
{
import com.atricore.idbus.console.main.controller.ApplicationStartUpCommand;
import com.atricore.idbus.console.main.controller.LoginCommand;
import com.atricore.idbus.console.main.controller.SetupServerCommand;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.main.view.setup.SetupWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;

import flash.events.Event;

import mx.events.FlexEvent;

import mx.events.MenuEvent;
import mx.events.StateChangeEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.ButtonBar;
import spark.events.IndexChangeEvent;

public class ApplicationMediator extends IocMediator {
    // Canonical name of the Mediator
    public static const REGISTER_HEAD:String = "User Registration";

    public static const MODELER_VIEW_INDEX:int = 0;
    public static const LIFECYCLE_VIEW_INDEX:int = 1;
    public static const ACCOUNT_VIEW_INDEX:int = 4;

    private var _secureContextProxy:SecureContextProxy;
    private var _popupManager:ConsolePopUpManager;
    private var _modelerMediator:IIocMediator;
    private var _lifecycleViewMediator:IIocMediator;
    private var _accountManagementMediator:IIocMediator;

    public function ApplicationMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {

        super(p_mediatorName, p_viewComponent);

    }

    public function get modelerMediator():IIocMediator {
        return _modelerMediator;
    }

    public function set modelerMediator(value:IIocMediator):void {
        _modelerMediator = value;
    }

    public function get lifecycleViewMediator():IIocMediator {
        return _lifecycleViewMediator;
    }

    public function set lifecycleViewMediator(value:IIocMediator):void {
        _lifecycleViewMediator = value;
    }

    public function get accountManagementMediator():IIocMediator {
        return _accountManagementMediator;
    }

    public function set accountManagementMediator(value:IIocMediator):void {
        _accountManagementMediator = value;
    }

    public function get popupManager():ConsolePopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:ConsolePopUpManager):void {
        _popupManager = value;
    }

    public function set secureContextProxy(value:SecureContextProxy):void {
        _secureContextProxy = value;
    }

    public function get secureContextProxy():SecureContextProxy {
        return _secureContextProxy;

    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
            app.stackButtonBar.removeEventListener(IndexChangeEvent.CHANGE, handleStackChange);
            app.removeEventListener(FlexEvent.SHOW, handleShowConsole);
        }

        super.setViewComponent(p_viewComponent);

        init();
    }


    public function init():void {
        popupManager.init(iocFacade, app);
        app.addEventListener(FlexEvent.SHOW, handleShowConsole);
    }

    public function handleStackChange(event:IndexChangeEvent):void {
        var selectedIndex = (event.currentTarget as ButtonBar).selectedIndex;
        if (selectedIndex == MODELER_VIEW_INDEX) {
            sendNotification(ApplicationFacade.MODELER_VIEW_SELECTED);
        } else if (selectedIndex == LIFECYCLE_VIEW_INDEX) {
            sendNotification(ApplicationFacade.LIFECYCLE_VIEW_SELECTED);
        } else if (selectedIndex == ACCOUNT_VIEW_INDEX) {
            sendNotification(ApplicationFacade.ACCOUNT_VIEW_SELECTED);
        }
    }

    public function handleShowConsole(event:Event):void {

    }

    private function handleUserMenuAction(event:MenuEvent):void {
        if (event.index == 0) {
            sendNotification(ApplicationFacade.DISPLAY_CHANGE_PASSWORD);
        } else
        if (event.index == 1) {
            app.currentState = "splash";
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.SHOW_ERROR_MSG,
            ApplicationFacade.SHOW_SUCCESS_MSG,
            ApplicationFacade.CLEAR_MSG,
            ApplicationStartUpCommand.SUCCESS,
            ApplicationStartUpCommand.FAILURE,
            SetupServerCommand.SUCCESS,
            SetupServerCommand.FAILURE,
            LoginCommand.SUCCESS,
            SetupWizardViewMediator.RUN,
            SimpleSSOWizardViewMediator.RUN,
            IdentityApplianceWizardViewMediator.RUN,
            ApplicationFacade.DISPLAY_APPLIANCE_MODELER,
            ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE,
            ApplicationFacade.DISPLAY_CHANGE_PASSWORD,
            ProcessingMediator.START,
            ProcessingMediator.STOP
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationStartUpCommand.SUCCESS:
                //TODO: Show login box
                break;
            case ApplicationStartUpCommand.FAILURE:
                //TODO: popupManager.showSetupWizardWindow(notification);
                break;
            case SimpleSSOWizardViewMediator.RUN:
                popupManager.showSimpleSSOWizardWindow(notification);
                break;
            case IdentityApplianceWizardViewMediator.RUN:
                popupManager.showCreateIdentityApplianceWindow(notification);
                break;
            case SetupServerCommand.SUCCESS:
                break;
            case SetupServerCommand.FAILURE:
                break;
            case LoginCommand.SUCCESS:
                app.addEventListener(StateChangeEvent.CURRENT_STATE_CHANGE, switchedMode);
                app.currentState = "operation";
                break;
            case ApplicationFacade.SHOW_ERROR_MSG :
                app.messageBox.showFailureMessage(notification.getBody() as String);
                break;
            case ApplicationFacade.SHOW_SUCCESS_MSG :
                app.messageBox.showSuccessMessage(notification.getBody() as String);
                break;
            case ApplicationFacade.CLEAR_MSG :
                app.messageBox.clearAndHide();
                break;
            case ApplicationFacade.DISPLAY_APPLIANCE_MODELER:
                app.stackButtonBar.selectedIndex = MODELER_VIEW_INDEX;
                app.modulesViewStack.selectedIndex = MODELER_VIEW_INDEX;
                sendNotification(ApplicationFacade.MODELER_VIEW_SELECTED);
                break;
            case ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE:
                app.stackButtonBar.selectedIndex = LIFECYCLE_VIEW_INDEX;
                app.modulesViewStack.selectedIndex = LIFECYCLE_VIEW_INDEX;
                sendNotification(ApplicationFacade.LIFECYCLE_VIEW_SELECTED);
                break;
            case ApplicationFacade.DISPLAY_CHANGE_PASSWORD:
                popupManager.showChangePasswordWindow(notification);
                break;
            case ProcessingMediator.START:
                popupManager.showProcessingWindow(notification);
                break;
            case ProcessingMediator.STOP:
                popupManager.hideProcessingWindow(notification);
                break;
        }
    }

    private function switchedMode(event:StateChangeEvent):void {
        if (event.newState == "operation") {
            login();
        } else
        if (event.newState == "splash") {
            logout();
        }
    }

    public function login():void {
        modelerMediator.setViewComponent(app.modelerView);
        lifecycleViewMediator.setViewComponent(app.lifecycleView);
        accountManagementMediator.setViewComponent(app.accountManagementView);

        app.stackButtonBar.addEventListener(IndexChangeEvent.CHANGE, handleStackChange);
        app.stackButtonBar.selectedIndex = 0;
        if (_secureContextProxy.currentUser != null)
            app.userActionMenuBar.dataProvider.source[0].@label = _secureContextProxy.currentUser.commonName;
        app.userActionMenuBar.addEventListener(MenuEvent.ITEM_CLICK, handleUserMenuAction)
        sendNotification(ApplicationFacade.CLEAR_MSG);
    }

    public function logout():void {
        _secureContextProxy.logout();
        // TODO: cleanup proxyies holding for instance appliance state
    }

    public function get app():AtricoreConsole {
        return getViewComponent() as AtricoreConsole;
    }

}
}
