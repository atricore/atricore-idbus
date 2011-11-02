package org.atricore.idbus.capabilities.oauth2.main.token.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuthAccessToken;
import org.atricore.idbus.capabilities.oauth2.main.OAuthMediator;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuthAccessTokenEmissionContext;
import org.atricore.idbus.capabilities.oauth2.main.token.endpoints.TokenEndpoint;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenRequestType;
import org.atricore.idbus.common.oauth._2_0.protocol.AccessTokenResponseType;
import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBElement;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class TokenProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

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

        // Get configured client id (parnterapp)
        String clientId = spChannel.getName();
        if (atReq.getClientId().equals(clientId)) {
            // TODO : Return invalid_clientid
        }


        // TODO : Get configured client secret (optional)

        // --------------------------------------------
        // Emit OAuth Access Token
        // --------------------------------------------

        // Call STS and wait for OAUTH AccessToken
        OAuthAccessToken at = null;

        // serialize, sign, encode , send access token

        // build response
        AccessTokenResponseType atRes = null;

        // send back

    }

/**
     * This will return an emission context with both, the required SAMLR2 Assertion and the associated Subject.
     *
     * @return SamlR2 Security emission context containing SAMLR2 Assertion and Subject.
     */
    protected OAuthAccessTokenEmissionContext emitAssertionFromClaims(CamelMediationExchange exchange,
                                                                         OAuthAccessTokenEmissionContext securityAccessTokenEmissionCtx,
                                                                         ClaimSet receivedClaims,
                                                                         CircleOfTrustMemberDescriptor sp) throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emitt a new security token
        // -------------------------------------------------------

        // TODO : Improve communication mechanism between STS and IDP!
        // Queue this contenxt and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(securityAccessTokenEmissionCtx);

        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(receivedClaims, emitterCtxArtifact.getContent());

        if (logger.isDebugEnabled())
            logger.debug("Requesting Security Token (RST) w/context " + rst.getContext());

        // Send request to STS
        RequestSecurityTokenResponseType rstrt = sts.requestSecurityToken(rst);

        if (logger.isDebugEnabled())
            logger.debug("Received Request Security Token Response (RSTR) w/context " + rstrt.getContext());

        // Recover emission context, to retrive Subject information
        securityAccessTokenEmissionCtx = (OAuthAccessTokenEmissionContext) aqm.pullMessage(ArtifactImpl.newInstance(rstrt.getContext()));

        /// Obtain assertion from STS Response
        JAXBElement<RequestedSecurityTokenType> token = (JAXBElement<RequestedSecurityTokenType>) rstrt.getAny().get(1);
        OAuthAccessTokenType accessToken = (OAuthAccessTokenType) token.getValue().getAny();
        if (logger.isDebugEnabled())
            logger.debug("Generated OAuth Access Token [" + accessToken.getAccessToken() + "]");

        securityAccessTokenEmissionCtx.setOAuthAccessToken(accessToken);

        // Return context with Assertion and Subject
        return securityAccessTokenEmissionCtx;

    }

    private RequestSecurityTokenType buildRequestSecurityToken(ClaimSet receivedClaims, String content) {
        // TODO !!!!
        throw new UnsupportedOperationException("Not implemented!");
    }

    protected MessageQueueManager getArtifactQueueManager() {
        OAuthMediator mediator = (OAuthMediator) channel.getIdentityMediator();
        return mediator.getArtifactQueueManager();
    }

}
