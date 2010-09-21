package com.atricore.idbus.console.components {
public class ListItemValueObject {
    [Bindable]
    public var label:String;

    [Bindable]
    public var isSelected:Boolean;

    public var data:String;

    public function ListItemValueObject() {
        super();
    }
}
}