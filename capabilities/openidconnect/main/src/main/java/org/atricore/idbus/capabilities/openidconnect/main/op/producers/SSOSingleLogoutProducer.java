package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
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
 *
 */
public class SSOSingleLogoutProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(SSOSingleSignOnProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SSOSingleLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        OpenIDConnectAuthnContext authnCtx =
                (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        SPInitiatedLogoutRequestType sloRequest = (SPInitiatedLogoutRequestType)
                state.getLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:SSOLogoutRequest");

        SSOResponseType sloResponse = (SSOResponseType) in.getMessage().getContent();

        verifyResponse(sloResponse, sloRequest);

        LogoutRequest logoutRequest = authnCtx.getLogoutRequest();

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