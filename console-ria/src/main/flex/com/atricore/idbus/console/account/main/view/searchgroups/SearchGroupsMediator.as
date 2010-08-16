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

package com.atricore.idbus.console.account.main.view.searchgroups {
import com.atricore.idbus.console.account.main.controller.SearchGroupsCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.GroupDTO;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class SearchGroupsMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _searchGroup:GroupDTO;

    private var _processingStarted:Boolean;

    public function SearchGroupsMediator(name:String = null, viewComp:SearchGroupsForm = null) {
        super(name, viewComp);
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.cancelSearchGroup.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitSearchGroupsButton.removeEventListener(MouseEvent.CLICK, onSubmitSearchGroup);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelSearchGroup.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitSearchGroupsButton.addEventListener(MouseEvent.CLICK, onSubmitSearchGroup);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function registerValidators():void {
        _validators.push(view.nameGroupValidator);
        _validators.push(view.groupDescriptionValidator);
    }

    override public function listNotificationInterests():Array {
        return [SearchGroupsCommand.SUCCESS,
            SearchGroupsCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case SearchGroupsCommand.SUCCESS :
                handleSearchGroupSuccess();
                break;
            case SearchGroupsCommand.FAILURE :
                handleSearchGroupFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.groupName.text = "";
        view.groupDescription.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newGroupDef:GroupDTO = new GroupDTO();
        newGroupDef.name = view.groupName.text;
        newGroupDef.description = view.groupDescription.text;

        _searchGroup = newGroupDef;
    }

    private function onSubmitSearchGroup(event:MouseEvent):void {
        _processingStarted = true;
        if (validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            _accountManagementProxy.currentGroup = _searchGroup;
            sendNotification(ApplicationFacade.SEARCH_GROUPS, _searchGroup);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function handleSearchGroupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        var resMan:IResourceManager = ResourceManager.getInstance();
        var srchResult:ArrayCollection = _accountManagementProxy.searchedGroups;

        if (srchResult.length == 0)
            Alert.show(resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.group.empty.query'));
        else {
            sendNotification(ApplicationFacade.DISPLAY_SEARCH_RESULTS_GROUPS, srchResult);
        }
    }

    public function handleSearchGroupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error searching groups.");
    }
    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():SearchGroupsForm
    {
        return viewComponent as SearchGroupsForm;
    }

}
}