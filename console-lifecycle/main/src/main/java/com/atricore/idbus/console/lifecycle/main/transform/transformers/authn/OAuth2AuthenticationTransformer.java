package com.atricore.idbus.console.lifecycle.main.transform.transformers.authn;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.sso.STSTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.AESTokenEncrypter;
import org.atricore.idbus.capabilities.oauth2.common.HMACTokenSigner;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2AccessTokenEmitter;
import org.atricore.idbus.capabilities.oauth2.main.sso.OAuth2AccessTokenAuthenticator;
import org.atricore.idbus.capabilities.oauth2.main.sso.OAuth2AuthenticationScheme;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OAuth2AuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(OAuth2AuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        // Only work for Local IdPs with OAuth 2.0 support enabled
        return event.getData() instanceof IdentityProvider &&
                !((IdentityProvider)event.getData()).isRemote() &&
                ((IdentityProvider)event.getData()).isOauth2Enabled();
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider provider = (IdentityProvider) event.getData();

        if (logger.isTraceEnabled())
            logger.trace("Generating OAuth2 Authentication for IdP " + provider.getName());


        // ----------------------------------------
        // Get IDP Bean
        // ----------------------------------------
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Authentication scheme

        if (logger.isTraceEnabled())
            logger.trace("Generating OAuth2 Authentication Scheme for IdP " + idpBean.getName());

        Bean oauthAuthn = newBean(idpBeans, normalizeBeanName(idpBean.getName() + "-oauth2-authn"), OAuth2AuthenticationScheme.class);

        // priority
        setPropertyValue(oauthAuthn, "priority", 0 + "");

        // Auth scheme name cannot be changed!
        setPropertyValue(oauthAuthn, "name", "oauth2-authentication");
        setPropertyValue(oauthAuthn, "sharedSecret", provider.getOauth2Key());
        setPropertyValue(oauthAuthn, "signKey", provider.getOauth2Key());
        setPropertyValue(oauthAuthn, "encryptKey", provider.getOauth2Key());
        setPropertyValue(oauthAuthn, "accessTokenValidityInterval", "300");

    }

    public Object after(TransformEvent event) throws TransformException {
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        IdentityProvider provider = (IdentityProvider) event.getData();

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        Bean oauth2AuthnBean = getBean(idpBeans, normalizeBeanName(idpBean.getName() + "-oauth2-authn"));

        // Wire oauth2 authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            Bean legacyAuthenticator = authenticators.iterator().next();
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", oauth2AuthnBean);

            // Add oauth2 STS authenticator
            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            Bean oauth2Authenticator = newAnonymousBean(OAuth2AccessTokenAuthenticator.class.getName());
            setPropertyRef(oauth2Authenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", oauth2Authenticator);

        }

        return oauth2AuthnBean;
    }


}
