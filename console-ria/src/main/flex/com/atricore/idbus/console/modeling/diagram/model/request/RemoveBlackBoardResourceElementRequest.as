/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/26/13
 */
package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.BlackBoardResource;

public class RemoveBlackBoardResourceElementRequest {

    private var _resource:BlackBoardResource;

    public function RemoveBlackBoardResourceElementRequest(resource:BlackBoardResource) {
        _resource = resource;
    }


    public function get resource():BlackBoardResource {
        return _resource;
    }
}
}
