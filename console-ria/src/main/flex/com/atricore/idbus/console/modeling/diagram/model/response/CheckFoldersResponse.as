package com.atricore.idbus.console.modeling.diagram.model.response {
import mx.collections.ArrayCollection;

public class CheckFoldersResponse {

    private var _invalidFolders:ArrayCollection;
    private var _environmentName:String;


    public function CheckFoldersResponse() {
    }

    public function get invalidFolders():ArrayCollection {
        return _invalidFolders;
    }

    public function set invalidFolders(value:ArrayCollection):void {
        _invalidFolders = value;
    }

    public function get environmentName():String {
        return _environmentName;
    }

    public function set environmentName(value:String):void {
        _environmentName = value;
    }
}
}