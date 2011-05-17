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
import org.apache.commons.io.HexDump;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spnego.*;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivilegedAction;


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
            logger.info("Received SPNEGO Message");

        if (content instanceof UnauthenticatedRequest) {
            spnegoResponse = doProcessUnauthenticatedRequest(exchange, (UnauthenticatedRequest) content);
        } else if (content instanceof AuthenticatedRequest) {
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


    protected SpnegoMessage doProcessUnauthenticatedRequest(CamelMediationExchange exchange, UnauthenticatedRequest content) {
        logger.info("Requesting Token to SPNEGO initiator");
        return new RequestToken();
    }

    protected SpnegoMessage doProcessAuthenticatedRequest(CamelMediationExchange exchange, AuthenticatedRequest content) {
        final SpnegoMediator spnegoMediator = (SpnegoMediator) channel.getIdentityMediator();

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
