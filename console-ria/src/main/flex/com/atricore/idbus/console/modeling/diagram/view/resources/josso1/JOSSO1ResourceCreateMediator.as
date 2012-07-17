package com.atricore.idbus.console.modeling.diagram.view.resources.josso1 {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.JOSSO1Resource;
import com.atricore.idbus.console.services.dto.Location;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class JOSSO1ResourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newResource:JOSSO1Resource;

    public function JOSSO1ResourceCreateMediator(name:String = null, viewComp:JOSSO1ResourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleResourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleResourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.resourceName);
    }

    private function resetForm():void {
        view.resourceName.text = "";
        view.resourceDescription.text = "";
        view.resourceProtocol.selectedIndex = 0;
        view.resourceDomain.text = "";
        view.resourcePort.text = "8080";
        view.resourceContext.text = "";
        view.resourcePath.text = "";
        view.resourcePartnerAppId.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var resource:JOSSO1Resource = new JOSSO1Resource();

        resource.name = view.resourceName.text;
        resource.description = view.resourceDescription.text;

        //location
        var loc:Location = new Location();
        loc.protocol = view.resourceProtocol.labelDisplay.text;
        loc.host = view.resourceDomain.text;
        loc.port = parseInt(view.resourcePort.text);
        loc.context = view.resourceContext.text;
        loc.uri = view.resourcePath.text;
        resource.partnerAppLocation = loc;

        resource.partnerAppId = view.resourcePartnerAppId.text;

        _newResource = resource;
    }

    private function handleResourceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.serviceResources.addItem(_newResource);
            _projectProxy.currentIdentityApplianceElement = _newResource;
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

    protected function get view():JOSSO1ResourceCreateForm {
        return viewComponent as JOSSO1ResourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.appIdValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
        _validators.push(view.contextValidator);
        _validators.push(view.pathValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}