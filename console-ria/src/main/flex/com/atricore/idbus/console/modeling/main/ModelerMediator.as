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
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceListLoadCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceUpdateCommand;
import com.atricore.idbus.console.modeling.main.controller.LookupIdentityApplianceByIdCommand;
import com.atricore.idbus.console.modeling.main.view.*;
import com.atricore.idbus.console.modeling.main.view.appliance.IdentityApplianceWizardViewMediator;
import com.atricore.idbus.console.modeling.main.view.build.BuildApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.deploy.DeployApplianceMediator;
import com.atricore.idbus.console.modeling.main.view.sso.SimpleSSOWizardViewMediator;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceState;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.events.FlexEvent;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ModelerMediator extends IocMediator implements IDisposable {

    public static const viewName:String = "ModelerView";

    public static const BUNDLE:String = "console";

    public static const ORIENTATION_MENU_ITEM_INDEX:int = 3;

    private var _projectProxy:ProjectProxy;

    private var _identityAppliance:IdentityAppliance;

    private var _emptyNotationModel:XML;

    private var _popupManager:ModelerPopUpManager;

    [Bindable]
    public var _applianceList:Array;

    private var _created:Boolean;
    private var _browserMediator:IIocMediator;
    private var _diagramMediator:IIocMediator;
    private var _paletteMediator:IIocMediator;
    private var _propertySheetMediator:IIocMediator;

    public function ModelerMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    public function get browserMediator():IIocMediator {
        return _browserMediator;
    }

    public function set browserMediator(value:IIocMediator):void {
        _browserMediator = value;
    }

    public function get diagramMediator():IIocMediator {
        return _diagramMediator;
    }

    public function set diagramMediator(value:IIocMediator):void {
        _diagramMediator = value;
    }

    public function get paletteMediator():IIocMediator {
        return _paletteMediator;
    }

    public function set paletteMediator(value:IIocMediator):void {
        _paletteMediator = value;
    }

    public function get propertySheetMediator():IIocMediator {
        return _propertySheetMediator;
    }

    public function set propertySheetMediator(value:IIocMediator):void {
        _propertySheetMediator = value;
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }


    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }


    public function get popupManager():ModelerPopUpManager {
        return _popupManager;
    }

    public function set popupManager(value:ModelerPopUpManager):void {
        _popupManager = value;
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        //this is not working for first viewStack child?
        (p_viewComponent as ModelerView).addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        super.setViewComponent(p_viewComponent);

    }

    private function creationCompleteHandler(event:Event):void {
        _created = true;

        /* Remove unused title in both modeler's and diagram's panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;
        view.diagram.titleDisplay.width = 0;
        view.diagram.titleDisplay.height = 0;

        browserMediator.setViewComponent(view.browser);
        diagramMediator.setViewComponent(view.diagram);
        paletteMediator.setViewComponent(view.palette);
        propertySheetMediator.setViewComponent(view.propertysheet);

        view.btnNew.addEventListener(MouseEvent.CLICK, handleNewClick);
        view.btnOpen.addEventListener(MouseEvent.CLICK, handleOpenClick);
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSaveClick);
        //view.btnLifecycle.addEventListener(MouseEvent.CLICK, handleLifecycleClick);

        view.appliances.labelFunction = applianceListLabelFunc;
        view.btnSave.enabled = false;

        popupManager.init(iocFacade, view);

        init();
    }

    public function init():void {
        if (_created) {
            sendNotification(ApplicationFacade.CLEAR_MSG);
            view.btnSave.enabled = false;
            if (projectProxy.currentIdentityAppliance != null) {
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                enableIdentityApplianceActionButtons();
            }/* else {
             view.btnLifecycle.enabled = false;
             }*/
            // TODO: delete IF condition (fetch list every time modeler is opened)?
            if (projectProxy.identityApplianceList == null) {
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
            }
        }
    }

    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        view.btnNew.removeEventListener(MouseEvent.CLICK, handleNewClick);
        view.btnOpen.removeEventListener(MouseEvent.CLICK, handleOpenClick);
        view.btnSave.removeEventListener(MouseEvent.CLICK, handleSaveClick);

        _identityAppliance = null;
        _emptyNotationModel = null;
        _popupManager = null;
        _applianceList = null;
        _created = null;
        _browserMediator = null;
        _diagramMediator = null;
        _paletteMediator = null;
        _propertySheetMediator = null;
        view = null;
    }

    private function handleNewClick(event:MouseEvent):void {
        trace("New Button Click: " + event);
        if (view.btnSave.enabled) {
            var buttonWidth:Number = Alert.buttonWidth;
            Alert.buttonWidth = 80;
            Alert.okLabel = "Continue";
            Alert.show("There are unsaved changes which will be lost in case you choose to continue.",
                    "Confirm", Alert.OK | Alert.CANCEL, null, newApplianceConfirmed, null, Alert.OK);
            Alert.buttonWidth = buttonWidth;
            Alert.okLabel = "OK";
        } else {
            openNewApplianceWizard();
        }
    }

    private function newApplianceConfirmed(event:CloseEvent):void {
        if (event.detail == Alert.OK) {
            openNewApplianceWizard();
        }
    }

    private function openNewApplianceWizard():void {
        if (view.applianceStyle.selectedItem.data == "Advanced") {
            sendNotification(IdentityApplianceWizardViewMediator.RUN);
        } else if (view.applianceStyle.selectedItem.data == "SimpleSSO") {
            sendNotification(SimpleSSOWizardViewMediator.RUN);
        }
    }

    private function handleOpenClick(event:MouseEvent):void {
        trace("Open Button Click: " + event);
        if (view.appliances.selectedItem != null) {
            var applianceId:String = (view.appliances.selectedItem as IdentityAppliance).id.toString();
            sendNotification(ProcessingMediator.START, "Opening Identity Appliance...");
            sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, applianceId);
        }
    }

    private function handleSaveClick(event:MouseEvent):void {
        trace("Save Button Click: " + event);
        sendNotification(ProcessingMediator.START, "Saving Identity Appliance...");
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE);
    }

    private function handleLifecycleClick(event:MouseEvent):void {
        trace("Lifecycle Button Click: " + event);
        if (view.btnSave.enabled) {
            sendNotification(ApplicationFacade.SHOW_ERROR_MSG, "Identity appliance not saved!");
        } else {
            sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_LIFECYCLE);
        }
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.MODELER_VIEW_SELECTED,
            ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT,
            ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_IDENTITY_VAULT_ELEMENT,
            ApplicationFacade.CREATE_DB_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_LDAP_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_XML_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.REMOVE_ACTIVATION_ELEMENT,
            ApplicationFacade.REMOVE_FEDERATED_CONNECTION_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_LOOKUP_ELEMENT,
            ApplicationFacade.REMOVE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_FEDERATED_CONNECTION,
            ApplicationFacade.MANAGE_CERTIFICATE,
            ApplicationFacade.SHOW_UPLOAD_PROGRESS,
            ApplicationFacade.IDENTITY_APPLIANCE_CHANGED,
            ApplicationFacade.CREATE_ACTIVATION,
            ApplicationFacade.APPLIANCE_VALIDATION_ERRORS,
            //ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT,
            ApplicationFacade.LOGOUT,
            BuildApplianceMediator.RUN,
            DeployApplianceMediator.RUN,
            LookupIdentityApplianceByIdCommand.SUCCESS,
            LookupIdentityApplianceByIdCommand.FAILURE,
            IdentityApplianceListLoadCommand.SUCCESS,
            IdentityApplianceListLoadCommand.FAILURE,
            IdentityApplianceUpdateCommand.SUCCESS,
            IdentityApplianceUpdateCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.MODELER_VIEW_SELECTED:
                projectProxy.currentView = viewName;
                init();
                break;
            case ApplicationFacade.UPDATE_IDENTITY_APPLIANCE:
                updateIdentityAppliance();
                enableIdentityApplianceActionButtons();
                break;
            case ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT:
                var ria:RemoveIdentityApplianceElementRequest = RemoveIdentityApplianceElementRequest(notification.getBody());
                // TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_REMOVE, ria.identityAppliance);
                break;
            case ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT:
                popupManager.showCreateIdentityProviderWindow(notification);
                break;
            case ApplicationFacade.REMOVE_IDENTITY_PROVIDER_ELEMENT:
                var rip:RemoveIdentityProviderElementRequest = RemoveIdentityProviderElementRequest(notification.getBody());
                // TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.IDENTITY_PROVIDER_REMOVE, rip.identityProvider);
                break;
            case ApplicationFacade.CREATE_SERVICE_PROVIDER_ELEMENT:
                popupManager.showCreateServiceProviderWindow(notification);
                break;
            case ApplicationFacade.REMOVE_SERVICE_PROVIDER_ELEMENT:
                var rsp:RemoveServiceProviderElementRequest = RemoveServiceProviderElementRequest(notification.getBody());
                //                 TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.SERVICE_PROVIDER_REMOVE, rsp.serviceProvider);
                break;
            //            case ApplicationFacade.CREATE_IDP_CHANNEL_ELEMENT:
            //                popupManager.showCreateIdpChannelWindow(notification);
            //                break;
            //            case ApplicationFacade.REMOVE_IDP_CHANNEL_ELEMENT:
            //                var ridpc:RemoveIdpChannelElementRequest = RemoveIdpChannelElementRequest(notification.getBody());
            //                //                 TODO: Perform UI handling for confirming removal action
            //                sendNotification(ApplicationFacade.IDP_CHANNEL_REMOVE, ridpc.idpChannel);
            //                break;
            //            case ApplicationFacade.CREATE_SP_CHANNEL_ELEMENT:
            //                popupManager.showCreateSpChannelWindow(notification);
            //                break;
            //            case ApplicationFacade.REMOVE_SP_CHANNEL_ELEMENT:
            //                var rspc:RemoveSpChannelElementRequest = RemoveSpChannelElementRequest(notification.getBody());
            //                //                 TODO: Perform UI handling for confirming removal action
            //                sendNotification(ApplicationFacade.SP_CHANNEL_REMOVE, rspc.spChannel);
            //                break;
            case ApplicationFacade.CREATE_IDENTITY_VAULT_ELEMENT:
                popupManager.showCreateIdentityVaultWindow(notification);
                break;
            case ApplicationFacade.CREATE_DB_IDENTITY_SOURCE_ELEMENT:
                popupManager.showCreateDbIdentitySourceWindow(notification);
                break;
            case ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT:
                var riv:RemoveIdentityVaultElementRequest = RemoveIdentityVaultElementRequest(notification.getBody());
                //                 TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.IDENTITY_SOURCE_REMOVE, riv.identityVault);
                break;
            case ApplicationFacade.CREATE_LDAP_IDENTITY_SOURCE_ELEMENT:
                popupManager.showCreateLdapIdentitySourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_XML_IDENTITY_SOURCE_ELEMENT:
                popupManager.showCreateXmlIdentitySourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateJBossExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateWeblogicExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateTomcatExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateJBossPortalExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateLiferayPortalExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateWASCEExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateApacheExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateWindowsIISExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_ACTIVATION:
                popupManager.showCreateActivationWindow(notification);
                break;
            case ApplicationFacade.REMOVE_ACTIVATION_ELEMENT:
                var ract:RemoveActivationElementRequest = RemoveActivationElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.ACTIVATION_REMOVE, ract.activation);
                break;
            case ApplicationFacade.CREATE_FEDERATED_CONNECTION:
                popupManager.showCreateFederatedConnectionWindow(notification);
                break;
            case ApplicationFacade.REMOVE_FEDERATED_CONNECTION_ELEMENT:
                var rfc:RemoveFederatedConnectionElementRequest = RemoveFederatedConnectionElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.FEDERATED_CONNECTION_REMOVE, rfc.federatedConnection);
                break;
            case ApplicationFacade.REMOVE_IDENTITY_LOOKUP_ELEMENT:
                var ril:RemoveIdentityLookupElementRequest = RemoveIdentityLookupElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.IDENTITY_LOOKUP_REMOVE, ril.identityLookup);
                break;
            case ApplicationFacade.REMOVE_EXECUTION_ENVIRONMENT_ELEMENT:
                var rev:RemoveExecutionEnvironmentElementRequest = RemoveExecutionEnvironmentElementRequest(notification.getBody());
                //                 TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.EXECUTION_ENVIRONMENT_REMOVE, rev.executionEnvironment);
                break;
            case ApplicationFacade.MANAGE_CERTIFICATE:
                popupManager.showManageCertificateWindow(notification);
                break;
            case ApplicationFacade.SHOW_UPLOAD_PROGRESS:
                popupManager.showUploadProgressWindow(notification);
                break;
            case ApplicationFacade.IDENTITY_APPLIANCE_CHANGED:
                if (projectProxy.currentIdentityAppliance.state != IdentityApplianceState.DISPOSED.name) {
                    view.btnSave.enabled = true;
                }
                break;
            case ApplicationFacade.LOGOUT:
                this.dispose();
                break;
            case BuildApplianceMediator.RUN:
                popupManager.showBuildIdentityApplianceWindow(notification);
                break;
            case DeployApplianceMediator.RUN:
                popupManager.showDeployIdentityApplianceWindow(notification);
                break;
            case LookupIdentityApplianceByIdCommand.SUCCESS:
                view.btnSave.enabled = false;
                //view.btnLifecycle.enabled = false;
                enableIdentityApplianceActionButtons();
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                //                sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                //                        "Appliance successfully opened.");
                break;
            case LookupIdentityApplianceByIdCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error opening appliance.");
                break;
            case IdentityApplianceListLoadCommand.SUCCESS:
                view.appliances.dataProvider = projectProxy.identityApplianceList;
                break;
            case IdentityApplianceListLoadCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error retrieving list of appliances.");
                break;
            case IdentityApplianceUpdateCommand.SUCCESS:
                view.btnSave.enabled = false;
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                sendNotification(ApplicationFacade.UPDATE_DIAGRAM_ELEMENTS_DATA);
                //                sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
                //                        "Appliance successfully updated.");
                break;
            case IdentityApplianceUpdateCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error updating appliance.");
                break;
            case ApplicationFacade.APPLIANCE_VALIDATION_ERRORS:
                sendNotification(ProcessingMediator.STOP);
                var validationErrors:ArrayCollection = projectProxy.identityApplianceValidationErrors;
                if (validationErrors != null && validationErrors.length > 0) {
                    var msg:String = "";
                    for each (var validationError:String in validationErrors) {
                        if (validationErrors.length > 1) {
                            msg += "* ";
                        }
                        msg += validationError + "\n";
                    }
                    Alert.show(msg, "Validation Errors");
                }
                projectProxy.identityApplianceValidationErrors = null;
                break;
        }

    }

    private function updateIdentityAppliance():void {
        _identityAppliance = projectProxy.currentIdentityAppliance;
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
    }

    private function enableIdentityApplianceActionButtons():void {
        var appliance:IdentityAppliance = projectProxy.currentIdentityAppliance;
        /*if (appliance != null && appliance.idApplianceDeployment == null) {
         view.btnLifecycle.enabled = true;
         }*/
    }

    private function applianceListLabelFunc(item:Object):String {
        return (item as IdentityAppliance).idApplianceDefinition.name;
    }

    protected function get view():ModelerView
    {
        return viewComponent as ModelerView;
    }

    protected function set view(md:ModelerView):void
    {
        viewComponent = md;
    }
}
}