/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.controller {
import com.atricore.idbus.console.base.app.BaseStartupContext;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.settings.main.SettingsMediator;


public class SettingsStartUpCommand extends AppSectionStartUpCommand {

    private var _settingsMediator:SettingsMediator;

    public function SettingsStartUpCommand() {
    }


    public function get settingsMediator():SettingsMediator {
        return _settingsMediator;
    }

    public function set settingsMediator(value:SettingsMediator):void {
        _settingsMediator = value;
    }

    override protected function setupCommands(ctx:BaseStartupContext):void {
        super.setupCommands(ctx);
    }

    override protected function setupMediators(ctx:BaseStartupContext):void {
        super.setupMediators(ctx);
        iocFacade.registerMediatorByConfigName(settingsMediator.getConfigName());

    }

}
}
