/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.main.controller {
import com.atricore.idbus.console.main.service.ServiceRegistry;

import mx.messaging.config.ServerConfig;
import mx.rpc.IResponder;
import mx.messaging.Channel;


import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.interfaces.IIocProxy;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;



public class AppSectionStartUpCommand extends IocSimpleCommand implements IResponder{

    public static const SUCCESS:String = "com.atricore.idbus.console.main.controller.StartUpCommand.SUCCESS";

    public static const FAILURE:String = "com.atricore.idbus.console.main.controller.UpCommand.FAILURE";

    private var _serviceRegistry:IIocProxy;

    public function AppSectionStartUpCommand() {
    }

    override public function execute(note:INotification):void {
        var ctx:StartupContext = note.getBody() as StartupContext;
        setupServices(ctx);

        setupCommands(ctx);

        setupMediators(ctx);

    }

    protected function setupServices(ctx:StartupContext):void{

    }

    protected function setupCommands(ctx:StartupContext):void{

    }

    protected function setupMediators(ctx:StartupContext):void{

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
