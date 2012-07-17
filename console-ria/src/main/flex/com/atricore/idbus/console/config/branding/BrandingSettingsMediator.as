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
import com.atricore.idbus.console.config.branding.view.edit.EditCustomBrandingViewMediator;
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
import mx.core.IVisualElement;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

import spark.components.Group;

public class BrandingSettingsMediator extends IocFormMediator implements IDisposable {

    private var _configProxy:ServiceConfigProxy;

    private var _popupManager:BrandingPopUpManager;

    protected var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _created:Boolean;

    [Bindable]
    public var _brandings:ArrayCollection;

    private var _editCustomBrandingViewMediatorName:String;
    private var _editCustomBrandingViewName:String;

    public function BrandingSettingsMediator(name:String = null, viewComp:BrandingSettingsView = null) {
        super(name, viewComp);
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (_created) {
            sendNotification(ApplicationFacade.LIST_BRANDINGS);
        }
        (viewComponent as BrandingSettingsView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
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
            ApplicationFacade.DISPLAY_CREATE_BRANDING_WIZARD,
            ApplicationFacade.DISPLAY_EDIT_BRANDING];
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
            case ApplicationFacade.DISPLAY_EDIT_BRANDING:
                var editCustomBrandingViewMediator:IIocMediator = iocFacade.container.getObject(editCustomBrandingViewMediatorName) as IIocMediator;
                var editCustomBrandingView:IVisualElement = iocFacade.container.getObject(editCustomBrandingViewName) as IVisualElement;
                var parentGroup:Group = view.parent as Group;
                parentGroup.removeAllElements();
                parentGroup.addElement(editCustomBrandingView);
                (editCustomBrandingViewMediator as EditCustomBrandingViewMediator).lookupId = notification.getBody() as Number;
                editCustomBrandingViewMediator.setViewComponent(editCustomBrandingView);
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

    private function handleCreate(event:MouseEvent):void {
        sendNotification(ApplicationFacade.DISPLAY_CREATE_BRANDING_WIZARD);
    }

    private function handleBrandingGridEvent(event:BrandingGridEvent):void {
        var id:Number = event.data.id;
        switch (event.action) {
            case BrandingGridEvent.ACTION_EDIT:
                sendNotification(ApplicationFacade.DISPLAY_EDIT_BRANDING, id);
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

    protected function get view():BrandingSettingsView {
        return viewComponent as BrandingSettingsView;
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

    public function get editCustomBrandingViewMediatorName():String {
        return _editCustomBrandingViewMediatorName;
    }

    public function set editCustomBrandingViewMediatorName(value:String):void {
        _editCustomBrandingViewMediatorName = value;
    }

    public function get editCustomBrandingViewName():String {
        return _editCustomBrandingViewName;
    }

    public function set editCustomBrandingViewName(value:String):void {
        _editCustomBrandingViewName = value;
    }

    public function dispose():void {
        // Clean up
        setViewComponent(null);
    }    
}
}
