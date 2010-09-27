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
import com.atricore.idbus.console.services.dto.User;


import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class SecureContextProxy extends IocProxy implements IDisposable
{

    private var _currentUser : User;

    public function SecureContextProxy() {
        super(NAME, null);
    }

    public function logout() : void {
        _currentUser = null;
    }

    public function set currentUser(user : User) : void {
        _currentUser = user;
    }

    public function get currentUser() : User {
        return _currentUser;
    }

    public function dispose():void {
        _currentUser = null;
    }
}
}