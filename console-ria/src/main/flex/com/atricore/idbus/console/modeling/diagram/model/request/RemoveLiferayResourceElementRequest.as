/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/5/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.LiferayResource;

public class RemoveLiferayResourceElementRequest {
    private var _resource:LiferayResource;

    public function RemoveLiferayResourceElementRequest(resource:LiferayResource) {
        _resource = resource;
    }

    public function get resource():LiferayResource {
        return _resource;
    }
}
}
