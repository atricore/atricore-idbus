package com.atricore.idbus.console.help.main.controller {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionStartUpCommand;
import com.atricore.idbus.console.help.main.HelpMediator;

public class HelpStartUpCommand extends AppSectionStartUpCommand {

    public function HelpStartUpCommand() {
    }

    public function get helpMediator():HelpMediator {
        return appSectionMediator as HelpMediator;
    }

    public function set helpMediator(value:HelpMediator):void {
        appSectionMediator = value;
    }
}
}