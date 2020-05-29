package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
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

public class InitLogoutProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(InitLogoutProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public InitLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        BindingChannel bChannel = (BindingChannel) channel;
        OpenIDConnectBPMediator mediator = ((OpenIDConnectBPMediator)(channel).getIdentityMediator());

        // Received OpenIDConnect authentication request (Nimbus)
        LogoutRequest logoutRequest = (LogoutRequest) in.getMessage().getContent();
        RPAuthnContext authnCtx =
                (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        validateRequest(logoutRequest, mediator, authnCtx);

        // Process request (trigger SLO)!
        if (authnCtx == null) {
            // We don't have a context ?!
            authnCtx = new RPAuthnContext();

        }

        authnCtx.setLogoutRequest(logoutRequest);

        // This producer just redirects the user to the configured target IDP.
        BindingChannel spBinding = resolveSpBindingChannel(bChannel);
        EndpointDescriptor destination = resolveSPInitiatedSLOEndpointDescriptor(exchange, spBinding);

        // Create SP AuthnRequest
        // TODO : Support on_error ?
        SPInitiatedLogoutRequestType request = buildSLORequest(exchange);
        authnCtx.setSloRequest(request);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(request.getID(),
                request,
                "SSOLogoutRequest",
                null,
                destination,
                in.getMessage().getState()));

        exchange.setOut(out);

        state.setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);

    }

    protected void validateRequest(LogoutRequest logoutRequest, OpenIDConnectBPMediator mediator, RPAuthnContext authnCtx) throws OpenIDConnectProviderException {

        OIDCClientInformation client = mediator.getClient();
        OIDCClientMetadata metadata = client.getOIDCMetadata();

        // TODO : ID Token HINT : We need to get the token from the OP.
        // TODO : Get session from tokens ?!
        authnCtx.getIdToken();

        JWT receivedIdToken = logoutRequest.getIDTokenHint();
        String receivedIdTokenStr = receivedIdToken.getParsedString();

        URI postLogoutURI = logoutRequest.getPostLogoutRedirectionURI();

        if (postLogoutURI == null)
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST_URI, "post_logout_redirect_uri is invalid");

        // POST LOGOUT URI
        if (metadata.getPostLogoutRedirectionURIs() != null &&
                metadata.getPostLogoutRedirectionURIs().size() > 0 &&
                !validateURI(metadata.getPostLogoutRedirectionURIs(), logoutRequest.getPostLogoutRedirectionURI()))
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST_URI, "post_logout_redirect_uri is invalid");

         if (!validateURI(metadata.getRedirectionURIs(), logoutRequest.getPostLogoutRedirectionURI())) {
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST_URI, "post_logout_redirect_uri is invalid");
        }
    }

    protected EndpointDescriptor resolveSPInitiatedSLOEndpointDescriptor(CamelMediationExchange exchange, BindingChannel idP) throws OpenIDConnectProviderException {

        try {

            logger.debug("Looking for " + SSOService.SPInitiatedSingleLogoutService.toString());

            for (IdentityMediationEndpoint endpoint : idP.getEndpoints()) {

                logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleLogoutService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  idP.getIdentityMediator().resolveEndpoint(idP, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new OpenIDConnectProviderException(OAuth2Error.SERVER_ERROR, e.getMessage(), e);
        }

        throw new OpenIDConnectProviderException(OAuth2Error.SERVER_ERROR, "No SP endpoint found for SP Initiated SLO using JOSSO Artifact binding");

    }


    protected SPInitiatedLogoutRequestType buildSLORequest(CamelMediationExchange exchange) throws IdentityMediationException {

        SPInitiatedLogoutRequestType req = new SPInitiatedLogoutRequestType();
        req.setID(uuidGenerator.generateId());
        OpenIDConnectBPMediator mediator = ((OpenIDConnectBPMediator)(channel).getIdentityMediator());

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (endpoint.getType().equals(SSOService.SingleLogoutService.toString())) {
                if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                    EndpointDescriptor ed = mediator.resolveEndpoint(channel, endpoint);
                    req.setReplyTo(ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation());

                    if (logger.isDebugEnabled())
                        logger.debug("SLORequest.Reply-To:" + req.getReplyTo());

                }
            }
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType ();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
            req.getRequestAttribute().add(a);
        }

        return req;

    }
}
