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

package com.atricore.idbus.console.main.view.progress
{
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ProcessingMediator extends IocMediator
{
    //public static const CREATED:String = "Note.ProcessingCreated";
    public static const START:String = "Note.StartProcessing";
    public static const STOP:String = "Note.StopProcessing";

    public function ProcessingMediator(name:String = null, viewComp:ProcessingView = null) {
        super(name, viewComp);
    }


    override public function setViewComponent(viewComponent:Object):void {
        super.setViewComponent(viewComponent);
    }

    override public function listNotificationInterests():Array {
        return [START,STOP];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case START:
                if (view != null) {
                    var progressBarLabel:String = view.progressBar.label;
                    if (notification.getBody() != null) {
                        progressBarLabel = notification.getBody() as String;
                    }
                    view.progressBar.label = progressBarLabel;
                    //sendNotification(CREATED);
                }
                break;
            case STOP:
                closeWindow();
                break;
        }
    }
    
    private function closeWindow():void {
        if (view.parent != null) {
            view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        } else {
            view.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
        }
    }
    
    protected function get view():ProcessingView
    {
        return viewComponent as ProcessingView;
    }
}
}