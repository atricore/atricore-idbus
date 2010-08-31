package com.atricore.idbus.console.components {
import com.atricore.idbus.console.modeling.diagram.event.VEdgeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodesLinkedEvent;
import com.atricore.idbus.console.modeling.diagram.model.GraphDataManager;
import com.atricore.idbus.console.modeling.diagram.renderers.node.NodeDetailedRenderer;
import com.atricore.idbus.console.modeling.diagram.view.util.DiagramUtil;

import flash.display.DisplayObject;
import flash.events.MouseEvent;
import flash.geom.Point;

import mx.managers.CursorManager;

import org.un.cava.birdeye.ravis.enhancedGraphLayout.event.VGEdgeEvent;
import org.un.cava.birdeye.ravis.enhancedGraphLayout.visual.EnhancedVisualGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class CustomVisualGraph extends EnhancedVisualGraph {

    private static var FEDERATED_CONNECTION_MODE:uint = 1;
    private static var ACTIVATION_MODE:uint = 2;
    private static var IDENTITY_LOOKUP_MODE:uint = 3;

    private var _isConnectionMode:Boolean;
    private var _connectionMode:uint;
    private var _connectionStartPoint:Point;
    private var _connectionSourceNode:IVisualNode;
    private var _connectionTargetNode:IVisualNode;
    private var _connectionDragInProgress:Boolean;

    private var _selectedEdge:IEdge;
    private var _selectedEdgeId:int;
    
    [Embed(source="/images/cursorImages/cross.png")]
    public static var crossCursorSymbol:Class;

    public function CustomVisualGraph() {
        super();
        this.addEventListener(VGEdgeEvent.VG_EDGE_CLICK, edgeEventHandler);
    }

    override protected function dragBegin(event:MouseEvent):void {
        super.dragBegin(event);
        if (event.currentTarget is NodeDetailedRenderer && _isConnectionMode) {
            _connectionDragInProgress = true;
            _connectionSourceNode = data as IVisualNode;
            _connectionStartPoint = new Point(_canvas.contentMouseX, _canvas.contentMouseY);
            _canvas.addEventListener(MouseEvent.MOUSE_UP, dragEnd);
        }
    }

    override protected function dragEnd(event:MouseEvent):void {
        super.dragEnd(event);
        if (_isConnectionMode) {
            connectionTargetNode = data as IVisualNode;
            if (_connectionSourceNode != null && _connectionTargetNode != null) {
                if (!DiagramUtil.nodeLinkExists(_connectionSourceNode.node, _connectionTargetNode.node)
                        && canConnect(_connectionSourceNode, _connectionTargetNode)){
                    // TODO: move linkNodes() call to DiagramMediator.nodesLinkedEventHandler() ?
                    GraphDataManager.linkVNodes(this, _connectionSourceNode, _connectionTargetNode);
                    dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.VNODES_LINKED, _connectionSourceNode, _connectionTargetNode, true, false, 0));
                }
            }
            resetConnectionModeParameters();
            _canvas.removeEventListener(MouseEvent.MOUSE_UP, dragEnd);
        }
    }

    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if (_isConnectionMode && _connectionDragInProgress) {
            var lineColor:uint = uint(0xCCCCCC);  //grey
            var targetNode:IVisualNode = data as IVisualNode;
            if (targetNode != null && targetNode!= _connectionSourceNode) {
                if (!DiagramUtil.nodeLinkExists(_connectionSourceNode.node, targetNode.node) && canConnect(_connectionSourceNode, targetNode)) {
                    lineColor = uint(0x00CC00);  //green
                } else {
                    lineColor = uint(0xFF0000);  //red
                }
            }

            edgeDrawGraphics.lineStyle(2, lineColor);
            //edgeDrawGraphics.lineStyle(_defaultEdgeStyle.thickness, lineColor);
            edgeDrawGraphics.beginFill(lineColor);
            edgeDrawGraphics.moveTo(_connectionStartPoint.x, _connectionStartPoint.y);
            edgeDrawGraphics.lineTo(_canvas.contentMouseX, _canvas.contentMouseY);
            edgeDrawGraphics.endFill();
        }
    }

    private function enterConnectionMode():void {
        _isConnectionMode = true;
        _moveNodeInDrag = false;
        _moveEdgeInDrag = false;
        _moveGraphInDrag = false;
        (document as DisplayObject).addEventListener(MouseEvent.CLICK, mouseClickHandler);
        (document as DisplayObject).addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        (document as DisplayObject).addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        (document as DisplayObject).addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
        (document as DisplayObject).addEventListener(MouseEvent.ROLL_OUT, rollOutHandler);
        (document as DisplayObject).addEventListener(MouseEvent.MOUSE_MOVE, handleConnectionDrag);
    }

    public function enterFederatedConnectionMode():void {
        _connectionMode = FEDERATED_CONNECTION_MODE;
        enterConnectionMode();
    }

    public function enterActivationMode():void {
        _connectionMode = ACTIVATION_MODE;
        enterConnectionMode();
    }

    public function enterIdentityLookupMode():void {
        _connectionMode = IDENTITY_LOOKUP_MODE;
        enterConnectionMode();
    }

    public function exitConnectionMode():void {
        _connectionDragInProgress = false;
        _isConnectionMode = false;
        _connectionStartPoint = null;
        _connectionSourceNode = null;
        _connectionTargetNode = null;
        _dragComponent = null;
        _moveNodeInDrag = true;
        _moveEdgeInDrag = true;
        _moveGraphInDrag = true;
        (document as DisplayObject).removeEventListener(MouseEvent.CLICK, mouseClickHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.ROLL_OUT, rollOutHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_MOVE, handleConnectionDrag);
    }

    public function resetConnectionModeParameters():void {
        _connectionStartPoint = null;
        _connectionSourceNode = null;
        _connectionTargetNode = null;
        _connectionDragInProgress = false;
        _canvas.removeEventListener(MouseEvent.ROLL_OUT,dragEnd);
	    _canvas.removeEventListener(MouseEvent.MOUSE_UP,dragEnd);
        _dragComponent = null;
    }

    private function mouseClickHandler(event:MouseEvent):void {
        if (_isConnectionMode && !_connectionDragInProgress) {
            exitConnectionMode();
            CursorManager.removeAllCursors();
        }
    }

    private function mouseOverHandler(event:MouseEvent):void {
        if (_isConnectionMode) {
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
        CursorManager.removeAllCursors();
    }

    private function rollOutHandler(event:MouseEvent):void {
        if (_isConnectionMode && _connectionDragInProgress) {
            exitConnectionMode();
            refresh();
            event.updateAfterEvent();
            CursorManager.removeAllCursors();
        }
    }

    private function handleConnectionDrag(event:MouseEvent):void {
        if (_isConnectionMode) {
            refresh();
			event.updateAfterEvent();
        }
    }

    public function createVisualNode(node:INode):IVisualNode {
        return createVNode(node);
    }

    public function createCustomVEdge(parentNode:INode, node:INode, edgeIcon:Class = null,
                edgeIconToolTip:String = null, edgeLabel:String = null):IVisualEdge {
        var tmpVEdge:IVisualEdge;
        var tmpEdge:IEdge;
        
        var edgeData:Object = new Object();
        if (edgeIcon) {
            edgeData.edgeIcon = edgeIcon;
            if (edgeIconToolTip) {
                edgeData.edgeIconToolTip = edgeIconToolTip;
            }
            //edgeData.edgeIconWidth = 22;
            //edgeData.edgeIconHeight = 22;
        }
        if (edgeLabel) {
            edgeData.edgeLabel = edgeLabel;
        }
        edgeData.fromID = parentNode.stringid;
        edgeData.toID = node.stringid;
        // TODO: set real connection data object
        edgeData.data = new Object();
        edgeData.data.type = "connection";

        tmpEdge = graph.link(parentNode,node, edgeData);

        if (tmpEdge == null) {
            throw Error("Could not create or find Graph edge!!!");
        } else {
            if (tmpEdge.vedge == null) {
                /* we have a new edge, so we create a new VEdge */
                tmpVEdge = createVEdge(tmpEdge);
            } else {
                /* existing one, so we use the existing vedge */
                tmpVEdge = tmpEdge.vedge;
            }
        }
        setEdgeVisibility(tmpEdge.vedge, true);

        return tmpVEdge;
    }

    public function setVNodeVisibility(vnode:IVisualNode, visible:Boolean):void {
        setNodeVisibility(vnode, visible);
    }

    public function removeEdge(vedge:IVisualEdge):void {
        if (vedge != null) {
            if (vedge.labelView != null) {
                removeVEdgeView(vedge.labelView);
            }
            removeVEdge(vedge);
        }
    }
    
    private function edgeEventHandler(event:VGEdgeEvent):void {
        _selectedEdge = event.edge;
        _selectedEdgeId = _selectedEdge.id;
        for each (var edge:IEdge in graph.edges) {
            if (edge.id == _selectedEdgeId) {
                edge.vedge.lineStyle.color = 0x6B8E23;
            } else {
                edge.vedge.lineStyle.color = 0xCCCCCC;
            }
        }

        dispatchEvent(new VEdgeSelectedEvent(VEdgeSelectedEvent.VEDGE_SELECTED, _selectedEdge, true, false, 0));
        refresh();
    }

    private function canConnect(sourceNode:IVisualNode, targetNode:IVisualNode):Boolean {
        var canConnect:Boolean = false;
        if(_connectionMode == FEDERATED_CONNECTION_MODE && DiagramUtil.nodesCanBeLinkedWithFederatedConnection(sourceNode, targetNode)) {
            canConnect = true;
        } else if (_connectionMode == ACTIVATION_MODE && DiagramUtil.nodesCanBeLinkedWithActivation(sourceNode, targetNode)){
            canConnect = true;
        } else if (_connectionMode == IDENTITY_LOOKUP_MODE && DiagramUtil.nodesCanBeLinkedWithIdentityLookup(sourceNode, targetNode)){
            canConnect = true;
        }
        return canConnect;
    }
    // Getters and Setters

    public function get connectionMode():Boolean {
        return _isConnectionMode;
    }

    public function set connectionMode(value:Boolean):void {
        _isConnectionMode = value;
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