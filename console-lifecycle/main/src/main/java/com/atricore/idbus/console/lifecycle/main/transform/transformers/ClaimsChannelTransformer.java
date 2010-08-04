package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProviderChannel;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.atricore.idbus.capabilities.samlr2.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.capabilities.samlr2.main.binding.logging.SamlR2LogMessageBuilder;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsMediator;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
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
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newAnonymousBean;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ClaimsChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(ClaimsChannelTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof ServiceProviderChannel;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
        IdentityProvider provider = (IdentityProvider) event.getContext().getParentNode();

        if (logger.isTraceEnabled())
            logger.trace("Generating Claims Channel Beans for SP Channel " + spChannel.getName()  + " of IdP " + provider.getName());

        Bean spChannelBean = (Bean) event.getContext().get("spChannelBean");

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();
        
        // ----------------------------------------
        // Claims Channel
        // ----------------------------------------

        Bean claimsChannelBean = newBean(idpBeans, spChannelBean.getName() + "-claims-channel", ClaimChannelImpl.class);

        // name
        setPropertyValue(claimsChannelBean, "name", claimsChannelBean.getName());

        // location
        String locationUrl = resolveLocationUrl(provider) + "/" + idpBean.getName().toUpperCase() + "/CC";
        setPropertyValue(claimsChannelBean, "location", locationUrl);

        // endpoints
        List<Bean> ccEndpoints = new ArrayList<Bean>();

        Bean ccPwdArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ccPwdArtifact.setName(spChannelBean.getName() + "-cc-pwd-artifact");
        setPropertyValue(ccPwdArtifact, "name", ccPwdArtifact.getName());
        setPropertyValue(ccPwdArtifact, "binding", SamlR2Binding.SSO_ARTIFACT.getValue());
        setPropertyValue(ccPwdArtifact, "location", "/IDBUS/PWD/ARTIFACT");
        setPropertyValue(ccPwdArtifact, "responseLocation", "/IDBUS/PWD/POST-RESP");
        setPropertyValue(ccPwdArtifact, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        ccEndpoints.add(ccPwdArtifact);

        Bean ccPwdPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
        ccPwdPost.setName(spChannelBean.getName() + "-cc-pwd-post");
        setPropertyValue(ccPwdPost, "name", ccPwdPost.getName());
        setPropertyValue(ccPwdPost, "binding", SamlR2Binding.SSO_POST.getValue());
        setPropertyValue(ccPwdPost, "location", "/IDBUS/PWD/POST");
        setPropertyValue(ccPwdPost, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        ccEndpoints.add(ccPwdPost);

        setPropertyAsBeans(claimsChannelBean, "endpoints", ccEndpoints);

        // ----------------------------------------
        // Claims Mediator
        // ----------------------------------------
        Bean ccMediator = newBean(idpBeans, spChannelBean.getName() + "-samlr2-claims-mediator", SamlR2ClaimsMediator.class);

        // logMessages
        setPropertyValue(ccMediator, "logMessages", true);

        // basicAuthnUILocation
        setPropertyValue(ccMediator, "basicAuthnUILocation", resolveLocationBaseUrl(provider) + "/idbus-ui/claims/username-password.do");

        // artifactQueueManager
        setPropertyRef(ccMediator, "artifactQueueManager", event.getContext().getCurrentModule().getId() + "-aqm");
        
        // bindingFactory
        setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

        List<Bean> ccLogBuilders = new ArrayList<Bean>();
        ccLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean ccLogger = newBean(idpBeans, spChannelBean.getName() + "-cc-mediation-logger", DefaultMediationLogger.class.getName());
        setPropertyValue(ccLogger, "category", "org.atricore.idbus.mediation.wire.cc1");
        setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

        // logger
        setPropertyBean(ccMediator, "logger", ccLogger);

        // errorUrl
        setPropertyValue(ccMediator, "errorUrl", resolveLocationBaseUrl(provider) + "/idbus-ui/error.do");

        // identityMediator
        setPropertyRef(claimsChannelBean, "identityMediator", ccMediator.getName());

        // provider
        setPropertyRef(claimsChannelBean, "provider", idpBean.getName());

        // unitContainer
        setPropertyRef(claimsChannelBean, "unitContainer", event.getContext().getCurrentModule().getId() + "-container");
        
        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(baseBeans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();
            addPropertyBeansAsRefs(mu, "channels", claimsChannelBean);
        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }
    }
}
