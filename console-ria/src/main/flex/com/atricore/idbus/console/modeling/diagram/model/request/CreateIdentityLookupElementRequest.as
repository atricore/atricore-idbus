package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentitySource;

public class CreateIdentityLookupElementRequest {

    private var _provider:FederatedProvider;
    private var _identitySource:IdentitySource;
    private var _notationalElementId:String;

    public function get provider():FederatedProvider {
        return _provider;
    }

    public function set provider(value:FederatedProvider):void {
        _provider = value;
    }

    public function get identitySource():IdentitySource {
        return _identitySource;
    }

    public function set identitySource(value:IdentitySource):void {
        _identitySource = value;
    }

    public function get notationalElementId():String {
        return _notationalElementId;
    }

    public function set notationalElementId(value:String):void {
        _notationalElementId = value;
    }
}
}