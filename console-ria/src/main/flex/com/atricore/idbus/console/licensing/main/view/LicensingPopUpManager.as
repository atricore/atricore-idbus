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

package com.atricore.idbus.console.licensing.main.view {
import com.atricore.idbus.console.licensing.main.view.displaylicensetext.DisplayLicenseTextForm;
import com.atricore.idbus.console.licensing.main.view.displaylicensetext.DisplayLicenseTextMediator;
import com.atricore.idbus.console.licensing.main.view.updatelicense.UpdateLicenseForm;
import com.atricore.idbus.console.licensing.main.view.updatelicense.UpdateLicenseMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import com.atricore.idbus.console.main.view.license.ActivateLicenseForm;
import com.atricore.idbus.console.main.view.license.ActivateLicenseMediator;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class LicensingPopUpManager extends BasePopUpManager {

    // mediators
    private var _updateLicenseMediator:UpdateLicenseMediator;
    private var _activateLicenseMediator:ActivateLicenseMediator;
    private var _displayLicenseTextMediator:DisplayLicenseTextMediator;

    // views
    private var _updateLicenseForm:UpdateLicenseForm;
    private var _activateLicenseForm:ActivateLicenseForm;
    private var _displayLicenseTextForm:DisplayLicenseTextForm;

    //commands
//    private var _updateLicenseCommand:UpdateLicenseCommand;

    public function LicensingPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "licensePopup";
    }

    public function get updateLicenseMediator():UpdateLicenseMediator {
        return _updateLicenseMediator;
    }

    public function set updateLicenseMediator(value:UpdateLicenseMediator):void {
        _updateLicenseMediator = value;
    }

    public function get activateLicenseMediator():ActivateLicenseMediator {
        return _activateLicenseMediator;
    }

    public function set activateLicenseMediator(value:ActivateLicenseMediator):void {
        _activateLicenseMediator = value;
    }

    public function get displayLicenseTextMediator():DisplayLicenseTextMediator {
        return _displayLicenseTextMediator;
    }

    public function set displayLicenseTextMediator(value:DisplayLicenseTextMediator):void {
        _displayLicenseTextMediator = value;
    }

    public function showUpdateLicenseWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createUpdateLicenseForm();
        
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.update.form.heading');
        _popup.width = 360;
        _popup.height =140;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_updateLicenseForm);
        //on show call bindForm()
    }

    private function createUpdateLicenseForm():void {
        _updateLicenseForm = new UpdateLicenseForm();
        _updateLicenseForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleUpdateLicenseFormCreated);
    }

    private function handleUpdateLicenseFormCreated(event:FlexEvent):void {
        updateLicenseMediator.setViewComponent(_updateLicenseForm);
        updateLicenseMediator.handleNotification(_lastWindowNotification);
    }

    public function showActivateLicenseWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createActivateLicenseForm();

        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.activate.form.heading');
        _popup.width = 360;
        _popup.height =140;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_activateLicenseForm);
        //on show call bindForm()
    }

    private function createActivateLicenseForm():void {
        _activateLicenseForm = new ActivateLicenseForm();
        _activateLicenseForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleActivateLicenseFormCreated);
    }

    private function handleActivateLicenseFormCreated(event:FlexEvent):void {
        activateLicenseMediator.setViewComponent(_activateLicenseForm);
        activateLicenseMediator.handleNotification(_lastWindowNotification);
    }

    public function showLicenseTextWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createViewLicenseTextForm();
        displayLicenseTextMediator.handleNotification(notification);

        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.vieweula.heading');
        _popup.width = 410;
        _popup.height =450;
        _popup.x = (_popupParent.parentDocument.width / 2) - 205;
        _popup.y = 80;
        showPopup(_displayLicenseTextForm);
    }

    private function createViewLicenseTextForm():void {
        _displayLicenseTextForm = new DisplayLicenseTextForm();
        _displayLicenseTextForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleDisplayLicenseTextFormCreated);
    }

    private function handleDisplayLicenseTextFormCreated(event:FlexEvent):void {
        displayLicenseTextMediator.setViewComponent(_displayLicenseTextForm);
        displayLicenseTextMediator.handleNotification(_lastWindowNotification);
    }
}
}