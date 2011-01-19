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

package com.atricore.idbus.console.liveupdate.main {
import com.atricore.idbus.console.liveupdate.main.controller.ApplyUpdateCommand;
import com.atricore.idbus.console.liveupdate.main.controller.CheckForUpdatesCommand;
import com.atricore.idbus.console.liveupdate.main.controller.ListUpdatesCommand;
import com.atricore.idbus.console.liveupdate.main.model.LiveUpdateProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.UpdateDescriptorType;

import com.atricore.idbus.console.services.spi.request.ApplyUpdateRequest;

import flash.events.Event;

import flash.events.MouseEvent;

import flash.text.TextFormat;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.core.IUITextField;
import mx.core.mx_internal;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.events.ListEvent;
import mx.managers.PopUpManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class LiveUpdateMediator extends IocMediator implements IDisposable{

    public static const BUNDLE:String = "console";
    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _liveUpdateProxy:LiveUpdateProxy;

    private var _created:Boolean;

    public function LiveUpdateMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get liveUpdateProxy():LiveUpdateProxy {
        return _liveUpdateProxy;
    }

    public function set liveUpdateProxy(value:LiveUpdateProxy):void {
        _liveUpdateProxy = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
        }

        (p_viewComponent as LiveUpdateView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(p_viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        init();
    }

    public function init():void {
        if (_created) {
            /* Remove unused title in account management panel */
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;

            view.updatesList.addEventListener(ListEvent.ITEM_CLICK , updateListSelectHandler);
            view.btnInstallUpdate.addEventListener(MouseEvent.CLICK, handleInstallUpdateClick);
        }
    }

    private function updateListSelectHandler(e:ListEvent):void {
        var selectedUpdate:UpdateDescriptorType = e.currentTarget.selectedItem as UpdateDescriptorType;
        _liveUpdateProxy.selectedUpdate = selectedUpdate;

        if (view.updatesList.selectedIndex != -1)
            view.btnInstallUpdate.enabled = true;
        else
            view.btnInstallUpdate.enabled = false;
    }

    private function handleInstallUpdateClick(event:MouseEvent):void {
        var updReq:ApplyUpdateRequest = new ApplyUpdateRequest();
        updReq.group = _liveUpdateProxy.selectedUpdate.group;
        updReq.name = _liveUpdateProxy.selectedUpdate.name;
        updReq.version = _liveUpdateProxy.selectedUpdate.version;
        updReq.offline = false;
        var alertBody:String = resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.install.answer');
        alertBody += "\n" + updateBasicInfo(_liveUpdateProxy.selectedUpdate);
        var title:String = resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.installUpdate.title');

        var updAlert:Alert = Alert.show(alertBody, title, (Alert.OK | Alert.CANCEL), view,
                                       function (event:CloseEvent) {
                                           if (event.detail == Alert.OK) {
                                               sendNotification(ApplicationFacade.APPLY_UPDATE, updReq);
                                               sendNotification(ProcessingMediator.START);
                                           }
                                           else
                                               PopUpManager.removePopUp(updAlert);
                                       });
        updAlert.width = 500;
        updAlert.height = 300;
        // align text in alert box
        updAlert.callLater(function():void {
            var textField:IUITextField =  IUITextField(updAlert.mx_internal::alertForm.mx_internal::textField);

            var textFormat:TextFormat = new TextFormat();
            textFormat.align = "center";

            textField.setActualSize(textField.getExplicitOrMeasuredWidth(),textField.getExplicitOrMeasuredHeight());
            textField.x = 0;
            textField.setTextFormat(textFormat);
        });
    }

    private function updateBasicInfo(update:UpdateDescriptorType):String {
        var updateInfo:String = "";
        var reqs:ArrayCollection = update.requirements;

        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.group') + ":  " + update.group + "\n";
        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.name') + ":  " + update.name + "\n";
        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.version') + ":  " + update.version + "\n";
        if (reqs.length>0) {
            updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.dependencies.warning') + ": \n";

            for(var count:int = 0; count < reqs.length; count++) {
                updateInfo+= reqs.getItemAt(count).group + " / " +
                        reqs.getItemAt(count).name + " / " +
                        reqs.getItemAt(count).versionExpresion + "\n";
            }
        }
        return updateInfo;
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.UPDATE_VIEW_SELECTED,
            ListUpdatesCommand.SUCCESS,
            ListUpdatesCommand.FAILURE,
            ApplyUpdateCommand.SUCCESS,
            ApplyUpdateCommand.FAILURE,
            CheckForUpdatesCommand.SUCCESS,
            CheckForUpdatesCommand.FAILURE
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.UPDATE_VIEW_SELECTED:
                init();
                sendNotification(ApplicationFacade.LIST_UPDATES);
                break;
            case ListUpdatesCommand.SUCCESS:
                view.updatesList.dataProvider = _liveUpdateProxy.availableUpdatesList;
                view.validateNow();
                break;
            case ListUpdatesCommand.FAILURE:
                break;
            case ApplyUpdateCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                break;
            case ApplyUpdateCommand.SUCCESS:
                break;
            case CheckForUpdatesCommand.SUCCESS:
                break;
            case CheckForUpdatesCommand.FAILURE:
                break;
        }
    }

    protected function get view():LiveUpdateView
    {
        return viewComponent as LiveUpdateView;
    }

    protected function set view(luv:LiveUpdateView):void
    {
        viewComponent = luv;
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        view.updatesList.removeEventListener(ListEvent.ITEM_CLICK , updateListSelectHandler);
        view = null;
    }
}
}