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

package com.atricore.idbus.console.skin
{
import flash.display.DisplayObject;
import flash.display.Shape;

import mx.core.EdgeMetrics;
import mx.core.IChildList;
import mx.core.IContainer;
import mx.core.IRawChildrenContainer;
import mx.skins.halo.HaloBorder;

public class HaloBorderWithImagePositioning extends HaloBorder {
   override public function layoutBackgroundImage():void {
      super.layoutBackgroundImage();
      if (!hasBackgroundImage) {
         return;
      }

      var style:Object = getStyle("backgroundPosition");
      // the default alignment is center center
      if (!(style is Array) || (style[0] == 'center' && style[1] == 'center')) {
         return;
      }
      var posHorizontal:String = style[0];
      var posVertical:String = style[1];

      var p:DisplayObject = parent;
      var bm:EdgeMetrics;
      if (p is IContainer) {
         bm = IContainer(p).viewMetrics;
      }
      else {
         bm = borderMetrics;
      }
      var childrenList:IChildList = parent is IRawChildrenContainer ?
                                    IRawChildrenContainer(parent).rawChildren : IChildList(parent);

      var backgroundImage:DisplayObject = childrenList.getChildAt(1);

      // default position is center center, or middle,middle
      var bgX:int = backgroundImage.x;
      var bgY:int = backgroundImage.y;

      if (posHorizontal == 'left') {
         bgX = bm.left;
      }
      if (posHorizontal == 'right') {
         bgX = p.width - bm.right - backgroundImage.width;
      }

      if (posVertical == 'top') bgY = bm.top;
      if (posVertical == 'bottom') bgY = p.height - bm.bottom - backgroundImage.height;

      backgroundImage.x = bgX;
      backgroundImage.y = bgY;

      const backgroundMask:Shape = Shape(backgroundImage.mask);
      backgroundMask.x = bgX;
      backgroundMask.y = bgY;
   }

}
}
