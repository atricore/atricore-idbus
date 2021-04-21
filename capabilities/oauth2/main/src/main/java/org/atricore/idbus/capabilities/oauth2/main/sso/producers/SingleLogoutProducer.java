package org.atricore.idbus.capabilities.oauth2.main.sso.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2BPMediator;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2Binding;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * Created by sgonzalez.
 */
public class SingleLogoutProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(SingleSignOnProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SingleLogoutProducer(Endpoint endpoint) {
        super(endpoint);
    }

    /**
     * Acts as SP initiated SSO service
     */
    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        if (in.getMessage().getContent() instanceof SSOResponseType) {

            SSOResponseType res = (SSOResponseType) in.getMessage().getContent();

            if (logger.isDebugEnabled())
                logger.debug("Processing SLO Response " + res.getID());


            OAuth2BPMediator mediator = (OAuth2BPMediator) channel.getIdentityMediator();

            String location = mediator.getResourceServer().getResourceLocation();

            EndpointDescriptor ed = new EndpointDescriptorImpl(
                    "SSOLogoutRequest",
                    "SSOLogoutRequest",
                    OAuth2Binding.OAUTH2_REDIRECT.getValue(),
                    location,
                    null);

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(null,
                    null,
                    "AuthenticationAssertion",
                    null,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

        }


    }
}
