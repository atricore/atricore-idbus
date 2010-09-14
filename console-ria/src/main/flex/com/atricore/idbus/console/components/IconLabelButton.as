package com.atricore.idbus.console.components {
import spark.components.Button;

//icons
[Style(name="iconUp",type="Class")]
[Style(name="iconOver",type="Class")]
[Style(name="iconDown",type="Class")]
[Style(name="iconDisabled",type="Class")]
[Style(name="iconWidth",type="Number")]
[Style(name="iconHeight",type="Number")]

//paddings
[Style(name="paddingLeft",type="Number")]
[Style(name="paddingRight",type="Number")]
[Style(name="paddingTop",type="Number")]
[Style(name="paddingBottom",type="Number")]
public class IconLabelButton extends Button
{
    private var _selected:Boolean;

    private var _isLinkButton:Boolean;

    private var _iconWidth:Number;

    private var _iconHeight:Number;


    public function IconLabelButton() {
        super();
    }

    public function get selected():Boolean {
        return _selected;
    }

    public function set selected(value:Boolean):void {
        _selected = value;
    }

    public function get isLinkButton():Boolean {
        return _isLinkButton;
    }

    // toggle hand cursor
    public function set isLinkButton(value:Boolean):void {
        _isLinkButton = value;
        if (_isLinkButton) {
            buttonMode = true;
            useHandCursor = true;
        } else {
            buttonMode = false;
            useHandCursor = false;
        }
    }
}

}