package com.atricore.idbus.console.modeling.diagram.model.request {
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;

public class ActivateExecutionEnvironmentRequest {

    private var _executionEnvironment:ExecutionEnvironment;
    private var _reactivate:Boolean;
    private var _replaceConfFiles:Boolean;
    private var _installSamples:Boolean;

    public function get executionEnvironment():ExecutionEnvironment {
        return _executionEnvironment;
    }

    public function set executionEnvironment(value:ExecutionEnvironment):void {
        _executionEnvironment = value;
    }

    public function get reactivate():Boolean {
        return _reactivate;
    }

    public function set reactivate(value:Boolean):void {
        _reactivate = value;
    }

    public function get replaceConfFiles():Boolean {
        return _replaceConfFiles;
    }

    public function set replaceConfFiles(value:Boolean):void {
        _replaceConfFiles = value;
    }

    public function get installSamples():Boolean {
        return _installSamples;
    }

    public function set installSamples(value:Boolean):void {
        _installSamples = value;
    }
}
}