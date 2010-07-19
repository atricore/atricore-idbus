
package com.atricore.idbus.console.components {
import flash.events.Event;

import mx.controls.TextArea;

public class BubbleTextArea extends TextArea
    {
         public function BubbleTextArea(){
              super();
              addEventListener(Event.CHANGE, bubbleEvent);
         }

        protected function bubbleEvent(evtChange:Event):void {
            removeEventListener(Event.CHANGE, bubbleEvent);
            dispatchEvent(new Event(Event.CHANGE, true));
            addEventListener(Event.CHANGE, bubbleEvent);
        }
}
}