package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedTokenRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedTokenResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.util.Map;

/**
 * Created by sgonzalez on 10/27/14.
 */
public class SsoPreAuthnTokenSvcBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SsoPreAuthnTokenSvcBinding.class);

    public SsoPreAuthnTokenSvcBinding(Channel channel) {
        super(SSOBinding.SSO_PREAUTHN.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null) {

            if (logger.isDebugEnabled()) {
                Map<String, Object> h = httpMsg.getHeaders();
                for (String key : h.keySet()) {
                    logger.debug("CAMEL Header:" + key + ":"+ h.get(key));
                }
            }

            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);
        String relayState = state.getTransientVariable("relay_state");
        String securityToken = state.getTransientVariable("atricore_security_token");
        String id = state.getTransientVariable("response_id");
        String scope = state.getTransientVariable("scope");
        String authnCtx = state.getTransientVariable("authn_ctx");
        String reply = state.getTransientVariable("reply");
        String rememberMe = state.getTransientVariable("remember_me");

        Object content = null;

        if (scope != null && scope.equals("preauth-token")) {
            PreAuthenticatedTokenResponseType response = new PreAuthenticatedTokenResponseType();
            response.setID(id);
            response.setSecurityToken(securityToken);
            response.setAuthnCtxClass(authnCtx);
            response.setInReplayTo(reply);

            content = response;
        } else {
            throw new IllegalArgumentException("scope not found or invalid : " + scope);
        }

        return new MediationMessageImpl(message.getMessageId(),
                content,
                null,
                relayState,
                null,
                state);

    }

    public void copyMessageToExchange(CamelMediationMessage msgOut, Exchange exchange) {
        // Content is OPTIONAL
        MediationMessage out = msgOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        // ------------------------------------------------------------
        // Validate received message
        // ------------------------------------------------------------
        assert ed != null : "Mediation Response MUST Provide a destination";
        if (out.getContent() == null)
            throw new IllegalStateException("Content not found for IDBUS HTTP PreAuthnTokenSvc bidning, found: " + out);

        // ------------------------------------------------------------
        // Create HTML Form for response body
        // ------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Creating HTML Redirect to " + ed.getLocation());

        StringBuilder ssoQryString = new StringBuilder();

        if (out.getContent() instanceof PreAuthenticatedTokenRequestType) {

            // Marshall pre-authn token request to query string
            PreAuthenticatedTokenRequestType req = (PreAuthenticatedTokenRequestType) out.getContent();

            ssoQryString.append("?request_id=").append(req.getID());

            if (out.getRelayState() != null) {
                ssoQryString.append("&relay_state=").append(out.getRelayState());
            }

            if (req.getIssuer() != null) {
                ssoQryString.append("&issuer=").append(req.getIssuer());
            }

            if (req.getAuthnCtxClass() != null) {
                ssoQryString.append("&atuhn_ctx=").append(req.getAuthnCtxClass());
            }

            if (req.getTarget() != null) {
                ssoQryString.append("&target=").append(req.getTarget());
            }


        } else {
            throw new IllegalStateException("Unsupported content type : " + out.getContent().getClass().getSimpleName());
        }

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();
        String ssoRedirLocation = this.buildHttpTargetLocation(httpIn, ed) + ssoQryString;

        if (logger.isDebugEnabled())
            logger.debug("Redirecting to " + ssoRedirLocation);

        try {

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 302);
            httpOut.getHeaders().put("Content-Type", "text/html");
            httpOut.getHeaders().put("Location", ssoRedirLocation);
            handleCrossOriginResourceSharing(exchange);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
