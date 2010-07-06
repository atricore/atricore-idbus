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
package com.atricore.idbus.console.components.wizard {
import mx.core.IChildList;
import mx.core.IUIComponent;

/**
	 * @author MBz
	 */	
	public interface IWizardStep extends IChildList, IUIComponent {
		/**
		 * This is the step name that will appear on the summary of the wizard
		 * when all the decisions will be displayed 
		 */		
		function get stepName():String;
		function set stepName(value:String):void;
		
		/**
		 * This is the description of the step. This string will appear at the 
		 * top, indicating instructions or whatever comes to mind 
		 */		
		function get stepDescription():String;
		function set stepDescription(value:String):void;
		
		/**
		 * Tells whether the step is valid and we can move on to the next step 
		 */		
		[Bindable("propertyChange")]
		function get isValid():Boolean;
		function set isValid(value:Boolean):void;
		
		/**
		 * This is the property name on which the data for this step would be
		 * saved on the data model given to the Wizard 
		 */		
		function get dataField():String;
		function set dataField(value:String):void;
		
		/**
		 * Returns the "decision" the current step has ended up with
		 * @return	Any type you can think of
		 */		
		function get stepDecision():*;
		
		/**
		 * Returns a string that represents the step decision in a readable 
		 * format. We use it for the end slide of the Wizard to describe the 
		 * step decision 
		 */		
		function get readableStepDecision():String;

        /**
         * Returns boolean value telling us whether we need to go to conditional step
         * or continue with the next step
         * @return
         */
        function get isJumpToConditionMet():Boolean;

        /**
         * Returns and id of the step to jump to if the condition is met
         * @return
         */
        function get jumpToStep():String;

        function set jumpToStep(value:String):void;

        function get stepId():String;

        function set stepId(value:String):void;

        /**
         * Returns an id of the step from which we came to this step
         * @return
         */
        function get previousStep():String;

        function set previousStep(value:String):void;

        function set previousStepData(value:*):void;

        function get  previousStepData():*;
	}
}