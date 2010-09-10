package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.IdentityLookup;

public class RemoveIdentityLookupElementRequest {

    private var _identityLookup:IdentityLookup;

    public function RemoveIdentityLookupElementRequest(identityLookup:IdentityLookup) {
        _identityLookup = identityLookup;
    }

    public function get identityLookup():IdentityLookup {
        return _identityLookup;
    }

    public function set identityLookup(value:IdentityLookup):void {
        _identityLookup = value;
    }
}
}