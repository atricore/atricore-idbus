/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.business {

import com.atricore.idbus.console.services.spi.request.FetchGroupMembershipRequest;
import com.atricore.idbus.console.services.spi.request.UpdateUserPasswordRequest;
import com.atricore.idbus.console.services.spi.request.UpdateUserProfileRequest;

import mx.rpc.AsyncToken;
import flash.events.Event;
import flash.events.IEventDispatcher;
import mx.rpc.remoting.RemoteObject;


public class ProfileManagementService implements IProfileManagementService {

    private var ro:RemoteObject;

    private static var _instance:ProfileManagementService;

    public function ProfileManagementService() {
        if (_instance != null)
             throw new Error("Singleton can only be accessed through Singleton.instance");
        this.ro = new RemoteObject("profileManagementService");
        _instance = this;
    }

    public static function get instance():ProfileManagementService {
        if (_instance == null)  _instance = new ProfileManagementService();
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
    

    public function updateUserProfile(updateUserProfileRequest:UpdateUserProfileRequest):AsyncToken {
        return ro.updateUserProfile(updateUserProfileRequest);
    }

    public function updateUserPassword(updateUserPasswordRequest:UpdateUserPasswordRequest):AsyncToken {
        return ro.updateUserPassword(updateUserPasswordRequest);
    }
                      
    public function fetchGroupMembership(fetchGroupMembershipRequest:FetchGroupMembershipRequest):AsyncToken {
        return ro.fetchGroupMembership(fetchGroupMembershipRequest);
    }

   }
}