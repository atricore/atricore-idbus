package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.FederatedConnection;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProviderChannel;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SPProxyFederatedConnectionTransformer extends AbstractIdPChannelTransformer {

    private static final Log logger = LogFactory.getLog(SPProxyFederatedConnectionTransformer.class);

    private boolean roleA;

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof IdentityProviderChannel) {

            // We generate a proxy definition for remote

            IdentityProviderChannel idpChannel = (IdentityProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (roleA) {
                // Accept all Federated connection nodes that have an SP as role A
                return idpChannel.isOverrideProviderSetup() && fc.getRoleA() instanceof ServiceProvider
                        && !fc.getRoleA().isRemote();
            } else {
                // Accept all Federated connection nodes that have an SP as role B
                return idpChannel.isOverrideProviderSetup() && fc.getRoleB() instanceof ServiceProvider
                        && !fc.getRoleB().isRemote();
            }

        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        // Generate the proxied version of

    }
}
