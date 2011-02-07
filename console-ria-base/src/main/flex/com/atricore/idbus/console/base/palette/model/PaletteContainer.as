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

package com.atricore.idbus.console.base.palette.model {
import mx.collections.ArrayList;

public class PaletteContainer extends PaletteEntry {
    private var _children:ArrayList;

//    public function PaletteContainer(label:String, icon:BitmapImage, description:String) {
//        super(label, icon, description, -1);
//
//        _children = new ArrayList();
//    }

    public function PaletteContainer(label:String, icon:Class, description:String) {
        super(label, icon, description, -1);

        _children = new ArrayList();
    }

    public function add(entry:PaletteEntry, ...args):void {
        if (args[0] is Number) {
            _children[args[0]] = entry;
        } else {
            _children.addItem(entry);
        }
    }

    public function get children():ArrayList {
        return _children;
    }
}
}