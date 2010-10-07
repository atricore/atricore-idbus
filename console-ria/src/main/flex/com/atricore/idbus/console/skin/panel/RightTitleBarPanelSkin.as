package com.atricore.idbus.console.skin.panel {
import flash.display.Graphics;

import spark.skins.spark.PanelSkin;

public class RightTitleBarPanelSkin extends PanelSkin {
    override public function RightTitleBarPanelSkin() {
        super();
        super.setStyle("borderStyle" , "none")

        titleDisplay.setStyle("textAlign", "right");
        titleDisplay.setStyle("fontSize", "10");
        titleDisplay.setStyle("fontWeight", "bold");
        titleDisplay.minHeight = 15;
    }

    override protected function updateDisplayList(w:Number, h:Number):void {
        if (getStyle("borderVisible") == true) {
            border.visible = true;
            background.left = background.top = background.right = background.bottom = 1;
        } else {
            border.visible = false;
            background.left = background.top = background.right = background.bottom = 0;
        }

        dropShadow.visible = getStyle("dropShadowVisible");
        borderStroke.color = getStyle("borderColor");
        borderStroke.alpha = getStyle("borderAlpha");
        backgroundFill.color = 0;
        backgroundFill.alpha = 0;

        super.updateDisplayList(unscaledWidth, unscaledHeight);

    }
}}
