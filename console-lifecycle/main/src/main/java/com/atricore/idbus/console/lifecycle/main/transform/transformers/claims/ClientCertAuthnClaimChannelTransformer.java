package com.atricore.idbus.console.lifecycle.main.transform.transformers.claims;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ClientCertAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ClientCertAuthnService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/5/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCertAuthnClaimChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(ClientCertAuthnClaimChannelTransformer.class);

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
            if (a instanceof ClientCertAuthentication)
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

        // ----------------------------------------
        // Claims Channel, we have a single claim channel for spnego, no matter how many WIA instances are.
        // ----------------------------------------
        String claimChannelBeanName = normalizeBeanName(idpBean.getName() + "-clientcert-authn-claim-channel");
        if (getBean(idpBeans, claimChannelBeanName) != null) {
            // We already created the basic authentication claim channel ..
            if (logger.isDebugEnabled())
                logger.debug("Client-Certificate authentication claim channel already created");
            return;
        }

        Bean claimChannelBean = null;

        for (AuthenticationMechanism authnMechanism : provider.getAuthenticationMechanisms()) {

            // Bind authn is a variant of basic authn
            if (authnMechanism instanceof ClientCertAuthentication) {

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

                Bean ccClientCert = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ccClientCert.setName(idpBean.getName() + "-cc-clientcert");
                setPropertyValue(ccClientCert, "name", ccClientCert.getName());
                setPropertyValue(ccClientCert, "binding", "urn:org:atricore:idbus:clientcert:bindings:HTTP-INITIATION");
                setPropertyValue(ccClientCert, "location", "/SPNEGO/INITIATE");
                setPropertyValue(ccClientCert, "type", "urn:oasis:names:tc:SAML:2.0:ac:classes:SSL");
                ccEndpoints.add(ccClientCert);

                setPropertyAsBeans(claimChannelBean, "endpoints", ccEndpoints);

                // ----------------------------------------
                // Claims Mediator
                // ----------------------------------------
                // TODO : Do not force spnego on name
                Bean ccMediator = newBean(idpBeans, claimChannelBeanName + "-mediator", "org.atricore.idbus.capabilities.clientcertauthn.ClientCertAuthnMediator");

                // Realm
                // setPropertyValue(ccMediator, "realm", authnMechanism.getDelegatedAuthentication().getName());

                // Service Principal Name
                ClientCertAuthnService clientCert = (ClientCertAuthnService) authnMechanism.getDelegatedAuthentication().getAuthnService();

                // String spn = WindowsIntegratedAuthenticationTransformer.buildSpn(clientCert);

                // setPropertyValue(ccMediator, "principal", spn);

                // logMessages
                setPropertyValue(ccMediator, "logMessages", true);

                // artifactQueueManager
                // setPropertyRef(ccMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
                setPropertyRef(ccMediator, "artifactQueueManager", "artifactQueueManager");

                // bindingFactory
                setPropertyBean(ccMediator, "bindingFactory", newAnonymousBean("org.atricore.idbus.capabilities.spnego.SpnegoBindingFactory"));

                List<Bean> ccLogBuilders = new ArrayList<Bean>();
                ccLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
                ccLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

                Bean ccLogger = newBean(idpBeans, claimChannelBeanName + "-mediation-logger", DefaultMediationLogger.class.getName());
                setPropertyValue(ccLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire.cc1");
                setPropertyAsBeans(ccLogger, "messageBuilders", ccLogBuilders);

                // logger
                setPropertyBean(ccMediator, "logger", ccLogger);

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
