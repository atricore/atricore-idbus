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

package com.atricore.idbus.console.services.business {
import com.atricore.idbus.console.services.spi.request.AddGroupRequest;
import com.atricore.idbus.console.services.spi.request.AddUserRequest;
import com.atricore.idbus.console.services.spi.request.FindGroupByIdRequest;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;
import com.atricore.idbus.console.services.spi.request.FindUserByIdRequest;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.request.GetUsersByGroupRequest;
import com.atricore.idbus.console.services.spi.request.RemoveGroupRequest;

import com.atricore.idbus.console.services.spi.request.RemoveUserRequest;
import com.atricore.idbus.console.services.spi.request.SearchGroupRequest;

import com.atricore.idbus.console.services.spi.request.SearchUserRequest;
import com.atricore.idbus.console.services.spi.request.UpdateGroupRequest;

import com.atricore.idbus.console.services.spi.request.UpdateUserRequest;

import flash.events.Event;
	import flash.events.IEventDispatcher;
	import mx.rpc.AsyncToken;
	import mx.rpc.remoting.RemoteObject;
//    import mx.core.Singleton;


public class UserProvisioningService implements IUserProvisioningService, IEventDispatcher {

    	private var ro:RemoteObject;

        private static var _instance:UserProvisioningService;


        public function UserProvisioningService() {
            if (_instance != null)
                 throw new Error("Singleton can only be accessed through Singleton.instance");
            this.ro = new RemoteObject("userProvisioningService");
            _instance = this;
        }

        public static function get instance():UserProvisioningService{
            if (_instance == null)  _instance = new UserProvisioningService();
                return _instance;
        }

		public function addEventListener(type:String, listener:Function, useCapture:Boolean=false, priority:int=0, useWeakReference:Boolean=false):void
		{
			ro.addEventListener(type, listener, useCapture, priority, useWeakReference);
		}

		public function removeEventListener(type:String, listener:Function, useCapture:Boolean=false):void
		{
			ro.removeEventListener(type, listener, useCapture);
		}

		public function dispatchEvent(event:Event):Boolean
		{
			return ro.dispatchEvent(event);
		}

		public function hasEventListener(type:String):Boolean
		{
			return ro.hasEventListener(type);
		}

		public function willTrigger(type:String):Boolean
		{
			return ro.willTrigger(type);
		}

		public function removeGroup(group:RemoveGroupRequest):AsyncToken
		{
			return ro.removeGroup(group);
		}

		public function addGroup(group:AddGroupRequest):AsyncToken
		{
			return ro.addGroup(group);
		}

		public function findGroupById(group:FindGroupByIdRequest):AsyncToken
		{
			return ro.findGroupById(group);
		}

        public function findGroupByName(group:FindGroupByNameRequest):AsyncToken
        {
            return ro.findGroupByName(group);
        }

		public function getGroups():AsyncToken
		{
			return ro.getGroups();
		}

        public function searchGroups(query:SearchGroupRequest):AsyncToken
        {
            return ro.searchGroups(query);
        }

        public function updateGroup(updateGroup:UpdateGroupRequest):AsyncToken
        {
            return ro.updateGroup(updateGroup);
        }

        public function removeUser(user:RemoveUserRequest):AsyncToken
        {
            return ro.removeUser(user);
        }

        public function addUser(user:AddUserRequest):AsyncToken
        {
            return ro.addUser(user);
        }

        public function findUserById(user:FindUserByIdRequest):AsyncToken
        {
            return ro.findUserById(user);
        }

        public function findUserByUsername(user:FindUserByUsernameRequest):AsyncToken
        {
            return ro.findUserByUsername(user);
        }

        public function getUsers():AsyncToken
        {
            return ro.getUsers();
        }

        public function searchUsers(query:SearchUserRequest):AsyncToken
        {
            return ro.searchUsers(query);
        }

        public function updateUser(updateUser:UpdateUserRequest):AsyncToken
        {
            return ro.updateUser(updateUser);
        }
        
        public function getUsersByGroup(group:GetUsersByGroupRequest):AsyncToken
        {
            return ro.getUsersByGroup(group);
        }
    }
}