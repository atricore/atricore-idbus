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

package org.atricore.idbus.capabilities.josso.test.support;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.component.http.HttpProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.claim.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: SampleClaimsMediator.java 1242 2009-06-05 02:15:49Z sgonzalez $
 */
public class SampleClaimsMediator extends AbstractCamelMediator {

    private static Log log = LogFactory.getLog(SampleClaimsMediator.class);

    private MessageQueueManager artifactQueueManager;

    protected RouteBuilder createClaimRoutes(final ClaimChannel claimChannel) throws Exception {

        return new RouteBuilder() {

            public void configure() {
                from("jetty:" + claimChannel.getLocation())
                    .process( new Processor() {
                                public void process(Exchange me) throws Exception {

                                    HttpExchange httpExchange = (HttpExchange) me;
                                    HttpServletRequest hreq = httpExchange.getRequest();

                                    String artifactContent = hreq.getParameter("Artifact");
                                    log.debug("Message received for Claim channel with Artifact [" +
                                              artifactContent + "]");
                                    Artifact artifact = ArtifactImpl.newInstance(artifactContent);

                                    if (artifactQueueManager != null) {
                                        ClaimsRequest claimsRequest;

                                        claimsRequest = (ClaimsRequest)artifactQueueManager.pullMessage(artifact);

                                        ClaimSet cs = new ClaimSetImpl();
                                        cs.addClaim(new ClaimImpl("username", "user1" ));
                                        cs.addClaim(new ClaimImpl("password", "user1pwd"));
                                        ClaimsResponse cr = new ClaimsResponseImpl(
                                                "FOO_ID",
                                                claimChannel,
                                                claimsRequest.getId(),
                                                cs
                                        );

                                        Artifact crspArtifact;
                                        crspArtifact = artifactQueueManager.pushMessage(cr);

                                        me.getOut(true).setHeader(HttpProducer.HTTP_RESPONSE_CODE, "302");
                                        me.getOut(true).setHeader(
                                                "Location", claimsRequest.getIssuerChannel().getLocation() +
                                                "?Artifact=" + crspArtifact.getContent()
                                        );

                                        log.debug("Providing Claim Response to Location [" +
                                                  claimsRequest.getIssuerChannel().getLocation() + "] " +
                                                  "with Artifact [" + crspArtifact.getContent() + "]");

                                    }

                              }
                    });
            }

        };

    }

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }
}