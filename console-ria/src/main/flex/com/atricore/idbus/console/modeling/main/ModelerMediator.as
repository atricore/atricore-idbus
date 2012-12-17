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
import com.atricore.idbus.console.base.app.BaseAppFacade;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionMediator;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.browser.BrowserMediator;
import com.atricore.idbus.console.modeling.diagram.DiagramMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveActivationElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDelegatedAuthnElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDirectoryServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExecutionEnvironmentElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveFederatedConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveGoogleAppsElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityApplianceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityLookupElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveIdentityVaultElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJBossEPPAuthenticationServiceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJBossEPPResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJOSSO1ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveJOSSO2ResourceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveOAuth2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveOAuth2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalOpenIDIdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSalesforceElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2IdentityProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveExternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveServiceConnectionElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveInternalSaml2ServiceProviderElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveSugarCRMElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveWikidElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveDominoElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveClientCertElementRequest;
import com.atricore.idbus.console.modeling.diagram.model.request.RemoveWindowsIntegratedAuthnElementRequest;
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
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.osmf.traits.IDisposable;
import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class ModelerMediator extends AppSectionMediator implements IDisposable {

    //public static const viewName:String = "ModelerView";

    public static const BUNDLE:String = "console";

    public static const ORIENTATION_MENU_ITEM_INDEX:int = 3;

    private var _projectProxy:ProjectProxy;

    private var _identityAppliance:IdentityAppliance;

    private var _emptyNotationModel:XML;

    private var _popupManager:ModelerPopUpManager;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    [Bindable]
    public var _applianceList:Array;

    private var _created:Boolean;
    private var _browserMediator:IIocMediator;
    private var _diagramMediator:IIocMediator;
    private var _paletteMediator:IIocMediator;
    private var _propertySheetMediator:IIocMediator;

    private var _appSectionChangeInProgress:Boolean;
    private var _exportInProgress:Boolean;

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
        _appSectionChangeInProgress = false;
        _exportInProgress = false;

        event.target.removeEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

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

        if (_created) {
            _created = false;
            _identityAppliance = null;
            _appSectionChangeInProgress = false;
            _exportInProgress = false;

            view.btnSave.enabled = false;
            view.btnExport.enabled = false;
            view.appliances.selectedItem = null;
            (browserMediator as BrowserMediator).dispose();
            (diagramMediator as DiagramMediator).dispose();
        }
    }

    private function handleNewClick(event:MouseEvent):void {
        trace("New Button Click: " + event);
        if (view.btnSave.enabled) {
            var buttonWidth:Number = Alert.buttonWidth;
            Alert.buttonWidth = 80;
            Alert.okLabel = "Continue";
            Alert.show(resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.unsaveddata"),
                    resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.confirm"), Alert.OK | Alert.CANCEL, null, newApplianceConfirmed, null, Alert.OK);
            Alert.buttonWidth = buttonWidth;
            Alert.okLabel = resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.okLabel");
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
            sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.opening.appliance"));
            sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, applianceId);
        }
    }

    private function handleSaveClick(event:MouseEvent):void {
        trace("Save Button Click: " + event);
        sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.saving.appliance"));
        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE);
    }

    private function handleImportClick(event:MouseEvent):void {
        trace("Import Button Click: " + event);
        _fileRef = new FileReference();
        var appTypes:FileFilter = new FileFilter(resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.appliance.files") + "(*.zip, *.jar)", "*.zip; *.jar");
        _fileRef.addEventListener(Event.SELECT, selectFileOpenHandler);
        _fileRef.addEventListener(Event.COMPLETE, completeFileOpenHandler);
        _fileRef.browse(new Array(appTypes));
    }

    private function handleExportClick(event:MouseEvent):void {
        trace("Export Button Click: " + event);
        if (view.btnSave.enabled) {
            _exportInProgress = true;
            sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.autosaving.appliance"));
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE);
        } else {
            sendNotification(ApplicationFacade.EXPORT_IDENTITY_APPLIANCE);
        }
    }

    override public function listNotificationInterests():Array {
        return [
            BaseAppFacade.APP_SECTION_CHANGE_START,
            BaseAppFacade.APP_SECTION_CHANGE_END,
            ApplicationFacade.UPDATE_IDENTITY_APPLIANCE,
            ApplicationFacade.REMOVE_IDENTITY_APPLIANCE_ELEMENT,
            ApplicationFacade.CREATE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_SAML2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_SAML_2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_INTERNAL_SAML_2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_OPENID_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_OPENID_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_OAUTH_2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.CREATE_OAUTH_2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_OPENID_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_OAUTH2_IDENTITY_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_OAUTH2_SERVICE_PROVIDER_ELEMENT,
            ApplicationFacade.REMOVE_SALESFORCE_ELEMENT,
            ApplicationFacade.REMOVE_GOOGLE_APPS_ELEMENT,
            ApplicationFacade.REMOVE_SUGAR_CRM_ELEMENT,
            ApplicationFacade.CREATE_SALESFORCE_ELEMENT,
            ApplicationFacade.CREATE_GOOGLE_APPS_ELEMENT,
            ApplicationFacade.CREATE_SUGAR_CRM_ELEMENT,
            ApplicationFacade.CREATE_IDENTITY_VAULT_ELEMENT,
            ApplicationFacade.CREATE_DB_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_LDAP_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_XML_IDENTITY_SOURCE_ELEMENT,
            ApplicationFacade.CREATE_JBOSSEPP_IDENTITYSOURCE_ELEMENT,
            ApplicationFacade.CREATE_JOSSO1_RESOURCE_ELEMENT,
            ApplicationFacade.CREATE_JOSSO2_RESOURCE_ELEMENT,
            ApplicationFacade.REMOVE_JOSSO1_RESOURCE_ELEMENT,
            ApplicationFacade.REMOVE_JOSSO2_RESOURCE_ELEMENT,
            ApplicationFacade.CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_JBOSSEPP_RESOURCE_ELEMENT,
            ApplicationFacade.CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_PHP_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_SHAREPOINT2010_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_COLDFUSION_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_MICROSTRATEGY_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.REMOVE_ACTIVATION_ELEMENT,
            ApplicationFacade.REMOVE_FEDERATED_CONNECTION_ELEMENT,
            ApplicationFacade.REMOVE_SERVICE_CONNECTION_ELEMENT,
            ApplicationFacade.REMOVE_IDENTITY_LOOKUP_ELEMENT,
            ApplicationFacade.REMOVE_DELEGATED_AUTHENTICATION_ELEMENT,
            ApplicationFacade.REMOVE_EXECUTION_ENVIRONMENT_ELEMENT,
            ApplicationFacade.CREATE_FEDERATED_CONNECTION,
            ApplicationFacade.MANAGE_CERTIFICATE,
            ApplicationFacade.SHOW_UPLOAD_PROGRESS,
            ApplicationFacade.IDENTITY_APPLIANCE_CHANGED,
            //ApplicationFacade.CREATE_ACTIVATION,
            ApplicationFacade.APPLIANCE_VALIDATION_ERRORS,
            //ApplicationFacade.ACTIVATE_EXEC_ENVIRONMENT,
            ApplicationFacade.LOGOUT,
            ApplicationFacade.AUTOSAVE_IDENTITY_APPLIANCE,
            ApplicationFacade.EXPORT_IDENTITY_APPLIANCE,
            ApplicationFacade.EXPORT_PROVIDER_CERTIFICATE,
            ApplicationFacade.EXPORT_METADATA,
            ApplicationFacade.EXPORT_AGENT_CONFIG,
            ApplicationFacade.DISPLAY_ACTIVATION_DIALOG,
            ApplicationFacade.CREATE_WIKID_ELEMENT,
            ApplicationFacade.CREATE_DOMINO_ELEMENT,
            ApplicationFacade.CREATE_CLIENTCERT_ELEMENT,
            ApplicationFacade.REMOVE_WIKID_ELEMENT,
            ApplicationFacade.REMOVE_CLIENTCERT_ELEMENT,
            ApplicationFacade.REMOVE_JBOSSEPP_AUTHENTICATION_SERVICE_ELEMENT,
            ApplicationFacade.REMOVE_DOMINO_ELEMENT,
            ApplicationFacade.CREATE_DIRECTORY_SERVICE_ELEMENT,
            ApplicationFacade.REMOVE_DIRECTORY_SERVICE_ELEMENT,
            ApplicationFacade.CREATE_WINDOWS_INTEGRATED_AUTHN_ELEMENT,
            ApplicationFacade.REMOVE_WINDOWS_INTEGRATED_AUTHN_ELEMENT,
            ApplicationFacade.REMOVE_JBOSSEPP_RESOURCE_ELEMENT,
            BuildApplianceMediator.RUN,
            DeployApplianceMediator.RUN,
            SimpleSSOWizardViewMediator.RUN,
            IdentityApplianceWizardViewMediator.RUN,
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
            case BaseAppFacade.APP_SECTION_CHANGE_START:
                var currentView:String = notification.getBody() as String;
                if (currentView == viewName) {
                    // check for null because we try to open Modeler after login and the view might not be created yet
                    if (view != null && view.btnSave != null && view.btnSave.enabled) {
                        _appSectionChangeInProgress = true;
                        sendNotification(ProcessingMediator.START, resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.autosaving.appliance"));
                        sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_UPDATE);
                    } else {
                        sendNotification(BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED);
                    }
                }
                break;
            case BaseAppFacade.APP_SECTION_CHANGE_END:
                var newView:String = notification.getBody() as String;
                if (newView == viewName) {
                    projectProxy.currentView = viewName;
                    init();
                }
                _appSectionChangeInProgress = false;
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
            case ApplicationFacade.REMOVE_SAML2_SERVICE_PROVIDER_ELEMENT:
                var rsp:RemoveInternalSaml2ServiceProviderElementRequest = RemoveInternalSaml2ServiceProviderElementRequest(notification.getBody());
                //                 TODO: Perform UI handling for confirming removal action
                sendNotification(ApplicationFacade.INTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, rsp.serviceProvider);
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
            case ApplicationFacade.CREATE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT:
                popupManager.showCreateExternalSaml2IdentityProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT:
                popupManager.showCreateExternalSaml2ServiceProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_INTERNAL_SAML_2_SERVICE_PROVIDER_ELEMENT:
                popupManager.showCreateInternalSaml2ServiceProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_OPENID_IDENTITY_PROVIDER_ELEMENT:
                popupManager.showCreateExternalOpenIDIdentityProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_OAUTH_2_IDENTITY_PROVIDER_ELEMENT:
                popupManager.showCreateOAuth2IdentityProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_OAUTH_2_SERVICE_PROVIDER_ELEMENT:
                popupManager.showCreateOAuth2ServiceProviderWindow(notification);
                break;
            case ApplicationFacade.CREATE_SALESFORCE_ELEMENT:
                popupManager.showCreateSalesforceWindow(notification);
                break;
            case ApplicationFacade.REMOVE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT:
                var rs2ip:RemoveExternalSaml2IdentityProviderElementRequest = RemoveExternalSaml2IdentityProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SAML2_IDENTITY_PROVIDER_REMOVE, rs2ip.identityProvider);
                break;
            case ApplicationFacade.REMOVE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT:
                var rs2sp:RemoveExternalSaml2ServiceProviderElementRequest = RemoveExternalSaml2ServiceProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, rs2sp.serviceProvider);
                break;
            case ApplicationFacade.REMOVE_EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT:
                var roidip:RemoveExternalOpenIDIdentityProviderElementRequest = RemoveExternalOpenIDIdentityProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.OPENID_IDENTITY_PROVIDER_REMOVE, roidip.identityProvider);
                break;
            case ApplicationFacade.REMOVE_OAUTH2_IDENTITY_PROVIDER_ELEMENT:
                var roa2ip:RemoveOAuth2IdentityProviderElementRequest = RemoveOAuth2IdentityProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.OAUTH2_IDENTITY_PROVIDER_REMOVE, roa2ip.identityProvider);
                break;
            case ApplicationFacade.REMOVE_OAUTH2_SERVICE_PROVIDER_ELEMENT:
                var roa2sp:RemoveOAuth2ServiceProviderElementRequest = RemoveOAuth2ServiceProviderElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.OAUTH2_SERVICE_PROVIDER_REMOVE, roa2sp.serviceProvider);
                break;
            case ApplicationFacade.REMOVE_SALESFORCE_ELEMENT:
                var rsf:RemoveSalesforceElementRequest = RemoveSalesforceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, rsf.salesforceProvider);
                break;
            case ApplicationFacade.CREATE_GOOGLE_APPS_ELEMENT:
                popupManager.showCreateGoogleAppsWindow(notification);
                break;
            case ApplicationFacade.REMOVE_GOOGLE_APPS_ELEMENT:
                var rga:RemoveGoogleAppsElementRequest = RemoveGoogleAppsElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, rga.googleAppsProvider);
                break;
            case ApplicationFacade.CREATE_SUGAR_CRM_ELEMENT:
                popupManager.showCreateSugarCRMWindow(notification);
                break;
            case ApplicationFacade.REMOVE_SUGAR_CRM_ELEMENT:
                var rscrm:RemoveSugarCRMElementRequest = RemoveSugarCRMElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE, rscrm.sugarCRMProvider);
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
            case ApplicationFacade.CREATE_JBOSSEPP_IDENTITYSOURCE_ELEMENT:
                popupManager.showCreateJBossEPPAuthenticationServiceWindow(notification);
                break;
            case ApplicationFacade.CREATE_JOSSO1_RESOURCE_ELEMENT:
                popupManager.showCreateJOSSO1ResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_JOSSO2_RESOURCE_ELEMENT:
                popupManager.showCreateJOSSO2ResourceWindow(notification);
                break;
            case ApplicationFacade.REMOVE_JOSSO1_RESOURCE_ELEMENT:
                var rj1r:RemoveJOSSO1ResourceElementRequest = RemoveJOSSO1ResourceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.JOSSO1_RESOURCE_REMOVE, rj1r.resource);
                break;
            case ApplicationFacade.REMOVE_JOSSO2_RESOURCE_ELEMENT:
                var rj2r:RemoveJOSSO2ResourceElementRequest = RemoveJOSSO2ResourceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.JOSSO2_RESOURCE_REMOVE, rj2r.resource);
                break;
            case ApplicationFacade.REMOVE_JBOSSEPP_RESOURCE_ELEMENT:
                var rjbeppr:RemoveJBossEPPResourceElementRequest = RemoveJBossEPPResourceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.JBOSSEPP_RESOURCE_REMOVE, rjbeppr.resource);
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
                popupManager.showCreateJBossPortalResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateLiferayPortalResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_JBOSSEPP_RESOURCE_ELEMENT:
                popupManager.showCreateJBossEPPResourceWindow(notification);
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
                popupManager.showCreateAlfrescoResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateJavaEEExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_PHP_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreatePHPExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreatePhpBBResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateWebserverExecutionEnvironmentWindow(notification);
                break;
            case ApplicationFacade.CREATE_SHAREPOINT2010_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateSharepointResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_COLDFUSION_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateColdfusionResourceWindow(notification);
                break;
            case ApplicationFacade.CREATE_MICROSTRATEGY_EXECUTION_ENVIRONMENT_ELEMENT:
                popupManager.showCreateMicroStrategyResourceWindow(notification);
                break;
            /*case ApplicationFacade.CREATE_ACTIVATION:
                popupManager.showCreateActivationWindow(notification);
                break;*/
            case ApplicationFacade.REMOVE_ACTIVATION_ELEMENT:
                var ract:RemoveActivationElementRequest = RemoveActivationElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.ACTIVATION_REMOVE, ract.activation);
                break;
            case ApplicationFacade.REMOVE_SERVICE_CONNECTION_ELEMENT:
                var rsce:RemoveServiceConnectionElementRequest = RemoveServiceConnectionElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.SERVICE_CONNECTION_REMOVE, rsce.serviceConnection);
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
            case ApplicationFacade.REMOVE_DELEGATED_AUTHENTICATION_ELEMENT:
                var rda:RemoveDelegatedAuthnElementRequest = RemoveDelegatedAuthnElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.DELEGATED_AUTHENTICATION_REMOVE, rda.delegatedAuthentication);
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
            case SimpleSSOWizardViewMediator.RUN:
                popupManager.showSimpleSSOWizardWindow(notification);
                break;
            case IdentityApplianceWizardViewMediator.RUN:
                popupManager.showCreateIdentityApplianceWindow(notification);
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
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.opening.error"));                    
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
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.list.error"));                    
                break;

            case IdentityApplianceImportCommand.SUCCESS:
                var appID:String = notification.getBody() as String;
                sendNotification(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, appID);
                sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);
                break;
            case IdentityApplianceImportCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.import.error"));
                break;
            case IdentityApplianceUpdateCommand.SUCCESS:
                var silentUpdate:Boolean = notification.getBody() as Boolean;
                if (!silentUpdate) {
                    var reopenGraph:Boolean = view.btnSave.enabled;
                    view.btnSave.enabled = false;
                    sendNotification(ApplicationFacade.APPLIANCE_SAVED);
                    sendNotification(ProcessingMediator.STOP);
                    sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
                    //sendNotification(ApplicationFacade.UPDATE_DIAGRAM_ELEMENTS_DATA);
                    sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD);  //appliance name might be changed
                    sendNotification(ApplicationFacade.REFRESH_DIAGRAM);
                    if (_appSectionChangeInProgress) {
                        sendNotification(BaseAppFacade.APP_SECTION_CHANGE_CONFIRMED);
                        _appSectionChangeInProgress = false;
                    }
                    if (_exportInProgress) {
                        sendNotification(ApplicationFacade.EXPORT_IDENTITY_APPLIANCE);
                        _exportInProgress = false;
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
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.update.error"));                    
                if (_appSectionChangeInProgress) {
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_REJECTED, viewName);
                    _appSectionChangeInProgress = false;
                }
                if (_exportInProgress) {
                    _exportInProgress = false;
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
                    Alert.show(msg, resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.validation.error"));
                }
                projectProxy.identityApplianceValidationErrors = null;
                if (_appSectionChangeInProgress) {
                    sendNotification(BaseAppFacade.APP_SECTION_CHANGE_REJECTED, viewName);
                    _appSectionChangeInProgress = false;
                }
                if (_exportInProgress) {
                    _exportInProgress = false;
                }
                break;
            case JDBCDriversListCommand.FAILURE:
                sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
                        resourceManager.getString(AtricoreConsole.BUNDLE, "modeler.mediator.loading.jdbc.error"));
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
            case ApplicationFacade.EXPORT_AGENT_CONFIG:
                popupManager.showCreateExportAgentConfigWindow(notification);
                break;
            case ApplicationFacade.DISPLAY_ACTIVATION_DIALOG:
                popupManager.showActivationWindow(notification);
                break;
            case ApplicationFacade.CREATE_WIKID_ELEMENT:
                popupManager.showCreateWikidWindow(notification);
                break;
            case ApplicationFacade.CREATE_DOMINO_ELEMENT:
                popupManager.showCreateDominoWindow(notification);
                break;
            case ApplicationFacade.CREATE_CLIENTCERT_ELEMENT:
                popupManager.showCreateClientCertWindow(notification);
                break;
            case ApplicationFacade.REMOVE_WIKID_ELEMENT:
                var rwikid:RemoveWikidElementRequest = RemoveWikidElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rwikid.wikidAuthnService);
                break;
            case ApplicationFacade.REMOVE_DOMINO_ELEMENT:
                var rdomino:RemoveDominoElementRequest = RemoveDominoElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rdomino.dominoAuthnService);
                break;
            case ApplicationFacade.REMOVE_CLIENTCERT_ELEMENT:
                var rclientcert:RemoveClientCertElementRequest = RemoveClientCertElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rclientcert.clientCertAuthnService);
                break;
            case ApplicationFacade.REMOVE_JBOSSEPP_AUTHENTICATION_SERVICE_ELEMENT:
                var rjbosseppas:RemoveJBossEPPAuthenticationServiceElementRequest = RemoveJBossEPPAuthenticationServiceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rjbosseppas.jbosseppAuthentication);
                break;
            case ApplicationFacade.CREATE_DIRECTORY_SERVICE_ELEMENT:
                popupManager.showCreateDirectoryServiceWindow(notification);
                break;
            case ApplicationFacade.REMOVE_DIRECTORY_SERVICE_ELEMENT:
                var rdirservice:RemoveDirectoryServiceElementRequest = RemoveDirectoryServiceElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rdirservice.directoryAuthnService);
                break;

            case ApplicationFacade.CREATE_WINDOWS_INTEGRATED_AUTHN_ELEMENT:
                popupManager.showCreateWindowsIntegratedAuthnWindow(notification);
                break;
            case ApplicationFacade.REMOVE_WINDOWS_INTEGRATED_AUTHN_ELEMENT:
                var rwinauthn:RemoveWindowsIntegratedAuthnElementRequest = RemoveWindowsIntegratedAuthnElementRequest(notification.getBody());
                sendNotification(ApplicationFacade.AUTHENTICATION_SERVICE_REMOVE, rwinauthn.windowsIntegratedAuthentication);
                break;

            default:
                // Let super mediator handle notifications.
                super.handleNotification(notification);
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

    protected function get view():ModelerView {
        return viewComponent as ModelerView;
    }

    protected function set view(md:ModelerView):void {
        viewComponent = md;
    }
}
}