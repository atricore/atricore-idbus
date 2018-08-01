package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.AuthorizationCodeTokenIdRequest;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sgonzalez on 2/25/15.
 */
public class GoogleAuthzTokenConsumerProducer extends AuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(GoogleAuthzTokenConsumerProducer.class);

    private static final int MAX_NUM_OF_USER_INFO_RETRIES = 3;

    public GoogleAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
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
            throw new OpenIDConnectException("OpenId Connect error: " + authnResp.getError() + " " +  authnResp.getErrorDescription());
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

        EndpointDescriptor accessTokenConsumerLocation = resolveAccessTokenConsumerEndpoint(OpenIDConnectConstants.GoogleAuthzTokenConsumerService_QNAME.toString());
        GenericUrl requestUrl = new GenericUrl(mediator.getAccessTokenServiceLocation());

        // URL used to get the access token.
        AuthorizationCodeTokenIdRequest request = new AuthorizationCodeTokenIdRequest(
                mediator.getHttpTransport(),
                mediator.getJacksonFactory(),
                requestUrl,
                code,
                mediator.getClientId(),
                mediator.getClientSecret());

        request.setRedirectUri(accessTokenConsumerLocation.getLocation());

        IdTokenResponse idTokenResponse = (IdTokenResponse) mediator.sendMessage(request, accessTokenConsumerLocation, channel);
        IdToken idToken = idTokenResponse.parseIdToken();

        String accessToken = idTokenResponse.getAccessToken();
        Long accessTokenExpiresIn = idTokenResponse.getExpiresInSeconds();
        String googleSubject = idToken.getPayload().getSubject();
        String email = (String) idToken.getPayload().get("email");

        if (logger.isDebugEnabled())
            logger.debug("Authz token resolved to " + email);

        if (logger.isTraceEnabled())
            logger.trace("Access token [" + accessToken + "]");

        if (logger.isTraceEnabled())
            logger.trace("Access token expires in [" + accessTokenExpiresIn + "]");

        if (logger.isTraceEnabled())
            logger.trace("Subject [" + googleSubject + "]");

        // get user info
        Oauth2 userInfoService = new Oauth2.Builder(mediator.getHttpTransport(), mediator.getJacksonFactory(),
                new GoogleCredential().setAccessToken(accessToken)).build();
        int retry = 0;
        Userinfoplus user = null;
        while (retry <= MAX_NUM_OF_USER_INFO_RETRIES) {
            try {
                user = userInfoService.userinfo().get().execute();
                break;
            } catch (IOException e) {
                retry++;
                logger.error(e.getMessage(), e);
                if (retry <= MAX_NUM_OF_USER_INFO_RETRIES) {
                    logger.debug("Getting Google user info, retry: " + retry);
                } else {
                    throw new IdentityMediationException(e);
                }
            }
        }
        if (user == null) {
            throw new IdentityMediationException("Google authorization failed!");
        }

        SubjectType subject;

        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();


        subject = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(email);
        a.setFormat(NameIDFormat.EMAIL.getValue());
        a.setLocalName(email);
        a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
        a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

        subject.getAbstractPrincipal().add(a);

        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(accessToken);
        attrs.add(accessTokenAttr);

        SubjectAttributeType accessTokenExpiresInAttr = new SubjectAttributeType();
        accessTokenExpiresInAttr.setName("accessTokenExpiresIn");
        accessTokenExpiresInAttr.setValue(accessTokenExpiresIn + "");
        attrs.add(accessTokenExpiresInAttr);

        SubjectAttributeType openIdSubjectAttr = new SubjectAttributeType();
        openIdSubjectAttr.setName("openIdSubject");
        openIdSubjectAttr.setValue(googleSubject);
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

    private void addUserAttributes(Userinfoplus user, List<SubjectAttributeType> attrs) {
        addUserAttribute(EMAIL_USER_ATTR_NAME, user.getEmail(), attrs);
        addUserAttribute(FIRST_NAME_USER_ATTR_NAME, user.getGivenName(), attrs);
        addUserAttribute(LAST_NAME_USER_ATTR_NAME, user.getFamilyName(), attrs);
        addUserAttribute(COMMON_NAME_USER_ATTR_NAME, user.getName(), attrs);
        addUserAttribute(GENDER_USER_ATTR_NAME, user.getGender(), attrs);
        addUserAttribute(LANGUAGE_USER_ATTR_NAME, user.getLocale(), attrs);
        addUserAttribute(PICTURE_USER_ATTR_NAME, user.getPicture(), attrs);
        addUserAttribute(PROFILE_LINK_USER_ATTR_NAME, user.getLink(), attrs);
        addUserAttribute(IS_VERIFIED_USER_ATTR_NAME, String.valueOf(user.isVerifiedEmail()), attrs);

        addUserAttribute("hostedDomain", user.getHd(), attrs);

        for (Map.Entry<String, Object> entry : user.getUnknownKeys().entrySet()) {
            if (entry.getValue() != null) {
                addUserAttribute(entry.getKey(), toJsonString(entry.getValue()), attrs);
            }
        }
    }
}
