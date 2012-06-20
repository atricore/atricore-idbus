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
 * 02110-1301 USA, or see the FSF site: ssh://www.fsf.org.
 */

package com.atricore.idbus.console.config.persistence
{
import com.atricore.idbus.console.config.main.controller.GetServiceConfigCommand;
import com.atricore.idbus.console.config.main.controller.UpdateServiceConfigCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
import com.atricore.idbus.console.services.dto.settings.PersistenceServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ServiceType;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class PersistenceServiceMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    private var _projectProxy:ProjectProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _created:Boolean;    

    private var _persistenceServiceConfig:PersistenceServiceConfiguration;

    [Bindable]
    public var _jdbcDrivers:ArrayCollection;

    public function PersistenceServiceMediator(name:String = null, viewComp:PersistenceServiceView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.PERSISTENCE);
        }
        (viewComponent as PersistenceServiceView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
        super.setViewComponent(viewComponent);
    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;
        init();
    }

    private function init():void {
        if (_created) {
            view.titleDisplay.width = 0;
            view.titleDisplay.height = 0;
            BindingUtils.bindProperty(view.connectionDriver, "dataProvider", this, "_jdbcDrivers");
            view.connectionDriver.addEventListener(Event.CHANGE, handleDriverChange);
            view.useExternalDB.addEventListener(Event.CHANGE, handleUseExternalDbChanged);
            sendNotification(ApplicationFacade.LIST_JDBC_DRIVERS);
            view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.PERSISTENCE);
        }
    }

    override public function listNotificationInterests():Array {
        return [ UpdateServiceConfigCommand.SUCCESS,
            UpdateServiceConfigCommand.FAILURE,
            GetServiceConfigCommand.SUCCESS,
            GetServiceConfigCommand.FAILURE,
            JDBCDriversListCommand.SUCCESS,
            JDBCDriversListCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case UpdateServiceConfigCommand.SUCCESS:
                var resp:ConfigureServiceResponse = notification.getBody() as ConfigureServiceResponse;
                var serviceType1:ServiceType = resp.serviceType;
                if (serviceType1.name == ServiceType.PERSISTENCE.name) {
                    sendNotification(ProcessingMediator.STOP);
                    if (resp.restart) {
                        Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.service.restartMessage'));
                    }
                }
                break;
            case UpdateServiceConfigCommand.FAILURE:
                var serviceType2:ServiceType = notification.getBody() as ServiceType;
                if (serviceType2.name == ServiceType.PERSISTENCE.name) {
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "config.persistence.save.error"));
                }
                break;
            case GetServiceConfigCommand.SUCCESS:
                var serviceType3:ServiceType = notification.getBody() as ServiceType;
                if (serviceType3.name == ServiceType.PERSISTENCE.name) {
                    displayServiceConfig();
                }
                break;
            case GetServiceConfigCommand.FAILURE:
                //TODO show error - this should not happen
                break;
            case JDBCDriversListCommand.SUCCESS:
                _jdbcDrivers = projectProxy.jdbcDrivers;
                break;
        }
    }

    override public function bindModel():void {
        _persistenceServiceConfig.port = parseInt(view.port.text);
        //_persistenceServiceConfig.username = view.username.text;
        _persistenceServiceConfig.password = view.password.text;
        _persistenceServiceConfig.useExternalDB = view.useExternalDB.selected;
        if (_persistenceServiceConfig.useExternalDB) {
            _persistenceServiceConfig.connectionDriver = view.connectionDriver.selectedItem.className;
            _persistenceServiceConfig.connectionUrl = view.connectionUrl.text;
            _persistenceServiceConfig.connectionUsername = view.connectionUsername.text;
            _persistenceServiceConfig.connectionPassword = view.connectionPassword.text;
        }
    }

    private function handleSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            var saveAlert:Alert = Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.persistence.save.warning.message'),
                resourceManager.getString(AtricoreConsole.BUNDLE, 'config.persistence.save.warning.title'),
                3, null,
                function(event:CloseEvent):void {
                    if (event.detail == Alert.YES) {
                        sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.persistence.save.progress"));
                        sendNotification(ApplicationFacade.UPDATE_SERVICE_CONFIG, _persistenceServiceConfig);
                    } else {
                        PopUpManager.removePopUp(saveAlert);
                    }
                }
            );
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function displayServiceConfig():void {
        _persistenceServiceConfig = configProxy.persistenceService;

        view.port.text = String(_persistenceServiceConfig.port);
        view.username.text = _persistenceServiceConfig.username;
        view.password.text = _persistenceServiceConfig.password;
        view.confirmPassword.text = _persistenceServiceConfig.password;
        view.useExternalDB.selected = _persistenceServiceConfig.useExternalDB;
        if (_persistenceServiceConfig.useExternalDB) {
            for (var i:int = 0; i < view.connectionDriver.dataProvider.length; i++) {
                if (_persistenceServiceConfig.connectionDriver == view.connectionDriver.dataProvider[i].className) {
                    view.connectionDriver.selectedIndex = i;
                    break;
                }
            }
            view.connectionUrl.text = _persistenceServiceConfig.connectionUrl;
            view.connectionUsername.text = _persistenceServiceConfig.connectionUsername;
            view.connectionPassword.text = _persistenceServiceConfig.connectionPassword;
            view.connectionConfirmPassword.text = _persistenceServiceConfig.connectionPassword;
        }
        handleUseExternalDbChanged(null);
        view.btnSave.enabled = true;
    }

    private function handleDriverChange(event:Event):void {
        view.connectionUrl.text = view.connectionDriver.selectedItem.defaultUrl;
    }

    private function handleUseExternalDbChanged(event:Event):void {
        var enabled:Boolean = view.useExternalDB.selected;
        view.connectionDriver.enabled = enabled;
        view.connectionUrl.enabled = enabled;
        view.connectionUsername.enabled = enabled;
        view.connectionPassword.enabled = enabled;
        view.connectionConfirmPassword.enabled = enabled;
        resetValidation();
        registerValidators();
        validate(true);
    }

    protected function get view():PersistenceServiceView {
        return viewComponent as PersistenceServiceView;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.portValidator);
        //_validators.push(view.usernameValidator);
        _validators.push(view.passwordValidator);
        if (view.useExternalDB.selected) {
            _validators.push(view.connDriverValidator);
            _validators.push(view.connUrlValidator);
            _validators.push(view.connUsernameValidator);
            _validators.push(view.connPasswordValidator);
        }
    }

    public function get configProxy():ServiceConfigProxy {
        return _configProxy;
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }    
}
}
