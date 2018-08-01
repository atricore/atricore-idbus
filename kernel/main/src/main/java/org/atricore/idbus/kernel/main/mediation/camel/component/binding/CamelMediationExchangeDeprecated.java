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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class CamelMediationExchangeDeprecated /* extends DefaultExchange */ {

    private static final Log logger = LogFactory.getLog(CamelMediationExchangeDeprecated.class);

    // Original exchange (soap, http, etc)
    private Exchange exchange;

    private CamelMediationEndpoint endpoint;

    public CamelMediationExchangeDeprecated(CamelContext camelContext) {
        //super(camelContext);
    }

    public CamelMediationExchangeDeprecated(CamelContext camelContext,
                          CamelMediationEndpoint endpoint,
                          ExchangePattern exchangePattern,
                          Exchange exchange) {

        //super(camelContext, exchangePattern);

        this.exchange = exchange;
        this.endpoint = endpoint;

    }

    public CamelMediationExchangeDeprecated(CamelMediationExchangeDeprecated exchange) {
        //super(exchange);
        this.exchange = exchange.getExchange();
        this.endpoint = exchange.getEndpoint();

    }

    //@Override
    public Exchange newInstance() {
        return null; // return new CamelMediationExchangeDeprecated(this);
    }

    //@Override
    protected org.apache.camel.Message createFaultMessage() {
        return new CamelMediationMessage();
    }

    //@Override
    protected Message createInMessage() {
        return new CamelMediationMessage();
    }

    //@Override
    protected Message createOutMessage() {
        return new CamelMediationMessage();
    }

    //@Override
    public void copyFrom(Exchange exchange) {
        //super.copyFrom(exchange);
    }

    public Exchange getExchange() {
        return exchange;
    }

    public CamelMediationEndpoint getEndpoint() {
        return endpoint;
    }
}
