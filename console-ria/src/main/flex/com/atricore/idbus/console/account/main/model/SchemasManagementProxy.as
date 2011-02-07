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

package com.atricore.idbus.console.account.main.model
{
import com.atricore.idbus.console.services.dto.schema.Attribute;

import mx.collections.ArrayCollection;

import org.osmf.traits.IDisposable;
import org.springextensions.actionscript.puremvc.patterns.proxy.IocProxy;

public class SchemasManagementProxy extends IocProxy implements IDisposable
{

    private var _schemaAttributeList:ArrayCollection;

    private var _currentSchemaAttribute:Attribute;

    public function SchemasManagementProxy() {
        super(NAME);
    }
    
    public function get schemaAttributeList():ArrayCollection {
        return _schemaAttributeList;
    }

    public function set schemaAttributeList(value:ArrayCollection):void {
        _schemaAttributeList = value;
    }

    public function get currentSchemaAttribute():Attribute {
        return _currentSchemaAttribute;
    }

    public function set currentSchemaAttribute(value:Attribute):void {
        _currentSchemaAttribute = value;
    }

    public function dispose():void {
        _schemaAttributeList = null;
        _currentSchemaAttribute = null;
    }
}
}