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

package org.atricore.idbus.capabilities.josso.main.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoAuthnContext;
import org.atricore.idbus.capabilities.josso.main.JossoConstants;
import org.atricore.idbus.capabilities.josso.main.JossoException;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectorConstants;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.common.sso._1_0.protocol.CredentialType;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.AttributedString;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;
import java.net.URLDecoder;

/**
 * JOSSO 1.1 Binding single signon producer
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SingleSignOnProducer extends AbstractJossoProducer {

    private static final Log logger = LogFactory.getLog(SingleSignOnProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator ();

    public SingleSignOnProducer(Endpoint endpoint) {
        super(endpoint);
    }

    /**
     */
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        BindingChannel bChannel = (BindingChannel) channel;

        String backTo = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_BACK_TO_VAR);
        String cmd = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_CMD_VAR);

        String appId = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_APPID_VAR);
        if (appId == null)
            throw new JossoException("Application ID parameter value not found");

        appId = appId.toLowerCase();

        String idpAliasB64 = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_IDPALIAS_VAR);
        String idpAlias = null;

        // This may be an authentication request that provides user credentials for BASIC authentication
        String username = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_USERNAME_VAR);
        String password = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_PASSWORD_VAR);

        JossoAuthnContext authnCtx = (JossoAuthnContext) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId);

        // Decode IDP Alias, if any
        if (idpAliasB64 != null) {
            idpAlias = URLDecoder.decode(new String(CipherUtil.decodeBase64(idpAliasB64)), "UTF-8");

            if (logger.isDebugEnabled())
                logger.debug("Using received idp alias " + idpAlias);
        }

        if (idpAlias == null) {
            idpAlias = authnCtx != null ? authnCtx.getIdpAlias() : null;

            if (logger.isDebugEnabled())
                logger.debug("Using previous idp alias " + idpAlias);
        }

        if (logger.isDebugEnabled())
            logger.debug("Starting JOSSO 1 SSO, requester [" + appId + "] cmd ["+cmd+"], " +
                    "back_to ["+backTo+"] idpAlias ["+idpAlias+"]");

        BindingChannel spBindingChannel = resolveSpBindingChannel(bChannel, appId);

        EndpointDescriptor destination = resolveSPInitiatedSSOEndpointDescriptor(exchange, spBindingChannel);

        // Create SP AuthnRequest
        // TODO : Support on_error ?
        SPInitiatedAuthnRequestType request = buildAuthnRequest(exchange, idpAlias);

        // Create context information
        authnCtx = new JossoAuthnContext();
        authnCtx.setAppId(appId);
        authnCtx.setSsoBackTo(backTo);
        authnCtx.setIdpAlias(idpAlias);
        authnCtx.setAuthnRequest(request);

        // Store state and request
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnCtx:" + appId, authnCtx);

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        out.setMessage(new MediationMessageImpl(request.getID(),
                request,
                "SSOAuthnRequest",
                null,
                destination,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    /**
     * @return
     */
    protected SPInitiatedAuthnRequestType buildAuthnRequest(CamelMediationExchange exchange,
                                                        String idpAlias) {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SPInitiatedAuthnRequestType req = new SPInitiatedAuthnRequestType();
        req.setID(uuidGenerator.generateId());

        String cmd = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_CMD_VAR);

        req.setPassive(cmd != null && cmd.equals("login_optional"));

        RequestAttributeType idpAliasAttr = new RequestAttributeType();
        idpAliasAttr.setName(EntitySelectorConstants.REQUESTED_IDP_ALIAS_ATTR);
        idpAliasAttr.setValue(idpAlias);
        req.getRequestAttribute().add(idpAliasAttr);

        
        if (logger.isDebugEnabled())
            logger.debug(JossoConstants.JOSSO_CMD_VAR + "='" + cmd +"'");

        String username = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_USERNAME_VAR);
        String password = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_PASSWORD_VAR);


        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
        }

        if (username != null && password != null) {

            if (logger.isDebugEnabled())
                logger.debug("Initializing Authnentiation request w/credentials");

            // Send credentials with authn request:
            UsernameTokenType usernameToken = new UsernameTokenType ();
            AttributedString usernameString = new AttributedString();
            usernameString.setValue( username );

            usernameToken.setUsername( usernameString );
            usernameToken.getOtherAttributes().put(new QName(Constants.PASSWORD_NS), password );

            CredentialType ct = new CredentialType();
            ct.setAny(usernameToken);

            req.getCredentials().add(ct);

            if (logger.isDebugEnabled())
                logger.debug("Received basic credentials for user " + username + ", forcing authentication");

            req.setForceAuthn(true);
            req.setAuthnCtxClass(AuthnCtxClass.ATC_SP_PASSWORD_AUTHN_CTX.getValue());

        } else if (username != null && cmd != null && cmd.equals("impersonate")) {
            if (logger.isDebugEnabled())
                logger.debug("Initializing Authnentiation request for impersonation");

            // Send credentials with authn request:
            UsernameTokenType usernameToken = new UsernameTokenType ();
            AttributedString usernameString = new AttributedString();
            usernameString.setValue( username );

            usernameToken.setUsername( usernameString );
            usernameToken.getOtherAttributes().put(new QName(Constants.IMPERSONATE_NS), password );

            CredentialType ct = new CredentialType();
            ct.setAny(usernameToken);

            req.setAuthnCtxClass(AuthnCtxClass.ATC_SP_IMPERSONATE_AUTHN_CTX.getValue());
            req.setForceAuthn(true);

            req.getCredentials().add(ct);

        }

        return req;
    }


    protected EndpointDescriptor resolveSPInitiatedSSOEndpointDescriptor(CamelMediationExchange exchange,
                                                                         BindingChannel bChannel) throws JossoException {

        try {

            if(logger.isDebugEnabled())
                logger.debug("Looking for " + SSOService.SPInitiatedSingleSignOnService.toString() + " on channel " + bChannel.getName());

            for (IdentityMediationEndpoint endpoint : bChannel.getEndpoints()) {

                if (logger.isDebugEnabled())
                    logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SSOService.SPInitiatedSingleSignOnService.toString())) {

                    if (endpoint.getBinding().equals(SSOBinding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  bChannel.getIdentityMediator().resolveEndpoint(bChannel, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new JossoException(e);
        }

        throw new JossoException("No SP endpoint found for SP Initiated SSO using SSO Artifact binding");
    }

}
