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

package com.atricore.idbus.console.main.model
{
import com.atricore.idbus.console.services.dto.IdentityAppliance;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class ProjectProxy extends IocProxy implements IDisposable
{

    public static const ACTION_ITEM_CREATE : int = 1;
    public static const ACTION_ITEM_EDIT : int = 2;

    private var _identityApplianceList:ArrayCollection;
    private var _viewAction:int;
    private var _currentIdentityAppliance:IdentityAppliance;
    private var _currentIdentityApplianceElement:Object;
    private var _currentIdentityApplianceElementOwner:Object;
    private var _currentView:String;
    private var _commandResultIdentityAppliance:IdentityAppliance;
    private var _identityApplianceValidationErrors:ArrayCollection;
    private var _jdbcDrivers:ArrayCollection;
    private var _accountLinkagePolicies:ArrayCollection;
    private var _identityMappingPolicies:ArrayCollection;
    private var _userDashboardBrandings:ArrayCollection;
    private var _idpSelectors:ArrayCollection;
    private var _subjectNameIdentifierPolicies:ArrayCollection;
    private var _impersonateUserPolicies:ArrayCollection;

    public function ProjectProxy()
    {
        super(NAME);
    }

    public function get identityApplianceList():ArrayCollection {
        return _identityApplianceList;
    }

    public function set identityApplianceList(identityApplianceList:ArrayCollection):void {
        _identityApplianceList = identityApplianceList;
    }

    public function get currentIdentityAppliance():IdentityAppliance {
        return _currentIdentityAppliance;
    }

    public function set currentIdentityAppliance(currentIdentityApplianceDefinition:IdentityAppliance):void {
        _currentIdentityAppliance = currentIdentityApplianceDefinition;
    }

    public function get currentIdentityApplianceElement():Object {
        return _currentIdentityApplianceElement;
    }

    public function set currentIdentityApplianceElement(currentIdentityApplianceElement:Object):void {
        _currentIdentityApplianceElement = currentIdentityApplianceElement;
    }

    public function get currentIdentityApplianceElementOwner():Object {
        return _currentIdentityApplianceElementOwner;
    }

    public function set currentIdentityApplianceElementOwner(value:Object):void {
        _currentIdentityApplianceElementOwner = value;
    }

    public function get viewAction():int {
        return _viewAction;
    }

    public function set viewAction(viewAction:int):void {
        _viewAction = viewAction;
    }

    public function get currentView():String {
        return _currentView;
    }

    public function set currentView(currentView:String):void {
        _currentView = currentView;
    }

    public function get commandResultIdentityAppliance():IdentityAppliance {
        return _commandResultIdentityAppliance;
    }

    public function set commandResultIdentityAppliance(commandResultIdentityAppliance:IdentityAppliance):void {
        _commandResultIdentityAppliance = commandResultIdentityAppliance;
    }

    public function get identityApplianceValidationErrors():ArrayCollection {
        return _identityApplianceValidationErrors;
    }

    public function set identityApplianceValidationErrors(value:ArrayCollection):void {
        _identityApplianceValidationErrors = value;
    }

    public function get jdbcDrivers():ArrayCollection {
        return _jdbcDrivers;
    }

    public function set jdbcDrivers(value:ArrayCollection):void {
        _jdbcDrivers = value;
    }

    public function get accountLinkagePolicies():ArrayCollection {
        return _accountLinkagePolicies;
    }

    public function set accountLinkagePolicies(value:ArrayCollection):void {
        _accountLinkagePolicies = value;
    }

    public function get identityMappingPolicies():ArrayCollection {
        return _identityMappingPolicies;
    }

    public function set identityMappingPolicies(value:ArrayCollection):void {
        _identityMappingPolicies = value;
    }

    public function get userDashboardBrandings():ArrayCollection {
        return _userDashboardBrandings;
    }

    public function set userDashboardBrandings(value:ArrayCollection):void {
        _userDashboardBrandings = value;
    }

    public function get idpSelectors():ArrayCollection {
        return _idpSelectors;
    }

    public function set idpSelectors(value:ArrayCollection):void {
        _idpSelectors = value;
    }

    public function get subjectNameIdentifierPolicies():ArrayCollection {
        return _subjectNameIdentifierPolicies;
    }

    public function set subjectNameIdentifierPolicies(value:ArrayCollection):void {
        _subjectNameIdentifierPolicies = value;
    }

    public function get impersonateUserPolicies():ArrayCollection {
        return _impersonateUserPolicies;
    }

    public function set impersonateUserPolicies(value:ArrayCollection):void {
        _impersonateUserPolicies = value;
    }

    public function dispose():void {
        _viewAction = 0;
        _currentIdentityAppliance = null;
        _currentIdentityApplianceElement = null;
        _currentIdentityApplianceElementOwner = null;
        _currentView = "";
        _commandResultIdentityAppliance = null;
        _identityApplianceValidationErrors = null;
        _jdbcDrivers = null;
    }
}
}