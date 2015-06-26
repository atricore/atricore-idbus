/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.common.sso._1_0.protocol.IDPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.PreAuthenticatedIDPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.Map;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SamlR2SsoIDPInitiatedHttpBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2SsoIDPInitiatedHttpBinding.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SamlR2SsoIDPInitiatedHttpBinding(Channel channel) {
        super(SSOBinding.SSO_IDP_INITIATED_SSO_HTTP_SAML2.getValue(), channel);
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
        IDPInitiatedAuthnRequestType idpInitReq = null;
        if (securityToken != null) {
            idpInitReq = new PreAuthenticatedIDPInitiatedAuthnRequestType();
            ((PreAuthenticatedIDPInitiatedAuthnRequestType)idpInitReq).setSecurityToken(securityToken);
            ((PreAuthenticatedIDPInitiatedAuthnRequestType)idpInitReq).setAuthnCtxClass(AuthnCtxClass.OAUTH2_PREAUTHN_PASSIVE_CTX.getValue());

            String rememberMe = state.getTransientVariable("remember_me");
            if (rememberMe != null) {
                // TODO : idpInitReq.setRememberMe(Boolean.parseBoolean(rememberMe));
            }
        } else {
            idpInitReq = new IDPInitiatedAuthnRequestType();
        }

        idpInitReq.setID(uuidGenerator.generateId());
        idpInitReq.setPreferredResponseFormat("urn:oasis:names:tc:SAML:2.0");


        // We can send several attributes within the request.
        String spAlias = state.getTransientVariable("atricore_sp_alias");
        if (spAlias != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_alias");
            a.setValue(spAlias);
            idpInitReq.getRequestAttribute().add(a);
        }

        String spId = state.getTransientVariable("atricore_sp_id");
        if (spId != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_sp_id");
            a.setValue(spId);
            idpInitReq.getRequestAttribute().add(a);
        }

        String idpAlias = state.getTransientVariable("atricore_idp_alias");
        if (idpAlias != null) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName("atricore_idp_alias");
            a.setValue(idpAlias);
            idpInitReq.getRequestAttribute().add(a);
        }

        String passive = state.getTransientVariable("passive");
        if (passive != null) {
            idpInitReq.setPassive(Boolean.parseBoolean(passive));
        }
       
        return new MediationMessageImpl<IDPInitiatedAuthnRequestType>(message.getMessageId(),
                        idpInitReq,
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
        if (out.getContent() != null)
            throw new IllegalStateException("Content not supported for IDBUS HTTP Redirect bidning");

        // ------------------------------------------------------------
        // Create HTML Form for response body
        // ------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Creating HTML Redirect to " + ed.getLocation());

        String ssoQryString = "";

        ssoQryString += "?ResponseMode=unsolicited";

        if (out.getRelayState() != null) {
            ssoQryString += "&relayState=" + out.getRelayState() ;
        }

        Message httpOut = exchange.getOut();
        Message httpIn = exchange.getIn();

        String ssoRedirLocation = this.buildHttpTargetLocation(httpIn, ed) + ssoQryString;

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