package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.FederatedConnection;

public class RemoveFederatedConnectionElementRequest {

    private var _federatedConnection:FederatedConnection;

    public function RemoveFederatedConnectionElementRequest(federatedConnection:FederatedConnection) {
        _federatedConnection = federatedConnection;
    }

    public function get federatedConnection():FederatedConnection {
        return _federatedConnection;
    }

    public function set federatedConnection(value:FederatedConnection):void {
        _federatedConnection = value;
    }
}
}