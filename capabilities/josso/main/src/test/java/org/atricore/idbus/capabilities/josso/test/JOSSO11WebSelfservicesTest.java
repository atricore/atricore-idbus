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

package org.atricore.idbus.capabilities.josso.test;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;

import java.net.URL;

/**
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: May 20, 2009
 */
public class JOSSO11WebSelfservicesTest extends ContextTestSupport {

    private ClassPathXmlApplicationContext applicationContext;
    private String lostPasswordEndpoint = "http://localhost:9191/JOSSO11/selfservices/lostpassword";

    protected void setUp () throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/josso/test/josso-test-selfservices.xml"}
        );

        super.setUp();
    }

    protected JndiRegistry createRegistry () throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind( "applicationContext", applicationContext );
        return jndi;
    }

    public void tearDown () throws Exception {
        if ( System.getProperty( "block.test" ) != null && Boolean.parseBoolean( System.getProperty( "block.test" ) ) ) {
            log.info( "BLOCKING TEST ..." );
            synchronized (this) {
                try {
                    wait();
                }
                catch ( InterruptedException e ) { /**/}
            }
        }

        super.tearDown();
        if ( applicationContext != null ) {
            applicationContext.close();
        }
    }

    @Override
    protected RouteBuilder createRouteBuilder () throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure () throws Exception {
                from( "jetty:" + lostPasswordEndpoint + "?sessionSupport=true" )
                        .to( "josso-binding:LostPasswordRecovery?channelRef=josso11-sp-binding" );
            }
        };
    }

    public void testSelfservices() throws Exception {
        HttpClient client = new HttpClient();
        initLostPasswordProcess( client );
        followLostPasswordProcess( client );
    }

    private void initLostPasswordProcess (HttpClient client) throws Exception {
        log.debug( "******************************************************************************" );
        log.debug( "initLostPasswordProcess" );
        log.debug( "******************************************************************************" );

        GetMethod get = new GetMethod( lostPasswordEndpoint + "?josso_cmd=lostPwd" );
        get.setFollowRedirects( false );
        int status = client.executeMethod( get );
        get.getResponseBodyAsString();
        assert status == HttpStatus.SC_MOVED_TEMPORARILY : "status code spected " + HttpStatus.SC_MOVED_TEMPORARILY + " found [" + status + "]";
        Header h = get.getResponseHeader( "Location" );
        assert h != null : "No Location found";
        URL url = new URL( h.getValue() );
        assert url.getQuery() != null && url.getQuery().contains( "artifactId" ) : "No artifactId found";
        
    }

    private void followLostPasswordProcess (HttpClient client) throws Exception {
        log.debug( "******************************************************************************" );
        log.debug( "followLostPasswordProcess" );
        log.debug( "******************************************************************************" );

        /*
        TODO: email-password-distributor and spring-mailsender should be configured for this specific context
         
        PostMethod post = new PostMethod( lostPasswordEndpoint );
        NameValuePair challenge = new NameValuePair( "email", "ajadzinsky@atricore.org");
        post.setRequestBody( new NameValuePair[]{ challenge } );
        post.setFollowRedirects( false );
        int status = client.executeMethod( post );
        assert status == HttpStatus.SC_MOVED_TEMPORARILY : "status code spected " + HttpStatus.SC_MOVED_TEMPORARILY + " found [" + status + "]";
        Header h = post.getResponseHeader( "Location" );
        assert h != null : "No Location found";
        URL url = new URL( h.getFormat() );
        assert url.getQuery() != null && url.getQuery().contains( "artifactId" ) : "No artifactId found";
        */
    }
}