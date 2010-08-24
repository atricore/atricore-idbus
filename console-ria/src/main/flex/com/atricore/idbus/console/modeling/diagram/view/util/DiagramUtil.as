package com.atricore.idbus.console.modeling.diagram.view.util {
import com.atricore.idbus.console.services.dto.IdentityProvider;

import com.atricore.idbus.console.services.dto.IdentityVault;
import com.atricore.idbus.console.services.dto.ServiceProvider;

import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinked(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            // TODO: finish this
            if ((node1.data is IdentityProvider && node2.data is ServiceProvider) ||
                (node1.data is ServiceProvider && node2.data is IdentityProvider) ||
                (node1.data is IdentityProvider && node2.data is IdentityVault) ||
                (node1.data is IdentityVault && node2.data is IdentityProvider) ||
                (node1.data is ServiceProvider && node2.data is IdentityVault) ||   //TODO - REMOVE. added as connection workaround
                (node1.data is IdentityVault && node2.data is ServiceProvider)) {      //TODO - REMOVE. added as connection workaround
                    
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }
}
}