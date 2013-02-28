/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.SelfServicesResource;

public class RemoveSelfServicesResourceElementRequest {

    private var _resource:SelfServicesResource;

    public function RemoveSelfServicesResourceElementRequest(resource:SelfServicesResource) {
        _resource = resource;
    }


    public function get resource():SelfServicesResource {
        return _resource;
    }
}
}
