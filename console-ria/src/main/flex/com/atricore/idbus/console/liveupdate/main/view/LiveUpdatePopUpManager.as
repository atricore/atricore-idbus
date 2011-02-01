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

package com.atricore.idbus.console.liveupdate.main.view {
import com.atricore.idbus.console.liveupdate.main.view.notifications.NotificationSchemeForm;
import com.atricore.idbus.console.liveupdate.main.view.notifications.NotificationSchemeMediator;
import com.atricore.idbus.console.main.BasePopUpManager;

import mx.core.UIComponent;
import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.IFacade;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;

public class LiveUpdatePopUpManager extends BasePopUpManager {

    // mediators
    private var _notificationSchemeMediator:NotificationSchemeMediator;

    // views
    private var _updateNotificationForm:NotificationSchemeForm;

    public function LiveUpdatePopUpManager() {
        super();
    }

    override public function init(facade:IFacade, popupParent:UIComponent):void {
        super.init(facade, popupParent);
        _popup.styleName = "liveUpdatePopup";
        (facade as IIocFacade).registerMediatorByConfigName(notificationSchemeMediator.getConfigName());
    }

    // Update Notification Popup
    public function showUpdateNotificationWindow(notification:INotification):void {
        _lastWindowNotification = notification;
        createUpdateNotificationForm();

        _popup.title = resourceManager.getString(AtricoreConsole.BUNDLE, 'liveupdate.setup.form.popupHeading');
        _popup.width = 600;
        _popup.height =370;
        _popup.x = (_popupParent.width / 2) - 225;
        _popup.y = 80;
        showPopup(_updateNotificationForm);
    }

    private function createUpdateNotificationForm():void {
        _updateNotificationForm = new NotificationSchemeForm();
        _updateNotificationForm.addEventListener(FlexEvent.CREATION_COMPLETE, handleUpdateNotificationFormCreated);
    }

    private function handleUpdateNotificationFormCreated(event:FlexEvent):void {
        notificationSchemeMediator.setViewComponent(_updateNotificationForm);
        notificationSchemeMediator.handleNotification(_lastWindowNotification);
    }

    public function get notificationSchemeMediator():NotificationSchemeMediator {
        return _notificationSchemeMediator;
    }

    public function set notificationSchemeMediator(value:NotificationSchemeMediator):void {
        _notificationSchemeMediator = value;
    }
}
}