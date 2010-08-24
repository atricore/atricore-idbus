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
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.Group;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class GroupPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    public function GroupPropertiesMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {

        }

        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.DISPLAY_GROUP_PROPERTIES
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_GROUP_PROPERTIES:
                var grp:Group = notification.getBody() as Group;;
                showPropertiresForGroup(grp);
                break;
        }
    }

    private function showPropertiresForGroup(group:Group) {
        view.groupName.text = formatFieldString(group.name);
        view.groupDescription.text = formatFieldString(group.description);
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