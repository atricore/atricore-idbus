package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ServiceConnection;

public class RemoveServiceConnectionElementRequest {

    private var _serviceConnection:ServiceConnection;

    public function RemoveServiceConnectionElementRequest(serviceConnection:ServiceConnection) {
        _serviceConnection = serviceConnection;
    }

    public function get serviceConnection():ServiceConnection {
        return _serviceConnection;
    }

    public function set serviceConnection(value:ServiceConnection):void {
        _serviceConnection = value;
    }
}
}