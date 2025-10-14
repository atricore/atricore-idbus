package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SsoIDPReselectedHttpBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SsoIDPReselectedHttpBinding.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SsoIDPReselectedHttpBinding(Channel channel) {
        super(SSOBinding.SSO_IDP_RESELECTED_SSO_HTTP_SAML2.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null) {

            if (logger.isDebugEnabled()) {
                Map <String, Object> h = httpMsg.getHeaders();
                for (String key : h.keySet()) {
                    logger.debug("CAMEL Header:" + key + ":"+ h.get(key));
                }
            }

            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }


        // HTTP Request Parameters from HTTP Request body
        MediationState state = createMediationState(exchange);
        String relayState = state.getTransientVariable("RelayState");

        String securityToken = state.getTransientVariable("atricore_security_token");
        IDPReselectedRequest idpReselect = null;
        idpReselect = new IDPReselectedRequest();
        idpReselect.setID(uuidGenerator.generateId());

        // We can send several attributes within the request.
        String spAlias = state.getTransientVariable("atricore_sp_alias");
        if (spAlias != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_alias");
            a.setValue(spAlias);
            idpReselect.getRequestAttribute().add(a);
        }

        String spId = state.getTransientVariable("atricore_sp_id");
        if (spId != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_id");
            a.setValue(spId);
            idpReselect.getRequestAttribute().add(a);
        }

        String idpAlias = state.getTransientVariable("atricore_idp_alias");
        if (idpAlias != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_idp_alias");
            a.setValue(idpAlias);
            idpReselect.getRequestAttribute().add(a);
            idpReselect.setIdpAlias(idpAlias);
        }

        String forceAuthn = state.getTransientVariable("force_authn");
        if (forceAuthn != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("force_authn");
            a.setValue(forceAuthn);
            idpReselect.getRequestAttribute().add(a);
        }

        String authnCtxClass = state.getTransientVariable("authn_ctx_class");
        if (authnCtxClass != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("authn_ctx_class");
            a.setValue(authnCtxClass);
            idpReselect.getRequestAttribute().add(a);
        }

        // Valid values are from SSOBinding
        String bindingStr =  state.getTransientVariable("protocol_binding");
        if (bindingStr != null) {
            try {
                SSOBinding binding = SSOBinding.asEnum(bindingStr);
                //idpReselect.setProtocolBinding(binding.getValue());

                if (logger.isDebugEnabled())
                    logger.debug("Using protocol binding: " + binding.getValue());

            } catch (IllegalArgumentException e) {
                logger.error ("Ignoring requested binding: " + e.getMessage());
            }
        }

        return new MediationMessageImpl<IDPReselectedRequest>(message.getMessageId(),
                idpReselect,
                null,
                relayState,
                null,
                state);

    }

    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {
        // Content is OPTIONAL
        MediationMessage out = samlOut.getMessage();
        EndpointDescriptor ed = out.getDestination();

        // ------------------------------------------------------------
        // Validate received message
        // ------------------------------------------------------------
        assert ed != null : "Mediation Response MUST Provide a destination";

        String ssoRedirLocation = null;
        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();

        if (out.getContent() != null) {

            if (out.getContent() instanceof PreAuthenticatedIDPInitiatedAuthnRequestType) {

                PreAuthenticatedIDPInitiatedAuthnRequestType req = (PreAuthenticatedIDPInitiatedAuthnRequestType) out.getContent();
                // ------------------------------------------------------------
                // Send redirect
                // ------------------------------------------------------------
                if (logger.isDebugEnabled())
                    logger.debug("Creating HTML Redirect to " + ed.getLocation());

                String ssoQryString = "";

                ssoQryString += "?ResponseMode=unsolicited";

                if (out.getRelayState() != null) {
                    ssoQryString += "&relayState=" + out.getRelayState();
                }

                try {
                    ssoQryString += "&atricore_security_token=" + URLEncoder.encode(req.getSecurityToken(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                if (req.getRememberMe() != null)
                    ssoQryString += "&remember_me=" + req.getRememberMe();

                for (RequestAttributeType attr : req.getRequestAttribute()) {

                    if (attr.getName().equals("atricore_sp_alias")) {
                        try {
                            ssoQryString += "&atricore_sp_alias=" +  URLEncoder.encode(attr.getValue(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                ssoRedirLocation = this.buildHttpTargetLocation(httpIn, ed) + ssoQryString;


            } else {
                throw new IllegalStateException("Content not supported for IDBUS HTTP Redirect bidning");
            }
        } else {

            // ------------------------------------------------------------
            // Send redirec
            // ------------------------------------------------------------
            if (logger.isDebugEnabled())
                logger.debug("Creating HTML Redirect to " + ed.getLocation());

            String ssoQryString = "";

            ssoQryString += "?ResponseMode=unsolicited";

            if (out.getRelayState() != null) {
                ssoQryString += "&relayState=" + out.getRelayState();
            }

            ssoRedirLocation = this.buildHttpTargetLocation(httpIn, ed) + ssoQryString;
        }

        if (logger.isDebugEnabled())
            logger.debug("Redirecting to " + ssoRedirLocation);

        // ------------------------------------------------------------
        // Prepare HTTP Resposne
        // ------------------------------------------------------------
        copyBackState(out.getState(), exchange);

        httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
        httpOut.getHeaders().put("Pragma", "no-cache");
        httpOut.getHeaders().put("http.responseCode", 302);
        httpOut.getHeaders().put("Content-Type", "text/html");
        httpOut.getHeaders().put("Location", ssoRedirLocation);
        handleCrossOriginResourceSharing(exchange);


    }

}