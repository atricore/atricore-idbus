package com.atricore.idbus.console.modeling.main.controller {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.service.ServiceRegistry;

import com.atricore.idbus.console.main.view.progress.ProcessingMediator;
import com.atricore.idbus.console.modeling.diagram.model.request.ActivateExecutionEnvironmentRequest;
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.IdentityAppliance;
import com.atricore.idbus.console.services.spi.request.ActivateExecEnvRequest;
import com.atricore.idbus.console.services.spi.response.ActivateExecEnvResponse;
import com.atricore.idbus.console.services.spi.response.CreateSimpleSsoResponse;

import mx.rpc.Fault;

import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;
import mx.rpc.remoting.mxml.RemoteObject;

public class ActivateExecEnvironmentCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "ActivateExecEnvironmentCommand.SUCCESS";
    public static const FAILURE:String = "ActivateExecEnvironmentCommand.FAILURE";

    private var _projectProxy:ProjectProxy;
    private var _registry:ServiceRegistry;


    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function execute(notification:INotification):void {
        var identityAppliance:IdentityAppliance = _projectProxy.currentIdentityAppliance;
        var activateExecutionEnvRequest:ActivateExecutionEnvironmentRequest = notification.getBody() as ActivateExecutionEnvironmentRequest;

        var execEnvironment:ExecutionEnvironment = activateExecutionEnvRequest.executionEnvironment;
        var req:ActivateExecEnvRequest = new ActivateExecEnvRequest();
        req.applianceId = identityAppliance.id.toString();
        req.execEnvName = execEnvironment.name;
        req.reactivate = activateExecutionEnvRequest.reactivate;

        req.activateSamples = activateExecutionEnvRequest.installSamples;
        req.replace = activateExecutionEnvRequest.replaceConfFiles;

        sendNotification(ProcessingMediator.START, "Activating execution environment...");

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.IDENTITY_APPLIANCE_MANAGEMENT_SERVICE);

        var call:Object = service.activateExecEnv(req);
        call.addResponder(this);
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(ProcessingMediator.STOP);
        sendNotification(ApplicationFacade.SHOW_ERROR_MSG,
            "Execution environment activation failed.");
        sendNotification(FAILURE, msg);
    }

    public function result(data:Object):void {
        var resp:ActivateExecEnvResponse = data.result as ActivateExecEnvResponse;
        sendNotification(ProcessingMediator.STOP);
//        sendNotification(ApplicationFacade.SHOW_SUCCESS_MSG,
//                        "Execution environment successfully activated.");
        sendNotification(SUCCESS);
    }
}
}