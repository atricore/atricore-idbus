package com.atricore.idbus.console.branding {
import flash.events.ProgressEvent;

import mx.preloaders.SparkDownloadProgressBar;

public class AtricoreConsolePreloader extends SparkDownloadProgressBar {
    public function AtricoreConsolePreloader() {
        super();
    }

    // Embed the background image.
    [Embed(source="/assets/icons/ui/a3c_logo.jpeg")]
    [Bindable]
    public var imgCls:Class;

    // Override to set a background image.
    override public function get backgroundImage():Object{
        return imgCls;
    }

    // Override to set the size of the background image to 100%.
    override public function get backgroundSize():String{
        return "12%";
    }

    // Override to return true so progress bar appears
    // during initialization.
    override protected function showDisplayForInit(elapsedTime:int,
        count:int):Boolean {
            return true;
    }

    // Override to return true so progress bar appears during download.
    override protected function showDisplayForDownloading(
        elapsedTime:int, event:ProgressEvent):Boolean {
            return true;
    }
}
}