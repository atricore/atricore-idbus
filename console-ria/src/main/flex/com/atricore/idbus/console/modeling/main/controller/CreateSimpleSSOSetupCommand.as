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

package com.atricore.idbus.console.modeling.main.controller
{
import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.command.SimpleCommand;

public class CreateSimpleSSOSetupCommand extends SimpleCommand implements IResponder
{
   public static const SUCCESS:String = "CreateSimpleSSOSetupCommand.SUCCESS";
   public static const FAILURE:String = "CreateSimpleSSOSetupCommand.FAILURE";

   public function CreateSimpleSSOSetupCommand() {
   }

   override public function execute(notification:INotification):void {
        // TODO: execute logic for creating a simple sso setup

   }

    public function fault(info:Object):void {
        var fault : Fault = (info as FaultEvent).fault;
        var msg : String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }

    public function result(data:Object):void {
        sendNotification(SUCCESS);
    }

}
}