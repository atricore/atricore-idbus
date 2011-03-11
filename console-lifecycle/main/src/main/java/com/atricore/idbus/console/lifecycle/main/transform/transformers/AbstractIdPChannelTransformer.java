package com.atricore.idbus.console.lifecycle.main.transform.transformers;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.util.HashGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;

public class AbstractIdPChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(AbstractIdPChannelTransformer.class);

    protected String contextIdpChannelBean = "idpChannelBean";

    protected void generateSPComponents(ServiceProvider sp,
                                     IdentityProviderChannel idpChannel,
                                     FederatedConnection fc,
                                     FederatedProvider target,
                                     FederatedChannel targetChannel,
                                     IdApplianceTransformationContext ctx) throws TransformException {

        Beans spBeans = (Beans) ctx.get("spBeans");
        Beans beans = (Beans) ctx.get("beans");
        
        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for IdP Channel " + (idpChannel != null ? idpChannel.getName() : "default") + " of SP " + sp.getName());

        Bean spBean = null;
        Collection<Bean> b = getBeansOfType(spBeans, ServiceProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + b.size());
        }
        
        spBean = b.iterator().next();
        String idpChannelName = spBean.getName() +  "-" + (idpChannel != null ? normalizeBeanName(target.getName()) : "default") + "-idp-channel";

        String idauPath = (String) ctx.get("idauPath");
        
        // Check if we already created default channel
        if (idpChannel == null && getPropertyRef(spBean, "channel") != null) {
            ctx.put(contextIdpChannelBean, getBean(spBeans, idpChannelName));
            return;
        }

        // Check if we already created override channel
        if (idpChannel != null) {
            List<Bean> idpChannelBeans = getPropertyBeans(spBeans, spBean, "channels");
            if (idpChannelBeans != null) {
                for (Bean idpChannelBean : idpChannelBeans) {
                    if (getPropertyValue(idpChannelBean, "name").equals(idpChannelName)) {
                        // Do not re-process a channel definition
                        if (logger.isTraceEnabled())
                            logger.trace("Ignoring channel " + idpChannel.getName() + ". It was alredy processed");
                        ctx.put(contextIdpChannelBean, idpChannelBean);
                        return;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating IdP Channel definition for " + idpChannelName);

        // COT Member Descriptor
        String mdName = spBean.getName() + "-md";
        if (idpChannel != null) {
            mdName = idpChannelName + "-md";
        }
        Bean spMd = newBean(spBeans, mdName, ResourceCircleOfTrustMemberDescriptorImpl.class);
        String alias = resolveLocationUrl(sp, idpChannel) + "/SAML2/MD";
        try {
            setPropertyValue(spMd, "id", HashGenerator.sha1(alias));
        } catch (UnsupportedEncodingException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': no such algorithm");
        }
        setPropertyValue(spMd, "alias", alias);
        String resourceName = spBean.getName();
        if (idpChannel != null) {
            resourceName = normalizeBeanName(idpChannel.getName());
        }
        setPropertyValue(spMd, "resource", "classpath:" + idauPath + spBean.getName() + "/" + resourceName + "-samlr2-metadata.xml");

        // -------------------------------------------------------
        // IDP Channel
        // -------------------------------------------------------
        Bean idpChannelBean = newBean(spBeans, idpChannelName, IdPChannelImpl.class.getName());
        ctx.put(contextIdpChannelBean, idpChannelBean);

        // name
        setPropertyValue(idpChannelBean, "name", idpChannelName);
        setPropertyValue(idpChannelBean, "description", (idpChannel != null ? idpChannel.getDisplayName() : sp.getName()));
        setPropertyValue(idpChannelBean, "location", resolveLocationUrl(sp, idpChannel));
        setPropertyRef(idpChannelBean, "provider", normalizeBeanName(sp.getName()));
        if (idpChannel != null)
            setPropertyRef(idpChannelBean, "targetProvider", normalizeBeanName(target.getName()));
        setPropertyRef(idpChannelBean, "sessionManager", spBean.getName() + "-session-manager");
        setPropertyRef(idpChannelBean, "member", spMd.getName());
        
        // identityMediator
        Bean identityMediatorBean = getBean(spBeans, spBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + spBean.getName() + "-samlr2-identity-mediator");

        setPropertyRef(idpChannelBean, "identityMediator", identityMediatorBean.getName());

        // accountLinkLifecycle
        setPropertyRef(idpChannelBean, "accountLinkLifecycle", spBean.getName() + "-account-link-lifecycle");

        // accountLinkEmitter
        setPropertyRef(idpChannelBean, "accountLinkEmitter", spBean.getName() + "-account-link-emitter");

        // identityMapper
        setPropertyRef(idpChannelBean, "identityMapper", spBean.getName() + "-identity-mapper");

        // endpoints
        List<Bean> endpoints = new ArrayList<Bean>();

        // profiles
        Set<Profile> activeProfiles = sp.getActiveProfiles();
        if (idpChannel != null) {
            activeProfiles = idpChannel.getActiveProfiles();
        }
        boolean ssoEnabled = false;
        boolean sloEnabled = false;
        for (Profile profile : activeProfiles) {
            if (profile.equals(Profile.SSO)) {
                ssoEnabled = true;
            } else if (profile.equals(Profile.SSO_SLO)) {
                sloEnabled = true;
            }
        }

        // bindings
        Set<Binding> activeBindings = sp.getActiveBindings();
        if (idpChannel != null) {
            activeBindings = idpChannel.getActiveBindings();
        }
        boolean postEnabled = false;
        boolean redirectEnabled = false;
        boolean artifactEnabled = false;
        boolean soapEnabled = false;
        for (Binding binding : activeBindings) {
            if (binding.equals(Binding.SAMLR2_HTTP_POST)) {
                postEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_HTTP_REDIRECT)) {
                redirectEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_ARTIFACT)) {
                artifactEnabled = true;
            } else if (binding.equals(Binding.SAMLR2_SOAP)) {
                soapEnabled = true;
            }
        }

        // SingleLogoutService

        if (sloEnabled) {
            // SAML2 SLO HTTP POST
            if (postEnabled) {
                Bean sloHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpPost.setName(idpChannelBean.getName() + "-saml2-slo-http-post");
                setPropertyValue(sloHttpPost, "name", sloHttpPost.getName());
                setPropertyValue(sloHttpPost, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
                endpoints.add(sloHttpPost);
            }

            // SAML2 SLO HTTP ARTIFACT
            if (artifactEnabled) {
                Bean sloHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpArtifact.setName(idpChannelBean.getName() + "-saml2-slo-http-artifact");
                setPropertyValue(sloHttpArtifact, "name", sloHttpArtifact.getName());
                setPropertyValue(sloHttpArtifact, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpArtifact, "binding", SamlR2Binding.SAMLR2_ARTIFACT.getValue());
                endpoints.add(sloHttpArtifact);
            }

            // SAML2 SLO HTTP REDIRECT
            if (redirectEnabled) {
                Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpRedirect.setName(idpChannelBean.getName() + "-saml2-slo-http-redirect");
                setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
                setPropertyValue(sloHttpRedirect, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpRedirect, "binding", SamlR2Binding.SAMLR2_REDIRECT.getValue());
                endpoints.add(sloHttpRedirect);
            }

            // SAML2 SLO SOAP
            if (soapEnabled) {
                Bean sloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloSoap.setName(idpChannelBean.getName() + "-saml2-slo-soap");
                setPropertyValue(sloSoap, "name", sloSoap.getName());
                setPropertyValue(sloSoap, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloSoap, "binding", SamlR2Binding.SAMLR2_SOAP.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spBean.getName() + "-spsso-samlr2sloreq-to-samlr2resp-plan");
                plansList.add(plan);
                setPropertyRefs(sloSoap, "identityPlans", plansList);
                endpoints.add(sloSoap);
            }

            // SAML2 SLO LOCAL
            Bean sloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            sloLocal.setName(idpChannelBean.getName() + "-saml2-slo-local");
            setPropertyValue(sloLocal, "name", sloLocal.getName());
            setPropertyValue(sloLocal, "type", SAMLR2MetadataConstants.SingleLogoutService_QNAME.toString());
            setPropertyValue(sloLocal, "binding", SamlR2Binding.SAMLR2_LOCAL.getValue());
            // NOTE: location doesn't exist in simple-federation example
            setPropertyValue(sloLocal, "location", "local://" + (idpChannel != null ?
                    idpChannel.getLocation().getUri().toUpperCase() : sp.getLocation().getUri().toUpperCase()) + "/SLO/LOCAL");
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(spBean.getName() + "-spsso-samlr2sloreq-to-samlr2resp-plan");
            plansList.add(plan);
            setPropertyRefs(sloLocal, "identityPlans", plansList);
            endpoints.add(sloLocal);
        }

        // AssertionConsumerService
        if (ssoEnabled) {
            if (postEnabled) {
                Bean acHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                acHttpPost.setName(idpChannelBean.getName() + "-saml2-ac-http-post");
                setPropertyValue(acHttpPost, "name", acHttpPost.getName());
                setPropertyValue(acHttpPost, "type", SAMLR2MetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpPost, "binding", SamlR2Binding.SAMLR2_POST.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spBean.getName() + "-idpunsolicitedresponse-to-subject-plan");
                plansList.add(plan);
                setPropertyRefs(acHttpPost, "identityPlans", plansList);
                endpoints.add(acHttpPost);
            }

            if (artifactEnabled) {
                Bean acHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                acHttpArtifact.setName(idpChannelBean.getName() + "-saml2-ac-http-artifact");
                setPropertyValue(acHttpArtifact, "name", acHttpArtifact.getName());
                setPropertyValue(acHttpArtifact, "type", SAMLR2MetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpArtifact, "binding", SamlR2Binding.SAMLR2_ARTIFACT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spBean.getName() + "-idpunsolicitedresponse-to-subject-plan");
                plansList.add(plan);
                setPropertyRefs(acHttpArtifact, "identityPlans", plansList);
                endpoints.add(acHttpArtifact);
            }

            if (redirectEnabled) {
                Bean acHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                acHttpRedirect.setName(idpChannelBean.getName() + "-saml2-ac-http-redirect");
                setPropertyValue(acHttpRedirect, "name", acHttpRedirect.getName());
                setPropertyValue(acHttpRedirect, "type", SAMLR2MetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpRedirect, "binding", SamlR2Binding.SAMLR2_REDIRECT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spBean.getName() + "-idpunsolicitedresponse-to-subject-plan");
                plansList.add(plan);
                setPropertyRefs(acHttpRedirect, "identityPlans", plansList);
                endpoints.add(acHttpRedirect);
            }
        }

        // ArtifactResolutionService must always be enabled, just in case other providers support this binding
        //if (artifactEnabled)
        {
            Bean arSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
            arSoap.setName(idpChannelBean.getName() + "-saml2-ar-soap");
            setPropertyValue(arSoap, "name", arSoap.getName());
            setPropertyValue(arSoap, "type", SAMLR2MetadataConstants.ArtifactResolutionService_QNAME.toString());
            setPropertyValue(arSoap, "binding", SamlR2Binding.SAMLR2_SOAP.getValue());
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(spBean.getName() + "-samlr2artresolve-to-samlr2artresponse-plan");
            plansList.add(plan);
            Ref plan2 = new Ref();
            plan2.setBean(spBean.getName() + "-samlr2art-to-samlr2artresolve-plan");
            plansList.add(plan2);
            setPropertyRefs(arSoap, "identityPlans", plansList);
            endpoints.add(arSoap);

            Bean arLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            arLocal.setName(idpChannelBean.getName() + "-saml2-ar-local");
            setPropertyValue(arLocal, "name", arLocal.getName());
            setPropertyValue(arLocal, "type", SAMLR2MetadataConstants.ArtifactResolutionService_QNAME.toString());
            setPropertyValue(arLocal, "binding", SamlR2Binding.SAMLR2_LOCAL.getValue());
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(spBean.getName() + "-samlr2artresolve-to-samlr2artresponse-plan");
            plansList.add(plan);
            plan2 = new Ref();
            plan2.setBean(spBean.getName() + "-samlr2art-to-samlr2artresolve-plan");
            plansList.add(plan2);
            setPropertyRefs(arLocal, "identityPlans", plansList);
            endpoints.add(arLocal);
        }

        setPropertyAsBeans(idpChannelBean, "endpoints", endpoints);

        if (idpChannel != null)
            addPropertyBeansAsRefsToSet(spBean, "channels", idpChannelBean);
        else
            setPropertyRef(spBean, "channel", idpChannelBean.getName());
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        // IdP Channel bean
        Bean idpChannelBean = (Bean) event.getContext().get(contextIdpChannelBean);
        Beans beans = (Beans) event.getContext().get("beans");
        Beans spBeans = (Beans) event.getContext().get("spBeans");

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(beans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();

            List<Bean> channels = getPropertyBeans(spBeans, mu, "channels");
            boolean found = false;

            if (channels != null)
                for (Bean bean : channels) {
                    if (getPropertyValue(bean, "name").equals(getPropertyValue(idpChannelBean, "name"))) {
                        found = true;
                        break;
                    }
                }

            if (!found)
                addPropertyBeansAsRefs(mu, "channels", idpChannelBean);

        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }

        return idpChannelBean;
    }

    public String getContextIdpChannelBean() {
        return contextIdpChannelBean;
    }

    public void setContextIdpChannelBean(String contextIdpChannelBean) {
        this.contextIdpChannelBean = contextIdpChannelBean;
    }
}

