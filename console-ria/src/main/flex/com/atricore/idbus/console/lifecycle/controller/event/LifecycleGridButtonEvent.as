package com.atricore.idbus.console.lifecycle.controller.event
{
import flash.events.Event;

public class LifecycleGridButtonEvent extends Event
{
    public static const CLICK:String = "LifecycleGridButtonEvent.CLICK";

    public static const ACTION_EDIT:int = 0;
    public static const ACTION_REMOVE:int = 1;
    public static const ACTION_START:int = 2;
    public static const ACTION_STOP:int = 3;
    public static const ACTION_UNDEPLOY:int = 4;


    private var _data:Object;
    private var _action:int;

    public function LifecycleGridButtonEvent(bubbles:Boolean = false, cancelable:Boolean = false, data:Object = null, action:int = LifecycleGridButtonEvent.ACTION_EDIT)
    {
        super(LifecycleGridButtonEvent.CLICK, bubbles, cancelable);
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
        return new LifecycleGridButtonEvent(bubbles, cancelable, data, action);
    }
}
}