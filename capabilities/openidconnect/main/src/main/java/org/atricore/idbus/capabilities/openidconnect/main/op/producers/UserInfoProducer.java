package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectOPMediator;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public class UserInfoProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(UserInfoProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    // Ten seconds (TODO : Get from mediator/console)
    private long timeToleranceInMillis = 5L * 60L * 1000L;

    public UserInfoProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        UserInfoRequest userInfoRequest = (UserInfoRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();

        // Validate accessToken
        OpenIDConnectAuthnContext authnCtx =
                (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);

        if (authnCtx == null || authnCtx.getAccessToken() == null) {
            UserInfoErrorResponse userInfoErrorResponse = new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    userInfoErrorResponse,
                    "UserInfoResponse",
                    "application/json",
                    null, // TODO
                    in.getMessage().getState()));

            exchange.setOut(out);
            return;
        }

        AccessToken at = authnCtx.getAccessToken();
        if (at == null || !at.getValue().equals(userInfoRequest.getAccessToken().getValue())) {
            UserInfoErrorResponse userInfoErrorResponse = new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN);
            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    userInfoErrorResponse,
                    "UserInfoResponse",
                    "application/json",
                    null, // TODO
                    in.getMessage().getState()));

            exchange.setOut(out);
            return;
        }

        // TODO : Validate lifetime
        long lifetime = authnCtx.getAccessToken().getLifetime();

        JWT idToken = authnCtx.getIdToken();
        JWTClaimsSet claims = idToken.getJWTClaimsSet();
        UserInfo claimSet = new UserInfo(claims);
        UserInfoSuccessResponse userInfoSuccessResponse = new UserInfoSuccessResponse(claimSet);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                userInfoSuccessResponse,
                "UserInfoResponse",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);

    }
}
