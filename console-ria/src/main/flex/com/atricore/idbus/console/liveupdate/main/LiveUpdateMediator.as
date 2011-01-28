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
import com.atricore.idbus.console.liveupdate.main.controller.GetUpdateProfileCommand;
import com.atricore.idbus.console.liveupdate.main.controller.ListUpdatesCommand;
import com.atricore.idbus.console.liveupdate.main.controller.LoadUpdateSchemeCommand;
import com.atricore.idbus.console.liveupdate.main.controller.SaveUpdateSchemeCommand;
import com.atricore.idbus.console.liveupdate.main.model.LiveUpdateProxy;
import com.atricore.idbus.console.liveupdate.main.view.LiveUpdatePopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.UpdateDescriptorType;
import com.atricore.idbus.console.services.spi.request.ApplyUpdateRequest;
import com.atricore.idbus.console.services.spi.request.GetUpdateProfileRequest;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.text.TextFieldAutoSize;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
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
    private var _created:Boolean;

    private var _liveUpdateProxy:LiveUpdateProxy;
    private var _liveUpdatePopUpManager:LiveUpdatePopUpManager;

    // LiveUpdate Commands
    private var _applyUpdateCommand:ApplyUpdateCommand;
    private var _checkForUpdatesCommand:CheckForUpdatesCommand;
    private var _getUpdateProfileCommand:GetUpdateProfileCommand;
    private var _listUpdatesCommand:ListUpdatesCommand;
    private var _loadUpdateSchemeCommand:LoadUpdateSchemeCommand;
    private var _saveUpdateSchemeCommand:SaveUpdateSchemeCommand;

    public function LiveUpdateMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        (p_viewComponent as LiveUpdateView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(p_viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        liveUpdatePopUpManager.init(iocFacade, view);
        registerCommands();
        init();
    }

    public function init():void {
        if (_created) {
            /* Remove unused title in account management panel */
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;

            view.updatesList.addEventListener(ListEvent.ITEM_CLICK , updateListSelectHandler);
            view.btnInstallUpdate.addEventListener(MouseEvent.CLICK, handleInstallUpdateClick);
            view.btnCheckForUpdates.addEventListener(MouseEvent.CLICK, handleCheckForUpdatesClick);
            view.btnUpdateNofification.addEventListener(MouseEvent.CLICK, handleUpdateNofificationClick);

            sendNotification(ApplicationFacade.LIST_UPDATES);
        }
    }

    private function registerCommands():void {
        iocFacade.registerCommandByConfigName(ApplicationFacade.APPLY_UPDATE, applyUpdateCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHECK_FOR_UPDATES, checkForUpdatesCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_UPDATE_PROFILE, getUpdateProfileCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LIST_UPDATES, listUpdatesCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOAD_UPDATE_SCHEME, loadUpdateSchemeCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SAVE_UPDATE_SCHEME, saveUpdateSchemeCommand.getConfigName());
    }

    private function updateListSelectHandler(e:ListEvent):void {
        var selectedUpdate:UpdateDescriptorType = e.currentTarget.selectedItem as UpdateDescriptorType;
        _liveUpdateProxy.selectedUpdate = selectedUpdate;

        var profileReq:GetUpdateProfileRequest  = new GetUpdateProfileRequest();
        profileReq.group = selectedUpdate.group;
        profileReq.name = selectedUpdate.name;
        profileReq.version = selectedUpdate.version;
        sendNotification(ApplicationFacade.GET_UPDATE_PROFILE, profileReq);

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
                                       function (event:CloseEvent):void {
                                           if (event.detail == Alert.OK) {
                                               sendNotification(ApplicationFacade.APPLY_UPDATE, updReq);
                                               sendNotification(ProcessingMediator.START);
                                           }
                                           else
                                               PopUpManager.removePopUp(updAlert);
                                       });
        updAlert.width = 550;
        updAlert.height = 200;
        // align text in alert box
        updAlert.callLater(function():void {
            updAlert.mx_internal::alertForm.mx_internal::textField.autoSize = TextFieldAutoSize.CENTER;
            updAlert.mx_internal::alertForm.mx_internal::textField.wordWrap = false ;
        });
    }

    private function handleCheckForUpdatesClick(event:MouseEvent):void {
        trace("Check for updates Button Click: " + event);
        sendNotification(ApplicationFacade.CHECK_FOR_UPDATES);
    }

    private function handleUpdateNofificationClick(event:MouseEvent):void {
        trace("Update Notification Button Click: " + event);
        sendNotification(ApplicationFacade.DISPLAY_UPDATE_NOTIFICATIONS);
    }

    private function updateBasicInfo(update:UpdateDescriptorType):String {
        var updateInfo:String = "";
        var reqs:ArrayCollection = update.requirements;

        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.group') + ":  " + update.group + "\n";
        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.name') + ":  " + update.name + "\n";
        updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.header.version') + ":  " + update.version + "\n";
        if (_liveUpdateProxy.selectedProfile != null) {
            updateInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.dependencies.warning') + ": \n";

            var insUnits:ArrayCollection = _liveUpdateProxy.selectedProfile.installableUnits;
            for(var count:int = 0; count < insUnits.length; count++) {
                updateInfo+= insUnits.getItemAt(count).group + " / " +
                        insUnits.getItemAt(count).name + " / " +
                        insUnits.getItemAt(count).version + "\n";
            }
        }
        return updateInfo;
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.DISPLAY_UPDATE_NOTIFICATIONS,
            ListUpdatesCommand.SUCCESS,
            ListUpdatesCommand.FAILURE,
            ApplyUpdateCommand.SUCCESS,
            ApplyUpdateCommand.FAILURE,
            CheckForUpdatesCommand.SUCCESS,
            CheckForUpdatesCommand.FAILURE,
            GetUpdateProfileCommand.SUCCESS,
            GetUpdateProfileCommand.FAILURE
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_UPDATE_NOTIFICATIONS:
                liveUpdatePopUpManager.showUpdateNotificationWindow(notification);
                break;
            case ListUpdatesCommand.SUCCESS:
                view.updatesList.dataProvider = _liveUpdateProxy.availableUpdatesList;
                view.validateNow();
                break;
            case ListUpdatesCommand.FAILURE:
                break;
            case ApplyUpdateCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                var msg:String = resMan.getString(AtricoreConsole.BUNDLE, 'liveupdate.restart.warning');
                var wMsg:Alert = Alert.show(msg, "Warning!", Alert.OK , view,
                                           function (event:CloseEvent):void {
                                               PopUpManager.removePopUp(wMsg);
                                           });
                break;
            case ApplyUpdateCommand.SUCCESS:
                break;
            case CheckForUpdatesCommand.SUCCESS:
                sendNotification(ApplicationFacade.LIST_UPDATES);
                break;
            case CheckForUpdatesCommand.FAILURE:
                break;
            case GetUpdateProfileCommand.SUCCESS:
                break;
            case GetUpdateProfileCommand.FAILURE:
                _liveUpdateProxy.selectedProfile = null;
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

    public function get liveUpdateProxy():LiveUpdateProxy {
        return _liveUpdateProxy;
    }

    public function set liveUpdateProxy(value:LiveUpdateProxy):void {
        _liveUpdateProxy = value;
    }

    public function get liveUpdatePopUpManager():LiveUpdatePopUpManager {
        return _liveUpdatePopUpManager;
    }

    public function set liveUpdatePopUpManager(value:LiveUpdatePopUpManager):void {
        _liveUpdatePopUpManager = value;
    }

    public function get applyUpdateCommand():ApplyUpdateCommand {
        return _applyUpdateCommand;
    }

    public function set applyUpdateCommand(value:ApplyUpdateCommand):void {
        _applyUpdateCommand = value;
    }

    public function get checkForUpdatesCommand():CheckForUpdatesCommand {
        return _checkForUpdatesCommand;
    }

    public function set checkForUpdatesCommand(value:CheckForUpdatesCommand):void {
        _checkForUpdatesCommand = value;
    }

    public function get getUpdateProfileCommand():GetUpdateProfileCommand {
        return _getUpdateProfileCommand;
    }

    public function set getUpdateProfileCommand(value:GetUpdateProfileCommand):void {
        _getUpdateProfileCommand = value;
    }

    public function get listUpdatesCommand():ListUpdatesCommand {
        return _listUpdatesCommand;
    }

    public function set listUpdatesCommand(value:ListUpdatesCommand):void {
        _listUpdatesCommand = value;
    }

    public function get loadUpdateSchemeCommand():LoadUpdateSchemeCommand {
        return _loadUpdateSchemeCommand;
    }

    public function set loadUpdateSchemeCommand(value:LoadUpdateSchemeCommand):void {
        _loadUpdateSchemeCommand = value;
    }

    public function get saveUpdateSchemeCommand():SaveUpdateSchemeCommand {
        return _saveUpdateSchemeCommand;
    }

    public function set saveUpdateSchemeCommand(value:SaveUpdateSchemeCommand):void {
        _saveUpdateSchemeCommand = value;
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        view.updatesList.removeEventListener(ListEvent.ITEM_CLICK , updateListSelectHandler);
        view.btnInstallUpdate.removeEventListener(MouseEvent.CLICK, handleInstallUpdateClick);
        view.btnCheckForUpdates.removeEventListener(MouseEvent.CLICK, handleCheckForUpdatesClick);
        view.btnUpdateNofification.removeEventListener(MouseEvent.CLICK, handleUpdateNofificationClick);
        view = null;
    }
}
}