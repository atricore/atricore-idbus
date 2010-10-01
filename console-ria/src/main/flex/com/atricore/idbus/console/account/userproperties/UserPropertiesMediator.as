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
import com.atricore.idbus.console.account.propertysheet.user.UserGeneralSection;
import com.atricore.idbus.console.account.propertysheet.user.UserGroupsSection;
import com.atricore.idbus.console.account.propertysheet.user.UserPasswordSection;
import com.atricore.idbus.console.account.propertysheet.user.UserPreferencesSection;
import com.atricore.idbus.console.account.propertysheet.user.UserSecuritySection;
import com.atricore.idbus.console.components.CustomViewStack;
import com.atricore.idbus.console.main.ApplicationFacade;
import com.atricore.idbus.console.services.dto.User;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.events.FlexEvent;
import mx.formatters.DateFormatter;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

import org.puremvc.as3.interfaces.INotification;
import org.springextensions.actionscript.puremvc.patterns.mediator.IocMediator;

import spark.components.Group;
import spark.components.TabBar;
import spark.events.IndexChangeEvent;

public class UserPropertiesMediator extends IocMediator {

    public static const BUNDLE:String = "console";

    private var resMan:IResourceManager = ResourceManager.getInstance();

    private var _userPropertiesTabBar:TabBar;
    private var _userPropertiesSheetsViewStack:CustomViewStack;
    private var _userGeneralSection:UserGeneralSection;
    private var _userPreferencesSection:UserPreferencesSection;
    private var _userGroupsSection:UserGroupsSection;
    private var _userSecuritySection:UserSecuritySection;
    private var _userPasswordSection:UserPasswordSection;

    private var _currentUser:com.atricore.idbus.console.services.dto.User;

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
        _userPropertiesSheetsViewStack = view.userPropertiesSheetsViewStack;
        _userPropertiesTabBar = view.userPropertiesTabBar;

        _userPropertiesTabBar.selectedIndex = 0;
        _userPropertiesTabBar.addEventListener(IndexChangeEvent.CHANGE, stackChanged);
    }

    private function stackChanged(event:IndexChangeEvent):void {
        _userPropertiesSheetsViewStack.selectedIndex = _userPropertiesTabBar.selectedIndex;
    }
    override public function listNotificationInterests():Array {
        return [ ApplicationFacade.DISPLAY_USER_PROPERTIES
        ];
    }

    override public function handleNotification(notification:INotification):void {
        switch (notification.getName()) {
            case ApplicationFacade.DISPLAY_USER_PROPERTIES:
                _userPropertiesSheetsViewStack.removeAllChildren();
                enablePropertyTabs();
                _currentUser = notification.getBody() as com.atricore.idbus.console.services.dto.User;
                showUserGeneralPropertiresTab();
                showUserPreferencesPropertiresTab();
                showUserGroupsPropertiresTab();
                showUserSecurityPropertiresTab();
                showUserPasswordPropertiresTab();
                _userPropertiesTabBar.selectedIndex = 0;
                break;
        }
    }

    protected function showUserGeneralPropertiresTab():void {
        var userGeneralTab:spark.components.Group = new spark.components.Group();
        userGeneralTab.id = "userPropertiesSheetsGeneralSection";
        userGeneralTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.general');
        userGeneralTab.width = Number("100%");
        userGeneralTab.height = Number("100%");
        userGeneralTab.setStyle("borderStyle", "solid");

        _userGeneralSection = new UserGeneralSection();
        userGeneralTab.addElement(_userGeneralSection);
        _userPropertiesSheetsViewStack.addNewChild(userGeneralTab);

        _userGeneralSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleGeneralPropertyTabCreationComplete);
        userGeneralTab.addEventListener(MouseEvent.ROLL_OUT, handleGeneralPropertyTabRollOut);

    }

    private function handleGeneralPropertyTabCreationComplete(event:Event):void {
        _userGeneralSection.userUsername.text = formatFieldString(_currentUser.userName);
        _userGeneralSection.userFirstName.text = formatFieldString(_currentUser.firstName);
        _userGeneralSection.userLastname.text = formatFieldString(_currentUser.surename);
        _userGeneralSection.userFullname.text = formatFieldString(_currentUser.commonName);
        _userGeneralSection.userEmail.text = formatFieldString(_currentUser.email);
        _userGeneralSection.userTelephone.text = formatFieldString(_currentUser.telephoneNumber);
        _userGeneralSection.userFax.text = formatFieldString(_currentUser.facsimilTelephoneNumber);
    }

    private function handleGeneralPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function showUserPreferencesPropertiresTab():void {
        var userPreferencesTab:spark.components.Group = new spark.components.Group();
        userPreferencesTab.id = "userPropertiesSheetsPreferencesSection";
        userPreferencesTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.preferences');
        userPreferencesTab.width = Number("100%");
        userPreferencesTab.height = Number("100%");
        userPreferencesTab.setStyle("borderStyle", "solid");

        _userPreferencesSection = new UserPreferencesSection();
        userPreferencesTab.addElement(_userPreferencesSection);
        _userPropertiesSheetsViewStack.addNewChild(userPreferencesTab);

        _userPreferencesSection.addEventListener(FlexEvent.CREATION_COMPLETE, handlePreferencesPropertyTabCreationComplete);
        _userPreferencesSection.addEventListener(MouseEvent.ROLL_OUT, handlePreferencesPropertyTabRollOut);
    }

    private function handlePreferencesPropertyTabCreationComplete(event:Event):void {
        _userPreferencesSection.userLanguage.text = formatFieldString(_currentUser.language);
    }

    private function handlePreferencesPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function showUserGroupsPropertiresTab():void {
        var userGroupsTab:spark.components.Group = new spark.components.Group();
        userGroupsTab.id = "userPropertiesSheetsGroupsSection";
        userGroupsTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.groups');
        userGroupsTab.width = Number("100%");
        userGroupsTab.height = Number("100%");
        userGroupsTab.setStyle("borderStyle", "solid");

        _userGroupsSection = new UserGroupsSection();
        userGroupsTab.addElement(_userGroupsSection);
        _userPropertiesSheetsViewStack.addNewChild(userGroupsTab);

        _userGroupsSection.addEventListener(FlexEvent.CREATION_COMPLETE, handleGroupsPropertyTabCreationComplete);
        _userGroupsSection.addEventListener(MouseEvent.ROLL_OUT, handleGroupsPropertyTabRollOut);
    }

    private function handleGroupsPropertyTabCreationComplete(event:Event):void {
        _userGroupsSection.groupsSelectedList.dataProvider = _currentUser.groups;
    }

    private function handleGroupsPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function showUserSecurityPropertiresTab():void {
        var userSecurityTab:spark.components.Group = new spark.components.Group();
        userSecurityTab.id = "userPropertiesSheetsSecuritySection";
        userSecurityTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.security');
        userSecurityTab.width = Number("100%");
        userSecurityTab.height = Number("100%");
        userSecurityTab.setStyle("borderStyle", "solid");

        _userSecuritySection = new UserSecuritySection();
        userSecurityTab.addElement(_userSecuritySection);
        _userPropertiesSheetsViewStack.addNewChild(userSecurityTab);

        _userSecuritySection.addEventListener(FlexEvent.CREATION_COMPLETE, handleSecurityPropertyTabCreationComplete);
        _userSecuritySection.addEventListener(MouseEvent.ROLL_OUT, handleSecurityPropertyTabRollOut);
    }

    private function handleSecurityPropertyTabCreationComplete(event:Event):void {
        _userSecuritySection.accountDisabledCheck.text = formatFieldBoolean(_currentUser.accountDisabled);
        _userSecuritySection.accountExpiresCheck.text = formatFieldBoolean(_currentUser.accountExpires);
        _userSecuritySection.accountExpiresDateItem.includeInLayout = _currentUser.accountExpires;
        _userSecuritySection.accountExpiresDateItem.visible = _currentUser.accountExpires;
        if (_currentUser.accountExpires) {
            _userSecuritySection.accountExpiresDate.text = formatFieldDate(_currentUser.accountExpirationDate);
        }

        _userSecuritySection.accountLimitLoginCheck.text = formatFieldBoolean(_currentUser.limitSimultaneousLogin);
        _userSecuritySection.accountMaxLimitLogiItem.includeInLayout = _currentUser.limitSimultaneousLogin;
        _userSecuritySection.accountMaxLimitLogiItem.visible = _currentUser.limitSimultaneousLogin;
        _userSecuritySection.accountSessionItem.includeInLayout = _currentUser.limitSimultaneousLogin;
        _userSecuritySection.accountSessionItem.visible = _currentUser.limitSimultaneousLogin;
        if (_currentUser.limitSimultaneousLogin) { // If limit login number is enabled
            _userSecuritySection.accountMaxLimitLogin.text = formatFieldNumber(_currentUser.maximunLogins);
            _userSecuritySection.terminatePrevSession.includeInLayout = _currentUser.terminatePreviousSession;
            _userSecuritySection.terminatePrevSession.visible = _currentUser.terminatePreviousSession;
            _userSecuritySection.preventNewSession.includeInLayout = _currentUser.preventNewSession;
            _userSecuritySection.preventNewSession.visible = _currentUser.preventNewSession;
        }
    }

    private function handleSecurityPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function showUserPasswordPropertiresTab():void {
        var userPasswordTab:spark.components.Group = new spark.components.Group();
        userPasswordTab.id = "userPropertiesSheetsPaswordSection";
        userPasswordTab.name = resMan.getString(AtricoreConsole.BUNDLE, 'provisioning.users.tab.label.password');
        userPasswordTab.width = Number("100%");
        userPasswordTab.height = Number("100%");
        userPasswordTab.setStyle("borderStyle", "solid");

        _userPasswordSection = new UserPasswordSection();
        userPasswordTab.addElement(_userPasswordSection);
        _userPropertiesSheetsViewStack.addNewChild(userPasswordTab);

        _userPasswordSection.addEventListener(FlexEvent.CREATION_COMPLETE, handlePasswordPropertyTabCreationComplete);
        _userPasswordSection.addEventListener(MouseEvent.ROLL_OUT, handlePasswordPropertyTabRollOut);
    }

    private function handlePasswordPropertyTabCreationComplete(event:Event):void {
        _userPasswordSection.allowPasswordChangeCheck.text = formatFieldBoolean(_currentUser.allowUserToChangePassword);
        _userPasswordSection.forcePasswordChangeCheck.text = formatFieldBoolean(_currentUser.forcePeriodicPasswordChanges);

        _userPasswordSection.forcePassChangeSection.includeInLayout = _currentUser.forcePeriodicPasswordChanges;
        _userPasswordSection.forcePassChangeSection.visible = _currentUser.forcePeriodicPasswordChanges;
        if (_currentUser.forcePeriodicPasswordChanges) { // If Force password change is enabled
            _userPasswordSection.forcePasswordChangeDays.text = formatFieldNumber(_currentUser.daysBetweenChanges);
            _userPasswordSection.expirationPasswordDate.text = formatFieldDate(_currentUser.passwordExpirationDate);
        }

        _userPasswordSection.notifyPasswordExpirationCheck.text = formatFieldBoolean(_currentUser.notifyPasswordExpiration);
        _userPasswordSection.notifyPasswordExpirationDayItem.includeInLayout = _currentUser.notifyPasswordExpiration;
        _userPasswordSection.notifyPasswordExpirationDayItem.visible = _currentUser.notifyPasswordExpiration;
        if (_currentUser.notifyPasswordExpiration)  // If password notication change is enabled
            _userPasswordSection.notifyPasswordExpirationDay.text = formatFieldNumber(_currentUser.daysBeforeExpiration);

        _userPasswordSection.generatePasswordCheck.text = formatFieldBoolean(_currentUser.automaticallyGeneratePassword);
        _userPasswordSection.emailNewPasswordCheck.text = formatFieldBoolean(_currentUser.emailNewPasword);
    }

    private function handlePasswordPropertyTabRollOut(e:Event):void {
        trace(e);
    }

    protected function enablePropertyTabs():void {
        _userPropertiesTabBar.visible = true;
        _userPropertiesSheetsViewStack.visible = true;
    }

    protected function clearPropertyTabs():void {
        _userPropertiesSheetsViewStack.removeAllChildren();
        _userPropertiesSheetsViewStack.visible = false;
        _userPropertiesTabBar.visible = false;
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