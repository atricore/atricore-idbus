package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;

import com.atricore.idbus.console.main.service.ServiceRegistry;

import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;
import com.atricore.idbus.console.services.spi.request.CheckInstallFolderExistenceRequest;

import com.atricore.idbus.console.services.spi.response.CheckInstallFolderExistenceResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class FolderExistsCommand extends IocSimpleCommand implements IResponder {

    public static const FOLDER_EXISTS : String = "FolderExistsCommand.FOLDER_EXISTS";
    public static const FOLDER_DOESNT_EXISTS : String = "FolderExistsCommand.FOLDER_DOESNT_EXISTS";
    public static const FAILURE : String = "FolderExistsCommand.FAILURE";

    private var _registry:ServiceRegistry;


    public function FolderExistsCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }    

    override public function execute(notification:INotification):void {
//        var folder:String = notification.getBody() as String;
        var cif:CheckInstallFolderRequest = notification.getBody() as CheckInstallFolderRequest;

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);
        var req:CheckInstallFolderExistenceRequest = new CheckInstallFolderExistenceRequest();
        req.installFolder = cif.homeDir;
        req.environmentName = cif.environmentName;
        var call:Object = service.checkInstallFolderExistence(req);
        call.addResponder(this);
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }

    public function result(data:Object):void {
        var resp:CheckInstallFolderExistenceResponse = data.result as CheckInstallFolderExistenceResponse;
        if(resp.folderExists){
            sendNotification(FOLDER_EXISTS, resp.environmentName);
        } else {
            sendNotification(FOLDER_DOESNT_EXISTS, resp.environmentName);
        }
    }
}
}