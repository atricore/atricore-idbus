package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.camel.component.bean.BeanHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlR2IdPProxyFederatedConnectionTransformer extends AbstractSPChannelTransformer {

    private static final Log logger = LogFactory.getLog(IdpFederatedConnectionTransformer.class);

    private boolean roleA;

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    public SamlR2IdPProxyFederatedConnectionTransformer() {
        super.setUseProxy(true);
    }

    @Override
    public boolean accept(TransformEvent event) {
        if (event.getData() instanceof ServiceProviderChannel) {

            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (roleA) {
                // Accept all Federated connection nodes that have an IdP as role A
                return fc.getRoleA() instanceof Saml2IdentityProvider && fc.getRoleA().isRemote();
                /* TODO : Enable after console support is added in the front-end
                return spChannel.isOverrideProviderSetup() && fc.getRoleA() instanceof Saml2IdentityProvider
                        && fc.getRoleA().isRemote(); */
            } else {
                // Accept all Federated connection nodes that have an IdP as role B
                return fc.getRoleB() instanceof Saml2IdentityProvider && fc.getRoleB().isRemote();
                /* TODO : Enable after console support is added in the front-end
                return spChannel.isOverrideProviderSetup() && fc.getRoleB() instanceof Saml2IdentityProvider
                        && fc.getRoleB().isRemote(); */
            }

        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        FederatedConnection federatedConnection = (FederatedConnection) event.getContext().getParentNode();
        ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();

        Saml2IdentityProvider idp;

        FederatedProvider target;
        FederatedChannel targetChannel;

        if (roleA) {

            assert spChannel == federatedConnection.getChannelA() :
                    "SP Channel " + spChannel.getName() + " should be 'A' channel in federated connection " +
                            federatedConnection.getName();

            idp = (Saml2IdentityProvider) federatedConnection.getRoleA();
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


            idp = (Saml2IdentityProvider) federatedConnection.getRoleB();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelB();

            target = federatedConnection.getRoleA();
            targetChannel = federatedConnection.getChannelA();

            if (!idp.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + idp +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }

        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");
        Bean idpProxyBean = (Bean) event.getContext().get("idpProxyBean");

        generateIdPComponents(idpProxyBeans, idp, spChannel, federatedConnection, target, targetChannel, event.getContext());

    }



}
