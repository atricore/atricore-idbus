package com.atricore.idbus.console.lifecycle.main.transform.transformers.oauth2;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Client;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2IdPMediator;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2BindingFactory;
import org.atricore.idbus.capabilities.oauth2.main.binding.logging.OAuth2LogMessageBuilder;
import org.atricore.idbus.capabilities.oauth2.main.util.JasonUtils;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.CamelLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.component.logging.HttpLogMessageBuilder;
import org.atricore.idbus.kernel.main.mediation.camel.logging.DefaultMediationLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isOAuth2IdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.addPropertyBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;

/**
 */
public class OAuth2IdPProxyTransformer extends AbstractTransformer implements InitializingBean {

    private static final Log logger = LogFactory.getLog(OAuth2IdPProxyTransformer.class);

    private boolean roleA;

    public void afterPropertiesSet() throws Exception {

    }

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }


    @Override
    public boolean accept(TransformEvent event) {
        // Do we have to enable an IdP proxy for a service provider ?
        if (event.getData() instanceof ServiceProviderChannel) {

            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            FederatedProvider idp = roleA ? fc.getRoleA() : fc.getRoleB();
            if (idp instanceof ExternalSaml2IdentityProvider)
                return isOAuth2IdPProxyRequired(fc);
        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        Date now = new Date();
        Beans baseBeans = (Beans) event.getContext().get("beans");
        //Beans idpBeans = (Beans) event.getContext().get("idpBeans");

        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");

        FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();

        ExternalSaml2IdentityProvider identityProvider = (ExternalSaml2IdentityProvider) (roleA ? fc.getRoleA() : fc.getRoleB());
        InternalSaml2ServiceProvider internalSaml2ServiceProvider = (InternalSaml2ServiceProvider) (roleA ? fc.getRoleB() : fc.getRoleA());
        IdentityProviderChannel idpChannel = (IdentityProviderChannel) (roleA ? fc.getChannelB() : fc.getChannelA());

        String idauPath = (String) event.getContext().get("idauPath");

        // Create internal IDP facing local SP
        createProxyIdPSide(event, identityProvider, internalSaml2ServiceProvider, baseBeans, idpProxyBeans, idauPath);

    }

    /**
     * @identityProvider remote SAML 2.0 IdP to be proxied
     * @serviceProvider local SAML 2.0 SP
     *
     * Creates an internal IdP that will face the local SP
     */
    protected void createProxyIdPSide(TransformEvent event,
                                      ExternalSaml2IdentityProvider remoteIdentityProvider,
                                      InternalSaml2ServiceProvider localInternalSaml2ServiceProvider,
                                      Beans baseBeans, Beans idpProxyBeans,
                                      String idauPath) throws TransformException {

        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();
        String idpName =  normalizeBeanName(remoteIdentityProvider.getName() + "-" + localInternalSaml2ServiceProvider.getName() + "-idp-proxy");

        // ----------------------------------------
        // IDP Proxy Provider Mediator
        // ----------------------------------------

        Bean idpMediator = newBean(idpProxyBeans, idpName + "-oauth2-mediator",
                OAuth2IdPMediator.class.getName());

        setPropertyValue(idpMediator, "logMessages", true);

        // artifactQueueManager
        // setPropertyRef(idpMediator, "artifactQueueManager", provider.getIdentityAppliance().getName() + "-aqm");
        setPropertyRef(idpMediator, "artifactQueueManager", "artifactQueueManager");

        // bindingFactory
        setPropertyBean(idpMediator, "bindingFactory", newAnonymousBean(OAuth2BindingFactory.class));

        // logger
        List<Bean> idpLogBuilders = new ArrayList<Bean>();
        idpLogBuilders.add(newAnonymousBean(OAuth2LogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(CamelLogMessageBuilder.class));
        idpLogBuilders.add(newAnonymousBean(HttpLogMessageBuilder.class));

        Bean idpLogger = newAnonymousBean(DefaultMediationLogger.class.getName());
        idpLogger.setName(idpName + "-mediation-logger");
        setPropertyValue(idpLogger, "category", appliance.getNamespace() + "." + appliance.getName() + ".wire." + idpName);
        setPropertyAsBeans(idpLogger, "messageBuilders", idpLogBuilders);
        setPropertyBean(idpMediator, "logger", idpLogger);

        // errorUrl
        setPropertyValue(idpMediator, "errorUrl", resolveUiErrorLocation(appliance));

        // warningUrl
        setPropertyValue(idpMediator, "warningUrl", resolveUiWarningLocation(appliance));


    }
}
