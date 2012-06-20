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

package com.atricore.idbus.console.config.branding
{
import com.atricore.idbus.console.config.branding.event.BrandingGridEvent;
import com.atricore.idbus.console.config.branding.view.BrandingPopUpManager;
import com.atricore.idbus.console.config.main.controller.CreateBrandingCommand;
import com.atricore.idbus.console.config.main.controller.ListBrandingsCommand;
import com.atricore.idbus.console.config.main.controller.RemoveBrandingCommand;
import com.atricore.idbus.console.config.main.controller.UpdateBrandingCommand;
import com.atricore.idbus.console.config.main.model.ServiceConfigProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;

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

public class BrandingServiceMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    private var _popupManager:BrandingPopUpManager;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    //commands
    //private var _getServiceConfigCommand:GetServiceConfigCommand;
    //private var _updateServiceConfigCommand:UpdateServiceConfigCommand;

    private var _created:Boolean;

    [Bindable]
    public var _brandings:ArrayCollection;

    //private var _brandingServiceConfig:BrandingServiceConfiguration;

    public function BrandingServiceMediator(name:String = null, viewComp:BrandingServiceView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.LIST_BRANDINGS);
        }
        (viewComponent as BrandingServiceView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            view.btnCreate.addEventListener(MouseEvent.CLICK, handleCreate);
            BindingUtils.bindProperty(view.brandings, "dataProvider", this, "_brandings");
            view.brandings.addEventListener(BrandingGridEvent.CLICK, handleBrandingGridEvent);
            sendNotification(ApplicationFacade.LIST_BRANDINGS);
            popupManager.init(iocFacade, view);
        }
    }

    override public function listNotificationInterests():Array {
        return [ ListBrandingsCommand.SUCCESS,
            ListBrandingsCommand.FAILURE,
            CreateBrandingCommand.SUCCESS,
            CreateBrandingCommand.FAILURE,
            UpdateBrandingCommand.SUCCESS,
            UpdateBrandingCommand.FAILURE,
            RemoveBrandingCommand.SUCCESS,
            RemoveBrandingCommand.FAILURE,
            ApplicationFacade.DISPLAY_CREATE_BRANDING_WIZARD];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ListBrandingsCommand.SUCCESS:
                sendNotification(ProcessingMediator.STOP);
                _brandings = _configProxy.brandingDefinitions;
                break;
            case ListBrandingsCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.list.error"));
                break;
            case ApplicationFacade.DISPLAY_CREATE_BRANDING_WIZARD:
                popupManager.showCreateBrandingWizardWindow(notification);
                break;
            case CreateBrandingCommand.SUCCESS:
                sendNotification(ApplicationFacade.LIST_BRANDINGS);
                break;
            case CreateBrandingCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.error"));
                break;
            case UpdateBrandingCommand.SUCCESS:
                sendNotification(ApplicationFacade.LIST_BRANDINGS);
                break;
            case UpdateBrandingCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.error"));
                break;
            case RemoveBrandingCommand.SUCCESS:
                sendNotification(ApplicationFacade.LIST_BRANDINGS);
                break;
            case RemoveBrandingCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.remove.error"));
                break;
        }
    }

    override public function bindModel():void {
        //_brandingServiceConfig.serviceMode = view.serviceMode.selectedItem.data;
    }

    private function handleCreate(event:MouseEvent):void {
        sendNotification(ApplicationFacade.DISPLAY_CREATE_BRANDING_WIZARD);
        /*if (validate(true)) {
            bindModel();
            //sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "config.branding.save.progress"));
            //sendNotification(ApplicationFacade.UPDATE_SERVICE_CONFIG, _brandingServiceConfig);
        }
        else {
            event.stopImmediatePropagation();
        }*/
    }

    /*public function displayServiceConfig():void {
        _brandingServiceConfig = _configProxy.brandingService;

        var serviceMods:ArrayCollection = new ArrayCollection();
        serviceMods.addItem({data:0,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.serviceMode.development')});
        serviceMods.addItem({data:10,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.serviceMode.production')});
        if (_brandingServiceConfig.serviceMode == 20) {
            serviceMods.addItem({data:20,label:resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.serviceMode.custom')});
        }
        view.serviceMode.dataProvider = serviceMods;
        view.serviceMode.validateNow();

        if (_brandingServiceConfig.serviceMode == 0) {
            view.serviceMode.selectedIndex = 0;
            view.customBrandingData.visible = false;
            view.customBrandingData.includeInLayout = false;
            view.serviceMode.enabled = true;
            view.btnSave.enabled = true;
        } else if (_brandingServiceConfig.serviceMode == 10) {
            view.serviceMode.selectedIndex = 1;
            view.customBrandingData.visible = false;
            view.customBrandingData.includeInLayout = false;
            view.serviceMode.enabled = true;
            view.btnSave.enabled = true;
        } else if (_brandingServiceConfig.serviceMode == 20) {
            view.serviceMode.selectedIndex = 2;
            view.customBrandingData.includeInLayout = true;
            view.customBrandingData.visible = true;
            view.serviceMode.enabled = false;
            view.btnSave.enabled = false;
        }
        
        view.customBrandingData.dataProvider = _brandingServiceConfig.configProperties;
        view.customBrandingData.validateNow();
    }*/

    private function handleBrandingGridEvent(event:BrandingGridEvent):void {
        var id:Number = event.data.id;
        switch (event.action) {
            case BrandingGridEvent.ACTION_EDIT:
                //sendNotification(ApplicationFacade.EDIT_BRANDING, event.data);
                break;
            case BrandingGridEvent.ACTION_REMOVE:
                var delAlert:Alert = Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.remove.confirmation.message', [event.data.name]),
                        resourceManager.getString(AtricoreConsole.BUNDLE, 'config.branding.remove.confirmation.title'),
                        3, null,
                        function (event:CloseEvent):void {
                            if (event.detail == Alert.YES) {
                                sendNotification(ApplicationFacade.REMOVE_BRANDING, id);
                                sendNotification(ProcessingMediator.START);
                            } else {
                                PopUpManager.removePopUp(delAlert);
                            }
                        }
                );
                break;
        }
    }

    protected function get view():BrandingServiceView {
        return viewComponent as BrandingServiceView;
    }

    override public function registerValidators():void {
        _validators = [];
    }

    public function set configProxy(value:ServiceConfigProxy):void {
        _configProxy = value;
    }

    public function get popupManager():BrandingPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:BrandingPopUpManager):void {
        _popupManager = value;
    }

    /*public function get getServiceConfigCommand():GetServiceConfigCommand {
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
    }*/

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }    
}
}
