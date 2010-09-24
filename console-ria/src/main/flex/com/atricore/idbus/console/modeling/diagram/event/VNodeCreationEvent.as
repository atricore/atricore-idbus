package com.atricore.idbus.console.modeling.diagram.event {
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class VNodeCreationEvent extends VGraphEvent {
    
    /**
     * This event type signals that a user clicked on the diagram
     * when he's in the nodeCreationMode and dialog needs to be opened.
     * */
    public static const OPEN_CREATION_FORM:String = "openCreationForm";
    
    private var _elementType:int;

    public function VNodeCreationEvent(type:String, elementType:int, bubbles:Boolean=false, cancelable:Boolean=false, subtype:uint = VEST_DEFAULT) {
        super(type, bubbles, cancelable, subtype);
        _elementType = elementType;
    }

    public function get elementType():int {
        return _elementType;
    }
}
}