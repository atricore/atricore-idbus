package com.atricore.idbus.console.lifecycle.main.transform.transformers.claims;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.sso.OAuth2ClaimsMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsMediator;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OAuth2AuthenticationClaimsChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(OAuth2AuthenticationClaimsChannelTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        if (!(event.getData() instanceof IdentityProvider))
            return false;
        IdentityProvider idp = (IdentityProvider) event.getData();
        if (idp.isRemote())
            return false;

        if (idp.getAuthenticationMechanisms() == null)
            return false;

        if (idp.isOauth2Enabled())
            return true;

        // OAuth2 is not enabled
        return false;
    }

    /**
     * @param event
     * @throws com.atricore.idbus.console.lifecycle.main.exception.TransformException
     *
     */
    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider provider = (IdentityProvider) event.getData();
        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        if (logger.isTraceEnabled())
            logger.trace("Generating Claim Channel Beans for IDP Channel " + provider.getName());

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        String claimChannelBeanName = normalizeBeanName(idpBean.getName() + "-oauth2-authn-claim-channel");
        if (getBean(idpBeans, claimChannelBeanName) != null) {
            // We already created the basic authentication claim channel ..
            if (logger.isDebugEnabled())
                logger.debug("OAuth2 claim channel already created");
            return;
        }


        Bean claimChannelBean = null;
        claimChannelBean = newBean(idpBeans, claimChannelBeanName, ClaimChannelImpl.class);

        setPropertyValue(claimChannelBean, "priority", "0");
        setPropertyValue(claimChannelBean, "name", claimChannelBean.getName());

        // location
        String locationUrl = resolveLocationUrl(provider) + "/CC/OAUTH2";
        setPropertyValue(claimChannelBean, "location", locationUrl);

        // endpoints
        List<Bean> ccEndpoints = new ArrayList<Bean>();

        Bean ccOAuth2Artifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ccOAuth2Artifact.setName(idpBean.getName() + "-cc-oauth2-artifact");
        setPropertyValue(ccOAuth2Artifact, "name", ccOAuth2Artifact.getName());
        setPropertyValue(ccOAuth2Artifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
        setPropertyValue(ccOAuth2Artifact, "location", "/PRE-AUTHN/ARTIFACT");
        setPropertyValue(ccOAuth2Artifact, "type", AuthnCtxClass.OAUTH2_AUTHN_CTX.getValue());
        ccEndpoints.add(ccOAuth2Artifact);

        setPropertyAsBeans(claimChannelBean, "endpoints", ccEndpoints);

        // ----------------------------------------
        // Claims Mediator
        // ----------------------------------------
        Bean ccMediator = newBean(idpBeans, claimChannelBeanName + "-mediator", SSOClaimsMediator.class.getName());

        // logMessages
        setPropertyValue(ccMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(ccMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean("org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2BindingFactory"));

        List<Bean> ccLogBuilders = new ArrayList<Bean>();
        ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean ccLogger = newBean(idpBeans, claimChannelBeanName + "-mediation-logger", DefaultMediationLogger.class.getName());
        setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire.cc1");
        setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

        // logger
        setPropertyBean(ccMediator, "logger", ccLogger);

        // Provisioning target (default)
        Reference provisioningTargetOsgi = new Reference();
        provisioningTargetOsgi.setId(ccMediator.getName() + "-provisioning-target");
        provisioningTargetOsgi.setInterface(ProvisioningTarget.class.getName());
        provisioningTargetOsgi.setCardinality("1..1");

        idpBeans.getImportsAndAliasAndBeen().add(provisioningTargetOsgi);

        setPropertyRef(ccMediator, "provisioningTarget", ccMediator.getName() + "-provisioning-target");

        // errorUrl
        setPropertyValue(ccMediator, "errorUrl", resolveUiErrorLocation(appliance, provider));

        // warningUrl
        setPropertyValue(ccMediator, "warningUrl", resolveUiWarningLocation(appliance, provider));


        // identityMediator
        setPropertyRef(claimChannelBean, "identityMediator", ccMediator.getName());

        // provider
        setPropertyRef(claimChannelBean, "federatedProvider", idpBean.getName());

        // unitContainer
        setPropertyRef(claimChannelBean, "unitContainer", provider.getIdentityAppliance().getName() + "-container");

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(baseBeans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            addPropertyBeansAsRefs(mu, "channels", claimChannelBean);
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

    }
}