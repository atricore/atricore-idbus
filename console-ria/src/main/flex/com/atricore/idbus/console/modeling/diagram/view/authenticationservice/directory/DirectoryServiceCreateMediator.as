package com.atricore.idbus.console.modeling.diagram.view.authenticationservice.directory {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.DirectoryAuthenticationService;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class DirectoryServiceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;
    private var _newDirectoryAuthnService:DirectoryAuthenticationService;

    public function DirectoryServiceCreateMediator(name:String = null, viewComp:DirectoryServiceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleDirectoryServiceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleDirectoryServiceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
        view.focusManager.setFocus(view.directoryName);
    }

    private function resetForm():void {
        view.directoryName.text = "";
        view.description.text = "";
        view.initialContextFactory.text = "com.sun.jndi.ldap.LdapCtxFactory";
        view.providerUrl.text = "ldap://localhost:389";
        view.performDnSearch.selected = false;
        view.securityPrincipal.text = "uid=admin,ou=system";
        view.securityCredential.text = "";
        view.securityCredentialRetype.text = "";
        view.securityAuthentication.selectedIndex = 1;
        view.ldapSearchScope.selectedIndex = 2;
        view.usersCtxDN.text = "ou=People,dc=my-domain,dc=com";
        view.principalUidAttributeID.text = "uid";

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindForm():void {
        view.directoryName.text = "";
        view.description.text = "";
        view.initialContextFactory.text = "com.sun.jndi.ldap.LdapCtxFactory";
        view.providerUrl.text = "ldap://localhost:389";
        view.performDnSearch.selected = false;
        view.securityPrincipal.text = "uid=admin,ou=system";
        view.securityCredential.text = "";
        view.securityCredentialRetype.text = "";
        view.securityAuthentication.selectedIndex = 1;
        view.ldapSearchScope.selectedIndex = 2;
        view.usersCtxDN.text = "ou=People,dc=my-domain,dc=com";
        view.principalUidAttributeID.text = "uid";

        FormUtility.clearValidationErrors(_validators);

    }

    override public function bindModel():void {
        var directoryAuthnService:DirectoryAuthenticationService = new DirectoryAuthenticationService();

        directoryAuthnService.name = view.directoryName.text;
        directoryAuthnService.description = view.description.text;
        
        directoryAuthnService.initialContextFactory = view.initialContextFactory.text;
        directoryAuthnService.providerUrl = view.providerUrl.text;
        directoryAuthnService.performDnSearch = view.performDnSearch.selected;
        directoryAuthnService.securityPrincipal = view.securityPrincipal.text;
        directoryAuthnService.securityCredential = view.securityCredential.text;
        directoryAuthnService.usersCtxDN = view.usersCtxDN.text;
        directoryAuthnService.principalUidAttributeID = view.principalUidAttributeID.text;
        directoryAuthnService.ldapSearchScope = view.ldapSearchScope.selectedItem.data;
        directoryAuthnService.securityAuthentication = view.securityAuthentication.selectedItem.data;

        _newDirectoryAuthnService = directoryAuthnService;
    }

    private function handleDirectoryServiceSave(event:MouseEvent):void {
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

    protected function get view():DirectoryServiceCreateForm {
        return viewComponent as DirectoryServiceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.initialContextFactoryValidator);
        _validators.push(view.providerUrlValidator);
        _validators.push(view.securityPrincipalValidator);
        _validators.push(view.securityCredentialValidator);
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