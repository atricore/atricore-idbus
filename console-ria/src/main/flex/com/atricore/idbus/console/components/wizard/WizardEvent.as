/*
  ~ Atricore IDBus
  ~
  ~ Copyright 2009, Atricore Inc.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

/*
Copyright 2009 Flashmattic, Matti Bar-Zeev

Licensed under the Apache License,Version 2.0 (the "License"); you may not use 
this file except in compliance with the License. You may obtain a copy of the 
License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
CONDITIONS OF ANY KIND, either express or implied. See the License for the 
specific language governing permissions and limitations under the License. 
*/
package  com.atricore.idbus.console.components.wizard {
import flash.events.Event;

/**
	 * @author MBz
	 */	
	public class WizardEvent extends Event {
		public static const NEXT_STEP:String = "nextStep";
		public static const PREV_STEP:String = "prevStep";
		public static const WIZARD_COMPLETE:String = "wizardComplete";
		public static const WIZARD_CANCEL:String = "wizardCancel";
		
		public function WizardEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
		}
	}
}