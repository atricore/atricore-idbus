package com.atricore.idbus.console.lifecycle.main.transform.transformers.oauth2;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.oauth2.AbstractOAuth2SPChannelTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Transformer for OAuth 2.0 IdP local services
 *
 * Generates default IdP components for IdPs having OAuth 2.0 enabled
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdpFederatedConnectionTransformer extends AbstractOAuth2SPChannelTransformer {

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

        // TODO : Make sure that OAuth 2.0 is enabled

        if (event.getData() instanceof ServiceProviderChannel) {

            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (roleA) {
                return spChannel.isOverrideProviderSetup() && fc.getRoleA() instanceof IdentityProvider
                        && !fc.getRoleA().isRemote() && ((IdentityProvider) fc.getRoleA()).isOauth2Enabled();
            } else {
                return spChannel.isOverrideProviderSetup() && fc.getRoleB() instanceof IdentityProvider
                        && !fc.getRoleB().isRemote() && ((IdentityProvider) fc.getRoleB()).isOauth2Enabled();
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
                    "SP OAUTH2 Channel " + spChannel.getName() + " should be 'A' channel in federated connection " +
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
                    "SP OAUTH2 Channel " + spChannel.getName() + " should be 'B' channel in federated connection " +
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
