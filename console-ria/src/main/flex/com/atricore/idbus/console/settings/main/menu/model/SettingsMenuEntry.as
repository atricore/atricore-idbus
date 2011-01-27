/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.menu.model {

public class SettingsMenuEntry {

    private var _label:String;
    private var _icon:Class;
    private var _shortDescription:String;
    private var _mediatorName:String;
    private var _viewName:String;
    private var _viewPriority:int = 0;

    public function SettingsMenuEntry() {
    }

    public function get label():String {
        return _label;
    }

    public function set label(value:String):void {
        _label = value;
    }

    public function get icon():Class {
        return _icon;
    }

    public function set icon(value:Class):void {
        _icon = value;
    }

    public function get shortDescription():String {
        return _shortDescription;
    }

    public function set shortDescription(value:String):void {
        _shortDescription = value;
    }

    public function get mediatorName():String {
        return _mediatorName;
    }

    public function set mediatorName(value:String):void {
        _mediatorName = value;
    }

    public function get viewName():String {
        return _viewName;
    }

    public function set viewName(value:String):void {
        _viewName = value;
    }

    public function get viewPriority():int {
        return _viewPriority;
    }

    public function set viewPriority(value:int):void {
        _viewPriority = value;
    }

    public function toString():String {
        return "[Settings Menu Entry: label=" + label + ", icon=" + icon  + ", shortDescription=" + shortDescription + "]";
    }
}
}
