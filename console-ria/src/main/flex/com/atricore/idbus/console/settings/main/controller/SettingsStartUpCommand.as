/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.settings.main.SettingsMediator;

import org.springextensions.actionscript.puremvc.interfaces.IIocCommand;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class SettingsStartUpCommand extends AppSectionStartUpCommand {

    private var _menuMediator:IIocMediator;

    private var _getServiceConfigCommand:IIocCommand;
    private var _updateServiceConfigCommand:IIocCommand;

    public function SettingsStartUpCommand() {
    }

    public function get settingsMediator():SettingsMediator {
        return appSectionMediator as SettingsMediator;
    }

    public function set settingsMediator(value:SettingsMediator):void {
        appSectionMediator = value;
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
        iocFacade.registerCommandByConfigName(ApplicationFacade.GET_SERVICE_CONFIG, getServiceConfigCommand.getConfigName());
        iocFacade.registerCommandByConfigName(ApplicationFacade.UPDATE_SERVICE_CONFIG, updateServiceConfigCommand.getConfigName());
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        super.setupMediators(ctx);
        iocFacade.registerMediatorByConfigName(menuMediator.getConfigName());
    }

    public function get menuMediator():IIocMediator {
        return _menuMediator;
    }

    public function set menuMediator(value:IIocMediator):void {
        _menuMediator = value;
    }

    public function get getServiceConfigCommand():IIocCommand {
        return _getServiceConfigCommand;
    }

    public function set getServiceConfigCommand(value:IIocCommand):void {
        _getServiceConfigCommand = value;
    }

    public function get updateServiceConfigCommand():IIocCommand {
        return _updateServiceConfigCommand;
    }

    public function set updateServiceConfigCommand(value:IIocCommand):void {
        _updateServiceConfigCommand = value;
    }
}
}
