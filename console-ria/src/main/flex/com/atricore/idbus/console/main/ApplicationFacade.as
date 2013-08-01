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
    public static const SCHEMAS_MANAGEMENT_SERVICE:String = "schemasManagementService";
    public static const SERVICE_CONFIGURATION_MANAGEMENT_SERVICE:String = "serviceConfigurationManagementService";
    public static const BRAND_MANAGEMENT_SERVICE:String = "brandManagementService";

    public static const ADMIN_GROUP:String = "Administrators";

    // Notification name constants application

    // command-backed notifications
    public static const STARTUP:String = "startup";

    public static const CHECK_LICENSE:String = "checkLicense";
    
    public static const SETUP_SERVER:String = "Note.SetupServer";
    public static const REGISTER:String = "Note.Register";
    public static const CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE:String = "createSimpleSSOIdentityAppliance";
    public static const LOOKUP_IDENTITY_APPLIANCE_BY_ID:String = "lookupIdentityApplianceById";
    public static const IDENTITY_APPLIANCE_LIST_LOAD:String = "identityApplianceListLoad";
    public static const CREATE_IDENTITY_APPLIANCE:String = "createIdentityAppliance";
    public static const IDENTITY_APPLIANCE_REMOVE:String = "identityApplianceRemove";
    public static const IDENTITY_PROVIDER_REMOVE:String = "identityProviderRemove";
    public static const INTERNAL_SAML2_SERVICE_PROVIDER_REMOVE:String = "internalSaml2ServiceProviderRemove";
    public static const EXTERNAL_SAML2_IDENTITY_PROVIDER_REMOVE:String = "externalSaml2IdentityProviderRemove";
    public static const EXTERNAL_SAML2_SERVICE_PROVIDER_REMOVE:String = "externalSaml2ServiceProviderRemove";
    public static const OPENID_IDENTITY_PROVIDER_REMOVE:String = "openIDIdentityProviderRemove";
    public static const OPENID_SERVICE_PROVIDER_REMOVE:String = "openIDServiceProviderRemove";
    public static const OAUTH2_IDENTITY_PROVIDER_REMOVE:String = "oauth2IdentityProviderRemove";
    public static const OAUTH2_SERVICE_PROVIDER_REMOVE:String = "oauth2ServiceProviderRemove";
    public static const EXTERNAL_WSFED_SERVICE_PROVIDER_REMOVE:String = "externalWSFedServiceProviderRemove";
    public static const JOSSO1_RESOURCE_REMOVE:String = "josso1ResourceRemove";
    public static const JOSSO2_RESOURCE_REMOVE:String = "josso2ResourceRemove";
    public static const JBOSSEPP_RESOURCE_REMOVE:String = "jbosseppResourceRemove";
    public static const LIFERAY_RESOURCE_REMOVE:String = "liferayResourceRemove";
    public static const SELFSERVICES_RESOURCE_REMOVE:String = "selfServicesResourceRemove";
    public static const DOMINO_RESOURCE_REMOVE:String = "dominoResourceRemove";

//    public static const IDP_CHANNEL_REMOVE:String = "idpChannelRemove";
//    public static const SP_CHANNEL_REMOVE:String = "spChannelRemove";
    public static const IDENTITY_SOURCE_REMOVE:String = "identitySourceRemove";
    public static const ACTIVATION_REMOVE:String = "activationRemove";
    public static const SERVICE_CONNECTION_REMOVE:String = "serviceConnectionRemove";
    public static const FEDERATED_CONNECTION_REMOVE:String = "federatedConnectionRemove";
    public static const IDENTITY_LOOKUP_REMOVE:String = "identityLookupRemove";
    public static const DELEGATED_AUTHENTICATION_REMOVE:String = "delegatedAuthenticationRemove";
    public static const EXECUTION_ENVIRONMENT_REMOVE:String = "executionEnvironmentRemove";
    public static const AUTHENTICATION_SERVICE_REMOVE:String = "authenticationServiceRemove";
    public static const IDENTITY_APPLIANCE_UPDATE:String = "identityApplianceUpdate";
    public static const UPLOAD:String = "upload";
    public static const BUILD_IDENTITY_APPLIANCE:String = "buildIdentityAppliance";
    public static const DEPLOY_IDENTITY_APPLIANCE:String = "deployIdentityAppliance";
    public static const UNDEPLOY_IDENTITY_APPLIANCE:String = "undeployIdentityAppliance";
    public static const START_IDENTITY_APPLIANCE:String = "startIdentityAppliance";
    public static const STOP_IDENTITY_APPLIANCE:String = "stopIdentityAppliance";
    public static const DISPOSE_IDENTITY_APPLIANCE:String = "disposeIdentityAppliance";
    public static const UNDISPOSE_IDENTITY_APPLIANCE:String = "undisposeIdentityAppliance";
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
    public static const LIST_SCHEMA_ATTRIBUTES:String = "listSchemaAttributes";
    public static const ADD_SCHEMA_ATTRIBUTE:String ="addAttribute";
    public static const EDIT_SCHEMA_ATTRIBUTE:String ="editAttribute";
    public static const DELETE_SCHEMA_ATTRIBUTE:String ="deleteAttribute";
    public static const CREATE_ACTIVATION:String = "createActivation";
    public static const CREATE_FEDERATED_CONNECTION:String = "createFederatedConnection";
    public static const CREATE_SERVICE_CONNECTION:String = "createServiceConnection";
    public static const CREATE_IDENTITY_LOOKUP:String = "createIdentityLookup";
    public static const CREATE_DELEGATED_AUTHENTICATION:String = "createDelegatedAuthentication";
    public static const ACTIVATE_EXEC_ENVIRONMENT:String = "activateExecEnvironment";
    public static const RESET_EXEC_ENV_ACTIVATION:String = "resetExecEnvActivation";
    public static const RESET_RESOURCE_ACTIVATION:String = "resetResourceActivation";
    public static const CHECK_INSTALL_FOLDER_EXISTENCE:String = "checkInstallFolderExistence";
    public static const CHECK_FOLDERS_EXISTENCE:String = "checkFoldersExistence";
    public static const LIST_JDBC_DRIVERS:String = "listJdbcDrivers";
    public static const GET_METADATA_INFO:String = "getMetadataInfo";
    public static const GET_CERTIFICATE_INFO:String = "getCertificateInfo";
    public static const ACTIVATE_LICENSE:String = "activateLicense";
    public static const UPDATE_LICENSE:String = "updateLicense";
    public static const GET_LICENSE:String = "getLicense";
    
    public static const LIST_UPDATES:String = "listUpdates";
    public static const CHECK_FOR_UPDATES:String = "checkForUpdates";
    public static const APPLY_UPDATE:String = "applyUpdate";
    public static const GET_UPDATE_PROFILE:String = "getUpdateProfile";
    public static const LOAD_UPDATE_SCHEME:String = "loadUpdateScheme";
    public static const SAVE_UPDATE_SCHEME:String = "saveUpdateScheme";

    public static const IDENTITY_APPLIANCE_EXPORT:String = "identityApplianceExport";
    public static const PROVIDER_CERTIFICATE_EXPORT:String = "providerCertificateExport";
    public static const METADATA_EXPORT:String = "metadataExport";
    public static const AGENT_CONFIG_EXPORT:String = "agentConfigExport";

    public static const LIST_ACCOUNT_LINKAGE_POLICIES:String = "listAccountLinkagePolicies";
    public static const LIST_USER_DASHBOARD_BRANDINGS:String = "listUserDashboardBrandings"
    public static const LIST_IDP_SELECTORS:String = "listIdPSelectors"
    public static const LIST_IDENTITY_MAPPING_POLICIES:String = "listIdentityMappingPolicies";
    public static const LIST_NAMEID_POLICIES:String = "listNameIDPolicies";
    public static const LIST_IMPERSONATE_USER_POLICIES:String = "listImpersonateUserPolicies";
    public static const LIST_IDENTITY_FLOW_COMPONENTS:String = "listIdentityFlowComponents"

    public static const GET_SERVICE_CONFIG:String = "getServiceConfig";
    public static const UPDATE_SERVICE_CONFIG:String = "updateServiceConfig";
    public static const LOOKUP_BRANDING:String = "lookupBranding";
    public static const LIST_BRANDINGS:String = "listBrandings";
    public static const CREATE_BRANDING:String = "createBranding";
    public static const EDIT_BRANDING:String = "editBranding";
    public static const REMOVE_BRANDING:String = "removeBranding";
    public static const ACTIVATE_BRANDING_CHANGES:String = "activateBrandingChanges";
    public static const DISPLAY_CREATE_BRANDING_WIZARD:String = "displayCreateBrandingWizard";
    public static const DISPLAY_EDIT_BRANDING:String = "displayEditBranding";

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
    public static const CREATE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT:String = "createExternalIdentityProviderElement";
    public static const CREATE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT:String = "createExternalServiceProviderElement";
    public static const CREATE_SAML_2_IDENTITY_PROVIDER_ELEMENT:String = "createSaml2IdentityProviderElement";
    public static const CREATE_INTERNAL_SAML_2_SERVICE_PROVIDER_ELEMENT:String = "createInternalSaml2ServiceProviderElement";
    public static const CREATE_OPENID_IDENTITY_PROVIDER_ELEMENT:String = "createOpenIdIdentityProviderElement";
    public static const CREATE_OPENID_SERVICE_PROVIDER_ELEMENT:String = "createOpenIdServiceProviderElement";
    public static const CREATE_OAUTH_2_IDENTITY_PROVIDER_ELEMENT:String = "createOAuth2IdentityProviderElement";
    public static const CREATE_OAUTH_2_SERVICE_PROVIDER_ELEMENT:String = "createOAuth2ServiceProviderElement";
    public static const CREATE_EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT:String = "createExternalWSFedServiceProviderElement";
    public static const CREATE_SALESFORCE_ELEMENT:String = "createSalesforceElement";
    public static const CREATE_GOOGLE_APPS_ELEMENT:String = "createGoogleAppsElement";
    public static const CREATE_SUGAR_CRM_ELEMENT:String = "createSugarCRMElement";
    public static const CREATE_WIKID_ELEMENT:String = "createWikidElement";
    public static const CREATE_DOMINO_ELEMENT:String = "createDominoElement";
    public static const CREATE_CLIENTCERT_ELEMENT:String = "createClientCertElement";
    public static const CREATE_JBOSSEPP_IDENTITYSOURCE_ELEMENT:String = "createJBossEPPAuthenticationServiceElement";
    public static const CREATE_DIRECTORY_SERVICE_ELEMENT:String = "createDirectoryServiceElement";
    public static const CREATE_WINDOWS_INTEGRATED_AUTHN_ELEMENT:String = "createWindowsIntegratedAuthnElement";
    public static const DIAGRAM_ELEMENT_CREATION_COMPLETE:String = "diagramElementCreationComplete";
    public static const DIAGRAM_ELEMENT_SELECTED:String = "diagramElementSelected";
    public static const DIAGRAM_ELEMENT_UPDATED:String = "diagramElementUpdated";
    public static const DIAGRAM_ELEMENT_REMOVE:String = "diagramElementRemove";
    public static const REMOVE_IDENTITY_APPLIANCE_ELEMENT:String = "removeIdentityApplianceElement";
    public static const REMOVE_IDENTITY_PROVIDER_ELEMENT:String = "removeIdentityProviderElement";
    public static const REMOVE_EXTERNAL_SAML2_IDENTITY_PROVIDER_ELEMENT:String = "removeExternalIdentityProviderElement";
    public static const REMOVE_EXTERNAL_OPENID_IDENTITY_PROVIDER_ELEMENT:String = "removeOpenIDIdentityProviderElement";
    public static const REMOVE_OPENID_SERVICE_PROVIDER_ELEMENT:String = "removeOpenIDServiceProviderElement";
    public static const REMOVE_OAUTH2_IDENTITY_PROVIDER_ELEMENT:String = "removeOAuth2IdentityProviderElement";
    public static const REMOVE_OAUTH2_SERVICE_PROVIDER_ELEMENT:String = "removeOAuth2ServiceProviderElement";
    public static const REMOVE_EXTERNAL_WSFED_SERVICE_PROVIDER_ELEMENT:String = "removeExternalWSFedServiceProviderElement";
    public static const REMOVE_JOSSO1_RESOURCE_ELEMENT:String = "removeJOSSO1ResourceElement";
    public static const REMOVE_JOSSO2_RESOURCE_ELEMENT:String = "removeJOSSO2ResourceElement";
//    public static const CREATE_IDP_CHANNEL_ELEMENT:String = "createIdpChannelElement";
//    public static const REMOVE_IDP_CHANNEL_ELEMENT:String = "removeIdpChannelElement";
//    public static const CREATE_SP_CHANNEL_ELEMENT:String = "createSpChannelElement";
//    public static const REMOVE_SP_CHANNEL_ELEMENT:String = "removeSpChannelElement";
    public static const REMOVE_JBOSSEPP_RESOURCE_ELEMENT:String = "removeJBossEPPResourceElement";
    public static const REMOVE_LIFERAY_RESOURCE_ELEMENT:String = "removeLiferayResourceElement";
    public static const REMOVE_SELFSERVICES_RESOURCE_ELEMENT:String = "removeSelfServicesResourceElement";
    public static const REMOVE_DOMINO_RESOURCE_ELEMENT:String = "removeDominoResourceElement";
    public static const CREATE_DB_IDENTITY_SOURCE_ELEMENT:String = "createDbIdentitySourceElement";
    public static const REMOVE_IDENTITY_SOURCE_ELEMENT:String = "removeIdentitySourceElement";
    public static const CREATE_LDAP_IDENTITY_SOURCE_ELEMENT:String = "createLdapIdentitySourceElement";
    public static const CREATE_XML_IDENTITY_SOURCE_ELEMENT:String = "createXmlIdentitySourceElement";
    public static const CREATE_IDENTITY_VAULT_ELEMENT:String = "createIdentityVaultElement";
    public static const REMOVE_SAML2_SERVICE_PROVIDER_ELEMENT:String = "removeSaml2ServiceProviderElement";
    public static const REMOVE_EXTERNAL_SAML2_SERVICE_PROVIDER_ELEMENT:String = "removeExternalServiceProviderElement";
    public static const REMOVE_SALESFORCE_ELEMENT:String = "removeSalesforceElement";
    public static const REMOVE_GOOGLE_APPS_ELEMENT:String = "removeGoogleAppsElement";
    public static const REMOVE_SUGAR_CRM_ELEMENT:String = "removeSugarCRMElement";
    public static const REMOVE_WIKID_ELEMENT:String = "removeWikidElement";
    public static const REMOVE_DOMINO_ELEMENT:String = "removeDominoElement";
    public static const REMOVE_CLIENTCERT_ELEMENT:String = "removeClientCertElement";
    public static const REMOVE_JBOSSEPP_AUTHENTICATION_SERVICE_ELEMENT:String = "removeJBossEPPAuthenticationServiceElement";
    public static const REMOVE_DIRECTORY_SERVICE_ELEMENT:String = "removeDirectoryServiceElement";
    public static const REMOVE_WINDOWS_INTEGRATED_AUTHN_ELEMENT:String = "removeWindowsIntegratedAuthnElement";
    public static const CREATE_JOSSO1_RESOURCE_ELEMENT:String = "createJosso1ResourceElement";
    public static const CREATE_JOSSO2_RESOURCE_ELEMENT:String = "createJosso2ResourceElement";
    public static const CREATE_JBOSS_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJbossExecutionEnvironmentElement";
    public static const CREATE_WEBLOGIC_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWeblogicExecutionEnvironmentElement";
    public static const CREATE_TOMCAT_EXECUTION_ENVIRONMENT_ELEMENT:String = "createTomcatExecutionEnvironmentElement";
    public static const CREATE_JBOSS_PORTAL_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJBossPortalExecutionEnvironmentElement";
    public static const CREATE_LIFERAY_RESOURCE_ELEMENT:String = "createLiferayResourceElement";
    public static const CREATE_JBOSSEPP_RESOURCE_ELEMENT:String = "createJBossEPPResourceElement";
    public static const CREATE_SELFSERVICES_RESOURCE_ELEMENT:String = "createSelfServicesResourceElement"
    public static const CREATE_DOMINO_RESOURCE_ELEMENT:String = "createDominoResourceElement"
    public static const CREATE_WEBSPHERE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWebsphereExecutionEnvironmentElement";
    public static const CREATE_APACHE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createApacheExecutionEnvironmentElement";
    public static const CREATE_WINDOWS_IIS_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWindowsIISExecutionEnvironmentElement";
    public static const CREATE_ALFRESCO_EXECUTION_ENVIRONMENT_ELEMENT:String = "createAlfrescoExecutionEnvironmentElement";
    public static const CREATE_JAVAEE_EXECUTION_ENVIRONMENT_ELEMENT:String = "createJavaEEExecutionEnvironmentElement";
    public static const CREATE_PHP_EXECUTION_ENVIRONMENT_ELEMENT:String = "createPHPExecutionEnvironmentElement";
    public static const CREATE_PHPBB_EXECUTION_ENVIRONMENT_ELEMENT:String = "createPhpBBExecutionEnvironmentElement";
    public static const CREATE_WEBSERVER_EXECUTION_ENVIRONMENT_ELEMENT:String = "createWebcontainerExecutionEnvironmentElement";
    public static const CREATE_SHAREPOINT2010_EXECUTION_ENVIRONMENT_ELEMENT:String = "createSharepoint2010ExecutionEnvironmentElement";
    public static const CREATE_COLDFUSION_EXECUTION_ENVIRONMENT_ELEMENT:String = "createColdfusionExecutionEnvironmentElement";
    public static const CREATE_MICROSTRATEGY_EXECUTION_ENVIRONMENT_ELEMENT:String = "createMicroStrategyExecutionEnvironmentElement";
    public static const REMOVE_ACTIVATION_ELEMENT:String = "removeActivationElement";
    public static const REMOVE_FEDERATED_CONNECTION_ELEMENT:String = "removeFederatedConnectionElement";
    public static const REMOVE_SERVICE_CONNECTION_ELEMENT:String = "removeServiceConnectionElement";
    public static const REMOVE_IDENTITY_LOOKUP_ELEMENT:String = "removeIdentityLookupElement";
    public static const REMOVE_DELEGATED_AUTHENTICATION_ELEMENT:String = "removeDelegatedAuthenticationElement";
    public static const REMOVE_EXECUTION_ENVIRONMENT_ELEMENT:String = "removeExecutionEnvironmentElement";
    public static const MANAGE_CERTIFICATE:String = "manageCertificate";
    public static const SHOW_UPLOAD_PROGRESS:String = "uploadProgress";
    public static const DISPLAY_ADD_NEW_GROUP:String = "displayAddNewGroup";
    public static const DISPLAY_ADD_NEW_USER:String = "displayAddNewUser";
    public static const DISPLAY_ADD_NEW_ATTRIBUTE:String = "displayAddNewAttribute";
    public static const DISPLAY_EDIT_GROUP:String = "displayEditGroup";
    public static const DISPLAY_EDIT_USER:String = "displayEditUser";
    public static const DISPLAY_EDIT_ATTRIBUTE:String = "displayEditAttribute";
    public static const DISPLAY_SEARCH_GROUPS:String = "displaySearchGroup";
    public static const DISPLAY_SEARCH_USERS:String = "displaySearchUser";
    public static const DISPLAY_SEARCH_RESULTS_USERS:String = "displaySearchResultUsers";
    public static const DISPLAY_SEARCH_RESULTS_GROUPS:String = "displaySearchResultGroups";
    public static const DISPLAY_GROUP_PROPERTIES:String = "displayGroupProperties";
    public static const DISPLAY_USER_PROPERTIES:String = "displayUserProperties";
    public static const DISPLAY_SCHEMA_PROPERTIES:String = "displaySchemaProperties";
    public static const DISPLAY_SCHEMA_ATTRIBUTES:String = "displaySchemaAttributes";
    public static const DISPLAY_CHANGE_PASSWORD:String = "displayChangePassword";
    public static const APPLIANCE_VALIDATION_ERRORS:String = "applianceValidationErrors";
    public static const APPLIANCE_SAVED:String = "applianceSaved";
    public static const DISPLAY_ACTIVATE_LICENSE:String = "displayActivateLicense";
    public static const DISPLAY_UPDATE_LICENSE:String = "displayUpdateLicense";
    public static const DISPLAY_EULA_TEXT:String = "displayEulaText";
    public static const EXPORT_IDENTITY_APPLIANCE:String = "exportIdentityAppliance";
    public static const EXPORT_PROVIDER_CERTIFICATE:String = "exportProviderCertificate";
    public static const EXPORT_METADATA:String = "exportMetadata";
    public static const EXPORT_AGENT_CONFIG:String = "exportAgentConfig";
    public static const DISPLAY_UPDATE_NOTIFICATIONS:String = "displayUpdateNotifications";
    public static const SETTINGS_MENU_ELEMENT_SELECTED:String = "settingsMenuElementSelected";
    public static const DISPLAY_ACTIVATION_DIALOG:String = "displayActivationDialog";

    // TODO: remove this?
    public static const LICENSE_VIEW_SELECTED:String = "licenseViewSelected";


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
