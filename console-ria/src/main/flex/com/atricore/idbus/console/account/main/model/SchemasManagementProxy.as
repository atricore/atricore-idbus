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
    private var _currentSchemaAttribute:Attribute;

    private var _currentEntity:String="User";

    private var _attributesForEntity:ArrayCollection;

    public function SchemasManagementProxy() {
        super(NAME);
    }

    public function get attributesForEntity():ArrayCollection {
        return _attributesForEntity;
    }

    public function set attributesForEntity(value:ArrayCollection):void {
        _attributesForEntity = value;
    }

    public function get currentSchemaAttribute():Attribute {
        return _currentSchemaAttribute;
    }

    public function set currentSchemaAttribute(value:Attribute):void {
        _currentSchemaAttribute = value;
    }

    public function get currentEntity():String {
        return _currentEntity;
    }

    public function set currentEntity(value:String):void {
        _currentEntity = value;
    }

    public function  getAttributeByName(nameStr:String):Attribute {
        var retVal:Attribute = null;
        for each (var attr:Attribute in _attributesForEntity) {
            if (attr.name == nameStr) {
                retVal = attr;
                break;
            }
        }
        return retVal;
    }

    public function dispose():void {
        _attributesForEntity = null;
        _currentSchemaAttribute = null;
    }
}
}