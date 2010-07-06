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
import mx.containers.Canvas;

/**
	 * @author MBz
	 */	
	public class WizardStep extends Canvas implements IWizardStep {
		private var _stepName:String;
		private var _stepDescription:String;
		private var _isValid:Boolean;
		private var _dataField:String;
        private var _jumpToStep:String;
        private var _stepId:String;
        private var _previousStep:String;
        private var _previousStepData:*;
		
		public function WizardStep() {
			super();
            _previousStep = null;
		}
		
		public function get stepName():String {
			return _stepName;
		}
		
		public function set stepName(value:String):void	{
			_stepName = value;
		}
		
		public function get stepDescription():String {
			return _stepDescription;
		}
		
		public function set stepDescription(value:String):void {
			_stepDescription = value;
		}
		[Bindable]
		public function get isValid():Boolean {
			return _isValid;
		}
		
		public function set isValid(value:Boolean):void	{
			_isValid = value;
		}
		
		public function get dataField():String {
			return _dataField;
		}
		
		public function set dataField(value:String):void {
			_dataField = value;
		}
		
		public function get stepDecision():* {
			return null;
		}
		
		public function get readableStepDecision():String {
			return null;	
		}

        //This needs to be overridden in every child class using jumpToStep property!
        public function get isJumpToConditionMet():Boolean {
            return false;
        }

        public function get stepId():String {
            return _stepId;
        }

        public function set stepId(value:String):void {
            _stepId = value;
        }

        public function get jumpToStep():String {
            return _jumpToStep;
        }

        public function set jumpToStep(value:String):void {
            _jumpToStep = value;
        }

        public function get previousStep():String {
            return _previousStep;
        }

        public function set previousStep(value:String):void {
            _previousStep = value;
        }

        public function set previousStepData(value:*):void {
            _previousStepData = value;
        }

        public function get previousStepData():* {
            return _previousStepData;
        }
    }
}