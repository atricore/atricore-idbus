package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdpFederatedConnectionTransformer extends AbstractSPChannelTransformer {

    private static final Log logger = LogFactory.getLog(IdpFederatedConnectionTransformer.class);

    private boolean roleA;

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof ServiceProviderChannel) {

            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (roleA) {
                return spChannel.isOverrideProviderSetup() && fc.getRoleA() instanceof IdentityProvider
                        && !fc.getRoleA().isRemote();
            } else {
                return spChannel.isOverrideProviderSetup() && fc.getRoleB() instanceof IdentityProvider
                        && !fc.getRoleB().isRemote();
            }

        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        FederatedConnection federatedConnection = (FederatedConnection) event.getContext().getParentNode();
        ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();

        IdentityProvider idp;

        FederatedProvider target;
        FederatedChannel targetChannel;

        if (roleA) {

            assert spChannel == federatedConnection.getChannelA() :
                    "SP Channel " + spChannel.getName() + " should be 'A' channel in federated connection " +
                            federatedConnection.getName();

            idp = (IdentityProvider) federatedConnection.getRoleA();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelA();

            target = federatedConnection.getRoleB();
            targetChannel = federatedConnection.getChannelB();

            if (!idp.getName().equals(federatedConnection.getRoleA().getName()))
                throw new IllegalStateException("Context provider " + idp +
                        " is not roleA provider in Federated Connection " + federatedConnection.getName());

        } else {

            assert spChannel == federatedConnection.getChannelB() :
                    "SP Channel " + spChannel.getName() + " should be 'B' channel in federated connection " +
                            federatedConnection.getName();


            idp = (IdentityProvider) federatedConnection.getRoleB();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelB();

            target = federatedConnection.getRoleA();
            targetChannel = federatedConnection.getChannelA();

            if (!idp.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + idp +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }

        generateIdPComponents(idp, spChannel, federatedConnection, target, targetChannel, event.getContext());

    }
}
