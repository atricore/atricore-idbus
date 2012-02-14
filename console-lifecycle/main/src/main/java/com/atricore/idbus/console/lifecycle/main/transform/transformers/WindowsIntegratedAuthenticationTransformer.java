package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationService;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityProvider;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsAuthentication;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.WindowsIntegratedAuthentication;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectModule;
import com.atricore.idbus.console.lifecycle.main.transform.IdProjectResource;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.AuthenticatorImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.capabilities.spnego.SpnegoAuthenticationScheme;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.addPropertyBeansAsRefs;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WindowsIntegratedAuthenticationTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(WindowsIntegratedAuthenticationTransformer.class);

    @Override
    public boolean accept(TransformEvent event) {

        if (!(event.getData() instanceof WindowsAuthentication))
            return false;

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();
        //AuthenticationService authnService = idp.getDelegatedAuthentication().getAuthnService();
        WindowsAuthentication wia = (WindowsAuthentication) event.getData();
        AuthenticationService authnService = wia.getDelegatedAuthentication().getAuthnService();

        return authnService != null && authnService instanceof WindowsIntegratedAuthentication;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        String idauPath = (String) event.getContext().get("idauPath");
        WindowsAuthentication wiaAuthn = (WindowsAuthentication) event.getData();

        IdentityProvider idp = (IdentityProvider) event.getContext().getParentNode();

        WindowsIntegratedAuthentication wia = (WindowsIntegratedAuthentication) wiaAuthn.getDelegatedAuthentication().getAuthnService();

        // TODO : For now user velocity, but we MUST use blueprint xml binding, like we do with spring!

        // TODO : Support MULTIPLE domains, does it require multiple JAAS entries ?!

        String spn = buildSpn(wia);

        String keyTabName = idp.getIdentityAppliance().getName() + "-" +  wia.getName() + ".keytab";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("realmName", wia.getName());
        params.put("servicePrincipalName", spn);
        params.put("kerberosRealm", wia.getDomain());
        params.put("keyDistributionCenter", wia.getDomainController());
        params.put("keyTabName", keyTabName);
        params.put("configureKerberos", wia.isOverwriteKerberosSetup());


        String keyTabsRepository = System.getProperty("karaf.base");
        keyTabsRepository += "/etc/krb5/keytabs/";
        keyTabsRepository = keyTabsRepository.replace('\\', '/');

        String defaultKrb5Config = System.getProperty("karaf.base");
        defaultKrb5Config += "/etc/krb5/krb5.conf";
        defaultKrb5Config = defaultKrb5Config.replace('\\', '/');

        params.put("keyTabsRepository", keyTabsRepository);
        params.put("defaultKrb5Config", defaultKrb5Config);

        IdProjectModule module = event.getContext().getCurrentModule();

        IdProjectResource<String> agentConfig = new IdProjectResource<String>(idGen.generateId(),
                "OSGI-INF/blueprint/", "kerberos-jaas", "kerberos", "jaas");
        agentConfig.setClassifier("velocity");
        agentConfig.setExtension("xml");
        agentConfig.setParams(params);
        agentConfig.setScope(IdProjectResource.Scope.RESOURCE);
        module.addResource(agentConfig);

        // Authentication scheme

        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        if (logger.isTraceEnabled())
            logger.trace("Generating Spnego Authentication Scheme for IdP " + idpBean.getName());

        Bean spnegoAuthn = newBean(idpBeans, normalizeBeanName(wiaAuthn.getName()), SpnegoAuthenticationScheme.class);

        // priority
        setPropertyValue(spnegoAuthn, "priority", wiaAuthn.getPriority() + "");

        // Auth scheme name cannot be changed!
        setPropertyValue(spnegoAuthn, "name", "spnego-authentication");
        setPropertyValue(spnegoAuthn, "realm", wia.getName());
        setPropertyValue(spnegoAuthn, "principalName", spn);

        // metadata file
        IdProjectResource<byte[]> keyTabResource = new IdProjectResource<byte[]>(idGen.generateId(),
                "META-INF/krb5/" , keyTabName,
                "binary", wia.getKeyTab().getValue());
        keyTabResource.setClassifier("byte");
        event.getContext().getCurrentModule().addResource(keyTabResource);

    }

    public static String buildSpn(WindowsIntegratedAuthentication wia) {
        return wia.getServiceClass() +
                "/" + wia.getHost() +
                (wia.getPort() > 0 ? ":" + wia.getPort() : "") +
                (wia.getServiceName() != null && !"".equals(wia.getServiceName()) ? "/" + wia.getServiceName() : "") +
                "@" + wia.getDomain();
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        WindowsAuthentication wiaAuthn = (WindowsAuthentication) event.getData();
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Bean basicAuthnBean = getBean(idpBeans, normalizeBeanName(wiaAuthn.getName()));
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
            Bean twoFactorAuthenticator = newAnonymousBean("org.atricore.idbus.capabilities.spnego.authenticators.SpnegoSecurityTokenAuthenticator");
            setPropertyRef(twoFactorAuthenticator, "authenticator", legacyAuthenticator.getName());

            addPropertyBean(sts, "authenticators", twoFactorAuthenticator);

        }

        return basicAuthnBean;
    }

}

