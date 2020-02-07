package org.atricore.idbus.capabilities.oauth2.main.token.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.main.*;
import org.atricore.idbus.capabilities.oauth2.main.emitter.OAuth2SecurityTokenEmissionContext;
import org.atricore.idbus.capabilities.oauth2.main.sso.producers.SingleSignOnProducer;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolver;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverFactory;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.oauth._2_0.protocol.*;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mail.MailService;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenResponseType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.RequestedSecurityTokenType;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.URLEncoder;
import java.util.Properties;

public class PasswordlessLinkProducer extends AbstractOAuth2Producer {


    private static final Log logger = LogFactory.getLog(SingleSignOnProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public PasswordlessLinkProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {


        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        OAuth2IdPMediator mediator = (OAuth2IdPMediator) channel.getIdentityMediator();
        PasswordLessConfig config = mediator.getPasswordLessConfig();

        if (!config.isEnabled())
            throw new IdentityMediationException("Service not available.  Passwordless authentication not enabled.");

        SendPasswordlessLinkRequestType atReq = (SendPasswordlessLinkRequestType) in.getMessage().getContent();

        // We are acting as OAUTH 2.0 Authorization server, we consider it an IDP role.
        SendPasswordlessLinkResponseType atRes = new SendPasswordlessLinkResponseType();

        OAuth2Client client = null;
        // This returns the SP associated with the OAuth request.
        validateRequest(atReq, atRes);

        client = resolveOAuth2Client(atReq.getClientId(), atRes);
        if (client == null) {
            throw new OAuth2ServerException(ErrorCodeType.UNAUTHORIZED_CLIENT, "Invalid clientId/clientSecret");
        }

        // Authenticate the client, unless an error has occurred.
        authenticateRequest(client, atReq, atRes);

        // Generate Token
        OAuth2SecurityTokenEmissionContext securityTokenEmissionCtx = new OAuth2SecurityTokenEmissionContext();

        // Send extra information to STS, using the emission context
        //securityTokenEmissionCtx.setMember(sp);
        //securityTokenEmissionCtx.setAuthnState(authnState);
        securityTokenEmissionCtx.setSessionIndex(uuidGenerator.generateId());

        securityTokenEmissionCtx = emitAccessTokenFromClaims(exchange, securityTokenEmissionCtx, atReq.getUsername());

        // Call STS and wait for OAuth AccessToken
        OAuthAccessTokenType at = securityTokenEmissionCtx.getAccessToken();
        // Decrypt access token and add user properties
        Properties oauth2Config = new Properties();
        oauth2Config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_PROPERTY, client.getSecret());
        oauth2Config.setProperty(SecureAccessTokenResolverFactory.TOKEN_VALIDITY_INTERVAL_PROPERTY, "30000");

        AccessTokenResolver r = AccessTokenResolverFactory.newInstance(oauth2Config).newResolver();
        OAuth2AccessToken token = r.resolve(at.getAccessToken());

        // Generate Message content
        VelocityEngine e = buildVelocityEngine();

        VelocityContext veCtx = new VelocityContext();


        // Build a context
        veCtx.put("username", atReq.getUsername());
        if (logger.isTraceEnabled())
            logger.trace("Adding claim as velocity variable: username=" + atReq.getUsername());

        veCtx.put("token", at.getAccessToken());
        if (logger.isTraceEnabled())
            logger.trace("Adding claim as velocity variable: token=" + at.getAccessToken());

        veCtx.put("encodedToken", URLEncoder.encode(at.getAccessToken(), "UTF-8"));
        if (logger.isTraceEnabled())
            logger.trace("Adding claim as velocity variable: encodedToken=" + URLEncoder.encode(at.getAccessToken(), "UTF-8"));

        if (logger.isTraceEnabled())
            logger.trace("Adding claim as velocity variable: targetSP=" + atReq.getTargetSP());
        veCtx.put("targetSP", atReq.getTargetSP());

        for (TemplatePropertyType prop : atReq.getProperties()) {

            if (logger.isTraceEnabled())
                logger.trace("Adding claim as velocity variable: " + prop.getName() + "=" + prop.getValues().get(0));

            veCtx.put(prop.getName(), prop.getValues().get(0)); /// Only first value supported for now
        }

        for (OAuth2Claim claim : token.getClaims()) {

            if (!claim.getType().equals("ATTRIBUTE"))
                continue;

            veCtx.put(claim.getValue(), claim.getAttribute());
            if (logger.isTraceEnabled())
                logger.trace("Adding claim as velocity variable: " + claim.getValue() + "=" + claim.getAttribute());
        }

        String authnURL = config.getIdpUrl() + "?atricore_security_token=" + URLEncoder.encode(at.getAccessToken(), "UTF-8") +
                "&scope=preauth-token" +
                "&atricore_sp_alias=" + URLEncoder.encode(atReq.getTargetSP(), "UTF-8");

        veCtx.put("authnURL", authnURL);

        // TODO : Make transport configurable
        MailService mailService = mediator.getMailService();

        // Send message
        String from = evaluateVelocity(e, veCtx, config.getSender());
        String to = evaluateVelocity(e, veCtx, config.getRecipient());
        String subject = evaluateVelocity(e, veCtx, config.getSubject());
        String contentType = "text/html";  // config.getContentType();

        String template = atReq.getTemplate() != null ? atReq.getTemplate() : config.getTemplate();
        String msg = evaluateVelocity(e, veCtx, template);

        mailService.send(from, to, subject, msg.toString(), contentType);

        // send response back
        EndpointDescriptor ed = null;
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                atRes, "SendPasswordlessLinkResponseType", null, ed, null));

        exchange.setOut(out);

    }

    /**
     * This will return an emission context with both, the required SAMLR2 Assertion and the associated Subject.
     *
     * @return SamlR2 Security emission context containing SAMLR2 Assertion and Subject.
     */
    protected OAuth2SecurityTokenEmissionContext emitAccessTokenFromClaims(CamelMediationExchange exchange,
                                                                           OAuth2SecurityTokenEmissionContext accessAccessTokenEmissionCtx,
                                                                           String username) throws Exception {

        MessageQueueManager aqm = getArtifactQueueManager();

        // -------------------------------------------------------
        // Emit a new security token
        // -------------------------------------------------------

        // TODO : Improve communication mechanism between STS and IDP!
        // Queue this contenxt and send the artifact as RST context information
        Artifact emitterCtxArtifact = aqm.pushMessage(accessAccessTokenEmissionCtx);

        SecurityTokenService sts = ((SPChannel) channel).getSecurityTokenService();
        // Send artifact id as RST context information, similar to relay state.
        RequestSecurityTokenType rst = buildRequestSecurityToken(username, emitterCtxArtifact.getContent());

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

    private RequestSecurityTokenType buildRequestSecurityToken(String username, String context) throws OAuth2Exception {
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

        String pwdlessLink = context;

        usernameToken.setUsername( usernameString );
        usernameToken.getOtherAttributes().put(new QName(Constants.PWDLESS_LINK), pwdlessLink);

        rstRequest.getAny().add(ofwss.createUsernameToken(usernameToken));

        if (context != null)
            rstRequest.setContext(context);

        logger.debug("generated RequestSecurityToken [" + rstRequest + "]");
        return rstRequest;
    }

    protected void validateRequest(SendPasswordlessLinkRequestType atReq, SendPasswordlessLinkResponseType atRes) throws InvalidRequestException {

        if (atReq.getClientId() == null) {
            throw new InvalidRequestException(ErrorCodeType.INVALID_REQUEST, "Passworless Token Request MUST include a client id");
        }

        if (atReq.getClientSecret() == null) {
            throw new InvalidRequestException(ErrorCodeType.INVALID_REQUEST, "Passworless Token Request MUST include a client secret");
        }

    }



    protected boolean authenticateRequest(OAuth2Client client, SendPasswordlessLinkRequestType atReq, SendPasswordlessLinkResponseType atRes) throws OAuth2ServerException {
        // TODO : Improve: support other authn methods (user grant permission), strong authn, add password hashing, etc
        if (logger.isTraceEnabled()) {
            logger.trace("Authenticating req "+atReq.getClientId()+" for client " + client);
        }

        if (!client.getSecret().equals(atReq.getClientSecret())) {
            throw new OAuth2ServerException(ErrorCodeType.UNAUTHORIZED_CLIENT, "Invalid clientId/clientSecret");
        }

        return true;

    }
    
    protected VelocityEngine buildVelocityEngine() throws Exception {

        VelocityEngine e = new VelocityEngine();

        e.setProperty(Velocity.RESOURCE_LOADER, "classpath");

        e.addProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        e.setProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".cache", "false");

        e.setProperty(
                "classpath." + Velocity.RESOURCE_LOADER + ".modificationCheckInterval",
                "2");

        e.init();

        return e;
    }

    protected String evaluateVelocity(VelocityEngine e, VelocityContext veCtx, String template) throws IOException {
        StringWriter msg = new StringWriter();
        e.evaluate(veCtx, msg, "default", template);
        return msg.toString();
    }
}
