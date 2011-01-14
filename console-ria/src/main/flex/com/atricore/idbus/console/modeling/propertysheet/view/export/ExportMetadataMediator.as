package com.atricore.idbus.console.modeling.propertysheet.view.export {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.controller.ExportMetadataCommand;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.spi.response.ExportMetadataResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportMetadataMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _exportedMetadata:Object;

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

        init();
    }

    private function init():void {
        view.btnSave.addEventListener(MouseEvent.CLICK, handleSave);

        _exportedMetadata = null;

        var provider:Provider = projectProxy.currentIdentityApplianceElement as Provider;

        if (provider != null) {

            view.progBar.setProgress(0, 0);
            view.progBar.label = "Exporting SAML Metadata...";

            sendNotification(ApplicationFacade.METADATA_EXPORT);
        } else {
            closeWindow();
        }
    }

    private function handleSave(event:MouseEvent):void {
        _fileRef = new FileReference();
        _fileRef.addEventListener(Event.COMPLETE, saveCompleteHandler);
        _fileRef.save(_exportedMetadata, projectProxy.currentIdentityApplianceElement.name + "-samlr2-metadata.xml");
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
            case ExportMetadataCommand.SUCCESS:
                var resp:ExportMetadataResponse = notification.getBody() as ExportMetadataResponse;
                _exportedMetadata = resp.metadata;
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                if (_exportedMetadata != null && _exportedMetadata.length > 0) {
                    view.progBar.label = "Metadata successfully exported";
                    view.btnSave.enabled = true;
                } else {
                    view.progBar.label = "Error exporting metadata!!!";
                }
                break;
            case ExportMetadataCommand.FAILURE:
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.progBar.label = "Error exporting metadata!!!";
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