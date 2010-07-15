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

package com.atricore.idbus.console.main.view.upload
{
import com.atricore.idbus.console.main.ApplicationFacade;

import flash.events.Event;
import flash.net.FileReference;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class UploadProgressMediator extends Mediator
{
    public static const NAME:String = "UploadProgressMediator";
    public static const CREATED:String = "Note.UploadProgressCreated";
    public static const UPDATE_PROGRESS:String = "Note.UpdateProgress";
    public static const UPLOAD_COMPLETED:String = "Note.UploadCompleted";
    public static const UPLOAD_CANCELED:String = "Note.UploadCanceled";

    private var _fileRef:FileReference;
    
    public function UploadProgressMediator(viewComp:UploadProgress) {
        super(NAME, viewComp);
        view.btnCancel.addEventListener("click", onUploadBtnCancel);
        view.btnFinish.addEventListener("click", onUploadBtnFinish);
        viewComp.parent.addEventListener(CloseEvent.CLOSE, handleClose);
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_SHOW_UPLOAD_PROGRESS, UPDATE_PROGRESS, UPLOAD_COMPLETED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_SHOW_UPLOAD_PROGRESS:
                _fileRef = notification.getBody() as FileReference;
                view.txtFile.text = "Uploading file: " + _fileRef.name;
                view.progBar.label = "0%";
                sendNotification(CREATED);
                break;
            case UPDATE_PROGRESS:
                var numPerc:Number = notification.getBody() as Number;
                view.progBar.setProgress(numPerc, 100);
                view.progBar.label = numPerc + "%";
                view.progBar.validateNow();
                if (numPerc > 90) {
                    view.btnCancel.enabled = false;
                } else {
                    view.btnCancel.enabled = true;
                }
                break;
            case UPLOAD_COMPLETED:
                view.btnCancel.visible = false;
                view.btnFinish.visible = true;
                break;
        }
    }

    private function onUploadBtnFinish(event:Event):void {
        closeWindow();
    }

    private function onUploadBtnCancel(event:Event):void {
        closeWindow();
        //_fileRef.cancel();
        sendNotification(UPLOAD_CANCELED);
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
        facade.removeMediator(UploadProgressMediator.NAME);
    }

    protected function get view():UploadProgress
    {
        return viewComponent as UploadProgress;
    }
}
}