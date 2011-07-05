/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.base.extensions.appsection {
import com.atricore.idbus.console.base.app.BaseStartupContext;

import mx.rpc.IResponder;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class AppSectionStartUpCommand extends IocSimpleCommand implements IResponder{

    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.StartUpCommand.SUCCESS";

    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.StartUpCommand.FAILURE";

    private var _appSectionMediator:AppSectionMediator;

    public function AppSectionStartUpCommand() {
    }

    override public function execute(note:INotification):void {
        var ctx:BaseStartupContext = note.getBody() as BaseStartupContext;
        setupServices(ctx);
        setupCommands(ctx);
        setupMediators(ctx);
    }


    public function get appSectionMediator():AppSectionMediator {
        return _appSectionMediator;
    }

    public function set appSectionMediator(value:AppSectionMediator):void {
        _appSectionMediator = value;
    }

    protected function setupServices(ctx:BaseStartupContext):void{

    }

    protected function setupCommands(ctx:BaseStartupContext):void{

    }

    protected function setupMediators(ctx:BaseStartupContext):void{
        iocFacade.registerMediatorByConfigName(appSectionMediator.getConfigName());
    }


    public function result(data:Object):void {
        // TODO : Need to propagate failure event!
        if (data.result == null) {
            //sendNotification(FAILURE);
            return;
        }

        //sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        // TODO : Need to propagate failure event!
        //sendNotification(FAILURE);
    }

}
}
