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

package com.atricore.idbus.console.main.view.license
{
import com.atricore.idbus.console.licensing.main.controller.GetLicenseCommand;
import com.atricore.idbus.console.licensing.main.model.LicenseProxy;
import com.atricore.idbus.console.licensing.main.view.LicensingPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;

import com.atricore.idbus.console.main.controller.ActivateLicenseCommand;
import com.atricore.idbus.console.main.controller.CheckLicenseCommand;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LicenseActivationMediator extends IocMediator {


    private var _popupManager:LicensingPopUpManager;

    private var _licenseProxy:LicenseProxy;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    //mediator
    private var _activateLicenseMediator:ActivateLicenseMediator;

    //commands
    private var _activateLicenseCommand:ActivateLicenseCommand;
//    private var _getLicenseCommand:GetLicenseCommand;

    public function LicenseActivationMediator(name:String = null, viewComp:LicenseActivationView = null) {
        super(name, viewComp);
    }


    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
//            view.btnLogin.removeEventListener(MouseEvent.CLICK, handleLoginButton);
        }

        super.setViewComponent(viewComponent);
        popupManager.init(iocFacade, view);
        init();
    }

    private function init():void {
        (facade as IIocFacade).registerMediatorByConfigName(activateLicenseMediator.getConfigName());
        (facade as IIocFacade).registerCommandByConfigName(ApplicationFacade.UPDATE_LICENSE, activateLicenseCommand.getConfigName());
//        if(!(facade as IIocFacade).hasCommand(getLicenseCommand.getConfigName())){
//            (facade as IIocFacade).registerCommandByConfigName(ApplicationFacade.GET_LICENSE, getLicenseCommand.getConfigName());
//        }
        view.btnActivateLicense.addEventListener(MouseEvent.CLICK, handleActivateLicenseButton);
    }

    public function handleActivateLicenseButton(event:Event):void {
        sendNotification(ApplicationFacade.DISPLAY_ACTIVATE_LICENSE);
    }

    override public function listNotificationInterests():Array {
        return [CheckLicenseCommand.SUCCESS,
                CheckLicenseCommand.INVALID,
                ApplicationFacade.DISPLAY_ACTIVATE_LICENSE,
                CheckLicenseCommand.FAILURE,
                ActivateLicenseCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CheckLicenseCommand.SUCCESS :
                // do nothing AppMediator is taking care of it
                break;
            case CheckLicenseCommand.FAILURE :
                handleCheckLicenseFailure();
                break;
            case CheckLicenseCommand.INVALID :
                handleCheckLicenseFailure();
                break;
            case ApplicationFacade.DISPLAY_ACTIVATE_LICENSE:
                popupManager.showActivateLicenseWindow(notification);
                break;
            case ActivateLicenseCommand.FAILURE:
                handleActivationFailure(notification);
                break;
        }
    }

    public function handleActivationFailure(notification:INotification):void {
        var errMsg:String = notification.getBody() as String;
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, errMsg);
    }

    public function handleCheckLicenseFailure():void {
//        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, resourceManager.getString(AtricoreConsole.BUNDLE, "licensing.checklicense.failure"));
        //set proper label and enable activateLicense button
        view.activationLabel1.text = resourceManager.getString(AtricoreConsole.BUNDLE, "licensing.productNotActivated1");
        view.activationLabel2.text = resourceManager.getString(AtricoreConsole.BUNDLE, "licensing.productNotActivated2");
        if(view.progressBar != null) {
            view.licensePanel.removeChild(view.progressBar);
        }
        view.activationLabel2.visible = true;
        view.btnActivateLicense.enabled = true;
    }

    public function resetLoginForm():void {
        sendNotification(ApplicationFacade.CLEAR_MSG);
    }

    protected function get view():LicenseActivationView
    {
        return viewComponent as LicenseActivationView;
    }

    public function get popupManager():LicensingPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:LicensingPopUpManager):void {
        _popupManager = value;
    }


    public function get activateLicenseCommand():ActivateLicenseCommand {
        return _activateLicenseCommand;
    }

    public function set activateLicenseCommand(value:ActivateLicenseCommand):void {
        _activateLicenseCommand = value;
    }

    public function get activateLicenseMediator():ActivateLicenseMediator {
        return _activateLicenseMediator;
    }

    public function set activateLicenseMediator(value:ActivateLicenseMediator):void {
        _activateLicenseMediator = value;
    }

    public function set licenseProxy(value:LicenseProxy):void {
        _licenseProxy = value;
    }
}
}
