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

package com.atricore.idbus.console.account.groupproperties {
import com.atricore.idbus.console.account.main.model.SchemasManagementProxy;
import com.atricore.idbus.console.account.propertysheet.extraattributes.ExtraAttributesSection;
import com.atricore.idbus.console.account.propertysheet.group.GroupGeneralSection;
import com.atricore.idbus.console.components.CustomViewStack;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.Group;
import com.atricore.idbus.console.services.dto.schema.Attribute;
import com.atricore.idbus.console.services.dto.schema.AttributeValue;
import com.atricore.idbus.console.services.dto.schema.TypeDTOEnum;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.containers.FormItem;
import mx.controls.List;
import mx.events.FlexEvent;
import mx.formatters.DateFormatter;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.ButtonBar;
import spark.components.Group;
import spark.components.Label;
import spark.events.IndexChangeEvent;

public class GroupPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _groupPropertiesTabBar:ButtonBar;
    private var _groupPropertiesSheetsViewStack:CustomViewStack;
    private var _groupGeneralSection:GroupGeneralSection;
    private var _extraAttributesSection:ExtraAttributesSection;

    private var _schemasManagementProxy:SchemasManagementProxy;

    private var _currentGroup:com.atricore.idbus.console.services.dto.Group;

    public function GroupPropertiesMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {
        }

        super.setViewComponent(p_viewComponent);
        init();
    }

    private function init():void {
        _groupPropertiesSheetsViewStack = view.groupPropertiesSheetsViewStack;
        _groupPropertiesTabBar = view.groupPropertiesTabBar;

        _groupPropertiesTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
    }

    private function stackChanged(event:IndexChangeEvent):void {
        _groupPropertiesSheetsViewStack.selectedIndex = _groupPropertiesTabBar.selectedIndex;
    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.DISPLAY_GROUP_PROPERTIES
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_GROUP_PROPERTIES:
                _groupPropertiesSheetsViewStack.removeAllChildren();
                enablePropertyTabs();
                _currentGroup = notification.getBody() as com.atricore.idbus.console.services.dto.Group;
                showGroupPropertiresTab();
                showGroupExtraAttributesTab();
                _groupPropertiesTabBar.selectedIndex = 0;
                break;
        }
    }

    protected function showGroupPropertiresTab():void {

        var groupPropertyTab:spark.components.Group = new spark.components.Group();
        groupPropertyTab.id = "groupsPropertiesSheetsGeneralSection";
        groupPropertyTab.name = "General";
        groupPropertyTab.width = Number("100%");
        groupPropertyTab.height = Number("100%");
        groupPropertyTab.setStyle("borderStyle", "solid");

        _groupGeneralSection = new GroupGeneralSection();
        groupPropertyTab.addElement(_groupGeneralSection);
        _groupPropertiesSheetsViewStack.addNewChild(groupPropertyTab);
        _groupPropertiesTabBar.selectedIndex = 0;

        _groupGeneralSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleGeneralPropertyTabCreationComplete);
        groupPropertyTab.addEventListener(MouseEvent.ROLL_OUT, handleGeneralPropertyTabRollOut);

    }

    private function handleGeneralPropertyTabCreationComplete(event:Event):void {
        _groupGeneralSection.groupName.text = formatFieldString(_currentGroup.name);
        _groupGeneralSection.groupDescription.text = formatFieldString(_currentGroup.description);
    }

    private function handleGeneralPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    private function showGroupExtraAttributesTab() {
        var extraAttrTab:spark.components.Group = new spark.components.Group();
        extraAttrTab.id = "extraAttributesSectionGroup";
        extraAttrTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.extraattributes');
        extraAttrTab.width = Number("100%");
        extraAttrTab.height = Number("100%");
        extraAttrTab.setStyle("borderStyle", "solid");

        _extraAttributesSection = new ExtraAttributesSection();
        extraAttrTab.addElement(_extraAttributesSection);
        _groupPropertiesSheetsViewStack.addNewChild(extraAttrTab);

        _extraAttributesSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleExtraAttributesTabCreationComplete);
        _extraAttributesSection.addEventListener(MouseEvent.ROLL_OUT, handleExtraAttributesTabRollOut);
    }

    private function handleExtraAttributesTabCreationComplete(event:Event):void {
        var attributesValues:Object = new Object();
        for each (var aVal:AttributeValue in _currentGroup.extraAttributes) {
            var aName:String = aVal.name;
            var arr:Array = attributesValues[aName];
            if (arr == null)
                arr = new Array();
            arr.push(aVal.value);
            attributesValues[aName] = arr;
        }

        if (attributesValues != null) {
            for each (var attVal:AttributeValue in _currentGroup.extraAttributes) {
                var attrDef:Attribute = schemasManagementProxy.getAttributeByName(attVal.name);
                var formItem:FormItem = new FormItem();

                formItem.label = attVal.name+":";

                if (attrDef != null && attrDef.multivalued) {
                    var multiArr:Array = attributesValues[attVal.name];
                    if (multiArr !=null) {
                        var multiList:List = new List();
                        multiList.width = 600;
                        multiList.percentHeight = 50;
                        multiList.dataProvider = new ArrayCollection();
                        for (var i:int=0; i<multiArr.length; i++)
                            multiList.dataProvider.addItem(multiArr[i]);
                        formItem.addElement(multiList);
                        attributesValues[attVal.name] = null;
                        _extraAttributesSection.extraAttrTab.addElement(formItem);
                    }
                }
                else {
                    var valueLabel:Label = new Label();
                    valueLabel.text = formatString(attrDef,attVal);
                    formItem.addElement(valueLabel);
                    _extraAttributesSection.extraAttrTab.addElement(formItem);
                }
            }
        }
    }

    private function formatString(attrDef:Attribute,attVal:AttributeValue):String {
        var formatedValueString:String = "";

        switch (attrDef.type.toString()) {
            case TypeDTOEnum.INT.toString():
                formatedValueString = formatFieldNumber(attVal.value as Number);
                break;
            case TypeDTOEnum.DATE.toString():
                formatedValueString = formatFieldDate(attVal.value as Date);
                break;
            default:
                formatedValueString = formatFieldString(attVal.value);
                break;
        }
        return formatedValueString;
    }

    private function handleExtraAttributesTabRollOut(e:Event):void {
        trace(e);
    }

    protected function enablePropertyTabs():void {
        _groupPropertiesTabBar.visible = true;
        _groupPropertiesSheetsViewStack.visible = true;
    }

    protected function clearPropertyTabs():void {
        _groupPropertiesSheetsViewStack.removeAllChildren();
        _groupPropertiesSheetsViewStack.visible = false;
        _groupPropertiesTabBar.visible = false;
    }

    private function formatFieldString(str:String):String {
        if (str != null && str.length > 0)
            return str;
        else
            return "---";
    }

    private function formatFieldNumber(num:Number):String {
        return num.toString();
    }

    private function formatFieldDate(date:Date):String {
        var formatter:DateFormatter = new DateFormatter();
        var resMan:IResourceManager = ResourceManager.getInstance();
        formatter.formatString = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.DATE_FORMAT');
        return formatter.format(date);
    }

    private function formatFieldBoolean(bol:Boolean):String {
        var resMan:IResourceManager = ResourceManager.getInstance();
        if (bol)
            return resMan.getString(AtricoreConsole.BUNDLE, 'boolean.yes');
        else
            return resMan.getString(AtricoreConsole.BUNDLE, 'boolean.no');
    }

    public function get schemasManagementProxy():SchemasManagementProxy {
        return _schemasManagementProxy;
    }

    public function set schemasManagementProxy(value:SchemasManagementProxy):void {
        _schemasManagementProxy = value;
    }

    protected function get view():GroupPropertiesView
    {
        return viewComponent as GroupPropertiesView;
    }

}
}