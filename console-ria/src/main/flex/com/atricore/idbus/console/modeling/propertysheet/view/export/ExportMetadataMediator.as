package com.atricore.idbus.console.modeling.propertysheet.view.export {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.controller.ExportMetadataCommand;
import com.atricore.idbus.console.services.spi.response.ExportMetadataResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportMetadataMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _exportedMetadata:Object;

    private var _applianceId:String;
    private var _providerName:String;
    private var _channelName:String;
    private var _override:Boolean;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();
    
    public function ExportMetadataMediator(name:String = null, viewComp:ExportMetadataView = null) {
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
    }

    private function init():void {
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);

        _exportedMetadata = null;

        if (_applianceId != null && _providerName != null) {

            view.progBar.setProgress(0, 0);
            view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata");            

            sendNotification(ApplicationFacade.METADATA_EXPORT, [_applianceId, _providerName, _channelName]);
        } else {
            closeWindow();
        }
    }

    private function handleSave(event:MouseEvent):void {
        if (_exportedMetadata != null && _exportedMetadata.length > 0) {
            var mdName:String = _providerName;
            if (_channelName != null && _override) {
                mdName = _channelName;
            }
            mdName = mdName.toLowerCase().replace(/\s+/g, "-") + "-samlr2-metadata.xml";
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.COMPLETE, saveCompleteHandler);
            _fileRef.save(_exportedMetadata, mdName);
        } else {
            closeWindow();
        }
    }

    private function saveCompleteHandler(event:Event):void {
        closeWindow();
    }

    override public function listNotificationInterests():Array {
        return [ExportMetadataCommand.SUCCESS,
                ExportMetadataCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.EXPORT_METADATA:
                var params:Array = notification.getBody() as Array;
                _applianceId = params[0];
                _providerName = params[1];
                _channelName = params[2];
                _override = params[3];
                init();
                break;
            case ExportMetadataCommand.SUCCESS:
                var resp:ExportMetadataResponse = notification.getBody() as ExportMetadataResponse;
                _exportedMetadata = resp.metadata;
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.btnSave.enabled = true;
                if (_exportedMetadata != null && _exportedMetadata.length > 0) {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata.success");
                } else {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata.error");
                    view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata.close");
                }
                break;
            case ExportMetadataCommand.FAILURE:
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata.error");
                view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.metadata.close");
                view.btnSave.enabled = true;
                break;
        }
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ExportMetadataView {
        return viewComponent as ExportMetadataView;
    }
}
}