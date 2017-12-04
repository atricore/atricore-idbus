package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgonzalez on 2/25/15.
 */
public class FacebookAuthzTokenConsumerProducer extends AuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(FacebookAuthzTokenConsumerProducer.class);

    private static final int MAX_NUM_OF_FB_API_CALL_RETRIES = 3;

    private static final String TOKEN_FOR_BUSINESS_USER_ATTR_NAME = "fbBusinessToken";

    public FacebookAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthorizationCodeResponseUrl authnResp) throws Exception {

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
        EndpointDescriptor response_uri = resolveAccessTokenConsumerEndpoint(OpenIDConnectConstants.FacebookAuthzTokenConsumerService_QNAME.toString());

        FacebookClient fb = new DefaultFacebookClient(Version.VERSION_2_4);

        int retry = 0;
        FacebookClient.AccessToken at = null;
        while (retry <= MAX_NUM_OF_FB_API_CALL_RETRIES) {
            try {
                at = fb.obtainUserAccessToken(mediator.getClientId(),
                        mediator.getClientSecret(),
                        response_uri.getLocation(),
                        code);
                break;
            } catch (Exception e) {
                retry++;
                logger.error(e.getMessage(), e);
                if (retry <= MAX_NUM_OF_FB_API_CALL_RETRIES) {
                    logger.debug("Getting Facebook access token, retry: " + retry);
                } else {
                    throw new IdentityMediationException(e);
                }
            }
        }
        if (at == null) {
            throw new IdentityMediationException("Facebook authorization failed!");
        }

        // Now create a new instance with the token
        fb = new DefaultFacebookClient(at.getAccessToken(), Version.VERSION_2_4);

        retry = 0;
        User user = null;
        while (retry <= MAX_NUM_OF_FB_API_CALL_RETRIES) {
            try {
                user = fb.fetchObject("me", User.class, Parameter.with("fields", mediator.getUserFields()));
                break;
            } catch (Exception e) {
                retry++;
                logger.error(e.getMessage(), e);
                if (retry <= MAX_NUM_OF_FB_API_CALL_RETRIES) {
                    logger.debug("Getting Facebook user info, retry: " + retry);
                } else {
                    throw new IdentityMediationException(e);
                }
            }
        }
        if (user == null) {
            throw new IdentityMediationException("Facebook authorization failed!");
        }

        SubjectType subject;

        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();

        subject = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(user.getEmail());
        a.setFormat(NameIDFormat.EMAIL.getValue());
        a.setLocalName(user.getEmail());
        a.setNameQualifier(getFederatedProvider().getName().toUpperCase());
        a.setLocalNameQualifier(getFederatedProvider().getName().toUpperCase());

        subject.getAbstractPrincipal().add(a);

        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(at.getAccessToken());
        attrs.add(accessTokenAttr);

        long expiresIn = (at.getExpires().getTime() - System.currentTimeMillis()) / 1000;

        SubjectAttributeType accessTokenExpiresInAttr = new SubjectAttributeType();
        accessTokenExpiresInAttr.setName("accessTokenExpiresIn");
        accessTokenExpiresInAttr.setValue(expiresIn + "");
        attrs.add(accessTokenExpiresInAttr);

        SubjectAttributeType openIdSubjectAttr = new SubjectAttributeType();
        openIdSubjectAttr.setName("openIdSubject");
        openIdSubjectAttr.setValue(user.getId());
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
        addUserAttribute(EMAIL_USER_ATTR_NAME, user.getEmail(), attrs);
        addUserAttribute(FIRST_NAME_USER_ATTR_NAME, user.getFirstName(), attrs);
        addUserAttribute(LAST_NAME_USER_ATTR_NAME, user.getLastName(), attrs);
        addUserAttribute(COMMON_NAME_USER_ATTR_NAME, getUserFullName(user), attrs);
        addUserAttribute(GENDER_USER_ATTR_NAME, user.getGender(), attrs);
        addUserAttribute(LANGUAGE_USER_ATTR_NAME, user.getLocale(), attrs);
        addUserAttribute(PICTURE_USER_ATTR_NAME, user.getPicture() != null ? user.getPicture().getUrl() : null, attrs);
        addUserAttribute(PROFILE_LINK_USER_ATTR_NAME, user.getLink(), attrs);
        addUserAttribute(IS_VERIFIED_USER_ATTR_NAME, String.valueOf(user.getVerified()), attrs);
        addUserAttribute(BIRTHDAY_USER_ATTR_NAME, user.getBirthday(), attrs);
        addUserAttribute(TOKEN_FOR_BUSINESS_USER_ATTR_NAME, user.getTokenForBusiness(), attrs);

        addUserAttribute("middleName", user.getMiddleName(), attrs);
        addUserAttribute("bio", user.getBio(), attrs);
        addUserAttribute("quotes", user.getQuotes(), attrs);
        addUserAttribute("about", user.getAbout(), attrs);
        addUserAttribute("relationshipStatus", user.getRelationshipStatus(), attrs);
        addUserAttribute("religion", user.getReligion(), attrs);
        addUserAttribute("timezone", toJsonString(user.getTimezone()), attrs);
        addUserAttribute("political", user.getPolitical(), attrs);
        addUserAttribute("ageRange", toJsonString(user.getAgeRange()), attrs);
        addUserAttribute("hometown", user.getHometownName(), attrs);
        addUserAttribute("location", toJsonString(user.getLocation()), attrs);
        addUserAttribute("significantOther", toJsonString(user.getSignificantOther()), attrs);
        addUserAttribute("updatedTime", toJsonString(user.getUpdatedTime()), attrs);
        addUserAttribute("currency", toJsonString(user.getCurrency()), attrs);
        addUserAttribute("interestedIn", listToJsonString(user.getInterestedIn()), attrs);
        addUserAttribute("meetingFor", listToJsonString(user.getMeetingFor()), attrs);
        addUserAttribute("devices", listToJsonString(user.getDevices()), attrs);
        addUserAttribute("work", listToJsonString(user.getWork()), attrs);
        addUserAttribute("education", listToJsonString(user.getEducation()), attrs);
        addUserAttribute("sports", listToJsonString(user.getSports()), attrs);
        addUserAttribute("favoriteTeams", listToJsonString(user.getFavoriteTeams()), attrs);
        addUserAttribute("favoriteAthletes", listToJsonString(user.getFavoriteAthletes()), attrs);
        addUserAttribute("languages", listToJsonString(user.getLanguages()), attrs);
    }

    private String getUserFullName(User user) {
        String fullName = user.getFirstName() != null ? user.getFirstName() : "";
        if (StringUtils.isNotBlank(user.getMiddleName())) {
            fullName += " " + user.getMiddleName();
        }
        if (StringUtils.isNotBlank(user.getLastName())) {
            fullName += " " + user.getLastName();
        }
        return fullName.trim();
    }
}
