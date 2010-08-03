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

package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationState;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationMessageImpl<E> implements MediationMessage<E>, java.io.Serializable {

    private static final Log logger = LogFactory.getLog(MediationMessageImpl.class);

    private String id;

    private E content;

    private String rawContent;

    private String contentType;

    private String relayState;

    private transient MediationState state;

    private IdentityMediationFault fault;

    private String faultDetails;

    // Only usefull for outgoing messages
    private EndpointDescriptor destination;

    public MediationMessageImpl(String id,
                                String faultDetails,
                                IdentityMediationFault fault) {
        this.id = id;
        this.fault = fault;
        this.faultDetails = faultDetails;
    }

    public MediationMessageImpl(String id,
                                E content,
                                String contentType,
                                String relayState,
                                EndpointDescriptor destination,
                                MediationState state) {

        this.id = id;
        this.content = content;
        this.contentType = contentType;
        this.relayState = relayState;
        this.destination = destination;
        this.state = state;
    }

    public MediationMessageImpl(String id,
                                E content,
                                String rawContent,
                                String contentType,
                                String relayState,
                                EndpointDescriptor destination,
                                MediationState state) {

        this.id = id;
        this.content = content;
        this.rawContent = rawContent;
        this.contentType = contentType;
        this.relayState = relayState;
        this.destination = destination;
        this.state = state;
    }



    public MediationMessageImpl() {

    }

    public String getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public E getContent() {
        return content;
    }

    public void setContent(E newContent) {
        this.content = newContent;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public EndpointDescriptor getDestination() {
        return destination;
    }

    public void setDestination(EndpointDescriptor destination) {
        this.destination = destination;
    }

    public MediationState getState() {
        return state;
    }

    public void setState(MediationState state) {
        this.state = state;
    }


    public IdentityMediationFault getFault() {
        return fault;
    }

    public void setFault(IdentityMediationFault fault) {
        this.fault = fault;
    }

    public String getFaultDetails() {
        return faultDetails;
    }

    public void setFaultDetails(String faultDetails) {
        this.faultDetails = faultDetails;
    }
}
