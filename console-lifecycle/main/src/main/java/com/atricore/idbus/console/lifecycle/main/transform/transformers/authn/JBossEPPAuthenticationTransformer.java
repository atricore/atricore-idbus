package com.atricore.idbus.console.lifecycle.main.transform.transformers.authn;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;


/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class JBossEPPAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(JBossEPPAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof JBossEPPAuthentication))
            return false;

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        JBossEPPAuthentication jbeppauthn = (JBossEPPAuthentication) event.getData();
        AuthenticationService authnService = jbeppauthn.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof JBossEPPAuthenticationService;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");
        JBossEPPAuthentication jbosseppauth = (JBossEPPAuthentication) event.getData();

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        IdApplianceTransformationContext ctx = event.getContext();

        JBossEPPAuthenticationService jbosseppas = (JBossEPPAuthenticationService) jbosseppauth.getDelegatedAuthentication().getAuthnService();

        // Authentication scheme

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Authentication Scheme for IdP " + idpBean.getName());

            Bean gateinBindCredStore = newBean(idpBeans, normalizeBeanName(jbosseppauth.getName() + "-credential-store"),
                    "org.atricore.idbus.idojos.gateinidentitystore.GateInBindIdentityStore");

            setPropertyValue(gateinBindCredStore, "gateInHost", jbosseppas.getHost());
            setPropertyValue(gateinBindCredStore, "gateInPort", jbosseppas.getPort());
            setPropertyValue(gateinBindCredStore, "gateInContext", jbosseppas.getContext());

            Bean bindAuthScheme = newBean(idpBeans, normalizeBeanName(jbosseppauth.getName()),
                    "org.atricore.idbus.kernel.main.authn.scheme.BindUsernamePasswordAuthScheme");

            // priority
            setPropertyValue(bindAuthScheme, "priority", jbosseppauth.getPriority() + "");

            // Auth scheme name cannot be changed!
            setPropertyValue(bindAuthScheme, "name", "basic-authentication");
            setPropertyRef(bindAuthScheme, "credentialStore", gateinBindCredStore.getName());
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        JBossEPPAuthentication jbosseppAuth = (JBossEPPAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Bean gateInAuthnBean = getBean(idpBeans, normalizeBeanName(jbosseppAuth.getName()));
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Wire basic authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            Bean legacyAuthenticator = authenticators.iterator().next();
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", gateInAuthnBean);

            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            Bean twoFactorAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.sts.main.authenticators.BasicSecurityTokenAuthenticator");
            setPropertyRef(twoFactorAuthenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", twoFactorAuthenticator);

        }

        return gateInAuthnBean;
    }

}

