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
import com.atricore.idbus.console.main.controller.ApplicationStartUpCommand;
import com.atricore.idbus.console.main.controller.RegisterCommand;
import com.atricore.idbus.console.main.controller.SetupServerCommand;
import com.atricore.idbus.console.main.controller.UploadCommand;
import com.atricore.idbus.console.modeling.main.controller.BuildIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.CreateSimpleSSOIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.DeployIdentityApplianceCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceCreateCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceListLoadCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceRemoveCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceUpdateCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityProviderRemoveCommand;

import com.atricore.idbus.console.modeling.main.controller.IdentityVaultRemoveCommand;
import com.atricore.idbus.console.modeling.main.controller.IdpChannelRemoveCommand;
import com.atricore.idbus.console.modeling.main.controller.LookupIdentityApplianceByIdCommand;

import com.atricore.idbus.console.modeling.main.controller.ServiceProviderRemoveCommand;

import com.atricore.idbus.console.modeling.main.controller.SpChannelRemoveCommand;

import org.puremvc.as3.patterns.facade.Facade;
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
    public static const EDIT_IDENTITY_APPLIANCE:String = "editIdentityAppliance";
    public static const UPLOAD:String = "upload";
    public static const BUILD_IDENTITY_APPLIANCE:String = "buildIdentityAppliance";
    public static const DEPLOY_IDENTITY_APPLIANCE:String = "deployIdentityAppliance";

    // mediator-backed notifications
    public static const SHOW_ERROR_MSG:String = "showErrorMsg";
    public static const SHOW_SUCCESS_MSG:String = "showSuccessMsg";
    public static const LOGIN:String = "login";
    public static const NAVIGATE:String = "navigate";
    public static const CLEAR_MSG:String = "clearMsg";
    public static const IDENTITY_APPLIANCE_CHANGED:String = "identityApplianceChanged";
    public static const UPDATE_IDENTITY_APPLIANCE:String = "updateIdentityAppliance";
    public static const DISPLAY_APPLIANCE_MODELER:String = "displayApplianceModeler";
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
        registerCommandByConfigName(SETUP_SERVER, CommandNames.SETUP_SERVER_CMD);
        registerCommandByConfigName(REGISTER, CommandNames.REGISTER_CMD);
        registerCommandByConfigName(CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, CommandNames.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE_CMD);
        registerCommandByConfigName(CREATE_IDENTITY_APPLIANCE, CommandNames.CREATE_IDENTITY_APPLIANCE_CMD);
        registerCommandByConfigName(IDENTITY_APPLIANCE_REMOVE, CommandNames.IDENTITY_APPLIANCE_REMOVE_CMD);
        registerCommandByConfigName(IDENTITY_PROVIDER_REMOVE, CommandNames.IDENTITY_PROVIDER_REMOVE_CMD);
        registerCommandByConfigName(SERVICE_PROVIDER_REMOVE, CommandNames.SERVICE_PROVIDER_REMOVE_CMD);
        registerCommandByConfigName(IDP_CHANNEL_REMOVE, CommandNames.IDP_CHANNEL_REMOVE_CMD);
        registerCommandByConfigName(SP_CHANNEL_REMOVE, CommandNames.SP_CHANNEL_REMOVE_CMD);
        registerCommandByConfigName(DB_IDENTITY_VAULT_REMOVE, CommandNames.DB_IDENTITY_VAULT_REMOVE_CMD);
        registerCommandByConfigName(LOOKUP_IDENTITY_APPLIANCE_BY_ID, CommandNames.LOOKUP_IDENTITY_APPLIANCE_BY_ID_CMD);
        registerCommandByConfigName(IDENTITY_APPLIANCE_LIST_LOAD, CommandNames.IDENTITY_APPLIANCE_LIST_LOAD_CMD);
        registerCommandByConfigName(UPLOAD, CommandNames.UPLOAD_CMD);
        registerCommandByConfigName(BUILD_IDENTITY_APPLIANCE, CommandNames.BUILD_IDENTITY_APPLIANCE_CMD);
        registerCommandByConfigName(DEPLOY_IDENTITY_APPLIANCE, CommandNames.DEPLOY_IDENTITY_APPLIANCE_CMD);
        registerCommandByConfigName(EDIT_IDENTITY_APPLIANCE, CommandNames.EDIT_IDENTITY_APPLIANCE_CMD);

    }

}
}