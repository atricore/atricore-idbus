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

package com.atricore.idbus.console.config.http
{
import com.atricore.idbus.console.config.main.controller.GetServiceConfigCommand;
import com.atricore.idbus.console.config.main.controller.UpdateServiceConfigCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.settings.HttpServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ServiceType;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class HttpServiceMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    //commands
    private var _getServiceConfigCommand:GetServiceConfigCommand;
    private var _updateServiceConfigCommand:UpdateServiceConfigCommand;

    private var _created:Boolean;    

    private var _httpServiceConfig:HttpServiceConfiguration;
    private var _port:Number;
    private var _bindAddresses:String;

    public function HttpServiceMediator(name:String = null, viewComp:HttpServiceView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.HTTP);
        }
        (viewComponent as HttpServiceView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);
            view.enableSSL.addEventListener(Event.CHANGE, handleEnableSslChanged);
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.HTTP);
        }
    }

    override public function listNotificationInterests():Array {
        return [ UpdateServiceConfigCommand.SUCCESS,
            UpdateServiceConfigCommand.FAILURE,
            GetServiceConfigCommand.SUCCESS,
            GetServiceConfigCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case UpdateServiceConfigCommand.SUCCESS:
                /*var serviceType1:ServiceType = notification.getBody() as ServiceType;
                if (serviceType1.name == ServiceType.HTTP.name) {
                    sendNotification(ProcessingMediator.STOP);
                }*/
                break;
            case UpdateServiceConfigCommand.FAILURE:
                var serviceType2:ServiceType = notification.getBody() as ServiceType;
                if (serviceType2.name == ServiceType.HTTP.name) {
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "config.http.save.error"));
                }
                break;
            case GetServiceConfigCommand.SUCCESS:
                var serviceType3:ServiceType = notification.getBody() as ServiceType;
                if (serviceType3.name == ServiceType.HTTP.name) {
                    displayServiceConfig();
                }
                break;
            case GetServiceConfigCommand.FAILURE:
                //TODO show error - this should not happen
                break;
        }
    }

    override public function bindModel():void {
        _httpServiceConfig.serverId = view.serverId.text;
        _httpServiceConfig.port = parseInt(view.port.text);
        var bindAddresses:Array = new Array();
        if (view.bindAddress.text != null && view.bindAddress.text != "") {
            bindAddresses = view.bindAddress.text.split(",");
        }
        _httpServiceConfig.bindAddresses = bindAddresses;
        _httpServiceConfig.sessionTimeout = parseInt(view.sessionTimeout.text);
        _httpServiceConfig.maxHeaderBufferSize = parseInt(view.maxHeaderBufferSize.text);
        _httpServiceConfig.disableSessionUrl = view.disableSessionURL.selected;
        _httpServiceConfig.enableSsl = view.enableSSL.selected;
        if (_httpServiceConfig.enableSsl) {
            _httpServiceConfig.sslPort = parseInt(view.sslPort.text);
            _httpServiceConfig.sslKeystorePath = view.sslKeystorePath.text;
            _httpServiceConfig.sslKeystorePassword = view.sslKeystorePassword.text;
            _httpServiceConfig.sslKeyPassword = view.sslKeyPassword.text;
        }
    }

    private function handleSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            //sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.http.save.progress"));
            sendNotification(ApplicationFacade.UPDATE_SERVICE_CONFIG, _httpServiceConfig);
            if (_port != _httpServiceConfig.port || _bindAddresses !=_httpServiceConfig.bindAddresses.join(",")) {
                Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.http.reconnectMessage'));
            }
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleEnableSslChanged(event:Event):void {
        var enabled:Boolean = view.enableSSL.selected;
        view.sslPort.enabled = enabled;
        view.sslKeystorePath.enabled = enabled;
        view.sslKeystorePassword.enabled = enabled;
        view.sslKeyPassword.enabled = enabled;
        //FormUtility.clearValidationErrors(_validators);
        registerValidators();
        validate(true);
    }

    public function displayServiceConfig():void {
        _httpServiceConfig = _configProxy.httpService;

        view.serverId.text = _httpServiceConfig.serverId;
        view.port.text = String(_httpServiceConfig.port);
        var bindAddresses:String = null;
        if (_httpServiceConfig.bindAddresses != null) {
            bindAddresses = _httpServiceConfig.bindAddresses.join(",");
        }
        view.bindAddress.text = bindAddresses;
        view.sessionTimeout.text = String(_httpServiceConfig.sessionTimeout);
        view.maxHeaderBufferSize.text = String(_httpServiceConfig.maxHeaderBufferSize);
        view.disableSessionURL.selected = _httpServiceConfig.disableSessionUrl;
        view.enableSSL.selected = _httpServiceConfig.enableSsl;
        var sslPort:String = null;
        if (_httpServiceConfig.sslPort > 0) {
            sslPort = String(_httpServiceConfig.sslPort);
        }
        view.sslPort.text = sslPort;
        view.sslKeystorePath.text = _httpServiceConfig.sslKeystorePath;
        view.sslKeystorePassword.text = _httpServiceConfig.sslKeystorePassword;
        view.sslKeyPassword.text = _httpServiceConfig.sslKeyPassword;

        _port = _httpServiceConfig.port;
        _bindAddresses = view.bindAddress.text;

        handleEnableSslChanged(null);
        view.btnSave.enabled = true;
    }

    protected function get view():HttpServiceView {
        return viewComponent as HttpServiceView;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.serverIdValidator);
        _validators.push(view.portValidator);
        _validators.push(view.sessionTimeoutValidator);
        _validators.push(view.maxHeaderBufferSizeValidator);
        if (view.enableSSL.selected) {
            _validators.push(view.sslPortValidator);
            _validators.push(view.sslKeystorePathValidator);
            _validators.push(view.sslKeystorePasswordValidator);
            _validators.push(view.sslKeyPasswordValidator);
        }
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }

    public function get getServiceConfigCommand():GetServiceConfigCommand {
        return _getServiceConfigCommand;
    }

    public function set getServiceConfigCommand(value:GetServiceConfigCommand):void {
        _getServiceConfigCommand = value;
    }

    public function get updateServiceConfigCommand():UpdateServiceConfigCommand {
        return _updateServiceConfigCommand;
    }

    public function set updateServiceConfigCommand(value:UpdateServiceConfigCommand):void {
        _updateServiceConfigCommand = value;
    }

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }    
}
}
