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

package com.atricore.idbus.console.licensing
{
import com.atricore.idbus.console.licensing.main.controller.UpdateLicenseCommand;
import com.atricore.idbus.console.licensing.main.view.LicensingPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;

import com.atricore.idbus.console.main.view.progress.ProcessingMediator;

import com.atricore.idbus.console.services.dto.Resource;

import flash.events.Event;
import flash.events.MouseEvent;

import flash.net.FileFilter;
import flash.net.FileReference;

import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.FlexEvent;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LicenseMediator extends IocMediator implements IDisposable {


    private var _popupManager:LicensingPopUpManager;

    [Bindable]
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:ArrayCollection;

    private var _created:Boolean;    

    public function LicenseMediator(name:String = null, viewComp:LicenseView = null) {
        super(name, viewComp);
    }

    public function get popupManager():LicensingPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:LicensingPopUpManager):void {
        _popupManager = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnUpdateLicense.removeEventListener(MouseEvent.CLICK, handleUpdateLicenseButton);
        }

        (viewComponent as LicenseView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        
        popupManager.init(iocFacade, view);
        view.btnUpdateLicense.addEventListener(MouseEvent.CLICK, handleUpdateLicenseButton);
        init();
    }

    private function init():void {
        if (_created) {
            /* Remove unused title in account management panel */
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;
        }

    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.LICENSE_VIEW_SELECTED,
            UpdateLicenseCommand.SUCCESS,
            UpdateLicenseCommand.FAILURE,
            ApplicationFacade.DISPLAY_UPDATE_LICENSE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case UpdateLicenseCommand.SUCCESS :
                // do nothing AppMediator is taking care of it
                break;
            case UpdateLicenseCommand.FAILURE :
                handleActivationFailure();
                break;
            case ApplicationFacade.LICENSE_VIEW_SELECTED:
                init();
                break;
            case ApplicationFacade.DISPLAY_UPDATE_LICENSE:
                popupManager.showUpdateLicenseWindow(notification);
                break;
        }
    }

    public function handleUpdateLicenseButton(event:Event):void {
        sendNotification(ApplicationFacade.DISPLAY_UPDATE_LICENSE);
    }

    public function handleActivationFailure():void {
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                "The license doesn't seem to be valid. " +
                "Please upload valid license.");
    }

    protected function get view():LicenseView
    {
        return viewComponent as LicenseView;
    }

//    protected function set view(amv:LicenseView):void
//    {
//        viewComponent = amv;
//    }

    public function dispose():void {
        // Clean up

        setViewComponent(null);
    }    
}
}
