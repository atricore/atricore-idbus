package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.ByteArrayInputStream;

public class PayloadResolutionBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(PayloadResolutionBinding.class);

    public static final String SSO_PAYLOAD = "uuid";

    public PayloadResolutionBinding(Channel channel) {
        super(SSOBinding.SSO_PAYLOAD.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {
        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        Message httpMsg = exchange.getIn();

        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);

        return new MediationMessageImpl(message.getMessageId(),
                null,
                null,
                null,
                null,
                state);    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {

        MediationMessage out = samlOut.getMessage();
        Message httpOut = exchange.getOut();

        // ------------------------------------------------------------
        // Prepare HTTP Resposne
        // ------------------------------------------------------------
        copyBackState(out.getState(), exchange);

        httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
        httpOut.getHeaders().put("Pragma", "no-cache");
        httpOut.getHeaders().put("http.responseCode", 200);
        httpOut.getHeaders().put("Content-Type", "text/html");
        handleCrossOriginResourceSharing(exchange);

        ByteArrayInputStream baos = new ByteArrayInputStream(out.getRawContent().getBytes());
        httpOut.setBody(baos);
    }
}
