package com.atricore.idbus.console.lifecycle.main.transform.transformers.claims;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.authn.WindowsIntegratedAuthenticationTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class DominoAuthenticationClaimsChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(DominoAuthenticationClaimsChannelTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        if (!(event.getData() instanceof IdentityProvider))
            return false;
        IdentityProvider idp = (IdentityProvider)event.getData();
        if (idp.isRemote())
            return false;

        if (idp.getAuthenticationMechanisms() == null)
            return false;

        for (AuthenticationMechanism a : idp.getAuthenticationMechanisms()) {
            if (a instanceof DominoAuthentication)
                return true;
        }

        // None of the authn mechanisms is supported!
        return false;
    }

    /**
     * @param event
     * @throws com.atricore.idbus.console.lifecycle.main.exception.TransformException
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

        String claimChannelBeanName = normalizeBeanName(idpBean.getName() + "-domino-authn-claim-channel");
        if (getBean(idpBeans, claimChannelBeanName) != null) {
            // We already created the basic authentication claim channel ..
            if (logger.isDebugEnabled())
                logger.debug("Domino claim channel already created");
            return;
        }

        Bean claimChannelBean = null;

        for (AuthenticationMechanism authnMechanism : provider.getAuthenticationMechanisms()) {

            if (authnMechanism instanceof DominoAuthentication) {

                if (claimChannelBean != null) {
                    int currentPriority = Integer.parseInt(getPropertyValue(claimChannelBean, "priority"));
                    if (authnMechanism.getPriority() < currentPriority)
                        setPropertyValue(claimChannelBean, "priority",  authnMechanism.getPriority() + "");

                    // Only create one channel
                    continue;
                }


                claimChannelBean = newBean(idpBeans, claimChannelBeanName, ClaimChannelImpl.class);

                // priority
                setPropertyValue(claimChannelBean, "priority",  authnMechanism.getPriority() + "");

                // name
                setPropertyValue(claimChannelBean, "name", claimChannelBean.getName());

                // location
                String s = authnMechanism.getDelegatedAuthentication().getAuthnService().getName().toUpperCase();
                String locationUrl = resolveLocationUrl(provider) + "/CC/" + s;
                setPropertyValue(claimChannelBean, "location", locationUrl);

                // endpoints
                List<Bean> ccEndpoints = new ArrayList<Bean>();

                Bean ccDominoArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccDominoArtifact.setName(idpBean.getName() + "-cc-domino-artifact");
                setPropertyValue(ccDominoArtifact, "name", ccDominoArtifact.getName());
                setPropertyValue(ccDominoArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
                setPropertyValue(ccDominoArtifact, "location", "/DOMINO/ARTIFACT");
                setPropertyValue(ccDominoArtifact, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession");
                ccEndpoints.add(ccDominoArtifact);

                Bean ccDominoHttpPreauth = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccDominoHttpPreauth.setName(idpBean.getName() + "-cc-domino-preauth");
                setPropertyValue(ccDominoHttpPreauth, "name", ccDominoHttpPreauth.getName());
                setPropertyValue(ccDominoHttpPreauth, "type", "{urn:org:atricore:idbus:domino:metadata}InboundSSOService");
                setPropertyValue(ccDominoHttpPreauth, "binding", "urn:org:atricore:idbus:capabilities:domino:bindings:PREAUTH");
                setPropertyValue(ccDominoHttpPreauth, "location", "/DOMINO/PREAUTH");
                setPropertyValue(ccDominoHttpPreauth, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession");
                ccEndpoints.add(ccDominoHttpPreauth);

                setPropertyAsBeans(claimChannelBean, "endpoints", ccEndpoints);

                // ----------------------------------------
                // Claims Mediator
                // ----------------------------------------
                // TODO : Do not force domino on name
                Bean ccMediator = newBean(idpBeans, claimChannelBeanName + "-mediator", "org.atricore.idbus.capabilities.domino.main.DominoPreauthMediator");

                DominoAuthenticationService das = (DominoAuthenticationService) authnMechanism.getDelegatedAuthentication().getAuthnService();

                // logMessages
                setPropertyValue(ccMediator, "logMessages", true);

                // artifactQueueManager
                // setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
                setPropertyRef(ccMediator, "artifactQueueManager", "artifactQueueManager");

                // bindingFactory
                setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean("org.atricore.idbus.capabilities.domino.main.DominoBindingFactory"));

                List<Bean> ccLogBuilders = new ArrayList<Bean>();
                ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
                ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

                Bean ccLogger = newBean(idpBeans, claimChannelBeanName + "-mediation-logger", DefaultMediationLogger.class.getName());
                setPropertyValue(ccLogger, "category", appliance.getNamespace() + ".wire.cc1");
                setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

                // logger
                setPropertyBean(ccMediator, "logger", ccLogger);

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


    }
}
