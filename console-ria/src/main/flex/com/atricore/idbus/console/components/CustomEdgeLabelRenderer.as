package com.atricore.idbus.console.components {
import com.atricore.idbus.console.main.EmbeddedIcons;

import com.atricore.idbus.console.modeling.diagram.event.VEdgeRemoveEvent;

import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;

import mx.controls.Alert;
import mx.controls.Button;
import mx.controls.Image;
import mx.core.IDataRenderer;
import mx.core.UIComponent;

import mx.events.CloseEvent;

import org.un.cava.birdeye.ravis.enhancedGraphLayout.event.VGEdgeEvent;
import org.un.cava.birdeye.ravis.graphLayout.data.IEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.utils.TypeUtil;

public class CustomEdgeLabelRenderer extends UIComponent implements IDataRenderer {

    //if we use Label we must manually set width and height
	//public var label:Label;
    public var label:TextField;
    public var enableLabel:Boolean = true;

    public var icon:Image;
    public var enableIcon:Boolean = true;

    public var btnRemove:Button;
    
    private var _data:Object;
    
    public function CustomEdgeLabelRenderer() {
        super();
    }

    public function get data():Object {
        return _data;
    }

    public function set data(value:Object):void {
        _data = value;
        var edge:IEdge = (data as IVisualEdge).edge;
        var edgeVO:Object = data.data;
        var visible:String = String(edgeVO.visible);
        //var fromVisible:String = String(edge.fromNode.data.visible);
        //var toVisible:String = String(edge.toNode.data.visible);
        
        //if (TypeUtil.isFalse(visible) || TypeUtil.isFalse(fromVisible) || TypeUtil.isFalse(toVisible)) {
        if (TypeUtil.isFalse(visible)) {
            //trace("Edge is invisible " + edge.node1.stringid + ' --> ' + edge.node2.stringid);
            //this.visible = false;
        } else {
            var edgeVO:Object = data.data;
            if (enableLabel && edgeVO.edgeLabel) {
                //label = new Label();
                label = new TextField();
                label.selectable = false;  //remove text cursor
                label.autoSize = TextFieldAutoSize.CENTER;
                label.text = edgeVO.edgeLabel;
                this.addChild(label);
            }

            if (enableIcon && edgeVO.edgeIcon) {
                icon = new Image();
                icon.source = edgeVO.edgeIcon;
                icon.width = 22;
                icon.height = 22;
                if (edgeVO.edgeIconWidth) {
                    icon.width = edgeVO.edgeIconWidth;
                }
                if (edgeVO.edgeIconHeight) {
                    icon.height = edgeVO.edgeIconHeight;
                }
                if (edgeVO.edgeIconToolTip) {
                    icon.toolTip = edgeVO.edgeIconToolTip;
                }
                this.addChild(icon);
            }

            btnRemove = new Button();
            btnRemove.setStyle("icon", EmbeddedIcons.removeNodeIcon);
            btnRemove.width = 22;
            btnRemove.height = 22;
            btnRemove.visible = false;
            btnRemove.addEventListener(MouseEvent.CLICK, confirmEdgeRemove);
            this.addChild(btnRemove);
            
            addEventListeners();
        }
    }

    private function addEventListeners():void {
        this.addEventListener(MouseEvent.CLICK, mouseEventHandler);
        this.addEventListener(MouseEvent.ROLL_OVER, internalRollOverHandler);
        this.addEventListener(MouseEvent.ROLL_OUT, internalRollOutHandler);
        /*
        this.addEventListener(MouseEvent.MOUSE_DOWN, mouseEventHandler);
        this.addEventListener(MouseEvent.MOUSE_UP, mouseEventHandler);
        this.addEventListener(MouseEvent.MOUSE_MOVE, mouseEventHandler);
        this.addEventListener(MouseEvent.MOUSE_OVER, mouseEventHandler);
        this.addEventListener(MouseEvent.MOUSE_OUT, mouseEventHandler);
        this.addEventListener(MouseEvent.DOUBLE_CLICK, mouseEventHandler);
        */
    }

    private function removeEventListeners():void {
        this.removeEventListener(MouseEvent.CLICK, mouseEventHandler);
        this.removeEventListener(MouseEvent.ROLL_OVER, internalRollOverHandler);
        this.removeEventListener(MouseEvent.ROLL_OUT, internalRollOutHandler);
        /*
        this.removeEventListener(MouseEvent.MOUSE_DOWN, mouseEventHandler);
        this.removeEventListener(MouseEvent.MOUSE_UP, mouseEventHandler);
        this.removeEventListener(MouseEvent.MOUSE_MOVE, mouseEventHandler);
        this.removeEventListener(MouseEvent.MOUSE_OVER, mouseEventHandler);
        this.removeEventListener(MouseEvent.MOUSE_OUT, mouseEventHandler);
        this.removeEventListener(MouseEvent.DOUBLE_CLICK, mouseEventHandler);
        */
    }

    private function mouseEventHandler(event:MouseEvent):void {
        var vedge:IVisualEdge = data as IVisualEdge;
        if (vedge && vedge.vgraph) {
            var edgeEvent:VGEdgeEvent = new VGEdgeEvent(VGEdgeEvent.VG_EDGE_EVENT_PREFIX + event.type);
            edgeEvent.originalEvent = event;
            edgeEvent.edge = vedge.edge;
            vedge.vgraph.dispatchEvent(edgeEvent);
        }
    }

    private function internalRollOverHandler(event:MouseEvent):void {
        btnRemove.visible = true;
    }

    private function internalRollOutHandler(event:MouseEvent):void {
        btnRemove.visible = false;
    }

    private function confirmEdgeRemove(e:MouseEvent):void {
        Alert.show("Are you sure you want to delete this connection?", "Confirm Removal",
                Alert.YES | Alert.NO, null, edgeRemoveConfirmed, null, Alert.YES);
    }

    private function edgeRemoveConfirmed(event:CloseEvent):void {
        if (event.detail == Alert.YES) {
            dispatchEvent(new VEdgeRemoveEvent(VEdgeRemoveEvent.VEDGE_REMOVE, data.data.data, true, false, 0));
        }
    }
}
}