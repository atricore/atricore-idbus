package com.atricore.idbus.console.components {
import mx.containers.Canvas;

public class GraphCanvas extends Canvas {

    private var _drawGrid:Boolean;
    private var _isGridVisible:Boolean;

    private var _gridSquareSize:int = 30;
    private var _gridDotDistance:int = 6;
    private var _gridDotColor:uint = uint(0xCCCCCC);

    private var _lastWidth:Number;
    private var _lastHeight:Number;

    public function GraphCanvas() {
        super();
        _lastWidth = 0;
        _lastHeight = 0;
    }

    protected override function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if (unscaledWidth != _lastWidth || unscaledHeight != _lastHeight) {
            _lastWidth = unscaledWidth;
            _lastHeight = unscaledHeight;
            if (drawGrid) {
                drawGraphGrid(unscaledWidth, unscaledHeight);
            }
        }
        if (drawGrid && !_isGridVisible) {
            drawGraphGrid(unscaledWidth, unscaledHeight);
        }
    }

    public function drawGraphGrid(unscaledWidth:Number, unscaledHeight:Number):void {
        removeGraphGrid();
        
        graphics.lineStyle(1, _gridDotColor);
        graphics.beginFill(_gridDotColor);

        for (var y1:int=_gridSquareSize; y1<unscaledHeight; y1=y1+_gridSquareSize) {
            for (var x1:int=0; x1<unscaledWidth; x1=x1+_gridDotDistance) {
                graphics.drawRect(x1 - 0.5, y1 - 0.5, 0.7, 0.7);
                //graphics.drawCircle(x, y, 0.5);
            }
        }

        for (var x2:int=_gridSquareSize; x2<unscaledWidth; x2=x2+_gridSquareSize) {
            for (var y2:int=0; y2<unscaledHeight; y2=y2+_gridDotDistance) {
                graphics.drawRect(x2 - 0.5, y2 - 0.5, 0.7, 0.7);
                //graphics.drawCircle(x, y, 0.5);
            }
        }

        _isGridVisible = true;
    }

    public function redrawGraphGrid():void {
        drawGraphGrid(_lastWidth, _lastHeight);
    }

    public function removeGraphGrid():void {
        graphics.clear();
        _isGridVisible = false;
    }

    public function get drawGrid():Boolean {
        return _drawGrid;
    }

    public function set drawGrid(value:Boolean):void {
        _drawGrid = value;
    }
}
}