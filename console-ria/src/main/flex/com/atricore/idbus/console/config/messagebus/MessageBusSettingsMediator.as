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

package com.atricore.idbus.console.config.messagebus
{
import com.atricore.idbus.console.config.main.controller.GetServiceConfigCommand;
import com.atricore.idbus.console.config.main.controller.UpdateServiceConfigCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.settings.ArtifactQueueManagerConfiguration;
import com.atricore.idbus.console.services.dto.settings.ServiceType;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class MessageBusSettingsMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _created:Boolean;

    private var _artifactQueueManagerConfig:ArtifactQueueManagerConfiguration;

    public function MessageBusSettingsMediator(name:String = null, viewComp:MessageBusSettingsView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.AQM);
        }
        (viewComponent as MessageBusSettingsView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.AQM);
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
                var serviceType1:ServiceType = resp.serviceType;
                if (serviceType1.name == ServiceType.AQM.name) {
                    sendNotification(ProcessingMediator.STOP);
                    if (resp.restart) {
                        Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.service.restartMessage'));
                    }
                }
                break;
            case UpdateServiceConfigCommand.FAILURE:
                var serviceType2:ServiceType = notification.getBody() as ServiceType;
                if (serviceType2.name == ServiceType.AQM.name) {
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "config.aqm.save.error"));
                }
                break;
            case GetServiceConfigCommand.SUCCESS:
                var serviceType3:ServiceType = notification.getBody() as ServiceType;
                if (serviceType3.name == ServiceType.AQM.name) {
                    displayServiceConfig();
                }
                break;
            case GetServiceConfigCommand.FAILURE:
                //TODO show error - this should not happen
                break;
        }
    }

    override public function bindModel():void {
        _artifactQueueManagerConfig.brokerName = view.brokerName.text;
        _artifactQueueManagerConfig.brokerHost = view.brokerHost.text;
        _artifactQueueManagerConfig.brokerBindAddress = view.brokerBindAddress.text;
        _artifactQueueManagerConfig.brokerPort = parseInt(view.brokerPort.text);
    }

    private function handleSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.aqm.save.progress"));
            sendNotification(ApplicationFacade.UPDATE_SERVICE_CONFIG, _artifactQueueManagerConfig);
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function displayServiceConfig():void {
        _artifactQueueManagerConfig = _configProxy.artifactQueueManagerService;

        view.brokerName.text = _artifactQueueManagerConfig.brokerName;
        view.brokerHost.text = _artifactQueueManagerConfig.brokerHost;
        view.brokerBindAddress.text = _artifactQueueManagerConfig.brokerBindAddress;
        view.brokerPort.text = String(_artifactQueueManagerConfig.brokerPort);
        view.btnSave.enabled = true;
    }

    protected function get view():MessageBusSettingsView {
        return viewComponent as MessageBusSettingsView;
    }

    override public function registerValidators():void {
        _validators = [];
        _validators.push(view.brokerNameValidator);
        _validators.push(view.brokerHostValidator);
        _validators.push(view.brokerBindAddressValidator);
        _validators.push(view.brokerPortValidator);
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
