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

package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.camel.Endpoint;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.http.common.HttpConsumer;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.URISupport;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;

import java.net.URI;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IDBusHttpComponent extends HttpComponent {

    private static final Log logger = LogFactory.getLog(IDBusHttpComponent.class);

    public IDBusHttpComponent() {
        super();
        this.setHeaderFilterStrategy(new IDBusHttpHeaderFilterStrategy());
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        uri = uri.startsWith("idbus-http:") ? remaining : uri;

        HttpClientParams params = new HttpClientParams();
        IntrospectionSupport.setProperties(params, parameters, "httpClient.");

        // TODO : 25_UPD configureParameters(parameters);

        // restructure uri to be based on the parameters left as we dont want to include the Camel internal options
        URI httpUri = URISupport.createRemainingURI(new URI(uri), parameters);
        uri = httpUri.toString();

        IDBusHttpEndpoint endpoint = new IDBusHttpEndpoint(this, uri, httpUri, params, getHttpConnectionManager(), httpClientConfigurer);
        if (httpBinding != null) {
            endpoint.setBinding(httpBinding);
        }
        setProperties(endpoint, parameters);
        return endpoint;
    }

    /**
     * Connects the URL specified on the endpoint to the specified processor.
     *
     * @throws Exception
     */
    @Override
    public void connect(HttpConsumer consumer) throws Exception {

        String consumerKey = "idbus:" + consumer.getPath();

        // Always publis context for /IDBUS context ...
        String consumerAltKey = null;

        // If we are not using the default context, we bind to it anyway ...
        if (!consumer.getPath().startsWith("/IDBUS/")) {
            consumerAltKey = consumer.getPath().substring(1);
            int pos = consumerAltKey.indexOf('/');
            consumerAltKey = pos > 0 ? "idbus:/IDBUS" + consumerAltKey.substring(pos) : null;
        }

        JndiRegistry jReg = (JndiRegistry) this.getCamelContext().getRegistry();

        logger.debug("Binding HTTP Consumer " + consumer.getPath() + " to " + consumerKey);

        try {

            // Try to bind the consumer
            jReg.bind(consumerKey, consumer);
            if (consumerAltKey != null && !consumerAltKey.equals(consumerKey)) {
                logger.debug("Binding HTTP Consumer " + consumer.getPath() + " to alt " + consumerAltKey);
                jReg.bind(consumerAltKey, consumer);
            }

        } catch (Exception e) {
            logger.error("Cannot bint HTTP Consumer " + consumer.getPath() + " to " + consumerKey, e);
            throw new IdentityMediationException(e);
        }

    }

    /**
     * Disconnects the URL specified on the endpoint from the specified
     * processor.
     */
    @Override
    public void disconnect(HttpConsumer consumer) throws Exception {
        super.disconnect(consumer);

    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

}
