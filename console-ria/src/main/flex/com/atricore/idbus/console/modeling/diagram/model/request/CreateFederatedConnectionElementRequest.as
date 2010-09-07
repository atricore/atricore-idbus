package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.FederatedProvider;

public class CreateFederatedConnectionElementRequest {

    private var _roleA:FederatedProvider;
    private var _roleB:FederatedProvider;

    public function get roleA():FederatedProvider {
        return _roleA;
    }

    public function set roleA(value:FederatedProvider):void {
        _roleA = value;
    }

    public function get roleB():FederatedProvider {
        return _roleB;
    }

    public function set roleB(value:FederatedProvider):void {
        _roleB = value;
    }
}
}