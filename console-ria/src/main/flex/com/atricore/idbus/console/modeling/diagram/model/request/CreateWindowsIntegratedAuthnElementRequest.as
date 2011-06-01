/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.IdentityAppliance;

public class CreateWindowsIntegratedAuthnElementRequest {
    private var _identityAppliance:IdentityAppliance;
    private var _notationalElementId:String;

    public function CreateWindowsIntegratedAuthnElementRequest(identityAppliance:IdentityAppliance, notationalElementId:String) {
        _identityAppliance = identityAppliance;
        _notationalElementId = notationalElementId;
    }

    public function get identityAppliance():IdentityAppliance {
        return _identityAppliance;
    }

    public function get notationalElementId():String {
        return _notationalElementId;
    }
}
}
