package com.atricore.idbus.console.components {
import mx.controls.AdvancedDataGrid;
import mx.controls.dataGridClasses.DataGridDragProxy;
import mx.core.IUIComponent;

public class CustomDataGrid extends AdvancedDataGrid {

    [Bindable]
    public var dragProxyImage:Class = DataGridDragProxy; //set the default value to the standard DataGridDragProxy class

    [Bindable]
    public var dragIcon:Class;

    public function CustomDataGrid() {
        super();
    }

    override protected function get dragImage():IUIComponent {
        var image:IUIComponent = new dragProxyImage();
        image.owner = this;
        return image;
    }

}
}