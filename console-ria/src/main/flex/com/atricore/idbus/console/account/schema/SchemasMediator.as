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

package com.atricore.idbus.console.account.schema {
import com.atricore.idbus.console.account.main.controller.ListSchemaAttributesCommand;
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.account.main.view.AccountManagementPopUpManager;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.schema.Attribute;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.events.ListEvent;
import mx.managers.PopUpManager;
import mx.messaging.management.Attribute;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class SchemasMediator extends IocMediator implements IDisposable{

    public static const BUNDLE:String = "console";

    //private var _popupManager:AccountManagementPopUpManager;
    private var _schemasManagementProxy:SchemasManagementProxy;
    private var _schemasPropertiesMediator:IIocMediator;

    private var _updatedSchemaAttrIndex:Number;

    public function SchemasMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
        view.btnCreateAttribute.addEventListener(MouseEvent.CLICK, handleCreateAttributeClick);
        view.btnEditAttribute.addEventListener(MouseEvent.CLICK, handleEditAttributeClick);
        view.btnDeleteAttribute.addEventListener(MouseEvent.CLICK, handleDeleteAttributeClick);

        view.schemaAttrList.addEventListener(ListEvent.ITEM_CLICK , schemaAttrListClickHandler);

        sendNotification(ApplicationFacade.LIST_SCHEMA_ATTRIBUTES);
        //schemasPropertiesMediator.setViewComponent(view.properties);
        //popupManager.init(iocFacade, view);
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null
        view.btnCreateAttribute.removeEventListener(MouseEvent.CLICK, handleCreateAttributeClick);
        view.btnEditAttribute.removeEventListener(MouseEvent.CLICK, handleEditAttributeClick);
        view.btnDeleteAttribute.removeEventListener(MouseEvent.CLICK, handleDeleteAttributeClick);

        view.schemaAttrList.removeEventListener(ListEvent.ITEM_CLICK , schemaAttrListClickHandler);

        view = null;
    }

    override public function listNotificationInterests():Array {
        return [ListSchemaAttributesCommand.SUCCESS,
            ListSchemaAttributesCommand.FAILURE
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ListSchemaAttributesCommand.SUCCESS:
                view.schemaAttrList.dataProvider = schemasManagementProxy.schemaAttributeList;
                if (_updatedSchemaAttrIndex != -1)  {
                    view.schemaAttrList.selectedIndex = _updatedSchemaAttrIndex;
                    _updatedSchemaAttrIndex = -1;
                }
                else {
                    view.schemaAttrList.selectedIndex = schemasManagementProxy.schemaAttributeList.length - 1;
                }
                // dispatch index change.
                view.schemaAttrList.dispatchEvent(
                        new ListEvent(ListEvent.ITEM_CLICK, false, false,view.schemaAttrList.selectedIndex,-1,null,null)
                        );
                view.validateNow();
                break;
            case ListSchemaAttributesCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "There was an error getting group list.");
                break;
        }
    }


    private function handleCreateAttributeClick(event:MouseEvent):void {
        trace("New Group Button Click: " + event);
        //sendNotification(ApplicationFacade.DISPLAY_ADD_NEW_GROUP);
    }

    private function handleEditAttributeClick(event:MouseEvent):void {
        trace("Edit Group Button Click: " + event);
        //sendNotification(ApplicationFacade.DISPLAY_EDIT_GROUP);
    }

    private function handleDeleteAttributeClick(event:MouseEvent):void {
        trace("Delete Group Button Click: " + event);
        showConfirmDeleteAlert(event);
    }

    public function schemaAttrListClickHandler(e:ListEvent):void {
        var selectedAttr:com.atricore.idbus.console.services.dto.schema.Attribute =
                e.currentTarget.selectedItem as com.atricore.idbus.console.services.dto.schema.Attribute;
        schemasManagementProxy.currentSchemaAttribute = selectedAttr;

        if (view.btnDeleteAttribute != null)
            view.btnDeleteAttribute.enabled = true;

        if (view.btnEditAttribute != null)
            view.btnEditAttribute.enabled = true;

        //view.properties.visible = true;
        //sendNotification(ApplicationFacade.DISPLAY_GROUP_PROPERTIES, selectedAttr);
    }

    private function attrBasicInfo(attr:com.atricore.idbus.console.services.dto.schema.Attribute):String {
        var attrInfo:String = "";
        var resMan:IResourceManager = ResourceManager.getInstance();
        //attrInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.name') + ": " + group.name + "\n";
        //attrInfo+=resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.description') + ": " + group.description + "\n";

        return attrInfo;
    }

    private function showConfirmDeleteAlert(event:Event):void {
        var resMan:IResourceManager = ResourceManager.getInstance();

        if (view.schemaAttrList.selectedIndex == -1)
            Alert.show(resMan.getString(AtricoreConsole.BUNDLE , 'provisioning.error.group.noselected'));
        else {
            var alertBody:String = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.delete.answer');
            alertBody += "\n" + attrBasicInfo(schemasManagementProxy.currentSchemaAttribute);
            var delAlert:Alert = Alert.show(alertBody,
                    resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.groups.delete.title'),
                    3, view,
                                           function(event:CloseEvent):void {
                                               if (event.detail == Alert.YES) {
                                                   sendNotification(ApplicationFacade.DELETE_GROUP, schemasManagementProxy.currentSchemaAttribute);
                                                   sendNotification(ProcessingMediator.START);
                                               }
                                               else
                                                   PopUpManager.removePopUp(delAlert);
                                           });

        }
    }

    protected function get view():SchemasView
    {
        return viewComponent as SchemasView;
    }

    protected function set view(gv:SchemasView):void
    {
        viewComponent = gv;
    }

    public function get schemasPropertiesMediator():IIocMediator {
        return _schemasPropertiesMediator;
    }

    public function set schemasPropertiesMediator(value:IIocMediator):void {
        _schemasPropertiesMediator = value;
    }
}
}