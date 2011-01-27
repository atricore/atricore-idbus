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
import com.atricore.idbus.console.licensing.main.view.updatelicense.UpdateLicenseForm;
import com.atricore.idbus.console.licensing.main.view.updatelicense.UpdateLicenseMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;

public class LicensingPopUpManager extends BasePopUpManager {

    // mediators
    private var _updateLicenseMediator:UpdateLicenseMediator;

    // views
    private var _updateLicenseForm:UpdateLicenseForm;

    public function LicensingPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "accountManPopup";
        (facade as IIocFacade).registerMediatorByConfigName(updateLicenseMediator.getConfigName());
    }

    public function get updateLicenseMediator():UpdateLicenseMediator {
        return _updateLicenseMediator;
    }

    public function set updateLicenseMediator(value:UpdateLicenseMediator):void {
        _updateLicenseMediator = value;
    }
    
    public function showUpdateLicenseWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createUpdateLicenseForm();
        
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'licensing.update.form.heading');
        _popup.width = 400;
        _popup.height =200;
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

}
}