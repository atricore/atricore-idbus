/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.controller.AppSectionStartUpCommand;

import com.atricore.idbus.console.main.controller.StartupContext;

import com.atricore.idbus.console.modeling.main.ModelerMediator;

import mx.controls.Alert;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocMediator;

public class ModelerStartUpCommand extends AppSectionStartUpCommand{

    public function ModelerStartUpCommand() {
    }


    public function get modelerMediator():ModelerMediator {
        return appSectionMediator as ModelerMediator;
    }

    public function set modelerMediator(value:ModelerMediator):void {
        appSectionMediator = value;
    }

    override protected function setupServices(ctx:StartupContext):void {
        // TODO : Setup services for Modeler
    }

    override protected function setupMediators(ctx:StartupContext):void {
        iocFacade.registerMediatorByConfigName(appSectionMediator.getConfigName());
        // TODO : Setup other mediators
    }

    override protected function setupCommands(ctx:StartupContext):void {
        // TODO : Setup commands for Modeler
    }


}
}
