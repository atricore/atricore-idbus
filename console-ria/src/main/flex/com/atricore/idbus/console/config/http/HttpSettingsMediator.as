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
import com.atricore.idbus.console.config.http.event.BindAddressGridEvent;
import com.atricore.idbus.console.config.main.controller.GetServiceConfigCommand;
import com.atricore.idbus.console.config.main.controller.UpdateServiceConfigCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.settings.HttpServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ServiceType;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.controls.TextInput;
import mx.events.DataGridEvent;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class HttpSettingsMediator extends IocFormMediator implements IDisposable {

    public static const ADD_BIND_ADDRESS:String = "Click to Add Bind Address";

    private var _configProxy:ServiceConfigProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _created:Boolean;

    private var _httpServiceConfig:HttpServiceConfiguration;
    private var _oldPort:Number;
    private var _oldBindAddresses:ArrayCollection;

    [Bindable]
    public var _bindAddresses:ArrayCollection;

    public function HttpSettingsMediator(name:String = null, viewComp:HttpSettingsView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.HTTP);
        }
        (viewComponent as HttpSettingsView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            view.bindAddresses.addEventListener(DataGridEvent.ITEM_EDIT_END, editEnd);
            view.bindAddresses.addEventListener(BindAddressGridEvent.CLICK, handleBindAddressGridEvent);
            _bindAddresses = new ArrayCollection();
            _bindAddresses.addItem({bindAddress:ADD_BIND_ADDRESS});
            BindingUtils.bindProperty(view.bindAddresses, "dataProvider", this, "_bindAddresses");
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
                var resp:ConfigureServiceResponse = notification.getBody() as ConfigureServiceResponse;
                if (resp.restart) {
                    //Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.service.restartMessage'));
                }
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
        for (var i:int = 0; i < _bindAddresses.length - 1; i++) {
            bindAddresses.push(_bindAddresses[i].bindAddress);
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
            if (_oldPort != _httpServiceConfig.port || bindAddressesChanged()) {
                Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.http.reconnectMessage'));
            }
        } else {
            event.stopImmediatePropagation();
        }
    }

    private function handleEnableSslChanged(event:Event):void {
        var enabled:Boolean = view.enableSSL.selected;
        view.sslPort.enabled = enabled;
        view.sslKeystorePath.enabled = enabled;
        view.sslKeystorePassword.enabled = enabled;
        view.sslKeyPassword.enabled = enabled;

        resetValidation();
        registerValidators();
        validate(true);
    }

    public function displayServiceConfig():void {
        _httpServiceConfig = _configProxy.httpService;

        view.serverId.text = _httpServiceConfig.serverId;
        view.port.text = String(_httpServiceConfig.port);
        _oldPort = _httpServiceConfig.port;
        _bindAddresses = new ArrayCollection();
        _oldBindAddresses = new ArrayCollection();
        if (_httpServiceConfig.bindAddresses != null) {
            for each (var bindAddress:String in _httpServiceConfig.bindAddresses) {
                _bindAddresses.addItem({bindAddress:bindAddress});
                _oldBindAddresses.addItem(bindAddress);
            }
        }
        _bindAddresses.addItem({bindAddress:ADD_BIND_ADDRESS});
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

        handleEnableSslChanged(null);
        view.btnSave.enabled = true;
    }

    private function editEnd(e:DataGridEvent):void {
        // Adding a new bind address
        if (e.rowIndex == _bindAddresses.length - 1) {
            var txtIn:TextInput = TextInput(e.currentTarget.itemEditorInstance);
            var newBindAddress:String = txtIn.text;

            // Add new bind address
            if (newBindAddress != ADD_BIND_ADDRESS) {
                var bindAddressExists:Boolean = false;
                for each (var bindAddress:Object in _bindAddresses) {
                    if (bindAddress.bindAddress == newBindAddress) {
                        bindAddressExists = true;
                        break;
                    }
                }
                if (!bindAddressExists) {
                    _bindAddresses.addItemAt({bindAddress:newBindAddress}, e.rowIndex);
                }
            }

            // Destroy item editor
            view.bindAddresses.destroyItemEditor();

            // Stop default behavior
            e.preventDefault();

            validateBindAddresses();
        }
    }

    private function handleBindAddressGridEvent(event:BindAddressGridEvent):void {
        switch (event.action) {
            case BindAddressGridEvent.ACTION_REMOVE:
                for (var i:int = 0; i < _bindAddresses.length; i++) {
                    if (_bindAddresses[i].bindAddress == event.data.bindAddress) {
                        _bindAddresses.removeItemAt(i);
                        validateBindAddresses();
                        break;
                    }
                }
                break;
        }
    }

    private function bindAddressesChanged():Boolean {
        var changed:Boolean = false;
        if (_oldBindAddresses.length != _httpServiceConfig.bindAddresses.length) {
            changed = true;
        }
        if (!changed) {
            for each (var bindAddress:String in _httpServiceConfig.bindAddresses) {
                if (!_oldBindAddresses.contains(bindAddress)) {
                    changed = true;
                    break;
                }
            }
        }
        return changed;
    }

    protected function get view():HttpSettingsView {
        return viewComponent as HttpSettingsView;
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

    override public function validate(revalidate:Boolean):Boolean {
        return validateBindAddresses() && super.validate(revalidate);
    }

    private function validateBindAddresses():Boolean {
        var valid:Boolean = false;
        if (_bindAddresses.length > 1) {
            valid = true;
            view.bindAddresses.errorString = "";
        } else {
            view.bindAddresses.errorString = resourceManager.getString(AtricoreConsole.BUNDLE, "config.http.bindAddressesValidationError");
        }
        return valid;
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }    
}
}
