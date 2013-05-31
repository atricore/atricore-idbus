package com.atricore.idbus.console.lifecycle.main.transform.transformers.idconf;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannelImpl;
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
public class BasicIdentityConfirmationChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(BasicIdentityConfirmationChannelTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        if (!(event.getData() instanceof IdentityProvider))
            return false;
        IdentityProvider idp = (IdentityProvider) event.getData();
        if (idp.isRemote())
            return false;

        if (idp.getAuthenticationMechanisms() == null)
            return false;

        return true;
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
            logger.trace("Generating Identity Confirmation Channel Beans for IDP Channel " + provider.getName());

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // ---------------------------------------------------------------
        // We have a single identity confirmation channel
        // ---------------------------------------------------------------
        String idConfChannelBeanName = normalizeBeanName(idpBean.getName() + "-default-idconf-channel");
        if (getBean(idpBeans, idConfChannelBeanName) != null) {
            // We already created the basic authentication claim channel ..
            if (logger.isDebugEnabled())
                logger.debug("Identity Confirmation channel already created");
            return;
        }

        Bean identityConfirmationChannelBean = null;

        identityConfirmationChannelBean = newBean(idpBeans, idConfChannelBeanName, IdentityConfirmationChannelImpl.class);

        // name
        setPropertyValue(identityConfirmationChannelBean, "name", identityConfirmationChannelBean.getName());

        // location
        String locationUrl = resolveLocationUrl(provider) + "/IDCONF";
        setPropertyValue(identityConfirmationChannelBean, "location", locationUrl);

        // endpoints
        List<Bean> idConfEndpoints = new ArrayList<Bean>();

        Bean idConfArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        idConfArtifact.setName(idpBean.getName() + "-idcc-idconf-artifact");
        setPropertyValue(idConfArtifact, "name", idConfArtifact.getName());
        setPropertyValue(idConfArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
        setPropertyValue(idConfArtifact, "location", "/EMB/ARTIFACT");
        setPropertyValue(idConfArtifact, "type", "urn:org:atricore:idbus:ac:classes:IdentityConfirmation");
        idConfEndpoints.add(idConfArtifact);

        Bean idConfHttpAuthn = newAnonymousBean(IdentityMediationEndpointImpl.class);
        idConfHttpAuthn.setName(idpBean.getName() + "-idcc-idconf-authentication");
        setPropertyValue(idConfHttpAuthn, "name", idConfHttpAuthn.getName());
        setPropertyValue(idConfHttpAuthn, "binding", "urn:org:atricore:idbus:identityconfirmation:bindings:HTTP-Authentication");
        setPropertyValue(idConfHttpAuthn, "location", "/EMB/AUTHN");
        setPropertyValue(idConfHttpAuthn, "type", "urn:org:atricore:idbus:ac:classes:IdentityConfirmation");
        idConfEndpoints.add(idConfHttpAuthn);

        setPropertyAsBeans(identityConfirmationChannelBean, "endpoints", idConfEndpoints);

        // ----------------------------------------
        // Identity Confirmation Mediator
        // ----------------------------------------
        Bean idcMediator = newBean(idpBeans, idConfChannelBeanName + "-mediator", "org.atricore.idbus.capabilities.idconfirmation.main.IdentityConfirmationMediator");

        // logMessages
        setPropertyValue(idcMediator, "logMessages", true);

        // identity confirmation initiation UI page
        setPropertyValue(idcMediator, "tokenSharingConfirmationUILocation",
                resolveUiLocationPath(appliance) + "/" + provider.getName().toUpperCase() + "/IDCONF/INITIATE");

        // OAuth2 stuff required for issuing access tokens
        setPropertyValue(idcMediator, "oauth2ClientId", "internal");
        setPropertyValue(idcMediator, "oauth2ClientSecret", "abc123");
        setPropertyValue(idcMediator, "oauth2AuthorizationServerEndpoint",
                      resolveLocationUrl(provider) + "/OAUTH2/TOKEN/SOAP");

        // artifactQueueManager
        // setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(idcMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(idcMediator, "bindingFactory", newAnonymousBean("org.atricore.idbus.capabilities.idconfirmation.main.IdentityConfirmationBindingFactory"));

        List<Bean> ccLogBuilders = new ArrayList<Bean>();
        ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean ccLogger = newBean(idpBeans, idConfChannelBeanName + "-mediation-logger", DefaultMediationLogger.class.getName());
        setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire.cc1");
        setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

        // logger
        setPropertyBean(idcMediator, "logger", ccLogger);

        // identityMediator
        setPropertyRef(identityConfirmationChannelBean, "identityMediator", idcMediator.getName());

        // provider
        setPropertyRef(identityConfirmationChannelBean, "federatedProvider", idpBean.getName());

        // unitContainer
        setPropertyRef(identityConfirmationChannelBean, "unitContainer", provider.getIdentityAppliance().getName() + "-container");

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(baseBeans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            addPropertyBeansAsRefs(mu, "channels", identityConfirmationChannelBean);
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

    }
}
