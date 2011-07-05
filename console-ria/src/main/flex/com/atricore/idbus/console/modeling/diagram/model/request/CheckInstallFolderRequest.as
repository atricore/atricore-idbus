package com.atricore.idbus.console.modeling.diagram.model.request {
public class CheckInstallFolderRequest {

    private var _homeDir:String;
    private var _environmentName:String;


    public function CheckInstallFolderRequest() {
    }

    public function get homeDir():String {
        return _homeDir;
    }

    public function set homeDir(value:String):void {
        _homeDir = value;
    }

    public function get environmentName():String {
        return _environmentName;
    }

    public function set environmentName(value:String):void {
        _environmentName = value;
    }
}
}