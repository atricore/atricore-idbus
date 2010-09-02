package com.atricore.idbus.console.modeling.diagram.view.util {
import com.atricore.idbus.console.services.dto.ExecutionEnvironment;
import com.atricore.idbus.console.services.dto.FederatedProvider;
import com.atricore.idbus.console.services.dto.IdentityProvider;

import com.atricore.idbus.console.services.dto.IdentitySource;
import com.atricore.idbus.console.services.dto.Provider;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import org.un.cava.birdeye.ravis.graphLayout.data.INode;
import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinkedWithFederatedConnection(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id && !nodeLinkExists(node1.node, node2.node)) {
            // TODO: finish this
            if ((node1.data is ServiceProvider || node1.data is IdentityProvider)
                    && (node2.data is IdentityProvider || node2.data is ServiceProvider)) {                    
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithActivation(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if ((node1.data is ServiceProvider && node2.data is ExecutionEnvironment) ||
                (node1.data is ExecutionEnvironment && node2.data is ServiceProvider)) {
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodesCanBeLinkedWithIdentityLookup(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            if ((node1.data is Provider && node2.data is IdentitySource) ||
                (node1.data is IdentitySource && node2.data is Provider)) {

                canBeLinked = true;
            }
        }
        return canBeLinked;
    }

    public static function nodeLinkExists(node1:INode, node2:INode):Boolean {
        if (node1 != null && node2 != null && node1.successors.indexOf(node2) != -1) {
            return true;
        }
        return false;
    }
}
}