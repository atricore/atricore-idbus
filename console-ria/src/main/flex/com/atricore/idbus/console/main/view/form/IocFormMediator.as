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

package com.atricore.idbus.console.main.view.form {
import mx.validators.Validator;

import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

/**
 * This class would be abstract if ActionScript supported such a construct. It is intended to provide
 * some basic shared functionality and a common interface for mediators which manage forms.
 * The following methods should be overriden to provide a proper, functioning, cpcrete form mediator
 * <ul>
 * <li>registerValidators - to add every vaidator on the form. validate and resetValidators will
 * only function correctly if this function is implemented</li>
 * <li>bindForm - to bind model fields onto the form</li>
 * <li>bindModel - to bind form fields back onto the model</li>
 */
public class IocFormMediator extends IocMediator {
   protected var _validators : Array;

    public function IocFormMediator(mediatorName:String = null, viewComponent:Object = null) {

        super(mediatorName, viewComponent);

        _validators = [];
        registerValidators();
    }

   /**
    * This method should be overridden to initialise the list of validators so that reset validators
    * and validate fill work correctly
    */
   public function registerValidators() : void { }

   /**
    * This function should be overriden to bind model fields onto the form.
    */
   public function bindForm() : void { }

   /**
    * This function should be overriden to bind form fields onto the model.
    */
   public function bindModel() : void { }

   public function validate(revalidate : Boolean) : Boolean {
      return FormUtility.validateAll(_validators, revalidate);
   }

   public function resetValidation() : void {
      for each(var validator : Validator in _validators) {
         validator.source.errorString = "";
      }
   }
}
}