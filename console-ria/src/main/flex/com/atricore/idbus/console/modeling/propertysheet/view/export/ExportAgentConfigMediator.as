package com.atricore.idbus.console.modeling.propertysheet.view.export {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.controller.ExportAgentConfigCommand;
import com.atricore.idbus.console.services.spi.response.ExportAgentConfigResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportAgentConfigMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _fileName:String;
    private var _exportedConfig:Object;

    private var _applianceId:String;
    private var _execEnvName:String;
    private var _platformId:String;

    private var resourceManager:IResourceManager = ResourceManager.getInstance();
    
    public function ExportAgentConfigMediator(name:String = null, viewComp:ExportAgentConfigView = null) {
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

        _exportedConfig = null;

        if (_applianceId != null && _execEnvName != null) {

            view.progBar.setProgress(0, 0);
            view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config");

            sendNotification(ApplicationFacade.AGENT_CONFIG_EXPORT, [_applianceId, _execEnvName]);
        } else {
            closeWindow();
        }
    }

    private function handleSave(event:MouseEvent):void {
        if (_exportedConfig != null && _exportedConfig.length > 0) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.COMPLETE, saveCompleteHandler);
            _fileRef.save(_exportedConfig, _fileName);
        } else {
            closeWindow();
        }
    }

    private function saveCompleteHandler(event:Event):void {
        closeWindow();
    }

    override public function listNotificationInterests():Array {
        return [ExportAgentConfigCommand.SUCCESS,
            ExportAgentConfigCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.EXPORT_AGENT_CONFIG:
                var params:Array = notification.getBody() as Array;
                _applianceId = params[0];
                _execEnvName = params[1];
                _platformId = params[2];
                init();
                break;
            case ExportAgentConfigCommand.SUCCESS:
                var resp:ExportAgentConfigResponse = notification.getBody() as ExportAgentConfigResponse;
                _fileName = resp.fileName;
                _exportedConfig = resp.agentConfig;
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.btnSave.enabled = true;
                if (_exportedConfig != null && _exportedConfig.length > 0) {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config.success");
                } else {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config.error");
                    view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config.close");
                }
                break;
            case ExportAgentConfigCommand.FAILURE:
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config.error");
                view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.agent.config.close");
                view.btnSave.enabled = true;
                break;
        }
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ExportAgentConfigView {
        return viewComponent as ExportAgentConfigView;
    }
}
}