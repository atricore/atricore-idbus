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

package com.atricore.idbus.console.account.main.view.addgroup {
import com.atricore.idbus.console.account.main.controller.AddGroupCommand;
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.account.main.view.extraattributes.ExtraAttributesMediator;
import com.atricore.idbus.console.account.main.view.extraattributes.ExtraAttributesTab;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.Group;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.IFocusManagerComponent;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

import spark.components.NavigatorContent;

public class AddGroupMediator extends IocFormMediator
{
    private var _schemasManagementProxy:SchemasManagementProxy;
    private var _extraAttributesMediator:ExtraAttributesMediator;
    private var _newGroup:Group;

    private var _processingStarted:Boolean;

    public function AddGroupMediator(name:String = null, viewComp:AddGroupForm = null) {
        super(name, viewComp);
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    public function get extraAttributesMediator():ExtraAttributesMediator {
        return _extraAttributesMediator;
    }

    public function set extraAttributesMediator(value:ExtraAttributesMediator):void {
        _extraAttributesMediator = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.cancelAddGroup.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitAddGroupButton.removeEventListener(MouseEvent.CLICK, onSubmitAddGroup);
            view.generalSection.removeEventListener(FlexEvent.SHOW, initGeneralSection);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelAddGroup.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitAddGroupButton.addEventListener(MouseEvent.CLICK, onSubmitAddGroup);
        view.generalSection.addEventListener(FlexEvent.SHOW, initGeneralSection);

        if (    schemasManagementProxy.attributesForEntity !=null &&
                schemasManagementProxy.attributesForEntity.length > 0) {
            var extraTab:ExtraAttributesTab = new ExtraAttributesTab();
            view.tabNav.addChild(extraTab);
            extraTab.addEventListener(FlexEvent.SHOW, initExtraSection);
            extraAttributesMediator.setViewComponent(extraTab);
        }

        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        view.focusManager.setFocus(view.groupName);
    }

    override public function registerValidators():void {
        _validators.push(view.nameGroupValidator);
//        _validators.push(view.groupDescriptionValidator);
    }

    override public function listNotificationInterests():Array {
        return [AddGroupCommand.SUCCESS,
            AddGroupCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case AddGroupCommand.SUCCESS :
                handleAddGroupSuccess();
                break;
            case AddGroupCommand.FAILURE :
                handleAddGroupFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.groupName.text = "";
        view.groupDescription.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newGroupDef:Group = new Group();
        newGroupDef.name = view.groupName.text;
        newGroupDef.description = view.groupDescription.text;

        extraAttributesMediator.bindModel();
        newGroupDef.extraAttributes = extraAttributesMediator.attributesValues;

        _newGroup = newGroupDef;
    }

    private function showTabForComponent(comp:UIComponent):void {
        for each (var tab:NavigatorContent in view.tabNav.getChildren()) {
            if (tab.contains(comp))
                view.tabNav.selectedChild = tab;
        }
    }

    private function onSubmitAddGroup(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true) && extraAttributesMediator.validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            sendNotification(ApplicationFacade.ADD_GROUP, _newGroup);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();

            for each (var valdator:Validator in _validators) {
                if (valdator.source.errorString != "") {
                    showTabForComponent(valdator.source as UIComponent);
                    view.focusManager.setFocus(valdator.source as IFocusManagerComponent);
                }
            }
            // do same for extra attributes section
            for each (var valdatorEXtra:Validator in extraAttributesMediator.getValidators) {
                if (valdatorEXtra.source.errorString != "") {
                    showTabForComponent(valdatorEXtra.source as UIComponent);
                    extraAttributesMediator.view.focusManager.setFocus(valdatorEXtra.source as IFocusManagerComponent);
                }
            }
        }
    }

    private function initGeneralSection(event:FlexEvent):void {
        view.focusManager.setFocus(view.groupName);
    }

    private function initExtraSection(event:FlexEvent):void {

    }

    public function handleAddGroupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.LIST_GROUPS);
    }

    public function handleAddGroupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error adding group.");
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():AddGroupForm
    {
        return viewComponent as AddGroupForm;
    }

}
}
