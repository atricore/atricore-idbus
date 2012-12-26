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

package org.atricore.idbus.capabilities.josso.main.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.MessageContentsList;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationSoapBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.StatefulChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.StatefulProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JossoSoapBinding extends AbstractMediationSoapBinding {

    private static final Log logger = LogFactory.getLog(JossoSoapBinding.class);

    protected JossoSoapBinding(Channel channel) {
        super(JossoBinding.JOSSO_SOAP.getValue(), channel);
    }

    public MediationMessage createMessage(CamelMediationMessage message) {

        // Get HTTP Exchange from SAML Exchange
        CamelMediationExchange samlR2exchange = message.getExchange();
        Exchange exchange = samlR2exchange.getExchange();

        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        // Converting from CXF Message to SAMLR2 Message
        // Is this a CXF message?
        Message in = exchange.getIn();

        if (in.getBody() instanceof MessageContentsList) {

            MessageContentsList mclIn = (MessageContentsList) in.getBody() ;
            logger.debug("Using CXF Message Content : " + mclIn.get(0));

            StatefulProvider p = null;
            if (getChannel() instanceof StatefulChannel) {
                p = ((StatefulChannel) getChannel()).getProvider();
            }

            MediationState state = null;
            LocalState lState = null;
            if (p != null) {

                if (logger.isDebugEnabled())
                    logger.debug("Attempting to retrieve provider state using JOSSO Backchannel messasge information");

                // Try to recover provider state using alternative sso session id value.
                // This should be a JOSSO Back channel message request (we do not process responses yet)
                Object content = mclIn.get(0);
                
                if (logger.isTraceEnabled())
                    logger.trace("JOSSO Backchannel message " + content);

                if (content!= null) {

                    String ssoSessionId = null;
                    String assertionId = null;

                    try {
                        Method getSsoSessionId = content.getClass().getMethod("getSsoSessionId");
                        ssoSessionId = (String) getSsoSessionId.invoke(content);
                    } catch (NoSuchMethodException e) {
                        if (logger.isTraceEnabled())
                            logger.trace(e.getMessage());
                    } catch (Exception e) {
                        logger.error("Cannot get SSO Session ID from JOSSO backchannel message: " + e.getMessage(), e);
                    }

                    try {
                        Method getSessionId = content.getClass().getMethod("getSessionId");
                        ssoSessionId = (String) getSessionId.invoke(content);
                    } catch (NoSuchMethodException e) {
                        if (logger.isTraceEnabled())
                            logger.trace(e.getMessage());
                    } catch (Exception e) {
                        logger.error("Cannot get SSO Session ID from JOSSO backchannel message: " + e.getMessage(), e);
                    }


                    try {
                        Method getAssertionId = content.getClass().getMethod("getAssertionId");
                        assertionId = (String) getAssertionId.invoke(content);
                    } catch (NoSuchMethodException e) {
                        if (logger.isTraceEnabled())
                            logger.trace(e.getMessage());
                    } catch (Exception e) {
                        logger.error("Cannot get SSO Assertion ID from JOSSO backchannel message: " + e.getMessage(), e);
                    }

                    ProviderStateContext ctx = createProviderStateContext();
                    
                    // SSO Session ID is an alternative ID for provider state.
                    if (lState == null && ssoSessionId != null) {
                        if (logger.isDebugEnabled())
                            logger.debug("Attempting to restore provider state based on SSO Session ID " + ssoSessionId);

                        // Add retries just in case we're in a cluster (they are disabled in non HA setups)
                        int retryCount = getRetryCount();
                        if (retryCount > 0) {
                            lState = ctx.retrieve("ssoSessionId", ssoSessionId, retryCount, getRetryDelay());
                        } else {
                            lState = ctx.retrieve("ssoSessionId", ssoSessionId);
                        }
                    }

                    if (lState == null && assertionId != null) {
                        if (logger.isDebugEnabled())
                            logger.debug("Attempting to restore provider state based on Assertion ID " + assertionId);
                        // Add retries just in case we're in a cluster (they are disabled in non HA setups)
                        int retryCount = getRetryCount();
                        if (retryCount > 0) {
                            lState = ctx.retrieve("assertionId", assertionId, retryCount, getRetryDelay());
                        } else {
                            lState = ctx.retrieve("assertionId", assertionId);
                        }

                    }

                } 

            } else {
                logger.warn("No provider found for channel " + channel.getName());
            }

            if (lState == null) {
                // Create a new local state instance ?
                state = createMediationState(exchange);
                lState = state.getLocalState();
                
                if (logger.isDebugEnabled())
                    logger.debug("Creating new Local State instance " + lState.getId() + " for " + channel.getName());

            } else {

                if (logger.isDebugEnabled())
                    logger.debug("Using Local State instance " + lState.getId() + " for " + channel.getName());

                state = new MediationStateImpl(lState);
            }

            return new MediationMessageImpl(in.getMessageId(),
                        mclIn.get(0),  null, null, null, state);

        } else {
            throw new IllegalArgumentException("Unknown message type " + in.getBody());
        }

    }
}
