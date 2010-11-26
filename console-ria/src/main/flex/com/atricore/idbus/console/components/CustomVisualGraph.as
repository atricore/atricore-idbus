package com.atricore.idbus.console.components {
import com.atricore.idbus.console.modeling.diagram.event.VEdgeSelectedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeCreationEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodeMovedEvent;
import com.atricore.idbus.console.modeling.diagram.event.VNodesLinkedEvent;
import com.atricore.idbus.console.modeling.diagram.renderers.node.NodeDetailedRenderer;
import com.atricore.idbus.console.modeling.diagram.view.util.DiagramUtil;
import com.atricore.idbus.console.services.dto.Connection;

import flash.display.DisplayObject;
import flash.events.MouseEvent;
import flash.geom.Point;

import mx.managers.CursorManager;

import org.un.cava.birdeye.ravis.enhancedGraphLayout.event.VGEdgeEvent;
import org.un.cava.birdeye.ravis.enhancedGraphLayout.visual.EnhancedVisualGraph;
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.layout.CircularLayouter;
import org.un.cava.birdeye.ravis.graphLayout.layout.ILayoutAlgorithm;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.utils.events.VGraphEvent;

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

    private var _isNodeCreationMode:Boolean;
    private var _nodeCreationElementType:int;
    private var _nodeCreationPosition:Point;
    private var _nodeCreationElementIcon:Class;

    private var _nodeMoved:Boolean;
    private var _allNodesMoved:Boolean;

    public function CustomVisualGraph() {
        super();
        _nodeCreationElementType = -1;
        this.addEventListener(VGEdgeEvent.VG_EDGE_CLICK, edgeEventHandler);
    }

    override protected function dragBegin(event:MouseEvent):void {
        _nodeMoved = false;
        _allNodesMoved = false;
        super.dragBegin(event);
        if (event.currentTarget is NodeDetailedRenderer && _isConnectionMode) {
            _connectionDragInProgress = true;
            _connectionSourceNode = data as IVisualNode;
            _connectionStartPoint = new Point(_canvas.contentMouseX, _canvas.contentMouseY);
            _canvas.addEventListener(MouseEvent.MOUSE_UP, dragEnd);
        }
    }

    protected override function handleDrag(event:MouseEvent):void {
        super.handleDrag(event);
        _nodeMoved = true;
    }

    protected override function backgroundDragBegin(event:MouseEvent):void {
        super.backgroundDragBegin(event);
        _allNodesMoved = false;
    }

    override protected function backgroundDragContinue(event:MouseEvent):void {
        super.backgroundDragContinue(event);
        _allNodesMoved = true;
    }

    override protected function dragEnd(event:MouseEvent):void {
        super.dragEnd(event);
        if (_isConnectionMode) {
            connectionTargetNode = data as IVisualNode;
            if (_connectionSourceNode != null && _connectionTargetNode != null) {
                if (!DiagramUtil.nodeLinkExists(_connectionSourceNode.node, _connectionTargetNode.node)
                        && canConnect(_connectionSourceNode, _connectionTargetNode)){
//                     TODO: move linkNodes() call to DiagramMediator.nodesLinkedEventHandler() ?
                    //don't link nodes here, dispatch the create LINK_NODES event and let modeler catch it
//                    GraphDataManager.linkVNodes(this, _connectionSourceNode, _connectionTargetNode);
                    if(_connectionMode == FEDERATED_CONNECTION_MODE){
                        dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.FEDERATED_CONNECTION_CREATED, _connectionSourceNode, _connectionTargetNode, true, false, 0));
                    } else if(_connectionMode == ACTIVATION_MODE){
                        dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.ACTIVATION_CREATED, _connectionSourceNode, _connectionTargetNode, true, false, 0));
                    } else if(_connectionMode == IDENTITY_LOOKUP_MODE){
                        dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.IDENTITY_LOOKUP_CREATED, _connectionSourceNode, _connectionTargetNode, true, false, 0));
                    }
                    exitConnectionMode();
                    CursorManager.removeAllCursors();
                }
            }
            resetConnectionModeParameters();
            _canvas.removeEventListener(MouseEvent.MOUSE_UP, dragEnd);
        } else {
            var draggedNode = data as IVisualNode;
            if (draggedNode != null && _nodeMoved) {
                dispatchEvent(new VNodeMovedEvent(VNodeMovedEvent.VNODE_MOVED, draggedNode.node.stringid, true, false, 0));
            } else if (_allNodesMoved) {
                dispatchEvent(new VNodeMovedEvent(VNodeMovedEvent.ALL_VNODES_MOVED, null, true, false, 0));
            }
        }
        _nodeMoved = false;
        _allNodesMoved = false;
    }

    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if (_isConnectionMode && _connectionDragInProgress) {
            var lineColor:uint = uint(0xCCCCCC);  //grey
            var targetNode:IVisualNode = data as IVisualNode;
            if (_connectionSourceNode != null && targetNode != null && targetNode != _connectionSourceNode) {
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

    override public function set layouter(l:ILayoutAlgorithm):void {
        //super.layouter = l;
        //if (_layouter != null) {
        //    _layouter.resetAll(); // to stop any pending animations
        //}
        _layouter = l;
        /* need to signal control components possibly */
        this.dispatchEvent(new VGraphEvent(VGraphEvent.LAYOUTER_CHANGED));
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
        exitNodeCreationMode();
        _connectionMode = FEDERATED_CONNECTION_MODE;
        enterConnectionMode();
    }

    public function enterActivationMode():void {
        exitNodeCreationMode();
        _connectionMode = ACTIVATION_MODE;
        enterConnectionMode();
    }

    public function enterIdentityLookupMode():void {
        exitNodeCreationMode();
        _connectionMode = IDENTITY_LOOKUP_MODE;
        enterConnectionMode();
    }

    public function enterNodeCreationMode(elementType:int):void {
        exitConnectionMode();
        _isNodeCreationMode = true;
        _nodeCreationElementType = elementType;
        _nodeCreationElementIcon = DiagramUtil.getIconForElementType(_nodeCreationElementType);
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

    public function exitNodeCreationMode():void {
        _isNodeCreationMode = false;
        _nodeCreationElementType = -1;
        _nodeCreationElementIcon = null;
        _dragComponent = null;
        _moveNodeInDrag = true;
        _moveEdgeInDrag = true;
        _moveGraphInDrag = true;
        (document as DisplayObject).removeEventListener(MouseEvent.CLICK, mouseClickHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        (document as DisplayObject).removeEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
    }

    public function resetGraph():void {
        _nodeCreationPosition = null;
    }

    private function mouseClickHandler(event:MouseEvent):void {
        if (_isConnectionMode && !_connectionDragInProgress) {
            exitConnectionMode();
            dispatchEvent(new VNodesLinkedEvent(VNodesLinkedEvent.LINKING_CANCELED, null, null, true, false, 0));
            CursorManager.removeAllCursors();
        } else if (_isNodeCreationMode) {
            _nodeCreationPosition = new Point(_canvas.contentMouseX, _canvas.contentMouseY);
            dispatchEvent(new VNodeCreationEvent(VNodeCreationEvent.OPEN_CREATION_FORM, _nodeCreationElementType, true, false, 0));
            exitNodeCreationMode();
            CursorManager.removeAllCursors();
        }
    }

    private function mouseOverHandler(event:MouseEvent):void {
        if (_isConnectionMode) {
            CursorManager.setCursor(crossCursorSymbol);
        } else if (_isNodeCreationMode && _nodeCreationElementIcon != null) {
            CursorManager.setCursor(_nodeCreationElementIcon);
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
        var vnode:IVisualNode = createVNode(node);
        if (!(_layouter is CircularLayouter)) {
            if (_nodeCreationPosition != null) {
                //vnode.viewX = _nodeCreationPosition.x - vnode.view.width / 2;
                //vnode.viewY = _nodeCreationPosition.y - vnode.view.height / 2;
                vnode.viewX = _nodeCreationPosition.x - 32;
                vnode.viewY = _nodeCreationPosition.y - 22;
                _nodeCreationPosition = null;
                node.data.x = vnode.viewX;
                node.data.y = vnode.viewY;
            } else {
                vnode.viewX = node.data.x;
                vnode.viewY = node.data.y;
            }
        }
        return vnode;
    }

    public function createCustomVEdge(parentNode:INode, node:INode, connection:Connection, edgeIcon:Class = null,
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
        
        edgeData.data = connection;

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
            unlinkNodes(vedge.edge.node1.vnode, vedge.edge.node2.vnode);
            /*if (vedge.labelView != null) {
                removeVEdgeView(vedge.labelView);
            }
            removeVEdge(vedge);
            _graph.removeEdge(vedge.edge);*/
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