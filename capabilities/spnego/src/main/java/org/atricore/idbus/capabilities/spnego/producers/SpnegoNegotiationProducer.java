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
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpnegoNegotiationProducer<E extends org.apache.camel.Exchange> extends DefaultProducer<E> {

    private static final Log logger = LogFactory.getLog( SpnegoNegotiationProducer.class );

    public SpnegoNegotiationProducer(Endpoint endpoint) {
        super( endpoint );
        assert endpoint != null : "Endpoint MUST be specified when creating producers!";
    }

    public void process ( final Exchange e) throws Exception {
        HttpExchange exchange = (HttpExchange) e;

        logger.debug("received http exchange to initiated SPNEGO negotiation");
    }

}
