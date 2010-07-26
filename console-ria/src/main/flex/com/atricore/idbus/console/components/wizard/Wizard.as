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
import com.atricore.idbus.console.components.GroupBox;

import flash.display.DisplayObject;
import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.binding.utils.ChangeWatcher;
import mx.containers.BoxDirection;
import mx.containers.ControlBar;
import mx.containers.TitleWindow;
import mx.containers.ViewStack;
import mx.controls.Button;
import mx.controls.Text;
import mx.controls.TextArea;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.resources.ResourceManager;

use namespace mx_internal;
	
	/**
	 *  Dispatched when the wizard is done
	 *
	 *  @eventType 	com.flashmattic.component.wizard.WizardEvent.WIZARD_COMPLETE
	 *  @tiptext 	wizard complete event
	 */
	[Event(name="wizardComplete", type="com.atricore.idbus.console.components.wizard.WizardEvent")]
	
	/**
	 *  Dispatched when the wizard is canceled
	 *
	 *  @eventType 	com.flashmattic.component.wizard.WizardEvent.WIZARD_CANCEL
	 *  @tiptext 	wizard complete event
	 */
	[Event(name="wizardCancel", type="com.atricore.idbus.console.components.wizard.WizardEvent")]
	
	/**
	 *  Dispatched when the wizard has moved to the next step
	 *
	 *  @eventType 	com.flashmattic.component.wizard.WizardEvent.NEXT_STEP
	 *  @tiptext 	wizard complete event
	 */
	[Event(name="nextStep", type="com.atricore.idbus.console.components.wizard.WizardEvent")]
	
	/**
	 *  Dispatched when the wizard has moved to the previous step
	 *
	 *  @eventType 	com.flashmattic.component.wizard.WizardEvent.PREV_STEP
	 *  @tiptext 	wizard complete event
	 */
	[Event(name="prevStep", type="com.atricore.idbus.console.components.wizard.WizardEvent")]
	
	/**
	 * Use the FlashmatticomponentsLabelsResourceBundle for this class
	 */	
	[ResourceBundle("FlashmatticomponentsLabelsResourceBundle")]
	
	/**
	 * @author MBz
	 */	
	public class Wizard extends TitleWindow {
		private const WIZARD_MIN_WIDTH:Number = 300;
		private const WIZARD_MIN_HEIGHT:Number = 200;
		private var _previousButton:Button;
		private var _nextButton:Button;
		private var _cancelButton:Button;
		private var _dataModel:*;
		private var _steps:Array;
		private var _stepDescriptionsArray:Array;
		private var _stepDescriptionText:Text;
		private var _stepsViewStack:ViewStack;
		private var _currentStepIndex:int;
		private var _stepsMaxWidth:Number = WIZARD_MIN_WIDTH;
		private var _stepsMaxHeight:Number = WIZARD_MIN_HEIGHT;
		private var _stepValidationWatcher:ChangeWatcher;
		private var _currentStep:IWizardStep;
		private var _showSummaryStep:Boolean;
		private var _summaryStep:IWizardStep;
		private var _summaryText:TextArea;
		private var _summarArray:Array;
		private var _summaryString:String;
		private var _showCancelButton:Boolean = true;
		// Commit properties flags
		private var _wizardStepsChanged:Boolean = false

        public static const SUMMARY_STEP_ID:String = "summary";
				
		public function Wizard() {
			super();
			// This component has a closing button always. This button does what
			// canceling the whole wizard do.			
			showCloseButton = true;
			addEventListener(CloseEvent.CLOSE, onClose);
			// Reset the current step to be the first
			currentStepIndex = 0;
			// Set the minimum scale of this wizard
			minWidth = WIZARD_MIN_WIDTH;
			minHeight = WIZARD_MIN_HEIGHT;
			// Reset the _summarArray 
			_summarArray = new Array();
			// Create the summary step although we're not sure we are actually 
			// going to use it
			createSummaryStep();
		}
		
		// OVERRIDES ------------------------------------------------------------
		override protected function createChildren():void {
			super.createChildren();
			// Create the control bar for the wizard
			initWizardControlBar();
			// Create the step description text control
			if (_stepDescriptionText == null) {
				_stepDescriptionText = new Text();
				_stepDescriptionText.percentWidth = 100;
				_stepDescriptionText.text = "Step description here...";
//				addChild(_stepDescriptionText);
			}
			// Create the view stack to hold all the steps content
			if (_stepsViewStack == null) {
				_stepsViewStack = new ViewStack();
				_stepsViewStack.percentWidth = 100;
				_stepsViewStack.percentHeight = 100;
				_stepsViewStack.creationPolicy = "all";
				_stepsViewStack.addEventListener(FlexEvent.CREATION_COMPLETE, onStepsViewStackCreationComplete, false, 1, true);
				addChild(_stepsViewStack);
			}
		}
		
		override protected function commitProperties():void {
			super.commitProperties();
			if (_wizardStepsChanged) {
				initWizardSteps();
				_wizardStepsChanged = false;
			}
		}
		
		/**
		 * The Wizard sets it's own scale according to its steps width and 
		 * height. This is why we don't want the end developer to set the 
		 * scale, since it will only tampre with the auto scaling.
		 * @param value
		 */		
		override public function set width(value:Number):void {
			throw new Error("Width property cannot be set for the Wizard");
		}
		
		/**
		 * The Wizard sets it's own scale according to its steps width and 
		 * height. This is why we don't want the end developer to set the 
		 * scale, since it will only tampre with the auto scaling.
		 * @param value
		 */				
		override public function set height(value:Number):void {
			throw new Error("Height property cannot be set for the Wizard");
		}
		
		// PRIVATES ------------------------------------------------------------
		/**
		 * Lays down the buttons for "next", "previous" and "cancel". Also 
		 * attach event listeners for them. 
		 */		
		private function initWizardControlBar():void {
			// if controlBar doesn't exist, create the controlBar
		    if (controlBar == null) {
		    	controlBar = new ControlBar();
		        //add control bar
		        addChildAt(controlBar as ControlBar, numChildren);
		        ControlBar(controlBar).direction = BoxDirection.HORIZONTAL;
		        ControlBar(controlBar).setStyle("horizontalAlign", "right");
		        createComponentsFromDescriptors();
		    }
		    
		    if (_previousButton == null) {
		    	_previousButton = new Button();
		        _previousButton.addEventListener(MouseEvent.CLICK, onPrevious, false, 0, true);
		        _previousButton.label = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.buttons.previous.label");
		        ControlBar(controlBar).addChild(_previousButton);
		    }
		    
		    // if the button doesn't exist, create button
		    if (_nextButton == null) {
		        _nextButton = new Button();
		        _nextButton.label = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.buttons.next.label");
		        _nextButton.addEventListener(MouseEvent.CLICK, onNext, false, 0, true);
		        ControlBar(controlBar).addChild(_nextButton);
		    }
		    
		    // if the button doesn't exist, create button
		    if (_showCancelButton && _cancelButton == null) {
		        _cancelButton = new Button();
		        _cancelButton.label = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.buttons.cancel.label");
		        _cancelButton.addEventListener(MouseEvent.CLICK, onCancel, false, 0, true);
		        ControlBar(controlBar).addChild(_cancelButton);
		    }
		    
		    showCloseButton = _showCancelButton;
		}
		
		/**
		 * Adds all the steps to the steps view stack, and also sets the array
		 * of description
		 */		
		private function initWizardSteps():void {
			_stepDescriptionsArray = new Array();
			for each (var step:IWizardStep in steps) {
				_stepsViewStack.addChild(step as DisplayObject);
				_stepDescriptionsArray.push(step.stepDescription);
			}
			validateWizardCurrentStep();
		}
		
		private function validateWizardCurrentStep():void {
			// Set the selected child of the steps view stack to the current
			// step of the wizard
			_stepsViewStack.selectedIndex = currentStepIndex;
			// Set the description of the current step
			_stepDescriptionText.text = _stepDescriptionsArray[currentStepIndex];
			// Bind the validation of the step with the next step availability

            var previousStepData:*;
			_currentStep = _stepsViewStack.selectedChild as IWizardStep;

            //we're sending data from previous step to next one
            if(_currentStep.previousStep != null){
                _currentStep.previousStepData = steps[findStepIndexByName(_currentStep.previousStep)].stepDecision;
            }                                   
			// Validate the next and previous step
			validateNextStep();
			validatePrevStep();
			// Setup the watcher for the next button
			if (_stepValidationWatcher) {
				_stepValidationWatcher.unwatch();
			} 
			_stepValidationWatcher = BindingUtils.bindProperty(_nextButton, "enabled", _currentStep, "isValid");
		}
		
		/**
		 * Wraps up the wizard by dispatching a WizardEvent.WIZARD_COMPLETE and
		 * disabling all the buttons 
		 * event
		 */		
		private function wrapUpWizard():void {
			_previousButton.enabled = false;
			_nextButton.enabled = false;
			if (_cancelButton != null) {
				_cancelButton.enabled = false;
			}
			// Dispatch the WizardEvent.WIZARD_COMPLETE event
			var e:WizardEvent = new WizardEvent(WizardEvent.WIZARD_COMPLETE);
			dispatchEvent(e);
		}
		
		/**
		 * When the view stack containing all the steps has finished creating
		 * itself, and it created them all since we've ordered it to do wo, we
		 * now wish the get the max width and height of the the steps and set it
		 * to the voew stack. we do that so that the end developer using this 
		 * component won't need to care about sizing his Wizard, and all the 
		 * scaling will be done behind the scenes.
		 */		
		private function scaleStepsViewStack():void {
			for (var i:int = 0; i < _stepsViewStack.numChildren; i++) {
				var step:UIComponent = _stepsViewStack.getChildAt(i) as UIComponent;
				if (step.explicitWidth > _stepsMaxWidth) {
					_stepsMaxWidth = step.explicitWidth;
				}
				if (step.measuredWidth > _stepsMaxWidth) {
					_stepsMaxWidth = step.measuredWidth;
				}
				if (step.explicitHeight> _stepsMaxHeight) {
					_stepsMaxHeight = step.explicitHeight;
				}
				if (step.measuredHeight > _stepsMaxHeight) {
					_stepsMaxHeight = step.measuredHeight;
				}
			}
			_stepsViewStack.width = _stepsMaxWidth;
			_stepsViewStack.height = _stepsMaxHeight;
			_stepsViewStack.invalidateSize();
		}
		
		/**
		 * Get the step desicion from the current step and store it in the data 
		 * model 
		 */		
		private function retreiveCurrentStepDecision():void {
			if (_currentStep.dataField) {
				dataModel[_currentStep.dataField] = _currentStep.stepDecision;
				_summarArray[_currentStep.stepName] = _currentStep.readableStepDecision;	
			}
		}
		
		/**
		 * Gather the information from the _summaryDict and set it in a nice
		 * string format to be displayed later on the summary step 
		 */		
		private function setSummaryContent():void {
			_summaryString = "";
			for (var stepName:String in _summarArray) {
				if (stepName != "null") {
					_summaryString += "<B>" + stepName + ":</B><BR>" +  _summarArray[stepName] + "<BR><BR>";	
				}
			}
			// Set the summary string to the summary step text
			_summaryText.htmlText = _summaryString;
		}
		
		/**
		 * When there are still steps ahead we keep the "Next" label to the
		 * next button, but when we reach the final step, it's time to put the
		 * "Finish" label on it
		 */		
		private function validateNextStep():void {
			var nextStepIndex:int = currentStepIndex + 1;
			if (nextStepIndex >= steps.length) {
				_nextButton.label = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.buttons.confirm.label"); 
				// Set the summary string to the summary step text
				setSummaryContent();
			} else {
				_nextButton.label = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.buttons.next.label");
			}
		}
		
		/**
		 * Sets the previous button state according to the current step index.
		 * In plain words, if we don't have anywhere to go backwards, simply
		 * remove the previous button.
		 */		
		private function validatePrevStep():void {
			if (_previousButton) {
				_previousButton.visible = _previousButton.enabled = currentStepIndex > 0;
			}
		}
		
		/**
		 * Creates the summary step and in it creates the TextArea that will 
		 * hold the summary information for the wizard. At this point we don't
		 * even know if we're going to use the summary step but we create it 
		 * anyway since the "code-smell" of many IF's is more disturbing than 
		 * the performance price we pay. 
		 */


        private function createSummaryStep():void{
			_summaryStep = new WizardStep();
            _summaryStep.stepId = SUMMARY_STEP_ID;
			_summaryStep.isValid = true;
//			_summaryStep.stepDescription = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.step.summary.description");
			_summaryStep.percentHeight = 100;
			_summaryStep.percentWidth = 100;
            var gBox:GroupBox = new GroupBox();
			gBox.percentHeight = 100;
			gBox.percentWidth = 100;            
            gBox.title = ResourceManager.getInstance().getString("FlashmatticomponentsLabelsResourceBundle", "wizard.step.summary.description");
			_summaryText = new TextArea();
			_summaryText.percentHeight = 100;
			_summaryText.percentWidth = 100;
            gBox.addChild(_summaryText);
			_summaryStep.addChild(gBox);
		}

        private function findStepIndexByName(stepName:String):int {
            var i:int = 0;
            while (steps[i].stepId != stepName && i <= steps.length) {
                i++;
            }
            if(steps[i].stepId != stepName && i == steps.length) return -1;
            return i;
        }        
		
		// EVENT HANDLERS ------------------------------------------------------
		private function onStepsViewStackCreationComplete(event:FlexEvent):void {
			scaleStepsViewStack();
		}
		
		/**
		 * Called when the user has pressed the close button on the wizard
		 * @param event
		 */		
		private function onClose(event:CloseEvent):void {
			cancelWizard();
		}
		
		/**
		 * Called when the user uses the cancel button on the wizard 
		 * @param event
		 */		
		private function onCancel(event:MouseEvent):void {
			cancelWizard();
		}
		
		private function onPrevious(event:MouseEvent):void {
            if(_currentStep.previousStep == null){
			    currentStepIndex--;
            } else {
                var i:int = findStepIndexByName(_currentStep.previousStep);
                currentStepIndex = i;
            }
			validateWizardCurrentStep();
			// Dispatch the WizardEvent.PREV_STEP event
			var e:WizardEvent = new WizardEvent(WizardEvent.PREV_STEP);
			dispatchEvent(e);	
		}

        private function onNext(event:MouseEvent):void {
			// Get the step desicion from the current step
			retreiveCurrentStepDecision();
			//Check if there is a condition and conditional step
            var i:int;
            if(_currentStep.isJumpToConditionMet){
                //go to conditional step
                i = findStepIndexByName(_currentStep.jumpToStep);
                if(i > steps.length){
                    throw new Error("Searched stepId doesn't exist");
                }
                currentStepIndex = i;
            } else {
                //go to next step
                currentStepIndex++;
            }

			// If there are no more steps in the wizard, wrap it up
			if (currentStepIndex == _steps.length) {
				wrapUpWizard();
			} else {
                //set the current step as "previousStep" in nextStep before proceeding
                steps[currentStepIndex].previousStep = _currentStep.stepId;
				validateWizardCurrentStep();
				// Dispatch the WizardEvent.NEXT_STEP event
				var e:WizardEvent = new WizardEvent(WizardEvent.NEXT_STEP);
				dispatchEvent(e);	
			}
		}
		
		/**
		 * A routine of action to be executed when the user decided to cancel
		 * the wizard
		 * - Dispatching an event to indicate the cancelation
		 */		
		private function cancelWizard():void {
			var e:WizardEvent = new WizardEvent(WizardEvent.WIZARD_CANCEL);
			dispatchEvent(e);			
		}
		
		// GETTER/SETTER -------------------------------------------------------
		public function get dataModel():* {
			return _dataModel;
		}
		
		public function set dataModel(value:*):void {
			_dataModel = value;
		}
		
		public function get steps():Array {
			return _steps;
		}
		
		public function set steps(value:Array):void {
			_steps = value;
			if (_showSummaryStep) {
				_steps.push(_summaryStep);
			}
			_wizardStepsChanged = true;
			invalidateProperties();
		}
		
		private function set currentStepIndex(value:int):void {
			_currentStepIndex = value;
		} 
		
		private function get currentStepIndex():int {
			return _currentStepIndex;
		}
		
		public function set showSummaryStep(value:Boolean):void {
			_showSummaryStep = value;
		}
		
		public function set showCancelButton(value:Boolean):void {
			_showCancelButton = value;
		}
	}
}