package com.atricore.idbus.console.branding
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.utils.ByteArray;

	import mx.graphics.codec.PNGEncoder;

	public class SplashScreen
		extends Loader
	{
        //~ Settings ----------------------------------------------------------
        private static var _BarWidth     : int = 200;  // Progress bar width
        private static var _BarHeight    : int = 12;   // Progress bar height
        private static var _LogoHeight   : int = 88;   // Logo picture height
        private static var _LogoWidth    : int = 259;  // Logo picture width
        private static var _Padding      : int = 10;   // Spacing between logo and progress bar
        private static var _LeftMargin   : int = 5;    // Left Margin
        private static var _RightMargin  : int = 5;    // Right Margin
        private static var _TopMargin    : int = 1;    // Top Margin
        private static var _BottomMargin : int = 1;    // Bottom Margin

        private static var _BarBackground  : uint = 0xFFFFFF; // background of progress bar
        private static var _BarOuterBorder : uint = 0x737373; // color of outer border
        private static var _BarColor       : uint = 0x6F9FD5; // color of prog bar
        private static var _BarInnerColor  : uint = 0xFFFFFF; // inner color of prog bar

		//~ Instance Attributes -----------------------------------------------
		[Embed(source="/assets/icons/ui/a3c_logo.jpeg")]
        private var MyLogoClass: Class;
        private var _logo : Bitmap;
        private var _logoData : BitmapData;

        private var isReady  : Boolean = false;
        public  var progress : Number;

		//~ Constructor -------------------------------------------------------
        public function SplashScreen()
        {
        	super();
        	this.progress = 0;
        	this._logo = new MyLogoClass as Bitmap;
        }

        //~ Methods -----------------------------------------------------------
        public function refresh() : void
        {
        	this._logoData = this.draw();
        	var encoder : PNGEncoder = new PNGEncoder();
        	var bytes   : ByteArray  = encoder.encode(this._logoData);
        	this.loadBytes(bytes);
        }

        override public function get width() : Number
        {
        	return Math.max(_BarWidth, _LogoWidth) + _LeftMargin + _RightMargin;
        }

        override public function get height() : Number
        {
        	return _LogoHeight + _BarHeight + _Padding + _TopMargin + _BottomMargin;
        }

        private function draw() : BitmapData
        {
        	// create bitmap data to create the data
        	var data : BitmapData = new BitmapData(this.width, this.height, true, 0);

        	// draw the progress bar
        	var s : Sprite = new Sprite();
        	var g : Graphics = s.graphics;

        	// draw the bar background
        	g.beginFill(_BarBackground);
        	g.lineStyle(2, _BarOuterBorder, 1, true);
        	var px : int = (this.width - _BarWidth) / 2;
        	var py : int = _TopMargin + _LogoHeight + _Padding;
        	g.drawRoundRect(px, py, _BarWidth, _BarHeight, 2);
        	var containerWidth : Number = _BarWidth - 4;
        	var progWidth : Number = containerWidth * this.progress / 100;
        	g.beginFill(_BarColor);
        	g.lineStyle(1, _BarInnerColor, 1, true);
        	g.drawRect(px + 1, py + 1, progWidth, _BarHeight - 3);
        	data.draw(s);

        	// draw the logo
        	data.draw(this._logo.bitmapData, null, null, null, null, true);
        	return data;
        }

        public function set ready(value : Boolean) : void
        {
        	this.isReady = value;
        	this.visible = !this.isReady;
        }

        public function get ready() : Boolean { return this.isReady; }

	}
}