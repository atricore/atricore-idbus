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

package com.atricore.idbus.console.main.controller
{
import flash.external.ExternalInterface;
import flash.net.FileReference;
import flash.net.URLRequest;
import flash.net.URLRequestMethod;
import flash.net.URLVariables;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class UploadCommand extends IocSimpleCommand
{
   public function UploadCommand() {
   }

   override public function execute(notification:INotification):void {
        var fileRef:FileReference = notification.getBody() as FileReference;

        if (fileRef != null) {
            var currentUrl:String = ExternalInterface.call("window.location.href.toString");
            var url:String = currentUrl.substring(0, currentUrl.lastIndexOf("/")) + "/upload";

            var request:URLRequest = new URLRequest(url);
            request.method = URLRequestMethod.POST;

            var variables:URLVariables = new URLVariables();
            variables.resourceName = fileRef.name;
            variables.resourceDisplayName = fileRef.name;
            request.data = variables;

            fileRef.upload(request, "file", false);
        }
   }
}
}