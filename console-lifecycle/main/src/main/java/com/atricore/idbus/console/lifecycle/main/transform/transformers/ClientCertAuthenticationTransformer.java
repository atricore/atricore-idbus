package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ClientCertAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.ClientCertAuthnService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
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
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 11/5/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCertAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(ClientCertAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof ClientCertAuthentication))
            return false;

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        ClientCertAuthentication ccert = (ClientCertAuthentication) event.getData();
        AuthenticationService authnService = ccert.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof ClientCertAuthnService;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");
        ClientCertAuthentication ccertAuthn = (ClientCertAuthentication) event.getData();

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        IdApplianceTransformationContext ctx = event.getContext();

        ClientCertAuthnService ccertAuthnSvc = (ClientCertAuthnService) ccertAuthn.getDelegatedAuthentication().getAuthnService();

        // Authentication scheme

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Client Certificate Authentication Scheme for IdP " + idpBean.getName());

        Bean ccertAuthnScheme = newBean(idpBeans, normalizeBeanName(ccertAuthn.getName()), "org.atricore.idbus.capabilities.clientcertauthn.X509CertificateAuthScheme");

        // priority
        setPropertyValue(ccertAuthnScheme, "priority", ccertAuthn.getPriority() + "");

        // Auth scheme name cannot be changed!
        setPropertyValue(ccertAuthnScheme, "name", "clientcert-authentication");


    }


    @Override
    public Object after(TransformEvent event) throws TransformException {
        ClientCertAuthentication ccertAuthn = (ClientCertAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Bean basicAuthnBean = getBean(idpBeans, normalizeBeanName(ccertAuthn.getName()));
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        // Wire basic authentication scheme to Authenticator
        Collection<Bean> authenticators = getBeansOfType(idpBeans, AuthenticatorImpl.class.getName());
        if (authenticators.size() == 1) {

            // Wire two factor authentication scheme to Authenticator
            Bean legacyAuthenticator = authenticators.iterator().next();
            addPropertyBeansAsRefs(legacyAuthenticator, "authenticationSchemes", basicAuthnBean);

            // Add new 2F Authenticator
            Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
            Bean twoFactorAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.clientcertauthn.authenticators.X509CertificateAuthenticator");
            setPropertyRef(twoFactorAuthenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", twoFactorAuthenticator);

        }

        return basicAuthnBean;
    }

    public class WiaRealms implements Serializable {
        private Set<WiaRealmDefinition> definitions = new HashSet<WiaRealmDefinition>();

        public Set<WiaRealmDefinition> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(Set<WiaRealmDefinition> definitions) {
            this.definitions = definitions;
        }

        public WiaRealmDefinition[] getDefinitionsAsArray() {
            return definitions.toArray(new WiaRealmDefinition[definitions.size()]);
        }

        @Override
        public String toString() {
            return "WIA Definitions: " + definitions.size();
        }
    }

    public class WiaRealmDefinition implements Serializable {
        private String keyTabsRepository;

        private String keyTabName;

        private String servicePrincipalName;

        private String realmName;

        private String keyDistributionCenter;

        private String kerberosRealm;

        private String defaultKrb5Config;

        private boolean configureKerberos;

        public String getKeyTabsRepository() {
            return keyTabsRepository;
        }

        public void setKeyTabsRepository(String keyTabsRepository) {
            this.keyTabsRepository = keyTabsRepository;
        }

        public String getKeyTabName() {
            return keyTabName;
        }

        public void setKeyTabName(String keyTabName) {
            this.keyTabName = keyTabName;
        }

        public String getServicePrincipalName() {
            return servicePrincipalName;
        }

        public void setServicePrincipalName(String servicePrincipalName) {
            this.servicePrincipalName = servicePrincipalName;
        }

        public String getRealmName() {
            return realmName;
        }

        public void setRealmName(String realmName) {
            this.realmName = realmName;
        }

        public String getKeyDistributionCenter() {
            return keyDistributionCenter;
        }

        public void setKeyDistributionCenter(String keyDistributionCenter) {
            this.keyDistributionCenter = keyDistributionCenter;
        }

        public String getKerberosRealm() {
            return kerberosRealm;
        }

        public void setKerberosRealm(String kerberosRealm) {
            this.kerberosRealm = kerberosRealm;
        }

        public String getDefaultKrb5Config() {
            return defaultKrb5Config;
        }

        public void setDefaultKrb5Config(String defaultKrb5Config) {
            this.defaultKrb5Config = defaultKrb5Config;
        }

        public boolean getConfigureKerberos() {
            return configureKerberos;
        }

        public void setConfigureKerberos(boolean configureKerberos) {
            this.configureKerberos = configureKerberos;
        }
    }

}

