package com.atricore.idbus.console.components {
import mx.controls.menuClasses.MenuBarItem;

public class IconMenuBarItemRenderer extends MenuBarItem  {

    override protected function measure():void
    {
        super.measure();
        measuredHeight = 28;
        measuredWidth = 10 + 16 + 10 + label.width + 15;
    }

    override protected function updateDisplayList(w:Number, h:Number):void{
        super.updateDisplayList(w,h);
        icon.width = 16;
        icon.height = 16;
        icon.x = label.x - icon.width - 15;
        icon.y = label.y;
    }
}
}
