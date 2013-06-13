package com.atricore.idbus.console.lifecycle.main.transform.transformers.samlr2;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformEvent;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.AbstractTransformer;
import com.atricore.idbus.console.lifecycle.main.transform.transformers.sso.IdpFederatedConnectionTransformer;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Beans;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SamlR2MetadataDefinitionIntrospector;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan;
import org.atricore.idbus.capabilities.sso.main.binding.plans.SamlR2ArtifactToSamlR2ArtifactResolvePlan;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.EmailNameIDBuilder;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.SamlR2SecurityTokenToAuthnAssertionPlan;
import org.atricore.idbus.capabilities.sso.main.emitter.plans.UnspecifiedNameIDBuiler;
import org.atricore.idbus.capabilities.sso.main.idp.plans.IDPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2AuthnRequestToSamlR2ResponsePlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSamlR2RespPlan;
import org.atricore.idbus.capabilities.sso.main.idp.plans.SamlR2SloRequestToSpSamlR2SloRequestPlan;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SPInitiatedAuthnReqToSamlR2AuthnReqPlan;
import org.atricore.idbus.capabilities.sso.main.sp.plans.SamlR2AuthnResponseToSPAuthnResponse;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.federation.*;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.kernel.main.federation.metadata.ResourceCircleOfTrustMemberDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannelImpl;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannelImpl;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpointImpl;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.provider.FederationServiceImpl;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProviderImpl;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProviderImpl;
import org.atricore.idbus.kernel.main.util.HashGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.atricore.idbus.console.lifecycle.main.transform.transformers.util.ProxyUtil.isIdPProxyRequired;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.*;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.newBean;
import static com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils.setPropertyRefs;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlR2IdPProxyFederatedConnectionTransformer extends AbstractTransformer {

    private static final Log logger = LogFactory.getLog(IdpFederatedConnectionTransformer.class);

    protected String contextSPChannelBean = "spSsoProxyChannelBean";

    protected String contextIdPChannelBean = "idpSsoProxyChannelBean";

    protected String contextBindingChannelBean = "bindingSsoProxyChannelBean";

    private boolean roleA;

    public boolean isRoleA() {
        return roleA;
    }

    public void setRoleA(boolean roleA) {
        this.roleA = roleA;
    }

    public SamlR2IdPProxyFederatedConnectionTransformer() {

    }

    /**
     * Internal SAML 2.0 SPs connected to external SAML 2.0 IdPs and using a resource that requires special functionallity (OAuth, Domino, etc).
     */
    @Override
    public boolean accept(TransformEvent event) {

        if (event.getData() instanceof ServiceProviderChannel) {
            FederatedConnection fc = (FederatedConnection) event.getContext().getParentNode();
            return isIdPProxyRequired(fc, roleA);
        }

        return false;
    }

    @Override
    public void before(TransformEvent event) throws TransformException {

        FederatedConnection federatedConnection = (FederatedConnection) event.getContext().getParentNode();
        ServiceProviderChannel spChannel = (ServiceProviderChannel) event.getData();
        IdentityProviderChannel idpChannel = (IdentityProviderChannel) (roleA ? federatedConnection.getChannelB() : federatedConnection.getChannelA());

        ExternalSaml2IdentityProvider remoteIdentityProvider;
        InternalSaml2ServiceProvider localInternalSaml2ServiceProvider;

        if (roleA) {

            assert spChannel == federatedConnection.getChannelA() :
                    "SP Channel " + spChannel.getName() + " should be 'A' channel in federated connection " +
                            federatedConnection.getName();

            remoteIdentityProvider = (ExternalSaml2IdentityProvider) federatedConnection.getRoleA();
            localInternalSaml2ServiceProvider = (InternalSaml2ServiceProvider) federatedConnection.getRoleB();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelA();

            if (!remoteIdentityProvider.getName().equals(federatedConnection.getRoleA().getName()))
                throw new IllegalStateException("Context provider " + remoteIdentityProvider +
                        " is not roleA provider in Federated Connection " + federatedConnection.getName());

        } else {

            assert spChannel == federatedConnection.getChannelB() :
                    "SP Channel " + spChannel.getName() + " should be 'B' channel in federated connection " +
                            federatedConnection.getName();


            remoteIdentityProvider = (ExternalSaml2IdentityProvider) federatedConnection.getRoleB();
            localInternalSaml2ServiceProvider = (InternalSaml2ServiceProvider) federatedConnection.getRoleA();
            spChannel = (ServiceProviderChannel) federatedConnection.getChannelB();

            if (!remoteIdentityProvider.getName().equals(federatedConnection.getRoleB().getName()))
                throw new IllegalStateException("Context provider " + remoteIdentityProvider +
                        " is not roleB provider in Federated Connection " + federatedConnection.getName());

        }

        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");

        // Generated SP proxy and IDP Channel proxy
        generateIdPComponents(event, idpProxyBeans, remoteIdentityProvider, spChannel, localInternalSaml2ServiceProvider, idpChannel, federatedConnection, event.getContext());

        // Generated IDP proxy and SP Channel proxy
        generateSPComponents(event, idpProxyBeans, localInternalSaml2ServiceProvider,  idpChannel, remoteIdentityProvider, spChannel, federatedConnection, event.getContext());

    }

    @Override
    public Object after(TransformEvent event) throws TransformException {
        Beans beans = (Beans) event.getContext().get("beans");

        Bean idpChannelBean = (Bean) event.getContext().get(contextIdPChannelBean);
        Bean spChannelBean = (Bean) event.getContext().get(contextSPChannelBean);
        Bean bindingChannelBean = (Bean) event.getContext().get(contextBindingChannelBean);
        Beans idpProxyBeans = (Beans) event.getContext().get("idpProxyBeans");

        // Mediation Unit
        Collection<Bean> mus = getBeansOfType(beans, OsgiIdentityMediationUnit.class.getName());
        if (mus.size() == 1) {
            Bean mu = mus.iterator().next();

            List<Bean> channels = getPropertyBeans(beans, mu, "channels");
            boolean foundIdp = false;
            boolean foundSp = false;
            boolean foundBc = false;

            if (channels != null)
                for (Bean bean : channels) {
                    if (getPropertyValue(bean, "name").equals(getPropertyValue(idpChannelBean, "name"))) {
                        foundIdp = true;
                    }
                    if (getPropertyValue(bean, "name").equals(getPropertyValue(spChannelBean, "name"))) {
                        foundSp = true;
                    }
                    if (getPropertyValue(bean, "name").equals(getPropertyValue(bindingChannelBean, "name"))) {
                        foundBc = true;
                    }


                }

            if (!foundIdp)
                addPropertyBeansAsRefs(mu, "channels", idpChannelBean);

            if (!foundSp)
                addPropertyBeansAsRefs(mu, "channels", spChannelBean);

            if (!foundBc)
                addPropertyBeansAsRefs(mu, "channels", bindingChannelBean);

            return null;

        } else {
            throw new TransformException("One and only one Identity Mediation Unit is expected, found " + mus.size());
        }


    }

    protected void generateSPComponents(TransformEvent event,
                                        Beans spBeans,
                                        InternalSaml2ServiceProvider sp,
                                        IdentityProviderChannel idpChannel,
                                        FederatedProvider target,
                                        FederatedChannel targetChannel,
                                        FederatedConnection fc,
                                        IdApplianceTransformationContext ctx) throws TransformException {


        Beans beans = (Beans) ctx.get("beans");
        Beans beansOsgi = (Beans) ctx.get("beansOsgi");
        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for IdP Proxy Channel " + (idpChannel != null ? idpChannel.getName() : "default") + " of SP " + sp.getName());

        //---------------------------------------------
        // Get SP Bean
        //---------------------------------------------
        Bean spProxyBean = null;
        Collection<Bean> spbs = getBeansOfType(spBeans, ServiceProviderImpl.class.getName());
        if (spbs.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + spbs.size());
        }
        spProxyBean = spbs.iterator().next();

        //---------------------------------------------
        // Get IDP Bean
        //---------------------------------------------
        Bean idpProxyBean = null;
        Collection<Bean> idpbs = getBeansOfType(spBeans, IdentityProviderImpl.class.getName());
        if (spbs.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + spbs.size());
        }
        idpProxyBean = spbs.iterator().next();


        //---------------------------------------------
        // Get IDP default (SSO) federation service bean
        //---------------------------------------------
        Bean spSsoSvcBean = null;
        String spSsoSvcBeanName = getPropertyRef(spProxyBean, "defaultFederationService");
        String spSsoServiceType = "urn:oasis:names:tc:SAML:2.0";
        if (spSsoSvcBeanName == null) {
            spSsoSvcBeanName = spProxyBean.getName() + "-sso-default-svc";
            spSsoSvcBean = newBean(spBeans, spSsoSvcBeanName, FederationServiceImpl.class);
            setPropertyRef(spProxyBean, "defaultFederationService", spSsoSvcBeanName);
            setPropertyValue(spSsoSvcBean, "serviceType", spSsoServiceType);
            setPropertyValue(spSsoSvcBean, "name", spSsoSvcBeanName);
            // TODO : Profiles ?!
        }
        spSsoSvcBean = getBean(spBeans, spSsoSvcBeanName);

        String idpChannelName = spProxyBean.getName() +  "-sso-proxy-channel";
        String idauPath = (String) ctx.get("idauPath");


        // Check if we already created default channel
        if (idpChannel == null && getPropertyRef(spSsoSvcBean, "channel") != null) {
            ctx.put(contextIdPChannelBean, getBean(spBeans, idpChannelName));
            return;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating IdP Channel definition for " + idpChannelName);

        // Build a location for this channel, we use SP location as base
        Location idpChannelLocation = null;
        {
            Location spLocation = sp.getLocation();

            idpChannelLocation = new Location();
            idpChannelLocation.setProtocol(spLocation.getProtocol());
            idpChannelLocation.setHost(spLocation.getHost());
            idpChannelLocation.setPort(spLocation.getPort());
            idpChannelLocation.setContext(spLocation.getContext());

            // Don't use channel name since it's the default channel
            idpChannelLocation.setUri(appliance.getName().toUpperCase() + "/" + spProxyBean.getName().toUpperCase());

        }

        // COT Member Descriptor
        String mdName = spProxyBean.getName() + "-md";
        if (idpChannel != null) {
            mdName = idpChannelName + "-md";
        }
        Bean spMd = newBean(spBeans, mdName, ResourceCircleOfTrustMemberDescriptorImpl.class);

        String alias = idpChannelLocation.toString() + "/SAML2/MD";
        try {
            setPropertyValue(spMd, "id", HashGenerator.sha1(alias));
        } catch (UnsupportedEncodingException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': no such algorithm");
        }
        setPropertyValue(spMd, "alias", alias);
        String resourceName = idpChannel.getName();
        setPropertyValue(spMd, "resource", "classpath:" + idauPath + target.getName() + "/" + resourceName + "-samlr2-metadata.xml");

        Bean mdIntrospector = newAnonymousBean(SamlR2MetadataDefinitionIntrospector.class);
        setPropertyBean(spMd, "metadataIntrospector", mdIntrospector);

        // -------------------------------------------------------
        // IDP Channel
        // -------------------------------------------------------
        Bean idpChannelBean = newBean(spBeans, idpChannelName, IdPChannelImpl.class.getName());
        ctx.put(contextIdPChannelBean, idpChannelBean);

        // name
        setPropertyValue(idpChannelBean, "name", idpChannelName);
        setPropertyValue(idpChannelBean, "description", (idpChannel != null ? idpChannel.getDisplayName() : sp.getName()));

        setPropertyValue(idpChannelBean, "location", idpChannelLocation.toString());
        setPropertyRef(idpChannelBean, "federatedProvider", normalizeBeanName(spProxyBean.getName()));

        setPropertyRef(idpChannelBean, "targetProvider", normalizeBeanName(target.getName()));
        Set<Ref> trustedProviders = new HashSet<Ref>();
        Ref t = new Ref();
        t.setBean(normalizeBeanName(normalizeBeanName(target.getName()))) ;
        trustedProviders.add(t);
        setPropertyRefs(idpChannelBean, "trustedProviders", trustedProviders);

        setPropertyRef(idpChannelBean, "sessionManager", spProxyBean.getName() + "-session-manager");
        setPropertyRef(idpChannelBean, "member", spMd.getName());
        setPropertyRef(idpChannelBean, "proxy", idpProxyBean.getName() + "-sso-proxy-channel");
        setPropertyValue(idpChannelBean, "proxyModeEnabled", true);

        // identityMediator
        Bean identityMediatorBean = getBean(spBeans, spProxyBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + spProxyBean.getName() + "-samlr2-identity-mediator");

        setPropertyRef(idpChannelBean, "identityMediator", identityMediatorBean.getName());

        // accountLinkLifecycle
        setPropertyRef(idpChannelBean, "accountLinkLifecycle", spProxyBean.getName() + "-account-link-lifecycle");

        // accountLinkEmitter
        Bean accountLinkEmitter = null;
        AccountLinkagePolicy ac = sp.getAccountLinkagePolicy();
        String accountLinkEmitterName = spProxyBean.getName() + "-account-link-emitter";
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
        String identityMapperName = spProxyBean.getName() + "-identity-mapper";
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
        Bean sloToSamlPlan = newBean(spBeans, idpChannelName + "-spsso-samlr2sloreq-to-samlr2resp-plan", org.atricore.idbus.capabilities.sso.main.sp.plans.SamlR2SloRequestToSamlR2RespPlan.class);
        setPropertyRef(sloToSamlPlan, "bpmsManager", "bpms-manager");

        Bean spAuthnToSamlPlan = newBean(spBeans, idpChannelName + "-idpunsolicitedresponse-to-subject-plan", SPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(spAuthnToSamlPlan, "bpmsManager", "bpms-manager");

        Bean samlAuthnRespToSPAuthnResp = newBean(spBeans, idpChannelName + "-samlr2authnresp-to-ssospauthnresp-plan", SamlR2AuthnResponseToSPAuthnResponse.class);
        setPropertyRef(samlAuthnRespToSPAuthnResp, "bpmsManager", "bpms-manager");

        Bean samlArtResToSamlArtRespPlan = newBean(spBeans, idpChannelName + "-samlr2artresolve-to-samlr2artresponse-plan", SamlR2ArtifactResolveToSamlR2ArtifactResponsePlan.class);
        setPropertyRef(samlArtResToSamlArtRespPlan, "bpmsManager", "bpms-manager");

        Bean samlArtToSamlArtResPlan = newBean(spBeans, idpChannelName + "-samlr2art-to-samlr2artresolve-plan", SamlR2ArtifactToSamlR2ArtifactResolvePlan.class);
        setPropertyRef(samlArtToSamlArtResPlan, "bpmsManager", "bpms-manager");

        Bean samlr2IdpInitToSamlr2AuthnReqPlan = newBean(spBeans, idpChannelName + "-samlr2idpinitiatedauthnreq-to-samlr2authnreq-plan", IDPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(samlr2IdpInitToSamlr2AuthnReqPlan, "bpmsManager", "bpms-manager");


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
                    idpChannel.getLocation().getUri().toUpperCase() : idpChannelLocation.getUri().toUpperCase()) + "/SAML2/SLO/LOCAL");

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
                    "local://" + (idpChannel != null ? idpChannelLocation.getUri().toUpperCase() : sp.getLocation().getUri().toUpperCase()) + "/CCBACK/LOCAL");

            endpoints.add(credCallbackLocal);

        }

        setPropertyAsBeans(idpChannelBean, "endpoints", endpoints);
        setPropertyRef(spSsoSvcBean, "channel", idpChannelBean.getName());


    }



    protected void generateIdPComponents(TransformEvent event,
                                         Beans idpBeans,
                                         FederatedProvider remoteIdentityProvider,
                                         ServiceProviderChannel spChannel,
                                         FederatedProvider localServiceProvider,
                                         FederatedChannel targetChannel,
                                         FederatedConnection fc,
                                         IdApplianceTransformationContext ctx) throws TransformException {

        // If no channel is provided, we assume this is the default
        assert spChannel != null : "An SP Channel is required when using IdP Proxy";

        Beans beansOsgi = (Beans) ctx.get("beansOsgi");
        IdentityAppliance appliance = event.getContext().getProject().getIdAppliance();

        if (logger.isTraceEnabled())
            logger.trace("Generating Beans for SSO SP Channel of IdP Proxy " + remoteIdentityProvider.getName());

        //---------------------------------------------
        // Get IdP Proxy Bean
        //---------------------------------------------
        Bean idpProxyBean = null;
        Collection<Bean> idpbs = getBeansOfType(idpBeans, IdentityProviderImpl.class.getName());
        if (idpbs.size() != 1) {
            throw new TransformException("Invalid IdP definition count : " + idpbs.size());
        }
        idpProxyBean = idpbs.iterator().next();

        //---------------------------------------------
        // Get SP Proxy Bean
        //---------------------------------------------
        Bean spProxyBean = null;
        Collection<Bean> spbs = getBeansOfType(idpBeans, ServiceProviderImpl.class.getName());
        if (spbs.size() != 1) {
            throw new TransformException("Invalid SP definition count : " + spbs.size());
        }
        spProxyBean = spbs.iterator().next();


        //---------------------------------------------
        // Get IDP default (SSO) federation service bean
        //---------------------------------------------
        Bean idpSsoSvcBean = null;
        String idpSsoSvcBeanName = getPropertyRef(idpProxyBean, "defaultFederationService");
        String idpSsoServiceType = "urn:oasis:names:tc:SAML:2.0";
        if (idpSsoSvcBeanName == null) {
            idpSsoSvcBeanName = idpProxyBean.getName() + "-sso-default-svc";
            idpSsoSvcBean  = newBean(idpBeans, idpSsoSvcBeanName, FederationServiceImpl.class);
            setPropertyRef(idpProxyBean, "defaultFederationService", idpSsoSvcBeanName);
            setPropertyValue(idpSsoSvcBean, "serviceType", idpSsoServiceType);
            setPropertyValue(idpSsoSvcBean, "name", idpSsoSvcBeanName);
            // TODO : Profiles ?!
        }
        idpSsoSvcBean = getBean(idpBeans, idpSsoSvcBeanName);

        //---------------------------------------------
        // See if we already defined the channel
        //---------------------------------------------
        // SP Channel name : <idp-name>-sso-<sp-channel-name>-sp-channel, sso service is the default, and the one this transformer generates
        String spChannelName = idpProxyBean.getName() +  "-sso-proxy-channel";
        String idauPath = (String) ctx.get("idauPath");

        // Check if we already created default service
        if (getPropertyRef(idpSsoSvcBean, "channel") != null) {
            ctx.put(contextSPChannelBean, getBean(idpBeans, spChannelName));
            return;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating SP Channel definition for " + spChannelName);

        // Build a location for this channel, we use SP location as base
        Location spChannelLocation = null;
        {
            Location spLocation = localServiceProvider.getLocation();

            spChannelLocation = new Location();
            spChannelLocation.setProtocol(spLocation.getProtocol());
            spChannelLocation.setHost(spLocation.getHost());
            spChannelLocation.setPort(spLocation.getPort());
            spChannelLocation.setContext(spLocation.getContext());

            // Don't use channel name since it's the default channel
            spChannelLocation.setUri(appliance.getName().toUpperCase() + "/" + idpProxyBean.getName().toUpperCase());

        }

        // COT Member Descriptor
        String mdName = idpProxyBean.getName() + "-md";
        if (spChannel != null) {
            mdName = spChannelName + "-md";
        }
        Bean idpMd = newBean(idpBeans, mdName, ResourceCircleOfTrustMemberDescriptorImpl.class);
        String alias = spChannelLocation.toString() + "/SAML2/MD";
        try {
            setPropertyValue(idpMd, "id", HashGenerator.sha1(alias));
        } catch (UnsupportedEncodingException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': unsupported encoding");
        } catch (NoSuchAlgorithmException e) {
            throw new TransformException("Error generating SHA-1 hash for alias '" + alias + "': no such algorithm");
        }
        setPropertyValue(idpMd, "alias", alias);
        String resourceName = spChannel.getName();
        setPropertyValue(idpMd, "resource", "classpath:" + idauPath + normalizeBeanName(remoteIdentityProvider.getName()) + "/" + resourceName + "-samlr2-metadata.xml");

        Bean mdIntrospector = newAnonymousBean(SamlR2MetadataDefinitionIntrospector.class);
        setPropertyBean(idpMd, "metadataIntrospector", mdIntrospector);




        // -------------------------------------------------------
        // SP Channel
        // -------------------------------------------------------
        Bean spChannelBean = newBean(idpBeans, spChannelName, SPChannelImpl.class.getName());
        ctx.put(contextSPChannelBean, spChannelBean);

        setPropertyValue(spChannelBean, "name", spChannelName);
        setPropertyValue(spChannelBean, "description", "SP Channel proxy " + spChannel.getName());
        setPropertyValue(spChannelBean, "location", spChannelLocation.toString());
        setPropertyRef(spChannelBean, "federatedProvider", normalizeBeanName(idpProxyBean.getName()));

        // The name of the local SP
        setPropertyRef(spChannelBean, "targetProvider", normalizeBeanName(localServiceProvider.getName()));
        // Set trustedProviders
        Set<Ref> trustedProviders = new HashSet<Ref>();
        Ref t = new Ref();
        t.setBean(normalizeBeanName(localServiceProvider.getName()));
        trustedProviders.add(t);
        setPropertyRefs(spChannelBean, "trustedProviders", trustedProviders);


        setPropertyRef(spChannelBean, "sessionManager", idpProxyBean.getName() + "-session-manager");
        //setPropertyRef(spChannelBean, "identityManager", idpProxyBean.getName() + "-identity-manager");
        setPropertyRef(spChannelBean, "member", idpMd.getName());
        // Set bellow, when creating binding channel : setPropertyRef(spChannelBean, "proxy", <binding-channel>);
        setPropertyValue(spChannelBean, "proxyModeEnabled", true);
        setPropertyRef(spChannelBean, "securityTokenService", idpProxyBean.getName() + "-sts");

        // identityMediator
        Bean identityMediatorBean = getBean(idpBeans, idpProxyBean.getName() + "-samlr2-mediator");
        if (identityMediatorBean == null)
            throw new TransformException("No identity mediator defined for " + idpProxyBean.getName() + "-samlr2-identity-mediator");
        setPropertyRef(spChannelBean, "identityMediator", identityMediatorBean.getName());

        // -------------------------------------------------------
        // endpoints
        // -------------------------------------------------------
        List<Bean> endpoints = new ArrayList<Bean>();

        // profiles
        Set<Profile> activeProfiles = remoteIdentityProvider.getActiveProfiles();
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
        Set<Binding> activeBindings = remoteIdentityProvider.getActiveBindings();
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
        //setPropertyRef(stmtToAssertionPlan, "identityManager", idpProxyBean.getName() + "-identity-manager");

        // Add name id builders based on channel properties


        // Unspecified nameid builder
        Bean unspecifiedNameIdBuilder = newAnonymousBean(UnspecifiedNameIDBuiler.class);
        addPropertyBean(stmtToAssertionPlan, "nameIDBuilders", unspecifiedNameIdBuilder);

        // Email nameid builder
        Bean emailNameIdBuilder = newAnonymousBean(EmailNameIDBuilder.class);
        addPropertyBean(stmtToAssertionPlan, "nameIDBuilders", emailNameIdBuilder);

        SubjectNameIdentifierPolicy subjectNameIDPolicy = null;
        if (remoteIdentityProvider instanceof ExternalSaml2IdentityProvider) {
            subjectNameIDPolicy = spChannel != null ? spChannel.getSubjectNameIDPolicy() : null;
        } else if (remoteIdentityProvider instanceof IdentityProvider) {
            subjectNameIDPolicy = spChannel != null ? spChannel.getSubjectNameIDPolicy() : ((IdentityProvider)remoteIdentityProvider).getSubjectNameIDPolicy();
        }

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

                        String customNameIDBuilderName = idpProxyBean.getName() + "-custom-subject-name-id-builder";

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

        boolean ignoreRequestedNameIDPolicy = true;

        if (remoteIdentityProvider instanceof IdentityProvider) {
            ignoreRequestedNameIDPolicy = spChannel != null ? spChannel.isIgnoreRequestedNameIDPolicy() : ((IdentityProvider)remoteIdentityProvider).isIgnoreRequestedNameIDPolicy();
        } else if (remoteIdentityProvider instanceof ExternalSaml2IdentityProvider) {
            ignoreRequestedNameIDPolicy = spChannel != null ? spChannel.isIgnoreRequestedNameIDPolicy() : true;
        }

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

        // PROXY Endpoints:
        {

            List<Ref> plansList = new ArrayList<Ref>();

            // ACSPROXY
            Bean acsPrxyArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
            acsPrxyArtifact.setName(spChannelBean.getName() + "-sso-acspxy-artifact");
            setPropertyValue(acsPrxyArtifact, "name", acsPrxyArtifact.getName());
            setPropertyValue(acsPrxyArtifact, "type", SSOMetadataConstants.ProxyAssertionConsumerService_QName.toString());
            setPropertyValue(acsPrxyArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
            setPropertyValue(acsPrxyArtifact, "location", "/SSO/ACSPROXY/ARTIFACT");

            Ref plan1 = new Ref();
            plan1.setBean(stmtToAssertionPlan.getName());
            plansList.add(plan1);

            Ref plan2 = new Ref();
            plan2.setBean(samlr2IdpInitToSamlr2AuthnReqPlan.getName());
            plansList.add(plan2);

            Ref plan3 = new Ref();
            plan3.setBean(authnToSamlPlan.getName());
            plansList.add(plan3);

            setPropertyRefs(acsPrxyArtifact, "identityPlans", plansList);

            endpoints.add(acsPrxyArtifact);


        }

        {
            // TODO : SLOPROXY
            //Bean acsPrxyArtifact = newAnonymousBean(IdentityMediationEndpointImpl.class);
            //acsPrxyArtifact.setName(spChannelBean.getName() + "-sso-slopxy-artifact");
            //setPropertyValue(acsPrxyArtifact, "name", acsPrxyArtifact.getName());
            //setPropertyValue(acsPrxyArtifact, "type", SSOMetadataConstants.ProxyAssertionConsumerService_QName.toString());
            //setPropertyValue(acsPrxyArtifact, "binding", SSOBinding.SSO_ARTIFACT.getValue());
            /*
            List<Ref> plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(sloToSamlPlan.getName());
            plansList.add(plan);
            Ref plan2 = new Ref();
            plan2.setBean(sloToSamlSpSloPlan.getName());
            plansList.add(plan2);
            setPropertyRefs(acsPrxyArtifact, "identityPlans", plansList);
            */
            //endpoints.add(acsPrxyArtifact);

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
        setPropertyValue(shbLocal, "location", "local://" + spChannelLocation.getUri().toUpperCase() + "/SSO/SSHB/LOCAL");
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
            setPropertyValue(ssoSloLocal, "location", "local://" + spChannelLocation.getUri().toUpperCase()  + "/SAML2/SLO/LOCAL");
            plansList = new ArrayList<Ref>();
            plan = new Ref();
            plan.setBean(sloToSamlSpSloPlan.getName());
            plansList.add(plan);
            setPropertyRefs(ssoSloLocal, "identityPlans", plansList);
            endpoints.add(ssoSloLocal);
        }

        setPropertyAsBeans(spChannelBean, "endpoints", endpoints);
        setPropertyRef(idpSsoSvcBean, "channel", spChannelBean.getName());

        // -----------------------------------------------------------------------------------------
        // SP Channel binding
        // -----------------------------------------------------------------------------------------
        String bindingChannelName = idpProxyBean.getName() +  "-binding-channel";

        setPropertyRef(spChannelBean, "proxy", bindingChannelName);
        setPropertyRef(spProxyBean, "bindingChannel", bindingChannelName);

        Bean bindingChannelBean = newBean(idpBeans, bindingChannelName, BindingChannelImpl.class.getName());
        event.getContext().put(contextBindingChannelBean, bindingChannelBean);

        setPropertyValue(bindingChannelBean, "name", bindingChannelName);
        setPropertyValue(bindingChannelBean, "description", "IdP Proxy biding channel");
        setPropertyRef(bindingChannelBean, "federatedProvider", normalizeBeanName(spProxyBean.getName()));


        // Location
        // Build a location for this channel, we use SP location as base
        Location bindingChannelLocation = null;
        {
            Location bLocation = localServiceProvider.getLocation();

            bindingChannelLocation = new Location();
            bindingChannelLocation.setProtocol(bLocation.getProtocol());
            bindingChannelLocation.setHost(bLocation.getHost());
            bindingChannelLocation.setPort(bLocation.getPort());
            bindingChannelLocation.setContext(bLocation.getContext());

            // Don't use channel name since it's the default channel
            bindingChannelLocation.setUri(appliance.getName().toUpperCase() + "/" + bindingChannelName.toUpperCase());

        }

        setPropertyValue(bindingChannelBean, "location", bindingChannelLocation.toString());
        setPropertyRef(bindingChannelBean, "identityMediator", spProxyBean.getName() + "-samlr2-mediator");
        setPropertyRef(bindingChannelBean, "unitContainer", appliance.getName() + "-container");

        Bean spInitAuthnReqToSamlAuthnReqPlan = newBean(idpBeans, spProxyBean.getName() + "-spinitauthnreq-to-samlauthnreq-plan", SPInitiatedAuthnReqToSamlR2AuthnReqPlan.class);
        setPropertyRef(spInitAuthnReqToSamlAuthnReqPlan, "bpmsManager", "bpms-manager");


        // Binding endpoints
        List<Bean> bEndpoints = new ArrayList<Bean>();

        {
            // SPInitiatedSingleSignOnServiceProxy
            Bean spInitSsoProxy = newAnonymousBean(IdentityMediationEndpointImpl.class);
            spInitSsoProxy.setName(spChannelBean.getName() + "-sso-spinitpxy-artifact");
            setPropertyValue(spInitSsoProxy, "name", spInitSsoProxy.getName());
            setPropertyValue(spInitSsoProxy, "type", SSOMetadataConstants.SPInitiatedSingleSignOnServiceProxy_QNAME.toString());
            setPropertyValue(spInitSsoProxy, "binding", SSOBinding.SSO_ARTIFACT.getValue());
            setPropertyValue(spInitSsoProxy, "location", "/SSO/SPINITPXY/ARTIFACT");
            List<Ref>plansList = new ArrayList<Ref>();
            Ref plan = new Ref();
            plan.setBean(spInitAuthnReqToSamlAuthnReqPlan.getName());
            plansList.add(plan);
            setPropertyRefs(spInitSsoProxy, "identityPlans", plansList);
            bEndpoints.add(spInitSsoProxy);
        }

        {
            // Session Heart-Beat SOAP
            Bean spSessionHBeatSoap = newAnonymousBean(IdentityMediationEndpointImpl.class);
            spSessionHBeatSoap.setName(spChannelBean.getName() + "-sso-sshb-soap");
            setPropertyValue(spSessionHBeatSoap, "name", spSessionHBeatSoap.getName());
            setPropertyValue(spSessionHBeatSoap, "type", SSOMetadataConstants.SPSessionHeartBeatService_QNAME.toString());
            setPropertyValue(spSessionHBeatSoap, "binding", SSOBinding.SSO_SOAP.getValue());
            setPropertyValue(spSessionHBeatSoap, "location", "/SSO/SSHB/SOAP");
            bEndpoints.add(spSessionHBeatSoap);
        }

        {
            // Session Heart-Beat LOCAL
            Bean spSessionHBeatLocal = newAnonymousBean(IdentityMediationEndpointImpl.class);
            spSessionHBeatLocal.setName(spChannelBean.getName() + "-sso-sshb-local");
            setPropertyValue(spSessionHBeatLocal, "name", spSessionHBeatLocal.getName());
            setPropertyValue(spSessionHBeatLocal, "type", SSOMetadataConstants.SPSessionHeartBeatService_QNAME.toString());
            setPropertyValue(spSessionHBeatLocal, "binding", SSOBinding.SSO_LOCAL.getValue());
            setPropertyValue(spSessionHBeatLocal, "location", "/SSO/SSHB/LOCAL");
            bEndpoints.add(spSessionHBeatLocal);
        }

        setPropertyAsBeans(bindingChannelBean, "endpoints", bEndpoints);

    }


}
