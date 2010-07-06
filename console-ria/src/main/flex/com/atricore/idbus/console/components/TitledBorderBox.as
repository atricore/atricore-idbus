package com.atricore.idbus.console.components
{
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.events.Event;
import flash.events.TextEvent;
import flash.filters.DropShadowFilter;
import flash.geom.Rectangle;
import flash.text.TextLineMetrics;

import mx.containers.BoxDirection;
import mx.containers.utilityClasses.BoxLayout;
import mx.containers.utilityClasses.CanvasLayout;
import mx.containers.utilityClasses.ConstraintColumn;
import mx.containers.utilityClasses.ConstraintRow;
import mx.containers.utilityClasses.IConstraintLayout;
import mx.containers.utilityClasses.Layout;
import mx.core.Container;
import mx.core.ContainerLayout;
import mx.core.EdgeMetrics;
import mx.core.IFlexModuleFactory;
import mx.core.IFontContextComponent;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.UITextFormat;

/**
     *  Alpha of the title bar, control bar and sides of the Panel.
     *  The default value is 1.
     */
    [Style(name="borderAlpha", type="Number", inherit="no")]

    /**
     *  Number of pixels between the children when a horizontal layout is used.
     *  @default 8
     */
    [Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

    /**
     *  Number of pixels between the children when a vertical layout is used.
     *  @default 6
     */
    [Style(name="verticalGap", type="Number", format="Length", inherit="no")]

    /**
     *  Number of pixels between the container's left border and its content area.
     *  @default 5
     */
    [Style(name="paddingLeft", type="Number", format="Length", inherit="no")]

    /**
     *  Number of pixels between the container's top border and its content area.
     *  @default 20
     */
    [Style(name="paddingTop", type="Number", format="Length", inherit="no")]

    /**
     *  Number of pixels between the container's right border and its content area.
     *  @default 5
     */
    [Style(name="paddingRight", type="Number", format="Length", inherit="no")]

    /**
     *  Number of pixels between the container's lower border and its content area.
     *  @default 5
     */
    [Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

    /**
     *  Style declaration name for the text in the title border.
     *  The default value is <code>"windowStyles"</code>,
     *  which causes the title to have boldface text.
     *  @default "windowStyles"
     */
    [Style(name="titleStyleName", type="String", inherit="no")]

    [IconFile("TitledBorder.png")]


    /**
     * This container has a title TextField and draws a border around the container except
     * for where the title TextField is located.  A lot of this class is copied from the Panel class.
     * Like the Panel class it has a title and layout properties.
     *
     * <pre>
     *  &lt;ui:TitledBorderBox
      *   <strong>Properties</strong>
      *   layout="vertical|horizontal|absolute"
     *   title=""
     *   borderDropShadow="false"
     *   <strong>Styles</strong>
     *   backgroundAlpha="1"
     *   backgroundColor="NaN"
      *   borderAlpha="1"
      *   borderColor="#000000"
      *   borderThickness="1"
      *   cornerRadius="0"
      *   horizontalGap="8"
     *   paddingLeft="5"
     *   paddingTop="20"
     *   paddingRight="5"
     *   paddingTop="5"
     *   verticalGap="6"
     *   titleStyleName="windowStyles"
      * &gt;
      *      ...
      *      <i>child tags</i>
      *      ...
      *  &lt;/ui:TitledBorderBox&gt;
      *  </pre>
     *
     * @author Chris Callendar
     * @date April 1st, 2009
     */
    public class TitledBorderBox extends Container implements IConstraintLayout, IFontContextComponent {
        private var layoutObject:Layout;
        private var _title:String;
        private var titleTextField:IUITextField;
        private var titleChanged:Boolean;
        private var border:UIComponent;
        private var _borderDropShadow:Boolean;

        public function TitledBorderBox() {
            super();
            layoutObject = new BoxLayout();
            layoutObject.target = this;
            titleChanged = true;
            _borderDropShadow = false;
        }

        [Bindable("titleChanged")]
        [Inspectable(category="General")]
        public function get title():String {
            return _title;
        }

        public function set title(t:String):void {
            _title = (t != null ? t : "");
            if (titleTextField) {
                titleTextField.text = _title;
                titleTextField.toolTip = _title;
                titleTextField.invalidateSize();
                titleChanged = true;
                invalidateDisplayList();
            }
            dispatchEvent(new TextEvent("titleChanged", false, false, _title));
        }

        [Bindable("borderDropShadowChanged")]
        [Inspectable(category="General")]
        /** Adds a DropShadowFilter to the border. False by default. */
        public function get borderDropShadow():Boolean {
            return _borderDropShadow;
        }

        public function set borderDropShadow(dropShadow:Boolean):void {
            if (dropShadow != _borderDropShadow) {
                _borderDropShadow = dropShadow;
                if (border) {
                    border.filters = (dropShadow ? [ new DropShadowFilter(2, 45, 0x0, 0.4) ] : []);
                }
                dispatchEvent(new Event("borderDropShadowChanged"));
            }
        }

        override protected function createChildren():void {
            super.createChildren();
            createTitleTextField();
        }

        /**
         * Creates the title text field child and adds it as a child of this component.
         * @param childIndex The index of where to add the child.
         * If -1, the text field is appended to the end of the list.
         */
        protected function createTitleTextField(childIndex:int = -1):void {
            // Create the titleTextField as a child of the titleBar.
            if (!titleTextField) {
                titleTextField = IUITextField(createInFontContext(UITextField));
                titleTextField.selectable = false;
                if (childIndex == -1) {
                    rawChildren.addChild(DisplayObject(titleTextField));
                } else {
                    rawChildren.addChildAt(DisplayObject(titleTextField), childIndex);
                }
               titleTextField.styleName = getStyle("titleStyleName");
                titleTextField.text = title;
                titleTextField.enabled = enabled;
                titleTextField.x = 15;
                titleTextField.y = 1;
            }
        }

        override protected function createBorder():void {
            if (!border && isBorderNeeded()) {
                border = new UIComponent();
                border.filters = (borderDropShadow ? [ new DropShadowFilter(2, 45, 0x0, 0.4) ] : []);
                // add first to put below all child components
                rawChildren.addChildAt(border, 0);
            }
        }

        private function isBorderNeeded():Boolean {
            var bgAlpha:Number = getNumberStyle("backgroundAlpha", 1);
            var bgColor:Number = getStyle("backgroundColor");
            var bt:Number = getNumberStyle("borderThickness", 1);
            var ba:Number = getNumberStyle("borderAlpha", 1);
            return (!isNaN(bgColor) && (bgAlpha > 0)) || ((bt > 0) && (ba > 0));
        }

        /**
         * Returns an EdgeMetrics object that has four properties:
         * <code>left</code>, <code>top</code>, <code>right</code>,
         * and <code>bottom</code> ONLY if the layout is absolute.
         * Otherwise the padding is used.
         */
        override public function get borderMetrics():EdgeMetrics {
            if (border && (layout == ContainerLayout.ABSOLUTE)) {
                var l:Number = getNumberStyle("paddingLeft", 5);
                var t:Number = getNumberStyle("paddingTop", 20);
                var r:Number = getNumberStyle("paddingRight", 5);
                var b:Number = getNumberStyle("paddingBottom", 5);
                return new EdgeMetrics(l, t, r, b);
             }
            return EdgeMetrics.EMPTY;
        }

        override public function styleChanged(styleProp:String):void {
            var allStyles:Boolean = !styleProp || styleProp == "styleName";
            super.styleChanged(styleProp);
            if (allStyles || styleProp == getStyle(getStyle("titleStyleName"))) {
                if (titleTextField) {
                   titleTextField.styleName = getStyle("titleStyleName");
                    titleChanged = true;
                }
            }
        }

        override protected function measure():void {
            super.measure();
            layoutObject.measure();

            var measuredSize:Rectangle = measureTitleText();
            var paddingW:int = 38;
            measuredMinWidth = Math.max(measuredSize.width + paddingW, measuredMinWidth);
            measuredWidth = Math.max(measuredSize.width + paddingW, measuredWidth);
        }

        private function measureTitleText():Rectangle {
            var textWidth:Number = 20;
            var textHeight:Number = 14;
            if (titleTextField && titleTextField.text) {
                titleTextField.validateNow();
                var textFormat:UITextFormat = titleTextField.getUITextFormat();
                var metrics:TextLineMetrics = textFormat.measureText(titleTextField.text, false);
                textWidth = metrics.width;
                textHeight = metrics.height;
            }
            return new Rectangle(0, 0, Math.round(textWidth), Math.round(textHeight));
        }

        /**
         * Size the title textfield.
         */
        protected function sizeTitleTextField():void {
            if (titleChanged) {
                var padding:int = 38;
                var measuredW:Number = titleTextField.measuredWidth;
                var measuredH:Number = titleTextField.measuredHeight;
                var widthWithPadding:Number = measuredW + padding;
                if (!isNaN(explicitWidth)) {
                    // explicit width set - make the title textfield smaller if necessary
                    if (explicitWidth < widthWithPadding) {
                        measuredW = Math.max(0, explicitWidth - padding);
                    }
                }
                titleTextField.setActualSize(measuredW, measuredH);
                titleChanged = false;
            }

          }

        override protected function updateDisplayList(w:Number, h:Number):void {
            super.updateDisplayList(w, h);
            layoutObject.updateDisplayList(unscaledWidth, unscaledHeight);
            sizeTitleTextField();
            drawBorder(w, h);
        }

        protected function drawBorder(w:Number, h:Number):void {
            if (border) {
                var tfx:Number = titleTextField.x;
                var tfw:Number = titleTextField.width;
                var tfh:Number = titleTextField.height;
                var hasTitle:Boolean = (titleTextField.text.length > 0);

                var bgAlpha:Number = getNumberStyle("backgroundAlpha", 1);
                var bgColor:Number = getStyle("backgroundColor");
                var borderThickness:int = getNumberStyle("borderThickness", 1);
                var borderColor:uint = uint(getNumberStyle("borderColor", 0x0));
                var borderAlpha:Number = getNumberStyle("borderAlpha", 1);
                var cornerRadius:uint = uint(getNumberStyle("cornerRadius", 0));

                var spacing:int = 4;    // same as UITextField.TEXT_HEIGHT_PADDING, but it is mx_internal
                border.move(0, Math.round(tfh / 2));
                border.setActualSize(w, h - border.y);
                var borderW:Number = w - borderThickness;
                var borderH:Number = border.height - borderThickness;

                var g:Graphics = border.graphics;
                g.clear();

                // draw the background first
                if (!isNaN(bgColor) && (bgAlpha > 0) && (bgAlpha <= 1)) {
                    g.lineStyle(0, 0, 0, true);
                    g.beginFill(bgColor, bgAlpha);
                    if (cornerRadius > 0) {
                        g.drawRoundRect(0, 0, borderW, borderH, cornerRadius*2, cornerRadius*2);
                    } else {
                        g.drawRect(0, 0, borderW, borderH);
                    }
                    g.endFill();
                }

                // draw the border
                if ((borderThickness > 0) && (borderAlpha > 0) && (borderAlpha <= 1)) {
                    g.lineStyle(borderThickness, borderColor, borderAlpha, true);
                    if (hasTitle) {
                        g.moveTo(tfx - spacing, 0);
                        if ((cornerRadius == 0) || (borderH < cornerRadius) || (borderW < cornerRadius)) {
                            g.lineTo(0, 0);
                            g.lineTo(0, borderH);
                            g.lineTo(borderW, borderH);
                            g.lineTo(borderW, 0);
                            g.lineTo(tfx + tfw + spacing, 0);
                        } else {
                            g.lineTo(cornerRadius, 0);
                            g.curveTo(0, 0, 0, cornerRadius);
                            g.lineTo(0, borderH - cornerRadius);
                            g.curveTo(0, borderH, cornerRadius, borderH);
                            g.lineTo(borderW - cornerRadius, borderH);
                            g.curveTo(borderW, borderH, borderW, borderH - cornerRadius);
                            g.lineTo(borderW, cornerRadius);
                            g.curveTo(borderW, 0, borderW - cornerRadius, 0);
                            g.lineTo(tfx + tfw + spacing, 0);
                        }
                    } else {
                        if ((cornerRadius == 0) || (borderH < cornerRadius) || (borderW < cornerRadius)) {
                            g.drawRect(0, 0, borderW, borderH);
                        } else {
                            g.drawRoundRect(0, 0, borderW, borderH, cornerRadius*2, cornerRadius*2);
                        }
                    }
                }
            }
        }

        protected function getNumberStyle(styleName:String, defaultValue:Number):Number {
            var num:Number = getStyle(styleName);
            if (isNaN(num)) {
                num = defaultValue;
            }
            return num;
        }



        //----------------------------------
        //  layout - copied from Panel
        //----------------------------------

        private var _layout:String = ContainerLayout.VERTICAL;

        [Bindable("layoutChanged")]
        [Inspectable(category="General", enumeration="vertical,horizontal,absolute", defaultValue="vertical")]

        /**
         *  Specifies the layout mechanism used for this container.
         *  Panel containers can use <code>"vertical"</code>, <code>"horizontal"</code>,
         *  or <code>"absolute"</code> positioning.
         *  Vertical positioning lays out the child components vertically from
         *  the top of the container to the bottom in the specified order.
         *  Horizontal positioning lays out the child components horizontally
         *  from the left of the container to the right in the specified order.
         *  Absolute positioning does no automatic layout and requires you to
         *  explicitly define the location of each child component.
         *  @default "vertical"
         */
        public function get layout():String {
            return _layout;
        }

        /**
         *  @private
         */
        public function set layout(value:String):void {
            if (_layout != value) {
                _layout = value;
                if (layoutObject) {
                    layoutObject.target = null; // cleanup
                }
                if (_layout == ContainerLayout.ABSOLUTE) {
                    layoutObject = new CanvasLayout();
                } else {
                    layoutObject = new BoxLayout();
                    if (_layout == ContainerLayout.VERTICAL) {
                        BoxLayout(layoutObject).direction  = BoxDirection.VERTICAL;
                    } else {
                        BoxLayout(layoutObject).direction = BoxDirection.HORIZONTAL;
                    }
                }
                if (layoutObject) {
                    layoutObject.target = this;
                }
                invalidateSize();
                invalidateDisplayList();
                dispatchEvent(new Event("layoutChanged"));
            }
        }

        //-----------------------------------------
        //  constraintColumns - copied from Panel
        //-----------------------------------------

        [ArrayElementType("mx.containers.utilityClasses.ConstraintColumn")]
        [Inspectable(arrayType="mx.containers.utilityClasses.ConstraintColumn")]

        /**
         *  @private
         *  Storage for the constraintColumns property.
         */
        private var _constraintColumns:Array = [];

        /**
         *  @copy mx.containers.utilityClasses.IConstraintLayout#constraintColumns
         */
        public function get constraintColumns():Array {
            return _constraintColumns;
        }

        /**
         *  @private
         */
        public function set constraintColumns(value:Array):void {
            if (value != _constraintColumns) {
                var n:int = value.length;
                for (var i:int = 0; i < n; i++) {
                    ConstraintColumn(value[i]).container = this;
                }
                _constraintColumns = value;
                invalidateSize();
                invalidateDisplayList();
            }
        }

        //--------------------------------------
        //  constraintRows - copied from Panel
        //--------------------------------------

        [ArrayElementType("mx.containers.utilityClasses.ConstraintRow")]
        [Inspectable(arrayType="mx.containers.utilityClasses.ConstraintRow")]

        /**
         *  @private
         *  Storage for the constraintRows property.
         */
        private var _constraintRows:Array = [];

        /**
         *  @copy mx.containers.utilityClasses.IConstraintLayout#constraintRows
         */
        public function get constraintRows():Array {
            return _constraintRows;
        }

        /**
         *  @private
         */
        public function set constraintRows(value:Array):void {
            if (value != _constraintRows) {
                var n:int = value.length;
                for (var i:int = 0; i < n; i++) {
                    ConstraintRow(value[i]).container = this;
                }
                _constraintRows = value;
                invalidateSize();
                invalidateDisplayList();
            }
        }

        //-----------------------------------
        //  fontContext - copied from Panel
        //-----------------------------------

        /**
         *  @inheritDoc
         */
        public function get fontContext():IFlexModuleFactory {
            return moduleFactory;
        }

        /**
         *  @private
         */
        public function set fontContext(moduleFactory:IFlexModuleFactory):void {
            this.moduleFactory = moduleFactory;
        }


    }
}