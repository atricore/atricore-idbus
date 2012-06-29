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

package com.atricore.idbus.console.modeling.palette.event
{
    import flash.events.Event;

    public class PaletteEvent extends Event
    {
        public static const CLICK:String = "PaletteEvent.CLICK";

        public static const ACTION_PALETTE_ITEM_CLICKED:int = 0;

        private var _uiComponent:Object;
        private var _data:Object;
        private var _action:int;

        public function PaletteEvent(bubbles:Boolean = false, cancelable:Boolean = false, uiComponent:Object = null,
                                     data:Object = null, action:int = ACTION_PALETTE_ITEM_CLICKED)
        {
            super(PaletteEvent.CLICK, bubbles, cancelable);
            this._uiComponent = uiComponent;
            this._data = data;
            this._action = action;
        }


        public function get uiComponent():Object {
            return this._uiComponent;
        }

        public function get data():Object
        {
            return this._data;
        }

        public function get action():int {
            return this._action;
        }


        override public function clone():Event
        {
            return new PaletteEvent(bubbles, cancelable, data, action);
        }

    }
}
