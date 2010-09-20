/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.modeling.diagram.view.ldapidentitysource {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.model.ProjectProxy;
import com.atricore.idbus.console.main.view.form.FormUtility;
import com.atricore.idbus.console.main.view.form.IocFormMediator;
import com.atricore.idbus.console.modeling.palette.PaletteMediator;
import com.atricore.idbus.console.services.dto.LdapIdentitySource;

import flash.events.MouseEvent;

import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class LdapIdentitySourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    private var _newLdapIdentitySource:LdapIdentitySource;

    public function LdapIdentitySourceCreateMediator(name:String = null, viewComp:LdapIdentitySourceCreateForm = null) {
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
            view.btnOk.removeEventListener(MouseEvent.CLICK, handleLdapIdentitySourceSave);
            view.btnCancel.removeEventListener(MouseEvent.CLICK, handleCancel);
        }

        super.setViewComponent(viewComponent);

        init();
    }

    private function init():void {
        view.btnOk.addEventListener(MouseEvent.CLICK, handleLdapIdentitySourceSave);
        view.btnCancel.addEventListener(MouseEvent.CLICK, handleCancel);
    }

    private function resetForm():void {
        view.userRepositoryName.text = "";
        view.description.text = "";
        view.initialContextFactory.text = "";
        view.providerUrl.text = "";
        view.securityPrincipal.text = "";
        view.securityCredential.text = "";
        view.securityAuthentication.selectedIndex = 0;
        view.ldapSearchScope.selectedIndex = 0;
        view.usersCtxDN.text = "";
        view.principalUidAttributeID.text = "";
        view.roleMatchingMode.text = "";
        view.uidAttributeID.text = "";
        view.rolesCtxDN.text = "";
        view.roleAttributeID.text = "";
        view.credentialQueryString.text = "";
        view.updateableCredentialAttribute.text = "";
        view.userPropertiesQueryString.text = "";
        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {
        var ldapIdentitySource:LdapIdentitySource = new LdapIdentitySource();
        ldapIdentitySource.name = view.userRepositoryName.text;
        ldapIdentitySource.description = view.description.text;
        ldapIdentitySource.initialContextFactory = view.initialContextFactory.text;
        ldapIdentitySource.providerUrl = view.providerUrl.text;
        ldapIdentitySource.securityPrincipal = view.securityPrincipal.text;
        ldapIdentitySource.securityCredential = view.securityCredential.text;
        ldapIdentitySource.securityAuthentication = view.securityAuthentication.selectedItem.data;
        ldapIdentitySource.ldapSearchScope = view.ldapSearchScope.selectedItem.data;
        ldapIdentitySource.usersCtxDN = view.usersCtxDN.text;
        ldapIdentitySource.principalUidAttributeID = view.principalUidAttributeID.text;
        ldapIdentitySource.roleMatchingMode = view.roleMatchingMode.text;
        ldapIdentitySource.uidAttributeID = view.uidAttributeID.text;
        ldapIdentitySource.rolesCtxDN = view.rolesCtxDN.text;
        ldapIdentitySource.roleAttributeID = view.roleAttributeID.text;
        ldapIdentitySource.credentialQueryString = view.credentialQueryString.text;
        ldapIdentitySource.updateableCredentialAttribute = view.updateableCredentialAttribute.text;
        ldapIdentitySource.userPropertiesQueryString = view.userPropertiesQueryString.text;
        _newLdapIdentitySource = ldapIdentitySource;
    }

    private function handleLdapIdentitySourceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.identitySources.addItem(_newLdapIdentitySource);
            _projectProxy.currentIdentityApplianceElement = _newLdapIdentitySource;
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

    protected function get view():LdapIdentitySourceCreateForm
    {
        return viewComponent as LdapIdentitySourceCreateForm;
    }

    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.initialContextFactoryValidator);
        _validators.push(view.providerUrlValidator);
        _validators.push(view.securityPrincipalValidator);
        _validators.push(view.securityCredentialValidator);
        _validators.push(view.usersCtxDNValidator);
        _validators.push(view.principalUidAttributeIDValidator);
        _validators.push(view.roleMatchingModeValidator);
        _validators.push(view.uidAttributeIDValidator);
        _validators.push(view.rolesCtxDNValidator);
        _validators.push(view.roleAttributeIDValidator);
        _validators.push(view.credentialQueryStringValidator);
        _validators.push(view.updateableCredentialAttributeValidator);
        _validators.push(view.userPropertiesQueryStringValidator);
    }

    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {
        super.handleNotification(notification);
    }
}
}