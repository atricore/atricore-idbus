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
import com.atricore.idbus.console.services.dto.Binding;
import com.atricore.idbus.console.services.dto.IdentityProvider;
import com.atricore.idbus.console.services.dto.LdapIdentityVault;
import com.atricore.idbus.console.services.dto.Location;
import com.atricore.idbus.console.services.dto.Profile;
import com.atricore.idbus.console.services.dto.ServiceProviderChannel;

import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.events.CloseEvent;

import org.puremvc.as3.interfaces.INotification;

public class LdapIdentitySourceCreateMediator extends IocFormMediator {

    private var _projectProxy:ProjectProxy;

    //TODO - create LdapIdentitySource domain object. LdapIdentitySource is used just as a workaround
    private var _newLdapIdentitySource:LdapIdentityVault;

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
        view.ldapIdentitySourceName.text = "";
        view.ldapIdentitySourceDescription.text = "";
        view.ldapIdentitySourceProtocol.selectedIndex = 0;
        view.ldapIdentitySourceDomain.text = "";
        view.ldapIdentitySourcePort.text = "";
        view.ldapIdentitySourceBaseDN.text = "";
        view.ldapIdentitySourceAuthMechanismCombo.selectedIndex = 0;

        FormUtility.clearValidationErrors(_validators);
    }

    override public function bindModel():void {

        var ldapIdentitySource:LdapIdentityVault = new LdapIdentityVault();

        ldapIdentitySource.name = view.ldapIdentitySourceName.text;
        ldapIdentitySource.description = view.ldapIdentitySourceDescription.text;
//
//        var loc:Location = new Location();
//        loc.protocol = view.ldapIdentitySourceProtocol.labelDisplay.text;
//        loc.host = view.ldapIdentitySourceDomain.text;
//        loc.port = parseInt(view.ldapIdentitySourcePort.text);
//        ldapIdentitySource.location = loc;
//
//        ldapIdentitySource.baseDn = view.ldapIdentitySourceBaseDN.text;
//        ldapIdentitySource.authMechanism = view.ldapIdentitySourceAuthMechanismCombo.labelDisplay.text;
//
//
        _newLdapIdentitySource = ldapIdentitySource;
    }

    private function handleLdapIdentitySourceSave(event:MouseEvent):void {
        if (validate(true)) {
            bindModel();
            _projectProxy.currentIdentityAppliance.idApplianceDefinition.identityVaults.addItem(_newLdapIdentitySource);
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
        view.parent.dispatchEvent(new CloseEvent(CloseEvent.CLOSE));
    }

    protected function get view():LdapIdentitySourceCreateForm
    {
        return viewComponent as LdapIdentitySourceCreateForm;
    }


    override public function registerValidators():void {
        _validators.push(view.nameValidator);
        _validators.push(view.portValidator);
        _validators.push(view.domainValidator);
    }


    override public function listNotificationInterests():Array {
        return super.listNotificationInterests();
    }

    override public function handleNotification(notification:INotification):void {

        super.handleNotification(notification);


    }
}
}