package com.atricore.idbus.console.branding {
import flash.events.ProgressEvent;

import mx.preloaders.DownloadProgressBar;

public class AtricoreConsolePreloader extends DownloadProgressBar
{
    public function AtricoreConsolePreloader()
    {
        super();
        // Set the download label.
        downloadingLabel="Downloading Atricore Console..."
        // Set the initialization label.
        initializingLabel="Initializing Atricore Console..."
        // Set the minimum display time to 2 seconds.
        MINIMUM_DISPLAY_TIME=2000;
    }

    [Embed(source="/assets/icons/ui/a3c_logo.jpeg")]
    [Bindable]
    public var imgCls:Class;

    // Override to set a background image.
    override public function get backgroundImage():Object{
        return imgCls;
    }

    // Override initialize so that we can position the loader
    override public function initialize():void {
        super.initialize();
        center(stageWidth, (stageHeight > 500) ? 500 : stageHeight);
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

