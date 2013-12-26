package com.atricore.idbus.console.lifecycle.main.transform.transformers.sso;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SamlR2MetadataDefinitionIntrospector;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactToSamlR2ArtifactResolvePlan;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SamlR2AuthnResponseToSPAuthnResponse;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.federation.*;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.FederationServiceImpl;
import org.atricore.idbus.kernel.main.util.HashGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyRefs;


/**
 * Abstract transformer to process an IdP channel configuration (as part of an SP definition)
 */
public class AbstractIdPChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(AbstractIdPChannelTransformer.class);

    protected String contextIdpChannelBean = "idpChannelBean";

    protected void generateSPComponents(InternalSaml2ServiceProvider sp,
                                     IdentityProviderChannel idpChannel,
                                     FederatedConnection fc,
                                     FederatedProvider target,
                                     FederatedChannel targetChannel,
                                     IdApplianceTransformationContext ctx) throws TransformException {

        Beans spBeans = (Beans) ctx.get("spBeans");
        Beans beans = (Beans) ctx.get("beans");
        Beans beansOsgi = (Beans) ctx.get("beansOsgi");
        
        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for IdP Channel " + (idpChannel != null ? idpChannel.getName() : "default") + " of SP " + sp.getName());

        //---------------------------------------------
        // Get IDP Bean
        //---------------------------------------------
        Bean spBean = null;
        Collection<Bean> b = getBeansOfType(spBeans, ServiceProviderImpl.class.getName());
        if (b.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + b.size());
        }
        spBean = b.iterator().next();

        //---------------------------------------------
        // Get IDP default (SSO) federation service bean
        //---------------------------------------------
        Bean spSsoSvcBean = null;
        String spSsoSvcBeanName = getPropertyRef(spBean, "defaultFederationService");
        String spSsoServiceType = "urn:oasis:names:tc:SAML:2.0";
        if (spSsoSvcBeanName == null) {
            spSsoSvcBeanName = spBean.getName() + "-sso-default-svc";
            spSsoSvcBean = newBean(spBeans, spSsoSvcBeanName, FederationServiceImpl.class);
            setPropertyRef(spBean, "defaultFederationService", spSsoSvcBeanName);
            setPropertyValue(spSsoSvcBean, "serviceType", spSsoServiceType);
            setPropertyValue(spSsoSvcBean, "name", spSsoSvcBeanName);
            // TODO : Profiles ?!
        }
        spSsoSvcBean = getBean(spBeans, spSsoSvcBeanName);

        String idpChannelName = spBean.getName() +  "-sso-" + (idpChannel != null ? normalizeBeanName(target.getName()) : "default") + "-idp-channel";

        String idauPath = (String) ctx.get("idauPath");
        
        // Check if we already created default channel
        if (idpChannel == null && getPropertyRef(spSsoSvcBean, "channel") != null) {
            ctx.put(contextIdpChannelBean, getBean(spBeans, idpChannelName));
            return;
        }

        // Check if we already created override channel
        if (idpChannel != null) {
            List<Bean> idpChannelBeans = getPropertyBeans(spBeans, spSsoSvcBean, "overrideChannels");
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

        Bean mdIntrospector = newAnonymousBean(SamlR2MetadataDefinitionIntrospector.class);
        setPropertyBean(spMd, "metadataIntrospector", mdIntrospector);

        // -------------------------------------------------------
        // IDP Channel
        // -------------------------------------------------------
        Bean idpChannelBean = newBean(spBeans, idpChannelName, IdPChannelImpl.class.getName());
        ctx.put(contextIdpChannelBean, idpChannelBean);

        // name
        setPropertyValue(idpChannelBean, "name", idpChannelName);
        setPropertyValue(idpChannelBean, "description", (idpChannel != null ? idpChannel.getDisplayName() : sp.getName()));
        setPropertyValue(idpChannelBean, "location", resolveLocationUrl(sp, idpChannel));
        setPropertyRef(idpChannelBean, "federatedProvider", normalizeBeanName(sp.getName()));

        if (idpChannel != null) {

            if (isIdPProxyRequired(fc, !fc.getRoleA().equals(sp))) {
                // Use proxy IdP as target
                setPropertyRef(idpChannelBean, "targetProvider", normalizeBeanName(target.getName() + "-" + sp.getName() + "-idp-proxy"));
                // Set trustedProviders
                Set<Ref> trustedProviders = new HashSet<Ref>();
                Ref t = new Ref();
                t.setBean(normalizeBeanName(target.getName()));
                trustedProviders.add(t);
                setPropertyRefs(idpChannelBean, "trustedProviders", trustedProviders);

            } else {
                setPropertyRef(idpChannelBean, "targetProvider", normalizeBeanName(target.getName()));
                // Set trustedProviders
                Set<Ref> trustedProviders = new HashSet<Ref>();
                Ref t = new Ref();
                t.setBean(normalizeBeanName(normalizeBeanName(target.getName())));
                trustedProviders.add(t);
                setPropertyRefs(idpChannelBean, "trustedProviders", trustedProviders);
            }

        } else {
            // Set trustedProviders
            Set<Ref> trustedProviders = new HashSet<Ref>();
            for (FederatedConnection fa : sp.getFederatedConnectionsA()) {

                if (fa.getChannelA().isOverrideProviderSetup())
                    continue;

                if (isIdPProxyRequired(fa, false)) {
                    Ref t = new Ref();
                    t.setBean(normalizeBeanName(fa.getRoleB().getName() + "-" + sp.getName() + "-idp-proxy"));
                    trustedProviders.add(t);
                } else {
                    Ref t = new Ref();
                    t.setBean(normalizeBeanName(fa.getRoleB().getName()));
                    trustedProviders.add(t);
                }
            }
            for (FederatedConnection fb : sp.getFederatedConnectionsB()) {
                if (fb.getChannelB().isOverrideProviderSetup())
                    continue;
                if (isIdPProxyRequired(fb, true)) {
                    Ref t = new Ref();
                    t.setBean(normalizeBeanName(fb.getRoleA().getName() + "-" + sp.getName() + "-idp-proxy"));
                    trustedProviders.add(t);

                } else {
                    Ref t = new Ref();
                    t.setBean(normalizeBeanName(fb.getRoleA().getName()));
                    trustedProviders.add(t);
                }
            }


            setPropertyRefs(idpChannelBean, "trustedProviders", trustedProviders);

        }
        setPropertyRef(idpChannelBean, "sessionManager", spBean.getName() + "-session-manager");
        setPropertyRef(idpChannelBean, "member", spMd.getName());
        
        // identityMediator
        Bean identityMediatorBean = getBean(spBeans, spBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + spBean.getName() + "-samlr2-mediator");

        setPropertyRef(idpChannelBean, "identityMediator", identityMediatorBean.getName());

        // accountLinkLifecycle
        setPropertyRef(idpChannelBean, "accountLinkLifecycle", spBean.getName() + "-account-link-lifecycle");

        // accountLinkEmitter
        Bean accountLinkEmitter = null;
        AccountLinkagePolicy ac = sp.getAccountLinkagePolicy();
        String accountLinkEmitterName = spBean.getName() + "-account-link-emitter";
        if (idpChannel != null) {
            ac = idpChannel.getAccountLinkagePolicy();
            accountLinkEmitterName = idpChannelBean.getName() + "-account-link-emitter";
        }
        AccountLinkEmitterType linkEmitterType = ac != null ? ac.getLinkEmitterType() : AccountLinkEmitterType.ONE_TO_ONE;
        switch (linkEmitterType) {
            case EMAIL:
                accountLinkEmitter = newBean(spBeans, accountLinkEmitterName, EmailAccountLinkEmitter.class);
                break;
            case UID:
                accountLinkEmitter = newBean(spBeans, accountLinkEmitterName, UidAccountLinkEmitter.class);
                break;
            case ONE_TO_ONE:
                accountLinkEmitter = newBean(spBeans, accountLinkEmitterName, OneToOneAccountLinkEmitter.class);
                break;
            case CUSTOM:
                Reference customAccountLinkEmitter = new Reference();
                customAccountLinkEmitter.setId(accountLinkEmitterName);
                customAccountLinkEmitter.setBeanName(ac.getCustomLinkEmitter());
                customAccountLinkEmitter.setInterface("org.atricore.idbus.kernel.main.federation.AccountLinkEmitter");
                beansOsgi.getImportsAndAliasAndBeen().add(customAccountLinkEmitter);
                break;
            default:
                accountLinkEmitter = newBean(spBeans, accountLinkEmitterName, OneToOneAccountLinkEmitter.class);
                break;
        }
        setPropertyRef(idpChannelBean, "accountLinkEmitter", accountLinkEmitterName);

        // identityMapper
        Bean identityMapper = null;
        IdentityMappingPolicy im = sp.getIdentityMappingPolicy();
        String identityMapperName = spBean.getName() + "-identity-mapper";
        if (idpChannel != null) {
            im = idpChannel.getIdentityMappingPolicy();
            identityMapperName = idpChannelBean.getName() + "-identity-mapper";
        }
        IdentityMappingType mappingType = im != null ? im.getMappingType() : IdentityMappingType.REMOTE;
        switch (mappingType) {
            case REMOTE:
                identityMapper = newBean(spBeans, identityMapperName, RemoteSubjectIdentityMapper.class);
                setPropertyValue(identityMapper, "useLocalId", im.isUseLocalId());
                break;
            case LOCAL:
                identityMapper = newBean(spBeans, identityMapperName, LocalSubjectIdentityMapper.class);
                break;
            case MERGED:
                identityMapper = newBean(spBeans, identityMapperName, MergedSubjectIdentityMapper.class);
                setPropertyValue(identityMapper, "useLocalId", im.isUseLocalId());
                break;
            case CUSTOM:
                Reference customIdentityMapper = new Reference();
                customIdentityMapper.setId(identityMapperName);
                customIdentityMapper.setBeanName(im.getCustomMapper());
                customIdentityMapper.setInterface("org.atricore.idbus.kernel.main.federation.IdentityMapper");
                beansOsgi.getImportsAndAliasAndBeen().add(customIdentityMapper);
                break;
            default:
                identityMapper = newBean(spBeans, identityMapperName, RemoteSubjectIdentityMapper.class);
                setPropertyValue(identityMapper, "useLocalId", im.isUseLocalId());
                break;
        }
        setPropertyRef(idpChannelBean, "identityMapper", identityMapperName);

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

        
        // IDP channel plans
        Bean sloToSamlPlan = newBean(spBeans, idpChannelName + "-spsso-samlr2sloreq-to-samlr2resp-plan", SamlR2SloRequestToSamlR2RespPlan.class);
        setPropertyRef(sloToSamlPlan, "bpmsManager", "bpms-manager");
        
        Bean spAuthnToSamlPlan = newBean(spBeans, idpChannelName + "-idpunsolicitedresponse-to-subject-plan", SPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(spAuthnToSamlPlan, "bpmsManager", "bpms-manager");

        Bean samlAuthnRespToSPAuthnResp = newBean(spBeans, idpChannelName + "-samlr2authnresp-to-ssospauthnresp-plan", SamlR2AuthnResponseToSPAuthnResponse.class);
        setPropertyRef(samlAuthnRespToSPAuthnResp, "bpmsManager", "bpms-manager");

        Bean samlArtResToSamlArtRespPlan = newBean(spBeans, idpChannelName + "-samlr2artresolve-to-samlr2artresponse-plan", SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan.class);
        setPropertyRef(samlArtResToSamlArtRespPlan, "bpmsManager", "bpms-manager");

        Bean samlArtToSamlArtResPlan = newBean(spBeans, idpChannelName + "-samlr2art-to-samlr2artresolve-plan", SamlR2ArtifactToSamlR2ArtifactResolvePlan.class);
        setPropertyRef(samlArtToSamlArtResPlan, "bpmsManager", "bpms-manager");

        // ---------------------------------------
        // IDP Channel Services
        // ---------------------------------------
        // SingleLogoutService

        if (sloEnabled) {
            // SAML2 SLO HTTP POST
            if (postEnabled) {
                Bean sloHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpPost.setName(idpChannelBean.getName() + "-saml2-slo-http-post");
                setPropertyValue(sloHttpPost, "name", sloHttpPost.getName());
                setPropertyValue(sloHttpPost, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpPost, "binding", SSOBinding.SAMLR2_POST.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                setPropertyRefs(sloHttpPost, "identityPlans", plansList);
                endpoints.add(sloHttpPost);
            }

            // SAML2 SLO HTTP ARTIFACT
            if (artifactEnabled) {
                Bean sloHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpArtifact.setName(idpChannelBean.getName() + "-saml2-slo-http-artifact");
                setPropertyValue(sloHttpArtifact, "name", sloHttpArtifact.getName());
                setPropertyValue(sloHttpArtifact, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpArtifact, "binding", SSOBinding.SAMLR2_ARTIFACT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                setPropertyRefs(sloHttpArtifact, "identityPlans", plansList);
                endpoints.add(sloHttpArtifact);
            }

            // SAML2 SLO HTTP REDIRECT
            if (redirectEnabled) {
                Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpRedirect.setName(idpChannelBean.getName() + "-saml2-slo-http-redirect");
                setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
                setPropertyValue(sloHttpRedirect, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpRedirect, "binding", SSOBinding.SAMLR2_REDIRECT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                setPropertyRefs(sloHttpRedirect, "identityPlans", plansList);
                endpoints.add(sloHttpRedirect);
            }

            // SAML2 SLO SOAP
            if (soapEnabled) {
                Bean sloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloSoap.setName(idpChannelBean.getName() + "-saml2-slo-soap");
                setPropertyValue(sloSoap, "name", sloSoap.getName());
                setPropertyValue(sloSoap, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloSoap, "binding", SSOBinding.SAMLR2_SOAP.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                setPropertyRefs(sloSoap, "identityPlans", plansList);
                endpoints.add(sloSoap);
            }

            // SAML2 SLO LOCAL
            Bean sloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            sloLocal.setName(idpChannelBean.getName() + "-saml2-slo-local");
            setPropertyValue(sloLocal, "name", sloLocal.getName());
            setPropertyValue(sloLocal, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
            setPropertyValue(sloLocal, "binding", SSOBinding.SAMLR2_LOCAL.getValue());
            // NOTE: location doesn't exist in simple-federation example
            setPropertyValue(sloLocal, "location", "local://" + (idpChannel != null ?
                    idpChannel.getLocation().getUri().toUpperCase() : sp.getLocation().getUri().toUpperCase()) + "/SAML2/SLO/LOCAL");

            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(sloToSamlPlan.getName());
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
                setPropertyValue(acHttpPost, "type", SSOMetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpPost, "binding", SSOBinding.SAMLR2_POST.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spAuthnToSamlPlan.getName());
                plansList.add(plan);

                Ref plan1 = new Ref();
                plan1.setBean(samlAuthnRespToSPAuthnResp.getName());
                plansList.add(plan1);

                setPropertyRefs(acHttpPost, "identityPlans", plansList);
                endpoints.add(acHttpPost);
            }

            if (artifactEnabled) {
                Bean acHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                acHttpArtifact.setName(idpChannelBean.getName() + "-saml2-ac-http-artifact");
                setPropertyValue(acHttpArtifact, "name", acHttpArtifact.getName());
                setPropertyValue(acHttpArtifact, "type", SSOMetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpArtifact, "binding", SSOBinding.SAMLR2_ARTIFACT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spAuthnToSamlPlan.getName());
                plansList.add(plan);

                Ref plan1 = new Ref();
                plan1.setBean(samlAuthnRespToSPAuthnResp.getName());
                plansList.add(plan1);

                setPropertyRefs(acHttpArtifact, "identityPlans", plansList);
                endpoints.add(acHttpArtifact);
            }

            if (redirectEnabled) {
                Bean acHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                acHttpRedirect.setName(idpChannelBean.getName() + "-saml2-ac-http-redirect");
                setPropertyValue(acHttpRedirect, "name", acHttpRedirect.getName());
                setPropertyValue(acHttpRedirect, "type", SSOMetadataConstants.AssertionConsumerService_QNAME.toString());
                setPropertyValue(acHttpRedirect, "binding", SSOBinding.SAMLR2_REDIRECT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(spAuthnToSamlPlan.getName());
                plansList.add(plan);

                Ref plan1 = new Ref();
                plan1.setBean(samlAuthnRespToSPAuthnResp.getName());
                plansList.add(plan1);

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
            setPropertyValue(arSoap, "type", SSOMetadataConstants.ArtifactResolutionService_QNAME.toString());
            setPropertyValue(arSoap, "binding", SSOBinding.SAMLR2_SOAP.getValue());
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(samlArtResToSamlArtRespPlan.getName());
            plansList.add(plan);
            Ref plan2 = new Ref();
            plan2.setBean(samlArtToSamlArtResPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(arSoap, "identityPlans", plansList);
            endpoints.add(arSoap);

            Bean arLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            arLocal.setName(idpChannelBean.getName() + "-saml2-ar-local");
            setPropertyValue(arLocal, "name", arLocal.getName());
            setPropertyValue(arLocal, "type", SSOMetadataConstants.ArtifactResolutionService_QNAME.toString());
            setPropertyValue(arLocal, "binding", SSOBinding.SAMLR2_LOCAL.getValue());
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(samlArtResToSamlArtRespPlan.getName());
            plansList.add(plan);
            plan2 = new Ref();
            plan2.setBean(samlArtToSamlArtResPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(arLocal, "identityPlans", plansList);
            endpoints.add(arLocal);
        }

        // Internal credentials callback
        {
            Bean credCallbackLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            credCallbackLocal.setName(idpChannelBean.getName() + "-sso-cc-local");
            setPropertyValue(credCallbackLocal, "name", credCallbackLocal.getName());
            setPropertyValue(credCallbackLocal, "type", SSOMetadataConstants.SPCredentialsCallbackService_QNAME.toString());
            setPropertyValue(credCallbackLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
            setPropertyValue(credCallbackLocal, "location",
                    "local://" + (idpChannel != null ? idpChannel.getLocation().getUri().toUpperCase() : sp.getLocation().getUri().toUpperCase()) + "/CCBACK/LOCAL");

            endpoints.add(credCallbackLocal);

        }

        setPropertyAsBeans(idpChannelBean, "endpoints", endpoints);

        if (idpChannel != null)
            addPropertyBeansAsRefsToSet(spSsoSvcBean, "overrideChannels", idpChannelBean);
        else
            setPropertyRef(spSsoSvcBean, "channel", idpChannelBean.getName());
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

            List<Bean> channels = getPropertyBeans(beans, mu, "channels");
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

