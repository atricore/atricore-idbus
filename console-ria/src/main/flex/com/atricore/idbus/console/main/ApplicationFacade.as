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

package com.atricore.idbus.console.main
{
import com.atricore.idbus.console.base.app.BaseAppFacade;

public class ApplicationFacade extends BaseAppFacade {

    public static const USER_PROVISIONING_SERVICE:String = "userProvisioningService";
    public static const IDENTITY_APPLIANCE_MANAGEMENT_SERVICE:String = "identityApplianceManagementService";
    public static const PROFILE_MANAGEMENT_SERVICE:String = "profileManagementService";
    public static const SIGN_ON_SERVICE:String = "signOnService";
    public static const LICENSE_MANAGEMENT_SERVICE:String = "licenseManagementService";
                                                      
    public static const LIVE_UPDATE_SERVICE:String = "liveUpdateService";

    public static const ADMIN_GROUP:String = "Administrators";

    // Notification name constants application

    // command-backed notifications
    public static const STARTUP:String = "startup";
    
    public static const SETUP_SERVER:String = "Note.SetupServer";
    public static const REGISTER:String = "Note.Register";
    public static const CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE:String = "createSimpleSSOIdentityAppliance";
    public static const LOOKUP_IDENTITY_APPLIANCE_BY_ID:String = "lookupIdentityApplianceById";
    public static const IDENTITY_APPLIANCE_LIST_LOAD:String = "identityApplianceListLoad";
    public static const CREATE_IDENTITY_APPLIANCE:String = "createIdentityAppliance";
    public static const IDENTITY_APPLIANCE_REMOVE:String = "identityApplianceRemove";
    public static const IDENTITY_PROVIDER_REMOVE:String = "identityProviderRemove";
    public static const SERVICE_PROVIDER_REMOVE:String = "serviceProviderRemove";
    public static const EXTERNAL_IDENTITY_PROVIDER_REMOVE:String = "externalIdentityProviderRemove";
    public static const EXTERNAL_SERVICE_PROVIDER_REMOVE:String = "externalServiceProviderRemove";
//    public static const IDP_CHANNEL_REMOVE:String = "idpChannelRemove";
//    public static const SP_CHANNEL_REMOVE:String = "spChannelRemove";
    public static const IDENTITY_SOURCE_REMOVE:String = "identitySourceRemove";
    public static const ACTIVATION_REMOVE:String = "activationRemove";
    public static const FEDERATED_CONNECTION_REMOVE:String = "federatedConnectionRemove";
    public static const IDENTITY_LOOKUP_REMOVE:String = "identityLookupRemove";
    public static const EXECUTION_ENVIRONMENT_REMOVE:String = "executionEnvironmentRemove";
    public static const IDENTITY_APPLIANCE_UPDATE:String = "identityApplianceUpdate";
    public static const UPLOAD:String = "upload";
    public static const BUILD_IDENTITY_APPLIANCE:String = "buildIdentityAppliance";
    public static const DEPLOY_IDENTITY_APPLIANCE:String = "deployIdentityAppliance";
    public static const UNDEPLOY_IDENTITY_APPLIANCE:String = "undeployIdentityAppliance";
    public static const START_IDENTITY_APPLIANCE:String = "startIdentityAppliance";
    public static const STOP_IDENTITY_APPLIANCE:String = "stopIdentityAppliance";
    public static const DISPOSE_IDENTITY_APPLIANCE:String = "disposeIdentityAppliance";
    public static const IMPORT_IDENTITY_APPLIANCE:String = "importIdentityAppliance";
    public static const ADD_GROUP:String = "addGroup";
    public static const ADD_USER:String = "addUser";
    public static const DELETE_GROUP:String = "deleteGroup";
    public static const DELETE_USER:String = "deleteUser";
    public static const EDIT_GROUP:String = "editGroup";
    public static const EDIT_USER:String = "editUser";
    public static const LIST_GROUPS:String = "listGroups";
    public static const LIST_USERS:String = "listUsers";
    public static const SEARCH_GROUPS:String = "searchGroups";
    public static const SEARCH_USERS:String = "searchUsers";
    public static const CREATE_ACTIVATION:String = "createActivation";
    public static const CREATE_FEDERATED_CONNECTION:String = "createFederatedConnection";
    public static const CREATE_IDENTITY_LOOKUP:String = "createIdentityLookup";
    public static const ACTIVATE_EXEC_ENVIRONMENT:String = "activateExecEnvironment";
    public static const CHECK_INSTALL_FOLDER_EXISTENCE:String = "checkInstallFolderExistence";
    public static const CHECK_FOLDERS_EXISTENCE:String = "checkFoldersExistence";
    public static const LIST_JDBC_DRIVERS:String = "listJdbcDrivers";
    public static const GET_METADATA_INFO:String = "getMetadataInfo";
    public static const GET_CERTIFICATE_INFO:String = "getCertificateInfo";
    public static const UPDATE_LICENSE:String = "updateLicense";
    
    public static const LIST_UPDATES:String = "listUpdates";
    public static const CHECK_FOR_UPDATES:String = "checkForUpdates";
    public static const APPLY_UPDATE:String = "applyUpdate";
    public static const GET_UPDATE_PROFILE:String = "getUpdateProfile";
    public static const IDENTITY_APPLIANCE_EXPORT:String = "identityApplianceExport";
    public static const PROVIDER_CERTIFICATE_EXPORT:String = "providerCertificateExport";
    public static const METADATA_EXPORT:String = "metadataExport";


    // mediator-backed notifications
    public static const SHOW_ERROR_MSG:String = "showErrorMsg";
//    public static const SHOW_SUCCESS_MSG:String = "showSuccessMsg";
    public static const LOGIN:String = "login";
    public static const LOGOUT:String = "logout";
    public static const CHANGE_PASSWORD:String = "changePassword";
    public static const NOT_FIRST_RUN:String = "notFirstRun";
    public static const NAVIGATE:String = "navigate";
    public static const CLEAR_MSG:String = "clearMsg";
    public static const IDENTITY_APPLIANCE_CHANGED:String = "identityApplianceChanged";
    public static const UPDATE_IDENTITY_APPLIANCE:String = "updateIdentityAppliance";
    public static const AUTOSAVE_IDENTITY_APPLIANCE:String = "autoSaveIdentityAppliance";
    public static const UPDATE_DIAGRAM_ELEMENTS_DATA:String = "updateDiagramElementsData";
    public static const DISPLAY_APPLIANCE_MODELER:String = "displayApplianceModeler";
    public static const DISPLAY_APPLIANCE_LIFECYCLE:String = "displayApplianceLifecycle";
    public static const DISPLAY_APPLIANCE_ACCOUNT:String = "displayApplianceAccount";
    public static const DISPLAY_LIVE_UPDATE:String = "displayLiveUpdate";
    public static const DISPLAY_LICENSING:String = "displayLicensing";
    public static const DISPLAY_VIEW:String = "displayView";
    public static const PALETTE_ELEMENT_SELECTED:String = "paletteElementSelected";
    public static const REFRESH_DIAGRAM:String = "refreshDiagram";
    public static const DIAGRAM_ELEMENT_REMOVE_COMPLETE:String = "diagramElementRemoveComplete";
    public static const DRAG_ELEMENT_TO_DIAGRAM:String = "dragElementToDiagram";
    public static const CREATE_DIAGRAM_ELEMENT:String = "createDiagramElement";
    public static const CREATE_IDENTITY_PROVIDER_ELEMENT:String = "createIdentityProviderElement";
    public static const CREATE_SERVICE_PROVIDER_ELEMENT:String = "createServiceProviderElement";
    public static const CREATE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT:String = "createExternalIdentityProviderElement";
    public static const CREATE_EXTERNAL_SERVICE_PROVIDER_ELEMENT:String = "createExternalServiceProviderElement";
    public static const CREATE_SALESFORCE_ELEMENT:String = "createSalesforceElement";
    public static const CREATE_GOOGLE_APPS_ELEMENT:String = "createGoogleAppsElement";
    public static const DIAGRAM_ELEMENT_CREATION_COMPLETE:String = "diagramElementCreationComplete";
    public static const DIAGRAM_ELEMENT_SELECTED:String = "diagramElementSelected";
    public static const DIAGRAM_ELEMENT_UPDATED:String = "diagramElementUpdated";
    public static const DIAGRAM_ELEMENT_REMOVE:String = "diagramElementRemove";
    public static const REMOVE_IDENTITY_APPLIANCE_ELEMENT:String = "removeIdentityApplianceElement";
    public static const REMOVE_IDENTITY_PROVIDER_ELEMENT:String = "removeIdentityProviderElement";
    public static const REMOVE_EXTERNAL_IDENTITY_PROVIDER_ELEMENT:String = "removeExternalIdentityProviderElement";
//    public static const CREATE_IDP_CHANNEL_ELEMENT:String = "createIdpChannelElement";
//    public static const REMOVE_IDP_CHANNEL_ELEMENT:String = "removeIdpChannelElement";
//    public static const CREATE_SP_CHANNEL_ELEMENT:String = "createSpChannelElement";
//    public static const REMOVE_SP_CHANNEL_ELEMENT:String = "removeSpChannelElement";
    public static const CREATE_DB_IDENTITY_SOURCE_ELEMENT:String = "createDbIdentitySourceElement";
    public static const REMOVE_IDENTITY_SOURCE_ELEMENT:String = "removeIdentitySourceElement";
    public static const CREATE_LDAP_IDENTITY_SOURCE_ELEMENT:String = "createLdapIdentitySourceElement";
    public static const CREATE_XML_IDENTITY_SOURCE_ELEMENT:String = "createXmlIdentitySourceElement";
    public static const CREATE_IDENTITY_VAULT_ELEMENT:String = "createIdentityVaultElement";
    public static const REMOVE_SERVICE_PROVIDER_ELEMENT:String = "removeServiceProviderElement";
    public static const REMOVE_EXTERNAL_SERVICE_PROVIDER_ELEMENT:String = "removeExternalServiceProviderElement";
    public static const REMOVE_SALESFORCE_ELEMENT:String = "removeSalesforceElement";
    public static const REMOVE_GOOGLE_APPS_ELEMENT:String = "removeGoogleAppsElement";
    public static const CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJbossExecutionEnvironmentElement";
    public static const CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWeblogicExecutionEnvironmentElement";
    public static const CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT:String = "createTomcatExecutionEnvironmentElement";
    public static const CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJBossPortalExecutionEnvironmentElement";
    public static const CREATE_LIFERAY_EXECUTION_ENVIRONMENT_ELEMENT:String = "createLiferayExecutionEnvironmentElement";
    public static const CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWebsphereExecutionEnvironmentElement";
    public static const CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createApacheExecutionEnvironmentElement";
    public static const CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWindowsIISExecutionEnvironmentElement";
    public static const CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT:String = "createAlfrescoExecutionEnvironmentElement";
    public static const CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJavaEEExecutionEnvironmentElement";
    public static const CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT:String = "createPhpBBExecutionEnvironmentElement";
    public static const CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWebcontainerExecutionEnvironmentElement";
    public static const REMOVE_ACTIVATION_ELEMENT:String = "removeActivationElement";
    public static const REMOVE_FEDERATED_CONNECTION_ELEMENT:String = "removeFederatedConnectionElement";
    public static const REMOVE_IDENTITY_LOOKUP_ELEMENT:String = "removeIdentityLookupElement";
    public static const REMOVE_EXECUTION_ENVIRONMENT_ELEMENT:String = "removeExecutionEnvironmentElement";
    public static const MANAGE_CERTIFICATE:String = "manageCertificate";
    public static const SHOW_UPLOAD_PROGRESS:String = "uploadProgress";
    public static const DISPLAY_ADD_NEW_GROUP:String = "displayAddNewGroup";
    public static const DISPLAY_ADD_NEW_USER:String = "displayAddNewUser";
    public static const DISPLAY_EDIT_GROUP:String = "displayEditGroup";
    public static const DISPLAY_EDIT_USER:String = "displayEditUser";
    public static const DISPLAY_SEARCH_GROUPS:String = "displaySearchGroup";
    public static const DISPLAY_SEARCH_USERS:String = "displaySearchUser";
    public static const DISPLAY_SEARCH_RESULTS_USERS:String = "displaySearchResultUsers";
    public static const DISPLAY_SEARCH_RESULTS_GROUPS:String = "displaySearchResultGroups";
    public static const DISPLAY_GROUP_PROPERTIES:String = "displayGroupProperties";
    public static const DISPLAY_USER_PROPERTIES:String = "displayUserProperties";
    public static const DISPLAY_CHANGE_PASSWORD:String = "displayChangePassword";
    public static const APPLIANCE_VALIDATION_ERRORS:String = "applianceValidationErrors";
    public static const APPLIANCE_SAVED:String = "applianceSaved";
    public static const DISPLAY_UPDATE_LICENSE:String = "displayUpdateLicense";
    public static const EXPORT_IDENTITY_APPLIANCE:String = "exportIdentityAppliance";
    public static const EXPORT_PROVIDER_CERTIFICATE:String = "exportProviderCertificate";
    public static const EXPORT_METADATA:String = "exportMetadata";
    public static const DISPLAY_UPDATE_NOTIFICATIONS:String = "displayUpdateNotifications";
    public static const SETTINGS_MENU_ELEMENT_SELECTED:String = "settingsMenuElementSelected";

    // TODO: remove this?
    public static const LICENSE_VIEW_SELECTED:String = "licenseViewSelected";
    public static const UPDATE_VIEW_SELECTED:String = "updateViewSelected";

    public function ApplicationFacade(p_configuration:* = null) {
        super(p_configuration);
    }

    /**
     * Singleton ApplicationFacade Factory Method
     */
    public static function getInstance(p_configSource:* = null):ApplicationFacade {
        if (instance == null) {
            instance = new ApplicationFacade(p_configSource);
        }

        return instance as ApplicationFacade;
    }

    /**
     * Register Commands with the Controller
     */
    override protected function initializeController():void {
        super.initializeController();

        // Startup Command should listen for notification STARTUP
        registerCommandByConfigName(STARTUP, CommandNames.STARTUP_CMD);
    }

}
}
