/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
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

package com.atricore.idbus.console.main.view.form
{
import mx.validators.Validator;

public class FormUtility
{
   public static function validateAll(validators:Array, triggerValidation : Boolean = false) : Boolean
   {
      if (triggerValidation) {
         doValidate(validators);
      }
      for each (var validator : Validator in validators) {
         if (validator.source.errorString != "") {
            return false;
         }
      }
      return true;
   }

   public static function clearValidationErrors(validators:Array) : void {
      for each (var validator : Validator in validators) {
         validator.source.errorString = "";
      }
   }

   public static function doValidate(validators:Array) : void {
      for each (var validator : Validator in validators) {
         validator.validate();
      }
   }

}
}