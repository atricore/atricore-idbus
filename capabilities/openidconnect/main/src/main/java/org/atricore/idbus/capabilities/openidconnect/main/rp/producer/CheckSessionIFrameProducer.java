package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import org.apache.camel.Endpoint;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class CheckSessionIFrameProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(CheckSessionIFrameProducer.class);

    public static final String CHK_SESSION_IFRAME_RESOURCE = "/check-session-iframe.html";

    private String iFrameContent;
    private Set<String> allowedOrigins;

    public CheckSessionIFrameProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        String iFrameContent = getIFrameContent();

        out.setMessage(new MediationMessageImpl(UUIDGenerator.generateJDKId(),
                null,
                iFrameContent,
                "application/html",
                null,
                null,
                in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected String getIFrameContent() throws OpenIDConnectException {

        if (iFrameContent == null) {

            OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();

            EndpointDescriptor ed = resolveOPStateEndpoint();

            //Build origins for configured client.
            mediator.getClient().getOIDCMetadata().getRedirectionURIs();

            // Should be in the current package
            InputStream is = getClass().getResourceAsStream(CHK_SESSION_IFRAME_RESOURCE);

            if (is == null) {
                logger.error("Cannot resolve iFrame resource: " + CHK_SESSION_IFRAME_RESOURCE);
                throw new OpenIDConnectException("internal_error");
            }

            try {
                String content = IOUtils.toString(is);
                content = content.replace("##__OP_STATE__##", ed.getLocation());
                iFrameContent = content;
            } catch (IOException e) {
                logger.error("Cannot load iFrame content " + e.getMessage(), e);
            }
        }

        return iFrameContent;
    }

    protected EndpointDescriptor resolveOPStateEndpoint() throws OpenIDConnectException {

        String service = OpenIDConnectConstants.OPStateService_QNAME.toString();
        OpenIDConnectBinding binding = OpenIDConnectBinding.OPENID_PROVIDER_STATE_RESTFUL;

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {

            if (service.equals(endpoint.getType())) {

                if (endpoint.getBinding().equals(binding.getValue())) {

                    return new EndpointDescriptorImpl(endpoint.getName(),
                            endpoint.getType(),
                            endpoint.getBinding(),
                            channel.getLocation() + endpoint.getLocation(), null);
                }
            }
        }

        throw new OpenIDConnectException("No endpoint found for service/binding " + service + "/" + binding);
    }
}
