package com.atricore.idbus.console.licensing.main.model {
import com.atricore.idbus.console.services.dto.LicenseType;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class LicenseProxy extends IocProxy implements IDisposable {

    private var _license:LicenseType;

    public function LicenseProxy() {
        super(NAME);
    }

    public function get license():LicenseType {
        return _license;
    }

    public function set license(value:LicenseType):void {
        _license = value;
    }

    public function dispose():void {
        license = null;
    }
}
}