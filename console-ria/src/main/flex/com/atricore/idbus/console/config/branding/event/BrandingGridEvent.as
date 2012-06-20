package com.atricore.idbus.console.config.branding.event {
import flash.events.Event;

public class BrandingGridEvent extends Event {
    public static const CLICK:String = "com.atricore.idbus.console.config.branding.event.BrandingGridEvent.CLICK";

    public static const ACTION_EDIT:int = 0;
    public static const ACTION_REMOVE:int = 1;

    private var _data:Object;
    private var _action:int;

    public function BrandingGridEvent(bubbles:Boolean = false, cancelable:Boolean = false, data:Object = null, action:int = BrandingGridEvent.ACTION_EDIT)
    {
        super(BrandingGridEvent.CLICK, bubbles, cancelable);
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
        return new BrandingGridEvent(bubbles, cancelable, data, action);
    }
}
}
