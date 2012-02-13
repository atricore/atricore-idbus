package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.BindAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.DirectoryAuthenticationService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.store.identity.SimpleIdentityStoreKeyAdapter;

import java.util.Collection;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.addPropertyBeansAsRefs;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DirectoryServiceAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(DirectoryServiceAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof BindAuthentication))
            return false;

        BindAuthentication ba = (BindAuthentication) event.getData();

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();
        AuthenticationService authnService = ba.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof DirectoryAuthenticationService;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");

        BindAuthentication bindAuthn = (BindAuthentication) event.getData();
        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Two-Factor Authentication Scheme for IdP " + idpBean.getName());

        AuthenticationService authnService = bindAuthn.getDelegatedAuthentication().getAuthnService();

        if (authnService instanceof DirectoryAuthenticationService) {
            DirectoryAuthenticationService directoryAuthnService = (DirectoryAuthenticationService) authnService;

            // LDAP Bind credential store
            Bean ldapBindCredStore = newBean(idpBeans, normalizeBeanName(bindAuthn.getName() + "-credential-store"),
                    "org.atricore.idbus.idojos.ldapidentitystore.LDAPBindIdentityStore");


            Bean bindAuthScheme = newBean(idpBeans, normalizeBeanName(bindAuthn.getName()),
                "org.atricore.idbus.kernel.main.authn.scheme.BindUsernamePasswordAuthScheme");

            // Auth scheme name cannot be changed!
            setPropertyValue(bindAuthScheme, "name", "basic-authentication");
            setPropertyRef(bindAuthScheme, "credentialStore", ldapBindCredStore.getName());


            setPropertyValue(ldapBindCredStore, "initialContextFactory", directoryAuthnService.getInitialContextFactory());
            setPropertyValue(ldapBindCredStore, "providerUrl", directoryAuthnService.getProviderUrl());
            setPropertyValue(ldapBindCredStore, "securityAuthentication", directoryAuthnService.getSecurityAuthentication());
            setPropertyValue(ldapBindCredStore, "validateBindWithSearch", directoryAuthnService.isPerformDnSearch());

            if (directoryAuthnService.getPasswordPolicy().equals("ldap-rfc-draft")) {
                setPropertyValue(ldapBindCredStore, "passwordPolicySupport", true);
            } else if (directoryAuthnService.getPasswordPolicy().equalsIgnoreCase("none")) {
                setPropertyValue(ldapBindCredStore, "passwordPolicySupport", false);
            } else {
                logger.warn("Unknown selected password policy support for Directory Authentication Service : " +
                        directoryAuthnService.getPasswordPolicy());
            }

            setPropertyValue(ldapBindCredStore, "usersCtxDN", directoryAuthnService.getUsersCtxDN());
            setPropertyValue(ldapBindCredStore, "principalUidAttributeID", directoryAuthnService.getPrincipalUidAttributeID());
            setPropertyValue(ldapBindCredStore, "securityPrincipal", directoryAuthnService.getSecurityPrincipal());
            setPropertyValue(ldapBindCredStore, "securityCredential", directoryAuthnService.getSecurityCredential());
            setPropertyValue(ldapBindCredStore, "ldapSearchScope", directoryAuthnService.getLdapSearchScope());

        }
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        BindAuthentication bindAuthentication = (BindAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Bean bindAuthnScheam = getBean(idpBeans, normalizeBeanName(bindAuthentication.getName()));
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Wire two factor authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            // Wire basic authentication scheme to Authenticator
            Bean legacyAuthenticator = authenticators.iterator().next();
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", bindAuthnScheam);

            // Add new Basic Authenticator, if not already configured ...
            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            Bean basicAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.sts.main.authenticators.BasicSecurityTokenAuthenticator");
            setPropertyRef(basicAuthenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", basicAuthenticator);

        }

        return bindAuthnScheam;
    }

}
