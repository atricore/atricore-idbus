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
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class UploadProgressMediator extends IocMediator
{
    public static const CREATED:String = "Note.UploadProgressCreated";
    public static const UPDATE_PROGRESS:String = "Note.UpdateProgress";
    public static const UPLOAD_COMPLETED:String = "Note.UploadCompleted";
    public static const UPLOAD_CANCELED:String = "Note.UploadCanceled";

    private var resourceManager:IResourceManager = ResourceManager.getInstance();
    
    private var _fileRef:FileReference;
    
    public function UploadProgressMediator(name:String = null, viewComp:UploadProgress = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnCancelFinish.removeEventListener(MouseEvent.CLICK, onUploadBtnCancelFinish);
            //view.btnCancel.removeEventListener("click", onUploadBtnCancel);
            //view.btnFinish.removeEventListener("click", onUploadBtnFinish);
        }

        init();

        super.setViewComponent(viewComponent);
    }

    private function init():void {
        if (view != null) {
            view.btnCancelFinish.addEventListener(MouseEvent.CLICK, onUploadBtnCancelFinish);
            //view.btnCancel.addEventListener("click", onUploadBtnCancel);
            //view.btnFinish.addEventListener("click", onUploadBtnFinish);
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.SHOW_UPLOAD_PROGRESS, UPDATE_PROGRESS, UPLOAD_COMPLETED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.SHOW_UPLOAD_PROGRESS:
                if (view != null) {
                    _fileRef = notification.getBody() as FileReference;
                    view.txtFile.text = resourceManager.getString(AtricoreConsole.BUNDLE, "upload.uploading") + ": " + _fileRef.name;
                    view.progBar.label = "0%";
                    init();
                    sendNotification(CREATED);
                }
                break;
            case UPDATE_PROGRESS:
                var numPerc:Number = notification.getBody() as Number;
                view.progBar.setProgress(numPerc, 100);
                view.progBar.label = numPerc + "%";
                view.progBar.validateNow();
                if (numPerc > 90) {
                    view.btnCancelFinish.enabled = false;
                    //view.btnCancel.enabled = false;
                } else {
                    view.btnCancelFinish.enabled = true;
                    //view.btnCancel.enabled = true;
                }
                break;
            case UPLOAD_COMPLETED:
                view.btnCancelFinish.label = resourceManager.getString(AtricoreConsole.BUNDLE, "upload.finish");
                view.btnCancelFinish.enabled = true;
                //view.btnCancel.visible = false;
                //view.btnFinish.visible = true;
                break;
        }
    }

    private function onUploadBtnCancelFinish(event:Event):void {
        closeWindow();
        if (view.btnCancelFinish.label == resourceManager.getString(AtricoreConsole.BUNDLE, "upload.cancel")) {
            //_fileRef.cancel();
            sendNotification(UPLOAD_CANCELED);
        }
    }
/*
    private function onUploadBtnFinish(event:Event):void {
        closeWindow();
    }

    private function onUploadBtnCancel(event:Event):void {
        closeWindow();
        //_fileRef.cancel();
        sendNotification(UPLOAD_CANCELED);
    }
*/
    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {
    }

    protected function get view():UploadProgress
    {
        return viewComponent as UploadProgress;
    }
}
}