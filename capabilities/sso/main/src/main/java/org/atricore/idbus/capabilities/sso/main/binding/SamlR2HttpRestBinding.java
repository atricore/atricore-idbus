package org.atricore.idbus.capabilities.sso.main.binding;

import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsMediator;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediator;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

// Only used to replya an authn response as a REST response (NON-NORMATIVE) instead of redirecting to the ACS
public class SamlR2HttpRestBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpRestBinding.class);

    public SamlR2HttpRestBinding(Channel channel) {
        super(SSOBinding.SAMLR2_REST.getValue(), channel);
    }

    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        throw new UnsupportedOperationException("Do not use as incomming binding!");
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage message, Exchange exchange) {
        MediationMessage out = message.getMessage();

        Message httpOut = exchange.getOut();

        Set<String> allowedAncestors = new HashSet<String>();
        String allowedOrigin = null;
        if (channel instanceof SPChannel) {
            // We act as IDP. We are a REST binding, add cross-origin support / iframe support for authentication page (if any)

            if (channel.getClaimProviders() != null) {

                for (ClaimChannel cc : channel.getClaimProviders()) {
                    IdentityMediator cm = cc.getIdentityMediator();
                    if (cm instanceof SSOClaimsMediator) {
                        SSOClaimsMediator ssocm = (SSOClaimsMediator) cm;
                        String source = ssocm.getBasicAuthnUILocation();
                        if (source != null && !source.equals("")) {

                            try {
                                URL s = new URL(source);
                                allowedAncestors.add(source);
                                allowedOrigin = s.getProtocol() + "://" + s.getHost() + (s.getPort() != 80 && s.getPort() != 443 ? ":" + s.getPort() : "");
                                allowedAncestors.add(allowedOrigin);
                                if (logger.isTraceEnabled())
                                    logger.trace("Allowing origin :" + allowedOrigin + " for rest endpoint");
                            } catch (MalformedURLException e) {
                                logger.error("Ignoring source : " + source + "("+e.getMessage()+")");
                            }
                        }
                    }
                }
            }
        }

        // ------------------------------------------------------------
        // Prepare HTTP Resposne
        // ------------------------------------------------------------
        copyBackState(out.getState(), exchange);

        httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
        httpOut.getHeaders().put("Pragma", "no-cache");
        httpOut.getHeaders().put("http.responseCode", 200);
        httpOut.getHeaders().put("Content-Type", "application/json");
        handleCrossOriginResourceSharing(exchange, allowedOrigin);
        handleCSPFrameAncestors(exchange, allowedAncestors);

        String payload = "{ \"status_code\":\"N/A\"}\n";

        if (out.getContent() instanceof StatusResponseType) {
            StatusResponseType r = (StatusResponseType) out.getContent();
            payload = toJSON(r.getStatus());
        }

        ByteArrayInputStream baos = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
        httpOut.setBody(baos);

    }

    protected String toJSON(StatusType s) {
        return "{\n" +
                "\"status_code\": \"" + (s.getStatusCode().getValue() != null ? s.getStatusCode().getValue() : "n/a") + "\",\n" +
                "\"status_message\": \"" + (s.getStatusMessage() != null ? s.getStatusMessage() : "n/a") + "\"\n" +
                "}\n";
    }
}
