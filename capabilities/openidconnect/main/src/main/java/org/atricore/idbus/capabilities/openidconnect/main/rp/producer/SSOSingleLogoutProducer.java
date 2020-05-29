package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.IDPPRoxyInitiatedLogoutResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.IDPProxyInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedLogoutRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;
import java.util.Set;

/**
 * Receives an SLO Response and triggers an RP initiated SLO
 */
public class SSOSingleLogoutProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(SSOSingleLogoutProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SSOSingleLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        if (in.getMessage().getContent() instanceof SSOResponseType) {
            doProcessSSOResponse((SSOResponseType) in.getMessage().getContent(), exchange);

        } else if (in.getMessage().getContent() instanceof IDPProxyInitiatedLogoutRequestType) {
            doProcessSLOProxyRequest((IDPProxyInitiatedLogoutRequestType) in.getMessage().getContent(), exchange);
        }

    }

    protected void doProcessSLOProxyRequest(IDPProxyInitiatedLogoutRequestType sloProxyRequest, CamelMediationExchange exchange) {
        // Invalidate OIDC Authn context.
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();

        RPAuthnContext authnCtx =
                (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        if (authnCtx != null) {
            AccessToken at = authnCtx.getAccessToken();
            if (logger.isDebugEnabled())
                logger.debug("Invalidating  access token: " + (at != null ? at.toJSONString() : "NULL"));
        }

        // Clear state.
        state.removeLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        // Send SSOResponse back to requesting party
        IDPPRoxyInitiatedLogoutResponseType ssoProxyResponse = new IDPPRoxyInitiatedLogoutResponseType();
        ssoProxyResponse.setInReplayTo(sloProxyRequest.getID());

        // TODO : Check if we need to notify the IDP selector!!!
        /*
        EndpointDescriptor idpSelectorCallbackEndpoint = resolveIdPSelectorCallbackEndpoint(exchange, requiredSpChannel);
        if (idpSelectorCallbackEndpoint != null) {
            logger.warn("NOT Sending Current Selected IdP request (IMPLEMENT), callback location : " + idpSelectorCallbackEndpoint);
        }
         */

        EndpointDescriptor proxyEd = new EndpointDescriptorImpl("idp-proxy-init-slo-repsonse",
                "SLOService",
                SSOBinding.SSO_ARTIFACT.toString(),
                sloProxyRequest.getReplyTo(),
                null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                ssoProxyResponse, "IDPPRoxyInitiatedLogoutResponse", null, proxyEd, in.getMessage().getState()));

        exchange.setOut(out);
        return;

    }


    protected void doProcessSSOResponse(SSOResponseType sloResponse, CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        RPAuthnContext authnCtx =
                (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        SPInitiatedLogoutRequestType sloRequest = authnCtx.getSloRequest();
        LogoutRequest logoutRequest = authnCtx.getLogoutRequest();

        verifyResponse(sloResponse, sloRequest);

        URI requestedURI = logoutRequest.getPostLogoutRedirectionURI();

        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();
        OIDCClientInformation client = mediator.getClient();

        if (requestedURI == null)  {
            // Use any configured URI
            Set<URI> postLogoutRedirectionURIs = client.getOIDCMetadata().getPostLogoutRedirectionURIs();
            if (postLogoutRedirectionURIs != null && postLogoutRedirectionURIs.size() > 0)
                requestedURI = postLogoutRedirectionURIs.iterator().next();
            else {
                Set<URI> redirectionURIs = client.getOIDCMetadata().getRedirectionURIs();
                requestedURI = redirectionURIs.iterator().next();
            }
        }

        // Redirect back to requestedURI
        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "RPInitLogoutResponse",
                "RPInitLogoutResponse",
                OpenIDConnectBinding.OPENID_PROVIDER_LOGOUT_HTTP.getValue(),
                requestedURI.toString(),
                null);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(null,
                null,
                "AuthenticationAssertion",
                logoutRequest.getState() != null ? logoutRequest.getState().getValue() : null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

        state.removeLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);
        state.removeLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:SSOLogoutRequest");


    }

    protected void verifyResponse(SSOResponseType sloResponse, SPInitiatedLogoutRequestType sloRequest) throws OpenIDConnectProviderException {
        if (sloRequest == null)
            return;

        if (sloResponse.getInReplayTo() == null || !sloResponse.getInReplayTo().equals(sloRequest.getID()) )
            throw new OpenIDConnectProviderException(OAuth2Error.SERVER_ERROR, "Invalid reply to " + sloResponse.getInReplayTo());
    }
}
