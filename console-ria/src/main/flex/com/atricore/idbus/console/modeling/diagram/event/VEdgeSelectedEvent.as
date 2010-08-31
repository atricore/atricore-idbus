package com.atricore.idbus.console.modeling.diagram.event {
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class VEdgeSelectedEvent extends VGraphEvent {

    /**
     * This event type signals that a graph Edge has been selected.
     * */
    public static const VEDGE_SELECTED:String = "vedgeSelected";

    private var _edge:IEdge;

    public function VEdgeSelectedEvent(type:String, edge:IEdge, bubbles:Boolean=false, cancelable:Boolean=false, subtype:uint = VEST_DEFAULT) {
        super(type, bubbles, cancelable, subtype);
        _edge = edge;
    }

    public function get edge():IEdge {
        return _edge;
    }
}
}