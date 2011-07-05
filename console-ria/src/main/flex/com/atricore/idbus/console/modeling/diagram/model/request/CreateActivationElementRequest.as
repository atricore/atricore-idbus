package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.ServiceProvider;

public class CreateActivationElementRequest {

    private var _sp:ServiceProvider;
    private var _executionEnvironment:ExecutionEnvironment;
    private var _notationalElementId:String;


    public function CreateActivationElementRequest() {
    }

    public function get sp():ServiceProvider {
        return _sp;
    }

    public function set sp(value:ServiceProvider):void {
        _sp = value;
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