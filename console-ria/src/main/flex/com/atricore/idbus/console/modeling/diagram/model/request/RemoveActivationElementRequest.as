package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.JOSSOActivation;

public class RemoveActivationElementRequest {

    private var _activation:JOSSOActivation;

    public function RemoveActivationElementRequest(activation:JOSSOActivation) {
        _activation = activation;
    }

    public function get activation():JOSSOActivation {
        return _activation;
    }

    public function set activation(value:JOSSOActivation):void {
        _activation = value;
    }
}
}