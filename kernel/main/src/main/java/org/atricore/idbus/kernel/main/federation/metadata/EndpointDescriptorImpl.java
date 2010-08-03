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

package org.atricore.idbus.kernel.main.federation.metadata;

import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: EndpointDescriptorImpl.java 1290 2009-06-17 12:52:17Z sgonzalez $
 */
public class EndpointDescriptorImpl implements EndpointDescriptor {

    private String name;
    private String type;
    private String binding;
    private String location;
    private String responseLocation;

    public EndpointDescriptorImpl(IdentityMediationEndpoint idEndpoint) {
        this.name = idEndpoint.getName();
        this.type = idEndpoint.getType();
        this.binding = idEndpoint.getBinding();
        this.location = idEndpoint.getLocation();
        this.responseLocation = idEndpoint.getResponseLocation();

    }

    public EndpointDescriptorImpl(String name, String type, String binding, String location, String responseLocation) {
        this.name = name;
        this.type = type;
        this.binding = binding;
        this.location = location;
        this.responseLocation = responseLocation;
    }

    public EndpointDescriptorImpl(String name, String type, String binding) {
        this.name = name;
        this.type = type;
        this.binding = binding;
    }

    /** Spring friendly */
    public EndpointDescriptorImpl() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getResponseLocation() {
        return responseLocation;
    }

    public void setResponseLocation(String responseLocation) {
        this.responseLocation = responseLocation;
    }


    @Override
    public String toString() {
        return super.toString() + "[name=" + name +
                ",type=" + type +
                ",binding=" + binding +
                ",location=" + location +
                ",responseLocation=" + responseLocation +
                "]";

    }
}

