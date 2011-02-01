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

package com.atricore.idbus.console.liveupdate.main.model
{
import com.atricore.idbus.console.services.dto.NotificationScheme;
import com.atricore.idbus.console.services.dto.ProfileType;
import com.atricore.idbus.console.services.dto.UpdateDescriptorType;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class LiveUpdateProxy extends IocProxy implements IDisposable
{
    private var _availableUpdatesList:ArrayCollection;

    private var _selectedUpdate:UpdateDescriptorType;

    private var _selectedProfile:ProfileType;

    private var _notificationScheme:NotificationScheme;

    public function LiveUpdateProxy()
    {
        super(NAME);
    }

    public function get availableUpdatesList():ArrayCollection {
        return _availableUpdatesList;
    }

    public function set availableUpdatesList(value:ArrayCollection):void {
        _availableUpdatesList = value;
    }

    public function dispose():void {
        _availableUpdatesList = null;

    }

    public function get selectedUpdate():UpdateDescriptorType {
        return _selectedUpdate;
    }

    public function set selectedUpdate(value:UpdateDescriptorType):void {
        _selectedUpdate = value;
    }

    public function get selectedProfile():ProfileType {
        return _selectedProfile;
    }

    public function set selectedProfile(value:ProfileType):void {
        _selectedProfile = value;
    }

    public function get notificationScheme():NotificationScheme {
        return _notificationScheme;
    }

    public function set notificationScheme(value:NotificationScheme):void {
        _notificationScheme = value;
    }
}
}