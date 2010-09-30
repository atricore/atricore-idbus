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

package com.atricore.idbus.console.modeling.browser.model {
import mx.collections.ArrayCollection;

public class BrowserNode
{
    public function BrowserNode() {
        super();
    }

    public var id:Number;

    public var label:String;

    public var type:int;

    public var icon:Class;

    public var selectable:Boolean;

    public var children:ArrayCollection;

    public var data:Object;

    public var parentNode:BrowserNode;
    
    public function addChild(node:BrowserNode):void {
        if (this.children == null) {
            this.children = new ArrayCollection();
        }
        this.children.addItem(node);
    }

    public function removeChildren():void {
        this.children = null;
    }

    public function getChildAt(index:int):BrowserNode {
        if (this.children == null || this.children.length <= index) {
            return null;
        }
        return this.children.getItemAt(index) as BrowserNode;
    }

    public function childsLength():Number {
        if (this.children == null) {
            return 0;
        }
        return this.children.length;
    }
}

}