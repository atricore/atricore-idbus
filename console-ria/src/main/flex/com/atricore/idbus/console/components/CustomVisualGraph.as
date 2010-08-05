package com.atricore.idbus.console.components {
import com.atricore.idbus.console.modeling.diagram.event.VNodesLinkedEvent;
import com.atricore.idbus.console.modeling.diagram.view.util.DiagramUtil;

import flash.events.MouseEvent;
import flash.geom.Point;

import mx.managers.CursorManager;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.graphLayout.visual.VisualGraph;

public class CustomVisualGraph extends VisualGraph {

    private var _connectionMode:Boolean;
    private var _connectionStartPoint:Point;
    private var _connectionSourceNode:IVisualNode;
    private var _connectionTargetNode:IVisualNode;
    private var _connectionDragInProgress:Boolean;

    [Embed(source="/images/cursorImages/cross.png")]
    public static var crossCursorSymbol:Class;

    public function CustomVisualGraph() {
        super();
    }

    override protected function dragBegin(event:MouseEvent):void {
        super.dragBegin(event);
        if (_dragComponent != null && _connectionMode) {
            _connectionDragInProgress = true;
            _connectionSourceNode = data as IVisualNode;
            _connectionStartPoint = new Point(_canvas.contentMouseX, _canvas.contentMouseY);
        }
    }

    override protected function dragEnd(event:MouseEvent):void {
        super.dragEnd(event);
        if (_connectionMode) {
            connectionTargetNode = data as IVisualNode;
            if (_connectionSourceNode != null && _connectionTargetNode != null) {
                if (!nodeLinkExists(_connectionSourceNode.node, _connectionTargetNode.node) &&
                        DiagramUtil.nodesCanBeLinked(_connectionSourceNode, _connectionTargetNode)) {
                    // TODO: move linkNodes() call to DiagramMediator.nodesLinkedEventHandler() ?
                    linkNodes(_connectionSourceNode, _connectionTargetNode);
                    dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.VNODES_LINKED, _connectionSourceNode, _connectionTargetNode, true, false, 0));
                }
            }
            resetConnectionModeParameters();
        }
    }

    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if (_connectionMode && _connectionDragInProgress) {
            var lineColor:uint = uint(0xCCCCCC);  //grey
            var targetNode:IVisualNode = data as IVisualNode;
            if (targetNode != null) {
                if (!nodeLinkExists(_connectionSourceNode.node, targetNode.node) &&
                        DiagramUtil.nodesCanBeLinked(_connectionSourceNode, targetNode)) {
                    lineColor = uint(0x00CC00);  //green
                } else {
                    lineColor = uint(0xFF0000);  //red
                }
            }

            edgeDrawGraphics.lineStyle(_defaultEdgeStyle.thickness, lineColor);
            edgeDrawGraphics.beginFill(lineColor);
            edgeDrawGraphics.moveTo(_connectionStartPoint.x, _connectionStartPoint.y);
            edgeDrawGraphics.lineTo(_canvas.contentMouseX, _canvas.contentMouseY);
            edgeDrawGraphics.endFill();
        }
    }

    public function enterConnectionMode():void {
        _connectionMode = true;
        _moveNodeInDrag = false;
        _moveEdgeInDrag = false;
        _moveGraphInDrag = false;
        _canvas.parent.addEventListener(MouseEvent.CLICK, mouseClickHandler);
        _canvas.parent.addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        _canvas.parent.addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        _canvas.parent.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
    }

    public function exitConnectionMode():void {
        _connectionDragInProgress = false;
        _connectionMode = false;
        _connectionStartPoint = null;
        _connectionSourceNode = null;
        _connectionTargetNode = null;
        _dragComponent = null;
        _moveNodeInDrag = true;
        _moveEdgeInDrag = true;
        _moveGraphInDrag = true;
        _canvas.parent.removeEventListener(MouseEvent.CLICK, mouseClickHandler);
        _canvas.parent.removeEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        _canvas.parent.removeEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        _canvas.parent.removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
    }

    public function resetConnectionModeParameters():void {
        _connectionStartPoint = null;
        _connectionSourceNode = null;
        _connectionTargetNode = null;
        _connectionDragInProgress = false;
        _canvas.removeEventListener(MouseEvent.ROLL_OUT,dragEnd);
	    _canvas.removeEventListener(MouseEvent.MOUSE_UP,dragEnd);
        if (_dragComponent != null && _dragComponent.stage != null) {
            _dragComponent.stage.removeEventListener(MouseEvent.MOUSE_MOVE, handleDrag);
        }
        _dragComponent = null;
    }

    private function mouseClickHandler(event:MouseEvent):void {
        if (_connectionMode && !_connectionDragInProgress) {
            exitConnectionMode();
            CursorManager.removeAllCursors();
        }
    }

    private function mouseOverHandler(event:MouseEvent):void {
        if (_connectionMode) {
            CursorManager.setCursor(crossCursorSymbol);
        }
    }

    private function mouseOutHandler(event:MouseEvent):void {
        CursorManager.removeAllCursors();
    }

    private function mouseUpHandler(event:MouseEvent):void {
        _connectionDragInProgress = false;
        _dragComponent = null;
        refresh();
        event.updateAfterEvent();
    }

    private function nodeLinkExists(node1:INode, node2:INode):Boolean {
        if (node1 != null && node2 != null && node1.successors.indexOf(node2) != -1) {
            return true;
        }
        return false;
    }

    // Getters and Setters

    public function get connectionMode():Boolean {
        return _connectionMode;
    }

    public function set connectionMode(value:Boolean):void {
        _connectionMode = value;
    }

    public function get connectionStartPoint():Point {
        return _connectionStartPoint;
    }

    public function set connectionStartPoint(value:Point):void {
        _connectionStartPoint = value;
    }

    public function get connectionSourceNode():IVisualNode {
        return _connectionSourceNode;
    }

    public function set connectionSourceNode(value:IVisualNode):void {
        _connectionSourceNode = value;
    }

    public function get connectionTargetNode():IVisualNode {
        return _connectionTargetNode;
    }

    public function set connectionTargetNode(value:IVisualNode):void {
        _connectionTargetNode = value;
    }

    public function get connectionDragInProgress():Boolean {
        return _connectionDragInProgress;
    }

    public function set connectionDragInProgress(value:Boolean):void {
        _connectionDragInProgress = value;
    }
}
}