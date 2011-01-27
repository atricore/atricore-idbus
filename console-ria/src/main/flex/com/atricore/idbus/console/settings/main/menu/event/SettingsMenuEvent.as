/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.menu.event {
import flash.events.Event;

public class SettingsMenuEvent extends Event {

    public static const CLICK:String = "SettingsMenuEvent.CLICK";

    public static const ACTION_MENU_ITEM_CLICKED:int = 0;

    private var _data:Object;
    private var _action:int;

    public function SettingsMenuEvent(bubbles:Boolean = false, cancelable:Boolean = false, data:Object = null, action:int = ACTION_MENU_ITEM_CLICKED) {
        super(SettingsMenuEvent.CLICK, bubbles, cancelable);
        this._data = data;
        this._action = action;
    }

    public function get data():Object {
        return this._data;
    }

    public function get action():int {
        return this._action;
    }

    override public function clone():Event {
        return new SettingsMenuEvent(bubbles, cancelable, data, action);
    }
}
}
