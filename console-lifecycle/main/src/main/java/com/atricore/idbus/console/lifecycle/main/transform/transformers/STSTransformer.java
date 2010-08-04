package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ServiceProviderChannel;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class STSTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(STSTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof ServiceProviderChannel;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
        IdentityProvider provider = (IdentityProvider) event.getContext().getParentNode();
        Bean spChannelBean = (Bean) event.getContext().get("spChannelBean");

        if (logger.isTraceEnabled())
            logger.trace("Generating STS Beans for SP Channel " + spChannel.getName()  + " of IdP " + provider.getName());

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();
        
        // ----------------------------------------
        // STS
        // ----------------------------------------
        Bean sts = newBean(idpBeans, spChannelBean.getName() + "-sts",
                "org.atricore.idbus.capabilities.sts.main.WSTSecurityTokenService");

        // ----------------------------------------
        // Emitters
        // ----------------------------------------
        Bean stsEmitter = newBean(idpBeans,
                spChannelBean.getName() + "-samlr2-assertion-emitter",
                "org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmitter");
        setPropertyValue(stsEmitter, "id", stsEmitter.getName());

        Bean stsSecTkn2AssertionPlan = newBean(idpBeans,
                spChannelBean.getName() + "-samlr2-sectoken-to-authnassertion-plan",
                "org.atricore.idbus.capabilities.samlr2.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan");
        if (spChannel.getIdentityVault() != null) {
            setPropertyRef(stsSecTkn2AssertionPlan, "identityManager", idpBean.getName() + "-identity-manager");
        }
        setPropertyRef(stsSecTkn2AssertionPlan, "bpmsManager", "bpms-manager");

        // identityPlan
        setPropertyRef(stsEmitter, "identityPlan", stsSecTkn2AssertionPlan.getName());

        String mediatorName = getPropertyRef(spChannelBean, "identityMediator");
        Bean mediatorBean = getBean(idpBeans, mediatorName);
        if (mediatorBean == null)
            throw new TransformException("No mediator found for name " + mediatorName);

        String signerName = getPropertyRef(mediatorBean, "signer");
        Bean signerBean = getBean(idpBeans, signerName);
        if (signerBean != null) {
            // signer
            setPropertyRef(stsEmitter, "signer", signerBean.getName());
        }

        List<Bean> emitters = new ArrayList<Bean>();
        emitters.add(stsEmitter);
        setPropertyAsBeans(sts, "emitters", emitters);

        // ----------------------------------------
        // JOSSO Legacy authenticator
        // ----------------------------------------
        Bean legacyAuthenticator = newAnonymousBean("org.atricore.idbus.kernel.main.authn.AuthenticatorImpl");
        List<Ref> authnSchemes = new ArrayList<Ref>();
        Ref authnScheme = new Ref();
        authnScheme.setBean("basic-authentication");
        authnSchemes.add(authnScheme);
        setPropertyRefs(legacyAuthenticator, "authenticationSchemes", authnSchemes);
        
        // ----------------------------------------
        // Atricore Authenticator
        // ----------------------------------------

        // Default Authenticator
        Bean stsAuthn = newAnonymousBean("org.atricore.idbus.capabilities.sts.main.DefaultSecurityTokenAuthenticator");
        setPropertyBean(stsAuthn, "authenticator", legacyAuthenticator);

        List<Bean> authenticators = new ArrayList<Bean>();
        authenticators.add(stsAuthn);

        // authenticators
        setPropertyAsBeans(sts, "authenticators", authenticators);

        // artifactQueueManager
        /*String aqmName = event.getContext().getCurrentModule().getId() + "-aqm";
        Beans beansOsgi = (Beans) event.getContext().get("beansOsgi");
        Bean aqmBean = getBean(beansOsgi, aqmName);
        if (aqmBean == null) {
            throw new TransformException("No Artifact Queue Manager defined as " + aqmName);
        }*/
        setPropertyRef(sts, "artifactQueueManager", event.getContext().getCurrentModule().getId() + "-aqm");
    }
}
