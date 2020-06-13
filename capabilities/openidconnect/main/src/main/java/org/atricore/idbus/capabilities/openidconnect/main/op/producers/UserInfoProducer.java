package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.op.AuthnContext;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.Date;

public class UserInfoProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(UserInfoProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public UserInfoProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        UserInfoRequest userInfoRequest = (UserInfoRequest) in.getMessage().getContent();
        AccessToken receivedAt = userInfoRequest.getAccessToken();

        String authnCtxId = authnCtxId(receivedAt);
        AuthnContext authnCtx = (AuthnContext) state.getLocalVariable(authnCtxId);

        if (logger.isDebugEnabled())
            logger.debug("Processing user info request with access token: " + receivedAt);

        if (receivedAt == null) {
            if (logger.isDebugEnabled())
                logger.debug("No access token received");
            setResponse(exchange, new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN));
            return;
        }

        if (authnCtx == null || authnCtx.getAccessToken() == null) {
            if (logger.isDebugEnabled())
                logger.debug("No context/access token available");
            setResponse(exchange, new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN));
            return;
        }

        AccessToken at = authnCtx.getAccessToken();
        if (at == null || !at.getValue().equals(receivedAt.getValue())) {
            if (logger.isDebugEnabled())
                logger.debug("Invalid access token received/expected [" + receivedAt + "/" + at + "]");
            setResponse(exchange, new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN));
            return;
        }

        // Validate AT lifetime
        JWT jwtAt = JWTParser.parse(at.getValue());
        Date exp = jwtAt.getJWTClaimsSet().getExpirationTime();
        if (System.currentTimeMillis() > exp.getTime()) {
            setResponse(exchange, new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN));
            return;
        }

        JWT idToken = authnCtx.getIdToken();
        JWTClaimsSet claims = idToken.getJWTClaimsSet();
        UserInfo claimSet = new UserInfo(claims);
        setResponse(exchange, new UserInfoSuccessResponse(claimSet));

    }

    protected void setResponse(CamelMediationExchange exchange, UserInfoResponse response)  {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(
                new MediationMessageImpl(uuidGenerator.generateId(),
                    response,
                    response.getClass().getSimpleName(),
                    "application/json",
                    null,
                    in.getMessage().getState()));

        exchange.setOut(out);
    }
}
