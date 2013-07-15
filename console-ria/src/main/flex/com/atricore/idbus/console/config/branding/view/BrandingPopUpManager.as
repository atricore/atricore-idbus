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
import com.atricore.idbus.console.config.branding.view.create.CreateBrandingExtensionView;
import com.atricore.idbus.console.config.branding.view.create.CreateBrandingExtensionMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;

public class BrandingPopUpManager extends BasePopUpManager {

    // mediators
    private var _createBrandingExtensionMediator:CreateBrandingExtensionMediator;

    // views
    private var _createBrandingExtensionView:CreateBrandingExtensionView;

    public function BrandingPopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "brandingPopup";
    }

    public function get createBrandingExtensionMediator():CreateBrandingExtensionMediator {
        return _createBrandingExtensionMediator;
    }

    public function set createBrandingExtensionMediator(value:CreateBrandingExtensionMediator):void {
        _createBrandingExtensionMediator = value;
    }

    public function showCreateBrandingExtensionWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createBrandingCreationView();
        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.create.title');
        _popup.width = 650
        _popup.x = (_popupParent.width / 2) - 325;
        _popup.y = 50;
        showPopup(_createBrandingExtensionView);
    }

    private function createBrandingCreationView():void {
        _createBrandingExtensionView = new CreateBrandingExtensionView();
        _createBrandingExtensionView.addEventListener(FlexEvent.CREATION_COMPLETE, handleCreateBrandingViewCreated);
    }

    private function handleCreateBrandingViewCreated(event:FlexEvent):void {
        createBrandingExtensionMediator.setViewComponent(_createBrandingExtensionView);
        createBrandingExtensionMediator.handleNotification(_lastWindowNotification);
    }
}
}