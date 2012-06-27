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

package com.atricore.idbus.console.config.branding.view {
import com.atricore.idbus.console.config.branding.view.create.CreateBrandingWizardView;
import com.atricore.idbus.console.config.branding.view.create.CreateBrandingWizardViewMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class BrandingPopUpManager extends BasePopUpManager {

    // mediators
    private var _createBrandingWizardMediator:CreateBrandingWizardViewMediator;

    // views
    private var _createBrandingWizardView:CreateBrandingWizardView;

    public function BrandingPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "brandingPopup";
    }

    public function get createBrandingWizardMediator():CreateBrandingWizardViewMediator {
        return _createBrandingWizardMediator;
    }

    public function set createBrandingWizardMediator(value:CreateBrandingWizardViewMediator):void {
        _createBrandingWizardMediator = value;
    }

    public function showCreateBrandingWizardWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createBrandingCreationWizardView();
        showWizard(_createBrandingWizardView);
    }

    private function createBrandingCreationWizardView():void {
        _createBrandingWizardView = new CreateBrandingWizardView();
        _createBrandingWizardView.addEventListener(FlexEvent.CREATION_COMPLETE, handleCreateBrandingWizardViewCreated);
    }

    private function handleCreateBrandingWizardViewCreated(event:FlexEvent):void {
        createBrandingWizardMediator.setViewComponent(_createBrandingWizardView);
        createBrandingWizardMediator.handleNotification(_lastWindowNotification);
    }
}
}