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

package com.atricore.idbus.console.modeling.diagram.event {

import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

public class VNodeRemoveEvent extends VGraphEvent{

        /**
         * This event type signals that a VNode has been
         * removed (e.g. its orientation)
         * */
        public static const VNODE_REMOVE:String = "vnodeRemove";

        private var _vnodeId:String;

        public function VNodeRemoveEvent(type:String, vnodeId:String, bubbles:Boolean=false, cancelable:Boolean=false, subtype:uint = VEST_DEFAULT) {
            super(type, bubbles, cancelable, subtype);
            _vnodeId = vnodeId;
        }

        public function get vnodeId():String {
            return _vnodeId;
        }
    }
}