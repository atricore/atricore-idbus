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

import org.apache.camel.Consumer;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.http.*;
import org.apache.camel.http.common.HttpBinding;
import org.apache.camel.http.common.HttpConsumer;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IDBusHttpEndpoint extends HttpEndpoint {

    private IDBusHttpComponent component;
    private IDBusHttpBinding httpBinding;
    private boolean sessionSupport;
    private String handlerNames;


    public IDBusHttpEndpoint(IDBusHttpComponent component, String uri, URI httpURL, HttpClientParams clientParams,
                             HttpConnectionManager httpConnectionManager, HttpClientConfigurer clientConfigurer) throws URISyntaxException {
        super(uri, component, httpURL, clientParams, httpConnectionManager, clientConfigurer);
        this.component = component;
    }

    @Override
    public Producer createProducer() throws Exception {
        return super.createProducer();
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new HttpConsumer(this, processor);
    }

    @Override
    public PollingConsumer createPollingConsumer() throws Exception {
        return new HttpPollingConsumer(this);
    }

    @Override
    public IDBusHttpComponent getComponent() {
        return component;
    }

    @Override
    public HttpBinding getBinding() {
        if (httpBinding == null) {
            httpBinding = new IDBusHttpBinding(getHeaderFilterStrategy());
            super.setBinding(httpBinding);
        }
        return httpBinding;
    }

    public void setSessionSupport(boolean support) {
        sessionSupport = support;
    }

    public boolean isSessionSupport() {
        return sessionSupport;
    }

    public String getHandlers() {
        return handlerNames;
    }

    public void setHandlers(String handlerNames) {
        this.handlerNames = handlerNames;
    }

}
