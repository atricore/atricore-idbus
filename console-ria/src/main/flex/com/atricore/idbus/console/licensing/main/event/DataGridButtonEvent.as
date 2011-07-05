package com.atricore.idbus.console.licensing.main.event {
import flash.events.Event;

public class DataGridButtonEvent extends Event
	{
		public static const BUTTON_CLICKED : String = "button_clicked";

		private var _data : *;

		// notice that this event is set to bubble ( the "true" arg in the super call)
		public function DataGridButtonEvent ( type : String, data : * = null )
		{
			super ( type, true );
			_data = data;
		}

		public function get data ( ) : * { return _data; }
	}
}