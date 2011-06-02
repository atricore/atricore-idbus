/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.windows {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.WindowsIntegratedAuthentication;

import flash.events.Event;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class WindowsIntegratedAuthnCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newDirectoryAuthnService:WindowsIntegratedAuthentication;

    public function WindowsIntegratedAuthnCreateMediator(name:String = null, viewComp:WindowsIntegratedAuthnCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleWindowsIntegratedAuthnSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleWindowsIntegratedAuthnSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.nodeName);

        view.domain.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.serviceClass.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.host.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.port.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
        view.serviceName.addEventListener(Event.CHANGE, handleWindowsIntegratedAuthnSPNAttributeChange);
    }

    private function resetForm():void {
        view.nodeName.text = "";
        view.description.text = "";

        view.protocol.selectedIndex = 0;
        view.domain.text = "myCompanyDomain";
        view.serviceClass.selectedIndex = 0;
        view.host.text = "";
        view.port.text = "8081";
        view.serviceName.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindForm():void {

        view.nodeName.text = "";
        view.description.text = "";
        view.protocol.selectedIndex = 0;
        view.domain.text = "myCompanyDomain";
        view.serviceClass.selectedIndex = 0;
        view.host.text = "";
        view.port.text = "8081";
        view.serviceName.text = "";


        FormUtility.clearValidationErrors(_validators);

    }

    override public function bindModel():void {
        var windowsIntegratedAuthn:WindowsIntegratedAuthentication = new WindowsIntegratedAuthentication();

        windowsIntegratedAuthn.name = view.nodeName.text;
        windowsIntegratedAuthn.description = view.description.text;

        windowsIntegratedAuthn.protocol = view.protocol.selectedItem.data;
        windowsIntegratedAuthn.domain = view.domain.text;
        windowsIntegratedAuthn.serviceClass = view.serviceClass.selectedItem.data;
        windowsIntegratedAuthn.host = view.host.text;
        windowsIntegratedAuthn.port = parseInt(view.port.text);
        windowsIntegratedAuthn.serviceName = view.serviceName.text;

        _newDirectoryAuthnService = windowsIntegratedAuthn;
    }

    private function handleWindowsIntegratedAuthnSPNAttributeChange(e:Event):void {
        var spn:String = "";
        spn = view.serviceClass.selectedItem.data + "/" + view.host.text + ":" + view.port.text;
        if (view.serviceName != null && view.serviceName.text != "") {
            spn += "/" + view.serviceName.text;
        }
        spn += "@" + view.domain.text;

        view.servicePrincipalName.text = spn;

    }

    private function handleWindowsIntegratedAuthnSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newDirectoryAuthnService);
            _projectProxy.currentIdentityApplianceElement = _newDirectoryAuthnService;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        } else {
            event.stopImmediatePropagation();
        }
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():WindowsIntegratedAuthnCreateForm {
        return viewComponent as WindowsIntegratedAuthnCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.hostValidator);
        _validators.push(view.portValidator);
        _validators.push(view.serviceNameValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

     override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
        bindForm();
    }
}
}