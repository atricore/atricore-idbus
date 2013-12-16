/**
 * @author: sgonzalez@atriocore.com
 * @date: 12/9/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.IdentityAppliance;

public class CreateDbIdentityVaultElementRequest {
    private var _identityAppliance:IdentityAppliance;
    private var _notationalElementId:String;

    public function CreateDbIdentityVaultElementRequest(identityAppliance:IdentityAppliance, notationalElementId:String) {
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
