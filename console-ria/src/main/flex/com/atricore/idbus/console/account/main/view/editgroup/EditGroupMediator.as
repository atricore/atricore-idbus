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

package com.atricore.idbus.console.account.main.view.editgroup {
import com.atricore.idbus.console.account.main.controller.EditGroupCommand;
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.account.main.view.extraattributes.ExtraAttributesMediator;
import com.atricore.idbus.console.account.main.view.extraattributes.ExtraAttributesTab;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.Group;

import com.atricore.idbus.console.services.dto.schema.Attribute;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.events.FlexEvent;

import mx.managers.IFocusManagerComponent;
import mx.validators.Validator;

import org.puremvc.as3.interfaces.INotification;

import spark.components.NavigatorContent;

public class EditGroupMediator extends IocFormMediator
{
    private var _accountManagementProxy:AccountManagementProxy;
    private var _extraAttributesMediator:ExtraAttributesMediator;
    private var _editedGroup:Group;

    private var _processingStarted:Boolean;

    public function EditGroupMediator(name:String = null, viewComp:EditGroupForm = null) {
        super(name, viewComp);
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    public function get extraAttributesMediator():ExtraAttributesMediator {
        return _extraAttributesMediator;
    }

    public function set extraAttributesMediator(value:ExtraAttributesMediator):void {
        _extraAttributesMediator = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.cancelEditGroup.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.submitEditGroupButton.removeEventListener(MouseEvent.CLICK, onSubmitEditGroup);
            view.generalSection.removeEventListener(FlexEvent.SHOW, initGeneralSection);
            if (view.parent != null) {
                view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);
            }
        }

        super.setViewComponent(viewComponent);
        init();
    }

    private function init():void {
        view.cancelEditGroup.addEventListener(MouseEvent.CLICK, handleCancel);
        view.submitEditGroupButton.addEventListener(MouseEvent.CLICK, onSubmitEditGroup);
        view.generalSection.addEventListener(FlexEvent.SHOW, initGeneralSection);

        if (    accountManagementProxy.attributesForEntity !=null &&
                accountManagementProxy.attributesForEntity.length > 0) {
            var extraTab:ExtraAttributesTab = new ExtraAttributesTab();
            view.tabNav.addChild(extraTab);
            extraTab.addEventListener(FlexEvent.SHOW, initExtraSection);
            extraAttributesMediator.setViewComponent(extraTab);
        }

        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);
        bindForm();
        view.focusManager.setFocus(view.groupName);
    }

    override public function registerValidators():void {
        _validators.push(view.nameGroupValidator);
//        _validators.push(view.groupDescriptionValidator);
    }

    override public function listNotificationInterests():Array {
        return [EditGroupCommand.SUCCESS,
            EditGroupCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case EditGroupCommand.SUCCESS :
                handleEditGroupSuccess();
                break;
            case EditGroupCommand.FAILURE :
                handleEditGroupFailure();
                break;
        }
    }

    override public function bindForm():void {
        view.groupName.text = _accountManagementProxy.currentGroup.name;
        view.groupDescription.text = _accountManagementProxy.currentGroup.description;
        if (_accountManagementProxy.currentGroup.extraAttributes.length > 0) {
            extraAttributesMediator.extraAttributes = _accountManagementProxy.currentGroup.extraAttributes;
            extraAttributesMediator.bindForm();
        }

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var newGroupDef:Group = new Group();
        newGroupDef.name = view.groupName.text;
        newGroupDef.description = view.groupDescription.text;

        extraAttributesMediator.bindModel();
        newGroupDef.extraAttributes = extraAttributesMediator.extraAttributes;

        _editedGroup = newGroupDef;
        _editedGroup.id = _accountManagementProxy.currentGroup.id;
    }

    private function showTabForComponent(comp:UIComponent):void {
        for each (var tab:NavigatorContent in view.tabNav.getChildren()) {
            if (tab.contains(comp))
                view.tabNav.selectedChild = tab;
        }
    }

    private function onSubmitEditGroup(event:MouseEvent):void {
        _processingStarted = true;

        if (validate(true) && extraAttributesMediator.validate(true)) {
            sendNotification(ProcessingMediator.START);
            bindModel();
            sendNotification(ApplicationFacade.EDIT_GROUP, _editedGroup);
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
            for each (var valdatorExtra:Validator in extraAttributesMediator.getValidators) {
                if (valdatorExtra.source.errorString != "") {
                    showTabForComponent(valdatorExtra.source as UIComponent);
                    extraAttributesMediator.view.focusManager.setFocus(valdatorExtra.source as IFocusManagerComponent);
                }
            }
        }
    }

    private function initGeneralSection(event:FlexEvent):void {
        view.focusManager.setFocus(view.groupName);
    }

    private function initExtraSection(event:FlexEvent):void {

    }

    public function handleEditGroupSuccess():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.LIST_GROUPS);
    }

    public function handleEditGroupFailure():void {
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error updating group.");
    }
    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function resetForm():void {
        view.groupName.text = "";
        view.groupDescription.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():EditGroupForm
    {
        return viewComponent as EditGroupForm;
    }

}
}
