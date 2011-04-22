package com.atricore.idbus.console.main {

public class DistributionContext {

    private var _distribution:String;

    public function DistributionContext() {
    }

    public function get distribution():String {
        return _distribution;
    }

    public function set distribution(value:String):void {
        _distribution = value;
    }
}
}