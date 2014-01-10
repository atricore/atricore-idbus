/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.IdentityAppliance;

public class CreateBlackBoardResourceElementRequest {

    private var _identityAppliance:IdentityAppliance;
    private var _notationalElementId:String;

    public function CreateBlackBoardResourceElementRequest(identityAppliance:IdentityAppliance, notationalElementId:String) {
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
