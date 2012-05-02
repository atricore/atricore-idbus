package com.atricore.idbus.console.config.http.event {
import flash.events.Event;

public class BindAddressGridEvent extends Event {
    public static const CLICK:String = "com.atricore.idbus.console.config.http.event.BindAddressGridEvent.CLICK";

    public static const ACTION_REMOVE:int = 0;

    private var _data:Object;
    private var _action:int;

    public function BindAddressGridEvent(bubbles:Boolean = false, cancelable:Boolean = false, data:Object = null, action:int = BindAddressGridEvent.ACTION_REMOVE)
    {
        super(BindAddressGridEvent.CLICK, bubbles, cancelable);
        this._data = data;
        this._action = action;
    }

    public function get data():Object
    {
        return this._data;
    }

    public function get action():int
    {
        return this._action;
    }

    override public function clone():Event
    {
        return new BindAddressGridEvent(bubbles, cancelable, data, action);
    }
}
}
