package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.util.HashSet;
import java.util.Set;

public class OPStateRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(OPStateRestfulBinding.class);

    public OPStateRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_STATE_RESTFUL.getValue(), channel);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage oidcOut, Exchange exchange) {

        try {

            MediationMessage out = oidcOut.getMessage();
            Message httpOut = exchange.getOut();

            // ------------------------------------------------------------
            // Prepare HTTP Response
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "application/octet-stream");
            handleCrossOriginResourceSharing(exchange);

            httpOut.setBody(out.getRawContent());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);

        }
    }

}
