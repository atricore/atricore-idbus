package com.atricore.idbus.console.modeling.diagram.event {
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class VEdgeRemoveEvent extends VGraphEvent {

    /**
     * This event type signals that a VEdge has been removed.
     * */
    public static const VEDGE_REMOVE:String = "vedgeRemove";

    private var _data:Object;

    public function VEdgeRemoveEvent(type:String, data:Object, bubbles:Boolean=false, cancelable:Boolean=false, subtype:uint = VEST_DEFAULT) {
        super(type, bubbles, cancelable, subtype);
        _data = data;
    }

    public function get data():Object {
        return _data;
    }
}
}