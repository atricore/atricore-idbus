package org.atricore.idbus.capabilities.oauth2.main.token.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Exception;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Mediator;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2AccessTokenEmitter;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.oauth2.main.token.endpoints.TokenEndpoint;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.Claim;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigInteger;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class TokenProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public TokenProducer(TokenEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        AccessTokenRequestType atReq = (AccessTokenRequestType) in.getMessage().getContent();

        // We are acting as authorization server, it's an IDP role ;)

        // Get the proper SP Channel
        SPChannel spChannel = (SPChannel) channel;

        // ---------------------------------------------------------
        // Validate Request
        // ---------------------------------------------------------

        if (atReq.getClientId() == null) {
            // TODO : Return invalid client id error
        }

        // Get configured client id (parnterapp)
        String clientId = spChannel.getName();
        if (atReq.getClientId().equals(clientId)) {
            // TODO : Return invalid client id error
        }

        // TODO : Get configured client secret (optional)

        // --------------------------------------------
        // Emit OAuth Access Token
        // --------------------------------------------

        OAuth2SecurityTokenEmissionContext securityTokenEmissionCtx = new OAuth2SecurityTokenEmissionContext();
        // Send extra information to STS, using the emission context

        //securityTokenEmissionCtx.setMember(sp);
        //securityTokenEmissionCtx.setAuthnState(authnState);
        securityTokenEmissionCtx.setSessionIndex(uuidGenerator.generateId());

        emitAccessTokenFromClaims(exchange, securityTokenEmissionCtx, atReq.getUsername(), atReq.getPassword());

        // Call STS and wait for OAUTH AccessToken
        OAuthAccessTokenType at = securityTokenEmissionCtx.getAccessToken();

        // serialize, sign, encode , send access token

        // build response
        AccessTokenResponseType atRes = new AccessTokenResponseType();
        atRes.setAccessToken(at.getAccessToken());
        atRes.setExpiresIn(BigInteger.valueOf(at.getExpiresIn()));
        atRes.setTokeyType(at.getTokenType());

        // send back
        EndpointDescriptor ed = null; // TODO : Only works for SOAP messages!
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                atRes, "AccessTokenResponseType", null, ed, null));

        exchange.setOut(out);



    }

/**
     * This will return an emission context with both, the required SAMLR2 Assertion and the associated Subject.
     *
     * @return SamlR2 Security emission context containing SAMLR2 Assertion and Subject.
     */
    protected OAuth2SecurityTokenEmissionContext emitAccessTokenFromClaims(CamelMediationExchange exchange,
                                                                           OAuth2SecurityTokenEmissionContext accessAccessTokenEmissionCtx,
                                                                           String username,
                                                                           String password) throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // TODO : Improve communication mechanism between STS and IDP!
        // Queue this contenxt and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(accessAccessTokenEmissionCtx);

        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(username, password, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting OAuth 2 Access Token (RST) w/context " + rst.getContext());

        // Send request to STS
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

        if (logger.isDebugEnabled())
            logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

        // Recover emission context, to retrive Subject information
        accessAccessTokenEmissionCtx = (OAuth2SecurityTokenEmissionContext) aqm.pullMessage(ArtifactImpl.newInstance(rstrt.getContext()));

        /// Obtain assertion from STS Response
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
        OAuthAccessTokenType accessToken = (OAuthAccessTokenType) token.getValue().getAny();
        if (logger.isDebugEnabled())
            logger.debug("Generated OAuth Access Token [" + accessToken.getAccessToken() + "]");

        accessAccessTokenEmissionCtx.setAccessToken(accessToken);

        // Return context with Assertion and Subject
        return accessAccessTokenEmissionCtx;

    }

    private RequestSecurityTokenType buildRequestSecurityToken(String username, String password, String context) throws OAuth2Exception {
        logger.debug("generating RequestSecurityToken...");
        org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory of = new org.xmlsoap.schemas.ws._2005._02.trust.ObjectFactory();

        RequestSecurityTokenType rstRequest = new RequestSecurityTokenType();

        rstRequest.getAny().add(of.createTokenType(WSTConstants.WST_OAUTH2_TOKEN_TYPE));
        rstRequest.getAny().add(of.createRequestType(WSTConstants.WST_ISSUE_REQUEST));

        org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory ofwss = new org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.ObjectFactory();

        // Send credentials with authn request:
        UsernameTokenType usernameToken = new UsernameTokenType ();
        AttributedString usernameString = new AttributedString();
        usernameString.setValue( username );

        usernameToken.setUsername( usernameString );
        usernameToken.getOtherAttributes().put(new QName(Constants.PASSWORD_NS), password);

        rstRequest.getAny().add(ofwss.createUsernameToken(usernameToken));

        if (context != null)
            rstRequest.setContext(context);

        logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
        return rstRequest;
    }

    protected MessageQueueManager getArtifactQueueManager() {
        OAuth2Mediator a2Mediator = (OAuth2Mediator) channel.getIdentityMediator();
        return a2Mediator.getArtifactQueueManager();
    }

}
