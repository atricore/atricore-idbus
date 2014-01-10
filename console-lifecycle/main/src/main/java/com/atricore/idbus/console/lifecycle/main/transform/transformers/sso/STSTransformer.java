package com.atricore.idbus.console.lifecycle.main.transform.transformers.sso;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmitter;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * WS-Trust security token service with default SAML 2.0 Emitter
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class STSTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(STSTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // TODO : Check that Federated connection is set to override ?!
        if (event.getData() instanceof IdentityProvider && !((IdentityProvider)event.getData()).isRemote()) {
            if (logger.isDebugEnabled())
                logger.debug("Required STS for local IdP " + ((IdentityProvider)event.getData()).getName());

            return true;
        }

        if (event.getData() instanceof ServiceProviderChannel) {
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            boolean requireProxy = isIdPProxyRequired(fc, true) || isIdPProxyRequired(fc, false);

            if (requireProxy)
                if (logger.isDebugEnabled())
                    logger.debug("Required STS for proxied IdP between "  + fc.getRoleA().getName() + ":" + fc.getRoleB().getName());

            return requireProxy;
        }


        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        boolean isProxy = false;

        FederatedProvider provider = null;
        if (event.getData() instanceof FederatedProvider) {
            provider = (FederatedProvider) event.getData();
            isProxy = false;

            if (logger.isDebugEnabled())
                logger.debug("Creating STS components for local IdP "  + provider.getName());

        } else if (event.getData() instanceof ServiceProviderChannel) {


            ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

            if (logger.isTraceEnabled())
                logger.trace("Creating STS components for proxied IdP between " + fc.getRoleA().getName() + ":" + fc.getRoleB().getName());

            isProxy = true;
            if (fc.getRoleA() instanceof ExternalSaml2IdentityProvider && fc.getRoleA().isRemote())
                provider = fc.getRoleA();
            else if (fc.getRoleB() instanceof ExternalSaml2IdentityProvider && fc.getRoleB().isRemote()) {
                provider = fc.getRoleB();
            }
        }

        assert provider != null : "No valid provider found for STS ";
        // Beans idpBeans = isProxy ? (Beans) event.getContext().get("idpProxyBeans") : (Beans) event.getContext().get("idpBeans");
        Beans idpBeans = isProxy ? (Beans) event.getContext().get("idpProxyBeans") : (Beans) event.getContext().get("idpBeans");

        if (logger.isTraceEnabled())
            logger.trace("Generating STS Beans for IdP " + provider.getName());

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();
        
        // ----------------------------------------
        // STS
        // ----------------------------------------
        Bean sts = newBean(idpBeans, idpBean.getName() + "-sts",
                "org.atricore.idbus.capabilities.sts.main.WSTSecurityTokenService");
        setPropertyValue(sts, "name", sts.getName());

        // ----------------------------------------
        // Emitters
        // ----------------------------------------
        Bean stsEmitter = newBean(idpBeans,
                idpBean.getName() + "-samlr2-assertion-emitter",
                SamlR2SecurityTokenEmitter.class.getName());
        setPropertyValue(stsEmitter, "id", stsEmitter.getName());

        // identityPlanRegistry
        setPropertyRef(stsEmitter, "identityPlanRegistry", "identity-plans-registry");

        Collection<Bean> mediators = getBeansOfType(idpBeans, SSOIDPMediator.class.getName());

        if (mediators.size() != 1)
            throw new TransformException("Too many/few mediators defined " + mediators.size());

        Bean mediatorBean = mediators.iterator().next();

        String signerBeanName = getPropertyRef(mediatorBean , "signer");
        if (signerBeanName == null)
            throw new TransformException("No 'SIGNER' defined in Mediator " + mediatorBean.getName());

        Bean signerBean = getBean(idpBeans, signerBeanName);
        if (signerBean != null) {
            // signer
            setPropertyRef(stsEmitter, "signer", signerBean.getName());
        } else {
            throw new TransformException("No 'SIGNER' defined as " + signerBeanName);
        }

        List<Bean> emitters = new ArrayList<Bean>();
        emitters.add(stsEmitter);
        setPropertyAsBeans(sts, "emitters", emitters);

        // ----------------------------------------
        // JOSSO Legacy authenticator
        // ----------------------------------------

        Bean legacyAuthenticator = newBean(idpBeans, idpBean.getName() + "-legacy-authenticator", AuthenticatorImpl.class.getName());
        List<Ref> authnSchemes = new ArrayList<Ref>();

        if (provider instanceof IdentityProvider) {
            IdentityProvider localIdp = (IdentityProvider) provider;
            if (localIdp.getAuthenticationMechanisms().size() < 1)
                throw new TransformException("No Authentication Mechanism defined for " + provider.getName());

            setPropertyRefs(legacyAuthenticator, "authenticationSchemes", authnSchemes);
        }
        
        // ----------------------------------------
        // Atricore Authenticators
        // ----------------------------------------

        // Create empty list with all authenticators ...
        List<Bean> authenticators = new ArrayList<Bean>();

        // authenticators
        setPropertyAsBeans(sts, "authenticators", authenticators);

        // Provisioning target (default)
        Reference provisioningTargetOsgi = new Reference();
        provisioningTargetOsgi.setId(sts.getName() + "-provisioning-target");
        provisioningTargetOsgi.setInterface(ProvisioningTarget.class.getName());
        provisioningTargetOsgi.setCardinality("1..1");

        idpBeans.getImportsAndAliasAndBeen().add(provisioningTargetOsgi);

        setPropertyRef(sts, "provisioningTarget", sts.getName() + "-provisioning-target");

        // Add pass-through authenticator
        Bean passThroughAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.sts.main.authenticators.PassthroughSecurityTokenAuthenticator");
        addPropertyBean(sts, "authenticators", passThroughAuthenticator);

        // artifactQueueManager
        // setPropertyRef(sts, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(sts, "artifactQueueManager", "artifactQueueManager");
    }
}
