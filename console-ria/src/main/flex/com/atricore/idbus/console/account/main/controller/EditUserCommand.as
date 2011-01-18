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

package com.atricore.idbus.console.account.main.controller
{
import com.atricore.idbus.console.account.main.model.AccountManagementProxy;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.main.service.ServiceRegistry;
import com.atricore.idbus.console.services.dto.User;
import com.atricore.idbus.console.services.spi.request.UpdateUserRequest;
import com.atricore.idbus.console.services.spi.response.UpdateUserResponse;

import mx.rpc.Fault;
import mx.rpc.IResponder;
import mx.rpc.events.FaultEvent;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.command.IocSimpleCommand;

public class EditUserCommand extends IocSimpleCommand implements IResponder {

    public static const SUCCESS:String = "EditUserCommand.SUCCESS";
    public static const FAILURE:String = "EditUserCommand.FAILURE";

    private var _registry:ServiceRegistry;
    private var _accountManagementProxy:AccountManagementProxy;
    private var user:User;


    public function EditUserCommand() {
    }

    public function get registry():ServiceRegistry {
        return _registry;
    }

    public function set registry(value:ServiceRegistry):void {
        _registry = value;
    }

    public function get accountManagementProxy():AccountManagementProxy {
        return _accountManagementProxy;
    }

    public function set accountManagementProxy(value:AccountManagementProxy):void {
        _accountManagementProxy = value;
    }

    override public function execute(notification:INotification):void {
        user = notification.getBody() as User;
        var req:UpdateUserRequest = new UpdateUserRequest();
        req.id = user.id;
        req.userName = user.userName;
        req.userPassword = user.userPassword;
        req.firstName = user.firstName;
        req.surename = user.surename;
        req.commonName = user.commonName;
        req.accountDisabled = user.accountDisabled;
        req.accountExpirationDate = user.accountExpirationDate;
        req.accountExpires = user.accountExpires;
        req.allowUserToChangePassword = user.allowUserToChangePassword;
        req.automaticallyGeneratePassword = user.automaticallyGeneratePassword;
        req.businessCategory = user.businessCategory;
        req.countryName = user.commonName;
        req.daysBeforeExpiration = user.daysBeforeExpiration;
        req.daysBetweenChanges = user.daysBetweenChanges;
        req.distinguishedName = user.distinguishedName;
        req.email = user.email;
        req.emailNewPasword = user.emailNewPasword;
        req.facsimilTelephoneNumber = user.facsimilTelephoneNumber;
        req.forcePeriodicPasswordChanges = user.forcePeriodicPasswordChanges;
        req.givenName = user.givenName;
        req.groups = user.groups;
        req.initials = user.initials;
        req.language = user.language;
        req.limitSimultaneousLogin = user.limitSimultaneousLogin;
        req.localityName = user.localityName;
        req.maximunLogins = user.maximunLogins;
        req.notifyPasswordExpiration = user.notifyPasswordExpiration;
        req.organizationName = user.organizationName;
        req.organizationUnitName = user.organizationUnitName;
        req.passwordExpirationDate = user.passwordExpirationDate;
        req.personalTitle = user.personalTitle;
        req.postalAddress = user.postalAddress;
        req.postalCode = user.postalCode;
        req.postOfficeBox = user.postOfficeBox;
        req.stateOrProvinceName = user.stateOrProvinceName;
        req.streetAddress = user.streetAddress;
        req.telephoneNumber = user.telephoneNumber;
        req.userCertificate = user.userCertificate;

        var service:RemoteObject = registry.getRemoteObjectService(ApplicationFacade.USER_PROVISIONING_SERVICE);
        var call:Object = service.updateUser(req);
        call.addResponder(this);
    }

    public function result(data:Object):void {
        var resp:UpdateUserResponse = data.result as UpdateUserResponse;
        accountManagementProxy.currentUser = user;
        sendNotification(SUCCESS);
    }

    public function fault(info:Object):void {
        var fault:Fault = (info as FaultEvent).fault;
        var msg:String = fault.faultString.substring((fault.faultString.indexOf('.') + 1), fault.faultString.length);
        trace(msg);
        sendNotification(FAILURE, msg);
    }


}
}