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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpExchange;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class JOSSO11IdPTest extends ContextTestSupport {
    private static Log log = LogFactory.getLog(JOSSO11IdPTest.class);

    public void testSessionSupport() throws Exception {
        HttpClient client = new HttpClient( );
        GetMethod get1 = new GetMethod( "http://localhost:8181/endpoint1" );
        log.debug( "get1 status = " + client.executeMethod(get1 ));
        GetMethod get2 = new GetMethod( "http://localhost:8181/endpoint1" );
        log.debug( "get2 status = " + client.executeMethod(get2 ));
        GetMethod get3 = new GetMethod( "http://localhost:8181/endpoint1/other" );
        log.debug( "get3 status = " + client.executeMethod(get3 ));
        GetMethod get4 = new GetMethod( "http://localhost:8181/endpoint1" );
        log.debug( "get4 status = " + client.executeMethod(get4 ));
    }

    @Override
    protected RouteBuilder createRouteBuilder () throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure () throws Exception {
                from( "jetty:http://localhost:8181/endpoint1?sessionSupport=true" )
                        .process( new Processor() {
                            public void process ( Exchange exchange ) throws Exception {
                                log.debug( "||||||||||||||||||||||||||||||||||||||||||||" );
                                log.debug( "             endpoint1" );
                                HttpExchange he = (HttpExchange) exchange;
                                HttpSession session = he.getRequest().getSession();
                                log.debug( "session id is [" + session.getId() + "]" );
                                Object attrib = session.getAttribute( "test" );
                                if(attrib != null){
                                    log.debug( "found attribute test=" + attrib );
                                    session.setAttribute( "test", ((Integer)attrib) + 1 );
                                } else
                                    session.setAttribute( "test", 1 );

                            }
                        } )
                        .to( "log:test" );


                from( "jetty:http://localhost:8181/endpoint1/other?sessionSupport=true" )
                        .process( new Processor() {
                            public void process ( Exchange exchange ) throws Exception {
                                log.debug( "||||||||||||||||||||||||||||||||||||||||||||" );
                                log.debug( "             endpoint1/other" );
                                HttpExchange he = (HttpExchange) exchange;
                                HttpSession session = he.getRequest().getSession();
                                log.debug( "session id is [" + session.getId() + "]" );
                                Object attrib = session.getAttribute( "test" );
                                if(attrib != null){
                                    log.debug( "found attribute test=" + attrib );
                                    session.setAttribute( "test", ((Integer)attrib) + 1 );
                                } else
                                    session.setAttribute( "test", 1 );

                            }
                        } )
                        .to( "log:test" );
            }
        };
    }
}