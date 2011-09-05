package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SamlR2BindingFactory;
import org.atricore.idbus.capabilities.sso.main.binding.logging.SamlR2LogMessageBuilder;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BasicAuthenticationClaimsChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(BasicAuthenticationClaimsChannelTransformer.class);

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
            // Basic and Bind are prity mutch the same from a 'claiming' point of view
            if (a instanceof BasicAuthentication ||
                a instanceof BindAuthentication)
                return true;
        }

        // None of the authn mechanisms is supported!
        return false;
    }

    /**
     *  TODO : Support multiple claim channels per IDP!
     * @param event
     * @throws TransformException
     */
    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider provider = (IdentityProvider) event.getData();
        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        if (logger.isTraceEnabled())
            logger.trace("Generating Claims Channel Beans for IDP Channel " + provider.getName());

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


        Bean claimsChannelBean = newBean(idpBeans, idpBean.getName() + "-claims-channel", ClaimChannelImpl.class);

        // name
        setPropertyValue(claimsChannelBean, "name", claimsChannelBean.getName());

        // location
        String locationUrl = resolveLocationUrl(provider) + "/CC";
        setPropertyValue(claimsChannelBean, "location", locationUrl);

        // endpoints
        List<Bean> ccEndpoints = new ArrayList<Bean>();


        for (AuthenticationMechanism authnMechanism : provider.getAuthenticationMechanisms()) {
            // Bind authn is a variant of basic authn
            if (authnMechanism instanceof BasicAuthentication ||
                authnMechanism instanceof BindAuthentication) {

                Bean ccPwdArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccPwdArtifact.setName(idpBean.getName() + "-cc-pwd-artifact");
                setPropertyValue(ccPwdArtifact, "name", ccPwdArtifact.getName());
                setPropertyValue(ccPwdArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
                setPropertyValue(ccPwdArtifact, "location", "/PWD/ARTIFACT");
                setPropertyValue(ccPwdArtifact, "responseLocation", "/PWD/POST-RESP");
                setPropertyValue(ccPwdArtifact, "type", AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccPwdArtifact);

                Bean ccPwdPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccPwdPost.setName(idpBean.getName() + "-cc-pwd-post");
                setPropertyValue(ccPwdPost, "name", ccPwdPost.getName());
                setPropertyValue(ccPwdPost, "binding", SSOBinding.SSO_POST.getValue());
                setPropertyValue(ccPwdPost, "location", "/PWD/POST");
                setPropertyValue(ccPwdPost, "type", AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccPwdPost);

                Bean ccSpPwdLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccSpPwdLocal.setName(idpBean.getName() + "-cc-sppwd-local");
                setPropertyValue(ccSpPwdLocal, "name", ccSpPwdLocal.getName());
                setPropertyValue(ccSpPwdLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
                setPropertyValue(ccSpPwdLocal, "location",  "local://" + claimsChannelBean.getName().toUpperCase() + "/CC/SPPWD/LOCAL");
                setPropertyValue(ccSpPwdLocal, "type", AuthnCtxClass.ATC_SP_PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccSpPwdLocal);

            }
        }

        setPropertyAsBeans(claimsChannelBean, "endpoints", ccEndpoints);

        // ----------------------------------------
        // Claims Mediator
        // ----------------------------------------
        Bean ccMediator = newBean(idpBeans, idpBean.getName() + "-basicauthn-claims-mediator", SSOClaimsMediator.class);

        // logMessages
        setPropertyValue(ccMediator, "logMessages", true);

        // basicAuthnUILocation
        setPropertyValue(ccMediator, "basicAuthnUILocation", resolveLocationBaseUrl(provider) + "/idbus-ui/claims/username-password.do");

        // artifactQueueManager
        setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        
        // bindingFactory
        setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

        List<Bean> ccLogBuilders = new ArrayList<Bean>();
        ccLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean ccLogger = newBean(idpBeans, idpBean.getName() + "-cc-mediation-logger", DefaultMediationLogger.class.getName());
        setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire.cc1");
        setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

        // logger
        setPropertyBean(ccMediator, "logger", ccLogger);

        // errorUrl
        setPropertyValue(ccMediator, "errorUrl", resolveLocationBaseUrl(provider) + "/idbus-ui/error.do");

        // warningUrl
        setPropertyValue(ccMediator, "warningUrl", resolveLocationBaseUrl(provider) + "/idbus-ui/warn/policy-enforcement.do");

        // identityMediator
        setPropertyRef(claimsChannelBean, "identityMediator", ccMediator.getName());

        // provider
        setPropertyRef(claimsChannelBean, "provider", idpBean.getName());

        // unitContainer
        setPropertyRef(claimsChannelBean, "unitContainer", provider.getIdentityAppliance().getName() + "-container");
        
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
