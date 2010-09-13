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

package com.atricore.idbus.console.main {
import com.atricore.idbus.console.main.view.profile.ChangePasswordMediator;
import com.atricore.idbus.console.main.view.profile.ChangePasswordView;
import com.atricore.idbus.console.main.view.setup.SetupWizardView;
import com.atricore.idbus.console.main.view.setup.SetupWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceForm;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardView;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ConsolePopUpManager extends BasePopUpManager {

    private var _setupWizardMediator:SetupWizardViewMediator;
    private var _simpleSSOWizardMediator:SimpleSSOWizardViewMediator;
    private var _identityApplianceMediator:IdentityApplianceMediator;
    private var _changePasswordMediator:ChangePasswordMediator;

    protected var _setupWizardView:SetupWizardView;
    protected var _simpleSSOWizardView:SimpleSSOWizardView;
    protected var _identityApplianceForm:IdentityApplianceForm;
    protected var _changePasswordForm:ChangePasswordView;


    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "mainPopup";

    }


    public function get setupWizardMediator():SetupWizardViewMediator {
        return _setupWizardMediator;
    }

    public function set setupWizardMediator(value:SetupWizardViewMediator):void {
        _setupWizardMediator = value;
    }

    public function get simpleSSOWizardMediator():SimpleSSOWizardViewMediator {
        return _simpleSSOWizardMediator;
    }

    public function set simpleSSOWizardMediator(value:SimpleSSOWizardViewMediator):void {
        _simpleSSOWizardMediator = value;
    }

    public function get identityApplianceMediator():IdentityApplianceMediator {
        return _identityApplianceMediator;
    }

    public function set identityApplianceMediator(value:IdentityApplianceMediator):void {
        _identityApplianceMediator = value;
    }

    public function get changePasswordMediator():ChangePasswordMediator {
        return _changePasswordMediator;
    }

    public function set changePasswordMediator(value:ChangePasswordMediator):void {
        _changePasswordMediator = value;
    }

    public function showSetupWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSetupWizardView();
        showWizard(_setupWizardView);
    }

    private function createSetupWizardView():void {
        _setupWizardView = new SetupWizardView();
        _setupWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleSetupViewCreated);
    }

    private function handleSetupViewCreated(event:FlexEvent):void {
        setupWizardMediator.setViewComponent(_setupWizardView);
        setupWizardMediator.handleNotification(_lastWindowNotification);
    }

    public function showSimpleSSOWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createSimpleSSOWizardView();
        showWizard(_simpleSSOWizardView);
    }

    private function createSimpleSSOWizardView():void {
        _simpleSSOWizardView = new SimpleSSOWizardView();
        _simpleSSOWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleSimpleSSOWizardViewCreated);
    }

    private function handleSimpleSSOWizardViewCreated(event:FlexEvent):void {
        simpleSSOWizardMediator.setViewComponent(_simpleSSOWizardView);
        simpleSSOWizardMediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createIdentityApplianceForm();
        _popup.title = "Identity Appliance";
        _popup.width = 650;
        _popup.height = 410;
        //_popup.x = (_popupParent.width / 2) - 225;
        //_popup.y = 80;
        showPopup(_identityApplianceForm);
    }

    private function createIdentityApplianceForm():void {
        _identityApplianceForm = new IdentityApplianceForm();
        _identityApplianceForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleIdentityApplianceFormCreated);
    }

    private function handleIdentityApplianceFormCreated(event:FlexEvent):void {
        identityApplianceMediator.setViewComponent(_identityApplianceForm);
        identityApplianceMediator.handleNotification(_lastWindowNotification);
    }

    public function showChangePasswordWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createChangePasswordForm();
        _popup.title = "Change Password";
        _popup.width = 400;
        _popup.height = 200;
        showPopup(_changePasswordForm);
    }

    private function createChangePasswordForm():void {
        _changePasswordForm = new ChangePasswordView();
        _changePasswordForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleChangePasswordFormCreated);
    }

    private function handleChangePasswordFormCreated(event:FlexEvent):void {
        changePasswordMediator.setViewComponent(_changePasswordForm);
        changePasswordMediator.handleNotification(_lastWindowNotification);
    }
}
}
