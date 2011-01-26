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
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import mx.messaging.Channel;
import mx.messaging.config.ServerConfig;
import mx.rpc.IResponder;

import org.puremvc.as3.interfaces.*;
import org.springextensions.actionscript.ioc.IObjectDefinition;
import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class ApplicationStartUpCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.SUCCESS";
    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.ApplicationStartUpCommand.FAILURE";

    // TODO : EXTENSIONS Remove All specific components
    /* Proxies */
    private var _serviceRegistry:IIocProxy;
    private var _projectProxy:IIocProxy;
    private var _keystoreProxy:IIocProxy;
    private var _profileProxy:IIocProxy;
    private var _secureContextProxy:IIocProxy;

    /* Mediators */
    private var _applicationMediator:IIocMediator;
    private var _processingMediator:IIocMediator;
    private var _loginMediator:IIocMediator;
    private var _changePasswordMediator:IIocMediator;
    //private var _setupWizardViewMediator:IIocMediator;
    //private var _uploadProgressMediator:IIocMediator;

    /* Commands */
    private var _loginCommand:IIocCommand;
    private var _changePasswordCommand:IIocCommand;
    private var _notFirstRunCommand:IIocCommand;
    //private var _setupServerCommand:IIocCommand;    
    //private var _registerCommand:IIocCommand;
    //private var _uploadCommand:IIocCommand;

    public function ApplicationStartUpCommand() {
    }

    public function get applicationMediator():IIocMediator {
        return _applicationMediator;
    }

    public function set applicationMediator(value:IIocMediator):void {
        _applicationMediator = value;
    }

    public function get processingMediator():IIocMediator {
        return _processingMediator;
    }

    public function set processingMediator(value:IIocMediator):void {
        _processingMediator = value;
    }

    public function get serviceRegistry():IIocProxy {
        return _serviceRegistry;
    }

    public function set serviceRegistry(value:IIocProxy):void {
        _serviceRegistry = value;
    }

    public function get projectProxy():IIocProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:IIocProxy):void {
        _projectProxy = value;
    }

    public function get keystoreProxy():IIocProxy {
        return _keystoreProxy;
    }

    public function set keystoreProxy(value:IIocProxy):void {
        _keystoreProxy = value;
    }

    public function get profileProxy():IIocProxy {
        return _profileProxy;
    }

    public function set profileProxy(value:IIocProxy):void {
        _profileProxy = value;
    }

    public function get secureContextProxy():IIocProxy {
        return _secureContextProxy;
    }

    public function set secureContextProxy(value:IIocProxy):void {
        _secureContextProxy = value;
    }

    public function get loginMediator():IIocMediator {
        return _loginMediator;
    }

    public function set loginMediator(value:IIocMediator):void {
        _loginMediator = value;
    }

    public function get changePasswordMediator():IIocMediator {
        return _changePasswordMediator;
    }

    public function set changePasswordMediator(value:IIocMediator):void {
        _changePasswordMediator = value;
    }

    public function get loginCommand():IIocCommand {
        return _loginCommand;
    }

    public function set loginCommand(value:IIocCommand):void {
        _loginCommand = value;
    }

    public function get changePasswordCommand():IIocCommand {
        return _changePasswordCommand;
    }

    public function set changePasswordCommand(value:IIocCommand):void {
        _changePasswordCommand = value;
    }

    public function get notFirstRunCommand():IIocCommand {
        return _notFirstRunCommand;
    }

    public function set notFirstRunCommand(value:IIocCommand):void {
        _notFirstRunCommand = value;
    }

    override public function execute(note:INotification):void {
        var registry:ServiceRegistry = setupServiceRegistry();
        var app:AtricoreConsole = note.getBody() as AtricoreConsole;
        var startupCtx:StartupContext = new StartupContext(app, registry);

        // Wire all STARTUP commands and send STARTUP notifications :)
        var appSectionStartUpCmdNames:Array = iocFacade.container.getObjectNamesForType(AppSectionStartUpCommand);

        appSectionStartUpCmdNames.forEach(function(appSectionStartUpCmdName:String, idx:int, arr:Array):void {

            // Get command object
            var appSectionStartUpCmd:AppSectionStartUpCommand =
                    iocFacade.container.getObject(appSectionStartUpCmdName) as AppSectionStartUpCommand;

            // Build note name
            var od:IObjectDefinition = iocFacade.container.getObjectDefinition(appSectionStartUpCmdName);
            var appSectionStartUpNote:String = od.className + ".APP_SECTION_STARTUP";

            // Register command for note
            iocFacade.registerCommandByConfigName(appSectionStartUpNote , appSectionStartUpCmd.getConfigName());

            // Send notification, including startup context
            sendNotification(appSectionStartUpNote, startupCtx);

        });

        // first register commands (some commands are needed for mediator creation/initialization)
        iocFacade.registerCommandByConfigName(ApplicationFacade.LOGIN, loginCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.CHANGE_PASSWORD, changePasswordCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.NOT_FIRST_RUN, notFirstRunCommand.getConfigName());
        //iocFacade.registerCommandByConfigName(ApplicationFacade.SETUP_SERVER, setupServerCommand.getConfigName());
        //iocFacade.registerCommandByConfigName(ApplicationFacade.REGISTER, registerCommand.getConfigName());
        //iocFacade.registerCommandByConfigName(ApplicationFacade.UPLOAD, uploadCommand.getConfigName());

        //iocFacade.registerCommandByConfigName(ApplicationFacade.EXPORT_IDENTITY_APPLIANCE, exportIdentityApplianceCommand.getConfigName());
        //iocFacade.registerCommandByConfigName(ApplicationFacade.EXPORT_METADATA, exportMetadataCommand.getConfigName());
        //iocFacade.registerCommandByConfigName(ApplicationFacade.EXPORT_PROVIDER_CERTIFICATE, exportProviderCertificateCommand.getConfigName());


        // setup for first level mediators
        applicationMediator.setViewComponent(app);
        iocFacade.registerMediatorByConfigName(applicationMediator.getConfigName());

        loginMediator.setViewComponent(app.loginView);
        iocFacade.registerMediatorByConfigName(loginMediator.getConfigName());

        // register mediators for popup managers - popup managers will wire the corresponding view to it
        //iocFacade.registerMediatorByConfigName(setupWizardViewMediator.getConfigName());

        //iocFacade.registerMediatorByConfigName(uploadProgressMediator.getConfigName());
        //iocFacade.registerMediatorByConfigName(buildApplianceMediator.getConfigName());
        //iocFacade.registerMediatorByConfigName(deployApplianceMediator.getConfigName());
        iocFacade.registerMediatorByConfigName(processingMediator.getConfigName());

        iocFacade.registerMediatorByConfigName(changePasswordMediator.getConfigName());
    }

    protected function setupServiceRegistry():ServiceRegistry {
        // Setup service registry
        var channel:Channel = ServerConfig.getChannel("my-amf");
        var registry:ServiceRegistry = serviceRegistry as ServiceRegistry;
        registry.setChannel(channel);

        // TODO : EXTENSIONS Remove and send to specific startup-commands
        registry.registerRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE, ApplicationFacade.USER_PROVISIONING_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE, ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.PROFILE_MANAGEMENT_SERVICE, ApplicationFacade.PROFILE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.SIGN_ON_SERVICE, ApplicationFacade.SIGN_ON_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.LICENSE_MANAGEMENT_SERVICE, ApplicationFacade.LICENSE_MANAGEMENT_SERVICE);
        registry.registerRemoteObjectService(ApplicationFacade.LIVE_UPDATE_SERVICE, ApplicationFacade.LIVE_UPDATE_SERVICE);

        return registry;
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
