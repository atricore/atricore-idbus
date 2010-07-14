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

package com.atricore.idbus.console.modeling.main {
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

import flash.events.MouseEvent;

import mx.controls.ButtonBar;
import mx.controls.buttonBarClasses.ButtonBarButton;
import mx.events.ItemClickEvent;

import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.browser.BrowserMediator;
import com.atricore.idbus.console.modeling.diagram.DiagramMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.main.view.*;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.modeling.propertysheet.PropertySheetMediator;
import org.puremvc.as3.interfaces.INotification;
import org.puremvc.as3.patterns.mediator.Mediator;

public class ModelerMediator extends Mediator {

    public static const BUNDLE:String = "console";

    public static const ORIENTATION_MENU_ITEM_INDEX:int = 3;

    private static const MODEL_ACTION_BAR_NEW_BUTTON_IDX:int = 0;

    private static const MODEL_ACTION_BAR_BUILD_BUTTON_IDX:int = 0;

    private static const MODEL_ACTION_BAR_DEPLOY_BUTTON_IDX:int = 1;


    private var _modelActionToolBar:ButtonBar;

    private var _identityAppliance:IdentityApplianceDTO;

    private var _emptyNotationModel:XML;

    private var _modelerPopUpManager:ModelerPopUpManager;

    public static const NAME:String = "ModelMediator";

    public function ModelerMediator(viewComp:ModelerView) {
        super(NAME, viewComp);

        // register mediators for child components
        facade.registerMediator(new BrowserMediator(viewComp.browser));
        facade.registerMediator(new DiagramMediator(viewComp.diagram));
        facade.registerMediator(new PaletteMediator(viewComp.palette));
        facade.registerMediator(new PropertySheetMediator(viewComp.propertysheet));

        _modelActionToolBar = viewComp.modelActionToolBar;

        (_modelActionToolBar.getChildAt(MODEL_ACTION_BAR_NEW_BUTTON_IDX) as ButtonBarButton).enabled = true;
        (_modelActionToolBar.getChildAt(MODEL_ACTION_BAR_BUILD_BUTTON_IDX) as ButtonBarButton).enabled = false;
        (_modelActionToolBar.getChildAt(MODEL_ACTION_BAR_DEPLOY_BUTTON_IDX) as ButtonBarButton).enabled = false;

        viewComp.btnNew.addEventListener(MouseEvent.CLICK, handleNewClick);
        _modelActionToolBar.addEventListener(ItemClickEvent.ITEM_CLICK, handleModelActionToolBarClick);

        _modelerPopUpManager = new ModelerPopUpManager(facade, viewComp);
    }

    private function handleNewClick(event:MouseEvent):void {
        trace("New Button Click: " + event);
        if (view.applianceStyle.selectedItem.data == "Advanced") {
            sendNotification(IdentityApplianceMediator.CREATE);
        } else if (view.applianceStyle.selectedItem.data == "SimpleSSO") {
            sendNotification(SimpleSSOWizardViewMediator.RUN);
        }
    }

    private function handleModelActionToolBarClick(event:ItemClickEvent):void {

        if (event.index == 0) {
            trace("New Button Click: " + event);
            sendNotification(IdentityApplianceMediator.CREATE)
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.NOTE_CREATE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.NOTE_REMOVE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.NOTE_MANAGE_CERTIFICATE,
            ApplicationFacade.NOTE_SHOW_UPLOAD_PROGRESS,
            ProcessingMediator.START];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.NOTE_UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                enableIdentityApplianceActionButtons();
                break;
            case ApplicationFacade.NOTE_CREATE_IDENTITY_PROVIDER_ELEMENT:
                _modelerPopUpManager.showCreateIdentityProviderWindow(notification);
                break;
            case ApplicationFacade.NOTE_REMOVE_IDENTITY_PROVIDER_ELEMENT:
                var rip:RemoveIdentityProviderElementRequest  = RemoveIdentityProviderElementRequest(notification.getBody());
                // TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.NOTE_IDENTITY_PROVIDER_REMOVE, rip.identityProvider);
                break;
            case ApplicationFacade.NOTE_MANAGE_CERTIFICATE:
                _modelerPopUpManager.showManageCertificateWindow(notification);
                break;
            case ApplicationFacade.NOTE_SHOW_UPLOAD_PROGRESS:
                _modelerPopUpManager.showUploadProgressWindow(notification);
                break;
            case ProcessingMediator.START:
                _modelerPopUpManager.showProcessingWindow(notification);
                break;
        }

    }

    private function updateIdentityAppliance():void {

        var proxy:ProjectProxy = facade.retrieveProxy(ProjectProxy.NAME) as ProjectProxy;
        _identityAppliance = proxy.currentIdentityAppliance;
    }

    private function enableIdentityApplianceActionButtons():void {
        (_modelActionToolBar.getChildAt(MODEL_ACTION_BAR_BUILD_BUTTON_IDX) as ButtonBarButton).enabled = true;
        (_modelActionToolBar.getChildAt(MODEL_ACTION_BAR_DEPLOY_BUTTON_IDX) as ButtonBarButton).enabled = true;

        // TODO: associate behavior to build and deploy buttons

    }


    protected function get view():ModelerView
    {
        return viewComponent as ModelerView;
    }
}
}