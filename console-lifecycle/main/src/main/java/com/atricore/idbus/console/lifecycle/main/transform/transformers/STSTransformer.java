package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.emitter.SamlR2SecurityTokenEmitter;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
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
                SamlR2SecurityTokenEmitter.class.getName());
        setPropertyValue(stsEmitter, "id", stsEmitter.getName());

        // identityPlanRegistry
        setPropertyRef(stsEmitter, "identityPlanRegistry", "identity-plans-registry");


        Collection<Bean> mediators = getBeansOfType(idpBeans, SSOIDPMediator.class.getName());

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

        Bean legacyAuthenticator = newBean(idpBeans, "authenticator", AuthenticatorImpl.class.getName());
        List<Ref> authnSchemes = new ArrayList<Ref>();

        if (provider.getAuthenticationMechanisms().size() < 1)
            throw new TransformException("No Authentication Mechanism defined for " + provider.getName());

        setPropertyRefs(legacyAuthenticator, "authenticationSchemes", authnSchemes);
        
        // ----------------------------------------
        // Atricore Authenticators
        // ----------------------------------------

        // Create empty list with all authenticators ...
        List<Bean> authenticators = new ArrayList<Bean>();

        // authenticators
        setPropertyAsBeans(sts, "authenticators", authenticators);

        // artifactQueueManager
        setPropertyRef(sts, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
    }
}
