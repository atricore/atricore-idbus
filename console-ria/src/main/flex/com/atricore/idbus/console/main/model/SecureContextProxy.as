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

package com.atricore.idbus.console.main.model
{
import mx.collections.ArrayCollection;

import com.atricore.idbus.console.main.model.domain.User;
import org.puremvc.as3.patterns.proxy.Proxy;

public class SecureContextProxy extends Proxy
{
   public static const NAME:String = "com.atricore.idbus.console.main.model.SecureContextProxy";
   public static const ROLE_PROJECT_ADMIN :String = "role.projectAdmin";
   public static const ROLE_ITERATION_ADMIN :String = "role.iterationAdmin";

   private var _currentUser : User;
   private var _availableRoles: ArrayCollection;
   private var _menu : ArrayCollection;

   public function SecureContextProxy() {
      super(NAME, null);
      _availableRoles = new ArrayCollection();
      _menu = new ArrayCollection();
   }

   public function logout() : void {
      _currentUser = null;
   }

   public function get menu() : ArrayCollection {
      return _menu;
   }

   public function set currentUser(user : User) : void {
      _currentUser = user;
      if(user != null) {
         loadMenu();
      }
   }

   public function get currentUser() : User {
      return _currentUser;
   }

   public function get availableRoles():ArrayCollection {
      return _availableRoles;
   }

   private function loadMenu() : void {
   }

   public function hasRole(role : String) : Boolean {
      for each(var existingRole : String in _availableRoles) {
         if(existingRole == role) {
            return true;
         }
      }
      return false;
   }
}
}