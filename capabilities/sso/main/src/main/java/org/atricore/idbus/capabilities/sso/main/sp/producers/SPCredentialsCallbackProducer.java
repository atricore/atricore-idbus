package org.atricore.idbus.capabilities.sso.main.sp.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.CredentialType;
import org.atricore.idbus.common.sso._1_0.protocol.SPCredentialsCallbackRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SPCredentialsCallbackResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SPCredentialsCallbackProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog(SPCredentialsCallbackProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SPCredentialsCallbackProducer( AbstractCamelEndpoint<CamelMediationExchange> endpoint ) throws Exception {
        super( endpoint );
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        // Get credentials from provider state and send them back!

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPCredentialsCallbackRequestType ssoCcReq =
                (SPCredentialsCallbackRequestType) in.getMessage().getContent();

        SPInitiatedAuthnRequestType ssoRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        if (logger.isDebugEnabled())
            logger.debug("Providing credentials for SP Initiated SSO AuthnRequest " + ssoRequest.getID());

        SPCredentialsCallbackResponseType ssoCredResp = new SPCredentialsCallbackResponseType();
        ssoCredResp.setID(uuidGenerator.generateId());
        ssoCredResp.setInReplayTo(ssoCcReq.getID());

        if (logger.isTraceEnabled())
            logger.trace("Adding received " + ssoRequest.getCredentials().size() + " credentials.");

        ssoCredResp.getCredentials().addAll(ssoRequest.getCredentials());

        if (!endpoint.getBinding().equals(SSOBinding.SSO_LOCAL.toString())) {
            logger.error("Unsupported binding " + endpoint.getBinding());
        }

        EndpointDescriptor destination =
                new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "CredentialsCallbackService",
                        null,
                        null,
                        null);

        logger.debug("Sending SSO Credentials Callback Response through LOCAL binding");

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(ssoCredResp.getID(),
                ssoCredResp, "SPCredentialsCallbackResponse", null, destination, in.getMessage().getState()));

        exchange.setOut(out);


    }
}
