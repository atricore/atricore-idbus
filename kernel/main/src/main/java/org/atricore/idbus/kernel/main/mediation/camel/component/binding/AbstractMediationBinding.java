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

package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.LocalProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractMediationBinding implements CamelMediationBinding {

    private static final Log logger = LogFactory.getLog(AbstractMediationBinding.class);

    private String binding;
    protected Channel channel;
    protected ClassLoader stateManagerClassloader;

    protected AbstractMediationBinding(String binding, Channel channel) {
        this.binding = binding;
        this.channel = channel;
    }

    public String getBinding() {
        return binding;
    }

    public Channel getChannel() {
        return channel;
    }

    public ClassLoader getStateManagerClassLoader() {
        return stateManagerClassloader;
    }

    public void setStateManagerClassLoader(ClassLoader stateManagerClassloader) {
        this.stateManagerClassloader = stateManagerClassloader;
    }

    protected ProviderStateContext createProviderStateContext() {

        return new ProviderStateContext(getProvider(),
                stateManagerClassloader != null ? stateManagerClassloader : getClass().getClassLoader());
    }

    protected LocalProvider getProvider() {

        LocalProvider p = null;
        if (channel instanceof FederationChannel) {
            FederationChannel fc = (FederationChannel) channel;
            p = fc.getProvider();

        } else if (channel instanceof BindingChannel) {
            BindingChannel bc = (BindingChannel) channel;
            p = bc.getProvider();

        } else if (channel instanceof ClaimChannel) {
            ClaimChannel cc = (ClaimChannel) channel;
            p = cc.getProvider();
        }

        return p;
    }


    protected void copyBackState(MediationState state, Exchange exchange) {
        if (logger.isDebugEnabled())
            logger.debug("Copying SOAP Message");

        // Local Variables are supported by Provider State Manager

        if (state == null) {
            if (logger.isTraceEnabled())
                logger.trace("No mediation state found, returning");
            return;
        }

        LocalState lState = state.getLocalState();
        if (lState == null) {
            if (logger.isTraceEnabled())
                logger.trace("No local state found, returning");
            return;
        }

        LocalProvider p = null;
        if (channel instanceof FederationChannel) {
            FederationChannel fc = (FederationChannel) channel;
            p = fc.getProvider();
        } else if (channel instanceof BindingChannel) {
            BindingChannel bc = (BindingChannel) channel;
            p = bc.getProvider();
        } else if (channel instanceof ClaimChannel) {
            ClaimChannel cc = (ClaimChannel) channel;
            p = cc.getProvider();
        }

        if (p != null) {

            String localStateVarName = p.getName().toUpperCase() + "_STATE";

            if (logger.isDebugEnabled())
                logger.debug("Using Provider State manager to store local state ("+p.getName()+"). Channel ("+channel.getName()+")");

            LocalState pState = state.getLocalState();
            String stateId = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie." + localStateVarName);
            if (stateId == null || !stateId.equals(pState.getId())) {

                if (logger.isDebugEnabled())
                    logger.debug("Updating state id " + stateId + " with new id " + pState.getId() + " ("+p.getName()+"). Channel ("+channel.getName()+")");

                state.setRemoteVariable(localStateVarName, pState.getId());
            }


            if (lState.getAlternativeIdNames().size() > 0) {
                if (logger.isTraceEnabled())
                    logger.trace("Storing binding channel local state : " + lState.getId());
                // Store this state
                ProviderStateContext ctx = createProviderStateContext();
                ctx.store(pState);
            } else {
                for (String key : lState.getKeys()) {
                    logger.warn("Local State does not have alternative keys! Local variable will not be updated nor added : " + key);
                }
            }


        } else {

            for (String key : lState.getKeys()) {
                logger.warn("No local state manager support available! Local variable will be lost "  + key);
            }
        }

    }


    protected MediationState createMediationState(Exchange exchange) {
        if (logger.isDebugEnabled())
            logger.debug("Creating Mediation State from Exchange " + exchange.getExchangeId());

        LocalProvider p = null;
        if (channel instanceof FederationChannel) {
            FederationChannel fc = (FederationChannel) channel;
            p = fc.getProvider();

        } else if (channel instanceof BindingChannel) {
            BindingChannel bc = (BindingChannel) channel;
            p = bc.getProvider();

        } else if (channel instanceof ClaimChannel) {
            ClaimChannel cc = (ClaimChannel) channel;
            p = cc.getProvider();

        }


        MediationStateImpl state = null;
        if (p != null) {

            if (logger.isDebugEnabled())
                logger.debug("Using Provider State manager to store local state ("+p.getName()+"). Channel ("+channel.getName()+")");

            //String localStateVarName = p.getName().toUpperCase() + "_STATE";
            ProviderStateContext ctx = createProviderStateContext();
            LocalState lState = ctx.createState();
            String localStateId = lState.getId();
            state = new MediationStateImpl(lState);

            if (logger.isDebugEnabled())
                logger.debug("Created new local state for provider " + p.getName() + ". Channel ("+channel.getName()+") " + localStateId);


        } else {

            if (logger.isDebugEnabled())
                logger.debug("Using Transient Local state. Channel ("+channel.getName()+")");

            LocalState lState = new TransientLocalState(exchange.getExchangeId());
            state = new MediationStateImpl(lState);
        }

        return state;

    }

    public Object sendMessage(MediationMessage message) throws IdentityMediationException {
        throw new UnsupportedOperationException("Binding does not support sending new messages");
    }
}
