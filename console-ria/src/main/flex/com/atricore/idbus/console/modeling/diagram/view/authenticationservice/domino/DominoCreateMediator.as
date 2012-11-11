/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/2/12
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.domino {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.Keystore;
import com.atricore.idbus.console.services.dto.Resource;
import com.atricore.idbus.console.services.dto.DominoAuthenticationService;

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

public class DominoCreateMediator extends IocFormMediator {

    private var resourceManager:IResourceManager = ResourceManager.getInstance();

    private var _projectProxy:ProjectProxy;
    private var _newDominoAuthnService:DominoAuthenticationService;

    public function DominoCreateMediator(name:String = null, viewComp:DominoCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleDominoSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);

        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        // upload bindings
        view.btnOk.addEventListener(MouseEvent.CLICK, handleDominoSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        resetForm();
        view.focusManager.setFocus(view.dominoName);
    }

    private function resetForm():void {
        view.dominoName.text = "domino";
        view.dominoDescription.text = "";
        view.serverUrl.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var dominoAuthnService:DominoAuthenticationService = new DominoAuthenticationService();

        dominoAuthnService.name = view.dominoName.text;
        dominoAuthnService.description = view.dominoDescription.text;

        dominoAuthnService.serverUrl = view.serverUrl.text;

        _newDominoAuthnService = dominoAuthnService;
    }

    private function handleDominoSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.authenticationServices.addItem(_newDominoAuthnService);
            _projectProxy.currentIdentityApplianceElement = _newDominoAuthnService;
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

    protected function get view():DominoCreateForm {
        return viewComponent as DominoCreateForm;
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