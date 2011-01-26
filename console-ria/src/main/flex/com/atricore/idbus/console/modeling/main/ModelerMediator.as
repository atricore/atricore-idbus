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
import com.atricore.idbus.console.modeling.browser.BrowserMediator;
import com.atricore.idbus.console.modeling.diagram.DiagramMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveGoogleAppsElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSalesforceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceImportCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceListLoadCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceUpdateCommand;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
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
import flash.net.FileFilter;
import flash.net.FileReference;

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

    private var _tempSelectedViewIndex:int;

    private var _fileRef:FileReference;


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
        _tempSelectedViewIndex = -1;

        event.target.removeEventListener(FlexEvent.CREATION_COMPLETE,creationCompleteHandler);

        /* Remove unused title in both modeler's and diagram's panel */
        view.titleDisplay.width = 0;
        view.titleDisplay.height = 0;
        view.diagram.titleDisplay.width = 0;
        view.diagram.titleDisplay.height = 0;

        browserMediator.setViewComponent(view.browser);
        diagramMediator.setViewComponent(view.diagram);
        paletteMediator.setViewComponent(view.palette);
        propertySheetMediator.setViewComponent(view.propertysheet);

        view.appliances.labelFunction = applianceListLabelFunc;
        view.btnSave.enabled = false;
        view.btnExport.enabled = false;

        popupManager.init(iocFacade, view);

        setupListeners(null); //setup listeners for the first time
        view.addEventListener(Event.ADDED_TO_STAGE, setupListeners);

        init();
    }

    public function init():void {
        if (_created) {
            sendNotification(ApplicationFacade.CLEAR_MSG);
            view.btnSave.enabled = false;
            view.btnExport.enabled = false;
            if (projectProxy.currentIdentityAppliance != null &&
                    projectProxy.currentIdentityAppliance.state != IdentityApplianceState.DISPOSED.name) {
                view.btnExport.enabled = true;
                sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID,
                        projectProxy.currentIdentityAppliance.id.toString());
                //sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                //enableIdentityApplianceActionButtons();
            } else {
                projectProxy.currentIdentityAppliance = null;
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
            }
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
        }
    }

    private function setupListeners(event:Event):void {
        view.btnNew.addEventListener(MouseEvent.CLICK, handleNewClick);
        view.btnOpen.addEventListener(MouseEvent.CLICK, handleOpenClick);
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSaveClick);
        view.btnImport.addEventListener(MouseEvent.CLICK, handleImportClick);
        view.btnExport.addEventListener(MouseEvent.CLICK, handleExportClick);
    }
    public function dispose():void {
        // Clean up:
        //      - Remove event listeners
        //      - Stop timers
        //      - Set references to null

        _identityAppliance = null;
        _tempSelectedViewIndex = -1;

        view.btnSave.enabled = false;
        view.btnExport.enabled = false;
        view.appliances.selectedItem = null;
        (browserMediator as BrowserMediator).dispose();
        (diagramMediator as DiagramMediator).dispose();
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

    private function handleImportClick(event:MouseEvent):void {
        trace("Import Button Click: " + event);
        _fileRef = new FileReference();
        var appTypes:FileFilter = new FileFilter("Appliances Files (*.zip, *.jar)", "*.zip; *.jar");
        _fileRef.addEventListener(Event.SELECT, selectFileOpenHandler);
        _fileRef.addEventListener(Event.COMPLETE, completeFileOpenHandler);
        _fileRef.browse(new Array(appTypes));
    }

    private function handleExportClick(event:MouseEvent):void {
        trace("Export Button Click: " + event);
        sendNotification(ApplicationFacade.EXPORT_IDENTITY_APPLIANCE);
    }

    override public function listNotificationInterests():Array {
        return [ApplicationFacade.MODELER_VIEW_SELECTED,
            ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT,
            ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_EXTERNAL_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_SALESFORCE_ELEMENT,
            ApplicationFacade.REMOVE_GOOGLE_APPS_ELEMENT,
            ApplicationFacade.CREATE_SALESFORCE_ELEMENT,
            ApplicationFacade.CREATE_GOOGLE_APPS_ELEMENT,
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
            ApplicationFacade.CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT,
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
            ApplicationFacade.AUTOSAVE_IDENTITY_APPLIANCE,
            ApplicationFacade.EXPORT_IDENTITY_APPLIANCE,
            ApplicationFacade.EXPORT_PROVIDER_CERTIFICATE,
            ApplicationFacade.EXPORT_METADATA,
            BuildApplianceMediator.RUN,
            DeployApplianceMediator.RUN,
            LookupIdentityApplianceByIdCommand.SUCCESS,
            LookupIdentityApplianceByIdCommand.FAILURE,
            IdentityApplianceListLoadCommand.SUCCESS,
            IdentityApplianceListLoadCommand.FAILURE,
            IdentityApplianceUpdateCommand.SUCCESS,
            IdentityApplianceUpdateCommand.FAILURE,
            IdentityApplianceImportCommand.SUCCESS,
            IdentityApplianceImportCommand.FAILURE,
            JDBCDriversListCommand.FAILURE];
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
            case ApplicationFacade.CREATE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT:
                popupManager.showCreateExternalIdentityProviderWindow(notification);
                break;
            case ApplicationFacade.REMOVE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT:
                var reip:RemoveExternalIdentityProviderElementRequest = RemoveExternalIdentityProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_IDENTITY_PROVIDER_REMOVE, reip.identityProvider);
                break;
            case ApplicationFacade.CREATE_EXTERNAL_SERVICE_PROVIDER_ELEMENT:
                popupManager.showCreateExternalServiceProviderWindow(notification);
                break;
            case ApplicationFacade.REMOVE_EXTERNAL_SERVICE_PROVIDER_ELEMENT:
                var resp:RemoveExternalServiceProviderElementRequest = RemoveExternalServiceProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SERVICE_PROVIDER_REMOVE, resp.serviceProvider);
                break;
            case ApplicationFacade.CREATE_SALESFORCE_ELEMENT:
                popupManager.showCreateSalesforceWindow(notification);
                break;
            case ApplicationFacade.REMOVE_SALESFORCE_ELEMENT:
                var rsf:RemoveSalesforceElementRequest = RemoveSalesforceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SERVICE_PROVIDER_REMOVE, rsf.salesforceProvider);
                break;
            case ApplicationFacade.CREATE_GOOGLE_APPS_ELEMENT:
                popupManager.showCreateGoogleAppsWindow(notification);
                break;
            case ApplicationFacade.REMOVE_GOOGLE_APPS_ELEMENT:
                var rga:RemoveGoogleAppsElementRequest = RemoveGoogleAppsElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SERVICE_PROVIDER_REMOVE, rga.googleAppsProvider);
                break;
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
            case ApplicationFacade.CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateAlfrescoExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateJavaEEExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreatePhpBBExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateWebserverExecutionEnvironmentWindow(notification);
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
                var redrawGraph:Boolean = view.btnSave.enabled;
                view.btnSave.enabled = false;
                sendNotification(ApplicationFacade.APPLIANCE_SAVED);
                view.btnExport.enabled = true;
                //view.btnLifecycle.enabled = false;
                enableIdentityApplianceActionButtons();
                sendNotification(ProcessingMediator.STOP);
                if (projectProxy.currentIdentityAppliance != null &&
                        projectProxy.currentIdentityAppliance.state == IdentityApplianceState.DISPOSED.name) {
                    projectProxy.currentIdentityAppliance = null;
                    view.btnExport.enabled = false;
                }
                sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                //sendNotification(ApplicationFacade.REFRESH_DIAGRAM, redrawGraph);
                sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                break;
            case LookupIdentityApplianceByIdCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error opening appliance.");
                break;
            case IdentityApplianceListLoadCommand.SUCCESS:
                if (projectProxy.identityApplianceList == null) {
                    view.appliances.dataProvider = null;
                } else {
                    var appliances:ArrayCollection = new ArrayCollection();
                    for each (var appliance:IdentityAppliance in projectProxy.identityApplianceList) {
                        if (appliance.state != IdentityApplianceState.DISPOSED.name) {
                            appliances.addItem(appliance);
                        }
                    }
                    view.appliances.dataProvider = appliances;
                }
                break;
            case IdentityApplianceListLoadCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error retrieving list of appliances.");
                break;

            case IdentityApplianceImportCommand.SUCCESS:
                var appID:String = notification.getBody() as String;
                sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, appID);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
                break;
            case IdentityApplianceImportCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error importing appliance. Bad artifact or already exists.");
                break;
            case IdentityApplianceUpdateCommand.SUCCESS:
                var silentUpdate:Boolean = notification.getBody() as Boolean;
                if (!silentUpdate) {
                    var reopenGraph = view.btnSave.enabled;
                    view.btnSave.enabled = false;
                    sendNotification(ApplicationFacade.APPLIANCE_SAVED);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                    //sendNotification(ApplicationFacade.UPDATE_DIAGRAM_ELEMENTS_DATA);
                    sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);  //appliance name might be changed
                    sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                    if (_tempSelectedViewIndex != -1) {
                        sendNotification(ApplicationFacade.DISPLAY_VIEW, _tempSelectedViewIndex);
                        _tempSelectedViewIndex = -1;
                    }
                } else {
                    // TODO: refactor this
                    // this will cause a diagram refresh (everything will be redrawn)
                    // but it's necessary if appliance was created using SSO wizard
                    sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                    sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                }
                break;
            case IdentityApplianceUpdateCommand.FAILURE:
                sendNotification(ProcessingMediator.STOP);
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error updating appliance.");
                if (_tempSelectedViewIndex != -1) {
                    sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                    _tempSelectedViewIndex = -1;
                }
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
                if (_tempSelectedViewIndex != -1) {
                    sendNotification(ApplicationFacade.DISPLAY_APPLIANCE_MODELER);
                    _tempSelectedViewIndex = -1;
                }
                break;
            case ApplicationFacade.AUTOSAVE_IDENTITY_APPLIANCE:
                var selectedIndex:int = notification.getBody() as int;
                if (view.btnSave.enabled) {
                    _tempSelectedViewIndex = selectedIndex;
                    sendNotification(ProcessingMediator.START, "Autosaving Identity Appliance...");
                    sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE);
                } else {
                    sendNotification(ApplicationFacade.DISPLAY_VIEW, selectedIndex);
                }
                break;
            case JDBCDriversListCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        "There was an error loading JDBC drivers list.");
                break;
            case ApplicationFacade.EXPORT_IDENTITY_APPLIANCE:
                if (projectProxy.currentIdentityAppliance != null) {
                    popupManager.showCreateExportIdentityApplianceWindow(notification);
                }
                break;
            case ApplicationFacade.EXPORT_PROVIDER_CERTIFICATE:
                popupManager.showCreateExportProviderCertificateWindow(notification);
                break;
            case ApplicationFacade.EXPORT_METADATA:
                popupManager.showCreateExportMetadataWindow(notification);
                break;
        }

    }

    private function updateIdentityAppliance():void {
        _identityAppliance = projectProxy.currentIdentityAppliance;
        //sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
    }

    private function selectFileOpenHandler(event:Event):void {
        var file:FileReference = FileReference(event.target);
        file.load();
    }

    private function completeFileOpenHandler(event:Event):void {
        //get the data from the file as a ByteArray
        var file:FileReference = FileReference(event.target);
        sendNotification(ApplicationFacade.IMPORT_IDENTITY_APPLIANCE, file.data);
    }

    private function enableIdentityApplianceActionButtons():void {
        var appliance:IdentityAppliance = projectProxy.currentIdentityAppliance;
        if (appliance != null) {
            view.btnExport.enabled = true;
        } else {
            view.btnExport.enabled = false;
        }
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