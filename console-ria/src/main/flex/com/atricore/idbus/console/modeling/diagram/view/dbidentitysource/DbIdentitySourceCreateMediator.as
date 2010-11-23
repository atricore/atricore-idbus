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

package com.atricore.idbus.console.modeling.diagram.view.dbidentitysource {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.DbIdentitySource;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class DbIdentitySourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _newDbIdentitySource:DbIdentitySource;

    /*
     private var _uploadedFile:ByteArray;
     private var _uploadedFileName:String;

     [Bindable]
     private var _fileRef:FileReference;

     [Bindable]
     public var _selectedFiles:ArrayCollection;
     */

    [Bindable]
    public var _jdbcDrivers:ArrayCollection;

    public function DbIdentitySourceCreateMediator(name:String = null, viewComp:DbIdentitySourceCreateForm = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleDbIdentitySourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            /*if (_fileRef != null) {
             _fileRef.removeEventListener(Event.SELECT, fileSelectHandler);
             _fileRef.removeEventListener(Event.COMPLETE, uploadCompleteHandler);
             }*/
            view.driver.removeEventListener(Event.CHANGE, handleDriverChange);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleDbIdentitySourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        /*// upload bindings
         view.driver.addEventListener(MouseEvent.CLICK, browseHandler);
         BindingUtils.bindProperty(view.driver, "dataProvider", this, "_selectedFiles");*/

        BindingUtils.bindProperty(view.driver, "dataProvider", this, "_jdbcDrivers");
        view.driver.addEventListener(Event.CHANGE, handleDriverChange);
        sendNotification(ApplicationFacade.LIST_JDBC_DRIVERS);
        view.focusManager.setFocus(view.userRepositoryName);
    }

    private function resetForm():void {
        view.userRepositoryName.text = "";
        //view.driverName.text = "";
        view.connectionUrl.text = "";
        view.dbUsername.text = "";
        view.dbPassword.text = "";

        view.userQuery.text = "SELECT LOGIN AS NAME FROM JOSSO_USER WHERE LOGIN = ?";
        view.rolesQuery.text = "SELECT NAME AS ROLE FROM JOSSO_USER_ROLE WHERE LOGIN = ?";
        view.credentialsQuery.text = "SELECT LOGIN AS USERNAME, PASSWORD FROM JOSSO_USER WHERE LOGIN = ?";
        view.propertiesQuery.text = "SELECT NAME, VALUE FROM JOSSO_USER_PROPERTY WHERE LOGIN = ?";
        view.credentialsUpdate.text = "UPDATE JOSSO_USER SET PASSWORD = ? WHERE LOGIN = ?";
        view.relayCredentialQuery.text = "SELECT LOGIN FROM JOSSO_USER WHERE #?# = ?";

        _jdbcDrivers = new ArrayCollection();

        /*_fileRef = null;
         _selectedFiles = new ArrayCollection();
         view.driver.prompt = "Browse Driver";
         view.lblUploadMsg.text = "";
         view.lblUploadMsg.visible = false;

         _uploadedFile = null;
         _uploadedFileName = null;*/

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindForm():void {
        resetForm();
    }


    private function handleDriverChange(event:Event):void {
        view.connectionUrl.text = view.driver.selectedItem.defaultUrl;
    }

    override public function bindModel():void {
        var dbIdentitySource:DbIdentitySource = new DbIdentitySource();

        dbIdentitySource.name = view.userRepositoryName.text;
        //dbIdentitySource.driverName = view.driverName.text;
        dbIdentitySource.driverName = view.driver.selectedItem.className;
        dbIdentitySource.connectionUrl = view.connectionUrl.text;
        dbIdentitySource.admin = view.dbUsername.text;
        dbIdentitySource.password = view.dbPassword.text;

        dbIdentitySource.userQueryString = view.userQuery.text;
        dbIdentitySource.rolesQueryString = view.rolesQuery.text;
        dbIdentitySource.credentialsQueryString = view.credentialsQuery.text;
        dbIdentitySource.userPropertiesQueryString = view.propertiesQuery.text;
        dbIdentitySource.resetCredentialDml = view.credentialsUpdate.text;
        dbIdentitySource.relayCredentialQueryString = view.relayCredentialQuery.text;

        /*var driver:Resource = new Resource();
         driver.name = _uploadedFileName.substring(0, _uploadedFileName.lastIndexOf("."));
         driver.displayName = _uploadedFileName;
         driver.uri = _uploadedFileName;
         driver.value = _uploadedFile;
         dbIdentitySource.driver = driver;*/

        _newDbIdentitySource = dbIdentitySource;
    }

    private function handleDbIdentitySourceSave(event:MouseEvent):void {
        if (validate(true)) {
            /*if (_selectedFiles == null || _selectedFiles.length == 0) {
             view.lblUploadMsg.text = "You must select a jdbc driver!";
             view.lblUploadMsg.setStyle("color", "Red");
             view.lblUploadMsg.visible = true;
             event.stopImmediatePropagation();
             return;
             } else {
             _fileRef.load();
             }*/
            saveDbIdentitySource();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function saveDbIdentitySource():void {
        bindModel();
        _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.addItem(_newDbIdentitySource);
        _projectProxy.currentIdentityApplianceElement = _newDbIdentitySource;
        sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
        sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
        closeWindow();
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }
    /*
     // upload functions
     private function browseHandler(event:MouseEvent):void {
     if (_fileRef == null) {
     _fileRef = new FileReference();
     _fileRef.addEventListener(Event.SELECT, fileSelectHandler);
     _fileRef.addEventListener(Event.COMPLETE, uploadCompleteHandler);
     }
     var fileFilter:FileFilter = new FileFilter("JAR(*.jar)", "*.jar");
     var fileTypes:Array = new Array(fileFilter);
     _fileRef.browse(fileTypes);
     }

     private function fileSelectHandler(evt:Event):void {
     view.driver.prompt = null;
     _selectedFiles = new ArrayCollection();
     _selectedFiles.addItem(_fileRef.name);
     view.driver.selectedIndex = 0;

     view.lblUploadMsg.text = "";
     view.lblUploadMsg.visible = false;
     }

     private function uploadCompleteHandler(event:Event):void {
     _uploadedFile = _fileRef.data;
     _uploadedFileName = _fileRef.name;

     _fileRef = null;
     _selectedFiles = new ArrayCollection();
     view.driver.prompt = "Browse Driver";

     saveDbIdentitySource();
     }
     */
    protected function get view():DbIdentitySourceCreateForm {
        return viewComponent as DbIdentitySourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.driverValidator);
        _validators.push(view.connUrlValidator);
        _validators.push(view.dbUsernameValidator);
        _validators.push(view.dbPasswordValidator);
    }

    override public function listNotificationInterests():Array {
        return [JDBCDriversListCommand.SUCCESS,
            JDBCDriversListCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case JDBCDriversListCommand.SUCCESS:
                _jdbcDrivers = projectProxy.jdbcDrivers;
                break;
        }
        bindForm();
    }
}
}