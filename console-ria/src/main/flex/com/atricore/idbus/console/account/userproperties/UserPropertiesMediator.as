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

package com.atricore.idbus.console.account.userproperties {
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.User;

import mx.formatters.DateFormatter;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

public class UserPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    public function UserPropertiesMediator(p_mediatorName:String = null, p_viewComponent:Object = null) {
        super(p_mediatorName, p_viewComponent);
    }

    override public function setViewComponent(p_viewComponent:Object):void {
        if (getViewComponent() != null) {

        }

        super.setViewComponent(p_viewComponent);
        init();
    }

    public function init():void {
    }

    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.DISPLAY_USER_PROPERTIES
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_USER_PROPERTIES:
                var usr:User = notification.getBody() as User;
                showPropertiresForUser(usr);
                break;
        }
    }

    private function showPropertiresForUser(user:User) {
        view.userUsername.text = formatFieldString(user.userName);
        view.userFirstName.text = formatFieldString(user.firstName);
        view.userLastname.text = formatFieldString(user.surename);
        view.userFullname.text = formatFieldString(user.commonName);
        view.userEmail.text = formatFieldString(user.email);
        view.userTelephone.text = formatFieldString(user.telephoneNumber);
        view.userFax.text = formatFieldString(user.facsimilTelephoneNumber);

        // Preference data
        view.userLanguage.text = formatFieldString(user.language);

        // Groups data
        view.groupsSelectedList.dataProvider = user.groups;

        // Security data
        view.accountDisabledCheck.text = formatFieldBoolean(user.accountDisabled);
        view.accountExpiresCheck.text = formatFieldBoolean(user.accountExpires);
        view.accountExpiresDateItem.includeInLayout = user.accountExpires;
        view.accountExpiresDateItem.visible = user.accountExpires;
        if (user.accountExpires) {
            view.accountExpiresDate.text = formatFieldDate(user.accountExpirationDate);
        }

        view.accountLimitLoginCheck.text = formatFieldBoolean(user.limitSimultaneousLogin);
        view.accountMaxLimitLogiItem.includeInLayout = user.limitSimultaneousLogin;
        view.accountMaxLimitLogiItem.visible = user.limitSimultaneousLogin;
        view.accountSessionItem.includeInLayout = user.limitSimultaneousLogin;
        view.accountSessionItem.visible = user.limitSimultaneousLogin;
        if (user.limitSimultaneousLogin) { // If limit login number is enabled
            view.accountMaxLimitLogin.text = formatFieldNumber(user.maximunLogins);
            view.terminatePrevSession.includeInLayout = user.terminatePreviousSession;
            view.terminatePrevSession.visible = user.terminatePreviousSession;
            view.preventNewSession.includeInLayout = user.preventNewSession;
            view.preventNewSession.visible = user.preventNewSession;
        }

        // Password data
        view.allowPasswordChangeCheck.text = formatFieldBoolean(user.allowUserToChangePassword);
        view.forcePasswordChangeCheck.text = formatFieldBoolean(user.forcePeriodicPasswordChanges);

        view.forcePassChangeSection.includeInLayout = user.forcePeriodicPasswordChanges;
        view.forcePassChangeSection.visible = user.forcePeriodicPasswordChanges;
        if (user.forcePeriodicPasswordChanges) { // If Force password change is enabled
            view.forcePasswordChangeDays.text = formatFieldNumber(user.daysBetweenChanges);
            view.expirationPasswordDate.text = formatFieldDate(user.passwordExpirationDate);
        }

        view.notifyPasswordExpirationCheck.text = formatFieldBoolean(user.notifyPasswordExpiration);
        view.notifyPasswordExpirationDayItem.includeInLayout = user.notifyPasswordExpiration;
        view.notifyPasswordExpirationDayItem.visible = user.notifyPasswordExpiration;
        if (user.notifyPasswordExpiration)  // If password notication change is enabled
            view.notifyPasswordExpirationDay.text = formatFieldNumber(user.daysBeforeExpiration);

        view.generatePasswordCheck.text = formatFieldBoolean(user.automaticallyGeneratePassword);
        view.emailNewPasswordCheck.text = formatFieldBoolean(user.emailNewPasword);
    }

    private function formatFieldString(str:String):String {
        if (str != null && str.length > 0)
            return str;
        else
            return "---";
    }

    private function formatFieldNumber(num:Number):String {
        return num.toString();
    }

    private function formatFieldDate(date:Date):String {
        var formatter:DateFormatter = new DateFormatter();
        var resMan:IResourceManager = ResourceManager.getInstance();
        formatter.formatString = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.DATE_FORMAT');
        return formatter.format(date);
    }

    private function formatFieldBoolean(bol:Boolean):String {
        var resMan:IResourceManager = ResourceManager.getInstance();
        if (bol)
            return resMan.getString(AtricoreConsole.BUNDLE, 'boolean.yes');
        else
            return resMan.getString(AtricoreConsole.BUNDLE, 'boolean.no');
    }

    protected function get view():UserPropertiesView
    {
        return viewComponent as UserPropertiesView;
    }


}
}