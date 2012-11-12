package com.atricore.idbus.console.lifecycle.main.transform.transformers.authn;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.io.Serializable;
import java.util.*;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;


/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class DominoAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(DominoAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof DominoAuthentication))
            return false;

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        DominoAuthentication da = (DominoAuthentication) event.getData();
        AuthenticationService authnService = da.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof DominoAuthenticationService;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");
        DominoAuthentication da = (DominoAuthentication) event.getData();

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        IdApplianceTransformationContext ctx = event.getContext();

        DominoAuthenticationService das = (DominoAuthenticationService) da.getDelegatedAuthentication().getAuthnService();

        // Authentication scheme

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Domino Authentication Scheme for IdP " + idpBean.getName());

        Bean dominoAuthn = newBean(idpBeans, normalizeBeanName(da.getName()), "org.atricore.idbus.capabilities.domino.main.Domino7AuthenticationScheme");

        // priority
        setPropertyValue(dominoAuthn, "priority", da.getPriority() + "");

        // Auth scheme name cannot be changed!
        setPropertyValue(dominoAuthn, "name", "domino-authentication");

        setPropertyValue(dominoAuthn, "dominoServer", das.getServerUrl());

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        DominoAuthentication dominoAuthn = (DominoAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Bean dominoAuthnBean = getBean(idpBeans, normalizeBeanName(dominoAuthn.getName()));
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Wire basic authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            // Wire domino authentication scheme to authenticator instance
            Bean legacyAuthenticator = authenticators.iterator().next();
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", dominoAuthnBean);

            // Add Domino Authenticator
            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            Bean twoFactorAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.domino.main.authenticators.DominoSecurityTokenAuthenticator");
            setPropertyRef(twoFactorAuthenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", twoFactorAuthenticator);

        }

        return dominoAuthnBean;
    }

}

