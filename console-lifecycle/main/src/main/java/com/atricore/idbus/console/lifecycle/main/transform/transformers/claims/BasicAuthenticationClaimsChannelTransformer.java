package com.atricore.idbus.console.lifecycle.main.transform.transformers.claims;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
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
            // Basic and Bind are pretty much the same from a 'claiming' point of view
            if ((a instanceof BasicAuthentication && ((BasicAuthentication)a).isEnabled() ) ||
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
            logger.trace("Generating Claim Channel Beans for IDP Channel " + provider.getName());

        Beans baseBeans = (Beans) event.getContext().get("beans");
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        String claimChannelBeanName = normalizeBeanName(idpBean.getName() + "-basic-authn-claim-channel");


        if (getBean(idpBeans, claimChannelBeanName) != null) {
            // We already created the basic authentication claim channel ..
            if (logger.isDebugEnabled())
                logger.debug("Basic authentication claim channel already created");
            return;
        }
        
        // ----------------------------------------
        // Claim Channel
        // ----------------------------------------
        boolean overrideBrinding = provider.getUserDashboardBranding() != null &&
                !provider.getUserDashboardBranding().equals(appliance.getIdApplianceDefinition().getUserDashboardBranding().getId());

        Bean claimChannelBean = null;
        for (AuthenticationMechanism authnMechanism : provider.getAuthenticationMechanisms()) {

            // Bind authn is a variant of basic authn
            if (authnMechanism instanceof BasicAuthentication ||
                authnMechanism instanceof BindAuthentication) {

                if (claimChannelBean != null) {
                    int currentPriority = Integer.parseInt(getPropertyValue(claimChannelBean, "priority"));
                    if (authnMechanism.getPriority() < currentPriority)
                        setPropertyValue(claimChannelBean, "priority",  authnMechanism.getPriority() + "");
                    continue;
                }

                // We will generate ONE and only ONE claim channel for retrieving basic authn credentials.
                claimChannelBean = newBean(idpBeans, claimChannelBeanName, ClaimChannelImpl.class);
                setPropertyValue(claimChannelBean, "priority",  authnMechanism.getPriority() + "");

                // name
                setPropertyValue(claimChannelBean, "name", claimChannelBean.getName());

                // location
                String locationUrl = resolveLocationUrl(provider) + "/CC/BASIC" ;
                setPropertyValue(claimChannelBean, "location", locationUrl);

                // endpoints
                List<Bean> ccEndpoints = new ArrayList<Bean>();

                Bean ccPwdArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccPwdArtifact.setName(claimChannelBeanName + "-cc-pwd-artifact");
                setPropertyValue(ccPwdArtifact, "name", ccPwdArtifact.getName());
                setPropertyValue(ccPwdArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
                setPropertyValue(ccPwdArtifact, "location", "/PWD/ARTIFACT");
                setPropertyValue(ccPwdArtifact, "responseLocation", "/PWD/POST-RESP");
                setPropertyValue(ccPwdArtifact, "type", AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccPwdArtifact);

                Bean ccPwdPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccPwdPost.setName(claimChannelBeanName + "-cc-pwd-post");
                setPropertyValue(ccPwdPost, "name", ccPwdPost.getName());
                setPropertyValue(ccPwdPost, "binding", SSOBinding.SSO_POST.getValue());
                setPropertyValue(ccPwdPost, "location", "/PWD/POST");
                setPropertyValue(ccPwdPost, "type", AuthnCtxClass.PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccPwdPost);

                Bean ccSpPwdLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccSpPwdLocal.setName(claimChannelBeanName + "-cc-sppwd-local");
                setPropertyValue(ccSpPwdLocal, "name", ccSpPwdLocal.getName());
                setPropertyValue(ccSpPwdLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
                setPropertyValue(ccSpPwdLocal, "location",  "local://" + claimChannelBean.getName().toUpperCase() + "/CC/SPPWD/LOCAL");
                setPropertyValue(ccSpPwdLocal, "type", AuthnCtxClass.ATC_SP_PASSWORD_AUTHN_CTX.getValue());
                ccEndpoints.add(ccSpPwdLocal);


                if (authnMechanism instanceof BasicAuthentication) {
                    ImpersonateUserPolicy impUsrPolicy = ((BasicAuthentication)authnMechanism).getImpersonateUserPolicy();
                    if (impUsrPolicy != null && !impUsrPolicy.getImpersonateUserPolicyType().equals(ImpersonateUserPolicyType.DISABLED)) {

                        // Enable endpoints for user impersonation

                        Bean ccSpImpersonateLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
                        ccSpImpersonateLocal.setName(claimChannelBeanName + "-cc-spimpersonate-local");
                        setPropertyValue(ccSpImpersonateLocal, "name", ccSpImpersonateLocal.getName());
                        setPropertyValue(ccSpImpersonateLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
                        setPropertyValue(ccSpImpersonateLocal, "location",  "local://" + claimChannelBean.getName().toUpperCase() + "/CC/SPIMPERSONATE/LOCAL");
                        setPropertyValue(ccSpImpersonateLocal, "type", AuthnCtxClass.ATC_SP_IMPERSONATE_AUTHN_CTX.getValue());
                        ccEndpoints.add(ccSpImpersonateLocal);

                    }
                }

                setPropertyAsBeans(claimChannelBean, "endpoints", ccEndpoints);

                // ----------------------------------------
                // Claim Channel Mediator
                // ----------------------------------------
                Bean ccMediator = newBean(idpBeans, claimChannelBeanName + "-claim-mediator", SSOClaimsMediator.class);

                // logMessages
                setPropertyValue(ccMediator, "logMessages", true);

                // basicAuthnUILocation
                setPropertyValue(ccMediator, "basicAuthnUILocation",
                        resolveUiLocationPath(appliance) +
                                (overrideBrinding ? "/" + provider.getName().toUpperCase() : "" ) + "/SSO/LOGIN/SIMPLE");

                // artifactQueueManager
                //setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
                setPropertyRef(ccMediator, "artifactQueueManager", "artifactQueueManager");

                // bindingFactory
                setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean(SamlR2BindingFactory.class));

                List<Bean> ccLogBuilders = new ArrayList<Bean>();
                ccLogBuilders.add(newAnonymousBean(SamlR2LogMessageBuilder.class));
                ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
                ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

                Bean ccLogger = newBean(idpBeans, claimChannelBeanName + "-mediation-logger", DefaultMediationLogger.class.getName());
                setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire." + claimChannelBeanName.toLowerCase());
                setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

                // logger
                setPropertyBean(ccMediator, "logger", ccLogger);

                // errorUrl
                setPropertyValue(ccMediator, "errorUrl", resolveUiErrorLocation(appliance));

                // warningUrl
                setPropertyValue(ccMediator, "warningUrl", resolveUiWarningLocation(appliance));

                // identityMediator
                setPropertyRef(claimChannelBean, "identityMediator", ccMediator.getName());

                // provider
                setPropertyRef(claimChannelBean, "provider", idpBean.getName());

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
