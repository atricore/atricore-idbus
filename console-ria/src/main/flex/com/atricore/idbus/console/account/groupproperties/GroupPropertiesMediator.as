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
import com.atricore.idbus.console.account.propertysheet.group.GroupGeneralSection;
import com.atricore.idbus.console.components.CustomViewStack;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.Group;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.FlexEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Group;
import spark.components.TabBar;
import spark.events.IndexChangeEvent;

public class GroupPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var _groupPropertiesTabBar:TabBar;
    private var _groupPropertiesSheetsViewStack:CustomViewStack;
    private var _groupGeneralSection:GroupGeneralSection;

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

        _groupPropertiesTabBar.selectedIndex = 0;
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
                enablePropertyTabs();
                _currentGroup = notification.getBody() as com.atricore.idbus.console.services.dto.Group;
                showGroupPropertiresTab();
                break;
        }
    }

    protected function showGroupPropertiresTab():void {
        _groupPropertiesSheetsViewStack.removeAllChildren();

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

    protected function get view():GroupPropertiesView
    {
        return viewComponent as GroupPropertiesView;
    }

}
}