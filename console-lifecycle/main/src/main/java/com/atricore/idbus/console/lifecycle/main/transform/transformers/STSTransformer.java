package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.BasicAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.scheme.UsernamePasswordAuthScheme;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

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
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider provider = (IdentityProvider) event.getData();

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

        // ----------------------------------------
        // Emitters
        // ----------------------------------------
        Bean stsEmitter = newBean(idpBeans,
                idpBean.getName() + "-samlr2-assertion-emitter",
                "org.atricore.idbus.capabilities.samlr2.main.emitter.SamlR2SecurityTokenEmitter");
        setPropertyValue(stsEmitter, "id", stsEmitter.getName());

        Bean stsSecTkn2AssertionPlan = newBean(idpBeans,
                idpBean.getName() + "-samlr2-sectoken-to-authnassertion-plan",
                "org.atricore.idbus.capabilities.samlr2.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan");
        // TODO RETROFIT  :
        /*
        if (spChannel.getIdentityVault() != null) {
            setPropertyRef(stsSecTkn2AssertionPlan, "identityManager", idpBean.getName() + "-identity-manager");
        } */
        setPropertyRef(stsSecTkn2AssertionPlan, "bpmsManager", "bpms-manager");

        // identityPlan
        setPropertyRef(stsEmitter, "identityPlan", stsSecTkn2AssertionPlan.getName());


        Collection<Bean> mediators = getBeansOfType(idpBeans, "org.atricore.idbus.capabilities.samlr2.main.idp.SamlR2IDPMediator");

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
        Bean legacyAuthenticator = newAnonymousBean("org.atricore.idbus.kernel.main.authn.AuthenticatorImpl");
        List<Ref> authnSchemes = new ArrayList<Ref>();

        for (AuthenticationMechanism authn : provider.getAuthenticationMechanisms()) {

            if (authn instanceof BasicAuthentication) {

                BasicAuthentication basicAuthn = (BasicAuthentication) authn;
                Bean basicAuthnBean = newBean(idpBeans, normalizeBeanName(provider.getName()) + "-basic-authn", UsernamePasswordAuthScheme.class);

                setPropertyValue(basicAuthnBean, "name", basicAuthnBean.getName());
                setPropertyValue(basicAuthnBean, "hashAlgorithm", basicAuthn.getHashAlgorithm());
                setPropertyValue(basicAuthnBean, "hashEncoding", basicAuthn.getHashEncoding());
                setPropertyValue(basicAuthnBean, "ignorePasswordCase", false); // Dangerous
                setPropertyValue(basicAuthnBean, "ignoreUserCase", basicAuthn.isIgnoreUsernameCase());

                setPropertyRef(basicAuthnBean, "credentialStore", provider.getName() + "-identity-store");
                setPropertyBean(basicAuthnBean, "credentialStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));

                Ref basicAuthnRef = new Ref();
                basicAuthnRef.setBean(basicAuthnBean.getName());

                authnSchemes.add(basicAuthnRef);

            } else {
                throw new TransformException("Unsupported Authentication Scheme Type [" + authn.getName() + "] " +
                        authn.getClass().getSimpleName());
            }

        }

        if (authnSchemes.size() < 1)
            throw new TransformException("No Authentication Mechanism defined for " + provider.getName());

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
