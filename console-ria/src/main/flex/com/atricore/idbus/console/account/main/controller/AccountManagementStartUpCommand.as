/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.account.main.controller {
import com.atricore.idbus.console.account.main.AccountManagementMediator;
import com.atricore.idbus.console.main.controller.AppSectionStartUpCommand;
import com.atricore.idbus.console.main.controller.StartupContext;

public class AccountManagementStartUpCommand extends AppSectionStartUpCommand {

    public function AccountManagementStartUpCommand() {

    }

    public function get accountManagementMediator():AccountManagementMediator {
        return appSectionMediator as AccountManagementMediator;
    }

    public function set accountManagementMediator(value:AccountManagementMediator):void {
        appSectionMediator = value;
    }


    override protected function setupMediators(ctx:StartupContext):void {
        iocFacade.registerMediatorByConfigName(appSectionMediator.getConfigName());
    }
}
}
