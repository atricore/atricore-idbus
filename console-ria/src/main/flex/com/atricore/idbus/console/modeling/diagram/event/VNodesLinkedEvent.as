package com.atricore.idbus.console.modeling.diagram.event {
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class VNodesLinkedEvent extends VGraphEvent {

    /**
     * This event type signals that two VNodes are linked.
     * */
    public static const FEDERATED_CONNECTION_CREATED:String = "vnodesLinked";
    public static const ACTIVATION_CREATED:String = "activationCreated";
    public static const IDENTITY_LOOKUP_CREATED:String = "identityLookupCreated";
    public static const LINKING_CANCELED:String = "linkingCanceled";

    private var _vnode1:IVisualNode;

    private var _vnode2:IVisualNode;

    public function VNodesLinkedEvent(type:String, vnode1:IVisualNode, vnode2:IVisualNode, bubbles:Boolean=false, cancelable:Boolean=false, subtype:uint = VEST_DEFAULT) {
        super(type, bubbles, cancelable, subtype);
        _vnode1 = vnode1;
        _vnode2 = vnode2;
    }

    public function get vnode1():IVisualNode {
        return _vnode1;
    }

    public function get vnode2():IVisualNode {
        return _vnode2;
    }
}
}