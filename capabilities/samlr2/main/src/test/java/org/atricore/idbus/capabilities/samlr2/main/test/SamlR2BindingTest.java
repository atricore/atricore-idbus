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

package org.atricore.idbus.capabilities.samlr2.main.test;

import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.wsdl.SAMLRequestPortType;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.transport.CamelTransportFactory;
import org.apache.camel.component.http.CamelServlet;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.CamelIdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2BindingTest extends SamlR2TestSupport {

    private static final Log logger = LogFactory.getLog(SamlR2BindingTest.class);

    protected Server server;
    protected ContextHandlerCollection collection;

    protected ClassPathXmlApplicationContext applicationContext;
    protected ConnectorRef connectorRef;

    private CamelContext context;

    @Before
    public void setup() throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                "/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-binding-test.xml"
        );

        // ---------------------------------------------------------
        // Setup Camel context
        // ---------------------------------------------------------
        Map beans = applicationContext.getBeansOfType(CamelIdentityMediationUnitContainer.class);
        CamelIdentityMediationUnitContainer engine = (CamelIdentityMediationUnitContainer) beans.values().iterator().next();
        context = engine.getContext();

        // ---------------------------------------------------------
        // Setup JETTY Server
        // ---------------------------------------------------------

        server = (Server) applicationContext.getBean("jetty-server");
        createServlet(applicationContext, server, "/IDBUS");

        // This will deploy the IDBusServlet

        // ---------------------------------------------------------
        // Setup CXF, adding Camel transport
        // ---------------------------------------------------------
        BusFactory bf = BusFactory.newInstance();
        Bus bus = bf.createBus();

        CamelTransportFactory camelTransportFactory = new CamelTransportFactory();
        camelTransportFactory.setTransportIds(new ArrayList<String>());
        camelTransportFactory.getTransportIds().add(CamelTransportFactory.TRANSPORT_ID);
        camelTransportFactory.setCamelContext(context);
        camelTransportFactory.setBus(bus);

        // register the conduit initiator
        ConduitInitiatorManager cim = bus.getExtension(ConduitInitiatorManager.class);
        cim.registerConduitInitiator(CamelTransportFactory.TRANSPORT_ID, camelTransportFactory);

        // register the destination factory
        DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        dfm.registerDestinationFactory(CamelTransportFactory.TRANSPORT_ID, camelTransportFactory);

        // set or bus as the default bus for cxf
        BusFactory.setDefaultBus(bus);

    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testSamlR2SoapBinding() throws Exception {

        // ---------------------------------------------------------
        // Create Camel routes
        // ---------------------------------------------------------

        RouteBuilder testRoutes = createSamlR2SoapRoutes();
        context.addRoutes(testRoutes);

        // 'BOOTSTRAP' consumers!
        //Endpoint bindingEndpoint = context.getEndpoint("idbus-bind:urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        //Consumer consumer = bindingEndpoint.createConsumer(new ConsumerProcessor());
        //consumer.start();

        // Client side setup!
        final QName SERVICE_NAME
            = new QName("urn:oasis:names:tc:SAML:2.0:wsdl", "SAMLService");
        final QName PORT_NAME
            = new QName("urn:oasis:names:tc:SAML:2.0:wsdl", "soap");

        // ---------------------------------------------------------
        // Setup CXF Client
        // ---------------------------------------------------------
        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING,
                            "http://localhost:8181/IDBUS/IDP-1/NMI/SOAP");

        SAMLRequestPortType port = service.getPort(PORT_NAME, SAMLRequestPortType.class);

        // ---------------------------------------------------------
        // Execute an operation!
        // ---------------------------------------------------------
        AuthnRequestType req = new AuthnRequestType();
        req.setID("MY-ID-1");

        ResponseType res = port.samlAuthnRequest(req);
        logger.debug("Received status: " + res.getID());

        String expected = req.getID() + "-ECHO";
        String received = res.getID();

        assert received.equals(expected): "IDs do not match, expected " + expected + " received " + received;

    }

    @Test
    public void testSamlR2HttpPostBinding() throws Exception {
        // ---------------------------------------------------------
        // Create Camel routes
        // ---------------------------------------------------------

        RouteBuilder testRoutes = createSamlR2HttpRoutes();
        context.addRoutes(testRoutes);

        // 'BOOTSTRAP' consumers!
        //Endpoint bindingEndpoint = context.getEndpoint("idbus-bind:urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        //Consumer consumer = bindingEndpoint.createConsumer(new ConsumerProcessor());
        //consumer.start();

        AuthnRequestType req = new AuthnRequestType();
        req.setID("MY-ID-1");

        HttpClient client = new HttpClient();

        PostMethod post = new PostMethod ("http://localhost:8181/IDBUS/IDP-1/NMI/POST");
        post.addParameter("SAMLRequest", XmlUtils.marshalSamlR2Request(req, true));
        post.addParameter("RelayState", "23058208054820&8");

        int status = client.executeMethod(post);

        logger.info("HTTP-Status:" + status + " " + post.getResponseBodyAsString());

        client.executeMethod(post);

    }


    protected class ConsumerProcessor implements Processor {
        public void process(Exchange exchange) {
            try {

                //
                logger.debug("We have to send the exchange to CAMEL again!!! ");

            } catch (Throwable ex) {
                logger.warn("Failed to process incoming message : ", ex);
            }
        }
    }



    protected RouteBuilder createSamlR2SoapRoutes() {
        return new RouteBuilder() {

            public void configure() throws Exception {

                // ---------------------------------------------------------
                // Camel route to a direct endpoint
                // ---------------------------------------------------------

                // HTTP Web Server -> SOAP CXF -> SamlR2 SamlR2Binding -> SamlR2 Business

                // Listen using JOSSO HTTP Component, protocol, host and port are ignored when working in hosted mode!
                from("idbus-http:http://localhost:8181/IDBUS/IDP-1/NMI/SOAP").
                        process(new DumpProcessor("FROM HTTP TO CXF")).
                        to("direct:idp-1-nmi-soap-cxf");

                // Receive HTTP requests and handle them as SOAP messages.
                from("cxf:camel://direct:idp-1-nmi-soap-cxf?" +
                        "serviceClass=org.atricore.idbus.capabilities.samlr2.main.binding.services.SamlR2ServiceImpl" +
                        "&serviceName={urn:oasis:names:tc:SAML:2.0:wsdl}SAMLService" +
                        "&portName={urn:oasis:names:tc:SAML:2.0:wsdl}soap" +
                        "&dataFormat=POJO").
                        process(new DumpProcessor("FROM CXF TO SAMLR2B")).
                        to("direct:idp-1-nm1-soap-samlr2");

                // Receive SOAP Messages and handle them as SAML Messages
                from("idbus-bind:camel://direct:idp-1-nm1-soap-samlr2" +
                        "?binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP").
                        process(new DumpProcessor("FROM SAMLR2B TO SAMLR2")).
                        process(new SamlR2EchoProcessor());

            }

        };
    }

    protected RouteBuilder createSamlR2HttpRoutes() {
        return new RouteBuilder() {

            public void configure() throws Exception {

                // ---------------------------------------------------------
                // Camel route to a direct endpoint
                // ---------------------------------------------------------

                // HTTP Web Server -> SOAP CXF -> SamlR2 SamlR2Binding -> SamlR2 Business

                // Listen using JOSSO HTTP Component, protocol, host and port are ignored when working in hosted mode!
                from("idbus-http:http://localhost:8181/IDBUS/IDP-1/NMI/POST").
                        process(new DumpProcessor("FROM HTTP TO SAMLR2B")).
                        to("direct:idp-1-nmi-http-samlr2");

                // Receive SOAP Messages and handle them as SAML Messages
                from("idbus-bind:camel://direct:idp-1-nmi-http-samlr2" +
                        "?binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").
                        process(new DumpProcessor("FROM SAMLR2B TO SAMLR2")).
                        process(new SamlR2EchoProcessor());

            }

        };
    }


    class DumpProcessor extends DelegateProcessor {

        private String prefix;

        DumpProcessor(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void process(Exchange ex) throws Exception {

            logger.debug(prefix + " Exchange:" + ex.getClass().getName());

            if (ex instanceof HttpExchange) {
                HttpExchange httpEx = (HttpExchange) ex;
            } else if (ex instanceof CamelMediationExchange){
                CamelMediationExchange mEx = (CamelMediationExchange) ex;
                CamelMediationMessage in = (CamelMediationMessage) mEx.getIn();

                logger.debug(in);

            }

            processNext(ex);
        }
    }

    class SamlR2EchoProcessor extends DelegateProcessor {
        @Override
        public void process(Exchange ex) throws Exception {
            logger.debug("SamlR2 Echo Processor Exchange:" + ex);

            assert ex != null : "Exchange is null";
            assert ex instanceof CamelMediationExchange : "Unexpected Exchange class " + ex.getClass().getName();
            
            CamelMediationMessage samlIn = (CamelMediationMessage) ex.getIn();
            CamelMediationMessage samlOut = (CamelMediationMessage) ex.getOut();

            assert samlIn != null : "In Message is null";
            assert samlIn.getBody() != null : "In Message does not have a body";
            assert samlIn.getBody() instanceof MediationMessage : "Unexpected body type " + samlIn.getBody().getClass().getName();
            assert samlOut != null : "Out Message is null";

            MediationMessage<AuthnRequestType> msgIn = (MediationMessage<AuthnRequestType>) ex.getIn().getBody();
            assert msgIn.getState() != null : "No Mediation state received!";

            AuthnRequestType req = msgIn.getContent();

            logger.debug("SamlR2 Echo Processor " + req.getID());

            ResponseType res = new ResponseType();
            res.setID(req.getID() + "-ECHO");



            EndpointDescriptor ed = new EndpointDescriptorImpl("HTTP-POST-BINDING-TEST",
                    "Resposne",
                    null,
                    "http://localhost:8181/IDBUS/TBD", null);

            MediationMessage out = new MediationMessageImpl<ResponseType>("1", res, null, null, ed, msgIn.getState());

            samlOut.setBody(out);

            //ex.setOut(msgOut);

        }

    }


    class ConnectorRef {
        Connector connector;
        CamelServlet servlet;
        int refCount;

        public ConnectorRef(Connector connector, CamelServlet servlet) {
            this.connector = connector;
            this.servlet = servlet;
            increment();
        }

        public int increment() {
            return ++refCount;
        }

        public int decrement() {
            return --refCount;
        }
    }


}
