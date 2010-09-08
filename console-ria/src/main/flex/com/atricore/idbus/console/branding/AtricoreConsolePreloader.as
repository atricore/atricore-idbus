package com.atricore.idbus.console.branding {
import flash.display.Sprite;
import flash.events.Event;
import flash.events.ProgressEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.events.FlexEvent;
import mx.preloaders.DownloadProgressBar;

public class AtricoreConsolePreloader extends DownloadProgressBar {
    public  var loader : SplashScreen;
    private var _timer : Timer;

    public function AtricoreConsolePreloader() {
        super();
    }


    override public function initialize() : void
    {
        super.initialize();

        this.loader = new SplashScreen();
        this.addChild(this.loader);

        this._timer = new Timer(1);
        this._timer.addEventListener(TimerEvent.TIMER, handleTimerTick);
        this._timer.start();
    }

    override public function set preloader(preloader : Sprite):void
    {
        preloader.addEventListener(ProgressEvent.PROGRESS,  SWFDownLoadScreen);
        preloader.addEventListener(Event.COMPLETE,          SWFDownloadComplete);
        preloader.addEventListener(FlexEvent.INIT_PROGRESS, FlexInitProgress);
        preloader.addEventListener(FlexEvent.INIT_COMPLETE, FlexInitComplete);
    }

    private function SWFDownLoadScreen(event : ProgressEvent) : void
    {
        var prog : Number = event.bytesLoaded / event.bytesTotal * 100;
        if (this.loader)
        {
            this.loader.progress = prog;
        }
    }

    private function handleTimerTick(event : TimerEvent) : void
    {
        this.stage.addChild(this);
        this.loader.x = (this.stageWidth  - this.loader.width)  / 2;
        this.loader.y = (this.stageHeight - this.loader.height) / 2;
        this.loader.refresh();
    }

    private function SWFDownloadComplete(event : Event) : void {}

    private function FlexInitProgress(event : Event) : void {}

    private function FlexInitComplete(event : Event) : void
    {
        this.loader.ready = true;
        this._timer.stop();
        this.dispatchEvent(new Event(Event.COMPLETE));
    }

    override protected function showDisplayForInit(elapsedTime:int, count:int):Boolean
    {
        return true;
    }

    override protected function showDisplayForDownloading(elapsedTime:int,
                                              event:ProgressEvent):Boolean
    {
        return true;
    }
}
}

