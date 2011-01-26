package com.atricore.idbus.console.modeling.main.view.export {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.controller.ExportIdentityApplianceCommand;
import com.atricore.idbus.console.services.spi.response.ExportIdentityApplianceProjectResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportIdentityApplianceMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _exportedAppliance:Object;

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

            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_EXPORT, projectProxy.currentIdentityAppliance.id.toString());
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

    override public function listNotificationInterests():Array {
        return [ExportIdentityApplianceCommand.SUCCESS,
                ExportIdentityApplianceCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ExportIdentityApplianceCommand.SUCCESS:
                var resp:ExportIdentityApplianceProjectResponse = notification.getBody() as ExportIdentityApplianceProjectResponse;
                _exportedAppliance = resp.zip;
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                if (_exportedAppliance != null && _exportedAppliance.length > 0) {
                    view.progBar.label = "Appliance successfully exported";
                    view.btnSave.enabled = true;
                } else {
                    view.progBar.label = "Error exporting Appliance!!!";
                }
                break;
            case ExportIdentityApplianceCommand.FAILURE:
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.progBar.label = "Error exporting Appliance!!!";
                break;
        }
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ExportIdentityApplianceView {
        return viewComponent as ExportIdentityApplianceView;
    }
}
}