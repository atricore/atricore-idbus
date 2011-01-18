package com.atricore.idbus.console.modeling.diagram.model.request {
import mx.collections.ArrayCollection;

public class CheckFoldersRequest {

    private var _folders:ArrayCollection;
    private var _environmentName:String;


    public function CheckFoldersRequest() {
    }

    public function get folders():ArrayCollection {
        return _folders;
    }

    public function set folders(value:ArrayCollection):void {
        _folders = value;
    }

    public function get environmentName():String {
        return _environmentName;
    }

    public function set environmentName(value:String):void {
        _environmentName = value;
    }
}
}