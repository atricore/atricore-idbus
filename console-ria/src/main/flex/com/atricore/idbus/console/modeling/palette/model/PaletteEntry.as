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

package com.atricore.idbus.console.modeling.palette.model {
import mx.controls.Image;

public class PaletteEntry {
    private var _label:String;
    private var _icon:Image;
    private var _shortDescription:String;
    private var _elementType:int;

    public function PaletteEntry(label:String, icon:Image, shortDescription:String, elementType:int) {
        _label = label;
        _icon = icon;
        _shortDescription = shortDescription;
        _elementType = elementType;
    }

    public function get label() : String {
        return _label;
    }

    public function get icon() : Image {
        return _icon;
    }

    public function get shortDescription() : String {
        return _shortDescription;
    }

    public function get elementType() : int {
        return _elementType;
    }

    public function toString():String {
        return "[Palette Entry: label=" + label + ", icon=" + icon  + ", shortDescription=" + shortDescription + "]";
    }
}
}