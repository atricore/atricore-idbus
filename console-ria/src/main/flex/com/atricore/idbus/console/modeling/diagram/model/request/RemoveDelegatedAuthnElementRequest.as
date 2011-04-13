package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.DelegatedAuthentication;

public class RemoveDelegatedAuthnElementRequest {

    private var _delegatedAuthentication:DelegatedAuthentication;

    public function RemoveDelegatedAuthnElementRequest(delegatedAuthentication:DelegatedAuthentication) {
        _delegatedAuthentication = delegatedAuthentication;
    }

    public function get delegatedAuthentication():DelegatedAuthentication {
        return _delegatedAuthentication;
    }

    public function set delegatedAuthentication(value:DelegatedAuthentication):void {
        _delegatedAuthentication = value;
    }
}
}