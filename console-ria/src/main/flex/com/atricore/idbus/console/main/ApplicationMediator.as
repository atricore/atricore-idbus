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
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.branding.AtricoreConsolePreloader;
import com.atricore.idbus.console.liveupdate.main.model.LiveUpdateProxy;
import com.atricore.idbus.console.main.controller.ApplicationStartUpCommand;
import com.atricore.idbus.console.main.controller.LoginCommand;
import com.atricore.idbus.console.main.controller.NotFirstRunCommand;
import com.atricore.idbus.console.main.controller.SetupServerCommand;
import com.atricore.idbus.console.main.model.KeystoreProxy;
import com.atricore.idbus.console.main.model.ProfileProxy;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.model.SecureContextProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.main.view.setup.SetupWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;

import flash.events.Event;

import mx.controls.Alert;
import mx.controls.MenuBar;
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
    public static const ACCOUNT_VIEW_INDEX:int = 2;
    public static const UPDATE_VIEW_INDEX:int = 3;
	public static const LICENSE_VIEW_INDEX:int = 4;
    

    public var userProfileIcon:Class = EmbeddedIcons.userProfileIcon;

    private var _secureContextProxy:SecureContextProxy;
    private var _projectProxy:ProjectProxy;
    private var _keystoreProxy:KeystoreProxy;
    private var _profileProxy:ProfileProxy;
    private var _accountManagementProxy:AccountManagementProxy;
    private var _liveUpdateProxy:LiveUpdateProxy;

    private var _popupManager:ConsolePopUpManager;
    private var _modelerMediator:IIocMediator;
    private var _lifecycleViewMediator:IIocMediator;
    private var _accountManagementMediator:IIocMediator;
    private var _liveUpdateMediator:IIocMediator;
    private var _licenseMediator:IIocMediator;

    private var _userActionMenuBar:MenuBar;

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

    public function get liveUpdateMediator():IIocMediator {
        return _liveUpdateMediator;
    }

    public function set liveUpdateMediator(value:IIocMediator):void {
        _liveUpdateMediator = value;
    }

    public function get licenseMediator():IIocMediator {
        return _licenseMediator;
    }

    public function set licenseMediator(value:IIocMediator):void {
        _licenseMediator = value;
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

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function get keystoreProxy():KeystoreProxy {
        return _keystoreProxy;
    }

    public function set keystoreProxy(value:KeystoreProxy):void {
        _keystoreProxy = value;
    }

    public function get profileProxy():ProfileProxy {
        return _profileProxy;
    }

    public function set profileProxy(value:ProfileProxy):void {
        _profileProxy = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    public function get liveUpdateProxy():LiveUpdateProxy {
        return _liveUpdateProxy;
    }

    public function set liveUpdateProxy(value:LiveUpdateProxy):void {
        _liveUpdateProxy = value;
    }

    public function get userActionMenuBar():MenuBar {
        return _userActionMenuBar;
    }

    public function set userActionMenuBar(value:MenuBar):void {
        _userActionMenuBar = value;
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
        sendNotification(ApplicationFacade.NOT_FIRST_RUN);
        popupManager.init(iocFacade, app);
        app.addEventListener(FlexEvent.SHOW, handleShowConsole);
        createHeading();
    }

    private function createHeading():void {
        //app.brandedHeading.addChild(app.messageBox);
        //app.brandedHeading.addChild(app.userActionMenuBar);
    }

    public function handleStackChange(event:IndexChangeEvent):void {
        var selectedIndex:int = (event.currentTarget as ButtonBar).selectedIndex;
        if (event.oldIndex == MODELER_VIEW_INDEX) {
            sendNotification(ApplicationFacade.AUTOSAVE_IDENTITY_APPLIANCE, selectedIndex);
        } else if (selectedIndex == MODELER_VIEW_INDEX) {
            app.modulesViewStack.selectedIndex = MODELER_VIEW_INDEX;
            sendNotification(ApplicationFacade.MODELER_VIEW_SELECTED);
        } else if (selectedIndex == LIFECYCLE_VIEW_INDEX) {
            app.modulesViewStack.selectedIndex = LIFECYCLE_VIEW_INDEX;
            sendNotification(ApplicationFacade.LIFECYCLE_VIEW_SELECTED);
        } else if (selectedIndex == ACCOUNT_VIEW_INDEX) {
            app.modulesViewStack.selectedIndex = ACCOUNT_VIEW_INDEX;
            sendNotification(ApplicationFacade.ACCOUNT_VIEW_SELECTED);
        } else if (selectedIndex == LICENSE_VIEW_INDEX) {
            app.modulesViewStack.selectedIndex = LICENSE_VIEW_INDEX;
            sendNotification(ApplicationFacade.LICENSE_VIEW_SELECTED);
        } else if (selectedIndex == UPDATE_VIEW_INDEX) {
            app.modulesViewStack.selectedIndex = UPDATE_VIEW_INDEX;
            sendNotification(ApplicationFacade.UPDATE_VIEW_SELECTED);
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
            //            ApplicationFacade.SHOW_SUCCESS_MSG,
            ApplicationFacade.CLEAR_MSG,
            ApplicationStartUpCommand.SUCCESS,
            ApplicationStartUpCommand.FAILURE,
            SetupServerCommand.SUCCESS,
            SetupServerCommand.FAILURE,
            NotFirstRunCommand.SUCCESS,
            NotFirstRunCommand.FAILURE,
            LoginCommand.SUCCESS,
            SetupWizardViewMediator.RUN,
            SimpleSSOWizardViewMediator.RUN,
            IdentityApplianceWizardViewMediator.RUN,
            ApplicationFacade.DISPLAY_VIEW,
            ApplicationFacade.DISPLAY_APPLIANCE_MODELER,
            ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE,
            ApplicationFacade.DISPLAY_APPLIANCE_ACCOUNT,
            ApplicationFacade.DISPLAY_LIVE_UPDATE,
            ApplicationFacade.DISPLAY_LICENSING,
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
            case NotFirstRunCommand.SUCCESS:
                break;
            case NotFirstRunCommand.FAILURE:
                app.preloader = new AtricoreConsolePreloader();
                app.preloader.visible = true;
                break;
            case LoginCommand.SUCCESS:
                app.addEventListener(StateChangeEvent.CURRENT_STATE_CHANGE, switchedMode);
                app.currentState = "operation";
                break;
            case ApplicationFacade.SHOW_ERROR_MSG :
                //              app.messageBox.showFailureMessage(notification.getBody() as String);
                var errString:String = notification.getBody() as String;
                Alert.show(errString, "Error");
                break;
            //            case ApplicationFacade.SHOW_SUCCESS_MSG :
            //                app.messageBox.showSuccessMessage(notification.getBody() as String);
            //                break;
            case ApplicationFacade.CLEAR_MSG :
                //                app.messageBox.clearAndHide();
                break;
            case ApplicationFacade.DISPLAY_VIEW:
                var viewIndex:int = notification.getBody() as int;
                if (viewIndex == MODELER_VIEW_INDEX) {
                    sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                } else if (viewIndex == LIFECYCLE_VIEW_INDEX) {
                    sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE);
                } else if (viewIndex == ACCOUNT_VIEW_INDEX) {
                    sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_ACCOUNT);
                } else if (viewIndex == UPDATE_VIEW_INDEX) {
                    sendNotification(ApplicationFacade.DISPLAY_LIVE_UPDATE);
                } else if (viewIndex == LICENSE_VIEW_INDEX) {
                    sendNotification(ApplicationFacade.DISPLAY_LICENSING);
                }
                break;
            case ApplicationFacade.DISPLAY_APPLIANCE_MODELER:
                app.stackButtonBar.selectedIndex = MODELER_VIEW_INDEX;
                if (app.modulesViewStack.selectedIndex != MODELER_VIEW_INDEX) {
                    app.modulesViewStack.selectedIndex = MODELER_VIEW_INDEX;
                    sendNotification(ApplicationFacade.MODELER_VIEW_SELECTED);
                }
                break;
            case ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE:
                app.stackButtonBar.selectedIndex = LIFECYCLE_VIEW_INDEX;
                if (app.modulesViewStack.selectedIndex != LIFECYCLE_VIEW_INDEX) {
                    app.modulesViewStack.selectedIndex = LIFECYCLE_VIEW_INDEX;
                    sendNotification(ApplicationFacade.LIFECYCLE_VIEW_SELECTED);
                }
                break;
            case ApplicationFacade.DISPLAY_APPLIANCE_ACCOUNT:
                app.stackButtonBar.selectedIndex = ACCOUNT_VIEW_INDEX;
                if (app.modulesViewStack.selectedIndex != ACCOUNT_VIEW_INDEX) {
                    app.modulesViewStack.selectedIndex = ACCOUNT_VIEW_INDEX;
                    sendNotification(ApplicationFacade.ACCOUNT_VIEW_SELECTED);
                }
                break;
            case ApplicationFacade.DISPLAY_LIVE_UPDATE:
                app.stackButtonBar.selectedIndex = UPDATE_VIEW_INDEX;
                if (app.modulesViewStack.selectedIndex != UPDATE_VIEW_INDEX) {
                    app.modulesViewStack.selectedIndex = UPDATE_VIEW_INDEX;
                    sendNotification(ApplicationFacade.UPDATE_VIEW_SELECTED);
                }
                break;
            case ApplicationFacade.DISPLAY_LICENSING:
                app.stackButtonBar.selectedIndex = LICENSE_VIEW_INDEX;
                if (app.modulesViewStack.selectedIndex != LICENSE_VIEW_INDEX) {
                    app.modulesViewStack.selectedIndex = LICENSE_VIEW_INDEX;
                    sendNotification(ApplicationFacade.LICENSE_VIEW_SELECTED);
                }
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
        liveUpdateMediator.setViewComponent(app.liveUpdateView);
        licenseMediator.setViewComponent(app.licenseView);

        app.stackButtonBar.addEventListener(IndexChangeEvent.CHANGE, handleStackChange);
        app.stackButtonBar.selectedIndex = 0;
        if (_secureContextProxy.currentUser != null) {
            app.userActionMenuBar.dataProvider.source[0].@label = _secureContextProxy.currentUser.commonName;
        }
        app.userActionMenuBar.addEventListener(MenuEvent.ITEM_CLICK, handleUserMenuAction)
        sendNotification(ApplicationFacade.CLEAR_MSG);
        sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
    }

    public function logout():void {
        secureContextProxy.dispose();
        projectProxy.dispose();
        keystoreProxy.dispose();
        profileProxy.dispose();
        accountManagementProxy.dispose();

        sendNotification(ApplicationFacade.LOGOUT);
    }

    public function get app():AtricoreConsole {
        return getViewComponent() as AtricoreConsole;
    }
}
}
