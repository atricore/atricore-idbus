package org.atricore.idbus.capabilities.openidconnect.main.rp.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.AbstractOpenIDRestfulBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.UserInfoRequestRestfulBinding;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.IDBusHttpConstants;
import org.atricore.idbus.kernel.main.mediation.camel.component.http.XFrameOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class CheckSessionIFrameRestfulBinding extends AbstractOpenIDRestfulBinding {

    private static final Log logger = LogFactory.getLog(UserInfoRequestRestfulBinding.class);


    public CheckSessionIFrameRestfulBinding(Channel channel) {
        super(OpenIDConnectBinding.OPENID_PROVIDER_CHKSESSION_IFRAME_RESTFUL.getValue(), channel);
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

            // Cross origin support
            Set<String> allowedOrigins = null;
            String xFrameOptoinsURLs = "";
            if (oidcOut.getHeader("OIDC-Origins") != null) {
                allowedOrigins = (Set<String>) oidcOut.getHeader("OIDC-Origins");

                for (String allowedOrigin : allowedOrigins) {
                    xFrameOptoinsURLs += " " + allowedOrigin;
                }

                // X-FrameOptions
                httpOut.getHeaders().put(IDBusHttpConstants.HTTP_HEADER_FRAME_OPTIONS, XFrameOptions.ALLOW_FROM + xFrameOptoinsURLs);
                httpOut.getHeaders().put(IDBusHttpConstants.HTTP_HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors" + xFrameOptoinsURLs);
            }

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "text/html");
            handleCrossOriginResourceSharing(exchange, allowedOrigins);

            httpOut.setBody(out.getRawContent());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);

        }
    }
}

