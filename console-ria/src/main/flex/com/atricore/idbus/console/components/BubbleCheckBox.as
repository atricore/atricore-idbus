
package com.atricore.idbus.console.components {
import flash.events.Event;

import mx.controls.CheckBox;

public class BubbleCheckBox extends CheckBox
    {
         public function BubbleCheckBox(){
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