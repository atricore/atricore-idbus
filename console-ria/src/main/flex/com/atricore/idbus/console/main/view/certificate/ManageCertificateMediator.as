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

package com.atricore.idbus.console.main.view.certificate {

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.KeystoreProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.upload.UploadProgressMediator;
import com.atricore.idbus.console.services.dto.KeystoreDTO;
import com.atricore.idbus.console.services.dto.ResourceDTO;

import flash.events.DataEvent;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.ProgressEvent;
import flash.net.FileFilter;
import flash.net.FileReference;

import mx.binding.utils.BindingUtils;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class ManageCertificateMediator extends IocFormMediator
{
    public static const CREATE:String = "ManageCertificateMediator.CREATE";
    public static const EDIT:String = "ManageCertificateMediator.EDIT";

    private var _keystoreProxy:KeystoreProxy;
    private var _keystore:KeystoreDTO;
    private var _resourceId:String;
    private var _fileRef:FileReference;

    [Bindable]
    public var _selectedFiles:Array;


    public function ManageCertificateMediator(mediatorName:String = null, viewComponent:Object = null) {

        super(mediatorName, viewComponent);

    }

    public function set keystoreProxy(value:KeystoreProxy):void {
        _keystoreProxy = value;
    }

    public function get keystoreProxy():KeystoreProxy {
        return _keystoreProxy;
    }

    override public function setViewComponent(viewComponent:Object):void {

        if (getViewComponent() != null) {

            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.btnConfirm.removeEventListener(MouseEvent.CLICK, handleConfirm);
            view.btnUpload.removeEventListener(MouseEvent.CLICK, handleUpload);
            view.certificateKeyPair.removeEventListener(MouseEvent.CLICK, browseHandler);
            view.parent.removeEventListener(CloseEvent.CLOSE, handleClose);

            _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.removeEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
            _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
            _fileRef.removeEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);
        }

        super.setViewComponent(viewComponent);

        init();
    }


    public function init():void {

        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.btnConfirm.addEventListener(MouseEvent.CLICK, handleConfirm);
        view.btnUpload.addEventListener(MouseEvent.CLICK, handleUpload);
        view.certificateKeyPair.addEventListener(MouseEvent.CLICK, browseHandler);
        view.parent.addEventListener(CloseEvent.CLOSE, handleClose);

        _fileRef = new FileReference();
        _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
        _fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
        _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
        _fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);

        BindingUtils.bindProperty(view.certificateKeyPair, "dataProvider", this, "_selectedFiles");
        view.certificateKeyPair.prompt = "Browse Key Pair";

    }

    override public function registerValidators():void {
        _validators.push(view.certificateAliasValidator);
        _validators.push(view.keyAliasValidator);
        _validators.push(view.keystorePasswordValidator);
        _validators.push(view.keyPasswordValidator);
    }

    override public function listNotificationInterests():Array {
        return [CREATE,EDIT, UploadProgressMediator.CREATED,
            UploadProgressMediator.UPLOAD_CANCELED];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case CREATE :
                _keystoreProxy.viewAction = KeystoreProxy.ACTION_ITEM_CREATE;
                bindForm();
                view.focusManager.setFocus(view.certificateKeyPair);
                break;
            case EDIT :
                _keystoreProxy.viewAction = KeystoreProxy.ACTION_ITEM_EDIT;
                bindForm();
                view.focusManager.setFocus(view.certificateAlias);
                break;
            case UploadProgressMediator.CREATED:
                // upload progress window created, start upload
                sendNotification(ApplicationFacade.UPLOAD, _fileRef);
                break;
            case UploadProgressMediator.UPLOAD_CANCELED:
                if (_fileRef != null)
                    _fileRef.cancel();
                break;
        }

    }

    override public function bindForm():void {
        if (_keystoreProxy.currentKeystore != null) {
            view.keystoreFormat.selectedItem = _keystoreProxy.currentKeystore.type;
            view.certificateAlias.text = _keystoreProxy.currentKeystore.certificateAlias;
            view.keyAlias.text = _keystoreProxy.currentKeystore.privateKeyName;
            view.keyPassword.text = _keystoreProxy.currentKeystore.privateKeyPassword;
            view.keystorePassword.text = _keystoreProxy.currentKeystore.password;
        }

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        _keystore = new KeystoreDTO();
        _keystore.certificateAlias = view.certificateAlias.text;
        _keystore.privateKeyName = view.keyAlias.text;
        _keystore.privateKeyPassword = view.keyPassword.text;
        _keystore.password = view.keystorePassword.text;
        _keystore.type = view.keystoreFormat.selectedItem.data;
        var resource:ResourceDTO = new ResourceDTO();
        resource.id = Number(_resourceId);
        _keystore.store = resource;
    }

    private function handleConfirm(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _keystoreProxy.currentKeystore = _keystore;
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function browseHandler(event:MouseEvent):void {
        if (_fileRef == null) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
            _fileRef.addEventListener(ProgressEvent.PROGRESS, uploadProgressHandler);
            _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
            _fileRef.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, uploadCompleteDataHandler);
        }
        var fileFilter:FileFilter = new FileFilter("JKS(*.jks)", "*.jks");
        var fileTypes:Array = new Array(fileFilter);
        _fileRef.browse(fileTypes);
    }

    private function handleUpload(event:MouseEvent):void {
        //_fileRef.load();  //this is available from flash player 10 and maybe flex sdk 3.4
        //_fileRef.data;
        sendNotification(ApplicationFacade.SHOW_UPLOAD_PROGRESS, _fileRef);
    }

    private function fileSelectHandler(evt:Event):void {
        _selectedFiles = new Array();
        _selectedFiles.push(_fileRef.name);
        view.certificateKeyPair.selectedIndex = 0;
        view.btnUpload.enabled = true;
    }

    private function uploadProgressHandler(event:ProgressEvent):void {
        var numPerc:Number = Math.round((Number(event.bytesLoaded) / Number(event.bytesTotal)) * 100);
        sendNotification(UploadProgressMediator.UPDATE_PROGRESS, numPerc);
    }

    private function uploadCompleteHandler(event:Event):void {
        sendNotification(UploadProgressMediator.UPLOAD_COMPLETED);
        _fileRef = null;
        _selectedFiles = new Array();
        view.btnUpload.enabled = false;
    }

    private function uploadCompleteDataHandler(event:DataEvent):void {
        //var xmlResponse:XML = XML(event.data);
        //resourceId = xmlResponse.elements("resource").attribute("id").toString();
        _resourceId = event.data;
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    private function handleClose(event:Event):void {

    }

    protected function get view():ManageCertificateView
    {
        return viewComponent as ManageCertificateView;
    }
}
}