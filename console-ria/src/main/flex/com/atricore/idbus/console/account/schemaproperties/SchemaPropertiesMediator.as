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

package com.atricore.idbus.console.account.schemaproperties {
import com.atricore.idbus.console.account.propertysheet.schema.ShemasAttributePropertiesSection;
import com.atricore.idbus.console.components.CustomViewStack;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.schema.Attribute;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Group;
import spark.components.TabBar;
import spark.events.IndexChangeEvent;

public class SchemaPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var _schemaPropertiesTabBar:TabBar;
    private var _schemaPropertiesSheetsViewStack:CustomViewStack;
    private var _schemaGeneralSection:ShemasAttributePropertiesSection;

    private var _currentSchema:Attribute;

    public function SchemaPropertiesMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
        }

        super.setViewComponent(p_viewComponent);
        init();
    }

    private function init():void {
        _schemaPropertiesSheetsViewStack = view.schemaPropertiesSheetsViewStack;
        _schemaPropertiesTabBar = view.schemaPropertiesTabBar;

        _schemaPropertiesTabBar.selectedIndex = 0;
        _schemaPropertiesTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
    }

    private function stackChanged(event:IndexChangeEvent):void {
        _schemaPropertiesSheetsViewStack.selectedIndex = _schemaPropertiesTabBar.selectedIndex;
    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.DISPLAY_SCHEMA_PROPERTIES
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_SCHEMA_PROPERTIES:
                enablePropertyTabs();
                _currentSchema = notification.getBody() as Attribute;
                showschemaPropertiresTab();
                break;
        }
    }

    protected function showschemaPropertiresTab():void {
        _schemaPropertiesSheetsViewStack.removeAllChildren();

        var schemaPropertyTab:Group = new Group();
        schemaPropertyTab.id = "schemasPropertiesSheetsGeneralSection";
        schemaPropertyTab.name = "General";
        schemaPropertyTab.width = Number("100%");
        schemaPropertyTab.height = Number("100%");
        schemaPropertyTab.setStyle("borderStyle", "solid");

        _schemaGeneralSection = new ShemasAttributePropertiesSection();
        schemaPropertyTab.addElement(_schemaGeneralSection);
        _schemaPropertiesSheetsViewStack.addNewChild(schemaPropertyTab);
        _schemaPropertiesTabBar.selectedIndex = 0;

        _schemaGeneralSection.addEventListener(FlexEvent.CREATION_COMPLETE, handlePropertyTabCreationComplete);
        schemaPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleGeneralPropertyTabRollOut);

    }

    private function handlePropertyTabCreationComplete(event:Event):void {
        _schemaGeneralSection.schEntity.text = formatFieldString(_currentSchema.entity);
        _schemaGeneralSection.schAttribute.text = formatFieldString(_currentSchema.name);
        _schemaGeneralSection.schType.text = formatFieldString(_currentSchema.type.toString());
        _schemaGeneralSection.schRequired.text = formatFieldString(_currentSchema.required.toString());
        _schemaGeneralSection.schMultivalued.text = formatFieldString(_currentSchema.multivalued.toString());
    }

    private function handleGeneralPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function enablePropertyTabs():void {
        _schemaPropertiesTabBar.visible = true;
        _schemaPropertiesSheetsViewStack.visible = true;
    }

    protected function clearPropertyTabs():void {
        _schemaPropertiesSheetsViewStack.removeAllChildren();
        _schemaPropertiesSheetsViewStack.visible = false;
        _schemaPropertiesTabBar.visible = false;
    }

    private function formatFieldString(str:String):String {
        if (str != null && str.length > 0)
            return str;
        else
            return "---";
    }

    protected function get view():SchemaPropertiesView
    {
        return viewComponent as SchemaPropertiesView;
    }

}
}