package com.atricore.idbus.console.skin.panel {
    import spark.skins.spark.PanelSkin;

    public class NoTitleBarPanelSkin extends PanelSkin {
        override public function NoTitleBarPanelSkin() {
            super();
            titleDisplay.minHeight = NaN;
        }
    }
}