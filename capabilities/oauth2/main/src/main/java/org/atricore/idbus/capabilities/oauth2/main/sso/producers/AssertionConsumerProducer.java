package org.atricore.idbus.capabilities.oauth2.main.sso.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2AuthnContext;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2BPMediator;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Exception;
import org.atricore.idbus.capabilities.oauth2.main.ResourceServer;
import org.atricore.idbus.capabilities.oauth2.main.binding.OAuth2Binding;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.net.URLEncoder;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AssertionConsumerProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(AssertionConsumerProducer.class);

    public AssertionConsumerProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();

        SPAuthnResponseType response = (SPAuthnResponseType) in.getMessage().getContent();

        // TODO : Add support for IDP initiated SSO in OAUTH2
        OAuth2AuthnContext authnCtx = (OAuth2AuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx");
        SPInitiatedAuthnRequestType request = authnCtx != null ? authnCtx.getAuthnRequest() : null;
        if (request == null) {
            // Process unsolicited response
            validateUnsolicitedAuthnResposne(exchange, response);
        } else {
            validateSolicitedAuthnResponse(exchange, request, response);
        }

        String accessToken = resolveAccessToken(response);

        OAuth2BPMediator bpMediator = (OAuth2BPMediator) channel.getIdentityMediator();
        ResourceServer rServer = bpMediator.getResourceServer();
        String rServerLocation = rServer.getResourceLocation();

        if (logger.isDebugEnabled())
            logger.debug("Using Resource Server URL: [" + rServerLocation + " ]");

        // Create destination with back/to and HTTP-Redirect binding
        EndpointDescriptor destination = new EndpointDescriptorImpl("OAuth2ResourceServer",
                "AccessToken",
                OAuth2Binding.OAUTH2_RESTFUL.getValue(),
                rServerLocation, null);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(response.getID(),
                accessToken, "AccessToken", null, destination, state));

        exchange.setOut(out);

    }

    protected void validateSolicitedAuthnResponse(CamelMediationExchange exchange,
                                                  SPInitiatedAuthnRequestType request,
                                                  SPAuthnResponseType response) throws OAuth2Exception {

        if (response  == null) {
            throw new OAuth2Exception("No response found!");
        }

        // TODO : Validate in-reply-to and other attributes: target acs, etc.
        if (response.getInReplayTo() == null || !request.getID().equals(response.getInReplayTo())) {
            throw new OAuth2Exception("Response is not a reply to " +
                    request.getID() + " ["+(response.getInReplayTo() == null ? "<null>" : response.getInReplayTo())+"]");
        }

        validateAuthnResponse(exchange, response);
    }

    protected void validateUnsolicitedAuthnResposne(CamelMediationExchange exchange, SPAuthnResponseType response) throws OAuth2Exception {
        // TODO : other attributes: target acs, etc.
        validateAuthnResponse(exchange, response);
        if (response  == null) {
            throw new OAuth2Exception("No response found!");
        }
    }

    protected void validateAuthnResponse(CamelMediationExchange exchange, SPAuthnResponseType response) throws OAuth2Exception {
        // Make sure that we have an OAUTH2 TOKEN
        if (resolveAccessToken(response) == null) {
            throw new OAuth2Exception("No token of type " + WSTConstants.WST_OAUTH2_TOKEN_TYPE + " received in subject attributes set");
        }
    }

    protected String resolveAccessToken(SPAuthnResponseType response) {
        SubjectType subject = response.getSubject();

        for (AbstractPrincipalType p : subject.getAbstractPrincipal()) {
            if (p instanceof SubjectAttributeType) {
                SubjectAttributeType attr = (SubjectAttributeType) p;

                if (attr.getName().equals(WSTConstants.WST_OAUTH2_TOKEN_TYPE)) {
                    return attr.getValue();
                }
            }
        }

        return null;
    }

}
