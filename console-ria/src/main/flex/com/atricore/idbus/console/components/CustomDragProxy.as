package com.atricore.idbus.console.components {
import flash.display.DisplayObject;

import mx.controls.Image;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.UIComponent;

public class CustomDragProxy extends UIComponent {

    public function CustomDragProxy() {
        super();
    }

    override protected function createChildren():void {
        super.createChildren();

        var dg:CustomDataGrid = CustomDataGrid(owner);
        var items:Array = dg.selectedIndices;

        var container:UIComponent = new UIComponent();
        addChild(DisplayObject(container));

        var image:Image = new Image();
        image.source = dg.dragIcon;
        image.width = image.height = 32;
        container.addChild(image);
        
        var src:IListItemRenderer = dg.indexToItemRenderer(items[0]);
        x = src.mouseX - 20;
        y = src.y;
    }
}
}