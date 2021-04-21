package org.atricore.idbus.capabilities.oauth2.main.token.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.*;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.oauth2.main.token.endpoints.TokenEndpoint;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.oauth._2_0.protocol.*;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * This emits an access, using a previously requested authorization token.
 * In this case, the authorization token can also be user credentials.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class TokenProducer extends AbstractOAuth2Producer {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    public TokenProducer(TokenEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        AccessTokenRequestType atReq = (AccessTokenRequestType) in.getMessage().getContent();

        // We are acting as OAUTH 2.0 Authorization server, we consider it an IDP role.
        AccessTokenResponseType atRes = new AccessTokenResponseType();

        // ---------------------------------------------------------
        // Validate Request
        // ---------------------------------------------------------
        try {

            OAuth2Client client = null;
            // This returns the SP associated with the OAuth request.
            validateRequest(atReq, atRes);

            client = resolveOAuth2Client(atReq.getClientId(), atRes);
            if (client == null) {
                throw new OAuth2ServerException(ErrorCodeType.UNAUTHORIZED_CLIENT, "Invalid clientId/clientSecret");
            }

            // Authenticate the client, unless an error has occurred.
            authenticateRequest(client, atReq, atRes);

            OAuth2SecurityTokenEmissionContext securityTokenEmissionCtx = new OAuth2SecurityTokenEmissionContext();

            // Send extra information to STS, using the emission context
            //securityTokenEmissionCtx.setMember(sp);
            //securityTokenEmissionCtx.setAuthnState(authnState);
            securityTokenEmissionCtx.setSessionIndex(uuidGenerator.generateId());

            securityTokenEmissionCtx = emitAccessTokenFromClaims(exchange, securityTokenEmissionCtx, atReq.getUsername(), atReq.getPassword());

            // Call STS and wait for OAuth AccessToken
            OAuthAccessTokenType at = securityTokenEmissionCtx.getAccessToken();

            // send access token back to requester

            // build response
            atRes.setAccessToken(at.getAccessToken());
            atRes.setExpiresIn(at.getExpiresIn());
            atRes.setTokenType(at.getTokenType());

        } catch (OAuth2ServerException e) {
            // Send oauth error in response
            atRes.setError(e.getErrorCode());
            atRes.setErrorDescription(e.getErrorDescription());

            // Dump stack trance if we have a cause:
            if (e.getCause() != null)
                logger.error(e.getErrorCode().value() + " ["+e.getErrorDescription()+"]", e);
            else
                logger.warn(e.getErrorCode().value() + " ["+e.getErrorDescription()+"]");

        } catch (SecurityTokenAuthenticationFailure e) {
            atRes.setError(ErrorCodeType.ACCESS_DENIED);
            atRes.setErrorDescription(e.getMessage());

            if (e.getSsoPolicyEnforcements() != null) {
                for (PolicyEnforcementStatement stmt : e.getSsoPolicyEnforcements()) {
                    SSOPolicyEnforcementStatementType stmtType = new SSOPolicyEnforcementStatementType();
                    stmtType.setNs(stmt.getNs());
                    stmtType.setName(stmt.getName());
                    if (stmt.getValues() != null) {
                        stmtType.getValues().addAll(stmt.getValues());
                    }
                    atRes.getSsoPolicyEnforcements().add(stmtType);
                }
            }

            if (logger.isDebugEnabled())
                logger.debug(e.getMessage(), e);

        } catch (Exception e) {
            // Something went wrong
            atRes.setError(ErrorCodeType.SERVER_ERROR);
            atRes.setErrorDescription(e.getMessage());
            logger.error(e.getMessage(), e);
        }


        // --------------------------------------------
        // Emit OAuth Access Token
        // --------------------------------------------


        // send response back
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

        // Recover emission context, to retrieve Subject information
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

    protected void validateRequest(AccessTokenRequestType atReq, AccessTokenResponseType atRes) throws InvalidRequestException {

        if (atReq.getClientId() == null) {
            throw new InvalidRequestException(ErrorCodeType.INVALID_REQUEST, "Access Token Request MUST include a client id");
        }

        if (atReq.getClientSecret() == null) {
            throw new InvalidRequestException(ErrorCodeType.INVALID_REQUEST, "Access Token Request MUST include a client secret");
        }

    }

    protected boolean authenticateRequest(OAuth2Client client, AccessTokenRequestType atReq, AccessTokenResponseType atRes) throws OAuth2ServerException {
        // TODO : Improve: support other authn methods (user grant permission), strong authn, add password hashing, etc
        if (logger.isTraceEnabled()) {
            logger.trace("Authenticating req "+atReq.getClientId()+" for client " + client);
        }

        if (!client.getSecret().equals(atReq.getClientSecret())) {
            throw new OAuth2ServerException(ErrorCodeType.UNAUTHORIZED_CLIENT, "Invalid clientId/clientSecret");
        }

        return true;

    }


}
