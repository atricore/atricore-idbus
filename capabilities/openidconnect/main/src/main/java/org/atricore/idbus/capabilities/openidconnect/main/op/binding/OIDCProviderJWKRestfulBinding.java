package org.atricore.idbus.capabilities.openidconnect.main.op.binding;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.SerializeException;
import net.minidev.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.ByteArrayInputStream;

public class OIDCProviderJWKRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(OIDCProviderJWKRestfulBinding.class);

    public OIDCProviderJWKRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_JWK_RESTFUL.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {

        return new MediationMessageImpl(message.getMessageId(),
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage oidcOut, Exchange exchange) {
        MediationMessage out = oidcOut.getMessage();

        String marshalledHttpResponseBody = "";

        if (out.getContent() instanceof JWKSet) {
            try {
                JWKSet keySet = (JWKSet) out.getContent();
                JSONObject jsonMd = keySet.toJSONObject();

                marshalledHttpResponseBody = jsonMd.toString();

            } catch (SerializeException e) {
                logger.error("Error marshalling JWKSet to JSON: " + e.getMessage(), e);
                throw new IllegalStateException("Error marshalling AccessTokenResponseType to JSON: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Content type supported for OAuth2 HTTP Redirect binding " + out.getContentType() + " ["+out.getContent()+"]");
        }

        Message httpOut = exchange.getOut();

        if (logger.isDebugEnabled())
            logger.debug("Returning json response: " + marshalledHttpResponseBody);

        try {

            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "application/json");
            handleCrossOriginResourceSharing(exchange);

            ByteArrayInputStream baos = new ByteArrayInputStream(marshalledHttpResponseBody.getBytes());
            httpOut.setBody(baos);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
