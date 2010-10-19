package com.atricore.idbus.console.modeling.main.view.export {
import com.atricore.idbus.console.main.model.ProjectProxy;

import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.MouseEvent;
import flash.events.SecurityErrorEvent;
import flash.external.ExternalInterface;
import flash.net.FileReference;
import flash.net.URLLoader;
import flash.net.URLLoaderDataFormat;
import flash.net.URLRequest;
import flash.net.URLRequestMethod;
import flash.net.URLVariables;

import mx.events.CloseEvent;

import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportIdentityApplianceMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _exportedAppliance:Object;

    private var _urlLoader:URLLoader;

    public function ExportIdentityApplianceMediator(name:String = null, viewComp:ExportIdentityApplianceView = null) {
        super(name, viewComp);
    }

    public function get projectProxy():ProjectProxy {
        return _projectProxy;
    }

    public function set projectProxy(value:ProjectProxy):void {
        _projectProxy = value;
    }

    override public function setViewComponent(viewComponent:Object):void {
        if (getViewComponent() != null) {
            view.btnSave.removeEventListener(MouseEvent.CLICK, handleSave);
            if (_fileRef != null) {
                _fileRef.removeEventListener(Event.COMPLETE, saveCompleteHandler);
            }
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);

        _exportedAppliance = null;
        
        if (projectProxy.currentIdentityAppliance != null) {

            view.progBar.setProgress(0, 0);
            view.progBar.label = "Exporting Identity Appliance...";

            var currentUrl:String = ExternalInterface.call("window.location.href.toString");
            var url:String = currentUrl.substring(0, currentUrl.lastIndexOf("/")) + "/export.do";

            _urlLoader = new URLLoader();
            _urlLoader.dataFormat = URLLoaderDataFormat.BINARY;
            
            _urlLoader.addEventListener(Event.COMPLETE, urlLoaderCompleteHandler);
            _urlLoader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);
            _urlLoader.addEventListener(IOErrorEvent.IO_ERROR, errorHandler);

            var request:URLRequest = new URLRequest(url);
            var variables:URLVariables = new URLVariables();
            variables.applianceId = projectProxy.currentIdentityAppliance.id;
            variables.name = projectProxy.currentIdentityAppliance.idApplianceDefinition.name + "-1.0." +
                    projectProxy.currentIdentityAppliance.idApplianceDefinition.revision;
            request.data = variables;
            request.method = URLRequestMethod.GET;
            _urlLoader.load(request);
        } else {
            closeWindow();
        }
    }

    private function handleSave(event:MouseEvent):void {
        _fileRef = new FileReference();
        _fileRef.addEventListener(Event.COMPLETE, saveCompleteHandler);
        _fileRef.save(_exportedAppliance, projectProxy.currentIdentityAppliance.idApplianceDefinition.name + "-1.0." +
                    projectProxy.currentIdentityAppliance.idApplianceDefinition.revision + ".zip");
    }

    private function saveCompleteHandler(event:Event):void {
        closeWindow();
    }

    private function urlLoaderCompleteHandler(event:Event):void {
        _exportedAppliance = _urlLoader.data;
        view.progBar.indeterminate = false;
        view.progBar.setProgress(100, 100);
        if (_exportedAppliance != null && _exportedAppliance.length > 0) {
            view.progBar.label = "Appliance successfully exported";
            view.btnSave.enabled = true;
        } else {
            view.progBar.label = "Error exporting Appliance!!!";
        }
    }

    public function errorHandler(event:ErrorEvent):void {
        view.progBar.indeterminate = false;
        view.progBar.setProgress(100, 100);
        view.progBar.label = "Error exporting Appliance!!!";
    }
    
    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ExportIdentityApplianceView {
        return viewComponent as ExportIdentityApplianceView;
    }
}
}