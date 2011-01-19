/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.lifecycle.main.controller {
import com.atricore.idbus.console.lifecycle.main.LifecycleMediator;
import com.atricore.idbus.console.main.controller.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.controller.StartupContext;

public class LifecycleStartUpCommand extends AppSectionStartUpCommand {

    public function LifecycleStartUpCommand() {
    }


    public function get lifecycleMediator():LifecycleMediator {
        return appSectionMediator as LifecycleMediator;
    }

    public function set lifecycleMediator(value:LifecycleMediator):void {
        appSectionMediator = value;
    }

    override protected function setupMediators(ctx:StartupContext):void {
        iocFacade.registerMediatorByConfigName(lifecycleMediator.getConfigName());
    }
}
}
