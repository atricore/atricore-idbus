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

package com.atricore.idbus.console.main.controller
{
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.spi.request.FindGroupByNameRequest;

import mx.managers.BrowserManager;
import mx.managers.IBrowserManager;
import mx.messaging.Channel;
import mx.messaging.config.ServerConfig;
import mx.rpc.IResponder;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.*;
import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ApplicationStartUpCommand extends IocSimpleCommand implements IResponder {
    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.FAILURE";

    public static var ADMIN_GROUP:String = "Administrators";

    private var _applicationMediator:IIocMediator;
    private var _modelerMediator:IIocMediator;
    private var _lifecycleMediator:IIocMediator;
    private var _browserMediator:IIocMediator;
    private var _diagramMediator:IIocMediator;
    private var _paletteMediator:IIocMediator;
    private var _propertySheetMediator:IIocMediator;
    private var _setupWizardMediator:IIocMediator;
    private var _simpleSSOWizardMediator:IIocMediator;
    private var _identityApplianceMediator:IIocMediator;
    private var _manageCertificateMediator:IIocMediator;
    private var _identityProviderCreateMediator:IIocMediator;
    private var _serviceProviderCreateMediator:IIocMediator;
    private var _idpChannelCreateMediator:IIocMediator;
    private var _spChannelCreateMediator:IIocMediator;
    private var _dbIdentityVaultWizardViewMediator:IIocMediator;
    private var _uploadProgressMediator:IIocMediator;
    private var _buildApplianceMediator:IIocMediator;
    private var _deployApplianceMediator:IIocMediator;

    private var _serviceRegistry:IIocProxy;

    private var _setupServerCommand:IIocCommand;
    private var _registerCommand:IIocCommand;
    private var _createSimpleSSOApplianceCommand:IIocCommand;
    private var _createIdentityApplianceCommand:IIocCommand;
    private var _identityApplianceRemoveCommand:IIocCommand;
    private var _identityProviderRemoveCommand:IIocCommand;
    private var _serviceProviderRemoveCommand:IIocCommand;
    private var _idpChannelRemoveCommand:IIocCommand;
    private var _spChannelRemoveCommand:IIocCommand;
    private var _identityVauleRemoveCommand:IIocCommand;
    private var _lookupIdentityApplianceByIdCommand:IIocCommand;
    private var _identityApplianceListCommand:IIocCommand;
    private var _uploadCommand:IIocCommand;
    private var _buildIdentityApplianceCommand:IIocCommand;
    private var _deployIdentityApplianceCommand:IIocCommand;
    private var _editIdentityApplianceCommand:IIocCommand;

    public function set applicationMediator(p_applicationMediator:IIocMediator):void {
        _applicationMediator = p_applicationMediator;
    }

    public function get applicationMediator():IIocMediator {
        return _applicationMediator;
    }

    public function set modelerMediator(p_modelerMediator:IIocMediator):void {
        _modelerMediator = p_modelerMediator;
    }

    public function get modelerMediator():IIocMediator {
        return _modelerMediator;
    }

    public function set lifecycleMediator(value:IIocMediator):void {
        _lifecycleMediator = value;
    }

    public function get lifecycleMediator():IIocMediator {
        return _lifecycleMediator;
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

    public function set serviceRegistry(p_serviceRegistry:IIocProxy):void {
        _serviceRegistry = p_serviceRegistry;
    }

    public function get serviceRegistry():IIocProxy {
        return _serviceRegistry;
    }


    public function get setupWizardMediator():IIocMediator {
        return _setupWizardMediator;
    }

    public function set setupWizardMediator(value:IIocMediator):void {
        _setupWizardMediator = value;
    }

    public function get simpleSSOWizardMediator():IIocMediator {
        return _simpleSSOWizardMediator;
    }

    public function set simpleSSOWizardMediator(value:IIocMediator):void {
        _simpleSSOWizardMediator = value;
    }

    public function get identityApplianceMediator():IIocMediator {
        return _identityApplianceMediator;
    }

    public function set identityApplianceMediator(value:IIocMediator):void {
        _identityApplianceMediator = value;
    }

    public function get manageCertificateMediator():IIocMediator {
        return _manageCertificateMediator;
    }

    public function set manageCertificateMediator(value:IIocMediator):void {
        _manageCertificateMediator = value;
    }

    public function get identityProviderCreateMediator():IIocMediator {
        return _identityProviderCreateMediator;
    }

    public function set identityProviderCreateMediator(value:IIocMediator):void {
        _identityProviderCreateMediator = value;
    }

    public function get serviceProviderCreateMediator():IIocMediator {
        return _serviceProviderCreateMediator;
    }

    public function set serviceProviderCreateMediator(value:IIocMediator):void {
        _serviceProviderCreateMediator = value;
    }

    public function get idpChannelCreateMediator():IIocMediator {
        return _idpChannelCreateMediator;
    }

    public function set idpChannelCreateMediator(value:IIocMediator):void {
        _idpChannelCreateMediator = value;
    }

    public function get spChannelCreateMediator():IIocMediator {
        return _spChannelCreateMediator;
    }

    public function set spChannelCreateMediator(value:IIocMediator):void {
        _spChannelCreateMediator = value;
    }

    public function get dbIdentityVaultWizardViewMediator():IIocMediator {
        return _dbIdentityVaultWizardViewMediator;
    }

    public function set dbIdentityVaultWizardViewMediator(value:IIocMediator):void {
        _dbIdentityVaultWizardViewMediator = value;
    }

    public function get uploadProgressMediator():IIocMediator {
        return _uploadProgressMediator;
    }

    public function set uploadProgressMediator(value:IIocMediator):void {
        _uploadProgressMediator = value;
    }

    public function get buildApplianceMediator():IIocMediator {
        return _buildApplianceMediator;
    }

    public function set buildApplianceMediator(value:IIocMediator):void {
        _buildApplianceMediator = value;
    }

    public function get deployApplianceMediator():IIocMediator {
        return _deployApplianceMediator;
    }

    public function set deployApplianceMediator(value:IIocMediator):void {
        _deployApplianceMediator = value;
    }

    public function get setupServerCommand():IIocCommand {
        return _setupServerCommand;
    }

    public function set setupServerCommand(value:IIocCommand):void {
        _setupServerCommand = value;
    }

    public function get registerCommand():IIocCommand {
        return _registerCommand;
    }

    public function set registerCommand(value:IIocCommand):void {
        _registerCommand = value;
    }

    public function get createSimpleSSOApplianceCommand():IIocCommand {
        return _createSimpleSSOApplianceCommand;
    }

    public function set createSimpleSSOApplianceCommand(value:IIocCommand):void {
        _createSimpleSSOApplianceCommand = value;
    }

    public function get createIdentityApplianceCommand():IIocCommand {
        return _createIdentityApplianceCommand;
    }

    public function set createIdentityApplianceCommand(value:IIocCommand):void {
        _createIdentityApplianceCommand = value;
    }

    public function get identityApplianceRemoveCommand():IIocCommand {
        return _identityApplianceRemoveCommand;
    }

    public function set identityApplianceRemoveCommand(value:IIocCommand):void {
        _identityApplianceRemoveCommand = value;
    }

    public function get identityProviderRemoveCommand():IIocCommand {
        return _identityProviderRemoveCommand;
    }

    public function set identityProviderRemoveCommand(value:IIocCommand):void {
        _identityProviderRemoveCommand = value;
    }

    public function get serviceProviderRemoveCommand():IIocCommand {
        return _serviceProviderRemoveCommand;
    }

    public function set serviceProviderRemoveCommand(value:IIocCommand):void {
        _serviceProviderRemoveCommand = value;
    }

    public function get idpChannelRemoveCommand():IIocCommand {
        return _idpChannelRemoveCommand;
    }

    public function set idpChannelRemoveCommand(value:IIocCommand):void {
        _idpChannelRemoveCommand = value;
    }

    public function get spChannelRemoveCommand():IIocCommand {
        return _spChannelRemoveCommand;
    }

    public function set spChannelRemoveCommand(value:IIocCommand):void {
        _spChannelRemoveCommand = value;
    }

    public function get identityVauleRemoveCommand():IIocCommand {
        return _identityVauleRemoveCommand;
    }

    public function set identityVauleRemoveCommand(value:IIocCommand):void {
        _identityVauleRemoveCommand = value;
    }

    public function get lookupIdentityApplianceByIdCommand():IIocCommand {
        return _lookupIdentityApplianceByIdCommand;
    }

    public function set lookupIdentityApplianceByIdCommand(value:IIocCommand):void {
        _lookupIdentityApplianceByIdCommand = value;
    }

    public function get identityApplianceListCommand():IIocCommand {
        return _identityApplianceListCommand;
    }

    public function set identityApplianceListCommand(value:IIocCommand):void {
        _identityApplianceListCommand = value;
    }

    public function get uploadCommand():IIocCommand {
        return _uploadCommand;
    }

    public function set uploadCommand(value:IIocCommand):void {
        _uploadCommand = value;
    }

    public function get buildIdentityApplianceCommand():IIocCommand {
        return _buildIdentityApplianceCommand;
    }

    public function set buildIdentityApplianceCommand(value:IIocCommand):void {
        _buildIdentityApplianceCommand = value;
    }

    public function get deployIdentityApplianceCommand():IIocCommand {
        return _deployIdentityApplianceCommand;
    }

    public function set deployIdentityApplianceCommand(value:IIocCommand):void {
        _deployIdentityApplianceCommand = value;
    }

    public function get editIdentityApplianceCommand():IIocCommand {
        return _editIdentityApplianceCommand;
    }

    public function set editIdentityApplianceCommand(value:IIocCommand):void {
        _editIdentityApplianceCommand = value;
    }

    override public function execute(note:INotification):void {
        var registry:ServiceRegistry = setupServiceRegistry();

        var app:AtricoreConsole = note.getBody() as AtricoreConsole;

        // setup for first level mediators
        applicationMediator.setViewComponent(app);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        modelerMediator.setViewComponent(app.modelerView);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        lifecycleMediator.setViewComponent(app.lifecycleView);
        iocFacade.registerMediatorByConfigName(lifecycleMediator.getConfigName());


        // setup for second level modeler mediators
        browserMediator.setViewComponent(app.modelerView.browser);
        iocFacade.registerMediatorByConfigName(browserMediator.getConfigName());

        diagramMediator.setViewComponent(app.modelerView.diagram);
        iocFacade.registerMediatorByConfigName(diagramMediator.getConfigName());

        paletteMediator.setViewComponent(app.modelerView.palette);
        iocFacade.registerMediatorByConfigName(paletteMediator.getConfigName());

        propertySheetMediator.setViewComponent(app.modelerView.propertysheet);
        iocFacade.registerMediatorByConfigName(propertySheetMediator.getConfigName());

       // register mediators for popup managers - popup managers will wire the corresponding view to it
        iocFacade.registerMediatorByConfigName(setupWizardMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(simpleSSOWizardMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(manageCertificateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(identityProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(serviceProviderCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(idpChannelCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(spChannelCreateMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(dbIdentityVaultWizardViewMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(uploadProgressMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(buildApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(deployApplianceMediator.getConfigName());

        // register commands
        iocFacade.registerCommandByConfigName(ApplicationFacade.SETUP_SERVER, setupServerCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.REGISTER, registerCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE, createSimpleSSOApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CREATE_IDENTITY_APPLIANCE, createIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_REMOVE, identityApplianceRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_PROVIDER_REMOVE, identityProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SERVICE_PROVIDER_REMOVE, serviceProviderRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDP_CHANNEL_REMOVE, idpChannelRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.SP_CHANNEL_REMOVE, spChannelRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DB_IDENTITY_VAULT_REMOVE, identityVauleRemoveCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOOKUP_IDENTITY_APPLIANCE_BY_ID, lookupIdentityApplianceByIdCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.IDENTITY_APPLIANCE_LIST_LOAD, identityApplianceListCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UPLOAD, uploadCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, buildIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DEPLOY_IDENTITY_APPLIANCE, deployIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.EDIT_IDENTITY_APPLIANCE, editIdentityApplianceCommand.getConfigName());

        checkFirstRun();
    }

    protected function setupServiceRegistry():ServiceRegistry {
        var channel:Channel = ServerConfig.getChannel("my-amf");

        var registry:ServiceRegistry = serviceRegistry as ServiceRegistry;
        registry.setChannel(channel);
        registry.registerRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE, ApplicationFacade.USER_PROVISIONING_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE, ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.PROFILE_MANAGEMENT_SERVICE, ApplicationFacade.PROFILE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE, ApplicationFacade.SIGN_ON_SERVICE);

        return registry;
    }

    private function checkFirstRun():void {

        var browserManager:IBrowserManager = BrowserManager.getInstance();
        browserManager.init();
        browserManager.setTitle("Atricore Identity Bus | Administration Console");

        var registry:ServiceRegistry = _serviceRegistry as ServiceRegistry;
        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);

        var findAdminGroup:FindGroupByNameRequest = new FindGroupByNameRequest();
        findAdminGroup.name = ADMIN_GROUP;
        var call:Object = service.findGroupByName(findAdminGroup);
        call.addResponder(this);

        /*
         var findAdminGroup:FindGroupByNameRequest = new FindGroupByNameRequest();
         findAdminGroup.name = ADMIN_GROUP;
         var call:Object = service.findGroupByName(findAdminGroup);
         call.addResponder(this);
         */
    }

    public function result(data:Object):void {
        if (data.result == null) {
            sendNotification(FAILURE);
            return;
        }

        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        sendNotification(FAILURE);

    }

}
}
