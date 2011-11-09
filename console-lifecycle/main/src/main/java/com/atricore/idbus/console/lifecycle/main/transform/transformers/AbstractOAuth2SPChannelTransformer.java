package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Constants;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Service;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2Binding;
import org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.FederationService;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.util.HashGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractOAuth2SPChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(AbstractSPChannelTransformer.class);

    protected String contextSpChannelBean = "spOAuth2ChannelBean";

    /**
     * Generate IDP Components for a federated connection:
     *
     * @param idp IdP definition
     * @param spChannel SP Channel
     * @param fc Federated connection
     * @param target target provider (SP or IdP)
     * @param targetChannel (Target Channel IdP or SP)
     * @param ctx
     * @throws com.atricore.idbus.console.lifecycle.main.exception.TransformException
     */
    protected void generateIdPComponents(IdentityProvider idp,
                                     ServiceProviderChannel spChannel,
                                     FederatedConnection fc,
                                     FederatedProvider target,
                                     FederatedChannel targetChannel,
                                     IdApplianceTransformationContext ctx) throws TransformException {

        // TODO : Support disabling enabling services/bindings when building endpoints !!!!

        // If no channel is provided, we asume this is the default
        boolean isDefaultChannel = spChannel == null;

        Beans idpBeans = (Beans) ctx.get("idpBeans");
        Beans beansOsgi = (Beans) ctx.get("beansOsgi");

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for OAUTH2 SP Channel " + (!isDefaultChannel ? spChannel.getName() : "default") + " of IdP " + idp.getName());

        //---------------------------------------------
        // Get IDP Bean
        //---------------------------------------------
        Bean idpBean = null;
        Collection<Bean> b = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + b.size());
        }
        idpBean = b.iterator().next();

        //---------------------------------------------
        // Get IDP default (SSO) federation service bean
        //---------------------------------------------
        Bean idpOAuth2SvcBean = null;
        String idpOAuth2SvcBeanName = null;
        Set<Bean> idpFederationSvcs = getPropertyBeansFromSet(idpBeans, idpBean, "federationServices");
        String idpOauth2ServiceType = "urn:org:atricore:idbus:OAUTH:2.0";

        if (idpFederationSvcs != null) {
            for (Bean idpFederationSvcBean : idpFederationSvcs) {
                if (idpFederationSvcBean.getName().equals(idpOAuth2SvcBeanName)) {
                    idpOAuth2SvcBean = idpFederationSvcBean;
                    break;
                }
            }
        }

        if (idpOAuth2SvcBean == null) {

            idpOAuth2SvcBeanName = idpBean.getName() + "-oauth2-svc";
            idpOAuth2SvcBean  = newBean(idpBeans, idpOAuth2SvcBeanName, FederationService.class);
            setPropertyValue(idpOAuth2SvcBean, "serviceType", idpOauth2ServiceType);
            setPropertyValue(idpOAuth2SvcBean, "name", idpOAuth2SvcBeanName);

            addPropertyRefsToSet(idpBean, "federationServices", idpOAuth2SvcBeanName);

        }
        idpOAuth2SvcBean = getBean(idpBeans, idpOAuth2SvcBeanName);

        //---------------------------------------------
        // See if we already defined the channel
        //---------------------------------------------
        // SP Channel name : <idp-name>-sso-<sp-channel-name>-sp-channel, sso service is the default, and the one this transformer generates
        String spChannelName = idpBean.getName() +  "-oauth2-" + (!isDefaultChannel ? normalizeBeanName(target.getName()) : "default") + "-sp-channel";

        String idauPath = (String) ctx.get("idauPath");

        // Check if we already created default service
        if (isDefaultChannel && getPropertyRef(idpOAuth2SvcBean, "channel") != null) {
            ctx.put(contextSpChannelBean, getBean(idpBeans, spChannelName));
            return;
        }

        // Check if we already created override channel
        if (spChannel != null) {
            Set<Bean> spChannelBeans = getPropertyBeansFromSet(idpBeans, idpOAuth2SvcBean, "overrideChannels");
            if (spChannelBeans != null) {
                for (Bean spChannelBean : spChannelBeans) {
                    if (getPropertyValue(spChannelBean, "name").equals(spChannelName)) {
                        // Do not re-process a channel definition
                        if (logger.isTraceEnabled())
                            logger.trace("Ignoring channel " + spChannel.getName() + ". It was alredy processed");

                        ctx.put(contextSpChannelBean, spChannelBean);
                        return;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating SP Channel definition for " + spChannelName);

        // COT Member Descriptor
        String mdName = idpBean.getName() + "-oauth2-md";
        if (spChannel != null) {
            mdName = spChannelName + "-oauth2-md";
        }
        Bean idpMd = newBean(idpBeans, mdName, ResourceCircleOfTrustMemberDescriptorImpl.class);
        String alias = resolveLocationUrl(idp, spChannel) + "/OAUTH2/MD";
        try {
            setPropertyValue(idpMd, "id", HashGenerator.sha1(alias));
        } catch (UnsupportedEncodingException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': no such algorithm");
        }
        setPropertyValue(idpMd, "alias", alias);

        /* Metadata resource not available for OAuth2
        String resourceName = idpBean.getName();
        if (spChannel != null) {
            resourceName = normalizeBeanName(spChannel.getName());
        }
        setPropertyValue(idpMd, "resource", "classpath:" + idauPath + idpBean.getName() + "/" + resourceName + "-oauth2-metadata.xml");

        Bean mdIntrospector = newAnonymousBean(SamlR2MetadataDefinitionIntrospector.class);
        setPropertyBean(idpMd, "metadataIntrospector", mdIntrospector);
        */


        // -------------------------------------------------------
        // SP Channel
        // -------------------------------------------------------
        Bean spChannelBean = newBean(idpBeans, spChannelName, SPChannelImpl.class.getName());
        ctx.put(contextSpChannelBean, spChannelBean);

        setPropertyValue(spChannelBean, "name", spChannelName);
        setPropertyValue(spChannelBean, "description", (spChannel != null ? spChannel.getDisplayName() : idp.getName()));
        setPropertyValue(spChannelBean, "location", resolveLocationUrl(idp, spChannel));
        setPropertyRef(spChannelBean, "provider", normalizeBeanName(idp.getName()));
        if (spChannel != null)
            setPropertyRef(spChannelBean, "targetProvider", normalizeBeanName(target.getName()));
        setPropertyRef(spChannelBean, "sessionManager", idpBean.getName() + "-session-manager");
        setPropertyRef(spChannelBean, "identityManager", idpBean.getName() + "-identity-manager");
        setPropertyRef(spChannelBean, "member", idpMd.getName());

        // identityMediator
        Bean identityMediatorBean = getBean(idpBeans, idpBean.getName() + "-oauth2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + idpBean.getName() + "-oauth2-mediator");
        setPropertyRef(spChannelBean, "identityMediator", identityMediatorBean.getName());

        // -------------------------------------------------------
        // endpoints
        // -------------------------------------------------------
        List<Bean> endpoints = new ArrayList<Bean>();

        // TODO : Check for enabled svcs/bindings from model
        boolean isSoapBindingEnabled = true;
        boolean isRestfulBindingEnabled = false;
        boolean isTokenServiceEnabled = true;
        boolean isAuthorizationServiceEnabled = false;

        if (isTokenServiceEnabled) {
            // -------------------------------------------------------
            // Token Service Endpoints
            // -------------------------------------------------------
            if (isSoapBindingEnabled) {
                Bean tokenSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
                tokenSoap.setName(spChannelBean.getName() + "-token-soap");
                setPropertyValue(tokenSoap, "name", tokenSoap.getName());
                setPropertyValue(tokenSoap, "type", OAuth2Service.TokenService.toString());
                setPropertyValue(tokenSoap, "binding", OAuth2Binding.OAUTH2_SOAP.getValue());
                setPropertyValue(tokenSoap, "location", "/OAUTH2/TOKEN/SOAP");
                endpoints.add(tokenSoap);

            }

            if (isRestfulBindingEnabled) {
                Bean tokenRestful = newAnonymousBean(IdentityMediationEndpointImpl.class);
                tokenRestful.setName(spChannelBean.getName() + "-token-restful");
                setPropertyValue(tokenRestful, "name", tokenRestful.getName());
                setPropertyValue(tokenRestful, "type", OAuth2Service.TokenService.toString());
                setPropertyValue(tokenRestful, "binding", OAuth2Binding.OAUTH2_RESTFUL.getValue());
                setPropertyValue(tokenRestful, "location", "/OAUTH2/TOKEN/RESTFUL");
                endpoints.add(tokenRestful);
            }
        }

        if (isAuthorizationServiceEnabled) {
            // -------------------------------------------------------
            // Token Service Endpoints
            // -------------------------------------------------------
            if (isSoapBindingEnabled) {
                Bean tokenSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
                tokenSoap.setName(spChannelBean.getName() + "-authz-soap");
                setPropertyValue(tokenSoap, "name", tokenSoap.getName());
                setPropertyValue(tokenSoap, "type", OAuth2Service.TokenService.toString());
                setPropertyValue(tokenSoap, "binding", OAuth2Binding.OAUTH2_SOAP.getValue());
                setPropertyValue(tokenSoap, "location", "/OAUTH2/AUTHZ/SOAP");
                endpoints.add(tokenSoap);

            }

            if (isRestfulBindingEnabled) {
                Bean tokenRestful = newAnonymousBean(IdentityMediationEndpointImpl.class);
                tokenRestful.setName(spChannelBean.getName() + "-authz-restful");
                setPropertyValue(tokenRestful, "name", tokenRestful.getName());
                setPropertyValue(tokenRestful, "type", OAuth2Service.TokenService.toString());
                setPropertyValue(tokenRestful, "binding", OAuth2Binding.OAUTH2_RESTFUL.getValue());
                setPropertyValue(tokenRestful, "location", "/OAUTH2/AUTHZ/RESTFUL");
                endpoints.add(tokenRestful);
            }
        }


        setPropertyAsBeans(spChannelBean, "endpoints", endpoints);

        //Bean authnToSamlResponsePlan = newBean(idpBeans, "samlr2authnreq-to-samlr2response-plan", SamlR2AuthnReqToSamlR2RespPlan.class);
        //setPropertyRef(authnToSamlResponsePlan, "bpmsManager", "bpms-manager");

        if (!isDefaultChannel)
            addPropertyBeansAsRefsToSet(idpOAuth2SvcBean, "overrideChannels", spChannelBean);
        else
            setPropertyRef(idpOAuth2SvcBean, "channel", spChannelBean.getName());
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        // SP Channel bean
        Bean spChannelBean = (Bean) event.getContext().get(contextSpChannelBean);
        Bean idpBean = (Bean) event.getContext().get("idpBean");
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans beans = (Beans) event.getContext().get("beans");

        Bean sts = getBean(idpBeans, idpBean.getName() + "-sts");
        if (sts == null)
            throw new TransformException("No STS defined as " + idpBean.getName() + "-sts");
        setPropertyRef(spChannelBean, "securityTokenService", sts.getName());


        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(beans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();

            List<Bean> channels = getPropertyBeans(idpBeans, mu, "channels");
            boolean found = false;

            if (channels != null)
                for (Bean bean : channels) {
                    if (getPropertyValue(bean, "name").equals(getPropertyValue(spChannelBean, "name"))) {
                        found = true;
                        break;
                    }
                }

            if (!found)
                addPropertyBeansAsRefs(mu, "channels", spChannelBean);

        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

        return spChannelBean;
    }

    public String getContextSpChannelBean() {
        return contextSpChannelBean;
    }

    public void setContextSpChannelBean(String contextSpChannelBean) {
        this.contextSpChannelBean = contextSpChannelBean;
    }

}
