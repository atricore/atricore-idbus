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

package com.atricore.idbus.console.main.view.login
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.controller.LoginCommand;
import com.atricore.idbus.console.main.model.PersistentLoginDetails;
import com.atricore.idbus.console.main.model.request.LoginRequest;
import com.atricore.idbus.console.main.view.form.FormUtility;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LoginMediator extends IocMediator
{
    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _loginValidators:Array;

    public function LoginMediator(name:String = null, viewComp:LoginView = null) {
        super(name, viewComp);
    }


    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnLogin.removeEventListener(MouseEvent.CLICK, handleLoginButton);
            view.removeEventListener(FlexEvent.SHOW, handleShowLogin);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {

        view.btnLogin.addEventListener(MouseEvent.CLICK, handleLoginButton);
        view.addEventListener(FlexEvent.SHOW, handleShowLogin);

        _loginValidators = [];
        _loginValidators.push(view.userNameValidator);
        _loginValidators.push(view.passwordValidator);
        handleShowLogin(null);
    }

    override public function listNotificationInterests():Array {
        return [ LoginCommand.SUCCESS,
            LoginCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case LoginCommand.SUCCESS :
                // do nothing AppMediator is taking care of it
                break;
            case LoginCommand.FAILURE :
                handleLoginFailure();
                break;
        }
    }

    public function handleShowLogin(event:Event):void {
        if (PersistentLoginDetails.hasDetails()) {
            view.userName.text = PersistentLoginDetails.getEmail();
            view.password.text = PersistentLoginDetails.getPassword();
            view.rememberMe.selected = true;
        }
        else {
            resetLoginForm();
        }
        view.focusManager.setFocus(view.userName);
    }

    public function handleLoginButton(event:Event):void {
        FormUtility.doValidate(_loginValidators);
        if (FormUtility.validateAll(_loginValidators)) {
            var loginRequest:LoginRequest = new LoginRequest();
            loginRequest.username = view.userName.text;
            loginRequest.password = view.password.text;
            loginRequest.passwordReminderRequest = false;
            sendNotification(ApplicationFacade.LOGIN, loginRequest);

            if (view.rememberMe.selected) {
                PersistentLoginDetails.clear();
                PersistentLoginDetails.store(view.userName.text, view.password.text);
            }
            else {
                PersistentLoginDetails.clear();
            }
        }
        else {
            sendNotification(ApplicationFacade.SHOW_ERROR_MSG, resourceManager.getString(AtricoreConsole.BUNDLE, "login.error.validation"));
        }
    }

    public function handleClearButton(event:MouseEvent):void {
        resetLoginForm();
    }


    public function handleLoginFailure():void {
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, resourceManager.getString(AtricoreConsole.BUNDLE, "login.error.failure"));
    }

    public function handleEmailSuccess():void {
//        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
//                "An email with your password has been sent to your account.");
        Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, "login.email.success"),
                resourceManager.getString(AtricoreConsole.BUNDLE, "alert.information"));
    }

    public function resetLoginForm():void {
        view.userName.text = "";
        view.password.text = "";
        view.rememberMe.selected = false;
        FormUtility.clearValidationErrors(_loginValidators);
        sendNotification(ApplicationFacade.CLEAR_MSG);
    }

    protected function get view():LoginView
    {
        return viewComponent as LoginView;
    }
}
}
