package com.atricore.idbus.console.modeling.propertysheet.view.export {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.modeling.main.controller.ExportProviderCertificateCommand;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.SamlR2ProviderConfig;
import com.atricore.idbus.console.services.spi.response.ExportProviderCertificateResponse;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileReference;

import mx.events.CloseEvent;

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class ExportProviderCertificateMediator extends IocMediator {

    private var _projectProxy:ProjectProxy;

    private var _fileRef:FileReference;

    private var _exportedCertificate:Object;
    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    public function ExportProviderCertificateMediator(name:String = null, viewComp:ExportProviderCertificateView = null) {
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

        _exportedCertificate = null;

        var provider:Provider = projectProxy.currentIdentityApplianceElement as Provider;

        if (provider != null) {

            view.progBar.setProgress(0, 0);
            view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert");

            sendNotification(ApplicationFacade.PROVIDER_CERTIFICATE_EXPORT, provider.config as SamlR2ProviderConfig);
        } else {
            closeWindow();
        }
    }

    private function handleSave(event:MouseEvent):void {
        if (_exportedCertificate != null && _exportedCertificate.length > 0) {
            _fileRef = new FileReference();
            _fileRef.addEventListener(Event.COMPLETE, saveCompleteHandler);
            _fileRef.save(_exportedCertificate, projectProxy.currentIdentityApplianceElement.name + ".pem");
        } else {
            closeWindow();
        }
    }

    private function saveCompleteHandler(event:Event):void {
        closeWindow();
    }

    override public function listNotificationInterests():Array {
        return [ExportProviderCertificateCommand.SUCCESS,
                ExportProviderCertificateCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ExportProviderCertificateCommand.SUCCESS:
                var resp:ExportProviderCertificateResponse = notification.getBody() as ExportProviderCertificateResponse;
                _exportedCertificate = resp.certificate;
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.btnSave.enabled = true;
                if (_exportedCertificate != null && _exportedCertificate.length > 0) {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert.success");
                } else {
                    view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert.error");
                    view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert.close");
                }
                break;
            case ExportProviderCertificateCommand.FAILURE:
                view.progBar.indeterminate = false;
                view.progBar.setProgress(100, 100);
                view.progBar.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert.error");
                view.btnSave.label = resourceManager.getString(AtricoreConsole.BUNDLE, "export.saml.prov.cert.close");
                view.btnSave.enabled = true;
                break;
        }
    }

    private function closeWindow():void {
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():ExportProviderCertificateView {
        return viewComponent as ExportProviderCertificateView;
    }
}
}