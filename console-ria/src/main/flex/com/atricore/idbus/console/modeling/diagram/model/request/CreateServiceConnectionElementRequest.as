package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.InternalSaml2ServiceProvider;
import com.atricore.idbus.console.services.dto.ServiceResource;

public class CreateServiceConnectionElementRequest {

    private var _sp:InternalSaml2ServiceProvider;
    private var _resource:ServiceResource;

    public function CreateServiceConnectionElementRequest() {
    }

    public function get sp():InternalSaml2ServiceProvider {
        return _sp;
    }

    public function set sp(value:InternalSaml2ServiceProvider):void {
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