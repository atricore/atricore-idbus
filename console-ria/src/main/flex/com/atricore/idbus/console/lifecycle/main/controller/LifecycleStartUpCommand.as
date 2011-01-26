/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.lifecycle.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.lifecycle.main.LifecycleMediator;
import com.atricore.idbus.console.main.ApplicationFacade;

import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;

public class LifecycleStartUpCommand extends AppSectionStartUpCommand {

    private var _buildIdentityApplianceCommand:IIocCommand;
    private var _deployIdentityApplianceCommand:IIocCommand;
    private var _undeployIdentityApplianceCommand:IIocCommand;
    private var _startIdentityApplianceCommand:IIocCommand;
    private var _stopIdentityApplianceCommand:IIocCommand;
    private var _disposeIdentityApplianceCommand:IIocCommand;

    public function LifecycleStartUpCommand() {
    }

    public function get lifecycleMediator():LifecycleMediator {
        return appSectionMediator as LifecycleMediator;
    }

    public function set lifecycleMediator(value:LifecycleMediator):void {
        appSectionMediator = value;
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        iocFacade.registerMediatorByConfigName(lifecycleMediator.getConfigName());
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        iocFacade.registerCommandByConfigName(ApplicationFacade.BUILD_IDENTITY_APPLIANCE, buildIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DEPLOY_IDENTITY_APPLIANCE, deployIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UNDEPLOY_IDENTITY_APPLIANCE, undeployIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.START_IDENTITY_APPLIANCE, startIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.STOP_IDENTITY_APPLIANCE, stopIdentityApplianceCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.DISPOSE_IDENTITY_APPLIANCE, disposeIdentityApplianceCommand.getConfigName());
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

    public function get undeployIdentityApplianceCommand():IIocCommand {
        return _undeployIdentityApplianceCommand;
    }

    public function set undeployIdentityApplianceCommand(value:IIocCommand):void {
        _undeployIdentityApplianceCommand = value;
    }

    public function get startIdentityApplianceCommand():IIocCommand {
        return _startIdentityApplianceCommand;
    }

    public function set startIdentityApplianceCommand(value:IIocCommand):void {
        _startIdentityApplianceCommand = value;
    }

    public function get stopIdentityApplianceCommand():IIocCommand {
        return _stopIdentityApplianceCommand;
    }

    public function set stopIdentityApplianceCommand(value:IIocCommand):void {
        _stopIdentityApplianceCommand = value;
    }

    public function get disposeIdentityApplianceCommand():IIocCommand {
        return _disposeIdentityApplianceCommand;
    }

    public function set disposeIdentityApplianceCommand(value:IIocCommand):void {
        _disposeIdentityApplianceCommand = value;
    }
}
}
