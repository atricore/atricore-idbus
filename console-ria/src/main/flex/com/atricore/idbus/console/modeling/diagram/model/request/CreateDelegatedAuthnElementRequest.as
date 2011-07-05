package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.AuthenticationService;
import com.atricore.idbus.console.services.dto.IdentityProvider;

public class CreateDelegatedAuthnElementRequest {

    private var _idp:IdentityProvider;
    private var _authnService:AuthenticationService;
    private var _notationalElementId:String;

    public function CreateDelegatedAuthnElementRequest() {
    }

    public function get idp():IdentityProvider {
        return _idp;
    }

    public function set idp(value:IdentityProvider):void {
        _idp = value;
    }

    public function get authnService():AuthenticationService {
        return _authnService;
    }

    public function set authnService(value:AuthenticationService):void {
        _authnService = value;
    }

    public function get notationalElementId():String {
        return _notationalElementId;
    }

    public function set notationalElementId(value:String):void {
        _notationalElementId = value;
    }
}
}