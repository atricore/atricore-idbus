/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.DominoResource;
import com.atricore.idbus.console.services.dto.SelfServicesResource;

public class RemoveDominoResourceElementRequest {

    private var _resource:DominoResource;

    public function RemoveDominoResourceElementRequest(resource:DominoResource) {
        _resource = resource;
    }


    public function get resource():DominoResource {
        return _resource;
    }
}
}
