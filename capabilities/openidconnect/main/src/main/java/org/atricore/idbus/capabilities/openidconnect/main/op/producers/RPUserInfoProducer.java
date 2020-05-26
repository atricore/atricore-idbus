package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;

public class RPUserInfoProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(RPUserInfoProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public RPUserInfoProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        UserInfoRequest userInfoRequest = (UserInfoRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();

        // Forward Token request to destination endpoint

        // Resolve IDP TOKEN endpoint, it supports multiple IDPs configured!
        OpenIDConnectAuthnContext authnCtx =
                (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        // Use localhost actually!
        EndpointDescriptor userInfoEndpoint = lookupUserInfoEndpoint(authnCtx);

        // Use localhost actually!
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();
        String targetBaseUrl = mediator.getKernelConfigCtx().getProperty("binding.http.localTargetBaseUrl", "http://localhost:8081");

        // Build token URI
        URI userInfoUri = new URI(userInfoEndpoint.getLocation());
        String internalUserInfoEndpoint = targetBaseUrl + userInfoUri.getPath();


        // Create a new USERINFO request w/new IDP TOKEN USERINFO
        UserInfoRequest proxyUserInfoRequest = new UserInfoRequest(new URI(internalUserInfoEndpoint), (BearerAccessToken) userInfoRequest.getAccessToken());


        // Send request/process response
        HTTPResponse proxyResponse = proxyUserInfoRequest.toHTTPRequest().send();

        UserInfoResponse proxyUserInfoResponse = UserInfoResponse.parse(proxyResponse);

        if (proxyUserInfoResponse.indicatesSuccess()) {
            UserInfoSuccessResponse s = proxyUserInfoResponse.toSuccessResponse();
            UserInfo userInfo = s.getUserInfo();
            userInfo.getSubject();
        } else {
            UserInfoErrorResponse err = proxyUserInfoResponse.toErrorResponse();
            ErrorObject error = err.getErrorObject();
            if (logger.isDebugEnabled())
                logger.error("Error obtaining User Information : " + error.getCode() + ". " + error.getDescription());
        }

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                proxyUserInfoResponse,
                "UserInfoResponse",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);
    }

}
