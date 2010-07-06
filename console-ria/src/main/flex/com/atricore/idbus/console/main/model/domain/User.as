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

package com.atricore.idbus.console.main.model.domain
{

import mx.collections.ArrayCollection;

public class User
{
   public function User() {
      projects = new ArrayCollection();
   }

   public var id : Number;

   public var version : Number;

   public var email : String; //unique field, used as username.

   private var _fullName : String;

   public var firstName : String;

   public var lastName : String;

   public var password : String;

   public var passwordHint : String;

   public var phone1 : String;

   public var phone2 : String;

   public var projects : ArrayCollection;

   public function get fullName() : String {
      if(!firstName && !lastName) {
         return email.substr(0,email.indexOf("@"));
      }
      return firstName + " " + lastName;
   }

   public function copyFrom(user : User) : void {
      email = user.email;
      firstName = user.firstName;
      lastName = user.lastName;
      id = user.id;
      version = user.version;
      phone1 = user.phone1;
      phone2 = user.phone2;
      password = user.password;
      passwordHint = user.passwordHint;
      projects.source = user.projects.source;
   }

}
}