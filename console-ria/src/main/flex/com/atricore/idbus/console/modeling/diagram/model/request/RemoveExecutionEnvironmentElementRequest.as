package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;

public class RemoveExecutionEnvironmentElementRequest {

    private var _executionEnvironment:ExecutionEnvironment;

    public function RemoveExecutionEnvironmentElementRequest(executionEnvironment:ExecutionEnvironment) {
        _executionEnvironment = executionEnvironment;
    }

    public function get executionEnvironment():ExecutionEnvironment {
        return _executionEnvironment;
    }
}
}