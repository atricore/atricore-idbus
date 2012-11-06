/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/2/12
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.clientcert {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.diagram.view.authenticationservice.clientcert.ClientCertCreateForm;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.ClientCertAuthnService;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class ClientCertCreateMediator extends IocFormMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _projectProxy:ProjectProxy;
    private var _newClientCertAuthnService:ClientCertAuthnService;

    public function ClientCertCreateMediator(name:String = null, viewComp:ClientCertCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleClientCertSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        // upload bindings
        view.btnOk.addEventListener(MouseEvent.CLICK, handleClientCertSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        resetForm();
        view.focusManager.setFocus(view.clientCertName);
    }

    private function resetForm():void {
        view.clientCertName.text = "client-cert";
        view.clientCertDescription.text = "";
        view.clientCertCrlUrl.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var clientCertAuthnService:ClientCertAuthnService = new ClientCertAuthnService();

        clientCertAuthnService.name = view.clientCertName.text;
        clientCertAuthnService.description = view.clientCertDescription.text;

        clientCertAuthnService.crlUrl= view.clientCertCrlUrl.text;

        _newClientCertAuthnService = clientCertAuthnService;
    }

    private function handleClientCertSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newClientCertAuthnService);
            _projectProxy.currentIdentityApplianceElement = _newClientCertAuthnService;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        }
        else {
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

    protected function get view():ClientCertCreateForm {
        return viewComponent as ClientCertCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}