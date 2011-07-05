/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.settings.main.SettingsMediator;

import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class SettingsStartUpCommand extends AppSectionStartUpCommand {

    private var _menuMediator:IIocMediator;

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
}
}
