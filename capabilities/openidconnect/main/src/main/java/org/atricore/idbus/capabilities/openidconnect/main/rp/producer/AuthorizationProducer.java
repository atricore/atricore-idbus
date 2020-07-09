package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Constants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;
import java.util.Set;

/**
 * Receives an Authentication Request (AuthorizationRequest for OAuth 2.0 standard) and issues an authorization token.
 *
 * This service ba
 */
public class AuthorizationProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(AuthorizationProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public AuthorizationProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        BindingChannel bChannel = (BindingChannel) channel;
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) bChannel.getIdentityMediator();

        // Received OpenIDConnect authentication request (Nimbus)
        AuthenticationRequest authnReq = (AuthenticationRequest) in.getMessage().getContent();
        RPAuthnContext authnCtx =
                (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        validateRequest(authnReq, mediator);

        // Create a SAML Authentication request based on configuration and received request.
        String idpAlias = null;
        String idpAliasB64 = in.getMessage().getState().getTransientVariable(OAuth2Constants.OAUTH2_IDPALIAS_VAR);

        if (idpAliasB64 == null) {
            idpAlias = authnCtx != null ? authnCtx.getIdpAlias() : null;

            if (logger.isDebugEnabled())
                logger.debug("Using previous idp alias " + idpAlias);
        } else {
            idpAlias = new String(Base64.decodeBase64(idpAliasB64.getBytes("UTF-8")));
        }

        // SSO endpoint
        BindingChannel spChannel = resolveSpBindingChannel(bChannel);
        EndpointDescriptor destination = resolveSPInitiatedSSOEndpointDescriptor(exchange, spChannel);

        // Create SP AuthnRequest
        SPInitiatedAuthnRequestType request = buildAuthnRequest(exchange, idpAlias, authnReq);

        // Create context information
        authnCtx = (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);
        if (authnCtx == null)
            authnCtx = new RPAuthnContext();

        authnCtx.setIdpAlias(idpAlias);
        authnCtx.setSsoAuthnRequest(request);
        authnCtx.setAuthnRequest(authnReq);

        // Store state
        in.getMessage().getState().setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(request.getID(),
                request,
                "SSOAuthnRequest",
                null,
                destination,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    /**
     * @return
     */
    protected SPInitiatedAuthnRequestType buildAuthnRequest(CamelMediationExchange exchange, String idpAlias, AuthenticationRequest authnReq) {

        Boolean passive = authnReq.getPrompt() != null && authnReq.getPrompt().toString().equals("none") ? true : null;
        Boolean forceAuthn = authnReq.getPrompt() != null && authnReq.getPrompt().toString().equals("login") ? true : null;

        // TODO : Support other prompt options: login, consent and select_account
        if (authnReq.getPrompt() != null && (authnReq.getPrompt().equals("consent")
                || authnReq.getPrompt().equals("select_account"))) {
            logger.warn("Unsupported 'prompt' value " + authnReq.getPrompt());
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPInitiatedAuthnRequestType req = new SPInitiatedAuthnRequestType();
        req.setID(uuidGenerator.generateId());
        req.setPassive(passive);
        req.setForceAuthn(forceAuthn);

        RequestAttributeType idpAliasAttr = new RequestAttributeType();
        idpAliasAttr.setName(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR);
        idpAliasAttr.setValue(idpAlias);
        req.getRequestAttribute().add(idpAliasAttr);

        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName(OIDC_EXT_NAMESPACE  + ":" + tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
            req.getRequestAttribute().add(a);
        }

        return req;
    }


    protected EndpointDescriptor resolveSPInitiatedSSOEndpointDescriptor(CamelMediationExchange exchange,
                                                                         BindingChannel sp) throws OpenIDConnectException {

        try {

            if (logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.SPInitiatedSingleSignOnService.toString());

            for (IdentityMediationEndpoint endpoint : sp.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleSignOnService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  sp.getIdentityMediator().resolveEndpoint(sp, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new OpenIDConnectException(e);
        }

        throw new OpenIDConnectException("No SP endpoint found for SP Initiated SSO using SSO Artifact binding");
    }

    protected void validateRequest(AuthenticationRequest authnReq, OpenIDConnectBPMediator mediator) throws OpenIDConnectException {
        OIDCClientInformation client = mediator.getClient();
        OIDCClientMetadata metadata = client.getOIDCMetadata();
        OIDCProviderMetadata idpMetadata = mediator.getProvider();

        // Verify redirect_uri
        URI requestedRedirectURI = authnReq.getRedirectionURI();
        Set<URI> redirectionURIs = metadata.getRedirectionURIs();

        if (!validateURI(redirectionURIs, requestedRedirectURI)) {

            if (logger.isDebugEnabled())
                logger.debug("Redirection_uri is invalid: " + requestedRedirectURI.toString());

            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST_URI, "redirection_uri is invalid: " + requestedRedirectURI.toString());
        }

        // Authz Grant
        CodeChallengeMethod ccm = authnReq.getCodeChallengeMethod();
        if (ccm != null) {

            // We received a code challenge method in the request.
            boolean ccmSupported = false;
            if (idpMetadata.getCodeChallengeMethods() != null) {
                for (CodeChallengeMethod supportedMethod : idpMetadata.getCodeChallengeMethods()) {
                    if (ccm.equals(supportedMethod)) {
                        ccmSupported = true;
                        break;
                    }
                }
            }

            if (!ccmSupported)
                throw new OpenIDConnectProviderException(OAuth2Error.ACCESS_DENIED.setURI(authnReq.getRedirectionURI()), "code_challenge_method not supported " + ccm);

        }

        // ClientID
        ClientID receivedClientID = authnReq.getClientID();
        if (!receivedClientID.equals(client.getID())) {
            if (logger.isDebugEnabled())
                logger.debug("client_id is not valid: " + receivedClientID);

            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_CLIENT.setURI(authnReq.getRedirectionURI()), "client_id is not valid: " + receivedClientID);
        }

        if (authnReq.getRequestURI() != null) {
            if (logger.isDebugEnabled())
                logger.debug("request resolution not supported: " + authnReq.getRequestURI().toString());

            throw new OpenIDConnectProviderException(OAuth2Error.REQUEST_URI_NOT_SUPPORTED.setURI(authnReq.getRedirectionURI()), "request resolution not supported");
        }

        // Verify response_type / response_mode consistency
        ResponseType responseType = authnReq.getResponseType();
        boolean rtOk = false;
        for (ResponseType rt : idpMetadata.getResponseTypes()) {
            if (rt.equals(responseType)) {
                rtOk = true;
                break;
            }
        }
        if (!rtOk) {
            if (logger.isDebugEnabled())
                logger.debug("ResponseType not supported by client " + responseType);

            throw new OpenIDConnectProviderException(OAuth2Error.REQUEST_NOT_SUPPORTED, "response_type not supported "+responseType+"]");
        }

    }
}
