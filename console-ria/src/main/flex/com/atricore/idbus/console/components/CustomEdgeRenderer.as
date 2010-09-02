package com.atricore.idbus.console.components {
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.geom.Point;

import mx.core.UIComponent;

import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;
import org.un.cava.birdeye.ravis.graphLayout.visual.edgeRenderers.BaseEdgeRenderer;

public class CustomEdgeRenderer extends BaseEdgeRenderer {

    protected var labelView:UIComponent;
    protected var drawSurface:UIComponent;
    
    public function CustomEdgeRenderer(g:Graphics) {
        super(g);
    }

    override public function draw(vedge:IVisualEdge):void {
        drawSurface = vedge.vgraph.drawingSurface;
        labelView = vedge.labelView;
        
        var castedComp:CustomEdgeLabelRenderer;
        if (labelView is CustomEdgeLabelRenderer) {
            castedComp = CustomEdgeLabelRenderer(labelView);
        }

        _g = labelView.graphics;
        _g.clear();

        /* first get the corresponding visual object */
        var fromNode:IVisualNode = vedge.edge.node1.vnode;
        var toNode:IVisualNode = vedge.edge.node2.vnode;

        vedge.lineStyle.thickness = 2;
        /* apply the line style */
        applyLineStyle(vedge);

        /* now we actually draw */
        _g.beginFill(uint(vedge.lineStyle.color));
        // remove button is not visible so we move "y" coordinate a little to the top
        moveTo(_g, fromNode.viewCenter.x, fromNode.viewCenter.y - 15);
        lineTo(_g, toNode.viewCenter.x, toNode.viewCenter.y - 15);
        _g.endFill();

        /* if the vgraph currently displays edgeLabels, then
         * we need to update their coordinates */
        if(vedge.vgraph.displayEdgeLabels) {
            var midPt:Point = labelCoordinates(vedge);
            
            if (castedComp && castedComp.label) {
                positionComponent(castedComp.label , midPt.x - (castedComp.label.width/2), midPt.y - (castedComp.label.height/2));
            }
            
            if (castedComp && castedComp.icon) {
                positionComponent(castedComp.icon, midPt.x - (castedComp.icon.width/2), midPt.y - (castedComp.icon.height/2));
                positionComponent(castedComp.btnRemove, midPt.x - (castedComp.btnRemove.width/2), midPt.y + (castedComp.icon.height/2) - 1);
            }
        }
    }

    override public function labelCoordinates(vedge:IVisualEdge):Point {
        var point:Point = super.labelCoordinates(vedge);
        point.y -= 15;
        return point;
    }

    protected function lineTo(g:Graphics, ptx:Number, pty:Number):void {
        var pos:Point = new Point(ptx, pty);
        pos = drawSurface.localToGlobal(pos);
        pos = labelView.globalToLocal(pos);
        g.lineTo(pos.x, pos.y);
    }

    protected function moveTo(g:Graphics, ptx:Number, pty:Number):void {
        var pos:Point = new Point(ptx, pty);
        pos = drawSurface.localToGlobal(pos);
        pos = labelView.globalToLocal(pos);
        g.moveTo(pos.x, pos.y);
    }

    protected function positionComponent(component:DisplayObject, ptx:Number, pty:Number):void {
        var pos:Point = new Point(ptx, pty);
        pos = drawSurface.localToGlobal(pos);
        pos = labelView.globalToLocal(pos);
        component.x = pos.x;
        component.y = pos.y;
    }
}
}