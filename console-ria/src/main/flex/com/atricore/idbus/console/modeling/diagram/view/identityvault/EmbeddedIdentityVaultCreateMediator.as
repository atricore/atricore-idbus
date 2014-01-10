package com.atricore.idbus.console.modeling.diagram.view.identityvault {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.main.controller.EmbeddedIdentityVaultsListCommand;
import com.atricore.idbus.console.modeling.main.controller.JDBCDriversListCommand;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.EmbeddedIdentityVault;

import flash.events.Event;

import flash.events.MouseEvent;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class EmbeddedIdentityVaultCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newIdentityVault:EmbeddedIdentityVault;

    [Bindable]
    public var _embeddedIdentityVaults:ArrayCollection;

    public function EmbeddedIdentityVaultCreateMediator(name:String = null, viewComp:EmbeddedIdentityVaultCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleIdentityVaultSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
            view.embeddedVault.removeEventListener(Event.CHANGE, handleVaultChange);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleIdentityVaultSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);

        BindingUtils.bindProperty(view.embeddedVault, "dataProvider", this, "_embeddedIdentityVaults");
        view.embeddedVault.addEventListener(Event.CHANGE, handleVaultChange);
        sendNotification(ApplicationFacade.LIST_EMBEDDED_IDVAUTLS);

        view.focusManager.setFocus(view.identityVaultName);
    }

    private function resetForm():void {
        view.identityVaultName.text = "";
        view.identityVaultDescription.text = "";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var embeddedIdVault:EmbeddedIdentityVault = new EmbeddedIdentityVault();

        embeddedIdVault.name = view.identityVaultName.text;
        embeddedIdVault.description = view.identityVaultDescription.text;
        if (view.embeddedVault.selectedItem != null)
            embeddedIdVault.identityConnectorName = view.embeddedVault.selectedItem.name;

        _newIdentityVault = embeddedIdVault;
    }

    private function handleIdentityVaultSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.addItem(_newIdentityVault);
            _projectProxy.currentIdentityApplianceElement = _newIdentityVault;
            sendNotification(ApplicationFacade.DIAGRAM_ELEMENT_CREATION_COMPLETE);
            sendNotification(ApplicationFacade.UPDATE_IDENTITY_APPLIANCE);
            sendNotification(ApplicationFacade.IDENTITY_APPLIANCE_CHANGED);
            closeWindow();
        }
        else {
            event.stopImmediatePropagation();
        }
    }

    private function handleVaultChange(event:Event):void {
        // view.em.text = view.embeddedVault.selectedItem.defaultUrl;
    }

    private function handleCancel(event:MouseEvent):void {
        closeWindow();
    }

    private function closeWindow():void {
        resetForm();
        sendNotification(PaletteMediator.DESELECT_PALETTE_ELEMENT);
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():EmbeddedIdentityVaultCreateForm {
        return viewComponent as EmbeddedIdentityVaultCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
    }


    override public function listNotificationInterests():Array {
        return [EmbeddedIdentityVaultsListCommand.SUCCESS,
            EmbeddedIdentityVaultsListCommand.FAILURE];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case EmbeddedIdentityVaultsListCommand.SUCCESS:
                _embeddedIdentityVaults = projectProxy.embeddedIdentityVaults;
                break;
        }
    }
}
}