package com.atricore.idbus.console.branding
{
    import flash.display.DisplayObject;
    import flash.display.GradientType;
    import flash.display.Sprite;
    import flash.events.Event;
    import flash.events.ProgressEvent;
    import flash.events.TimerEvent;
    import flash.filters.DropShadowFilter;
    import flash.geom.Matrix;
    import flash.text.TextField;
    import flash.text.TextFormat;
    import flash.utils.Timer;

    import mx.events.FlexEvent;
    import mx.preloaders.IPreloaderDisplay;

    public class AtricoreConsolePreloader extends Sprite implements IPreloaderDisplay
    {

        [Embed(source="/assets/icons/ui/atricore_console_banner.png")]
        [Bindable] 
        public var LogoClass:Class;
        private var logo:DisplayObject;

        // Implementation variables, used to make everything work properly
        private var _IsInitComplete:Boolean = false;
        private var _timer:Timer;                 // we have a timer for animation
        private var _bytesLoaded:uint = 0;
        private var _bytesExpected:uint = 1;      // we start at 1 to avoid division by zero errors.
        private var _fractionLoaded:Number = 0;   // 0-1
        private var _preloader:Sprite;

        // drop shadow filters used on many sprites
        private var smallDropShadow:DropShadowFilter = new DropShadowFilter(2, 45, 0x000000,0.5)
        private var largeDropShadow:DropShadowFilter = new DropShadowFilter(6, 45, 0x333333, 0.9)

        // this is the border mainBox
        private var mainBox:Sprite;
        // the progress sprite
        private var bar:Sprite = new Sprite();
        // draws the border around the progress bar
        private var barFrame:Sprite;
        // the textfield for rendering the "Loading 0%" string
        private var loadingTextField:TextField;

        // the background color(s) - specify 1 or 2 colors
        private var bgColors:Array = [ 0xffffff, 0xffffff ];
        // the mainBox background gradient colors - specify 1 or 2 colors
        private var boxColors:Array = [ 0x4184d4, 0x14479a ];
        // the progress bar color - specify either 1, 2, or 4 colors
        private var barColors:Array = [ 0x95b7e7, 0x6e99d5, 0x1379fb, 0x2d9be8 ];  //0x0687d7;
        // the progress bar border color
        private var barBorderColor:uint = 0xdddddd;
        // the rounded corner radius for the progressbar
        private var barRadius:int = 0;
        // the width of the progressbar
        private var barWidth:int = 400;
        // the height of the progressbar
        private var barHeight:int = 30;
        // the loading text font
        private var textFont:String = "Tahoma"; // "Verdana";
        // the loading text color
        private var textColor:uint = 0xffffff;

        private var loading:String = "Loading ";

        private var minimumDisplayTime:int = 5000; // Wait 5 secs before closing splash screen

        public function AtricoreConsolePreloader() {
            super();
        }

        virtual public function initialize():void {
            _timer = new Timer(minimumDisplayTime);
            _timer.addEventListener(TimerEvent.TIMER, timerHandler);
            _timer.start();

            // clear here, rather than in draw(), to speed up the drawing
            clear();

            //creates all visual elements
            createAssets();
        }

        private function clear():void {
            // Draw background
            var bg:Sprite = new Sprite();
            if (bgColors.length == 2) {
                var matrix:Matrix =  new Matrix();
                matrix.createGradientBox(stageWidth, stageHeight, Math.PI/2);
                bg.graphics.beginGradientFill(GradientType.LINEAR, bgColors, [1, 1], [0, 255], matrix);
            } else {
                bg.graphics.beginFill(uint(bgColors[0]));
            }
            bg.graphics.drawRect(0, 0, stageWidth, stageHeight);
            bg.graphics.endFill();
            addChild(bg);
        }

        private function createAssets():void {
            // load the logo first so that we can get its dimensions
            logo = new LogoClass();
            var logoWidth:Number = logo.width;
            var logoHeight:Number = logo.height;

            // make the progress bar the same width as the logo if the logo is large
            barWidth = Math.max(barWidth, logoWidth) - 40;
            // calculate the box size & add some padding
            var boxWidth:Number = Math.max(logoWidth, barWidth);
            var boxHeight:Number = logoHeight + barHeight;

            // create and position the main box (all other sprites are added to it)
            mainBox = new Sprite();
            mainBox.x = stageWidth/2 - boxWidth/2;
            mainBox.y = stageHeight/2 - boxHeight/2;
            mainBox.filters = [ largeDropShadow ];
            if (boxColors.length == 2) {
                   var matrix:Matrix =  new Matrix();
                matrix.createGradientBox(boxWidth, boxHeight, Math.PI/2);
                mainBox.graphics.beginGradientFill(GradientType.LINEAR, boxColors, [1, 1], [0, 255], matrix);
             } else {
                 mainBox.graphics.beginFill(uint(boxColors[0]));
             }
            mainBox.graphics.drawRoundRectComplex(0, 0, boxWidth, boxHeight, 12, 0, 0, 12);
            mainBox.graphics.drawRoundRectComplex(0, 0, boxWidth, boxHeight, 12, 0, 0, 12);
            mainBox.graphics.endFill();
            addChild(mainBox);
            // position the logo
            logo.y = 10;
            logo.x = 10;
            mainBox.addChild(logo);

            //create progress bar
            bar = new Sprite();
            bar.graphics.drawRoundRect(0, 0, barWidth, barHeight, barRadius, barRadius);
            bar.x = logo.x + logoWidth/2 - barWidth/2;
            bar.y = logo.y + logoHeight - 70;
            mainBox.addChild(bar);

            //create progress bar frame
            barFrame = new Sprite();
            barFrame.graphics.lineStyle(1, barBorderColor, 1)
            barFrame.graphics.drawRoundRect(0, 0, barWidth, barHeight, barRadius, barRadius);
            barFrame.graphics.endFill();
            barFrame.x = bar.x;
            barFrame.y = bar.y;
            barFrame.filters = [ smallDropShadow ];
            mainBox.addChild(barFrame);

            //create text field to show percentage of loading, centered over the progress bar
            loadingTextField = new TextField()
            loadingTextField.width = barWidth;
            // setup the loading text font, color, and center alignment
            var tf:TextFormat = new TextFormat(textFont, null, textColor, true, null, null, null, null, "center");
            loadingTextField.defaultTextFormat = tf;
            // set the text AFTER the textformat has been set, otherwise the text sizes are wrong
            loadingTextField.text = loading + " 0%";
            // important - give the textfield a proper height
            loadingTextField.height = loadingTextField.textHeight + 8;
            loadingTextField.x = barFrame.x;
            // center the textfield vertically on the progress bar
            loadingTextField.y = barFrame.y + Math.round((barFrame.height - loadingTextField.height) / 2);
            mainBox.addChild(loadingTextField);
        }

        // This function is called whenever the state of the preloader changes.
        // Use the _fractionLoaded variable to draw your progress bar.
        virtual protected function draw():void {
            // update the % loaded string
            loadingTextField.text = loading + Math.round(_fractionLoaded * 100).toString() + "%";
            //loadingTextField.text = loading + _bytesExpected + "/" + _bytesLoaded + "%";

            // draw a complex gradient progress bar
            var matrix:Matrix =  new Matrix();
            matrix.createGradientBox(bar.width, bar.height, Math.PI/2);
            if (barColors.length == 2) {
                bar.graphics.beginGradientFill(GradientType.LINEAR, barColors, [1, 1], [0, 255], matrix);
            } else if (barColors.length == 4) {
                bar.graphics.beginGradientFill(GradientType.LINEAR, barColors, [1, 1, 1, 1], [0, 127, 128, 255], matrix);
            } else {
                bar.graphics.beginFill(uint(barColors[0]), 1);
            }
            bar.graphics.drawRoundRect(0, 0, bar.width * _fractionLoaded, bar.height, barRadius, barRadius);
            bar.graphics.endFill();
        }

        /**
         * The Preloader class passes in a reference to itself to the display class
         * so that it can listen for events from the preloader.
         * This code comes from DownloadProgressBar.  I have modified it to remove some unused event handlers.
         */
        virtual public function set preloader(value:Sprite):void {
            _preloader = value;

            value.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            value.addEventListener(Event.COMPLETE, completeHandler);
            value.addEventListener(FlexEvent.INIT_PROGRESS, initProgressHandler);
            value.addEventListener(FlexEvent.INIT_COMPLETE, initCompleteHandler);
        }

        virtual public function set backgroundAlpha(alpha:Number):void{}
        virtual public function get backgroundAlpha():Number { return 1; }

        protected var _backgroundColor:uint = 0xffffffff;
        virtual public function set backgroundColor(color:uint):void { _backgroundColor = color; }
        virtual public function get backgroundColor():uint { return _backgroundColor; }

        virtual public function set backgroundImage(image:Object):void {}
        virtual public function get backgroundImage():Object { return null; }

        virtual public function set backgroundSize(size:String):void {}
        virtual public function get backgroundSize():String { return "auto"; }

        protected var _stageHeight:Number = 300;
        virtual public function set stageHeight(height:Number):void { _stageHeight = height; }
        virtual public function get stageHeight():Number { return _stageHeight; }

        protected var _stageWidth:Number = 400;
        virtual public function set stageWidth(width:Number):void { _stageWidth = width; }
        virtual public function get stageWidth():Number { return _stageWidth; }

        //--------------------------------------------------------------------------
        //  Event handlers
        //--------------------------------------------------------------------------

        // Called from time to time as the download progresses.
        virtual protected function progressHandler(event:ProgressEvent):void {
            _bytesLoaded = event.bytesLoaded;
            _bytesExpected = event.bytesTotal;

            // some browsers return 0 as the bytesTotal
            if (_bytesExpected == 0) {
                _bytesExpected = _bytesLoaded;
            }

            _fractionLoaded = Number(_bytesLoaded) / Number(_bytesExpected);
            draw();
        }

        // Called when the download is complete, but initialization might not be done yet.  (I *think*)
        // Note that there are two phases- download, and init
        virtual protected function completeHandler(event:Event):void {
        }


        // Called from time to time as the initialization continues.
        virtual protected function initProgressHandler(event:Event):void {
            draw();
        }

        // Called when both download and initialization are complete
        virtual protected function initCompleteHandler(event:Event):void {
            _IsInitComplete = true;
        }

        // Called as often as possible
        virtual protected function timerHandler(event:Event):void {
            if (_IsInitComplete) {
                // We're done!
                _timer.stop();
                dispatchEvent(new Event(Event.COMPLETE));
            } else {
                draw();
            }
        }

    }

}