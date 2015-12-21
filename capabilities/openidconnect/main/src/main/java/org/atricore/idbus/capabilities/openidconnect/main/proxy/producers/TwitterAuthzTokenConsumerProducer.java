package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth.OAuthCallbackUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.oauth.OAuthGetAccessTokenUsingPost;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TwitterAuthzTokenConsumerProducer extends AuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(TwitterAuthzTokenConsumerProducer.class);

    public TwitterAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        if (in.getMessage().getContent() instanceof OAuthCallbackUrl) {
            if (logger.isTraceEnabled())
                logger.trace("Processing OAuthCallback");
            OAuthCallbackUrl oauthCallback = (OAuthCallbackUrl) in.getMessage().getContent();
            doProcessOAuthCallbackResponse(exchange, oauthCallback);
        } else {
            throw new IdentityMediationException("Unknown message type " + in.getMessage().getContent());
        }
    }

    @Override
    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthorizationCodeResponseUrl authnResp) throws Exception {
    }

    protected void doProcessOAuthCallbackResponse(CamelMediationExchange exchange, OAuthCallbackUrl authnResp ) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // OAuth authorization response
        String token = authnResp.token;
        String verifier = authnResp.verifier;

        if (token == null) {
            logger.error("Missing oauth token");
            throw new OpenIDConnectException("Illegal response, no oauth token received");
        } else if (verifier == null) {
            logger.error("Missing oauth verifier");
            throw new OpenIDConnectException("Illegal response, no oauth verifier received");
        }

        OAuthHmacSigner signer = (OAuthHmacSigner) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:signer");
        String requestToken = (String) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:requestToken");

        // Validate request token
        if (!token.equals(requestToken)) {
            logger.error("Received token doesn't match original request token");
            throw new OpenIDConnectException("Illegal response, request tokens mismatch");
        }

        // Validate relay state
        String expectedRelayState = (String) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:relayState");
        ArrayList relayState = (ArrayList) authnResp.get("state");
        if (relayState == null || relayState.size() == 0 || !expectedRelayState.equals(relayState.get(0))) {
            // Invalid response
            if (logger.isDebugEnabled())
                logger.debug("Invalid state [" + relayState + "], expected [" + expectedRelayState + "]");

            throw new OpenIDConnectException("Illegal response, received OpenID Connect state is not valid");
        }

        // ---------------------------------------------------------------
        // Request access token
        // ---------------------------------------------------------------

        OAuthGetAccessToken accessTokenRequest = new OAuthGetAccessTokenUsingPost(mediator.getAccessTokenServiceLocation());
        accessTokenRequest.consumerKey = mediator.getClientId();
        accessTokenRequest.signer = signer;
        accessTokenRequest.transport = mediator.getHttpTransport();
        accessTokenRequest.temporaryToken = token;
        accessTokenRequest.verifier = verifier;

        OAuthCredentialsResponse accessTokenResponse = accessTokenRequest.execute();

        String accessToken = accessTokenResponse.token;
        String accessTokenSecret = accessTokenResponse.tokenSecret;

        if (logger.isTraceEnabled())
            logger.trace("Access token [" + accessToken + "]");

        // Get user profile
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        confBuilder.setOAuthConsumerKey(mediator.getClientId());
        confBuilder.setOAuthConsumerSecret(mediator.getClientSecret());
        confBuilder.setOAuthAccessToken(accessToken);
        confBuilder.setOAuthAccessTokenSecret(accessTokenSecret);
        confBuilder.setJSONStoreEnabled(true);
        TwitterFactory factory = new TwitterFactory(confBuilder.build());
        Twitter twitter = factory.getInstance();
        User user = twitter.verifyCredentials();

        SubjectType subject;

        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();

        subject = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(user.getName());
        a.setFormat(NameIDFormat.UNSPECIFIED.getValue());
        a.setLocalName(user.getName());
        a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
        a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

        subject.getAbstractPrincipal().add(a);

        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(accessToken);
        attrs.add(accessTokenAttr);

        SubjectAttributeType accessTokenSecretAttr = new SubjectAttributeType();
        accessTokenSecretAttr.setName("accessTokenSecret");
        accessTokenSecretAttr.setValue(accessTokenSecret);
        attrs.add(accessTokenSecretAttr);

        SubjectAttributeType accessTokenExpiresInAttr = new SubjectAttributeType();
        accessTokenExpiresInAttr.setName("accessTokenExpiresIn");
        accessTokenExpiresInAttr.setValue("0"); // Twitter access tokens do not expire
        attrs.add(accessTokenExpiresInAttr);

        SubjectAttributeType openIdSubjectAttr = new SubjectAttributeType();
        openIdSubjectAttr.setName("openIdSubject");
        openIdSubjectAttr.setValue(String.valueOf(user.getId()));
        attrs.add(openIdSubjectAttr);

        SubjectAttributeType authnCtxClassAttr = new SubjectAttributeType();
        authnCtxClassAttr.setName("authnCtxClass");
        authnCtxClassAttr.setValue(AuthnCtxClass.PPT_AUTHN_CTX.getValue());
        attrs.add(authnCtxClassAttr);

        addUserAttributes(user, attrs);

        SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
        ssoResponse.setID(uuidGenerator.generateId());
        ssoResponse.setIssuer(getFederatedProvider().getName());
        SPInitiatedAuthnRequestType ssoRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        if (ssoRequest != null) {
            ssoResponse.setInReplayTo(ssoRequest.getID());
        }

        ssoResponse.setSessionIndex(sessionUuidGenerator.generateId());
        ssoResponse.setSubject(subject);
        ssoResponse.getSubjectAttributes().addAll(attrs);

        // ------------------------------------------------------------------------------
        // Send SP Authentication response
        // ------------------------------------------------------------------------------
        SPInitiatedAuthnRequestType authnRequest = (SPInitiatedAuthnRequestType) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:authnRequest");

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

    private void addUserAttributes(User user, List<SubjectAttributeType> attrs) {
        addUserAttribute(FIRST_NAME_USER_ATTR_NAME, user.getName(), attrs);
        addUserAttribute(COMMON_NAME_USER_ATTR_NAME, user.getScreenName(), attrs);
        addUserAttribute(LANGUAGE_USER_ATTR_NAME, user.getLang(), attrs);
        addUserAttribute(PICTURE_USER_ATTR_NAME, user.getProfileImageURL(), attrs);
        addUserAttribute(IS_VERIFIED_USER_ATTR_NAME, String.valueOf(user.isVerified()), attrs);

        String userJSON = TwitterObjectFactory.getRawJSON(user);
        if (userJSON != null) {
            String attrName;
            Map<String, Object> map = (Map<String, Object>) fromJsonString(userJSON, Map.class);
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    attrName = entry.getKey();
                    if (entry.getValue() != null && !(attrName.equals("name") || attrName.equals("screen_name") ||
                            attrName.equals("lang") || attrName.equals("profile_image_url") || attrName.equals("verified"))) {
                        addUserAttribute(toJavaName(attrName), String.valueOf(entry.getValue()), attrs);
                    }
                }
            }
        }
    }
}
