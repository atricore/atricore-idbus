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

package org.atricore.idbus.capabilities.openid.main.common.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.common.OpenIDException;
import org.atricore.idbus.capabilities.openid.main.support.OpenIDConstants;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.planning.IdentityPlan;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchangeImpl;

import java.util.Collection;

/**
 * TODO: GB/Refactor - Factor out to kernel
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id: OpenIDProducer.java 1359 2009-07-19 16:57:57Z gbrigand $
 */
public abstract class OpenIDProducer extends AbstractCamelProducer<CamelMediationExchange>
        implements OpenIDConstants {

    private static final Log logger = LogFactory.getLog(OpenIDProducer.class);

    protected OpenIDProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // DO Nothing!
    }

    protected FederatedLocalProvider getProvider() {
        if (channel instanceof FederationChannel) {
            return ((FederationChannel) channel).getProvider();
        } else if (channel instanceof BindingChannel) {
            return ((BindingChannel) channel).getProvider();
        } else if (channel instanceof ClaimChannel) {
            return ((ClaimChannel) channel).getProvider();
        } else {
            throw new IllegalStateException("Configured channel does not support Federated Provider : " + channel);
        }
    }

}
