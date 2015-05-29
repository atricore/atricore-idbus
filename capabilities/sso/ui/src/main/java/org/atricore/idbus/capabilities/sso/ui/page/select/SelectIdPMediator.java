package org.atricore.idbus.capabilities.sso.ui.page.select;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.main.select.selectors.UserSelectedIdPEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOUIApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.capabilities.sso.ui.page.error.SessionExpiredPage;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard.AppResource;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.*;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.Serializable;
import java.util.*;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/18/13
 */
public class SelectIdPMediator implements Serializable {

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    private static Log logger = LogFactory.getLog(SelectIdPMediator.class);

    private transient SSOUIApplication application;

    private IdentityMediationUnitRegistry idsuRegistry;
    private MessageQueueManager artifactQueueManager;
    private SSOWebSession session;
    private List<IdPModel> idpModels;
    private UserClaimsRequest userClaimsReq;
    private BasePage page;

    public SelectIdPMediator(BasePage page, IdentityMediationUnitRegistry idsuRegistry, MessageQueueManager artifactQueueManager, SSOUIApplication app, SSOWebSession session) {
        this.idsuRegistry = idsuRegistry;
        this.artifactQueueManager = artifactQueueManager;
        this.application = app;
        this.session = session;
        this.page = page;
    }

    public void onInitialize(PageParameters parameters) {

        String artifactId = null;
        if (parameters != null)
            artifactId = parameters.get(SsoHttpArtifactBinding.SSO_ARTIFACT_ID).toString();

        idpModels = new ArrayList<IdPModel>();
        userClaimsReq = null;

        WebAppConfig cfg = getApplication().getAppConfig();

        if (artifactId != null) {

            if (logger.isDebugEnabled())
                logger.debug("Artifact ID = " + artifactId);

            // Lookup for ClaimsRequest!
            try {
                userClaimsReq = (UserClaimsRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
            } catch (Exception e) {
                logger.error("Cannot resolve artifact id ["+artifactId+"] : " + e.getMessage(), e);
            }

            if (userClaimsReq != null) {

                getSession().setUserClaimsRequest(userClaimsReq);

                if (logger.isDebugEnabled())
                    logger.debug("Received select entity request " + userClaimsReq +
                            " for SP " + userClaimsReq.getAttribute("ServiceProvider)"));

            } else {
                logger.debug("No claims request received, try stored value");
                userClaimsReq = (getSession()).getUserClaimsRequest();
            }
        } else {
            userClaimsReq = getSession().getUserClaimsRequest();
        }

        logger.debug("claimsRequest = " + userClaimsReq);

        if (userClaimsReq == null) {
            // No way to process this page, fall-back
            WebBranding branding = getApplication().getBranding();
            if (branding.getFallbackUrl() != null) {
                // Redirect to fall-back (session expired !)
                throw new RestartResponseAtInterceptPageException(getApplication().resolvePage("ERROR/SESSION"));

            }
            // Redirect to Session Expired Page
            throw new RestartResponseAtInterceptPageException(getApplication().resolvePage("ERROR/SESSION"));
        }

        // We have the SP that issued the entity selection request.
        String spName = (String) userClaimsReq.getAttribute("ServiceProvider");
        if (spName == null)
            logger.error("No 'ServiceProvider' attribute received in user claims request : " + userClaimsReq.getId() + "["+userClaimsReq+"]");

        ServiceProvider sp = null;

        // IdPs list

        String unitName = cfg.getUnitName();
        if(unitName != null) {
            IdentityMediationUnit unit = idsuRegistry.lookupUnit(unitName);

            if (unit == null) {
                // TODO : Error
                logger.error("No Identity Mediation Unit found for " + unitName + "(maybe unit was not started yet)");
            } else {

                for (Channel c: unit.getChannels()) {
                    if (c instanceof IdPChannel) {
                        IdPChannel idpChannel = (IdPChannel) c;

                        if (idpChannel.getProvider().getName().equals(spName)) {
                            sp = (ServiceProvider) idpChannel.getProvider();
                            break;
                        }
                    }
                }

                if (sp != null) {

                    List<IdPChannel> idpChannels = new ArrayList<IdPChannel>();

                    if (sp.getChannel() instanceof  IdPChannel ) {
                        idpChannels.add((IdPChannel) sp.getChannel());
                    }

                    for (Channel c : sp.getChannels()) {
                        if (c instanceof IdPChannel) {
                            idpChannels.add((IdPChannel) c);
                        }
                    }

                    String spInitSso = null;
                    String spInitSlo = null;
                    for (IdentityMediationEndpoint endpoint : sp.getBindingChannel().getEndpoints()) {

                        if (logger.isTraceEnabled())
                            logger.trace("Checking endpoint : " + endpoint.getName());

                        if (endpoint.getType().equals(SSOService.SPInitiatedSingleSignOnService.toString())) {
                            spInitSso = sp.getBindingChannel().getLocation() + endpoint.getLocation();
                        } else if (endpoint.getType().equals(SSOService.SPInitiatedSingleLogoutService.toString())) {
                            spInitSlo = sp.getBindingChannel().getLocation() + endpoint.getLocation();
                        }
                    }

                    for (IdPChannel idpChannel : idpChannels) {

                        // SP Initiated SSO and SLO endpoint

                        // Create IdP models, go through trusted providers (IDPs)
                        Set<FederatedProvider> idps = idpChannel.getTrustedProviders();
                        for (FederatedProvider p : idps) {

                            String idpAlias = null;
                            String providerType = null;

                            if (p instanceof FederatedRemoteProvider) {
                                // Remote IdP has a single member descriptor

                                FederatedRemoteProvider idp = (FederatedRemoteProvider) p;

                                // Check if this is a SAML 2 IdP
                                if (idp.getRole().equals(SSOMetadataConstants.IDPSSODescriptor_QNAME.toString())) {
                                    // Get Entity ID and resource type
                                    CircleOfTrustMemberDescriptor d = idp.getMembers().get(0);
                                    idpAlias = d.getAlias();
                                    providerType = AppResource.SAML2_IDP_REMOTE.getResourceType();

                                } else {
                                    logger.warn("Unknown IdP role " + idp.getRole());
                                    continue;
                                }


                            } else  if (p instanceof IdentityProvider) {

                                providerType = AppResource.SAML2_IDP_LOCAL.getResourceType();

                                // Local IdPs may have dedicated channels to talk to us, with specific MD

                                IdentityProvider idp = (IdentityProvider) p;
                                // Get the proper SP Channel and look for the entity ID.

                                SPChannel spChannel = null;
                                for (Channel c : idp.getChannels()) {
                                    if (c instanceof SPChannel) {
                                        SPChannel spC = (SPChannel) c;
                                        if (spC.getTargetProvider() != null && spC.getTargetProvider().getName().equals(sp.getName())) {
                                            spChannel = spC;
                                            break;
                                        }
                                    }
                                }

                                // No override channel configured on IdP to talk to us, use default.
                                if (spChannel == null) {
                                    // This better be an SP Channel ...
                                    spChannel = (SPChannel) idp.getChannel();
                                }

                                if (logger.isTraceEnabled())
                                    logger.trace("SPChannel used to access IdP : " + spChannel.getName());

                                idpAlias = spChannel.getMember().getAlias();

                                if (spChannel.getProxy() != null)
                                    if (spChannel.getProxy() instanceof BindingChannel) {
                                        BindingChannel bc = (BindingChannel) spChannel.getProxy();
                                        FederatedRemoteProvider remoteIdP = null;
                                        if (bc.getFederatedProvider() instanceof BindingProvider) {
                                            BindingProvider bp = (BindingProvider) bc.getFederatedProvider();
                                            remoteIdP = bp.getProxy();
                                        } else {
                                            ServiceProvider localSp = (ServiceProvider) bc.getFederatedProvider();
                                            remoteIdP = (FederatedRemoteProvider)
                                                    localSp.getDefaultFederationService().getChannel().getTargetProvider();
                                        }

                                        if (logger.isTraceEnabled())
                                            logger.debug("Remote IdP available : " + remoteIdP.getName());

                                        // TODO: This IdP works as proxy, report remote service type based on remote idp

                                    } else {
                                        logger.error("Unsupported proxy binding channel type " + spChannel.getProxy());
                                    }


                            } else {
                                logger.error("Uknown Identity Provider type " + p.getClass().getName());
                                continue;
                            }


                            // Create the IDP model
                            IdPModel idpModel = new IdPModel(p.getName(),
                                    p.getName(),
                                    p.getDisplayName() != null ? p.getDisplayName() : p.getDescription(),
                                    p.getDescription(),
                                    idpAlias,
                                    spInitSso + "?"+ EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR+"=" + new String(Base64.encodeBase64(idpAlias.getBytes())),
                                    spInitSlo,
                                    providerType);

                            idpModels.add(idpModel);
                        }

                    }
                } else {
                    logger.error("Invalid SP Name received in claims request : " + spName);
                }

            }

        } else {
            // TODO : Error
            logger.error("No configuration property 'unitName' found for application " + cfg.getAppName());
        }

    }

    protected SSOWebSession getSession() {
        return session;
    }

    protected SSOUIApplication getApplication() {

        if (application == null)
            logger.error("No application instance found for SelectIdPMediator (UI), check transient property value");

        return application;
    }

    public List<IdPModel> getIdpModels() {
        return idpModels;
    }

    public void onSelectIdp(String name, boolean rememberSelection) {
        // TODO : !
        // We need to send a browser redirect, with the artifact Id and the response!

        try {
            for (IdPModel idp : idpModels) {
                if (idp.getName().equals(name)) {

                    ClaimSet claims = new ClaimSetImpl();
                    claims.addClaim(new UserClaimImpl(UserSelectedIdPEntitySelector.SELECTED_IDP_ALIAS_ATTR, idp.getEntityId()));

                    if (rememberSelection)
                        claims.addClaim(new UserClaimImpl(UserSelectedIdPEntitySelector.REMEMBER_IDP_ATTR, "TRUE"));


                    UserClaimsResponse response = new UserClaimsResponseImpl(uuidGenerator.generateId(),
                            null,
                            userClaimsReq.getId(),
                            claims,
                            userClaimsReq.getRelayState());

                    EndpointDescriptor claimsEndpoint = resolveClaimsEndpoint(userClaimsReq);
                    String claimsEndpointUrl = claimsEndpoint.getResponseLocation() != null ?
                            claimsEndpoint.getResponseLocation() : claimsEndpoint.getLocation();

                    Artifact artifact = artifactQueueManager.pushMessage(response);
                    claimsEndpointUrl += "?SSOArt=" + artifact.getContent();

                    if (logger.isDebugEnabled())
                        logger.debug("Returning claims to " + claimsEndpointUrl);

                    page.getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(claimsEndpointUrl));

                    return;
                }
            }

            logger.error("Cannot process user claims");
        } catch (Exception e) {
            // TODO : Error
            logger.error(e.getMessage(),  e);
        }

    }

    protected EndpointDescriptor resolveClaimsEndpoint(UserClaimsRequest claimsRequest) throws IdentityMediationException {

        for (IdentityMediationEndpoint endpoint : claimsRequest.getIssuerChannel().getEndpoints()) {
            // Look for unspecified claim endpoint using Artifacc binding

            if ("{urn:org:atricore:idbus:sso:metadata}IdPSelectorService".equals(endpoint.getType()) &&
                    SSOBinding.SSO_ARTIFACT.getValue().equals(endpoint.getBinding())) {

                if (logger.isDebugEnabled())
                    logger.debug("Resolved claims endpoint " + endpoint);

                return new EndpointDescriptorImpl(endpoint.getName(),
                        endpoint.getType(),
                        endpoint.getBinding(),
                        claimsRequest.getIssuerChannel().getLocation() + endpoint.getLocation(),
                        endpoint.getResponseLocation() != null ?
                                claimsRequest.getIssuerChannel().getLocation() + endpoint.getResponseLocation() : null);

            }
        }

        return null;
    }
}
