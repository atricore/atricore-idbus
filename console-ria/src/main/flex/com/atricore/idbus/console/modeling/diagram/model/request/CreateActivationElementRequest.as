package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ServiceResource;

public class CreateActivationElementRequest {

    private var _serviceResource:ServiceResource;
    private var _executionEnvironment:ExecutionEnvironment;
    private var _notationalElementId:String;

    public function CreateActivationElementRequest() {
    }

    public function get serviceResource():ServiceResource {
        return _serviceResource;
    }

    public function set serviceResource(value:ServiceResource):void {
        _serviceResource = value;
    }

    public function get executionEnvironment():ExecutionEnvironment {
        return _executionEnvironment;
    }

    public function set executionEnvironment(value:ExecutionEnvironment):void {
        _executionEnvironment = value;
    }

    public function get notationalElementId():String {
        return _notationalElementId;
    }

    public function set notationalElementId(value:String):void {
        _notationalElementId = value;
    }
}
}