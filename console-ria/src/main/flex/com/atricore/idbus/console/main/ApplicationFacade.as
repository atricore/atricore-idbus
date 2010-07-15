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
import com.atricore.idbus.console.modeling.main.controller.IdentityApplianceUpdateCommand;
import com.atricore.idbus.console.modeling.main.controller.IdentityProviderRemoveCommand;

import com.atricore.idbus.console.modeling.main.controller.LookupIdentityApplianceByIdCommand;

import org.puremvc.as3.patterns.facade.Facade;

public class ApplicationFacade extends Facade {

    public static const USER_PROVISIONING_SERVICE:String = "userProvisioningService";
    public static const IDENTITY_APPLIANCE_MANAGEMENT_SERVICE:String = "identityApplianceManagementService";

    // Notification name constants application
    public static const NOTE_STARTUP:String = "startup";
    public static const NOTE_SETUP_SERVER:String = "Note.SetupServer";

    public static const NOTE_SHOW_ERROR_MSG:String = "Note.ShowErrorMsg";
    public static const NOTE_SHOW_SUCCESS_MSG:String = "Note.ShowSuccessMsg";
    public static const NOTE_LOGIN:String = "Note.Login";
    public static const NOTE_NAVIGATE:String = "Note.Navigate";
    public static const NOTE_CLEAR_MSG:String = "Note.ClearMsg";
    public static const NOTE_REGISTER:String = "Note.Register";
    public static const NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE:String = "Note.CreateSimpleSSOIdentityAppliance";
    public static const NOTE_LOOKUP_IDENTITY_APPLIANCE_BY_ID:String = "Note.LookupIdentityApplianceById";
    public static const NOTE_IDENTITY_APPLIANCE_LIST_LOAD:String = "Note.IdentityApplianceListLoad";
    public static const NOTE_CREATE_IDENTITY_APPLIANCE:String = "Note.CreateIdentityAppliance";
    public static const NOTE_IDENTITY_PROVIDER_REMOVE:String = "Node.IdentityProviderRemove" ;
    public static const NOTE_IDENTITY_APPLIANCE_CHANGED:String = "Note.IdentityApplianceChanged";
    public static const NOTE_EDIT_IDENTITY_APPLIANCE:String = "Note.EditIdentityAppliance";
    public static const NOTE_UPDATE_IDENTITY_APPLIANCE:String = "Note.UpdateIdentityAppliance";
    public static const NOTE_DISPLAY_APPLIANCE_MODELER:String = "Note.DisplayApplianceModeler";
    public static const NOTE_DRAG_ELEMENT_TO_DIAGRAM:String = "Note.DragElementToDiagram";
    public static const NOTE_CREATE_DIAGRAM_ELEMENT:String = "Note.CreateDiagramElement";
    public static const NOTE_CREATE_IDENTITY_PROVIDER_ELEMENT:String = "Note.CreateIdentityProviderElement";
    public static const NOTE_CREATE_SERVICE_PROVIDER_ELEMENT:String = "Note.CreateServiceProviderElement";
    public static const NOTE_DIAGRAM_ELEMENT_CREATION_COMPLETE:String = "Note.DiagramElementCreationComplete";
    public static const NOTE_DIAGRAM_ELEMENT_SELECTED:String = "Note.DiagramElementSelected";
    public static const NOTE_DIAGRAM_ELEMENT_UPDATED:String = "Note.DiagramElementUpdated";
    public static const NOTE_DIAGRAM_ELEMENT_REMOVE:String = "Note.DiagramElementRemove";
    public static const NOTE_REMOVE_IDENTITY_APPLIANCE_ELEMENT:String = "Node.RemoveIdentityApplianceElement";
    public static const NOTE_REMOVE_IDENTITY_PROVIDER_ELEMENT:String = "Node.RemoveIdentityProviderElement";
    public static const NOTE_REMOVE_SERVICE_PROVIDER_ELEMENT:String = "Node.RemoveServiceProviderElement";
    public static const NOTE_MANAGE_CERTIFICATE:String = "Note.ManageCertificate";
    public static const NOTE_SHOW_UPLOAD_PROGRESS:String = "Note.UploadProgress";
    public static const NOTE_UPLOAD:String = "Note.Upload";
    public static const NOTE_BUILD_IDENTITY_APPLIANCE:String = "Note.BuildIdentityAppliance";
    public static const NOTE_DEPLOY_IDENTITY_APPLIANCE:String = "Note.DeployIdentityAppliance";


    public static function getInstance():ApplicationFacade {
        if (instance == null) {
            instance = new ApplicationFacade();
        }
        return instance as ApplicationFacade;
    }

    /**
     * Register Commands with the Controller
     */
    override protected function initializeController():void {
        super.initializeController();
        registerCommand(NOTE_STARTUP, ApplicationStartUpCommand);
        registerCommand(NOTE_SETUP_SERVER, SetupServerCommand);
        registerCommand(NOTE_REGISTER, RegisterCommand);
        registerCommand(NOTE_CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, CreateSimpleSSOIdentityApplianceCommand);
        registerCommand(NOTE_CREATE_IDENTITY_APPLIANCE, IdentityApplianceCreateCommand);
        registerCommand(NOTE_IDENTITY_PROVIDER_REMOVE, IdentityProviderRemoveCommand);
        registerCommand(NOTE_LOOKUP_IDENTITY_APPLIANCE_BY_ID, LookupIdentityApplianceByIdCommand);
        registerCommand(NOTE_IDENTITY_APPLIANCE_LIST_LOAD, IdentityApplianceListLoadCommand);
        registerCommand(NOTE_UPLOAD, UploadCommand);
        registerCommand(NOTE_BUILD_IDENTITY_APPLIANCE, BuildIdentityApplianceCommand);
        registerCommand(NOTE_DEPLOY_IDENTITY_APPLIANCE, DeployIdentityApplianceCommand);
        registerCommand(NOTE_EDIT_IDENTITY_APPLIANCE, IdentityApplianceUpdateCommand);
    }

    public function startUp(app:AtricoreConsole):void {
        sendNotification(NOTE_STARTUP, app);
    }
}
}