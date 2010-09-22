package com.atricore.idbus.console.components {
import flash.display.DisplayObject;

import mx.controls.menuClasses.MenuBarItem;

public class IconMenuBarItemRenderer extends MenuBarItem
{
    private var iconClass:Class;

    override protected function commitProperties():void
    {
        super.commitProperties();

        if (data)
        {
            iconClass = menuBar.itemToIcon(data);
            if (iconClass)
            {
                icon = new iconClass();
                icon.width = 16;
                icon.height = 16;
                addChild(DisplayObject(icon));
            }
        }
    }
}
}