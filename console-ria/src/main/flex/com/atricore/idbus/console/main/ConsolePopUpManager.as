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
import com.atricore.idbus.console.main.view.setup.SetupWizardView;
import com.atricore.idbus.console.main.view.setup.SetupWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceForm;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardView;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class ConsolePopUpManager extends BasePopUpManager {

    protected var _setupWizardView:SetupWizardView;
    protected var _simpleSSOWizardView:SimpleSSOWizardView;
    protected var _identityApplianceForm:IdentityApplianceForm;
    
    public function ConsolePopUpManager(facade:IFacade, application:AtricoreConsole) {
        super(facade, application);
        _popup.styleName = "mainPopup";
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
        var mediator:SetupWizardViewMediator = new SetupWizardViewMediator(_setupWizardView);
        _facade.removeMediator(SetupWizardViewMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
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
        var mediator:SimpleSSOWizardViewMediator = new SimpleSSOWizardViewMediator(_simpleSSOWizardView);
        _facade.removeMediator(SimpleSSOWizardViewMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }

    public function showCreateIdentityApplianceWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        if (!_identityApplianceForm) {
           createIdentityApplianceForm();
        }
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
        var mediator:IdentityApplianceMediator = new IdentityApplianceMediator(_identityApplianceForm);
        _facade.removeMediator(IdentityApplianceMediator.NAME);
        _facade.registerMediator(mediator);
        mediator.handleNotification(_lastWindowNotification);
    }
}
}