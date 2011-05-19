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

package org.atricore.idbus.capabilities.spnego.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.io.HexDump;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsRequest;
import org.atricore.idbus.capabilities.samlr2.main.claims.SamlR2ClaimsResponse;
import org.atricore.idbus.capabilities.samlr2.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.spnego.*;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.*;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.ietf.jgss.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.Collection;


/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpnegoNegotiationProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(SpnegoNegotiationProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SpnegoNegotiationProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        Object content = in.getMessage().getContent();

        SpnegoMessage spnegoResponse = null;

        if (logger.isDebugEnabled())
            logger.info("doProcess() - Received SPNEGO Message = " + content);


        if (content instanceof UnauthenticatedRequest) {
            spnegoResponse = doProcessClaimsRequest(exchange, (UnauthenticatedRequest) content);
        }

        // Send spnegoResponse back.
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(), endpoint.getBinding(), null, null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                spnegoResponse,
                null,
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);
    }

    @Override
    protected void doProcessResponse(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        Object content = in.getMessage().getContent();

        SpnegoMessage spnegoResponse = null;

        if (logger.isDebugEnabled())
            logger.info("doProcess() - Received SPNEGO Message = " + content);


        if (content instanceof UnauthenticatedRequest) {
            spnegoResponse = doProcessUnauthenticatedRequest(exchange, (UnauthenticatedRequest) content);
        } else
        if (content instanceof AuthenticatedRequest) {
            spnegoResponse = doProcessAuthenticatedRequest(exchange, (AuthenticatedRequest) content);
        }

        // Send spnegoResponse back.
        EndpointDescriptor ed = new EndpointDescriptorImpl(endpoint.getName(),
                endpoint.getType(), endpoint.getBinding(), null, null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                spnegoResponse,
                null,
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);
    }


    protected SpnegoMessage doProcessClaimsRequest(CamelMediationExchange exchange, UnauthenticatedRequest content) throws Exception {
        logger.info("Initiating Spnego Negotiation");
        return new InitiateSpnegoNegotiation( channel.getLocation() + endpoint.getResponseLocation());
    }

    protected SpnegoMessage doProcessUnauthenticatedRequest(CamelMediationExchange exchange, UnauthenticatedRequest content) {
        logger.info("Requesting Token to SPNEGO initiator");
        return new RequestToken();
    }

    /* Factor out authentication to STS */
    protected SpnegoMessage doProcessAuthenticatedRequest(CamelMediationExchange exchange, AuthenticatedRequest content) throws Exception {
        final SpnegoMediator spnegoMediator = (SpnegoMediator) channel.getIdentityMediator();
        final byte[] securityToken = content.getTokenValue();

        // -------------------------------------------------------------------------
        // Process collected claims
        // -------------------------------------------------------------------------
        if (logger.isDebugEnabled())
            logger.debug("Received SPNEGO Security Token");

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        ClaimsRequest claimsRequest = (ClaimsRequest) in.getMessage().getState().getLocalVariable("urn:org:atricore:idbus:claims-request");
        if (claimsRequest == null)
            throw new IllegalStateException("Claims request not found!");

        if (logger.isDebugEnabled())
            logger.debug("Recovered claims request from local variable, id:" + claimsRequest.getId());

        SpnegoMediator mediator = ((SpnegoMediator) channel.getIdentityMediator());

        // This is the binding we're using to send the response
        SamlR2Binding binding = SamlR2Binding.SSO_ARTIFACT;
        Channel issuer = claimsRequest.getIssuerChannel();

        IdentityMediationEndpoint claimsProcessingEndpoint = null;

        // Look for an endpoint to send the response
        for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
            if (endpoint.getType().equals(claimsRequest.getIssuerEndpoint().getType()) &&
                    endpoint.getBinding().equals(binding.getValue())) {
                claimsProcessingEndpoint = endpoint;
                break;
            }
        }

        if (claimsProcessingEndpoint == null) {
            throw new SpnegoException("No endpoint supporting " + binding + " of type " +
                    claimsRequest.getIssuerEndpoint().getType() + " found in channel " + claimsRequest.getIssuerChannel().getName());
        }

        EndpointDescriptor ed = mediator.resolveEndpoint(claimsRequest.getIssuerChannel(),
                claimsProcessingEndpoint);

        String serviceTicket = null;

        // Build a SAMLR2 Compatible Security token
        BinarySecurityTokenType binarySecurityToken = new BinarySecurityTokenType ();
        binarySecurityToken.setValue(serviceTicket);

        Claim claim = new ClaimImpl(AuthnCtxClass.KERBEROS_AUTHN_CTX.getValue(), binarySecurityToken);
        ClaimSet claims = new ClaimSetImpl();
        claims.addClaim(claim);

        SamlR2ClaimsResponse claimsResponse = new SamlR2ClaimsResponse (uuidGenerator.generateId(),
                channel, claimsRequest.getId(), claims, claimsRequest.getRelayState());

        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        out.setMessage(new MediationMessageImpl(claimsResponse.getId(),
                claimsResponse,
                "ClaimsResponse",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

        /*
        logger.info("Relaying SPNEGO token [" + content.getTokenValue() + "]");



        try {
            LoginContext loginContext = new LoginContext(spnegoMediator.getRealm(), new CallbackHandler() {
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            ((NameCallback) callbacks[i]).setName(spnegoMediator.getPrincipal());
                        } else {
                            throw new UnsupportedCallbackException(callbacks[i]);
                        }
                    }
                }
            });
            loginContext.login();
            Subject kerberosSubject = loginContext.getSubject();
            new SecurityContextEstablisher().acceptKerberosServiceTicket(
                    content.getTokenValue(),
                    kerberosSubject,
                    spnegoMediator.getPrincipal()
            );
        } catch (LoginException e) {
            throw new SecurityException("Authentication failed", e);
        }

        */

        return null;
    }



    class SecurityContextEstablisher implements PrivilegedAction {

        private byte[] kerberosServiceTicket;
        private String principal;
        private boolean loginOk = false;

        /**
         * Authenticates the kerberos service token .
         *
         * @param kerberosSubject the kerberos subject under which the authentication logic is ran
         */
        void acceptKerberosServiceTicket(byte[] kerberosServiceTicket, Subject kerberosSubject, String principal) {
            this.kerberosServiceTicket = kerberosServiceTicket;
            this.principal = principal;
            Subject.doAs(kerberosSubject, this);
        }

        /**
         * <p>
         * This is the only method in PrivilegedAction interface.
         * </p>
         * <p>
         * Created a GSS security context by verifying the kerberos service ticket supplied
         * by the client
         * </p>
         */
        public Object run() {
            //The context for secure communication with client.
            GSSContext serverGSSContext = null;

            try {
                GSSManager manager = GSSManager.getInstance();
                Oid spnego = new Oid("1.3.6.1.5.5.2");

                GSSName serverGSSName = manager.createName(principal, null);
                GSSCredential serverGSSCreds = manager.createCredential(serverGSSName,
                        GSSCredential.INDEFINITE_LIFETIME,
                        spnego,
                        GSSCredential.ACCEPT_ONLY);

                serverGSSContext = manager.createContext(serverGSSCreds);

                byte[] outputToken;
                outputToken = serverGSSContext.acceptSecContext(kerberosServiceTicket, 0,
                        kerberosServiceTicket.length);

                loginOk = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                HexDump.dump(outputToken, 0, baos, 0);
                logger.debug("Kerberos Service Ticket Successfully relayed (hex) : " + baos.toString());
                return outputToken;
            } catch (Exception e) {
                logger.debug("Error creating security context", e);
            }

            return null;
        }

    }

}
