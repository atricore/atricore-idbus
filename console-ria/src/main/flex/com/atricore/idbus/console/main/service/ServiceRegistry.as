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

package com.atricore.idbus.console.main.service
{
import flash.utils.Dictionary;

import mx.messaging.Channel;
import mx.messaging.ChannelSet;
import mx.rpc.remoting.mxml.RemoteObject;

import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

/**
    * The ServiceRegistry allows the registration of RemoteObject and HTTPService objects. It provides
    * central management of credentials for remote calls.
    */
public class ServiceRegistry extends IocProxy {

   protected var _services:Dictionary;
   private var _channelSet : ChannelSet;

   public function ServiceRegistry() {
      super("Proxy", null);
      _services = new Dictionary();
      _channelSet = new ChannelSet();
   }

   public function unregister(id:String):void {
      _services[id] = null;
   }

   public function registerRemoteObjectService(id:String, destination:String,
                                               showBusyCursor:Boolean = true):void {
      var service:RemoteObject = new RemoteObject(destination);
      //service.showBusyCursor = showBusyCursor;
      //service.channelSet = _channelSet;
      _services[id] = service;
   }

   public function getService(id:String):Object {
      return _services[id];
   }

   public function getRemoteObjectService(id:String):RemoteObject {
      return _services[id] as RemoteObject;
   }

   public function setCredentials(username:String, password:String, charset:String = null):void {
      _channelSet.login(username, password, charset);
   }

   public function logout():void {
      _channelSet.logout();
   }

    public function setChannel(channel:Channel):void {
        ///_channelSet.addChannel(channel);
    }
}
}