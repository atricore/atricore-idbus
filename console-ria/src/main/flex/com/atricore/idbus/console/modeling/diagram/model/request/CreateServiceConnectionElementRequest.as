package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

public class CreateServiceConnectionElementRequest {

    private var _sp:ServiceProvider;
    private var _resource:ServiceResource;

    public function CreateServiceConnectionElementRequest() {
    }

    public function get sp():ServiceProvider {
        return _sp;
    }

    public function set sp(value:ServiceProvider):void {
        _sp = value;
    }

    public function get resource():ServiceResource {
        return _resource;
    }

    public function set resource(value:ServiceResource):void {
        _resource = value;
    }
}
}