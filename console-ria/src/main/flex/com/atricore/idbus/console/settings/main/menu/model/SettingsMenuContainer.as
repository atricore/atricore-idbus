/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.menu.model {

public class SettingsMenuContainer extends SettingsMenuEntry {
    
    private var _children:Array;

    public function SettingsMenuContainer() {
        super();
        _children = new Array();
    }

    public function add(entry:SettingsMenuEntry, ...args):void {
        if (args[0] is Number) {
            _children[args[0]] = entry;
        } else {
            _children[_children.length] = entry;
        }
    }

    public function get children():Array {
        return _children;
    }

    public function set children(value:Array):void {
        _children = value;
    }
}
}
