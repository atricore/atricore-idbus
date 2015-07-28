package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Constants;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 * Receives an Authentication Request (AuthorizationRequest for OAuth 2.0 standard) and issues an authorization token.
 *
 * This service ba
 */
public class AuthorizationProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(AuthorizationProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public AuthorizationProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        MediationState state = in.getMessage().getState();
        BindingChannel bChannel = (BindingChannel) channel;

        AuthenticationRequest authnReq = (AuthenticationRequest) in.getMessage().getContent();



        OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext) state.getLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:authnCtx");

        try {
            validateRequest(authnReq);

            String idpAlias = null;
            String idpAliasB64 = in.getMessage().getState().getTransientVariable(OAuth2Constants.OAUTH2_IDPALIAS_VAR);

            if (idpAliasB64 == null) {
                idpAlias = authnCtx != null ? authnCtx.getIdpAlias() : null;

                if (logger.isDebugEnabled())
                    logger.debug("Using previous idp alias " + idpAlias);
            } else {
                idpAlias = new String(Base64.decodeBase64(idpAliasB64.getBytes("UTF-8")));
            }


            // SSO endpoint
            BindingChannel spChannel = resolveSpBindingChannel(bChannel);
            EndpointDescriptor destination = resolveSPInitiatedSSOEndpointDescriptor(exchange, spChannel);

            // Create SP AuthnRequest
            SPInitiatedAuthnRequestType request = buildAuthnRequest(exchange, idpAlias, authnReq);

            // Create context information
            authnCtx = new OpenIDConnectAuthnContext();
            authnCtx.setIdpAlias(idpAlias);
            authnCtx.setSsoAuthnRequest(request);
            authnCtx.setAuthnRequest(authnReq);

            // Store state
            in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:authnCtx", authnCtx);

            CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
            out.setMessage(new MediationMessageImpl(request.getID(),
                    request,
                    "SSOAuthnRequest",
                    null,
                    destination,
                    in.getMessage().getState()));

            exchange.setOut(out);

        } catch (Exception e) {
            // TODO : Return error
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @return
     */
    protected SPInitiatedAuthnRequestType buildAuthnRequest(CamelMediationExchange exchange, String idpAlias, AuthenticationRequest authnReq) {

        boolean passive = authnReq.getPrompt() != null && authnReq.getPrompt().toString().equals("none");
        boolean forceAuthn = authnReq.getPrompt() != null && authnReq.getPrompt().toString().equals("login");

        // TODO : Support other prompt options: login, consent and select_account
        if (authnReq.getPrompt() != null && (authnReq.getPrompt().equals("consent")
                || authnReq.getPrompt().equals("select_account"))) {
            logger.warn("Unsupported 'prompt' value " + authnReq.getPrompt());
        }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPInitiatedAuthnRequestType req = new SPInitiatedAuthnRequestType();
        req.setID(uuidGenerator.generateId());
        req.setPassive(passive);
        req.setForceAuthn(forceAuthn);

        RequestAttributeType idpAliasAttr = new RequestAttributeType();
        idpAliasAttr.setName(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR);
        idpAliasAttr.setValue(idpAlias);
        req.getRequestAttribute().add(idpAliasAttr);

        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
        }

        return req;
    }


    protected EndpointDescriptor resolveSPInitiatedSSOEndpointDescriptor(CamelMediationExchange exchange,
                                                                         BindingChannel sp) throws OpenIDConnectException {

        try {

            if (logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.SPInitiatedSingleSignOnService.toString());

            for (IdentityMediationEndpoint endpoint : sp.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleSignOnService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  sp.getIdentityMediator().resolveEndpoint(sp, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new OpenIDConnectException(e);
        }

        throw new OpenIDConnectException("No SP endpoint found for SP Initiated SSO using SSO Artifact binding");
    }

    protected BindingChannel resolveSpBindingChannel(BindingChannel bChannel) throws OpenIDConnectException {

        String spAlias = null; ((OpenIDConnectBPMediator)bChannel.getIdentityMediator()).getSpAlias();

        CircleOfTrust cot = getFederatedProvider().getCircleOfTrust();

        for (Provider p : cot.getProviders()) {

            if (p instanceof ServiceProvider) {

                ServiceProvider sp = (ServiceProvider)p;
                for (CircleOfTrustMemberDescriptor m : sp.getMembers()) {
                    if (m.getAlias().equals(spAlias)) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found Service Provider " + p.getName() + " for alias " + spAlias);

                        return ((ServiceProvider) p).getBindingChannel();

                    }
                }

            }
        }

        if (logger.isDebugEnabled())
            logger.debug("No Service Provider found for alias " + spAlias);

        return null;

    }

    protected void validateRequest(AuthenticationRequest authnReq) throws OpenIDConnectException {
        // TODO : Verify redirect_uri

        // TODO : Verify client_id

        // TODO : Verify response_type / response_mode consistency

        // TODO : Verify response_type with active flows

        // TODO : Mark nonce as used ?!
    }
}
