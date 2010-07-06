
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

package com.atricore.idbus.console.modeling.browser.renderers {
import mx.events.FlexEvent;

import com.atricore.idbus.console.components.*;

import flash.events.MouseEvent;

import mx.controls.Tree;
import mx.controls.treeClasses.*;

import com.atricore.idbus.console.main.EmbeddedIcons;
import com.atricore.idbus.console.modeling.browser.model.BrowserNode;

public class DefaultBrowserNodeRenderer extends TreeItemRenderer
    {

        // Define the constructor.
        public function DefaultBrowserNodeRenderer() {
            super();
            this.addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        }

        private function creationCompleteHandler(event:FlexEvent):void
        {
        }

        override public function set data(value:Object):void {
			super.data = value;

			var treeDataItem:TreeListData = TreeListData(listData);

            if (value != null) {
                var node:BrowserNode = value as BrowserNode;
                treeDataItem.icon = node.icon;
                if (node.childsLength() > 0 && node.icon == null) {
                    if (treeDataItem.open) {
                        treeDataItem.icon = EmbeddedIcons.folderOpenIcon;
                    } else {
                        treeDataItem.icon = EmbeddedIcons.folderClosedIcon;
                    }
                }

            }
		}

    }
}