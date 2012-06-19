package com.atricore.idbus.console.lifecycle.main.transform.transformers.sso;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractIdPChannelTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This handles SP components generation for a specific federated connection (override == true)
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SPFederatedConnectionTransformer extends AbstractIdPChannelTransformer {

    private static final Log logger = LogFactory.getLog(SPFederatedConnectionTransformer.class);

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

        FederatedConnection federatedConnection = (FederatedConnection) event.getContext().getParentNode();
        IdentityProviderChannel idpChannel = (IdentityProviderChannel) event.getData();

        ServiceProvider sp;

        FederatedProvider target;
        FederatedChannel targetChannel;

        // Determine our role in the relationship: A or B, and make some validations

        if (roleA) {

            assert idpChannel == federatedConnection.getChannelA() :
                    "IDP Channel " + idpChannel.getName() + " should be 'A' channel in federated connection " +
                            federatedConnection.getName();

            sp = (ServiceProvider) federatedConnection.getRoleA();
            idpChannel = (IdentityProviderChannel) federatedConnection.getChannelA();

            target = federatedConnection.getRoleB();
            targetChannel = federatedConnection.getChannelB();

            if (!sp.getName().equals(federatedConnection.getRoleA().getName()))
                throw new IllegalStateException("Context provider " + sp +
                        " is not roleA provider in Federated Connection " + federatedConnection.getName());

        } else {

            assert idpChannel == federatedConnection.getChannelB() :
                    "IDP Channel " + idpChannel.getName() + " should be 'B' channel in federated connection " +
                            federatedConnection.getName();


            sp = (ServiceProvider) federatedConnection.getRoleB();
            idpChannel = (IdentityProviderChannel) federatedConnection.getChannelB();

            target = federatedConnection.getRoleA();
            targetChannel = federatedConnection.getChannelA();

            if (!sp.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + sp +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }

        // Generate connection specific SP components
        generateSPComponents(sp, idpChannel, federatedConnection, target, targetChannel, event.getContext());

        // TODO : Generate SP Proxy components

    }
}

