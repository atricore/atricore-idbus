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

package org.atricore.idbus.kernel.main.mediation.endpoint;

import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.planning.IdentityPlan;

import java.util.Collection;

/**
 * @org.apache.xbean.XBean element="endpoint"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IdentityMediationEndpointImpl.java 1259 2009-06-09 20:08:04Z sgonzalez $
 */
public class IdentityMediationEndpointImpl implements IdentityMediationEndpoint, java.io.Serializable  {

    private String name;
    private String type;
    private String binding;
    private String location;
    private String responseLocation;

    private transient Collection<IdentityPlan> identityPlans;

    private MetadataEntry metadata;

    public IdentityMediationEndpointImpl(String name, String type, String binding, String location, String responseLocation) {
        this.name = name;
        this.type = type;
        this.binding = binding;
        this.location = location;
        this.responseLocation = responseLocation;
    }

    public IdentityMediationEndpointImpl(String name, String type, String binding) {
        this.name = name;
        this.type = type;
        this.binding = binding;
    }

    /** Spring friendly */
    public IdentityMediationEndpointImpl() {

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

    public MetadataEntry getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataEntry metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @org.apache.xbean.Property alias="identity-plans" nestedType="org.atricore.idbus.kernel.planning.IdentityPlan"
     */
    public Collection<IdentityPlan> getIdentityPlans() {
        return identityPlans;
    }

    public void setIdentityPlans(Collection<IdentityPlan> identityPlans) {
        this.identityPlans = identityPlans;
    }

    @Override
    public String toString() {
        return super.toString() + "[name="+name +
                ",type=" + type +
                ",binding=" + binding +
                ",location=" + location +
                ",responseLocation=" + responseLocation + "]";
    }
}
