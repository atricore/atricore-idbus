package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.BasicAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ImpersonateUserPolicy;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ImpersonateUserPolicyType;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.authn.scheme.UsernamePasswordAuthScheme;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

public class BasicAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(BasicAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {
        return event.getData() instanceof BasicAuthentication &&
               event.getContext().getParentNode() instanceof IdentityProvider;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        BasicAuthentication basicAuthn = (BasicAuthentication) event.getData();
        
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Basic Authentication Scheme for IdP " + idpBean.getName());
        
        Bean basicAuthnBean = newBean(idpBeans, normalizeBeanName(basicAuthn.getName()), UsernamePasswordAuthScheme.class);

        // priority
        setPropertyValue(basicAuthnBean, "priority", basicAuthn.getPriority() + "");

        // Auth scheme name cannot be changed!
        setPropertyValue(basicAuthnBean, "name", "basic-authentication");

        if (!basicAuthn.getHashAlgorithm().equalsIgnoreCase("none"))
            setPropertyValue(basicAuthnBean, "hashAlgorithm", basicAuthn.getHashAlgorithm());

        if (!basicAuthn.getHashEncoding().equalsIgnoreCase("none"))
            setPropertyValue(basicAuthnBean, "hashEncoding", basicAuthn.getHashEncoding());

        setPropertyValue(basicAuthnBean, "ignorePasswordCase", basicAuthn.isIgnorePasswordCase()); // Dangerous
        setPropertyValue(basicAuthnBean, "ignoreUserCase", basicAuthn.isIgnoreUsernameCase());


        setPropertyRef(basicAuthnBean, "credentialStore", idpBean.getName() + "-identity-store");
        setPropertyBean(basicAuthnBean, "credentialStoreKeyAdapter", newAnonymousBean(SimpleIdentityStoreKeyAdapter.class));

        ImpersonateUserPolicy impUsrPolicy = basicAuthn.getImpersonateUserPolicy();
        if (impUsrPolicy != null && !impUsrPolicy.getImpersonateUserPolicyType().equals(ImpersonateUserPolicyType.DISABLED)) {

            // Add additional authn scheme  and reference the configured policy using OSGi services
            Bean impersonateAuthnBean = newBean(idpBeans, normalizeBeanName("impersonate-scheme"),
                    "org.atricore.idbus.idojos.impersonateusrauthscheme.ImpersonateUserAuthScheme");


            ImpersonateUserPolicyType impUsrPolicyType = impUsrPolicy.getImpersonateUserPolicyType();
            String impersonateUsrPolicyName = idpBean.getName() + "-impersonate-user-policy";

            switch (impUsrPolicyType) {
                case CUSTOM:

                    // Add OSGi reference to policy service bean:
                    Reference customIpersonateValidationPolicy = new Reference();
                    customIpersonateValidationPolicy.setId(impersonateUsrPolicyName);
                    customIpersonateValidationPolicy.setBeanName(impUsrPolicy.getCustomImpersonateUserPolicy());
                    customIpersonateValidationPolicy.setInterface("org.atricore.idbus.idojos.impersonateusrauthscheme.CurrentUserValidationPolicy");

                    idpBeans.getImportsAndAliasAndBeen().add(customIpersonateValidationPolicy);
                    setPropertyRef(impersonateAuthnBean, "policy", impersonateUsrPolicyName);

                    break;
                default:
                    logger.warn("Unsupported Impersonate User Policy : " + impUsrPolicyType.getDisplayName());
                    break;
            }



        }
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        BasicAuthentication basicAuthn = (BasicAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        Bean basicAuthnBean = getBean(idpBeans, normalizeBeanName(basicAuthn.getName()));
        Bean impersonateAuthnBean = getBean(idpBeans, normalizeBeanName("impersonate-scheme"));

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Wire basic authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            // Wire basic authentication scheme to Authenticator
            Bean legacyAuthenticator = authenticators.iterator().next();

            // Add basic authenticator
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", basicAuthnBean);

            // Add impersonate authenticator, if any
            if (impersonateAuthnBean != null)
                addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", impersonateAuthnBean);

            // Add Basic Authenticator, if not already configured
            Bean basicAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.sts.main.authenticators.BasicSecurityTokenAuthenticator");
            setPropertyRef(basicAuthenticator, "authenticator", legacyAuthenticator.getName());

            // Add authenticators to STS
            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            addPropertyBean(sts, "authenticators", basicAuthenticator);


        }

        return basicAuthnBean;
    }
}
