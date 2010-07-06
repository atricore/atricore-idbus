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
import flash.display.Graphics;
import flash.geom.Point;

import mx.core.EdgeMetrics;
import mx.core.UITextField;
import mx.skins.RectangularBorder;

/**
	 * @private
	 */
	public class GroupBoxBorder extends RectangularBorder
	{
		public function GroupBoxBorder() {
        	super();
    	}
    	
		////////////////////////////////////////////////////////////////
		//	BORDER METRICS
		////////////////////////////////////////////////////////////////

	    /**
	     * @private
	     */
	    protected var bm:EdgeMetrics;

	    /**
	     * @private
	     */
	    override public function get borderMetrics ():EdgeMetrics
	    {
	        if (!bm)
	        {
	        	var radius:Number = getStyle("cornerRadius");
	        	var borderTop:Number = getStyle("borderThickness");
	        	var borderThickness:Number = getStyle("borderThickness");

	        	if (parent && parent is GroupBox)
	        	{
	        		var textHeight:Number = GroupBox(parent).titleTextField.getExplicitOrMeasuredHeight();

	        		if (borderTop < textHeight || borderTop < radius)
	        			borderTop = Math.max(textHeight, radius);
	        	}

	            bm = new EdgeMetrics(borderThickness, borderTop, borderThickness, borderThickness);
	        }

	        return bm;
	    }

		////////////////////////////////////////////////////////////////
		//	TEXT FIELD
		////////////////////////////////////////////////////////////////

	    /**
	     * @private
	     *
	     * A reference to the GroupBox's title textField.
	     */
	    protected var textField:UITextField;

	    /**
	     * @private
	     */
	    protected function get title ():UITextField
	    {
	    	if (!textField)
	    	{
	    		if (parent && parent is GroupBox)
	    			textField = GroupBox(parent).titleTextField;
	    	}

	    	return textField;
	    }

		/**
		 * @private
		 */
		override protected function updateDisplayList (unscaledWidth:Number, unscaledHeight:Number):void
	    {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

			if (!title)
				return;
			
			var backgroundAlpha:Number = getStyle("backgroundAlpha");
			var backgroundColor:uint = getStyle("backgroundColor");
			var borderAlpha:Number = getStyle("borderAlpha");
			var borderColor:uint = getStyle("borderColor");
			var borderThickness:Number = getStyle("borderThickness");
			var radius:Number = getStyle("cornerRadius");
			var titleGap:Number = getStyle("titleGap");

			if (isNaN(titleGap))
				titleGap = 0;

			var top:Number = title.y + title.getExplicitOrMeasuredHeight() / 2;

			var startX:Number = Math.max(title.x - titleGap,
				titleGap + radius);
			var pt00:Point = new Point(startX, top);

			//upper left corner
			var pt01:Point = new Point(radius, top);
			var pt02:Point = new Point(0, top);
			var pt03:Point = new Point(0, top + radius);

			//lower left corner
			var pt04:Point = new Point(0, height - radius);
			var pt05:Point = new Point(0, height);
			var pt06:Point = new Point(radius, height);

			//lower right corner
			var pt07:Point = new Point(width - radius, height);
			var pt08:Point = new Point(width, height);
			var pt09:Point = new Point(width, height - radius);

			//upper right corner
			var pt10:Point = new Point(width, top + radius);
			var pt11:Point = new Point(width, top);
			var pt12:Point = new Point(width - radius, top);

			var endX:Number = Math.min(title.x + title.getExplicitOrMeasuredWidth() + titleGap,
				width - radius - titleGap);
			var pt13:Point = new Point(endX, top);

			var g:Graphics = graphics;
			g.clear();

			g.lineStyle(borderThickness, borderColor, borderAlpha, true);
			g.moveTo(pt00.x, pt00.y);
			g.beginFill(backgroundColor, backgroundAlpha);
			g.lineTo(pt01.x, pt01.y);
			g.curveTo(pt02.x, pt02.y, pt03.x, pt03.y);
			g.lineTo(pt04.x, pt04.y);
			g.curveTo(pt05.x, pt05.y, pt06.x, pt06.y);
			g.lineTo(pt07.x, pt07.y);
			g.curveTo(pt08.x, pt08.y, pt09.x, pt09.y);
			g.lineTo(pt10.x, pt10.y);
			g.curveTo(pt11.x, pt11.y, pt12.x, pt12.y);
			g.lineTo(pt13.x, pt13.y);
			g.lineStyle(0, 0, 0.0);
			g.lineTo(pt00.x, pt00.y);
			g.endFill();
	    }
	}
}