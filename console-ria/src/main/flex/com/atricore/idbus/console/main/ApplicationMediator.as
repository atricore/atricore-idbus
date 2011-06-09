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
import com.atricore.idbus.console.base.app.BaseAppFacade;
import com.atricore.idbus.console.base.branding.AtricoreConsoleBrandingFactory;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.branding.AtricoreConsolePreloader;
import com.atricore.idbus.console.branding.heading.AtricoreHeading;
import com.atricore.idbus.console.licensing.main.model.LicenseProxy;
import com.atricore.idbus.console.main.controller.ActivateLicenseCommand;
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

import com.atricore.idbus.console.main.controller.CheckLicenseCommand;

import com.atricore.idbus.console.main.view.util.MouseWheelEnabler;

import flash.events.Event;

import flash.events.MouseEvent;

import mx.controls.Alert;
import mx.controls.MenuBar;
import mx.core.IVisualElement;
import mx.events.FlexEvent;
import mx.events.MenuEvent;
import mx.events.StateChangeEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.ButtonBar;
import spark.events.IndexChangeEvent;

public class ApplicationMediator extends IocMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    public var userProfileIcon:Class = EmbeddedIcons.userProfileIcon;

    private var _brandingFactory:AtricoreConsoleBrandingFactory;

    private var _appSections:Array;

    private var _selectedAppSectionIndex:int;

    // TODO : Remove Dependencies to specific services

    private var _secureContextProxy:SecureContextProxy;
    private var _projectProxy:ProjectProxy;
    private var _keystoreProxy:KeystoreProxy;
    private var _profileProxy:ProfileProxy;
    private var _licenseProxy:LicenseProxy;

    //login mediator
    private var _loginMediator:IIocMediator;
    private var _licenseActivationMediator:IIocMediator;

    private var _popupManager:ConsolePopUpManager;

    private var _userActionMenuBar:MenuBar;

    public function ApplicationMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {

        super(p_mediatorName, p_viewComponent);

    }


    public function get brandingFactory():AtricoreConsoleBrandingFactory {
        return _brandingFactory;
    }

    public function set brandingFactory(value:AtricoreConsoleBrandingFactory):void {
        _brandingFactory = value;
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

    public function get licenseProxy():LicenseProxy {
        return _licenseProxy;
    }

    public function set licenseProxy(value:LicenseProxy):void {
        _licenseProxy = value;
    }

    public function get loginMediator():IIocMediator {
        return _loginMediator;
    }

    public function set loginMediator(value:IIocMediator):void {
        _loginMediator = value;
    }

    public function get licenseActivationMediator():IIocMediator {
        return _licenseActivationMediator;
    }

    public function set licenseActivationMediator(value:IIocMediator):void {
        _licenseActivationMediator = value;
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

        // Apply branding:
        var brandedHeader:AtricoreHeading = app.mainVGroup.getElementAt(0) as AtricoreHeading;
        brandedHeader.setStyle("skinClass",  brandingFactory.getHeaderSkinClass());

        init();
    }


    public function init():void {
        sendNotification(ApplicationFacade.NOT_FIRST_RUN);
        popupManager.init(iocFacade, app);
        app.addEventListener(FlexEvent.SHOW, handleShowConsole);
        app.addEventListener(Event.ADDED_TO_STAGE, stage_init);
        createHeading();
    }

    private function createHeading():void {
        //app.brandedHeading.addChild(app.messageBox);
        //app.brandedHeading.addChild(app.userActionMenuBar);
    }

    private function stage_init(e:Event):void {
        MouseWheelEnabler.init(app.stage);
    }

    public function handleStackChange(event:IndexChangeEvent):void {
        var selectedIndex:int = (event.currentTarget as ButtonBar).selectedIndex;
        var currentMediator:AppSectionMediator = _appSections[event.oldIndex];

        // TODO : Is there a better way, with a proxy ?
        _selectedAppSectionIndex = selectedIndex;

        // Send old and new view names ...
        sendNotification(BaseAppFacade.APP_SECTION_CHANGE_START, currentMediator.viewName);

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
        return [BaseAppFacade.APP_SECTION_CHANGE,
            BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED,
            BaseAppFacade.APP_SECTION_CHANGE_REJECTED,
            ApplicationFacade.SHOW_ERROR_MSG,
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
            ApplicationFacade.DISPLAY_VIEW,
            ApplicationFacade.DISPLAY_APPLIANCE_MODELER,
            ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE,
            ApplicationFacade.DISPLAY_APPLIANCE_ACCOUNT,
            ApplicationFacade.DISPLAY_LIVE_UPDATE,
            ApplicationFacade.DISPLAY_LICENSING,
            ApplicationFacade.DISPLAY_CHANGE_PASSWORD,
            ProcessingMediator.START,
            ProcessingMediator.STOP,
            CheckLicenseCommand.SUCCESS,
            CheckLicenseCommand.FAILURE,
            ActivateLicenseCommand.SUCCESS
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
                // Login SUCCESS, switch application state to operational :
                app.addEventListener(StateChangeEvent.CURRENT_STATE_CHANGE, switchedMode);
                app.currentState = "operation";
                break;
            case ApplicationFacade.SHOW_ERROR_MSG :
                //              app.messageBox.showFailureMessage(notification.getBody() as String);
                var errString:String = notification.getBody() as String;
                Alert.show(errString, resourceManager.getString(AtricoreConsole.BUNDLE, "alert.error"));
                break;
            //            case ApplicationFacade.SHOW_SUCCESS_MSG :
            //                app.messageBox.showSuccessMessage(notification.getBody() as String);
            //                break;
            case ApplicationFacade.CLEAR_MSG :
                //                app.messageBox.clearAndHide();
                break;
            case BaseAppFacade.APP_SECTION_CHANGE:
                // manual app. section change trigger
                var viewName:String = notification.getBody() as String;
                var currentMediator:AppSectionMediator = _appSections[_selectedAppSectionIndex];

                _selectedAppSectionIndex = getAppSectionIndex(viewName);

                sendNotification(BaseAppFacade.APP_SECTION_CHANGE_START, currentMediator.viewName);
                break;
            case BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED:
                // Get selected mediator
                var selectedMediator:AppSectionMediator = _appSections[_selectedAppSectionIndex];

                app.stackButtonBar.selectedIndex = _selectedAppSectionIndex;
                if (app.appSectionsViewStack.selectedIndex != _selectedAppSectionIndex) {
                    app.appSectionsViewStack.selectedIndex = _selectedAppSectionIndex;
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_END, selectedMediator.viewName);
                }

                break;
            case BaseAppFacade.APP_SECTION_CHANGE_REJECTED:
                // open rejected view
                var rejectedViewName:String = notification.getBody() as String;
                _selectedAppSectionIndex = getAppSectionIndex(rejectedViewName);

                app.callLater(function ():void {
                    app.stackButtonBar.selectedIndex = _selectedAppSectionIndex;
                    if (app.appSectionsViewStack.selectedIndex != _selectedAppSectionIndex) {
                        app.appSectionsViewStack.selectedIndex = _selectedAppSectionIndex;
                    }
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_END, null);
                });
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
            case CheckLicenseCommand.FAILURE:
                showLicenseActivation();
                break;
            case CheckLicenseCommand.SUCCESS:
                // CheckLicense SUCCESS, switch application state to splash :
                app.addEventListener(StateChangeEvent.CURRENT_STATE_CHANGE, switchedMode);
                app.currentState = "splash";
                break;
            case ActivateLicenseCommand.SUCCESS:
                //product successfully activated
                app.addEventListener(StateChangeEvent.CURRENT_STATE_CHANGE, switchedMode);
                app.currentState = "splash";
                break;
        }
    }

    private function switchedMode(event:StateChangeEvent):void {
        if (event.newState == "operation") {
            login();
        } else
        if (event.newState == "splash") {
            if(event.oldState == "activation"){
                //TODO call method to setup login command,mediator and view
                showLogin();
            } else {
                logout();
            }
        }
    }

    public function showLicenseActivation():void {
        licenseActivationMediator.setViewComponent(app.licenseActivationView);
    }

    public function showLogin():void {
        loginMediator.setViewComponent(app.loginView);
        iocFacade.registerMediatorByConfigName(loginMediator.getConfigName());
    }

    public function login():void {

        // Dynamically discover all app section mediators
        _appSections = new Array();
        var appSectionMediatorNames:Array = iocFacade.container.getObjectNamesForType(AppSectionMediator);
        appSectionMediatorNames.forEach(function(mediatorName:String, idx:int, arr:Array):void {

            // App Section Mediator found
            var mediator:AppSectionMediator = iocFacade.container.getObject(mediatorName) as AppSectionMediator;
            // Store mediators for later use
            _appSections.push(mediator);

        });

        // Sort sections before building the view
        function sortAppSections(a:AppSectionMediator, b:AppSectionMediator):int {
            return a.viewPriority - b.viewPriority;
        }
        _appSections.sort(sortAppSections);

        app.appSectionsViewStack.removeAllChildren();

        // Wire stack view with app section views
        _appSections.forEach(function(mediator:AppSectionMediator, idx:int, arr:Array):void {
            // Add new section to stack view:
            // wired mediator with view
            var view:IVisualElement = mediator.viewFactory.createView() as IVisualElement;
            mediator.setViewComponent(view);

            app.appSectionsViewStack.addNewChild(view);
        });


        app.stackButtonBar.addEventListener(IndexChangeEvent.CHANGE, handleStackChange);

        app.stackButtonBar.selectedIndex = 0;
        if (_secureContextProxy.currentUser != null) {
            app.userActionMenuBar.dataProvider.source[0].@label = _secureContextProxy.currentUser.commonName;
        }
        app.userActionMenuBar.addEventListener(MenuEvent.ITEM_CLICK, handleUserMenuAction);
        sendNotification(ApplicationFacade.CLEAR_MSG);

        // By default, switch to first app section view
        sendNotification(BaseAppFacade.APP_SECTION_CHANGE, _appSections[0].viewFactory.viewName);
    }

    public function logout():void {
        secureContextProxy.dispose();
        projectProxy.dispose();
        keystoreProxy.dispose();
        profileProxy.dispose();

        sendNotification(ApplicationFacade.LOGOUT);
    }

    private function getAppSectionIndex(viewName:String):int {
        var index:int = -1;
        for (var i:int = 0; i < _appSections.length; i++) {
            if (_appSections[i].viewName == viewName) {
                index = i;
                break;
            }
        }
        return index;
    }

    public function get app():AtricoreConsole {
        return getViewComponent() as AtricoreConsole;
    }
}
}