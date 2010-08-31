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

import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class ProjectProxy extends IocProxy
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
}
}