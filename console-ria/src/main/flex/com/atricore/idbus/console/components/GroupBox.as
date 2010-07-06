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
 
/*

Copyright (c) 2006 J.W.Opitz, All Rights Reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package com.atricore.idbus.console.components
{
import flash.events.Event;
import flash.geom.Point;

import mx.containers.Box;
import mx.core.UITextField;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

/**
	 * Sets the horizontal alignment of the title.  Values are "left, "center" and "right".
	 *
	 * @default left
	 */
	[Style(name="titleAlign", type="String", enumeration="left,center,right", inherit="no")]

	/**
	 * Sets the gap between the title and the drawn border endpoints on each side.
	 * @default 2
	 */
	[Style(name="titleGap", type="Number", inherit="no")]

	/**
	 * @private
	 *
	 * Sets the vertical placement of the title.  Current values are "top".  The default value is "top".
	 *
	 * 2007.04.12 - this feature has not yet been implemented - jwopitz
	 */
	[Style(name="titlePlacement", type="String", enumeration="top", inherit="no")]

	/**
	 * Sets the style for the title.
	 *
	 * @default bold
	 */
	[Style(name="titleStyleName", type="String", inherit="no")]

	/**
	 * The GroupBox is a container class that is modeled after the html fieldSet version.
	 * It can layout its children in either a vertical (default) or horizontal.
	 * <p>The <code>&lt;GroupBox&gt;</code> tag inherits all of the tag
	 * attributes of its superclass, and adds the following tag attributes:</p>
	 *
	 * <p>
	 * <pre>
	 * &lt;GroupBox
	 *	 <strong>Properties</strong>
	 * 	 title=""
	 * 	 &nbsp;
	 *   <strong>Styles</strong>
	 *   titleAlign="left|center|right"
	 *   titleGap="2"
	 *   titleStyleName="bold"
	 *	 &gt;
	 *   ...
	 *     <i>child tags</i>
	 *   ...
	 *  &lt;/GroupBox&gt;
	 *  </pre>
	 *  </p>
	 *
	 *  @see mx.containers.Box
	 */
	public class GroupBox extends Box
	{
		public function GroupBox() {
        	super();
    	}
		
		////////////////////////////////////////////////////////////////
		//	DEFAULT STYLES INIT
		////////////////////////////////////////////////////////////////

		/**
		 * @private
		 */
		private static var defaultStylesInitialized:Boolean = setDefaultStyles();

		/**
		 * @private
		 */
		private static function setDefaultStyles ():Boolean
		{
			var s:CSSStyleDeclaration = StyleManager.getStyleDeclaration('GroupBox');
			if (!s)
				s = new CSSStyleDeclaration();
			
			if (!s.getStyle("titleStyleName"))
			{
				var tsn:CSSStyleDeclaration = new CSSStyleDeclaration();
				tsn.setStyle("fontWeight", "bold");
				
				s.setStyle("titleStyleName", tsn);
			}
			
			if (!s.getStyle("borderStyle"))
				s.setStyle("borderStyle", "solid");
			
			if (!s.getStyle("borderSkin"))
				s.setStyle("borderSkin", GroupBoxBorder);
			
			if (!s.getStyle("backgroundColor"))
				s.setStyle("backgroundColor", 0xFFFFFF);
			
			if (!s.getStyle("backgroundAlpha"))
				s.setStyle("backgroundAlpha", 0.0);
			
			if (!s.getStyle("paddingLeft"))
				s.setStyle("paddingLeft", 2);
			
			if (!s.getStyle("paddingRight"))
				s.setStyle("paddingRight", 2);
			
			if (!s.getStyle("paddingTop"))
				s.setStyle("paddingTop", 2);
			
			if (!s.getStyle("paddingBottom"))
				s.setStyle("paddingBottom", 2);
			
			if (!s.getStyle("titleAlign"))
				s.setStyle("titleAlign", "left");
			
			if (!s.getStyle("titleGap"))
				s.setStyle("titleGap", 2);
			
			if (!s.getStyle("titlePlacement"))
				s.setStyle("titlePlacement", "top");

			StyleManager.setStyleDeclaration('GroupBox', s, true);

			return true;
		}

		/**
		 * @private
		 */
		protected var titleStyleNameChanged:Boolean = false;

		/**
		 * @private
		 */
		override public function styleChanged (styleProp:String):void
		{
			super.styleChanged(styleProp);

			var allStyles:Boolean = !styleProp || styleProp == "styleName";
			if (allStyles || styleProp == "titleAlign" || styleProp == "titleGap")
				titlePtChanged = true;

			if (allStyles || styleProp == "titleStyleName")
				titleStyleNameChanged = true;

			invalidateDisplayList();
		}

		/**
		 * @private
		 */
		override protected function createChildren ():void
		{
			super.createChildren();

			if (!textField)
			{
				textField = new UITextField();
				textField.mouseEnabled = false;
				textField.text = title;
				textField.styleName = getStyle("titleStyleName");

				rawChildren.addChild(textField);
			}
		}

		/**
		 * @private
		 */
		override protected function commitProperties ():void
		{
			super.commitProperties();

			if (titleTextChanged && textField)
			{
				textField.text = title;
				titleTextChanged = false;
			}
		}
		
		override protected function layoutChrome (unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.layoutChrome(unscaledWidth, unscaledHeight);
			
			if (titleStyleNameChanged)
			{
				textField.styleName = getStyle('titleStyleName');
				titleStyleNameChanged = false;
				titlePtChanged = true; //style may affect the position of the textField
			}

			if (titlePtChanged)
			{
				var pt:Point = getTitlePoint();
				var minX:Number = getStyle("cornerRadius") + getStyle("titleGap") + 5;
				var maxW:Number = getExplicitOrMeasuredWidth() - 2 * minX + 5;
				if (pt.x <= minX || textField.getExplicitOrMeasuredWidth() >= maxW)
				{
					pt.x = minX;

					textField.setActualSize(maxW, textField.getExplicitOrMeasuredHeight());
					textField.truncateToFit();
				}
				else
					textField.setActualSize(textField.getExplicitOrMeasuredWidth(), textField.getExplicitOrMeasuredHeight());

				textField.move(pt.x, pt.y);

				titlePtChanged = false;
			}
		}
		
		////////////////////////////////////////////////////////////////
		//	TITLE PT
		////////////////////////////////////////////////////////////////
		
		/**
		 * @private
		 */
		protected var titlePtChanged:Boolean = false;
		
		/**
		 * Calculates the targeted origin pt of the title textField based on titleAlignment.
		 *
		 * @return A point whose x and y coordinates are the location for the tilte textField.
		 */
		protected function getTitlePoint ():Point
		{
			var pt:Point = new Point();

			if (!textField)
				return pt;

			var nx:Number = 0;
					var ny:Number = 0;

			var ta:String = getStyle("titleAlign");
			var tg:Number = getStyle("titleGap");
			var cr:Number = getStyle("cornerRadius");

			switch (ta)
			{
				case "right":
				{
					nx = width - cr - borderMetrics.right - tg - textField.getExplicitOrMeasuredWidth() - 5;
					break;
				}

				case "center":
				{
					nx = (width - textField.getExplicitOrMeasuredWidth()) / 2;
					break;
				}

				case "left":
				default:
				{
					nx = cr + borderMetrics.left + tg + 5;
					break;
				}
			}

			if (pt.x != nx)
				pt.x = nx;

			return pt;
		}

		////////////////////////////////////////////////////////////////
		//	TITLE
		////////////////////////////////////////////////////////////////

		/**
		 * @private
		 */
		protected var titleText:String = "";

		/**
		 * @private
		 */
		protected var titleTextChanged:Boolean = false;

		/**
		 * The string value of the GroupBox's title.
		 */
		[Bindable("titleChanged")]
		public function get title ():String
		{
				return titleText;
		}
		/**
		 * @private
		 */
		public function set title (value:String):void
		{
			if (titleText != value)
			{
				titleText = value;
				titleTextChanged = true;
				titlePtChanged = true;

				invalidateProperties();
				invalidateDisplayList();

				dispatchEvent(new Event("titleChanged"));
			}
		}

		////////////////////////////////////////////////////////////////
		//	TITLE TEXT FIELD
		////////////////////////////////////////////////////////////////

		/**
		 * @private
		 */
		protected var textField:UITextField;

		/**
		 * @private
		 *
		 * Allows outside access to the GroupBox's textField.
		 */
		public function get titleTextField ():UITextField
		{
					return textField;
		}
	}
}