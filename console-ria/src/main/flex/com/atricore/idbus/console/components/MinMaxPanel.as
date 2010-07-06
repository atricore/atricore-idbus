/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
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

package com.atricore.idbus.console.components {

import flash.events.MouseEvent;

import mx.containers.Panel;
import mx.controls.Button;
import mx.effects.Resize;

public class MinMaxPanel extends Panel
    {
        private var minBtn:Button = new Button();
        private var maxBtn:Button = new Button();
        private var effResize:Resize = new Resize();
        private var previousHeight:int = 30;

        public function MinMaxPanel()
        {
            super();
            minBtn.addEventListener(MouseEvent.CLICK, minimisePanel);
            //maxBtn.addEventListener(MouseEvent.CLICK, maximisePanel);
        }

        override protected function createChildren():void{
            super.createChildren();
            super.titleBar.addChild(minBtn);
            //super.titleBar.addChild(maxBtn);
        }

        private function minimisePanel(e:MouseEvent):void{
            effResize.stop();
            minBtn.removeEventListener(MouseEvent.CLICK, minimisePanel);
            minBtn.addEventListener(MouseEvent.CLICK, maximisePanel);
            effResize.heightFrom = height;
            effResize.heightTo = previousHeight;
            previousHeight = height;
            effResize.play([this]);
        }

        private function maximisePanel(e:MouseEvent):void{
            effResize.stop();
            minBtn.removeEventListener(MouseEvent.CLICK, maximisePanel);
            minBtn.addEventListener(MouseEvent.CLICK, minimisePanel);
            effResize.heightFrom = height;
            effResize.heightTo = previousHeight;
            previousHeight = height;
            effResize.play([this]);
        }

        override protected function updateDisplayList(w:Number, h:Number):void{
            super.updateDisplayList(w,h);
            minBtn.x = super.titleBar.width - 30;
            minBtn.y = 5;
            minBtn.width = 20;
            minBtn.height = 20;
        }
    }
}