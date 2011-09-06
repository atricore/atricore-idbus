package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsIntegratedAuthentication;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.addPropertyBeansAsRefs;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIntegratedAuthenticationClaimsChannelTransformer extends AbstractTransformer {

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

        if (idp.getDelegatedAuthentication() != null &&
            idp.getDelegatedAuthentication().getAuthnService() != null &&
            idp.getDelegatedAuthentication().getAuthnService() instanceof WindowsIntegratedAuthentication)

            return true;

        // None of the authn mechanisms is supported!
        return false;
    }

    /**
     *  TODO : Support multiple claim channels per IDP!
     * @param event
     * @throws com.atricore.idbus.console.lifecycle.main.exception.TransformException
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
            if (authnMechanism instanceof WindowsAuthentication) {

                Bean ccWiaSpnegoArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccWiaSpnegoArtifact.setName(idpBean.getName() + "-cc-spnego-artifact");
                setPropertyValue(ccWiaSpnegoArtifact, "name", ccWiaSpnegoArtifact.getName());
                setPropertyValue(ccWiaSpnegoArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
                setPropertyValue(ccWiaSpnegoArtifact, "location", "/SPNEGO/HTTP/ARTIFACT");
                setPropertyValue(ccWiaSpnegoArtifact, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos");
                ccEndpoints.add(ccWiaSpnegoArtifact);

                Bean ccWiaSpnegoHttpInit = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccWiaSpnegoHttpInit.setName(idpBean.getName() + "-cc-spnego-initiator");
                setPropertyValue(ccWiaSpnegoHttpInit, "name", ccWiaSpnegoHttpInit.getName());
                setPropertyValue(ccWiaSpnegoHttpInit, "binding", "urn:org:atricore:idbus:spnego:bindings:HTTP-INITIATION");
                setPropertyValue(ccWiaSpnegoHttpInit, "location", "/SPNEGO/HTTP/INITIATE");
                //setPropertyValue(ccWiaSpnegoHttpInit, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos");
                ccEndpoints.add(ccWiaSpnegoHttpInit);

                Bean ccWiaSpnegoHttpNegotiate = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccWiaSpnegoHttpNegotiate.setName(idpBean.getName() + "-cc-spnego-negotiatior");
                setPropertyValue(ccWiaSpnegoHttpNegotiate, "name", ccWiaSpnegoHttpNegotiate.getName());
                setPropertyValue(ccWiaSpnegoHttpNegotiate, "binding", "urn:org:atricore:idbus:spnego:bindings:HTTP-NEGOTIATION");
                setPropertyValue(ccWiaSpnegoHttpNegotiate, "location", "/SPNEGO/HTTP/NEGOTIATE");
                setPropertyValue(ccWiaSpnegoHttpNegotiate, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos");
                ccEndpoints.add(ccWiaSpnegoHttpNegotiate);


            }
        }

        setPropertyAsBeans(claimsChannelBean, "endpoints", ccEndpoints);

        // ----------------------------------------
        // Claims Mediator
        // ----------------------------------------
        // TODO : Do not force spnego on name
        Bean ccMediator = newBean(idpBeans, idpBean.getName() + "-spnego-claims-mediator", "org.atricore.idbus.capabilities.spnego.SpnegoMediator");

        // Realm
        setPropertyValue(ccMediator, "realm", provider.getDelegatedAuthentication().getName());

        // Service Principal Name
        WindowsIntegratedAuthentication wia = (WindowsIntegratedAuthentication) provider.getDelegatedAuthentication().getAuthnService();
        String spn = WindowsIntegratedAuthenticationTransformer.buildSpn(wia);

        setPropertyValue(ccMediator, "principal", spn);

        // logMessages
        setPropertyValue(ccMediator, "logMessages", true);

        // artifactQueueManager
        setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");

        // bindingFactory
        setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean("org.atricore.idbus.capabilities.spnego.SpnegoBindingFactory"));

        List<Bean> ccLogBuilders = new ArrayList<Bean>();
        ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean ccLogger = newBean(idpBeans, idpBean.getName() + "-cc-mediation-logger", DefaultMediationLogger.class.getName());
        setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire.cc1");
        setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

        // logger
        setPropertyBean(ccMediator, "logger", ccLogger);

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
