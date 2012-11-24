package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.jbossepp {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.JBossEPPAuthenticationService;

import flash.events.MouseEvent;

import mx.events.CloseEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;

public class JBossEPPAuthenticationServiceCreateMediator extends IocFormMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _projectProxy:ProjectProxy;
    private var _newJBossEPPAuthenticationService:JBossEPPAuthenticationService;

    public function JBossEPPAuthenticationServiceCreateMediator(name:String = null, viewComp:JBossEPPAuthenticationServiceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleJBossEPPSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        // upload bindings
        view.btnOk.addEventListener(MouseEvent.CLICK, handleJBossEPPSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        resetForm();
        view.focusManager.setFocus(view.jbosseppName);
    }

    private function resetForm():void {
        view.jbosseppName.text = "";
        view.description.text = "";
        view.host.text = "";
        view.port.text = "";
        view.context.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var jbosseppAuthentication:JBossEPPAuthenticationService = new JBossEPPAuthenticationService();

        jbosseppAuthentication.name = view.jbosseppName.text;
        jbosseppAuthentication.description = view.description.text;
        jbosseppAuthentication.host = view.host.text;
        jbosseppAuthentication.port = view.port.text;
        jbosseppAuthentication.context = view.context.text;

        _newJBossEPPAuthenticationService = jbosseppAuthentication;
    }

    private function handleJBossEPPSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newJBossEPPAuthenticationService);
            _projectProxy.currentIdentityApplianceElement = _newJBossEPPAuthenticationService;
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

    protected function get view():JBossEPPAuthenticationServiceCreateForm {
        return viewComponent as JBossEPPAuthenticationServiceCreateForm;
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