package com.atricore.idbus.console.modeling.main.view.sso.event {
import com.atricore.idbus.console.modeling.diagram.model.request.CheckInstallFolderRequest;

import flash.events.Event;

public class SsoEvent extends Event {
    public static const VALIDATE_HOME_DIR:String = "validateHomeDir";
    public static const DIRECTORY_EXISTS:String = "directoryExists";
    public static const DIRECTORY_DOESNT_EXIST:String = "directoryDoesntExist";

    private var _cif:CheckInstallFolderRequest;

    public function SsoEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) {
        super(type, bubbles, cancelable);
    }

    public function get cif():CheckInstallFolderRequest {
        return _cif;
    }

    public function set cif(value:CheckInstallFolderRequest):void {
        _cif = value;
    }
}
}