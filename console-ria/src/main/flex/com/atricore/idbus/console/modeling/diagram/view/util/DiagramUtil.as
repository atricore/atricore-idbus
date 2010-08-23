package com.atricore.idbus.console.modeling.diagram.view.util {
import com.atricore.idbus.console.services.dto.IdentityProviderDTO;

import com.atricore.idbus.console.services.dto.IdentityVaultDTO;
import com.atricore.idbus.console.services.dto.ServiceProviderDTO;

import org.un.cava.birdeye.ravis.graphLayout.visual.IVisualNode;

public class DiagramUtil {

    public function DiagramUtil() {
    }

    public static function nodesCanBeLinked(node1:IVisualNode, node2:IVisualNode):Boolean {
        var canBeLinked:Boolean = false;
        if (node1 != null && node2 != null && node1.id != node2.id) {
            // TODO: finish this
            if ((node1.data is IdentityProviderDTO && node2.data is ServiceProviderDTO) ||
                (node1.data is ServiceProviderDTO && node2.data is IdentityProviderDTO) ||
                (node1.data is IdentityProviderDTO && node2.data is IdentityVaultDTO) ||
                (node1.data is IdentityVaultDTO && node2.data is IdentityProviderDTO) ||
                (node1.data is ServiceProviderDTO && node2.data is IdentityVaultDTO) ||   //TODO - REMOVE. added as connection workaround
                (node1.data is IdentityVaultDTO && node2.data is ServiceProviderDTO)) {      //TODO - REMOVE. added as connection workaround
                    
                canBeLinked = true;
            }
        }
        return canBeLinked;
    }
}
}