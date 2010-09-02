/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.modeling.diagram.model {
import com.atricore.idbus.console.components.CustomVisualGraph;

import com.atricore.idbus.console.main.EmbeddedIcons;

import com.atricore.idbus.console.services.dto.Connection;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualEdge;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualGraph;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class GraphDataManager {

    public static function addVNodeAsChild(_vgraph:IVisualGraph, sid:String, data:Object, parentNode:IVisualNode, connection:Connection, refresh:Boolean, deep: int):IVisualNode {
        var rootVNode:IVisualNode;
        var node:INode;
        var vnode:IVisualNode;

        if (_vgraph == null)
            return null;

        if (_vgraph.currentRootVNode != null)
            rootVNode = _vgraph.currentRootVNode;

        node = _vgraph.graph.createNode(sid, data);
        vnode = (_vgraph as CustomVisualGraph).createVisualNode(node);
        (_vgraph as CustomVisualGraph).setVNodeVisibility(vnode, true);

        if (parentNode != null) {
            (_vgraph as CustomVisualGraph).createCustomVEdge(parentNode.node, node, connection, EmbeddedIcons.connectionMiniIcon, "Connection");
            //_vgraph.currentRootVNode = rootVNode;
        } else {
            //_vgraph.currentRootVNode = vnode;
        }

        if (deep > 0 && deep > _vgraph.maxVisibleDistance){
            _vgraph.maxVisibleDistance = deep;
        }

        if (refresh)
            _vgraph.draw();
        return vnode;
    }

    public static function linkVNodes(_vgraph:IVisualGraph, node:IVisualNode, parentNode:IVisualNode, connection:Connection):void {
        (_vgraph as CustomVisualGraph).createCustomVEdge(parentNode.node, node.node, connection, EmbeddedIcons.connectionMiniIcon, "Connection");
    }

    public static function removeVEdge(_vgraph:IVisualGraph, edge:IVisualEdge):void {
        (_vgraph as CustomVisualGraph).removeEdge(edge);
    }

    /**
     * Removes a subtree from the main tree for which the root node is @node.
     */
    public static function removeSubTree(_vgraph:IVisualGraph, node:INode, isRootNodeRemovable:Boolean = true):Boolean {
        if ((!isRootNodeRemovable) && (node.vnode == _vgraph.currentRootVNode))
            return false;

        var arrTreeRoots:Array = [node];
        var curTreeRoot:INode = arrTreeRoots.pop();

        while(curTreeRoot) {
            for each (var nextTreeRoot:INode in curTreeRoot.successors)
                arrTreeRoots.push(nextTreeRoot)
            _vgraph.removeNode(curTreeRoot.vnode);

            curTreeRoot = arrTreeRoots.pop();
        }

        _vgraph.draw();
        return true;
    }

}
}