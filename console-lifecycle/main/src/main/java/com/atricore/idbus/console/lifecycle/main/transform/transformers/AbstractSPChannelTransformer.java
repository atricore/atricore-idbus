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
import org.atricore.idbus.capabilities.sso.main.SamlR2MetadataDefinitionIntrospector;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactToSamlR2ArtifactResolvePlan;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.IDPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2AuthnRequestToSamlR2ResponsePlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.EmailNameIDBuilder;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.UnspecifiedNameIDBuiler;
import org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.FederationService;

import org.atricore.idbus.kernel.main.util.HashGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyValue;

public class AbstractSPChannelTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(AbstractSPChannelTransformer.class);

    protected String contextSpChannelBean = "spSsoChannelBean";

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

        // If no channel is provided, we asume this is the default
        boolean isDefaultChannel = spChannel == null;

        Beans idpBeans = (Beans) ctx.get("idpBeans");
        Beans beansOsgi = (Beans) ctx.get("beansOsgi");

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for SSO SP Channel " + (!isDefaultChannel ? spChannel.getName() : "default") + " of IdP " + idp.getName());

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
        Bean idpSsoSvcBean = null;
        String idpSsoSvcBeanName = getPropertyRef(idpBean, "defaultFederationService");
        String idpSsoServiceType = "urn:oasis:names:tc:SAML:2.0";
        if (idpSsoSvcBeanName == null) {
            idpSsoSvcBeanName = idpBean.getName() + "-sso-default-svc";
            idpSsoSvcBean  = newBean(idpBeans, idpSsoSvcBeanName, FederationService.class);
            setPropertyRef(idpBean, "defaultFederationService", idpSsoSvcBeanName);
            setPropertyValue(idpSsoSvcBean, "serviceType", idpSsoServiceType);
            setPropertyValue(idpSsoSvcBean, "name", idpSsoSvcBeanName);

        }
        idpSsoSvcBean = getBean(idpBeans, idpSsoSvcBeanName);

        //---------------------------------------------
        // See if we already defined the channel
        //---------------------------------------------
        // SP Channel name : <idp-name>-sso-<sp-channel-name>-sp-channel, sso service is the default, and the one this transformer generates
        String spChannelName = idpBean.getName() +  "-sso-" + (!isDefaultChannel ? normalizeBeanName(target.getName()) : "default") + "-sp-channel";


        String idauPath = (String) ctx.get("idauPath");

        // Check if we already created default service
        if (isDefaultChannel && getPropertyRef(idpSsoSvcBean, "channel") != null) {
            ctx.put(contextSpChannelBean, getBean(idpBeans, spChannelName));
            return;
        }

        // Check if we already created override channel
        if (spChannel != null) {
            Set<Bean> spChannelBeans = getPropertyBeansFromSet(idpBeans, idpSsoSvcBean, "overrideChannels");
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
        String mdName = idpBean.getName() + "-md";
        if (spChannel != null) {
            mdName = spChannelName + "-md";
        }
        Bean idpMd = newBean(idpBeans, mdName, ResourceCircleOfTrustMemberDescriptorImpl.class);
        String alias = resolveLocationUrl(idp, spChannel) + "/SAML2/MD";
        try {
            setPropertyValue(idpMd, "id", HashGenerator.sha1(alias));
        } catch (UnsupportedEncodingException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': no such algorithm");
        }
        setPropertyValue(idpMd, "alias", alias);
        String resourceName = idpBean.getName();
        if (spChannel != null) {
            resourceName = normalizeBeanName(spChannel.getName());
        }
        setPropertyValue(idpMd, "resource", "classpath:" + idauPath + idpBean.getName() + "/" + resourceName + "-samlr2-metadata.xml");

        Bean mdIntrospector = newAnonymousBean(SamlR2MetadataDefinitionIntrospector.class);
        setPropertyBean(idpMd, "metadataIntrospector", mdIntrospector);


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
        Bean identityMediatorBean = getBean(idpBeans, idpBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + idpBean.getName() + "-samlr2-identity-mediator");
        setPropertyRef(spChannelBean, "identityMediator", identityMediatorBean.getName());

        // -------------------------------------------------------
        // endpoints
        // -------------------------------------------------------
        List<Bean> endpoints = new ArrayList<Bean>();

        // profiles
        Set<Profile> activeProfiles = idp.getActiveProfiles();
        if (spChannel != null) {
            activeProfiles = spChannel.getActiveProfiles();
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
        Set<Binding> activeBindings = idp.getActiveBindings();
        if (spChannel != null) {
            activeBindings = spChannel.getActiveBindings();
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
        

        // SP Channel plans
        Bean sloToSamlPlan = newBean(idpBeans, spChannelName + "-samlr2sloreq-to-samlr2resp-plan", SamlR2SloRequestToSamlR2RespPlan.class);
        setPropertyRef(sloToSamlPlan, "bpmsManager", "bpms-manager");

        Bean sloToSamlSpSloPlan = newBean(idpBeans, spChannelName + "-samlr2sloreq-to-samlr2spsloreq-plan", SamlR2SloRequestToSpSamlR2SloRequestPlan.class);
        setPropertyRef(sloToSamlSpSloPlan, "bpmsManager", "bpms-manager");

        Bean authnToSamlPlan = newBean(idpBeans, spChannelName + "-samlr2authnreq-to-samlr2resp-plan", SamlR2AuthnRequestToSamlR2ResponsePlan.class);
        setPropertyRef(authnToSamlPlan, "bpmsManager", "bpms-manager");

        Bean stmtToAssertionPlan = newBean(idpBeans, spChannelName + "-samlr2authnstmt-to-samlr2assertion-plan", SamlR2SecurityTokenToAuthnAssertionPlan.class);
        setPropertyRef(stmtToAssertionPlan, "bpmsManager", "bpms-manager");
        setPropertyRef(stmtToAssertionPlan, "identityManager", idpBean.getName() + "-identity-manager");

        // Add name id builders based on channel properties


        // Unspecified nameid builder
        Bean unspecifiedNameIdBuilder = newAnonymousBean(UnspecifiedNameIDBuiler.class);
        addPropertyBean(stmtToAssertionPlan, "nameIDBuilders", unspecifiedNameIdBuilder);

        // Email nameid builder
        Bean emailNameIdBuilder = newAnonymousBean(EmailNameIDBuilder.class);
        addPropertyBean(stmtToAssertionPlan, "nameIDBuilders", emailNameIdBuilder);

        SubjectNameIdentifierPolicy subjectNameIDPolicy = spChannel != null ? spChannel.getSubjectNameIDPolicy() : idp.getSubjectNameIDPolicy();
        if (subjectNameIDPolicy != null) {

            // Set attribute if policy is defined
            if (subjectNameIDPolicy.getSubjectAttribute() != null)
                setPropertyValue(unspecifiedNameIdBuilder, "ssoUserProperty", subjectNameIDPolicy.getSubjectAttribute());

            if (subjectNameIDPolicy.getType() != null) {
                switch (subjectNameIDPolicy.getType()) {
                    case PRINCIPAL:
                        setPropertyBean(stmtToAssertionPlan, "defaultNameIDBuilder", unspecifiedNameIdBuilder);
                        break;
                    case EMAIL:
                        setPropertyBean(stmtToAssertionPlan, "defaultNameIDBuilder", emailNameIdBuilder);
                        break;
                    case CUSTOM:
                        // Define CUSTOM builder

                        String customNameIDBuilderName = idpBean.getName() + "-custom-subject-name-id-builder";

                        CustomNameIdentifierPolicy cp = (CustomNameIdentifierPolicy) subjectNameIDPolicy;

                        Reference customNameIDBuilder = new Reference();
                        customNameIDBuilder.setId(customNameIDBuilderName);
                        customNameIDBuilder.setBeanName(cp.getCustomNameIDBuilder());
                        customNameIDBuilder.setInterface("org.atricore.idbus.kernel.main.federation.AccountLinkEmitter");
                        beansOsgi.getImportsAndAliasAndBeen().add(customNameIDBuilder);

                        addPropertyRefsToSet(stmtToAssertionPlan, "nameIDBuilders", customNameIDBuilderName);

                        // set it as default
                        setPropertyRef(stmtToAssertionPlan, "defaultNameIDBuilder", customNameIDBuilderName);
                        break;
                    default:
                        setPropertyBean(stmtToAssertionPlan, "defaultNameIDBuilder", unspecifiedNameIdBuilder);
                        break;

                }
            } else {
                // Default is principal
                setPropertyBean(stmtToAssertionPlan, "defaultNameIDBuilder", unspecifiedNameIdBuilder);
            }
        } else {
            // Default is principal
            setPropertyBean(stmtToAssertionPlan, "defaultNameIDBuilder", unspecifiedNameIdBuilder);
        }

        boolean ignoreRequestedNameIDPolicy = spChannel != null ? spChannel.isIgnoreRequestedNameIDPolicy() : idp.isIgnoreRequestedNameIDPolicy();
        setPropertyValue(stmtToAssertionPlan, "ignoreRequestedNameIDPolicy", ignoreRequestedNameIDPolicy);

        Bean samlArtResToSamlArtRespPlan = newBean(idpBeans, spChannelName + "-samlr2artresolve-to-samlr2artresponse-plan", SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan.class);
        setPropertyRef(samlArtResToSamlArtRespPlan, "bpmsManager", "bpms-manager");

        Bean samlArtToSamlArtResPlan = newBean(idpBeans, spChannelName + "-samlr2art-to-samlr2artresolve-plan", SamlR2ArtifactToSamlR2ArtifactResolvePlan.class);
        setPropertyRef(samlArtToSamlArtResPlan, "bpmsManager", "bpms-manager");

        Bean samlr2IdpInitToSamlr2AuthnReqPlan = newBean(idpBeans, spChannelName + "-samlr2idpinitiatedauthnreq-to-samlr2authnreq-plan", IDPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(samlr2IdpInitToSamlr2AuthnReqPlan, "bpmsManager", "bpms-manager");

        // ---------------------------------------
        // SP Channel Services
        // ---------------------------------------

        // SingleLogoutService

        if (sloEnabled) {
            // SAML2 SLO HTTP POST
            if (postEnabled) {
                Bean sloHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpPost.setName(spChannelBean.getName() + "-saml2-slo-http-post");
                setPropertyValue(sloHttpPost, "name", sloHttpPost.getName());
                setPropertyValue(sloHttpPost, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpPost, "binding", SSOBinding.SAMLR2_POST.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(sloToSamlSpSloPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(sloHttpPost, "identityPlans", plansList);
                endpoints.add(sloHttpPost);
            }

            // SAML2 SLO HTTP ARTIFACT
            if (artifactEnabled) {
                Bean sloHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpArtifact.setName(spChannelBean.getName() + "-saml2-slo-http-artifact");
                setPropertyValue(sloHttpArtifact, "name", sloHttpArtifact.getName());
                setPropertyValue(sloHttpArtifact, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpArtifact, "binding", SSOBinding.SAMLR2_ARTIFACT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(sloToSamlSpSloPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(sloHttpArtifact, "identityPlans", plansList);
                endpoints.add(sloHttpArtifact);
            }

            // SAML2 SLO HTTP REDIRECT
            if (redirectEnabled) {
                Bean sloHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloHttpRedirect.setName(spChannelBean.getName() + "-saml2-slo-http-redirect");
                setPropertyValue(sloHttpRedirect, "name", sloHttpRedirect.getName());
                setPropertyValue(sloHttpRedirect, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloHttpRedirect, "binding", SSOBinding.SAMLR2_REDIRECT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(sloToSamlSpSloPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(sloHttpRedirect, "identityPlans", plansList);
                endpoints.add(sloHttpRedirect);
            }

            // SAML2 SLO SOAP
            if (soapEnabled) {
                Bean sloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
                sloSoap.setName(spChannelBean.getName() + "-saml2-slo-soap");
                setPropertyValue(sloSoap, "name", sloSoap.getName());
                setPropertyValue(sloSoap, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
                setPropertyValue(sloSoap, "binding", SSOBinding.SAMLR2_SOAP.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(sloToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(sloToSamlSpSloPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(sloSoap, "identityPlans", plansList);
                endpoints.add(sloSoap);
            }

            // SAML2 SLO LOCAL
            Bean sloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            sloLocal.setName(spChannelBean.getName() + "-saml2-slo-local");
            setPropertyValue(sloLocal, "name", sloLocal.getName());
            setPropertyValue(sloLocal, "type", SSOMetadataConstants.SingleLogoutService_QNAME.toString());
            setPropertyValue(sloLocal, "binding", SSOBinding.SAMLR2_LOCAL.getValue());
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(sloToSamlPlan.getName());
            plansList.add(plan);
            Ref plan2 = new Ref();
            plan2.setBean(sloToSamlSpSloPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(sloLocal, "identityPlans", plansList);
            endpoints.add(sloLocal);
        }

        // SingleSignOnService

        if (ssoEnabled) {
            // SAML2 SSO HTTP POST
            if (postEnabled) {
                Bean ssoHttpPost = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ssoHttpPost.setName(spChannelBean.getName() + "-saml2-sso-http-post");
                setPropertyValue(ssoHttpPost, "name", ssoHttpPost.getName());
                setPropertyValue(ssoHttpPost, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
                setPropertyValue(ssoHttpPost, "binding", SSOBinding.SAMLR2_POST.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(authnToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(stmtToAssertionPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(ssoHttpPost, "identityPlans", plansList);
                endpoints.add(ssoHttpPost);
            }

            // SAML2 SSO HTTP ARTIFACT
            if (artifactEnabled) {
                Bean ssoHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ssoHttpArtifact.setName(spChannelBean.getName() + "-saml2-sso-http-artifact");
                setPropertyValue(ssoHttpArtifact, "name", ssoHttpArtifact.getName());
                setPropertyValue(ssoHttpArtifact, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
                setPropertyValue(ssoHttpArtifact, "binding", SSOBinding.SAMLR2_ARTIFACT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(authnToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(stmtToAssertionPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(ssoHttpArtifact, "identityPlans", plansList);
                endpoints.add(ssoHttpArtifact);
            }

            // SAML2 SSO HTTP REDIRECT
            if (redirectEnabled) {
                Bean ssoHttpRedirect = newAnonymousBean(IdentityMediationEndpointImpl.class);
                ssoHttpRedirect.setName(spChannelBean.getName() + "-saml2-sso-http-redirect");
                setPropertyValue(ssoHttpRedirect, "name", ssoHttpRedirect.getName());
                setPropertyValue(ssoHttpRedirect, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
                setPropertyValue(ssoHttpRedirect, "binding", SSOBinding.SAMLR2_REDIRECT.getValue());
                List<Ref> plansList = new ArrayList<Ref>();
                Ref plan = new Ref();
                plan.setBean(authnToSamlPlan.getName());
                plansList.add(plan);
                Ref plan2 = new Ref();
                plan2.setBean(stmtToAssertionPlan.getName());
                plansList.add(plan2);
                setPropertyRefs(ssoHttpRedirect, "identityPlans", plansList);
                endpoints.add(ssoHttpRedirect);
            }
        }

        // ArtifactResolutionService must always be enabled just in case other providers support this binding
        //if (artifactEnabled)
        {
            Bean arSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
            arSoap.setName(spChannelBean.getName() + "-saml2-ar-soap");
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
            arLocal.setName(spChannelBean.getName() + "-saml2-ar-local");
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

            Bean arSoap11 = newAnonymousBean(IdentityMediationEndpointImpl.class);
            arSoap11.setName(spChannelBean.getName() + "-saml11-ar-soap");
            setPropertyValue(arSoap11, "name", arSoap11.getName());
            setPropertyValue(arSoap11, "type", SSOMetadataConstants.ArtifactResolutionService_QNAME.toString());
            setPropertyValue(arSoap11, "binding", SSOBinding.SAMLR11_SOAP.getValue());
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(samlArtResToSamlArtRespPlan.getName());
            plansList.add(plan);
            plan2 = new Ref();
            plan2.setBean(samlArtToSamlArtResPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(arSoap11, "identityPlans", plansList);
            endpoints.add(arSoap11);
        }

        // IDP Initiated SSO
        if (ssoEnabled) {
            Bean idpSsoInit11 = newAnonymousBean(IdentityMediationEndpointImpl.class);
            idpSsoInit11.setName(spChannelBean.getName() + "-idp-initiated-saml11");
            setPropertyValue(idpSsoInit11, "name", idpSsoInit11.getName());
            setPropertyValue(idpSsoInit11, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
            setPropertyValue(idpSsoInit11, "binding", SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML11.getValue());
            setPropertyValue(idpSsoInit11, "location", "/SAML11/SSO/IDP_INITIATE");
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(samlr2IdpInitToSamlr2AuthnReqPlan.getName());
            plansList.add(plan);
            setPropertyRefs(idpSsoInit11, "identityPlans", plansList);
            endpoints.add(idpSsoInit11);

            Bean idpSsoInit2 = newAnonymousBean(IdentityMediationEndpointImpl.class);
            idpSsoInit2.setName(spChannelBean.getName() + "-idp-initiated-saml2");
            setPropertyValue(idpSsoInit2, "name", idpSsoInit2.getName());
            setPropertyValue(idpSsoInit2, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
            setPropertyValue(idpSsoInit2, "binding", SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue());
            setPropertyValue(idpSsoInit2, "location", "/SAML2/SSO/IDP_INITIATE");
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(samlr2IdpInitToSamlr2AuthnReqPlan.getName());
            plansList.add(plan);
            setPropertyRefs(idpSsoInit2, "identityPlans", plansList);
            endpoints.add(idpSsoInit2);
        }

        // SessionHeartBeatService (non-saml)

        // SSO SHB SOAP
        Bean shbSOAP = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbSOAP.setName(spChannelBean.getName() + "-sso-shb-soap");
        setPropertyValue(shbSOAP, "name", shbSOAP.getName());
        setPropertyValue(shbSOAP, "type", SSOMetadataConstants.IDPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbSOAP, "binding", SSOBinding.SSO_SOAP.getValue());
        setPropertyValue(shbSOAP, "location", "/SSO/SSHB/SOAP");
        endpoints.add(shbSOAP);

        // SSO SHB LOCAL
        Bean shbLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
        shbLocal.setName(spChannelBean.getName() + "-sso-shb-local");
        setPropertyValue(shbLocal, "name", shbLocal.getName());
        setPropertyValue(shbLocal, "type", SSOMetadataConstants.IDPSessionHeartBeatService_QNAME.toString());
        setPropertyValue(shbLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
        setPropertyValue(shbLocal, "location", "local://" + (spChannel != null ?
                spChannel.getLocation().getUri().toUpperCase() : idp.getLocation().getUri().toUpperCase()) + "/SSO/SSHB/LOCAL");
        endpoints.add(shbLocal);

        // SSO SSO HTTP ARTIFACT
        if (ssoEnabled) {
            Bean ssoSsoHttpArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
            ssoSsoHttpArtifact.setName(spChannelBean.getName() + "-sso-sso-http-artifact");
            setPropertyValue(ssoSsoHttpArtifact, "name", ssoSsoHttpArtifact.getName());
            setPropertyValue(ssoSsoHttpArtifact, "type", SSOMetadataConstants.SingleSignOnService_QNAME.toString());
            setPropertyValue(ssoSsoHttpArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
            setPropertyValue(ssoSsoHttpArtifact, "location", "/SSO/SSO/ARTIFACT");
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(authnToSamlPlan.getName());
            plansList.add(plan);
            Ref plan2 = new Ref();
            plan2.setBean(stmtToAssertionPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(ssoSsoHttpArtifact, "identityPlans", plansList);
            endpoints.add(ssoSsoHttpArtifact);
        }

        // IDP Initiated SLO
        if (sloEnabled) {

            // SSO SLO SOAP
            Bean ssoSloSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
            ssoSloSoap.setName(spChannelBean.getName() + "-sso-slo-soap");
            setPropertyValue(ssoSloSoap, "name", ssoSloSoap.getName());
            setPropertyValue(ssoSloSoap, "type", SSOMetadataConstants.IDPInitiatedSingleLogoutService_QNAME.toString());
            setPropertyValue(ssoSloSoap, "binding", SSOBinding.SSO_SOAP.getValue());
            setPropertyValue(ssoSloSoap, "location", "/SSO/SLO/SOAP");
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(sloToSamlSpSloPlan.getName());
            plansList.add(plan);
            setPropertyRefs(ssoSloSoap, "identityPlans", plansList);
            endpoints.add(ssoSloSoap);

            // SSO SLO LOCAL
            Bean ssoSloLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            ssoSloLocal.setName(spChannelBean.getName() + "-sso-slo-local");
            setPropertyValue(ssoSloLocal, "name", ssoSloLocal.getName());
            setPropertyValue(ssoSloLocal, "type", SSOMetadataConstants.IDPInitiatedSingleLogoutService_QNAME.toString());
            setPropertyValue(ssoSloLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
            setPropertyValue(ssoSloLocal, "location", "local://" + (spChannel != null ?
                    spChannel.getLocation().getUri().toUpperCase() : idp.getLocation().getUri().toUpperCase()) + "/SAML2/SLO/LOCAL");
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(sloToSamlSpSloPlan.getName());
            plansList.add(plan);
            setPropertyRefs(ssoSloLocal, "identityPlans", plansList);
            endpoints.add(ssoSloLocal);
        }
        
        setPropertyAsBeans(spChannelBean, "endpoints", endpoints);
        
        //Bean authnToSamlResponsePlan = newBean(idpBeans, "samlr2authnreq-to-samlr2response-plan", SamlR2AuthnReqToSamlR2RespPlan.class);
        //setPropertyRef(authnToSamlResponsePlan, "bpmsManager", "bpms-manager");

        if (!isDefaultChannel)
            addPropertyBeansAsRefsToSet(idpSsoSvcBean, "overrideChannels", spChannelBean);
        else
            setPropertyRef(idpSsoSvcBean, "channel", spChannelBean.getName());
    }

    @Override
    public Object after(TransformEvent event) throws TransformException {

        // SP Channel bean
        Bean spChannelBean = (Bean) event.getContext().get(contextSpChannelBean);
        Bean idpBean = (Bean) event.getContext().get("idpBean");
        Beans idpBeans = (Beans) event.getContext().get("idpBeans");
        Beans beans = (Beans) event.getContext().get("beans");

        // The same Claim Providers and STS are used for the IDP in all channels!

        // claimsProvider
        String claimChannelName = idpBean.getName() + "-claim-channel";
        Collection<Bean> claimChannels = getBeansOfType(idpBeans, ClaimChannelImpl.class.getName());

        for (Bean claimChannel : claimChannels) {
            if (claimChannel == null)
                throw new TransformException("No claim channel defined as " + claimChannelName);
            addPropertyBeansAsRefs(spChannelBean, "claimProviders", claimChannel);
        }

        // STS
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
