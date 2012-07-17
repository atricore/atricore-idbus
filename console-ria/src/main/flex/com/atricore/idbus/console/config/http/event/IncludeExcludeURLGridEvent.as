package com.atricore.idbus.console.config.http.event {
import flash.events.Event;

public class IncludeExcludeURLGridEvent extends Event {
    public static const CLICK:String = "com.atricore.idbus.console.config.http.event.IncludeExcludeURLGridEvent.CLICK";

    public static const ACTION_REMOVE_INCLUDE_URL:int = 0;
    public static const ACTION_REMOVE_EXCLUDE_URL:int = 1;

    private var _data:Object;
    private var _action:int;

    public function IncludeExcludeURLGridEvent(bubbles:Boolean = false, cancelable:Boolean = false, data:Object = null, action:int = IncludeExcludeURLGridEvent.ACTION_REMOVE_INCLUDE_URL)
    {
        super(IncludeExcludeURLGridEvent.CLICK, bubbles, cancelable);
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
        return new IncludeExcludeURLGridEvent(bubbles, cancelable, data, action);
    }
}
}
