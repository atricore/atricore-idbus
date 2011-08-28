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
import org.atricore.idbus.capabilities.josso.main.JossoConstants;
import org.atricore.idbus.capabilities.josso.main.JossoException;
import org.atricore.idbus.capabilities.sso.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.sso.support.metadata.SamlR2Service;
import org.atricore.idbus.common.sso._1_0.protocol.RequestAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

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
        String idpAliasB64 = in.getMessage().getState().getTransientVariable(JossoConstants.JOSSO_IDPALIAS_VAR);
        String idpAlias = null;

        // Decode IDP Alias, if any
        if (idpAliasB64 != null) {
            idpAlias = URLDecoder.decode(new String(CipherUtil.decodeBase64(idpAliasB64)), "UTF-8");

            if (logger.isDebugEnabled())
                logger.debug("Using received idp alias " + idpAlias);
        }

        if (idpAlias == null) {
            idpAlias = (String) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:capabilities:josso:idpAlias");

            if (logger.isDebugEnabled())
                logger.debug("Using previous idp alias " + idpAlias);
        }

        if (logger.isDebugEnabled())
            logger.debug("Starting JOSSO 1 SSO, requester [" + appId + "] cmd ["+cmd+"], " +
                    "back_to ["+backTo+"] idpAlias ["+idpAlias+"]");

        BindingChannel spChannel = resolveSpBindingChannel(bChannel, appId);

        EndpointDescriptor destination = resolveSPInitiatedSSOEndpointDescriptor(exchange, spChannel);

        // Create SP AuthnRequest
        // TODO : Support on_error ?
        SPInitiatedAuthnRequestType request = buildAuthnRequest(exchange, idpAlias);

        // Store state
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:backTo", backTo);
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:authnRequest", request);
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:appId", appId);
        in.getMessage().getState().setLocalVariable("urn:org:atricore:idbus:capabilities:josso:idpAlias", idpAlias);

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
        idpAliasAttr.setName("atricore_idp_alias");
        idpAliasAttr.setValue(idpAlias);
        req.getRequestAttribute().add(idpAliasAttr);

        
        if (logger.isDebugEnabled())
            logger.debug(JossoConstants.JOSSO_CMD_VAR + "='" + cmd +"'");

        // Send all transient vars to SP
        for (String tvarName : in.getMessage().getState().getTransientVarNames()) {
            RequestAttributeType a = new RequestAttributeType();
            a.setName(tvarName);
            a.setValue(in.getMessage().getState().getTransientVariable(tvarName));
        }

        return req;
    }


    protected EndpointDescriptor resolveSPInitiatedSSOEndpointDescriptor(CamelMediationExchange exchange,
                                                                         BindingChannel sp) throws JossoException {

        try {

            logger.debug("Looking for " + SamlR2Service.SPInitiatedSingleSignOnService.toString());

            for (IdentityMediationEndpoint endpoint : sp.getEndpoints()) {

                logger.debug("Processing endpoint : " + endpoint.getType() + "["+endpoint.getBinding()+"]");

                if (endpoint.getType().equals(SamlR2Service.SPInitiatedSingleSignOnService.toString())) {

                    if (endpoint.getBinding().equals(SamlR2Binding.SSO_ARTIFACT.getValue())) {
                        // This is the endpoint we're looking for
                        return  sp.getIdentityMediator().resolveEndpoint(sp, endpoint);
                    }
                }
            }
        } catch (IdentityMediationException e) {
            throw new JossoException(e);
        }

        throw new JossoException("No SP endpoint found for SP Initiated SSO using SSO Artifact binding");
    }

}
