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

public class PaletteDrawer extends PaletteContainer {

    public static const INITIAL_STATE_OPEN:int = 0;
    public static const INITIAL_STATE_CLOSED:int = 1;
    public static const INITIAL_STATE_PINNED_OPEN:int = 2;

    private var _initialState:int;
    private var _showDefaultIcon:Boolean;

    public function PaletteDrawer(label:String, icon:Image, description:String) {
        super(label, icon, description);
    }

    public function get initialState() : int {
        return _initialState;
    }

    public function isInitiallyOpen():Boolean {

        return (initialState == INITIAL_STATE_OPEN || initialState == INITIAL_STATE_PINNED_OPEN);
    }

    public function isInitiallyPinned() : Boolean {
        return initialState == INITIAL_STATE_PINNED_OPEN;
    }

    public function get showDefaultIcon() : Boolean  {
        return _showDefaultIcon;
    }

    public function set showDefaultIcon(showDefaultIcon: Boolean) {
        _showDefaultIcon = showDefaultIcon;
    }
}
}