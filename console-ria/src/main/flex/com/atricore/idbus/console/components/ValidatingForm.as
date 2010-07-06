/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.components
{
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Form;
import mx.events.ValidationResultEvent;
import mx.validators.IValidatorListener;
import mx.validators.Validator;

public class ValidatingForm extends Form
    {
        public function ValidatingForm()
        {
            super();
        }

        private var _validatorsArray:Array;

        private var _validationOk:Function;

        private var _validationFail:Function;

        public function set validationOk(f:Function):void {
            _validationOk=f;
        }

        public function set validationFail(f:Function):void {
            _validationFail = f;
        }

        [Bindable]
        public function set validators(v:Array):void {
            _validatorsArray = v;
        }
        
        public function get validators():Array{
            return _validatorsArray;
        }

        [Bindable]
        public var isValid:Boolean = false;

        private var focussedFormControl:DisplayObject;

        public function validateForm(event:Event=null):void
        {
            // Save a reference to the currently focussed form control
            // so that the isValid() helper method can notify only
            // the currently focussed form control and not affect
            // any of the other form controls.
            if(event!=null)
                focussedFormControl = event.target as DisplayObject;
            // Mark the form as valid to start with
            isValid = true;
            
            // Run each validator in turn, using the isValid()
            // helper method and update the value of formIsValid
            // accordingly.

            var errorsMsg: String;
            for each (var validator:Validator in _validatorsArray) {
                validate(validator);
            }


            if(isValid) {
                if(_validationOk!=null) {
                    _validationOk.call();
                }
            } else {                
                if(_validationFail!=null) {
                    _validationFail.call();
                }
            }
        }


        private function validate(validator:Validator):Boolean
        {
            // Get a reference to the component that is the
            // source of the validator.
            var validatorSource:DisplayObject = validator.source as DisplayObject;
            var validatorListener:IValidatorListener = validator.source as IValidatorListener;


            // Suppress event if the current control being validated is not
            // the currently focussed control on the form. This stops the user
            // from receiving visual validation cues on other form controls.
            var suppressEvents:Boolean = (validatorSource != focussedFormControl);

            // Carry out validation. Returns a ValidationResultEvent.
            // Passing null for the first parameter makes the validator
            // use the property defined in the property tag of the
            //  tag.
            var event:ValidationResultEvent = validator.validate(null, suppressEvents);

            // Check if validation passed and return a boolean value accordingly.
            var currentControlIsValid:Boolean = (event.type == ValidationResultEvent.VALID);

            if (!currentControlIsValid){
                 validatorSource.dispatchEvent(new MouseEvent(MouseEvent.MOUSE_OVER));
            }
            
            // Update the isValid flag
            isValid = isValid && currentControlIsValid;

            return currentControlIsValid;
        }

    }
}