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
import org.springextensions.actionscript.puremvc.interfaces.IIocFacade;
import org.springextensions.actionscript.puremvc.patterns.facade.IocFacade;

public class ApplicationFacade extends IocFacade implements IIocFacade {

    public static const USER_PROVISIONING_SERVICE:String = "userProvisioningService";
    public static const IDENTITY_APPLIANCE_MANAGEMENT_SERVICE:String = "identityApplianceManagementService";
    public static const PROFILE_MANAGEMENT_SERVICE:String = "profileManagementService";
    public static const SIGN_ON_SERVICE:String = "signOnService";

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
    public static const IDP_CHANNEL_REMOVE:String = "idpChannelRemove";
    public static const SP_CHANNEL_REMOVE:String = "spChannelRemove";
    public static const DB_IDENTITY_VAULT_REMOVE:String = "identityVaultRemove";
    public static const IDENTITY_APPLIANCE_UPDATE:String = "identityApplianceUpdate";
    public static const UPLOAD:String = "upload";
    public static const BUILD_IDENTITY_APPLIANCE:String = "buildIdentityAppliance";
    public static const DEPLOY_IDENTITY_APPLIANCE:String = "deployIdentityAppliance";
    public static const UNDEPLOY_IDENTITY_APPLIANCE:String = "uneployIdentityAppliance";
    public static const START_IDENTITY_APPLIANCE:String = "startIdentityAppliance";
    public static const STOP_IDENTITY_APPLIANCE:String = "stopIdentityAppliance";
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

    // mediator-backed notifications
    public static const SHOW_ERROR_MSG:String = "showErrorMsg";
    public static const SHOW_SUCCESS_MSG:String = "showSuccessMsg";
    public static const LOGIN:String = "login";
    public static const NAVIGATE:String = "navigate";
    public static const CLEAR_MSG:String = "clearMsg";
    public static const IDENTITY_APPLIANCE_CHANGED:String = "identityApplianceChanged";
    public static const UPDATE_IDENTITY_APPLIANCE:String = "updateIdentityAppliance";
    public static const DISPLAY_APPLIANCE_MODELER:String = "displayApplianceModeler";
    public static const DISPLAY_APPLIANCE_LIFECYCLE:String = "displayApplianceLifecycle";
    public static const DRAG_ELEMENT_TO_DIAGRAM:String = "dragElementToDiagram";
    public static const CREATE_DIAGRAM_ELEMENT:String = "createDiagramElement";
    public static const CREATE_IDENTITY_PROVIDER_ELEMENT:String = "createIdentityProviderElement";
    public static const CREATE_SERVICE_PROVIDER_ELEMENT:String = "createServiceProviderElement";
    public static const DIAGRAM_ELEMENT_CREATION_COMPLETE:String = "diagramElementCreationComplete";
    public static const DIAGRAM_ELEMENT_SELECTED:String = "diagramElementSelected";
    public static const DIAGRAM_ELEMENT_UPDATED:String = "diagramElementUpdated";
    public static const DIAGRAM_ELEMENT_REMOVE:String = "diagramElementRemove";
    public static const REMOVE_IDENTITY_APPLIANCE_ELEMENT:String = "removeIdentityApplianceElement";
    public static const REMOVE_IDENTITY_PROVIDER_ELEMENT:String = "removeIdentityProviderElement";
    public static const CREATE_IDP_CHANNEL_ELEMENT:String = "createIdpChannelElement";
    public static const REMOVE_IDP_CHANNEL_ELEMENT:String = "removeIdpChannelElement";
    public static const CREATE_SP_CHANNEL_ELEMENT:String = "createSpChannelElement";
    public static const REMOVE_SP_CHANNEL_ELEMENT:String = "removeSpChannelElement";
    public static const CREATE_DB_IDENTITY_VAULT_ELEMENT:String = "createIdentityVaultElement";
    public static const REMOVE_DB_IDENTITY_VAULT_ELEMENT:String = "removeIdentityVaultElement";
    public static const REMOVE_SERVICE_PROVIDER_ELEMENT:String = "removeServiceProviderElement";
    public static const MANAGE_CERTIFICATE:String = "manageCertificate";
    public static const SHOW_UPLOAD_PROGRESS:String = "uploadProgress";
    public static const DISPLAY_ACCOUNT_MNGMT_HOME:String = "displayAccountManagementHome";
    public static const DISPLAY_ADD_NEW_GROUP:String = "displayAddNewGroup";
    public static const DISPLAY_ADD_NEW_USER:String = "displayAddNewUser";
    public static const DISPLAY_EDIT_GROUP:String = "displayEditGroup";
    public static const DISPLAY_EDIT_USER:String = "displayEditUser";
    public static const DISPLAY_SEARCH_GROUPS:String = "displaySearchGroup";
    public static const DISPLAY_SEARCH_USERS:String = "displaySearchUser";
    public static const DISPLAY_SEARCH_RESULTS_USERS = "displaySearchResultUsers";
    public static const DISPLAY_SEARCH_RESULTS_GROUPS = "displaySearchResultGroups";
    public static const DISPLAY_GROUP_PROPERTIES = "displayGroupProperties";
    public static const DISPLAY_USER_PROPERTIES = "displayUserProperties";
    public static const MODELER_VIEW_SELECTED:String = "modelerViewSelected";
    public static const LIFECYCLE_VIEW_SELECTED:String = "lifecycleViewSelected";
    public static const ACCOUNT_VIEW_SELECTED:String = "accountViewSelected";


    public function ApplicationFacade(p_configuration:* = null) {
        super(p_configuration);
    }

    /**
     * Singleton ApplicationFacade Factory Method
     */
    public static function getInstance(p_configSource:* = null):ApplicationFacade {
        if (instance == null) {
            new ApplicationFacade(p_configSource);
        }

        return instance as ApplicationFacade;
    }

    /**
     * Register Commands with the Controller
     */
    override protected function initializeController():void {
        super.initializeController();

        registerCommandByConfigName(STARTUP, CommandNames.STARTUP_CMD);
    }

}
}