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

package com.atricore.idbus.console.config.logging
{
import com.atricore.idbus.console.config.main.controller.GetServiceConfigCommand;
import com.atricore.idbus.console.config.main.controller.UpdateServiceConfigCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.services.dto.settings.LogServiceConfiguration;
import com.atricore.idbus.console.services.dto.settings.ServiceType;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;

public class LoggingSettingsMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _created:Boolean;    

    private var _logServiceConfig:LogServiceConfiguration;

    public function LoggingSettingsMediator(name:String = null, viewComp:LoggingSettingsView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.LOG);
        }
        (viewComponent as LoggingSettingsView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            sendNotification(ApplicationFacade.GET_SERVICE_CONFIG, ServiceType.LOG);
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
                if (serviceType1.name == ServiceType.LOG.name) {
                    sendNotification(ProcessingMediator.STOP);
                    if (resp.restart) {
                        Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.service.restartMessage'));
                    }
                }
                break;
            case UpdateServiceConfigCommand.FAILURE:
                var serviceType2:ServiceType = notification.getBody() as ServiceType;
                if (serviceType2.name == ServiceType.LOG.name) {
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                            resourceManager.getString(AtricoreConsole.BUNDLE, "config.log.save.error"));
                }
                break;
            case GetServiceConfigCommand.SUCCESS:
                var serviceType3:ServiceType = notification.getBody() as ServiceType;
                if (serviceType3.name == ServiceType.LOG.name) {
                    displayServiceConfig();
                }
                break;
            case GetServiceConfigCommand.FAILURE:
                //TODO show error - this should not happen
                break;
        }
    }

    override public function bindModel():void {
        _logServiceConfig.serviceMode = view.serviceMode.selectedItem.data;
    }

    private function handleSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.log.save.progress"));
            sendNotification(ApplicationFacade.UPDATE_SERVICE_CONFIG, _logServiceConfig);
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    public function displayServiceConfig():void {
        _logServiceConfig = _configProxy.logService;

        var serviceMods:ArrayCollection = new ArrayCollection();
        serviceMods.addItem({data:0,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.log.loggingVerbosity.detailed')});
        serviceMods.addItem({data:10,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.log.loggingVerbosity.normal')});
        if (_logServiceConfig.serviceMode == 20) {
            serviceMods.addItem({data:20,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.log.loggingVerbosity.custom')});
        }
        view.serviceMode.dataProvider = serviceMods;
        view.serviceMode.validateNow();

        if (_logServiceConfig.serviceMode == 0) {
            view.serviceMode.selectedIndex = 0;
            view.customLogData.visible = false;
            view.customLogData.includeInLayout = false;
            view.serviceMode.enabled = true;
            view.btnSave.enabled = true;
        } else if (_logServiceConfig.serviceMode == 10) {
            view.serviceMode.selectedIndex = 1;
            view.customLogData.visible = false;
            view.customLogData.includeInLayout = false;
            view.serviceMode.enabled = true;
            view.btnSave.enabled = true;
        } else if (_logServiceConfig.serviceMode == 20) {
            view.serviceMode.selectedIndex = 2;
            view.customLogData.includeInLayout = true;
            view.customLogData.visible = true;
            view.serviceMode.enabled = false;
            view.btnSave.enabled = false;
        }
        
        view.customLogData.dataProvider = _logServiceConfig.configProperties;
        view.customLogData.validateNow();
    }

    protected function get view():LoggingSettingsView {
        return viewComponent as LoggingSettingsView;
    }

    override public function registerValidators():void {
        _validators = [];
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
