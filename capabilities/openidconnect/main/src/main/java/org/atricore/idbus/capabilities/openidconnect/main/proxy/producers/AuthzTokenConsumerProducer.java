package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.OpenIDConnectProducer;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.IOException;
import java.util.Collection;

/**
 * Receives an OAuth2 authorization code and requests the proper access token (back-channel)
 * Then an authn response is sent to the IdP proxy party
 *
 * Created by sgonzalez on 3/12/14.
 */
public class AuthzTokenConsumerProducer extends OpenIDConnectProducer {

    private static final Log logger = LogFactory.getLog(AuthzTokenConsumerProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public AuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        if (in.getMessage().getContent() instanceof AuthorizationCodeResponseUrl) {
            if (logger.isTraceEnabled())
                logger.trace("Processing AuthorizationCodeResponse");
            AuthorizationCodeResponseUrl authnResp = (AuthorizationCodeResponseUrl) in.getMessage().getContent();
            doProcessAuthzTokenResponse(exchange, authnResp);
        } else if (in.getMessage().getContent() instanceof IdTokenResponse) {
            if (logger.isTraceEnabled())
                logger.trace("Processing IdTokenResponse");
            IdTokenResponse idTokenResp = (IdTokenResponse) in.getMessage().getContent();
            doProcessIdTokenResponse(exchange, idTokenResp);
        }

    }

    protected void doProcessIdTokenResponse(CamelMediationExchange exchange, IdTokenResponse idTokenResp) {


        try {
            if (logger.isTraceEnabled())
                logger.trace("AccessToken" + idTokenResp.getAccessToken());

            String accessToken = idTokenResp.getAccessToken();
            Long expiresIn = idTokenResp.getExpiresInSeconds();
            IdToken idToken = idTokenResp.parseIdToken();
            IdToken.Payload payload = idToken.getPayload();

            String subject = payload.getSubject();

            // TODO

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthorizationCodeResponseUrl authnResp ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // OpenID Connect authorization code response
        String code = authnResp.getCode();

        if (authnResp.getError() != null) {
            // onError(req, resp, responseUrl);
            logger.error("Error received [" + authnResp.getError() + "] " + authnResp.getErrorDescription() + ", uri:" + authnResp.getErrorDescription());
            throw new OpenIDConnectException("OpenId Connect error: " + authnResp.getError() + authnResp.getErrorDescription());
        } else if (code == null) {
            logger.error("Missing authorization code ");
            throw new OpenIDConnectException("Illegal response, no authorization code received ");
        }

        // Validate relay state

        String expectedRelayState = (String) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:relayState");
        String relayState = authnResp.getState();
        if (!expectedRelayState.equals(relayState)) {
            // Invalid response
            if (logger.isDebugEnabled())
                logger.debug("Invalid state [" + relayState + "], expected [" + expectedRelayState + "]");

            throw new OpenIDConnectException("Illegal response, received OpenID Connect state is not valid");
        }

        // ---------------------------------------------------------------
        // Request access token
        // ---------------------------------------------------------------

        // Unfortunately, this does not work on the front channel. An HTTP POST is used, the response is not a
        // redirect, so the browser does not know how to handle it.  We use the HTTP agent instead.

        EndpointDescriptor accessTokenConsumerLocation = resolveAccessTokenConsumerEndpoint();

        // URL used to get the access token.
        GenericUrl requestUrl = new GenericUrl(mediator.getAccessTokenServiceLocation());

        if (logger.isDebugEnabled())
            logger.debug("AccessTokenConsumer: " + requestUrl.build());

        AuthorizationcodeTokenIdRequest request = new AuthorizationcodeTokenIdRequest (
                mediator.getHttpTransport(),
                mediator.getJacksonFactory(),
                requestUrl,
                code,
                mediator.getClientId(),
                mediator.getClientSecret());

        request.setRedirectUri(accessTokenConsumerLocation.getLocation());
        // request.setClientAuthentication(new BasicAuthentication(mediator.getClientId(), mediator.getClientSecret()));

        IdTokenResponse idTokenResponse = IdTokenResponse.execute(request);
        IdToken idToken = idTokenResponse.parseIdToken();

        String accessToken = idTokenResponse.getAccessToken();
        Long accessTokenExpiresIn = idTokenResponse.getExpiresInSeconds();
        String subject = idToken.getPayload().getSubject();
        String email = (String) idToken.getPayload().get("email");

        if (logger.isDebugEnabled())
            logger.debug("Authz token resolved to " + email);

        if (logger.isTraceEnabled())
            logger.trace("Access token ["+accessToken+"]");

        if (logger.isTraceEnabled())
            logger.trace("Access token expires in ["+accessTokenExpiresIn+"]");


        if (logger.isTraceEnabled())
            logger.trace("Subject ["+subject+"]");

        // ------------------------------------------------------------------------------
        // Send SP Authentication response
        // ------------------------------------------------------------------------------
        SPInitiatedAuthnRequestType authnRequest = (SPInitiatedAuthnRequestType) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:authnRequest");
        SPAuthnResponseType resp = null;

        SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
        ssoResponse.setID(uuidGenerator.generateId());
        ssoResponse.setIssuer(getFederatedProvider().getName());
        SPInitiatedAuthnRequestType ssoRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        if (ssoRequest != null) {
            ssoResponse.setInReplayTo(ssoRequest.getID());
        }

        SubjectType st = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(email);
        a.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:email");
        a.setLocalName(email);
        a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
        a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

        st.getAbstractPrincipal().add(a);

        ssoResponse.setSessionIndex(uuidGenerator.generateId());
        ssoResponse.setSubject(st);

        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(accessToken);
        ssoResponse.getSubjectAttributes().add(accessTokenAttr);

        SubjectAttributeType accessTokenExpiresInAttr = new SubjectAttributeType();
        accessTokenExpiresInAttr.setName("accessTokenExpiresIn");
        accessTokenExpiresInAttr.setValue(accessTokenExpiresIn + "");
        ssoResponse.getSubjectAttributes().add(accessTokenExpiresInAttr);

        SubjectAttributeType openIdSubjectAttr = new SubjectAttributeType();
        openIdSubjectAttr.setName("openIdSubject");
        openIdSubjectAttr.setValue(subject);
        ssoResponse.getSubjectAttributes().add(openIdSubjectAttr);

        // Send response back
        String destinationLocation = resolveSpProxyACS(authnRequest);

        if (logger.isTraceEnabled())
            logger.trace("Sending response to " + destinationLocation);

        EndpointDescriptor destination =
                new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "AssertionConsumerService",
                        OpenIDConnectBinding.SSO_ARTIFACT.getValue(),
                        destinationLocation, null);

        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResponse", "", destination, in.getMessage().getState()));

        exchange.setOut(out);
        return;


    }

    protected EndpointDescriptor resolveAccessTokenConsumerEndpoint() {

        String svc = OpenIDConnectConstants.AuthzTokenConsumerService_QNAME.toString();
        String binding = OpenIDConnectBinding.OPENID_HTTP_REDIR.toString();

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(svc)) {
                if (endpoint.getBinding().equals(binding))
                    return new EndpointDescriptorImpl(channel.getLocation(), endpoint);
            }
        }

        logger.warn("No endpoint found for service/binding " +
                svc +  "/" +
                binding);

        return null;
    }

    /**
     * Google client extension to support OpenIDConnect Authorization code request (not provided yet)
     */
    public class AuthorizationcodeTokenIdRequest extends AuthorizationCodeTokenRequest {


        @Key("client_id")
        private String clientId;

        @Key("client_secret")
        private String clientSecret;

        public AuthorizationcodeTokenIdRequest(HttpTransport transport,
                                               JsonFactory jsonFactory,
                                               GenericUrl tokenServerUrl,
                                               String code,
                                               String clientId,
                                               String clientSecret) {
            super(transport, jsonFactory, tokenServerUrl, code);
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public String getClientId() {
            return clientId;
        }

        public AuthorizationcodeTokenIdRequest setClientId(String clientId) {
            this.clientId = Preconditions.checkNotNull(clientId);
            return this;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public AuthorizationcodeTokenIdRequest setClientSecret(String clientSecret) {
            this.clientSecret = Preconditions.checkNotNull(clientSecret);
            return this;
        }

        @Override
        public AuthorizationcodeTokenIdRequest setRequestInitializer(
                HttpRequestInitializer requestInitializer) {
            return (AuthorizationcodeTokenIdRequest) super.setRequestInitializer(requestInitializer);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setTokenServerUrl(GenericUrl tokenServerUrl) {
            return (AuthorizationcodeTokenIdRequest) super.setTokenServerUrl(tokenServerUrl);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setScopes(Collection<String> scopes) {
            return (AuthorizationcodeTokenIdRequest) super.setScopes(scopes);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setGrantType(String grantType) {
            return (AuthorizationcodeTokenIdRequest) super.setGrantType(grantType);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setClientAuthentication(
                HttpExecuteInterceptor clientAuthentication) {
            return (AuthorizationcodeTokenIdRequest) super.setClientAuthentication(clientAuthentication);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setCode(String code) {
            return (AuthorizationcodeTokenIdRequest) super.setCode(code);
        }

        @Override
        public AuthorizationcodeTokenIdRequest setRedirectUri(String redirectUri) {
            return (AuthorizationcodeTokenIdRequest) super.setRedirectUri(redirectUri);
        }

        @Override
        public AuthorizationcodeTokenIdRequest set(String fieldName, Object value) {
            return (AuthorizationcodeTokenIdRequest) super.set(fieldName, value);
        }
    }


    public class AccessTokenRequestUrl extends GenericUrl {

        @Key("client_id")
        private String clientId;

        @Key("client_secret")
        private String clientSecret;

        public AccessTokenRequestUrl(String authorizationServerEncodedUrl,
                                     String clientId,
                                     String clientSecret) {

            super(authorizationServerEncodedUrl);
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }

    protected String resolveSpProxyACS(SPInitiatedAuthnRequestType authnRequest) {

        FederatedProvider issuerProvider = null;
        String issuer = authnRequest.getIssuer();
        if (logger.isDebugEnabled())
            logger.debug("Resolving issuer ["+issuer+"]");

        FederatedProvider provider = getFederatedProvider();
        for (FederatedProvider p : provider.getCircleOfTrust().getProviders()) {
            if (p.getName().equals(issuer)) {
                if (logger.isDebugEnabled())
                    logger.debug("Found issuer : " + p.getName());
                issuerProvider = p;
                break;
            }
        }

        // Look-up response endpoint for original issuer, this is not SAML, so we asume this is a local provider
        return ((OpenIDConnectProxyMediator) channel.getIdentityMediator()).getSpProxyACS();
    }

}
