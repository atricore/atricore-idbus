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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.springframework.context.ApplicationContext;
import org.w3._1999.xhtml.Div;
import org.w3._1999.xhtml.Form;
import org.w3._1999.xhtml.Input;

import java.io.IOException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Test.java 1277 2009-06-13 23:08:21Z gbrigand $
 */
public class SamlR2SSOTest extends SamlR2TestSupport {

    private static final Log logger = LogFactory.getLog(SamlR2Test.class);

    private ApplicationContext applicationContext;

    private HttpClient client;

    private Server server;

    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-sso-test.xml"}
        );

        server = (Server) applicationContext.getBean("jetty-server");
        createServlet(applicationContext, server, "/IDBUS");

        client = new HttpClient();
    }

    @After
    public void tearDown() throws Exception {
        String stTimeout = System.getProperty("org.atricore.josso2.test.wait", "-1");
        long timeout = Long.parseLong(stTimeout);
        if (timeout >= 0 ) {
            synchronized (this) {
                logger.info("Waiting " + (timeout > 0 ? timeout + " millis"  : "forever"));
                try { wait(timeout); } catch (InterruptedException e) { /**/}
            }
        }

        if (server != null)
            server.stop();

        if (applicationContext != null)
            ((ClassPathXmlApplicationContext)applicationContext).close();

    }

    @Test
    public void testSPIinitiatedSSO() throws Exception{

        // Access SP SSO Endpoint
        String response = this.accessSPInitiantedSSOEndpoint();
        logger.debug("Step 1 : Access SP Initiated SSO Endpoint. Response:\n" + response);

        // SP Sends POST AuthnReq to IDP
        Form form = getForm(unmarshallHtml(response));
        assert form != null : "No Form received";

        // POST AuthnReq to IDP SSO Endpoint, IDP will send us to Claims channel.
        String claimsLocation = this.accessIDPSingleSignOnEndpoint(form);
        logger.debug("Step 2 : Access IDP SSO Endpoint. Response:\n" + claimsLocation);

        // Get clamis form location
        String claimsFormLocation = this.accessClaimsEndpoint(claimsLocation);
        logger.debug("Step 3 : Access Claims Endpoint. Response:\n" + claimsFormLocation);

        //String claimsForm = this.accessClaimsFormLocation( claimsFormLocation );
        //logger.debug("Step 4 : Access Claims Form Endpoint. Response:\n" + claimsForm);

        // Post wrong credentials to claims channel
        String claimsPostLocation = "http://localhost:8181/IDBUS/CC-1/IDBUS/PWD/POST";
        String idpLocation = this.postWrongCredentials(claimsPostLocation);
        logger.debug("Step 5 : Post Wrong Credentiansl to Claims Endpoint. Response:\n" + claimsLocation);

        // Process wrong credentials with IDP, will send us back to Claims channel
        claimsLocation = this.accessIDPSingleSingonEndpoint(idpLocation);
        logger.debug("Step 6 : Access IDP Single SignOn Endpoint. Response:\n" + response);

        // Access claims endpoint again.
        claimsFormLocation = this.accessClaimsEndpoint(claimsLocation);
        logger.debug("Step 7 : Access Claims Endpoint. Response:\n" + claimsFormLocation);

        //claimsForm = this.accessClaimsFormLocation( claimsFormLocation );
        //logger.debug("Step 8 : Access Claims Form Endpoint. Response:\n" + claimsForm);

        // Post right credentials
        idpLocation = this.postCredentials(claimsPostLocation);
        logger.debug("Step 9 : Post Credentiansl to Claims Endpoint. Response:\n" + idpLocation);

        // Access IDP SSO Endpoint with right credentials
        response = this.accessIDPSingleSingonEndpointForPost(idpLocation);
        logger.debug("Step 10 : Access IDP Single SignOn Endpoint. Response:\n" + response);

        // POST SamlR2Response to SP

        // SP Sends POST AuthnReq to IDP
        /*
        form = getForm(response);
        assert form != null : "No Form received";
        this.accessSPAssertionConsumer(form);
        logger.debug("Step 9 : Access SP Assertion Consumer Endpoint. Response:\n" + response);
        */

        response = this.accessSPInitiantedSSOEndpoint();
        logger.debug("Step 10 : Access SP Initiated SSO Endpoint. Response:\n" + response);

        // SP Sends POST AuthnReq to IDP
        form = getForm(unmarshallHtml(response));
        assert form != null : "No Form received";

        // POST AuthnReq to IDP SSO Endpoint, IDP will send us to Claims channel.
        response = this.accessIDPSingleSignOnEndpointAuthenticated(form);
        logger.debug("Step 11 : Access IDP SSO Endpoint. Response:\n" + response);


        // SP Sends POST AuthnReq to IDP
        /*
        form = getForm(response);
        assert form != null : "No Form received";
        this.accessSPAssertionConsumer(form);
        logger.debug("Step 12 : Access SP Assertion Consumer Endpoint. Response:\n" + response);
        */

    }

    protected String accessSPInitiantedSSOEndpoint() throws Exception{

        // Access SP, asking for identity.
        String url = "http://localhost:8181/IDBUS/SP-1/IDBUS/SSO/REDIR";

        logger.debug( "******************************************************************************" );
        logger.debug( "SP GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        return get.getResponseBodyAsString();
    }

    protected String accessIDPSingleSignOnEndpoint(Form form) throws Exception {
        // Access SP, asking for identity.
        String url = form.getAction();

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);

        Div div = (Div) form.getPOrH1OrH2().get(0);

        for (Object o :div.getContent()) {
            Input input = (Input) o;
            logger.debug("Adding POST param " + input.getName() + "=" + input.getValue());
            post.addParameter(input.getName(), input.getValue());
        }


        int status = client.executeMethod( post );
        assert status == 302 : "status code spected 302 found [" + status + "]";

        return post.getResponseHeader("Location").getValue();
    }

    protected String accessIDPSingleSignOnEndpointAuthenticated(Form form) throws Exception {
        // Access SP, asking for identity.
        String url = form.getAction();

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);

        Div div = (Div) form.getPOrH1OrH2().get(0);

        for (Object o :div.getContent()) {
            Input input = (Input) o;
            logger.debug("Adding POST param " + input.getName() + "=" + input.getValue());
            post.addParameter(input.getName(), input.getValue());
        }


        int status = client.executeMethod( post );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        return post.getResponseBodyAsString();
    }


    protected String accessClaimsEndpoint(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "CLAIMS GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(false);

        int status = client.executeMethod( get );
        assert status == 302 : "status code spected 302 found [" + status + "]";

        get.releaseConnection();
        return get.getResponseHeader("Location").getValue();

    }

    protected String accessClaimsFormLocation(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "CLAIMS FORM GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(false);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";
        get.releaseConnection();
        return get.getResponseBodyAsString();
    }

    protected String postWrongCredentials(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "CLAIMS POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);

        // post.addParameter("josso_cmd", "login"); // Change this!
        post.addParameter("josso_username", "user1");
        post.addParameter("josso_password", "user1pwd2");

        int status = client.executeMethod( post );
        assert status == 302 : "status code spected 302 found [" + status + "]";

        return post.getResponseHeader("Location").getValue();

    }

    protected String postCredentials(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "CLAIMS POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);

        // post.addParameter("josso_cmd", "login"); // Change this!
        post.addParameter("josso_username", "user1");
        post.addParameter("josso_password", "user1pwd");

        int status = client.executeMethod( post );
        assert status == 302 : "status code spected 302 found [" + status + "]";

        return post.getResponseHeader("Location").getValue();

    }


    protected String accessIDPSingleSingonEndpoint(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(false);

        int status = client.executeMethod( get );
        assert status == 302: "status code spected 302 found [" + status + "]";

        return get.getResponseHeader("Location").getValue();

    }

    protected String accessIDPSingleSingonEndpointForPost(String location) throws IOException {
        // Access SP, asking for identity.
        String url = location;

        logger.debug( "******************************************************************************" );
        logger.debug( "IDP GET : " + url);
        logger.debug( "******************************************************************************" );

        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(false);

        int status = client.executeMethod( get );
        assert status == 200 : "status code spected 200 found [" + status + "]";

        return get.getResponseBodyAsString();

    }


    protected String accessSPAssertionConsumer(Form form) throws IOException {
        // Access SP, asking for identity.
        String url = form.getAction();

        logger.debug( "******************************************************************************" );
        logger.debug( "SP POST : " + url);
        logger.debug( "******************************************************************************" );

        PostMethod post = new PostMethod(url);
        post.setFollowRedirects(false);

        Div div = (Div) form.getPOrH1OrH2().get(0);

        for (Object o :div.getContent()) {
            Input input = (Input) o;
            logger.debug("Adding POST param " + input.getName() + "=" + input.getValue());
            post.addParameter(input.getName(), input.getValue());
        }


        int status = client.executeMethod( post );
        assert status == 302 : "status code spected 302 found [" + status + "]";

        return post.getResponseHeader("Location").getValue();
    }




}